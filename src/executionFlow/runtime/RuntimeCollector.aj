package executionFlow.runtime;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import executionFlow.info.ConstructorInvokerInfo;
import executionFlow.info.CollectorInfo;


/**
 * Responsible for data collection of methods and class constructors used in 
 * tests.
 * 
 * @apiNote		It will ignore all methods of a class if it has 
 * {@link SkipCollection} annotation
 * @apiNote		It will ignore methods with {@link SkipMethod} annotation
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		1.5
 * @since		1.0
 */
public abstract aspect RuntimeCollector 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	/**
	 * Stores signature of collected methods.<hr/>
	 * <b>Format: </b><code>method_name + method_arguments + 
	 * constructor@hashCode (if it has one)</code>
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
	 * 		<li><b>Key(with arguments):</b>		<code>classSignature[arg1,arg2,...]</code></li>
	 * 		<li><b>Key(without arguments):</b>	<code>classSignature[]</code></li>
	 * 		<li><b>Value:</b> Informations about the constructor</li>
	 * </ul>
	 */
	protected static Map<String, ConstructorInvokerInfo> constructorCollector = new LinkedHashMap<>();
	
	protected static String testMethodSignature;
	protected static Path testClassPath;
	protected static Path testSrcPath;
	protected static String testMethodPackage;
	protected static boolean skipCollection;
	protected static int lastInvocationLine;
	protected static int order;
	protected static Object[] testMethodArgs;
	
	
	//-------------------------------------------------------------------------
	//		Pointcuts
	//-------------------------------------------------------------------------
	pointcut skipAnnotation():
		within(@SkipCollection *) ||
		withincode(@executionFlow.runtime.SkipMethod * *.*(..)) ||
		withincode(@executionFlow.runtime._SkipMethod * *.*(..)) ||
		execution(@executionFlow.runtime.SkipMethod * *.*(..)) ||
		execution(@executionFlow.runtime._SkipMethod * *.*(..)); 
	
	pointcut junit4():
		!junit4_internal() && 
		withincode(@org.junit.Test * *.*());
	
	pointcut junit5():
		!junit5_internal() && (
			withincode(@org.junit.jupiter.api.Test * *.*()) ||
			withincode(@org.junit.jupiter.params.ParameterizedTest * *.*(..)) ||
			withincode(@org.junit.jupiter.api.RepeatedTest * *.*(..))
		);
	
	pointcut junit4_internal():
		call(* org.junit.runner.JUnitCore.runClasses(*)) ||
		call(void org.junit.Assert.*(*)) ||
		call(void org.junit.Assert.*(*,*)) ||
		call(void org.junit.Assert.*(*,*,*)) ||
		call(void org.junit.Assert.*(*,*,*,*)) ||
		call(void org.junit.Assert.fail());
	
	pointcut junit5_internal():
		call(void org.junit.jupiter.api.Assertions.*(*)) ||
		call(void org.junit.jupiter.api.Assertions.*(*,*)) ||
		call(void org.junit.jupiter.api.Assertions.*(*,*,*));
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------	
	/**
	 * Returns if a method is a native method of Java.
	 * 
	 * @param		methodSignature Signature of the method
	 * @return		If the method is a native method
	 */
	protected boolean isNativeMethod(String methodSignature)
	{
		return	methodSignature == null || 
				methodSignature.contains("java.") || 
				methodSignature.contains("jdk.");
	}
	
	/**
	 * Returns if a signature is a method signature.
	 * 
	 * @param		signature Signature to be analyzed
	 * @return		If the signature is a method signature
	 */
	protected boolean isMethodSignature(String signature)
	{
		return signature.matches("[A-z\\.]+\\s([A-z0-9-_$]+\\.)+[A-z0-9-_$]+\\([A-z0-9-_$,\\s]*\\)");
	}
	
	/**
	 * Sets default value for all attributes.
	 */
	protected void reset()
	{
		collectedMethods.clear();
		methodCollector.clear();
		constructorCollector.clear();
		testMethodSignature = null;
		testClassPath = null;
		testSrcPath = null;
		skipCollection = false;
		testMethodPackage = null;
		lastInvocationLine = 0;
		order = 0;
	}
	
	/**
	 * Checks if a signature belongs to an internal call.
	 * 
	 * @param		signature Signature of the method
	 * @return		If the signature belongs to an internal call
	 */
	protected boolean isInternalCall(String signature)
	{
		return !Thread.currentThread().getStackTrace()[3].toString().contains(testMethodPackage);
	}
	
	/**
	 * Checks if a signature of a constructor is from a test method 
	 * constructor.
	 * 
	 * @param		constructorSignature Signature of the constructor
	 * @return		If the signature of the constructor is from a test method 
	 * constructor
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
	 * Checks if a signature belongs to a builder class.
	 * 
	 * @param		signature Signature to be analyzed
	 * @return		If the signature belongs to a builder class
	 */
	protected boolean isBuilderClass(String signature)
	{
		String[] tmp = signature.split("\\.");
		return tmp[tmp.length-2].toLowerCase().contains("builder");
	}
}
