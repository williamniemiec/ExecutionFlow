package executionFlow.runtime.collector;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.aspectj.lang.JoinPoint;

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
 * @version		5.2.0
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
		String signature, className, classSignature, key;
		Object constructor = null;

		
		signature = thisJoinPoint.getSignature().toString();
		
		fixAnonymousSignature(thisJoinPoint);

		if (!isMethodSignature(signature) || isNativeMethod(signature))
			return;
		
		// Ignores methods in the method test (with @Test) (it will only consider internal calls)
		if (testMethodSignature != null && signature.contains(testMethodSignature))
			return;

		signature = getFixedMethodSignature(thisJoinPoint, signature);
		
		className = getClassName(thisJoinPoint);
		classSignature = (thisJoinPoint.getTarget() == null) ? 
							thisJoinPoint.getSignature().getDeclaringTypeName() : 
							thisJoinPoint.getTarget().getClass().getName();
		
		// Key is method's signature + values of method's parameters
		key = signature + Arrays.toString(thisJoinPoint.getArgs());
		
		// Checks if there is a constructor (if it is a static method or not)
		if (thisJoinPoint.getTarget() != null) {
			constructor = thisJoinPoint.getTarget();
			
			// Key: <method_name>+<method_params>+<constructor@hashCode>
			key += constructor.getClass().getName()+"@"+Integer.toHexString(constructor.hashCode());
		}
		
		// Checks if the collected constructor is not the constructor of the test method
		if ((constructor != null) && isTestMethodConstructor(key))
			return;
		
		key += invocationLine;
		
		// If the method has already been collected, skip it (avoids collect duplicate methods)
		if (collectedMethods.contains(key))
			return;
		
		// Gets class path and source path
		try {
			classPath = CollectorExecutionFlow.findBinPath(className, classSignature);
			srcPath = CollectorExecutionFlow.findSrcPath(className, classSignature);
		} 
		catch (IOException e) {
			ConsoleOutput.showError("[ERROR] MethodCollector - " + e.getMessage() + "\n");
		}
		
		if ((srcPath == null) || (classPath == null)) {
			ConsoleOutput.showWarning("The method with the following signature" 
					+ " will be skiped because its source file and / or " 
					+ " binary file cannot be found: " + signature);
			return;
		}
		
		collectMethod(thisJoinPoint, signature, invocationLine);
		
		collectedMethods.add(key);
	}

	/**
	 * Gets class name of the current method.
	 * 
	 * @param		jp Join point
	 * 
	 * @return		Class name
	 */
	private String getClassName(JoinPoint jp)
	{
		String REGEX_ANONYMOUS_SIGNATURE = ".+\\$[0-9]+.*";
		String className;
		
		
		if ((jp.getTarget() != null) && 
				jp.getTarget().getClass().getName().matches(REGEX_ANONYMOUS_SIGNATURE)) {
			className = jp.getTarget().getClass().getName();
		}
		else {
			className = jp.getSignature().getDeclaringTypeName();
		}

		className = className.substring(className.lastIndexOf(".")+1);
		
		return className;
	}
	
	/**
	 * Collects current method.
	 * 
	 * @param		jp Join point
	 * @param		signature Method signature
	 * @param		invocationLine Line that the method is invoked
	 */
	private void collectMethod(JoinPoint jp, String signature, int invocationLine)
	{
		CollectorInfo ci;
		List<CollectorInfo> list;
		MethodInvokedInfo methodInfo;
		Class<?>[] paramTypes;
		Class<?> returnType;
		String methodName = CollectorExecutionFlow.extractMethodName(signature);
		
		
		// Extracts types of method parameters (if there is any)
		paramTypes = CollectorExecutionFlow.extractParamTypes(jp);
		returnType = CollectorExecutionFlow.extractReturnType(jp);
		
		methodInfo = new MethodInvokedInfo.Builder()
			.binPath(classPath)
			.methodSignature(signature)
			.methodName(methodName)
			.returnType(returnType)
			.parameterTypes(paramTypes)
			.args(jp.getArgs())
			.invocationLine(invocationLine)
			.srcPath(srcPath)
			.build();

		ci = new CollectorInfo.Builder()
			.methodInfo(methodInfo)
			.testMethodInfo(testMethodInfo)
			.build();
		
		lastInvocationLine = invocationLine;
		
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
	
	/**
	 * Gets correct signature of anonymous methods.
	 * 
	 * @param		jp Join point
	 */
	private void fixAnonymousSignature(JoinPoint jp)
	{
		if (jp.getTarget() != null) {
			String anonymousClassSignature = jp.getTarget().getClass().getName(); 
			
			
			anonymousClassSignatures.put(
					anonymousClassSignature, 
					jp.getSignature().getDeclaringTypeName()
			);
		}
	}
	
	/**
	 * Gets correct signature of methods.
	 * 
	 * @param		jp Join point
	 * @param		signature Method signature
	 * 
	 * @return		Method signature
	 */
	private String getFixedMethodSignature(JoinPoint jp, String signature)
	{
		String fixedSignature;
		
		
		if (jp.getTarget() == null) {	// Static method			
			fixedSignature = jp.getSignature().getDeclaringTypeName() + "." 
					+ jp.getSignature().getName() + signature.substring(signature.indexOf("("));
		}
		else { 	// Non-static method
			if (anonymousClassSignatures.containsKey(jp.getTarget().getClass().getName())) {
				fixedSignature = anonymousClassSignatures.get(jp.getTarget().getClass().getName()) 
						+ "." + jp.getSignature().getName()
						+ signature.substring(signature.indexOf("("));
			}
			else {
				fixedSignature = jp.getTarget().getClass().getName() 
						+ "." + jp.getSignature().getName() 
						+ signature.substring(signature.indexOf("("));
			}
		}
		
		return fixedSignature;
	}
}
