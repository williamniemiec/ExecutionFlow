package executionFlow.runtime;

import java.io.IOException;
import java.nio.file.Path;

import executionFlow.ConsoleOutput;
import executionFlow.ConstructorExecutionFlow;
import executionFlow.ExecutionFlow;
import executionFlow.MethodExecutionFlow;
import executionFlow.core.file.FileManager;
import executionFlow.core.file.MethodManager;
import executionFlow.core.file.ParserType;
import executionFlow.core.file.parser.factory.AssertFileParserFactory;
import executionFlow.info.MethodInvokerInfo;


/**
 * Captures all test methods, not including internal calls.
 * 
 * @apiNote		Ignores methods with {@link executionFlow.runtime.SkipMethod}
 * annotation, methods with {@link executionFlow.runtime._SkipMethod} and all
 * methods from classes with {@link executionFlow.runtime.SkipCollection} 
 * annotation
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		1.5
 * @since		1.0
 */
public aspect TestMethodCollector extends RuntimeCollector
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private MethodManager testMethodManager;
	private static boolean firstTime = true;
	private static boolean finished = false;
	private String testClassName;
	private String testClassPackage;
	private static Checkpoint checkpoint = new Checkpoint("Test_Method");
	private boolean junit5NewTest;
	private Path testClassPath;
	
	
	//-------------------------------------------------------------------------
	//		Pointcuts
	//-------------------------------------------------------------------------
	pointcut junit4():
		!junit4_internal() && 
		execution(@org.junit.Test * *.*());
	
	pointcut junit5():
		!junit5_internal() && (
			execution(@org.junit.jupiter.api.Test * *.*()) ||
			execution(@org.junit.jupiter.params.ParameterizedTest * *.*(..)) ||
			execution(@org.junit.jupiter.api.RepeatedTest * *.*(..))
		);
	
	pointcut testMethodCollector():
		!skipAnnotation() &&
		(junit4() || junit5());
	
	pointcut junit5_newTests():
		execution(@org.junit.jupiter.params.ParameterizedTest * *.*(..)) ||
		execution(@org.junit.jupiter.api.RepeatedTest * *.*(..));
	
	/**
	 * Executed on test not supported by the application.
	 */
	before(): junit5_newTests()
	{
		junit5NewTest = true;
	}
	
	/**
	 * Executed before each test method.
	 */
	before(): testMethodCollector()
	{
		if (finished)
			return;
		
		if (junit5NewTest) {
			ConsoleOutput.showWarning("New JUnit 5 annotations are not supported");
			return;
		}
		
		reset();

		testMethodSignature = CollectorExecutionFlow.extractMethodSignature(thisJoinPoint.getSignature().toString());

		// Gets information about test method
		try {
			// Gets compiled file path of the test method
			String className = thisJoinPoint.getTarget().getClass().getSimpleName();
			String classSignature = thisJoinPoint.getSignature().getDeclaringTypeName();
			testClassPath = CollectorExecutionFlow.findClassPath(className, classSignature);
			
			// Gets source file path of the test method
			String testClassSignature = CollectorExecutionFlow.extractClassSignature(testMethodSignature);
			testClassName = CollectorExecutionFlow.getClassName(testClassSignature);
			testClassPackage = MethodInvokerInfo.extractPackage(testClassSignature);
			Path testSrcPath = CollectorExecutionFlow.findSrcPath(testClassName, testClassSignature);
			testMethodArgs = thisJoinPoint.getArgs();

			testMethodInfo = new MethodInvokerInfo.MethodInvokerInfoBuilder()
				.classPath(testClassPath)
				.methodSignature(testMethodSignature)
				.args(testMethodArgs)
				.srcPath(testSrcPath)
				.build();
			
			FileManager testMethodFileManager = new FileManager(
				testSrcPath,
				MethodInvokerInfo.getCompiledFileDirectory(testClassPath),
				testClassPackage,
				new AssertFileParserFactory(),
				"original_assert"
			);

			// Checks if it the first execution
			if (testMethodManager == null && !checkpoint.isActive())
				testMethodManager = new MethodManager(ParserType.ASSERT_TEST_METHOD, false);

			// Checks if there are files that were not restored in the last execution
			if (checkpoint.exists() && !checkpoint.isActive()) {
				// Deletes backup file from the last execution
				testMethodManager.deleteBackup();
				checkpoint.delete();
			}
			
			firstTime = !checkpoint.exists();
			
			// Performs pre-processing of the file containing the test method so 
			// that the collection of the methods is done even if an assert fails
			if (firstTime) {
				ConsoleOutput.showInfo("Pre-processing test method...");
				
				// Enables checkpoint
				checkpoint.enable();
				
				// Parses test method and handles all asserts so that method collection 
				// is done even if an assert fails
				testMethodManager.parse(testMethodFileManager).compile(testMethodFileManager);
				
				ConsoleOutput.showInfo("Pre-processing completed");
			}
		} catch(IOException | ClassNotFoundException e) {
			ConsoleOutput.showError(e.getMessage());
			e.printStackTrace();
			
			System.exit(-1);	// Stops execution if a problem occurs
		}
	}
	
	/**
	 * Executed after the end of a method with <code>@Test</code> annotation.
	 */
	after(): testMethodCollector() 
	{			
		if (junit5NewTest)
			return;
		
		// Runs a new process of the application. This code block must only be
		// executed once per test file
		if (firstTime) {
			boolean hasError = false;
			
			firstTime = false;
			TestMethodRunner.run(testClassName, testClassPath, testClassPackage);
			finished = true;
			
			// Restores original test method file and its compiled file
			try {
				if (ExecutionFlow.testMethodManager.load())
					ExecutionFlow.testMethodManager.restoreAll();	
				
			} catch (ClassNotFoundException e) {
				hasError = true;
				ConsoleOutput.showError("Class FileManager not found");
				e.printStackTrace();
			} catch (IOException e) {
				hasError = true;
				ConsoleOutput.showError("Could not recover the backup file of the test method");
				ConsoleOutput.showError("See more: https://github.com/williamniemiec/ExecutionFlow/wiki/Solu%C3%A7%C3%A3o-de-problemas#could-not-recover-all-backup-files");
				e.printStackTrace();
			}
			
			// Restores original method files and its compiled files
			try {
				if (ExecutionFlow.invokerManager.load())
					ExecutionFlow.invokerManager.restoreAll();		
			} catch (ClassNotFoundException e) {
				hasError = true;
				ConsoleOutput.showError("Class FileManager not found");
				e.printStackTrace();
			} catch (IOException e) {
				hasError = true;
				ConsoleOutput.showError("Could not recover all backup files for methods");
				ConsoleOutput.showError("See more: https://github.com/williamniemiec/ExecutionFlow/wiki/Solu%C3%A7%C3%A3o-de-problemas#could-not-recover-all-backup-files");
				e.printStackTrace();
			}
			
			testMethodManager.restoreAll();
			
			// Deletes backup files
			ExecutionFlow.invokerManager.deleteBackup();
			ExecutionFlow.testMethodManager.deleteBackup();
			testMethodManager.deleteBackup();
			
			// Disables checkpoint
			try {
				checkpoint.disable();
			} catch (IOException e) {
				hasError = true;
				ConsoleOutput.showError("Checkpoint cannot be disabled");
				e.printStackTrace();
			}
			
			// Stops execution if a problem occurs
			if (hasError)
				System.exit(-1);
			
			return;
		}
		
		// If the execution of the process of the application has been 
		// completed all test paths have been computed
		if (finished)
			return;

		// Gets test paths of the collected methods and export them
		ExecutionFlow ef = new MethodExecutionFlow(methodCollector);
		ef.execute().export();
		
		// Gets test paths of the collected constructors and export them
		ef = new ConstructorExecutionFlow(constructorCollector.values());
		ef.execute().export();
		
		reset();	// Prepares for next test
	}
}
