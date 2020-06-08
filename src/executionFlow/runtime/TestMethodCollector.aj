package executionFlow.runtime;

import java.io.IOException;

import org.junit.*;

import executionFlow.ConsoleOutput;
import executionFlow.ExecutionFlow;
import executionFlow.core.JDB;
import executionFlow.core.file.FileCompiler;
import executionFlow.core.file.FileManager;
import executionFlow.core.file.MethodManager;
import executionFlow.core.file.ParserType;
import executionFlow.core.file.parser.FileParser;
import executionFlow.core.file.parser.factory.AssertFileParserFactory;
import executionFlow.exporter.ConsoleExporter;
import executionFlow.exporter.FileExporter;
import executionFlow.info.ClassConstructorInfo;
import executionFlow.info.ClassMethodInfo;
import executionFlow.info.CollectorInfo;
import executionFlow.info.SignaturesInfo;


/**
 * Captures all executed methods with <code>@Test</code> annotation, not 
 * including internal calls.
 * 
 * @apiNote		Ignores methods with {@link SkipMethod} annotation, methods with
 * {@link _SkipMethod} and all methods from classes with {@link SkipCollection}
 * annotation
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		1.5
 * @since		1.0
 */
public aspect TestMethodCollector extends RuntimeCollector
{
	private MethodManager testMethodManager;
	static boolean firstTime = true;
	static boolean finished = false;
	private String testClassName;
	private String testClassPackage;
	private static Checkpoint checkpoint = new Checkpoint("Test_Method");
	
	
	//-------------------------------------------------------------------------
	//		Pointcut
	//-------------------------------------------------------------------------
	pointcut testMethodCollector():
		!cflow(execution(@SkipMethod * *.*())) 
		&& !cflow(execution(@_SkipMethod * *.*()))
		&& execution(@Test * *.*())
		&& !execution(public int hashCode())
//		(execution(@AssertTest * *.*()) || 
		 //execution(@RepeatedTest * *.*()) ||
//		 execution(@ParameterizedTest * *.*()) ||
//		 execution(@TestFactory * *.*())) 
		&& !execution(private * *(..))
		&& !execution(@Ignore * *(..))
		&& !execution(@Before * *(..))
		&& !execution(@After * *(..))
		&& !execution(@BeforeClass * *(..))
		&& !execution(@AfterClass * *(..))
		&& !within(@SkipCollection *)
		&& !execution(@SkipMethod * *())
		&& !within(ExecutionFlow)
		&& !within(JDB)
		&& !within(FileCompiler)
		&& !within(FileParser)
		&& !within(FileManager)
		&& !within(ConsoleExporter)
		&& !within(FileExporter)
		&& !within(ClassConstructorInfo)
		&& !within(ClassMethodInfo)
		&& !within(CollectorInfo)
		&& !within(SignaturesInfo)
		&& !within(CollectorExecutionFlow)
		&& !within(ConstructorCollector) 
		&& !within(MethodCollector)
		&& !within(RuntimeCollector)
		&& !within(TestMethodCollector)
		&& !call(* org.junit.runner.JUnitCore.runClasses(*))
		&& !call(void org.junit.Assert.*(*,*));
	
	/**
	 * Executed before each method with <code>@Test</code> annotation.
	 */
	before(): testMethodCollector()
	{
		if (finished)
			return;
		
		reset();

		testMethodSignature = CollectorExecutionFlow.extractMethodSignature(thisJoinPoint.getSignature().toString());
		testMethodPackage = testMethodSignature.replaceAll("\\(.*\\)", "");
		
		// Gets information about test method
		try {
			// Gets compiled file path of the test method
			String className = thisJoinPoint.getTarget().getClass().getSimpleName();
			String classSignature = thisJoinPoint.getSignature().getDeclaringTypeName();
			testClassPath = CollectorExecutionFlow.findClassPath(className, classSignature);
			
			// Gets source file path of the test method
			String testClassSignature = CollectorExecutionFlow.extractClassSignature(testMethodSignature);
			testClassName = CollectorExecutionFlow.getClassName(testClassSignature);
			testClassPackage = ClassMethodInfo.extractPackage(testClassSignature);
			testSrcPath = CollectorExecutionFlow.findSrcPath(testClassName, testClassSignature);
			
			FileManager testMethodFileManager = new FileManager(
				testSrcPath,
				ClassMethodInfo.getCompiledFileDirectory(testClassPath),
				testClassPackage,
				new AssertFileParserFactory(),
				"original_assert"
			);
			
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
				
				// Enabled checkpoint
				checkpoint.enable();
				
				// Parses test method and handles all asserts so that method collection 
				// is done even if {@link org.junit.ComparisonFailure} occurs
				testMethodManager.parse(testMethodFileManager).compile(testMethodFileManager);
				
				ConsoleOutput.showInfo("Pre-processing completed");
			}
		} catch(IOException | ClassNotFoundException e) {
			// Stops execution if a problem occurs
			System.exit(-1);
		}	
	}
	
	/**
	 * Executed after the end of a method with <code>@Test</code> annotation.
	 */
	after(): testMethodCollector() 
	{	
		
		// Runs a new process of the application. This code block must only be
		// executed once per test file
		if (firstTime) {
			firstTime = false;
			TestMethodRunner.run(testClassName, testClassPath, testClassPackage);
			finished = true;
			
			// Restores original method files and its compiled files
			ExecutionFlow.methodManager.restoreAll();
			
			// Restores original test method file and its compiled file
			testMethodManager.restoreAll();
			
			// Deletes backup files
			ExecutionFlow.methodManager.deleteBackup();
			ExecutionFlow.testMethodManager.deleteBackup();
			testMethodManager.deleteBackup();
			
			// Disables checkpoint
			try {
				checkpoint.disable();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return;
		}
		
		// If the execution of the process of the application has been 
		// completed all test paths have been computed
		if (finished)
			return;
		
		// Gets test paths of the collected methods and export them
		ExecutionFlow ef = new ExecutionFlow(methodCollector);
		
		try {
			ef.execute().export();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		reset();	// Prepares for next test
		TestMethodRunner.putEndDelimiter();
	}
}
