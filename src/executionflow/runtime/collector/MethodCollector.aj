package executionflow.runtime.collector;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import executionflow.info.InvokedContainer;
import executionflow.info.InvokedInfo;
import executionflow.util.logger.Logger;

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
 * with {@link executionflow.runtime.SkipInvoked} annotation and all methods
 * from classes with {@link executionflow.runtime.SkipCollection} annotation.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		6.0.4
 * @since		1.0 
 */
@SuppressWarnings("unused")
public aspect MethodCollector extends RuntimeCollector {	
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private String signature;
	private Path classPath;
	private Path srcPath;
	
	
	//-----------------------------------------------------------------------
	//		Pointcuts
	//-----------------------------------------------------------------------
	private pointcut insideTestedMethod(): 
		!skipAnnotation() 
		&& insideJUnitTest()
		&& !get(* *.*) 
		&& !set(* *.*)
		&& !within(api..*)
		&& !execution(public int hashCode());
	

	//-------------------------------------------------------------------------
	//		Join points
	//-------------------------------------------------------------------------
	before(): insideTestedMethod() {
		initializeSignature(thisJoinPoint);

		if (!isValidState(thisJoinPoint))
			return;
		
		String key = generateKey(thisJoinPoint);
		
		if (alreadyCollected(key))
			return;
		
		collectSourceAndBinaryPaths(thisJoinPoint);
		
		if ((srcPath == null) || (classPath == null))
			return;
		
		collectMethod(thisJoinPoint, key);
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	private void initializeSignature(JoinPoint jp) {
		fixAnonymousSignature(jp);
		
		signature = getSignature(jp);
	}
	
	private void fixAnonymousSignature(JoinPoint jp) {
		if (isStaticMethod(jp))
			return;
		
		anonymousClassSignatures.put(
				getConstructorName(jp), 
				jp.getSignature().getDeclaringTypeName()
		);
	}
	
	private boolean isStaticMethod(JoinPoint jp) {
		return jp.getTarget() == null;
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
	
	private boolean isValidState(JoinPoint jp) {
		return	(isStaticMethod(jp) || !constructorBelongsToTestMethod(jp))
				&& isMethodSignature(jp) 
				&& !isNativeMethod(jp) 
				&& !belongsToTestMethod(jp);
	}
	
	private boolean constructorBelongsToTestMethod(JoinPoint jp) {
		if ((jp.getClass() == null) || (testMethodSignature == null))
			return true;
		
		String objectClassName = extractClassNameFromClassSignature(jp.getClass().getName());
		String testMethodClassName = extractClassNameFromClassSignature(testMethodSignature);
		
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
		
		return 	(testMethodSignature != null)
				&& signature.contains(testMethodSignature);
	}
	
	private String generateKey(JoinPoint jp) {
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
	
	private boolean alreadyCollected(String key) {
		return collectedMethods.contains(key);
	}
	
	private void collectSourceAndBinaryPaths(JoinPoint jp) {
		try {
			findSrcAndBinPath(jp);
		} 
		catch (IOException e) {
			Logger.error("[ERROR] MethodCollector - " + e.getMessage() + "\n");
		}
	}
	
	private void findSrcAndBinPath(JoinPoint jp) throws IOException {
		String classSignature = getClassSignature(jp);
				
		classPath = ClassPathSearcher.findBinPath(classSignature);
		srcPath = ClassPathSearcher.findSrcPath(classSignature);
		
		if ((srcPath == null) || (classPath == null)) {
			Logger.warning("The method with the following signature" 
					+ " will be skiped because its source file and / or " 
					+ " binary file cannot be found: " + signature);
		}
	}
	
	private String getClassSignature(JoinPoint jp) {
		if (jp.getTarget() == null)
			return jp.getSignature().getDeclaringTypeName();
		
		return jp.getTarget().getClass().getName();
	}
	
	private void collectMethod(JoinPoint jp, String key) {
		InvokedInfo methodInfo = new InvokedInfo.Builder()
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
		
		storeCollector(jp, methodInfo);
		collectedMethods.add(key);

		lastInvocationLine = getInvocationLine(jp);
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
	
	private void storeCollector(JoinPoint jp, InvokedInfo methodInfo) {
		if (methodCollector.containsKey(getInvocationLine(jp))) {
			List<InvokedContainer> list = methodCollector.get(getInvocationLine(jp));
			list.add(new InvokedContainer(methodInfo, testMethodInfo));
		} 
		else {	
			List<InvokedContainer> list = new ArrayList<>();
			list.add(new InvokedContainer(methodInfo, testMethodInfo));
			
			methodCollector.put(getInvocationLine(jp), list);
		}
	}
}
