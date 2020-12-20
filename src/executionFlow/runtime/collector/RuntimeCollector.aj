package executionFlow.runtime.collector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aspectj.lang.JoinPoint;

import executionFlow.info.InvokedContainer;
import executionFlow.info.InvokedInfo;
import executionFlow.io.processor.InvokedFileProcessor;
import executionFlow.io.processor.TestMethodFileProcessor;


/**
 * Responsible for data collection of methods and class constructors used in 
 * tests.
 * 
 * @apiNote		It will ignore all methods of a class if it has 
 * {@link executionFlow.runtime.SkipCollection} annotation
 * @apiNote		It will ignore methods with 
 * {@link executionFlow.runtime.SkipInvoked} annotation
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.2
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
	protected static Map<Integer, List<InvokedContainer>> methodCollector = new LinkedHashMap<>();
	
	/**
	 * Stores information about collected constructor.<hr/>
	 * <ul>
	 * 	<li><b>Key(with arguments):</b>		<code>invocationLine + classSignature[arg1,arg2,...]</code></li>
	 * 	<li><b>Key(without arguments):</b>	<code>invocationLine + classSignature[]</code></li>
	 * 	<li><b>Value:</b> Informations about the constructor</li>
	 * </ul>
	 */
	protected static Map<String, InvokedContainer> constructorCollector = new LinkedHashMap<>();

	/**
	 * Stores anonymous class signature where it is created and where it is 
	 * declared.
	 * <ul>
	 * 	<li><b>Key:</b>	Class signature where anonymous class is created</li>
	 * 	<li><b>Value:</b> Class signature where anonymous class is declared</li>
	 * </ul> 
	 */
	protected static Map<String, String> anonymousClassSignatures = new HashMap<>();

	protected static String testMethodSignature;
	protected static InvokedInfo testMethodInfo;
	protected static boolean skipCollection;
	protected static int lastInvocationLine;
//	protected static Object[] testMethodArgs;
	protected static Map<String, Set<String>> methodsCalledByTestedInvoked = new HashMap<>();
	
	
	//-------------------------------------------------------------------------
	//		Pointcuts
	//-------------------------------------------------------------------------
	pointcut skipAnnotation():
		within(@executionFlow.runtime.SkipCollection *)
		|| withincode(@executionFlow.runtime.SkipInvoked * *.*(..))
		|| withincode(@executionFlow.runtime._SkipInvoked * *.*(..))
		|| execution(@executionFlow.runtime.SkipInvoked * *.*(..))
		|| execution(@executionFlow.runtime._SkipInvoked * *.*(..)); 
	
	protected pointcut insideJUnitTest():
		insideJUnit4Test() 
		|| insideJUnit5Test();
	
	protected pointcut insideJUnit4Test():
		JUnit4Annotation()
		&& !JUnit4InternalCall();
	
	protected pointcut JUnit4Annotation():
		withincode(@org.junit.Test * *.*());
	
	protected pointcut JUnit4InternalCall():
		call(* org.junit.runner.JUnitCore.runClasses(..))
		|| call(void org.junit.Assert.*(..))
		|| call(void org.junit.Assert.fail());

	protected pointcut insideJUnit5Test():
		JUnit5Annotation()
		&& !JUnit5InternalCall();
		
	protected pointcut JUnit5Annotation():
		withincode(@org.junit.jupiter.api.Test * *.*()) 
		|| withincode(@org.junit.jupiter.params.ParameterizedTest * *.*(..)) 
		|| withincode(@org.junit.jupiter.api.RepeatedTest * *.*(..));

	protected pointcut JUnit5InternalCall():
		call(void org.junit.jupiter.api.Assertions.*(..));
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------	
	/**
	 * Checks whether a method signature belongs to a native Java method or 
	 * if it is a JUnit method.
	 * 
	 * @param		methodSignature Signature of the method
	 * 
	 * @return		If the method is a native method or JUnit method
	 */
	protected boolean isNativeMethod(JoinPoint jp)
	{
		String signature = jp.getSignature().toString();
		
		return	signature == null || 
				signature.contains("java.") || 
				signature.contains("jdk.") ||
				signature.contains("org.junit.");
	}
	
	protected boolean isMethodSignature(JoinPoint jp)
	{
		String signature = jp.getSignature().toString();
		
		return signature.matches("[A-z\\.]+(\\s|\\t)+([A-z0-9-_$]+\\.)+"
				+ "[A-z0-9-_$]+\\([A-z0-9-\\._$,\\s]*\\)") && 
				!signature.matches(".*\\.(access\\$[0-9]+\\().*");
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
		methodsCalledByTestedInvoked.clear();
		TestMethodFileProcessor.clearMapping();
		InvokedFileProcessor.clearMapping();
	}
	
	/**
	 * Extracts class name from a class signature.
	 * 
	 * @param	classSignature Signature of the class
	 * 
	 * @return	Name of the class
	 */
	public static String extractClassNameFromClassSignature(String classSignature)
	{
		String response;
		String[] tmp = classSignature.split("\\.");
		
		if (tmp.length < 1)
			response = tmp[0];
		else
			response = tmp[tmp.length-1];
		
		return response;	
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
