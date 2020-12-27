package executionflow.runtime.collector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aspectj.lang.JoinPoint;

import executionflow.info.InvokedContainer;
import executionflow.info.InvokedInfo;
import executionflow.io.processor.fileprocessor.InvokedFileProcessor;
import executionflow.io.processor.fileprocessor.TestMethodFileProcessor;

/**
 * Responsible for data collection of methods and class constructors used in 
 * tests.
 * 
 * @apiNote		It will ignore all methods of a class if it has 
 * {@link executionflow.runtime.SkipCollection} annotation
 * @apiNote		It will ignore methods with 
 * {@link executionflow.runtime.SkipInvoked} annotation
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		6.0.0
 * @since		1.0
 */
public abstract aspect RuntimeCollector {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	/**
	 * Stores signature of collected methods.<hr/>
	 * <b>Format: </b><code>method_name + method_arguments + 
	 * constructor@hashCode (if it has one)</code>
	 */
	protected static List<String> collectedMethods;
	
	/**
	 * Stores information about collected methods.<hr/>
	 * <ul>
	 * 	<li><b>Key:</b> Method invocation line</li>
	 * 	<li><b>Value:</b> List of methods invoked from this line</li>
	 * </ul>
	 */
	protected static Map<Integer, List<InvokedContainer>> methodCollector;
	
	/**
	 * Stores information about collected constructor.<hr/>
	 * <ul>
	 * 	<li><b>Key(with arguments):</b>		
	 * 	<code>invocationLine + classSignature[arg1,arg2,...]</code></li>
	 * 	<li><b>Key(without arguments):</b>	
	 * 	<code>invocationLine + classSignature[]</code></li>
	 * 	<li><b>Value:</b> Informations about the constructor</li>
	 * </ul>
	 */
	protected static Map<String, InvokedContainer> constructorCollector;

	/**
	 * Stores anonymous class signature where it is created and where it is 
	 * declared.
	 * <ul>
	 * 	<li><b>Key:</b>	Class signature where anonymous class is created</li>
	 * 	<li><b>Value:</b> Class signature where anonymous class is declared</li>
	 * </ul> 
	 */
	protected static Map<String, String> anonymousClassSignatures;
	
	protected static Map<InvokedInfo, Set<String>> methodsCalledByTestedInvoked;
	protected static String testMethodSignature;
	protected static InvokedInfo testMethodInfo;
	protected static boolean skipCollection;
	protected static int lastInvocationLine;
	
	
	//-------------------------------------------------------------------------
	//		Initialization block
	//-------------------------------------------------------------------------
	static {
		collectedMethods = new ArrayList<>();
		methodCollector = new LinkedHashMap<>();
		constructorCollector = new LinkedHashMap<>();
		methodsCalledByTestedInvoked = new HashMap<>();
		anonymousClassSignatures = new HashMap<>();
	}
	
	
	//-------------------------------------------------------------------------
	//		Pointcuts
	//-------------------------------------------------------------------------
	protected pointcut skipAnnotation():
		within(@executionflow.runtime.SkipCollection *)
		|| withincode(@executionflow.runtime.SkipInvoked * *.*(..))
		|| execution(@executionflow.runtime.SkipInvoked * *.*(..));
	
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
	protected boolean isNativeMethod(JoinPoint jp) {
		String signature = jp.getSignature().toString();
		
		return	(signature == null)
				|| signature.contains("java.") 
				|| signature.contains("jdk.")
				|| signature.contains("org.junit.")
				|| signature.matches(".+(\\$|\\.)[0-9]+.+");
	}
	
	protected boolean isMethodSignature(JoinPoint jp) {
		final String regexMethodSignature = "[A-z\\.]+(\\s|\\t)+([A-z0-9-_$]+\\.)+"
				+ "[A-z0-9-_$]+\\([A-z0-9-\\._$,\\s]*\\)";
		String signature = jp.getSignature().toString();
		
		return	signature.matches(regexMethodSignature) 
				&& !signature.matches(".*\\.(access\\$[0-9]+\\().*");
	}

	protected void reset() {
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
	
	protected String removeParametersFromSignature(String signature) {
		return signature.substring(signature.indexOf("("));
	}
}
