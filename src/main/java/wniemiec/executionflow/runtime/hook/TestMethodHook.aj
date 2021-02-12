package wniemiec.executionflow.runtime.hook;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aspectj.lang.JoinPoint;

import wniemiec.api.junit4.JUnit4API;
import wniemiec.executionflow.App;
import wniemiec.executionflow.ConstructorExecutionFlow;
import wniemiec.executionflow.ExecutionFlow;
import wniemiec.executionflow.MethodExecutionFlow;
import wniemiec.executionflow.exporter.ExportManager;
import wniemiec.executionflow.invoked.InvokedContainer;
import wniemiec.executionflow.invoked.InvokedInfo;
import wniemiec.executionflow.io.manager.FileManager;
import wniemiec.executionflow.io.manager.FilesManager;
import wniemiec.executionflow.io.manager.InvokedManager;
import wniemiec.executionflow.io.processor.ProcessorType;
import wniemiec.executionflow.io.processor.factory.PreTestMethodFileProcessorFactory;
import wniemiec.executionflow.io.processor.fileprocessor.PreTestMethodFileProcessor;
import wniemiec.executionflow.lib.LibraryManager;
import wniemiec.executionflow.runtime.collector.ClassPathSearcher;
import wniemiec.executionflow.runtime.collector.ConstructorCollector;
import wniemiec.executionflow.runtime.collector.MethodCollector;
import wniemiec.executionflow.user.RemoteControl;
import wniemiec.executionflow.user.User;
import wniemiec.util.data.storage.Session;
import wniemiec.util.logger.Logger;
import wniemiec.util.task.Checkpoint;

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
	private static boolean inTestMethodWithAspectsDisabled;
	private static boolean finishedTestMethodWithAspectsDisabled;
	
	
	
	
	private static boolean errorProcessingTestMethod;
	private static volatile boolean success;
	private boolean isRepeatedTest;
	private String lastRepeatedTestSignature;
	private Path classPath;
	private Path srcPath;
	
	
	
	public TestMethodHook() {
		onShutdown();
	}
	
	
	//-------------------------------------------------------------------------
	//		Initialization block
	//-------------------------------------------------------------------------
	static {
		inTestMethodWithAspectsDisabled = true;
		
	}
	
	
	
	
	//-------------------------------------------------------------------------
	//		Pointcuts
	//-------------------------------------------------------------------------
	private pointcut insideJUnit5RepeatedTest():
		!skipAnnotation() 
		&& execution(@org.junit.jupiter.api.RepeatedTest * *.*(..));
	
	private pointcut insideTestMethod():
		!skipAnnotation() 
		&& insideJUnitTest()
		&& !isInternalPackage()
		&& !withincode(@org.junit.Test * *.*());
	
	protected pointcut JUnit4Annotation():
		!skipAnnotation()
		&& execution(@org.junit.Test * *.*());
	
	protected pointcut JUnit5Annotation():
		!skipAnnotation() && (
		execution(@org.junit.jupiter.api.Test * *.*())
		|| execution(@org.junit.jupiter.params.ParameterizedTest * *.*(..))
		|| execution(@org.junit.jupiter.api.RepeatedTest * *.*(..)));
	
	
	//-------------------------------------------------------------------------
	//		Join points
	//-------------------------------------------------------------------------	
	before(): insideJUnit5RepeatedTest() {
		isRepeatedTest = true;
	}
	
	before(): insideTestMethod() {	
		if (errorProcessingTestMethod)
			return;
		
		App.checkDevelopmentMode();
		checkRepeatedTest(thisJoinPoint);
		checkInTestMethodWithAspectDisabled();
		
		if (finishedTestMethodWithAspectsDisabled)
			return;
		
		reset();
		
		testMethodSignature = getSignature(thisJoinPoint);
		
		collectSourceAndBinaryPaths(thisJoinPoint);
		
		if ((classPath == null) || (srcPath == null))
			return;
		
		collectTestMethod(thisJoinPoint);
		
		
		ProcessingManager.initializeManagers(!App.runningFromJUnitAPI());
		App.onFirstRun();
		App.beforeEachTestMethod();
		App.initializeLogger();
		
		inTestMethodWithAspectsDisabled = App.inTestMethodWithAspectsDisabled();
		try {
			App.doPreprocessing(testMethodInfo);
		}
		catch (IOException e) {
			Logger.error(e.getMessage());
			reset();
			
			errorProcessingTestMethod = true;
		}
	}
	
	after(): insideTestMethod() {
		if (errorProcessingTestMethod) {
			errorProcessingTestMethod = false;
			return;
		}
		
		if ((classPath == null) || (srcPath == null) || finishedTestMethodWithAspectsDisabled)
			return;

		if (inTestMethodWithAspectsDisabled) {
			parseInvokedCollector();
			reset();
			
			success = true;			
		}
		else {
			App.exportAllMethodsUsedInTestMethods();
			App.exportAllConstructorsUsedInTestMethods();
			
			App.runTestMethodWithAspectsDisabled(testMethodInfo);
			
			App.afterEachTestMethod();
			
			finishedTestMethodWithAspectsDisabled = true;
			inTestMethodWithAspectsDisabled = true;
			isRepeatedTest = false;
			lastRepeatedTestSignature = thisJoinPoint.getSignature().toString();
		}
	}
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------	
	private void onShutdown() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() {
		    	if (!success)
		    		finishedTestMethodWithAspectsDisabled = true;
		    }
		});
	}
	
	private void checkRepeatedTest(JoinPoint jp) {
		// Prevents repeated tests from being performed more than once
		if (finishedTestMethodWithAspectsDisabled && isRepeatedTest && 
				!jp.getSignature().toString().equals(lastRepeatedTestSignature)) {
			isRepeatedTest = false;
		}
	}
	
	private void checkInTestMethodWithAspectDisabled() {
		if (finishedTestMethodWithAspectsDisabled && inTestMethodWithAspectsDisabled && !isRepeatedTest) {
			finishedTestMethodWithAspectsDisabled = false;
			inTestMethodWithAspectsDisabled = true;
		}
	}
	
	@Override
	protected void reset() {
		super.reset();
		
		success = false;
	}
	
	
	
	
	
	
	private String getSignature(JoinPoint jp) {
		return removeReturnTypeFromSignature(jp.getSignature().toString());
	}
	
	private String removeReturnTypeFromSignature(String signature) {
		return signature.substring(signature.indexOf(' ') + 1);
	}
	
	private void collectSourceAndBinaryPaths(JoinPoint jp) {
		String classSignature = getClassSignature(jp);
				
		try {
			classPath = ClassPathSearcher.findBinPath(classSignature);
			srcPath = ClassPathSearcher.findSrcPath(classSignature);
		}
		catch(IOException | NoClassDefFoundError e) {
			Logger.error(e.getMessage());
			
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
			Logger.error("Test method info - "+e.getMessage());
		}
	}
	
	private void createTestMethodInfo(JoinPoint jp) {
		testMethodInfo = new InvokedInfo.Builder()
				.binPath(classPath)
				.srcPath(srcPath)
				.invokedSignature(testMethodSignature)
				.args(getParameterValues(jp))
				.build();
		
		dump();
	}
	
	
	
	
	
	
	
	
	
	
	
	// APP
	
	
	
	
	
	
	
	
	private void dump() {
		Logger.debug("Test method collector: " + testMethodInfo);
	}
	
	
	
	
	
	
	
	// PARSE COLLECTORS
	private void parseInvokedCollector() {
		ExecutionFlow methodExecutionFlow = new MethodExecutionFlow(
				MethodCollector.getCollector()
		);
		methodExecutionFlow.run();
		
		ExecutionFlow constructorExecutionFlow = new ConstructorExecutionFlow(
				ConstructorCollector.getConstructorCollector().values()
		);
		constructorExecutionFlow.run();
	}
	
	
	
	
	
	
	
	
	
	
	// EXPORT
	
	
	
}
