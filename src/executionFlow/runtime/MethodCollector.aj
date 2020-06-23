package executionFlow.runtime;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import executionFlow.info.MethodInvokerInfo;
import executionFlow.info.CollectorInfo;


/**
 * Collects various information about methods tested by a JUnit test.
 * 
 * <h1>Collected information</h1>
 * <ul>
 * 	<li>Tested methods by a JUnit test</li>
 * 	<li>Invoked methods by a tested method</li>
 * </ul>
 * 
 * @apiNote		Excludes calls to native java methods, ExecutionFlow's classes,
 * methods with {@link SkipMethod} annotation, methods with {@link _SkipMethod}
 * and all methods from classes with {@link SkipCollection} annotation.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		1.5
 * @since		1.0 
 */
public aspect MethodCollector extends RuntimeCollector
{	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private Path classPath;
	private Path srcPath;
	
	
	//-----------------------------------------------------------------------
	//		Pointcuts
	//-----------------------------------------------------------------------
	/**
	 * Displays tested method signatures by a JUnit test that has 
	 * {@link @executionFlow.runtime._SkipMethod} annotation.
	 */
	pointcut invokerSignature(): 
		cflow(execution(@executionFlow.runtime._SkipMethod * *.*(..))) && 
		(junit4() || junit5()) &&
		!junit4_internal() && !junit5_internal() &&
		!execution(public int hashCode());
	
	before(): invokerSignature()
	{
		String invocationSignature = thisJoinPoint.getSignature().toString();
		
		
		// Checks if is a method signature
		if (!isMethodSignature(invocationSignature)) { return; }
		
		// Ignores native java methods
		if (isNativeMethod(invocationSignature)) { return; }
		
		System.out.println("__TES_SIG:"+invocationSignature);
		
	}
	
	/**
	 * Displays invoked method signatures within a method with 
	 * {@link @executionFlow.runtime.CollectInvokedMethods} annotation.
	 */
	pointcut invokedMethodsByTestedMethod():
		withincode(@executionFlow.runtime.CollectInvokedMethods * *.*(..)) &&
		!cflowbelow(withincode(@executionFlow.runtime.CollectInvokedMethods * *.*(..)));
	
	before(): invokedMethodsByTestedMethod()
	{
		String invokedMethodSignature = thisJoinPoint.getSignature().toString();

		
		// Checks if is a method signature
		if (!isMethodSignature(invokedMethodSignature)) { return; }
		
		// Ignores native java methods
		if (isNativeMethod(invokedMethodSignature)) { return; }

		System.out.println("__INV_SIG:"+invokedMethodSignature);
	}
	
	/**
	 * Intercepts methods within a test method.
	 */
	pointcut methodCollector(): 
		!skipAnnotation() &&
		(junit4() || junit5()) &&
		!junit4_internal() && !junit5_internal() &&
		!execution(public int hashCode());

	before(): methodCollector()
	{
		// Gets method invocation line
		int invocationLine = thisJoinPoint.getSourceLocation().getLine();
		
		//if (invocationLine <= 0) { return; }
		String signature = thisJoinPoint.getSignature().toString();

		// Checks if is a method signature
		if (!isMethodSignature(signature)) { return; }
		
		// Ignores native java methods
		if (isNativeMethod(signature)) { return; }
		
		// Ignores methods in the method test (with @Test) (it will only consider internal calls)
		if (testMethodSignature != null && signature.contains(testMethodSignature)) { return; }
		
		// Extracts the method name
		String methodName = CollectorExecutionFlow.extractMethodName(signature);
		
		// Extracts class signature
		String classSignature = thisJoinPoint.getSignature().getDeclaringTypeName();
		
		// Extracts types of method parameters (if there is any)
		Class<?>[] paramTypes = CollectorExecutionFlow.extractParamTypes(thisJoinPoint);
		Class<?> returnType = CollectorExecutionFlow.extractReturnType(thisJoinPoint);		
		
		// Key is method's signature + values of method's parameters
		String key = signature+Arrays.toString(thisJoinPoint.getArgs());
		Object constructor = null;
		
		// Checks if there is a constructor (if it is a static method or not)
		if (thisJoinPoint.getTarget() != null) {
			constructor = thisJoinPoint.getTarget();
			
			// Key: <method_name>+<method_params>+<constructor@hashCode>
			key += constructor.getClass().getName()+"@"+Integer.toHexString(constructor.hashCode());
		}
		
		// Checks if the collected constructor is not the constructor of the test method
		if (constructor != null && isTestMethodConstructor(key)) { return; }
		
		// If the method has already been collected, skip it (avoids collect duplicate methods)
		if (collectedMethods.contains(key)) {
			order++;
			return; 
		}
		
		// Gets class path and source path
		try {
			// Class path and source path from method
			String className = CollectorExecutionFlow.getClassName(classSignature);
			classPath = CollectorExecutionFlow.findClassPath(className, classSignature);
			srcPath = CollectorExecutionFlow.findSrcPath(className, classSignature);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		// Gets method signature
		String methodSignature = CollectorExecutionFlow.extractMethodSignature(signature);
		
		if (lastInvocationLine != invocationLine) {
			order = 0;
		}
		
		// Collects the method
		try {
			MethodInvokerInfo methodInfo = new MethodInvokerInfo.MethodInvokerInfoBuilder()
				.classPath(classPath)
				.methodSignature(methodSignature)
				.methodName(methodName)
				.returnType(returnType)
				.parameterTypes(paramTypes)
				.args(thisJoinPoint.getArgs())
				.invocationLine(invocationLine)
				.srcPath(srcPath)
				.build();
			
			CollectorInfo ci = new CollectorInfo.CollectorInfoBuilder()
				.methodInfo(methodInfo)
				.testMethodInfo(testMethodInfo)
				.order(order++)
				.build();
			
			lastInvocationLine = invocationLine;
			
			// Stores key of collected method
			collectedMethods.add(key);
			
			// If the method is called in a loop, stores this method in a list with its arguments and constructor
			if (methodCollector.containsKey(invocationLine)) {
				List<CollectorInfo> list = methodCollector.get(invocationLine);
				list.add(ci);
			} 
			// Else stores the method with its arguments and constructor
			else {	
				List<CollectorInfo> list = new ArrayList<>();
				list.add(ci);
				methodCollector.put(invocationLine, list);
			}

		} catch(IllegalArgumentException e) {
			System.err.println("[ERROR] MethodCollector - "+e.getMessage()+"\n");
		}
	}
}
