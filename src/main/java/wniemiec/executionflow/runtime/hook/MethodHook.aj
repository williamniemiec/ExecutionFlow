package wniemiec.executionflow.runtime.hook;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import wniemiec.executionflow.invoked.InvokedInfo;
import wniemiec.executionflow.runtime.collector.ClassPathSearcher;
import wniemiec.executionflow.runtime.collector.MethodCollector;
import wniemiec.util.logger.Logger;

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
 * @apiNote		Excludes calls to native java methods, methods or constructors
 * with {@link wniemiec.executionflow.runtime.SkipInvoked} annotation and all methods
 * from classes with {@link wniemiec.executionflow.runtime.SkipCollection} annotation.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		7.0.0
 * @since		1.0 
 */
@SuppressWarnings("unused")
public aspect MethodHook extends RuntimeHook {	
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private String methodID;
	private String signature;
	private String classSignature;
	private Path classPath;
	private Path srcPath;
	private InvokedInfo methodInfo;
	
	/**
	 * Stores anonymous class signature where it is created and where it is 
	 * declared.
	 * <ul>
	 * 	<li><b>Key:</b>	Class signature where anonymous class is created</li>
	 * 	<li><b>Value:</b> Class signature where anonymous class is declared</li>
	 * </ul> 
	 */
	private static Map<String, String> anonymousClassSignatures;

	
	//-------------------------------------------------------------------------
	//		Initialization block
	//-------------------------------------------------------------------------
	static {
		anonymousClassSignatures = new HashMap<>();
	}
	
	//-----------------------------------------------------------------------
	//		Pointcuts
	//-----------------------------------------------------------------------
	private pointcut insideTestedMethod(): 
		!skipAnnotation() 
		&& insideJUnitTest()
		&& !get(* *.*) 
		&& !set(* *.*)
		&& !isInternalPackage()
		&& !execution(public int hashCode());
	

	//-------------------------------------------------------------------------
	//		Join points
	//-------------------------------------------------------------------------
	before(): insideTestedMethod() {
		if (!isValidMethod(thisJoinPoint))
			return;
		
		parseMethodInfo(thisJoinPoint);
		
		if (!wasMethodAlreadyParsed() && hasSourceAndBinearyPath()) {
			parseMethod(thisJoinPoint);	
			collectMethod();
		}
	}
	
	
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	private boolean isValidMethod(JoinPoint jp) {
		return	(isStaticMethod(jp) || !constructorBelongsToTestMethod(jp))
				&& !belongsToTestMethod(jp)
				&& isMethodSignature(jp) 
				&& !isNativeMethod(jp); 
	}
	
	private boolean isStaticMethod(JoinPoint jp) {
		return jp.getTarget() == null;
	}
	
	private boolean constructorBelongsToTestMethod(JoinPoint jp) {
		if ((jp.getClass() == null) || !isTestMethodSignatureInitialized())
			return true;
		
		String objectClassName = extractClassNameFromClassSignature(jp.getClass().getName());
		String testMethodClassName = extractClassNameFromClassSignature(
				testMethodInfo.getInvokedSignature()
		);
		
		return objectClassName.equals(testMethodClassName);
	}
	
	public static String extractClassNameFromClassSignature(String classSignature) {
		String response;
		String[] tmp = classSignature.split("\\.");
		
		if (tmp.length < 1)
			response = tmp[0];
		else
			response = tmp[tmp.length-1];
		
		return response;	
	}
	
	private boolean belongsToTestMethod(JoinPoint jp) {
		String signature = jp.getSignature().toString();
		
		return 	isTestMethodSignatureInitialized()
				&& signature.contains(testMethodInfo.getInvokedSignature());
	}
	
	private boolean isTestMethodSignatureInitialized() {
		return	(testMethodInfo != null)
				&& (testMethodInfo.getInvokedSignature() != null);
	}
	
	private void parseMethodInfo(JoinPoint jp) {
		initializeSignature(jp);
		methodID = generateMethodID(jp);
	}
	
	private void initializeSignature(JoinPoint jp) {
		fixAnonymousSignature(jp);
		
		signature = getSignature(jp);
		classSignature = getClassSignature(jp);
	}
	
	private void fixAnonymousSignature(JoinPoint jp) {
		if (isStaticMethod(jp))
			return;
		
		anonymousClassSignatures.put(
				getConstructorName(jp), 
				jp.getSignature().getDeclaringTypeName()
		);
	}
	
	private String getConstructorName(JoinPoint jp) {
		if (isStaticMethod(jp))
			return "";
		
		return jp.getTarget().getClass().getName();
	}
	
	private String getSignature(JoinPoint jp) {
		StringBuilder signature = new StringBuilder();

		if (isStaticMethod(jp)) {			
			signature.append(jp.getSignature().getDeclaringTypeName());
		}
		else { 
			if (anonymousClassSignatures.containsKey(getConstructorName(jp))) {
				signature.append(anonymousClassSignatures.get(getConstructorName(jp)));
			}
			else {
				signature.append(getConstructorName(jp));
			}
		}
		
		signature.append(".");
		signature.append(jp.getSignature().getName());
		signature.append(removeParametersFromSignature(jp.getSignature().toString()));
		
		return signature.toString();
	}
	
	private String getClassSignature(JoinPoint jp) {
		if (jp.getTarget() == null)
			return jp.getSignature().getDeclaringTypeName();
		
		return jp.getTarget().getClass().getName();
	}
	
	private String generateMethodID(JoinPoint jp) {
		// Key: <method_name>+<method_params>+<constructor@hashCode>
		StringBuilder key = new StringBuilder();
		
		key.append(signature);
		key.append(Arrays.toString(jp.getArgs()));
		
		if (!isStaticMethod(jp)) {
			key.append(getConstructorName(jp));
			key.append("@");
			key.append(Integer.toHexString(getConstructorHashCode(jp)));
		}

		key.append(getInvocationLine(jp));
		
		return key.toString();
	}
	
	private int getConstructorHashCode(JoinPoint jp) {
		if (isStaticMethod(jp))
			return 0;
		
		return jp.getTarget().hashCode();
	}
	
	private int getInvocationLine(JoinPoint jp) {
		return (jp.getSourceLocation() == null) ? 0 : jp.getSourceLocation().getLine();
	}
	
	private boolean wasMethodAlreadyParsed() {
		return MethodCollector.wasCollected(methodID);
	}
	
	private boolean hasSourceAndBinearyPath() {
		findSourceAndBinaryPaths(classSignature);
		
		if ((srcPath == null) || (classPath == null)) {
			Logger.warning("The method with the following signature" 
					+ " will be skiped because its source file and / or " 
					+ " binary file cannot be found: " + signature);
			
			return false;
		}
		
		return true;
	}
	
	private void findSourceAndBinaryPaths(String classSignature) {
		try {
			classPath = ClassPathSearcher.findBinPath(classSignature);
			srcPath = ClassPathSearcher.findSrcPath(classSignature);
		} 
		catch (IOException e) {
			Logger.error("[ERROR] MethodCollector - " + e.getMessage() + "\n");
		}
	}
	
	private void parseMethod(JoinPoint jp) {
		methodInfo = new InvokedInfo.Builder()
				.binPath(classPath)
				.srcPath(srcPath)
				.invokedSignature(signature)
				.invokedName(InvokedInfo.extractMethodName(signature))
				.parameterTypes(getParameterTypes(jp))
				.args(getParameterValues(jp))
				.returnType(extractReturnType(jp))
				.invocationLine(getInvocationLine(jp))
				.build();
		methodInfo.setConcreteMethodSignature(getConcreteMethodSignature(jp));
	}
	
	private Class<?>[] getParameterTypes(JoinPoint jp) { 
		Method method = ((MethodSignature) jp.getSignature()).getMethod();
		
		return method.getParameterTypes();
	}
	
	private Object[] getParameterValues(JoinPoint jp) {
		return jp.getArgs();
	}
	
	private Class<?> extractReturnType(JoinPoint jp) {
		Method method = ((MethodSignature) jp.getSignature()).getMethod();
		
		return method.getReturnType();
	}
	
	private String getConcreteMethodSignature(JoinPoint jp) {
		if (isStaticMethod(jp))
			return "";
		
		StringBuilder concreteMethodSignature = new StringBuilder();
		
		concreteMethodSignature.append(getConstructorName(jp));
		concreteMethodSignature.append(".");
		concreteMethodSignature.append(jp.getSignature().getName());
		concreteMethodSignature.append(removeParametersFromSignature(signature));
		
		return isValidSignature(concreteMethodSignature.toString()) ?
				concreteMethodSignature.toString()
				: signature;
	}
	
	private boolean isValidSignature(String signature) {
		final String regexDollarSignAndNumbers = ".+(\\$[0-9]+(\\.|\\()).*";
		
		return signature.matches(regexDollarSignAndNumbers);
	}
	
	private void collectMethod() {
		MethodCollector.storeCollector(methodID, methodInfo, testMethodInfo);
	}
}