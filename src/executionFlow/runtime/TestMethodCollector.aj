package executionFlow.runtime;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import executionFlow.ConsoleOutput;
import executionFlow.ConstructorExecutionFlow;
import executionFlow.ExecutionFlow;
import executionFlow.MethodExecutionFlow;
import executionFlow.core.file.FileManager;
import executionFlow.core.file.InvokerManager;
import executionFlow.core.file.ParserType;
import executionFlow.core.file.parser.factory.PreTestMethodFileParserFactory;
import executionFlow.exporter.TestedInvokersExporter;
import executionFlow.info.InvokerInfo;
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
	private InvokerManager testMethodManager;
	private static boolean firstTime = true;
	private static boolean finished = false;
	private String testClassName;
	private String testClassPackage;
	private static Checkpoint checkpoint = new Checkpoint("Test_Method");
	private boolean junit5NewTest;
	private Path testClassPath;
	private FileManager testMethodFileManager;
	
	
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
	{System.out.println("bef");
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
			String testClassSignature = InvokerInfo.extractClassSignature(testMethodSignature);
			testClassName = CollectorExecutionFlow.getClassName(testClassSignature);
			testClassPackage = MethodInvokerInfo.extractPackage(testClassSignature);
			Path testSrcPath = CollectorExecutionFlow.findSrcPath(testClassName, testClassSignature);
			testMethodArgs = thisJoinPoint.getArgs();
			
			try {
				testMethodInfo = new MethodInvokerInfo.MethodInvokerInfoBuilder()
					.classPath(testClassPath)
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
				MethodInvokerInfo.getCompiledFileDirectory(testClassPath),
				testClassPackage,
				new PreTestMethodFileParserFactory(testMethodArgs),
				"original_pre_processing"
			);
			
			// Checks if it the first execution
			if (testMethodManager == null && !checkpoint.isActive())
				testMethodManager = new InvokerManager(ParserType.PRE_TEST_METHOD, false);

			// Checks if there are files that were not restored in the last execution
			if (checkpoint.exists() && !checkpoint.isActive()) {
				// Deletes backup file from the last execution
				testMethodManager.deleteBackup();
				checkpoint.delete();
			}
			
			firstTime = !checkpoint.exists();
			
			// Performs pre-processing of the file containing the test method so 
			// that the collection of the methods is done even if an assert fails
			if (firstTime) {System.out.println(collectedMethods);
				ConsoleOutput.showInfo("Pre-processing test method...");
				
				
				System.out.println(testMethodFileManager);
		
				
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
	 * Executed after the end of a test method.
	 */
	after(): testMethodCollector() 
	{System.out.println("af");
		if (finished)
			return;
		
		// Runs a new process of the application. This code block must only be
		// executed once per test file
		if (firstTime) {
			boolean hasError = false;
			
			
			System.out.println("RUN");TestMethodRunner.run(testClassName, testClassPath, testClassPackage);System.out.println("END RUN");
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
			
//			if (junit5NewTest) {
//				firstTime = false;
//				junit5NewTest = false;
//				testMethodManager.remove(testMethodFileManager);
//				finished = false;
//			}
			
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
		ef.setExporter(new TestedInvokersExporter("Invokers_TestMethods", new File(ExecutionFlow.getCurrentProjectRoot(), "testPaths")))
			.export();
		
		// Gets test paths of the collected constructors and export them
		ef = new ConstructorExecutionFlow(constructorCollector.values());
		ef.execute().export();
		
		// Exports tested constructors to a CSV
		ef.setExporter(new TestedInvokersExporter("Invokers_TestMethods", new File(ExecutionFlow.getCurrentProjectRoot(), "testPaths")))
			.export();
		
		reset();	// Prepares for next test
	}
}
