package executionFlow.runtime.collector;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import executionFlow.ConstructorExecutionFlow;
import executionFlow.ExecutionFlow;
import executionFlow.MethodExecutionFlow;
import executionFlow.exporter.TestedInvokedExporter;
import executionFlow.info.InvokedInfo;
import executionFlow.info.MethodInvokedInfo;
import executionFlow.io.FileManager;
import executionFlow.io.InvokedManager;
import executionFlow.io.ProcessorType;
import executionFlow.io.processor.factory.PreTestMethodFileProcessorFactory;
import executionFlow.util.Checkpoint;
import executionFlow.util.ConsoleOutput;
import executionFlow.util.JUnit4Runner;


/**
 * Captures all test methods, not including internal calls.
 * 
 * @apiNote		Ignores methods with {@link executionFlow.runtime.SkipInvoked}
 * annotation, methods with {@link executionFlow.runtime._SkipInvoked} and all
 * methods from classes with {@link executionFlow.runtime.SkipCollection} 
 * annotation
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		2.0.0
 * @since		1.0
 */
public aspect TestMethodCollector extends RuntimeCollector
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static boolean firstTime = true;
	private static boolean finished = false;
	private static Checkpoint checkpoint = new Checkpoint("Test_Method");
	private static String outputDir;
	private InvokedManager testMethodManager;
	private String testClassName;
	private String testClassPackage;
	private boolean junit5NewTest;
	private Path testClassPath;
	private FileManager testMethodFileManager;
	
	
	//-------------------------------------------------------------------------
	//		Initialization block
	//-------------------------------------------------------------------------
	static {
		outputDir = ExecutionFlow.isDevelopment() ? "examples\\results" : "results";
	}
	
	
	//-------------------------------------------------------------------------
	//		Pointcuts
	//-------------------------------------------------------------------------
	/**
	 * Intercepts JUnit 4 test methods.
	 */
	pointcut junit4():
		!junit4_internal() && 
		execution(@org.junit.Test * *.*());
	
	/**
	 * Intercepts JUnit 4 test methods.
	 */
	pointcut junit5():
		!junit5_internal() && (
			execution(@org.junit.jupiter.api.Test * *.*()) ||
			execution(@org.junit.jupiter.params.ParameterizedTest * *.*(..)) ||
			execution(@org.junit.jupiter.api.RepeatedTest * *.*(..))
		);
	
	/**
	 * Intercepts JUnit 5 new tests (parameterized test and repeated test).
	 */
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
	 * Intercept test methods.
	 */
	pointcut testMethodCollector():
		!skipAnnotation() &&
		(junit4() || junit5()) &&
		!junit4_internal() && !junit5_internal() && !withincode(@org.junit.Test * *.*());
	
	/**
	 * Executed before each test method.
	 */
	before(): testMethodCollector()
	{
		if (finished)
			return;
		
		reset();
		ExecutionFlow.init();
		
		testMethodSignature = CollectorExecutionFlow.extractMethodSignature(thisJoinPoint.getSignature().toString());

		// Gets information about test method
		try {
			// Gets compiled file path of the test method
			String className = thisJoinPoint.getTarget().getClass().getSimpleName();
			String classSignature = thisJoinPoint.getSignature().getDeclaringTypeName();
			testClassPath = CollectorExecutionFlow.findClassPath(className, classSignature);
			
			// Gets source file path of the test method
			String testClassSignature = InvokedInfo.extractClassSignature(testMethodSignature);
			testClassName = CollectorExecutionFlow.getClassName(testClassSignature);
			testClassPackage = MethodInvokedInfo.extractPackage(testClassSignature);
			Path testSrcPath = CollectorExecutionFlow.findSrcPath(testClassName, testClassSignature);
			testMethodArgs = thisJoinPoint.getArgs();
			
			try {
				testMethodInfo = new MethodInvokedInfo.MethodInvokedInfoBuilder()
					.binPath(testClassPath)
					.methodSignature(testMethodSignature)
					.args(testMethodArgs)
					.srcPath(testSrcPath)
					.build();
			} catch(IllegalArgumentException e) {
				ConsoleOutput.showError("Test method info - "+e.getMessage());
				e.printStackTrace();
			}
			
			testMethodFileManager = new FileManager(
				testSrcPath,
				MethodInvokedInfo.getCompiledFileDirectory(testClassPath),
				testClassPackage,
				new PreTestMethodFileProcessorFactory(testMethodArgs),
				"pre_processing.original"
			);
			
			// Checks if it the first execution
			if (testMethodManager == null && !checkpoint.isActive())
				testMethodManager = new InvokedManager(ProcessorType.PRE_TEST_METHOD, false);

			// Checks if there are files that were not restored in the last execution
			if (checkpoint.exists() && !checkpoint.isActive()) {
				// Deletes backup file from the last execution
				testMethodManager.deleteBackup();
				checkpoint.delete();
			}
			
			firstTime = !checkpoint.exists();
			
			// Performs pre-processing of the file containing the test method
			if (firstTime) {
				ConsoleOutput.showInfo("Pre-processing test method...");
				
				// Enables checkpoint
				checkpoint.enable();
				
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
	 * Executed after the end of a test method.
	 */
	after(): testMethodCollector() 
	{
		if (finished)
			return;
		
		// Runs a new process of the application. This code block must only be
		// executed once per test file
		if (firstTime) {
			boolean hasError = false;
			
			
			System.out.println("RUN");JUnit4Runner.run(testClassName, testClassPath, testClassPackage);System.out.println("END RUN");
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
				ConsoleOutput.showError("See more: https://github.com/williamniemiec/"
						+ "ExecutionFlow/wiki/Solu%C3%A7%C3%A3o-de-problemas#could-not-recover-all-backup-files");
				e.printStackTrace();
			}
			
			// Restores original method files and its compiled files
			try {
				if (ExecutionFlow.invokedManager.load())
					ExecutionFlow.invokedManager.restoreAll();		
			} catch (ClassNotFoundException e) {
				hasError = true;
				ConsoleOutput.showError("Class FileManager not found");
				e.printStackTrace();
			} catch (IOException e) {
				hasError = true;
				ConsoleOutput.showError("Could not recover all backup files for methods");
				ConsoleOutput.showError("See more: https://github.com/williamniemiec/"
						+ "ExecutionFlow/wiki/Solu%C3%A7%C3%A3o-de-problemas#could-not-recover-all-backup-files");
				e.printStackTrace();
			}

			testMethodManager.restoreAll();
			
			// Deletes backup files
			ExecutionFlow.invokedManager.deleteBackup();
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
		if (finished && !junit5NewTest)
			return;

		// Gets test paths of the collected methods and export them
		ExecutionFlow ef = new MethodExecutionFlow(methodCollector);
		ef.execute().export();
		
		// Exports tested methods to a CSV
		ef.setExporter(new TestedInvokedExporter("Invokers_TestMethods", new File(ExecutionFlow.getCurrentProjectRoot(), outputDir)))
			.export();
		
		// Gets test paths of the collected constructors and export them
		ef = new ConstructorExecutionFlow(constructorCollector.values());
		ef.execute().export();
		
		// Exports tested constructors to a CSV
		ef.setExporter(new TestedInvokedExporter("Invokers_TestMethods", new File(ExecutionFlow.getCurrentProjectRoot(), outputDir)))
			.export();
		
		reset();	// Prepares for next test
	}
}
