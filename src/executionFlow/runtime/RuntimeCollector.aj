package executionFlow.runtime;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.aspectj.lang.JoinPoint;

import executionFlow.info.ClassConstructorInfo;
import executionFlow.info.ClassMethodInfo;
import executionFlow.info.CollectorInfo;


/**
 * Responsible for data collection of methods and class constructors used in tests.
 * 
 * <h2>Requirements</h2>
 * <ul>
 * 		<li>Each test method uses only one constructor of the class to be tested (consequently there 
 * 		will only be one class path per test method)</li>
 * 		<li>Each test method tests only methods of a class / object</li>
 * 		<li>Each test method must have <code>@Test</code> annotation</li>
 * </ul>
 * 
 * @implNote It will ignore methods with <code>@SkipCollection</code> annotation
 */
@SuppressWarnings("unused")
public abstract aspect RuntimeCollector 
{
	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
	protected static String classPath;
	
	/**
	 * Stores information about methods.<hr/>
	 * <ul>
	 * 		<li>
	 * 			<b>Key:</b> Map with information about signature and parameters of the method
	 * 			<ul>
	 * 				<li><b>Key:</b> Signature of the method</li>
	 * 				<li><b>Value:</b> Parameter's values of the method</li>
	 * 			</ul>
	 * 		</li>
	 * 		<li><b>Value:</b> Informations about the method</li>
	 * </ul>
	 */
	protected static Map<String, CollectorInfo> methodCollector = new LinkedHashMap<>();
	
	protected static Map<String, ClassConstructorInfo> consCollector = new LinkedHashMap<>();
	protected static ClassConstructorInfo cci;
	protected static String testMethodSignature;
	protected static String lastInsertedMethod = "";
	protected static boolean lastWasInternalCall = false;
	
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	/**
	 * Returns if the class of a method that a join point intercepted has <code>@SkipCollection</code>. 
	 * 
	 * @param jp Join point that intercepted a method
	 * @return If the class of the intercepted method has <code>@SkipCollection</code>
	 */
	protected boolean hasSkipCollectionAnnotation(JoinPoint jp)
	{
		return jp.getThis() != null && hasClassSkipCollectionAnnotation(jp.getThis().getClass());
	}
	
	/**
	 * Returns if a method is a native method of Java.
	 * 
	 * @param methodSignature Signature of the method
	 * @return If the method is a native method
	 */
	protected boolean isNativeMethod(String methodSignature)
	{
		return methodSignature == null || methodSignature.contains("java.");
	}
	
	/**
	 * Returns if a signature is a method signature.
	 * 
	 * @param signature Signature to be analyzed
	 * @return If the signature is a method signature
	 */
	protected boolean isMethodSignature(String signature)
	{
		return signature.matches("[A-z]+\\s([A-z0-9-_$]+\\.)+[A-z0-9-_$]+\\([A-z0-9-_$,\\s]*\\)");
	}
	
	/**
	 * Sets default value for all attributes.
	 */
	protected void reset()
	{
		methodCollector.clear();
		consCollector.clear();
		cci = null;
		testMethodSignature = null;
		lastInsertedMethod = "";
		lastWasInternalCall = false;
	}
	
	/**
	 * Checks if there is the <code>@SkipCollection</code> annotation in a class.
	 * 
	 * @param c Class to be analyzed
	 * @return If <code>@SkipCollection</code> annotation is present in the class
	 */
	private boolean hasClassSkipCollectionAnnotation(Class<?> c)
	{
		if (c == null) { return false; }
		
		return c.isAnnotationPresent(SkipCollection.class);
	}
}
