package executionFlow.runtime.collector;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import executionFlow.info.CollectorInfo;
import executionFlow.info.MethodInvokedInfo;
import executionFlow.util.ConsoleOutput;


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
 * @version		4.0.1
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
		String signature, anonymousClassSignature, methodName, classSignature, className, key;
		Class<?>[] paramTypes;
		Class<?> returnType;
		Object constructor = null;
		MethodInvokedInfo methodInfo;
		CollectorInfo ci;
		List<CollectorInfo> list;
		
		
		signature = thisJoinPoint.getSignature().toString();
		anonymousClassSignature = "";
		
		if (thisJoinPoint.getTarget() != null) {
			anonymousClassSignature = thisJoinPoint.getTarget().getClass().getName(); 
			
			anonymousClassSignatures.put(
					anonymousClassSignature, 
					thisJoinPoint.getSignature().getDeclaringTypeName()
			);
		}

		if (!isMethodSignature(signature) || isNativeMethod(signature)) { return; }
		
		// Ignores methods in the method test (with @Test) (it will only consider internal calls)
		if (testMethodSignature != null && signature.contains(testMethodSignature)) { return; }
		
		// Gets correct signature of inner classes
		if (thisJoinPoint.getTarget() == null) {	// Static method			
			signature = thisJoinPoint.getSignature().getDeclaringTypeName() + "." 
					+ thisJoinPoint.getSignature().getName() + signature.substring(signature.indexOf("("));
		}
		else { 	// Non-static method
			if (anonymousClassSignatures.containsKey(thisJoinPoint.getTarget().getClass().getName())) {
				signature = anonymousClassSignatures.get(thisJoinPoint.getTarget().getClass().getName()) 
						+ "." + thisJoinPoint.getSignature().getName()
						+ signature.substring(signature.indexOf("("));
			}
			else {
				signature = thisJoinPoint.getTarget().getClass().getName() 
						+ "." + thisJoinPoint.getSignature().getName() 
						+ signature.substring(signature.indexOf("("));
			}
		}
		// Extracts the method name
		methodName = CollectorExecutionFlow.extractMethodName(signature);
		
		// Extracts class signature
		classSignature = thisJoinPoint.getSignature().getDeclaringTypeName();

		// Extracts types of method parameters (if there is any)
		paramTypes = CollectorExecutionFlow.extractParamTypes(thisJoinPoint);
		returnType = CollectorExecutionFlow.extractReturnType(thisJoinPoint);		
		
		// Key is method's signature + values of method's parameters
		key = signature+Arrays.toString(thisJoinPoint.getArgs());
		
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
		if (collectedMethods.contains(key)) { return; }
		
		// Collects the method
		try {
			// Gets class path and source path
			className = CollectorExecutionFlow.getClassName(classSignature);
			classPath = CollectorExecutionFlow.findBinPath(className, classSignature);
			srcPath = CollectorExecutionFlow.findSrcPath(className, classSignature);
			
			if (srcPath == null || classPath == null) {
				ConsoleOutput.showWarning("The method with the following signature" 
						+ " will be skiped because its source file and / or " 
						+ " binary file cannot be found: " + signature);
				return;
			}
			
			methodInfo = new MethodInvokedInfo.Builder()
				.binPath(classPath)
				.methodSignature(signature)
				.methodName(methodName)
				.returnType(returnType)
				.parameterTypes(paramTypes)
				.args(thisJoinPoint.getArgs())
				.invocationLine(invocationLine)
				.srcPath(srcPath)
				.build();

			ci = new CollectorInfo.Builder()
				.methodInfo(methodInfo)
				.testMethodInfo(testMethodInfo)
				.build();
			
			lastInvocationLine = invocationLine;
			
			// Stores key of collected method
			collectedMethods.add(key);
			
			// If the method is called in a loop, stores this method in a list with its arguments and constructor
			if (methodCollector.containsKey(invocationLine)) {
				list = methodCollector.get(invocationLine);
				list.add(ci);
			} 
			// Else stores the method with its arguments and constructor
			else {	
				list = new ArrayList<>();
				methodCollector.put(invocationLine, list);
				list.add(ci);
			}
		} 
		catch(IllegalArgumentException | IOException e) {
			System.err.println("[ERROR] MethodCollector - "+e.getMessage()+"\n");
		}
	}
}
