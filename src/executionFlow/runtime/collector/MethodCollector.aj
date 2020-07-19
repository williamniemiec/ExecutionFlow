package executionFlow.runtime.collector;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import executionFlow.info.CollectorInfo;
import executionFlow.info.MethodInvokedInfo;
import executionFlow.runtime.SkipCollection;


/**
 * Collects various information about methods called by a JUnit test.
 * 
 * <h1>Collected information</h1>
 * <ul>
 * 	<li>Compiled file path</li>
 *	<li>Source file path</li>
 *	<li>Method signature</li>
 *	<li>Method name</li>
 *	<li>Return type</li>
 *	<li>Parameter types</li>
 *	<li>Method arguments</li>
 *	<li>Test method line that calls the method</li>
 * </ul>
 * 
 * @apiNote		Excludes calls to native java methods, methods with 
 * {@link executionFlow.runtime.SkipInvoked} annotation, methods
 * with {@link executionFlow.runtime._SkipInvoked} and all methods from classes
 * with {@link executionFlow.runtime.SkipCollection} annotation.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		2.0.0
 * @since		1.0 
 */
public aspect MethodCollector extends RuntimeCollector
{	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private Path classPath;
	private Path srcPath;
	private boolean isRepeatedTest;
	
	
	//-----------------------------------------------------------------------
	//		Pointcuts
	//-----------------------------------------------------------------------
	/**
	 * Intercepts repeated tests, that is, tests with 
	 * <code>@org.junit.jupiter.api.RepeatedTest</code>
	 */
	pointcut repeatedTest():
		!within(@SkipCollection *) &&
		withincode(@executionFlow.runtime.isRepeatedTest * *.*());
	
	before(): repeatedTest()
	{
		isRepeatedTest = true;
	}
	
	/**
	 * Intercepts the following test methods:
	 * <ul>
	 * 	<li><code>@org.junit.Test</code></li>
	 * 	<li><code>@org.junit.jupiter.api.Test</code></li>
	 * 	<li><code>@org.junit.jupiter.params.ParameterizedTest</code></li>
	 * </ul>
	 */
	pointcut noRepeatedTest():
		!within(@SkipCollection *) &&
		withincode(@org.junit.Test * *.*()) && 
		!withincode(@executionFlow.runtime.isRepeatedTest * *.*());
	
	before(): noRepeatedTest()
	{
		isRepeatedTest = false;
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
		
		String signature = thisJoinPoint.getSignature().toString();

		// Checks if is a method signature
		if (!isMethodSignature(signature)) { return; }
		
		// Ignores native java methods
		if (isNativeMethod(signature)) { return; }
		
		// Ignores methods in the method test (with @Test) (it will only consider internal calls)
		if (testMethodSignature != null && signature.contains(testMethodSignature)) { return; }
		
		// Gets correct signature of inner classes
		if (thisJoinPoint.getTarget() == null) {	// Static method			
			signature = thisJoinPoint.getSignature().getDeclaringTypeName() + "." 
					+ thisJoinPoint.getSignature().getName() + signature.substring(signature.indexOf("("));
		}
		else { 	// Non-static method
			signature = thisJoinPoint.getTarget().getClass().getName() + "." 
					+ thisJoinPoint.getSignature().getName() + signature.substring(signature.indexOf("("));
		}
		
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
		
		
		key += invocationLine;
		
		// If the method has already been collected, skip it (avoids collect duplicate methods)
		if (collectedMethods.contains(key)) {
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
		
		// Collects the method
		try {
			MethodInvokedInfo methodInfo = new MethodInvokedInfo.MethodInvokedInfoBuilder()
				.binPath(classPath)
				.methodSignature(signature)
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
				.build();
			
			lastInvocationLine = invocationLine;
			
			// Stores key of collected method
			collectedMethods.add(key);
			
			// If the method is called in a loop, stores this method in a list with its arguments and constructor
			if (methodCollector.containsKey(invocationLine)) {
				List<CollectorInfo> list = methodCollector.get(invocationLine);
				
	
				if (!isRepeatedTest) {
					list.add(ci);
				}
			} 
			// Else stores the method with its arguments and constructor
			else {	
				List<CollectorInfo> list = new ArrayList<>();
				
				
				methodCollector.put(invocationLine, list);
				list.add(ci);
			}
		} catch(IllegalArgumentException e) {
			System.err.println("[ERROR] MethodCollector - "+e.getMessage()+"\n");
		}
	}
}