package wniemiec.app.executionflow.runtime.hook;

import org.aspectj.lang.JoinPoint;

import wniemiec.app.executionflow.collector.CallCollector;
import wniemiec.app.executionflow.collector.ConstructorCollector;
import wniemiec.app.executionflow.collector.InvokedCollector;
import wniemiec.app.executionflow.collector.MethodCollector;
import wniemiec.app.executionflow.invoked.Invoked;
import wniemiec.app.executionflow.io.processing.file.InvokedFileProcessor;
import wniemiec.app.executionflow.io.processing.file.TestMethodFileProcessor;

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
 * @since		1.0
 */
public abstract aspect RuntimeHook {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	protected static Invoked testMethod;
	
	
	//-------------------------------------------------------------------------
	//		Pointcuts
	//-------------------------------------------------------------------------
	protected pointcut isInternalPackage():
		within(wniemiec..*)
		|| within(java.lang..*)
		|| within(auxfiles..*)
		|| within(org.aspectj..*)
		|| within(org.eclipse..*)
		|| within(org.osgi..*)
		|| within(org.objectweb.asm..*);
	
	protected pointcut skipAnnotation():
		within(@wniemiec.app.executionflow.runtime.SkipCollection *)
		|| withincode(@wniemiec.app.executionflow.runtime.SkipInvoked * *.*(..))
		|| execution(@wniemiec.app.executionflow.runtime.SkipInvoked * *.*(..));
	
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
	 * Checks whether a signature belongs to a native Java method or 
	 * if it is a JUnit method.
	 * 
	 * @param		signature Signature to be analyzed
	 * 
	 * @return		If the signature belongs to a native method or JUnit method
	 */
	protected boolean isNativeMethod(String signature) {
		return	(signature == null)
				|| signature.matches("^java\\..+") 
				|| signature.matches("^jdk\\..+")
				|| signature.contains("^org\\.junit\\..+")
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
		resetCollectors();
		TestMethodFileProcessor.clearMapping();
		InvokedFileProcessor.clearMapping();
	}
	
	private void resetCollectors() {
		InvokedCollector collector = MethodCollector.getInstance();
		collector.reset();
		
		collector = ConstructorCollector.getInstance();
		collector.reset();
		
		CallCollector callCollector = CallCollector.getInstance();
		callCollector.reset();
	}
	
	protected String removeParametersFromSignature(String signature) {
		return signature.substring(signature.indexOf("("));
	}
	
	protected boolean wasInterrupted() {
		return Thread.currentThread().isInterrupted();
	}
}
