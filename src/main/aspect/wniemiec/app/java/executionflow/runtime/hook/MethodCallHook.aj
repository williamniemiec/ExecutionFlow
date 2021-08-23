package wniemiec.app.java.executionflow.runtime.hook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;

import wniemiec.app.java.executionflow.collector.CallCollector;
import wniemiec.app.java.executionflow.collector.parser.TestedInvokedParser;
import wniemiec.app.java.executionflow.invoked.Invoked;

/**
 * Captures all methods called within the tested invoked, where an invoked can
 * be a method or a constructor.
 * 
 * @apiNote		Test method, that is, the method that calls the tested method
 * must have {@link executionflow.runtime.CollectMethodsCalled} annotation
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @since		2.0.0
 */
@SuppressWarnings("unused")
public aspect MethodCallHook extends RuntimeHook {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private Invoked invoked;
	
	
	//-----------------------------------------------------------------------
	//		Pointcuts
	//-----------------------------------------------------------------------
	/**
	 * Gets tested method signatures by a JUnit test that has 
	 * {@link executionflow.runtime.CollectMethodsCalled} annotation.
	 */
	private pointcut testedInvoked(): 
		!within(@wniemiec.app.java.executionflow.runtime.SkipCollection *)
		&& !withincode(@wniemiec.app.java.executionflow.runtime.SkipInvoked * *.*(..))
		&& cflow(execution(@wniemiec.app.java.executionflow.runtime.CollectMethodsCalled * *.*(..)))
		&& insideJUnitTest()
		&& !get(* *.*) 
		&& !set(* *.*)
		&& !isInternalPackage()
		&& !execution(public int hashCode());
	
	/**
	 * Intercepts methods called within an invoked with 
	 * {@link @wniemiec.app.executionflow.runtime.CollectCalls} annotation.
	 */
	private pointcut invokedMethodByTestedInvoker():
		!withincode(@wniemiec.app.java.executionflow.runtime.SkipInvoked * *.*(..))
		&& !get(* *.*) 
		&& !set(* *.*) 
		&& !isInternalPackage()
		&& insideConstructor()	|| insideMethod();
	
	private pointcut insideConstructor():
		withincode(@wniemiec.app.java.executionflow.runtime.CollectCalls *.new(..))  
		&& !isInternalPackage()
		&& !cflowbelow(withincode(@wniemiec.app.java.executionflow.runtime.CollectCalls * *(..)));
	
	private pointcut insideMethod():
		withincode(@wniemiec.app.java.executionflow.runtime.CollectCalls * *(..))
		&& !isInternalPackage()
		&& !cflowbelow(withincode(@wniemiec.app.java.executionflow.runtime.CollectCalls *.new(..)))  
		&& !cflowbelow(withincode(@wniemiec.app.java.executionflow.runtime.CollectCalls * *(..)));
	
	
	//-------------------------------------------------------------------------
	//		Join points
	//-------------------------------------------------------------------------
	before(): testedInvoked() {
		if (wasInterrupted())
			return;
		
		if (isNativeMethod(getSignature(thisJoinPoint)) || !isValidSignature(thisJoinPoint)) 
			return;
		
		collectInvoked(thisJoinPoint);
	}
	
	before(): invokedMethodByTestedInvoker() {
		if (wasInterrupted())
			return;
		
		if (!isMethod(thisJoinPoint) || isNativeMethod(getSignature(thisJoinPoint)) || !wasTestedInvokedCollected())
			return;
		
		CallCollector callCollector = CallCollector.getInstance();
		callCollector.collectCall(extractMethodCalledSignature(thisJoinPoint), invoked);
	}
	

	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	private String getSignature(JoinPoint jp) {
		return jp.getSignature().toString();
	}
	
	private boolean isValidSignature(JoinPoint jp) {
		String signature = jp.getSignature().toString();
		
		return signature.contains("(");
	}

	private void collectInvoked(JoinPoint jp) {
		invoked = new Invoked.Builder()
				.signature(extractSignatureFromTypedSignature(jp))
				.isConstructor(isConstructor(jp))
				.build();
	}
	
	private String extractSignatureFromTypedSignature(JoinPoint jp) {
		String accessAndSignature = isConstructor(jp) ? 
				jp.getSignature().toLongString() 
				: jp.getSignature().toString();

		return accessAndSignature.substring(accessAndSignature.indexOf(" ") + 1);
	}
	
	private boolean isConstructor(JoinPoint jp) {
		return jp.getKind().equals("constructor-call");
	}
	
	private boolean isMethod(JoinPoint jp) {
		return jp.getKind().equals("method-call");
	}
	
	private boolean wasTestedInvokedCollected() {
		return (invoked != null);
	}
	
	private String removeReturnTypeFromSignature(String signature) {
		return signature.substring(signature.indexOf(' ') + 1);
	}
	
	private String extractMethodCalledSignature(JoinPoint jp) {
		StringBuilder methodSignature = new StringBuilder();
		String signature = jp.getSignature().toString();
		
		methodSignature.append(jp.getSignature().getDeclaringTypeName());
		methodSignature.append(".");
		methodSignature.append(jp.getSignature().getName());
		methodSignature.append(signature.substring(signature.indexOf("(")));

		return methodSignature.toString().replaceAll("\\$", ".");
	}
}
