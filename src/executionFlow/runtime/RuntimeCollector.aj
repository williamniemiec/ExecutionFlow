package executionFlow.runtime;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.aspectj.lang.JoinPoint;

import executionFlow.info.ClassConstructorInfo;
import executionFlow.info.CollectorInfo;


/**
 * Responsible for data collection of methods and class constructors used in tests.
 * 
 * <h2>Requirements</h2>
 * <ul>
 * 		<li>Each test method uses only one constructor of the class to be tested (consequently there 
 * 		will only be one class path per test method)</li>
 * 		<li>Each test method tests only methods of a class / object</li>
 * 		<li>Each test method must have one of the following annotations:
 * 			<ul>
 * 				<li><code>@Test</code></li>
 * 				<li><code>@RepeatedTest</code></li>
			 	<li><code>@ParameterizedTest</code></li>
			 	<li><code>@TestFactory</code></li>
		 	</ul>
 * 		</li>
 * </ul>
 * 
 * @implNote It will ignore all methods of a class if it has <code>@SkipCollection</code> annotation
 */
public abstract aspect RuntimeCollector 
{
	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
	/**
	 * Stores signature of collected methods.<hr/>
	 * <b>Format: </b><code>method_name + method_arguments + constructor@hashCode (if it has one)</code>
	 */
	protected static List<String> collectedMethods = new ArrayList<>();
	
	/**
	 * Stores information about collected methods.<hr/>
	 * <ul>
	 * 		<li><b>Key:</b> Method invocation line</li>
	 * 		<li><b>Value:</b> List of methods invoked from this line</li>
	 * </ul>
	 */
	protected static Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
	
	/**
	 * Stores information about collected constructor.<hr/>
	 * <ul>
	 * 		<li><b>Key:</b> constructor@hashCode</li>
	 * 		<li><b>Value:</b> Informations about the constructor</li>
	 * </ul>
	 */
	protected static Map<String, ClassConstructorInfo> consCollector = new LinkedHashMap<>();
	
	protected static String testMethodSignature;
	protected static String lastInsertedMethod = "";
	protected static boolean lastWasInternalCall;
	protected static boolean skipCollection;
	
	
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
		if (jp.getThis() != null && hasClassSkipCollectionAnnotation(jp.getThis().getClass())) {
			skipCollection = true;
		}
		
		return skipCollection;
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
		testMethodSignature = null;
		lastInsertedMethod = "";
		lastWasInternalCall = false;
	}
	
	/**
	 * Checks if a signature belongs to an internal call.
	 * 
	 * @param signature Signature of the method
	 * @return If the signature belongs to an internal call
	 */
	protected boolean isInternalCall(String signature)
	{
		// Removes parentheses from the signature of the test method
		testMethodSignature = testMethodSignature.replaceAll("\\(\\)", "");
		
		// It is necessary because if it is an internal call, the next will also be
		if (lastWasInternalCall) {
			lastWasInternalCall = false;
			return true;
		}

		// Checks the execution stack to see if it is an internal call
		if (!Thread.currentThread().getStackTrace()[3].toString().contains(testMethodSignature) && 
			!Thread.currentThread().getStackTrace()[4].toString().contains(testMethodSignature)) {
			lastWasInternalCall = true;
			return true;
		}
		
		return false;
	}
	
	/**
	 * Checks if a signature of a constructor is from a test method constructor.
	 * 
	 * @param constructorSignature Signature of the constructor
	 * @return If the signature of the constructor is from a test method constructor
	 */
	protected boolean isTestMethodConstructor(String constructorSignature)
	{
		if (constructorSignature == null) { return true; }
		
		String testMethodClassName = CollectorExecutionFlow.getClassName(testMethodSignature);
		String[] tmp = constructorSignature.split("\\@")[0].split("\\.");
		String methodName = tmp[tmp.length-1];
		
		return methodName.equals(testMethodClassName);
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
