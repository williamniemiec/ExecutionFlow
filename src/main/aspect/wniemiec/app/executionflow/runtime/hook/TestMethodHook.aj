package wniemiec.app.executionflow.runtime.hook;

import java.io.IOException;
import java.nio.file.Path;

import org.aspectj.lang.JoinPoint;

import wniemiec.app.executionflow.App;
import wniemiec.app.executionflow.invoked.Invoked;
import wniemiec.app.executionflow.io.ClassPathSearcher;
import wniemiec.io.consolex.Consolex;
import wniemiec.util.task.Scheduler;

/**
 * Run in each test method
 * 
 * @apiNote		Ignores methods and constructors with {@link executionflow
 * .runtime.SkipInvoked} annotation and all methods from classes with 
 * {@link executionflow.runtime.SkipCollection} annotation.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		6.0.5
 * @since		1.0
 */
@SuppressWarnings("unused")
public aspect TestMethodHook extends RuntimeHook {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final int COLLECTION_TIMEOUT_MS = 10000;
	private static String testMethodSignature;
	private boolean isRepeatedTest;
	private String lastRepeatedTestSignature;
	private Path classPath;
	private Path srcPath;
	private Object returnedContent;
	
	
	//-------------------------------------------------------------------------
	//		Pointcuts
	//-------------------------------------------------------------------------
	private pointcut insideJUnit5RepeatedTest():
		!skipAnnotation()
		&& !isInternalPackage()
		&& execution(@org.junit.jupiter.api.RepeatedTest * *.*(..));
	
	private pointcut insideTestMethod():
		!skipAnnotation() 
		&& insideJUnitTest()
		&& !isInternalPackage()
		&& !withincode(@org.junit.Test * *.*());
	
	protected pointcut JUnit4Annotation():
		!skipAnnotation()
		&& !isInternalPackage()
		&& execution(@org.junit.Test * *.*());
	
	protected pointcut JUnit5Annotation():
		!isInternalPackage()
		&& !skipAnnotation() 
		&& (
			execution(@org.junit.jupiter.api.Test * *.*())
			|| execution(@org.junit.jupiter.params.ParameterizedTest * *.*(..))
			|| execution(@org.junit.jupiter.api.RepeatedTest * *.*(..)));
	
	
	//-------------------------------------------------------------------------
	//		Join points
	//-------------------------------------------------------------------------	
	before(): insideJUnit5RepeatedTest() {
		isRepeatedTest = true;
	}
	
	Object around(): insideTestMethod()
	{
		returnedContent = new Object();
		
		beforeEachTestMethod(thisJoinPoint);
		
		boolean timeout = Scheduler.setTimeoutToRoutine(() -> {
			Consolex.writeInfo("Obtaining information about the tested methods of " + 
							   testMethodSignature + " ... ");
			returnedContent = proceed();
			Consolex.writeInfo("Done!");
		}, COLLECTION_TIMEOUT_MS);
		
		if (timeout)
			Consolex.writeWarning("Information on all tested methods will not " +
								  "be collected due to timeout");
		
		afterEachTestMethod(thisJoinPoint);
		
		return returnedContent;
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	private void beforeEachTestMethod(JoinPoint jp) {
		reset();
		checkRepeatedTest(jp);
		parseTestMethod(jp);
		
		if (hasSourceAndBinearyPath()) {
			collectTestMethod(jp);
			//App.inEachTestMethod(testMethod, isRepeatedTest);
		}
	}
	
	private void afterEachTestMethod(JoinPoint jp) {
		if (!hasSourceAndBinearyPath())
			return;

		//App.afterEachTestMethod(testMethod);
		
		if (!isRepeatedTest)
			lastRepeatedTestSignature = getSignature(jp);
	}
	
	private boolean hasSourceAndBinearyPath() {
		return ((srcPath != null) && (classPath != null));
	}

	private void parseTestMethod(JoinPoint jp) {
		testMethodSignature = getSignature(jp);
		findSourceAndBinaryPaths(jp);
	}
	
	
	private void checkRepeatedTest(JoinPoint jp) {
		// Prevents repeated tests from being performed more than once
		if (isRepeatedTest && !jp.getSignature().toString().equals(lastRepeatedTestSignature)) {
			isRepeatedTest = false;
		}
	}
	
	private String getSignature(JoinPoint jp) {
		return removeReturnTypeFromSignature(jp.getSignature().toString());
	}
	
	private String removeReturnTypeFromSignature(String signature) {
		return signature.substring(signature.indexOf(' ') + 1);
	}
	
	private void findSourceAndBinaryPaths(JoinPoint jp) {
		try {
			classPath = ClassPathSearcher.findBinPath(getClassSignature(jp));
			srcPath = ClassPathSearcher.findSrcPath(getClassSignature(jp));
		}
		catch(IOException | NoClassDefFoundError e) {
			Consolex.writeError(e.toString());
			
			System.exit(-1);
		}
	}
	
	private String getClassSignature(JoinPoint jp) {
		if (jp == null)
			return "";
		
		StringBuilder classSignature = new StringBuilder();
		String[] terms = jp.getSignature().toString().split("\\.");

		for (int i=0; i<terms.length-1; i++) {
			classSignature.append(terms[i]);
			classSignature.append(".");
		}
		
		// Removes last dot
		if (classSignature.length() > 0)
			classSignature.deleteCharAt(classSignature.length()-1);
		
		return removeReturnTypeFromSignature(classSignature.toString());
	}
	
	private Object[] getParameterValues(JoinPoint jp) {
		return jp.getArgs();
	}
	
	private void collectTestMethod(JoinPoint jp) {
		try {
			createTestMethodInfo(jp);
		} 
		catch(IllegalArgumentException e) {
			Consolex.writeError("Test method - " + e.getMessage());
		}
	}
	
	private void createTestMethodInfo(JoinPoint jp) {
		testMethod = new Invoked.Builder()
				.binPath(classPath)
				.srcPath(srcPath)
				.signature(testMethodSignature)
				.args(getParameterValues(jp))
				.build();
		
		dump();
	}
	
	private void dump() {
		Consolex.writeDebug("Test method: " + testMethod);
	}
	
	@Override
	protected void reset() {
		super.reset();
		
		testMethodSignature = null;
	}
}
