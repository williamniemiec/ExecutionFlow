package executionFlow.runtime;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import executionFlow.info.CollectorInfo;
import executionFlow.info.MethodInvokerInfo;


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
	 * 	<li><b>Key:</b> Method invocation line</li>
	 * 	<li><b>Value:</b> List of methods invoked from this line</li>
	 * </ul>
	 */
	protected static Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
	
	/**
	 * Stores information about collected constructor.<hr/>
	 * <ul>
	 * 	<li><b>Key(with arguments):</b>		<code>classSignature[arg1,arg2,...]</code></li>
	 * 	<li><b>Key(without arguments):</b>	<code>classSignature[]</code></li>
	 * 	<li><b>Value:</b> Informations about the constructor</li>
	 * </ul>
	 */
	protected static Map<String, CollectorInfo> constructorCollector = new LinkedHashMap<>();

	protected static String testMethodSignature;
	protected static MethodInvokerInfo testMethodInfo;
	protected static boolean skipCollection;
	protected static int lastInvocationLine;
	protected static int order;
	protected static Object[] testMethodArgs;
	
	
	//-------------------------------------------------------------------------
	//		Pointcuts
	//-------------------------------------------------------------------------
	/**
	 * Intercepts methods and classes with skip annotations.
	 */
	pointcut skipAnnotation():
		within(@SkipCollection *) ||
		withincode(@executionFlow.runtime.SkipMethod * *.*(..)) ||
		withincode(@executionFlow.runtime._SkipMethod * *.*(..)) ||
		execution(@executionFlow.runtime.SkipMethod * *.*(..)) ||
		execution(@executionFlow.runtime._SkipMethod * *.*(..)); 
	
	/**
	 * Intercepts test methods with JUnit 4 test annotation.
	 */
	pointcut junit4():
		withincode(@org.junit.Test * *.*());
	
	/**
	 * Intercepts test methods with JUnit 5 test annotation.
	 */
	pointcut junit5():
		withincode(@org.junit.jupiter.api.Test * *.*()) ||
		withincode(@org.junit.jupiter.params.ParameterizedTest * *.*(..)) ||
		withincode(@org.junit.jupiter.api.RepeatedTest * *.*(..));
	
	/**
	 * Intercepts methods from JUnit 4.
	 */
	pointcut junit4_internal():
		call(* org.junit.runner.JUnitCore.runClasses(..)) ||
		call(void org.junit.Assert.*(..)) ||
		call(void org.junit.Assert.fail());
	
	/**
	 * Intercepts methods from JUnit 5.
	 */
	pointcut junit5_internal():
		call(void org.junit.jupiter.api.Assertions.*(..));
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------	
	/**
	 * Returns if a method is a native method of Java.
	 * 
	 * @param		methodSignature Signature of the method
	 * 
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
	 * 
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
		skipCollection = false;
		lastInvocationLine = 0;
		order = 0;
	}
	
	/**
	 * Checks if a signature of a constructor is from a test method 
	 * constructor.
	 * 
	 * @param		constructorSignature Signature of the constructor
	 * 
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
	 * 
	 * @return		If the signature belongs to a builder class
	 */
	protected boolean isBuilderClass(String signature)
	{
		String[] tmp = signature.split("\\.");
		return tmp[tmp.length-2].toLowerCase().contains("builder");
	}
}
