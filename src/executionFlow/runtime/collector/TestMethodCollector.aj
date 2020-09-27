package executionFlow.runtime.collector;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import executionFlow.ConstructorExecutionFlow;
import executionFlow.Control;
import executionFlow.ExecutionFlow;
import executionFlow.MethodExecutionFlow;
import executionFlow.dependency.DependencyManager;
import executionFlow.exporter.TestedInvokedExporter;
import executionFlow.info.CollectorInfo;
import executionFlow.info.InvokedInfo;
import executionFlow.info.MethodInvokedInfo;
import executionFlow.io.FileManager;
import executionFlow.io.FilesManager;
import executionFlow.io.ProcessorType;
import executionFlow.io.processor.InvokedFileProcessor;
import executionFlow.io.processor.PreTestMethodFileProcessor;
import executionFlow.io.processor.factory.PreTestMethodFileProcessorFactory;
import executionFlow.util.Checkpoint;
import executionFlow.util.ConsoleOutput;
import executionFlow.util.JUnit4Runner;


/**
 * Run in each test method
 * 
 * @apiNote		Ignores methods with {@link executionFlow.runtime.SkipInvoked}
 * annotation, methods with {@link executionFlow.runtime._SkipInvoked} and all
 * methods from classes with {@link executionFlow.runtime.SkipCollection} 
 * annotation
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.1.0
 * @since		1.0
 */
public aspect TestMethodCollector extends RuntimeCollector
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static boolean firstTime = true;
	private static boolean finished;
	private static Checkpoint checkpoint =
			new Checkpoint(ExecutionFlow.getAppRootPath(), "Test_Method");
	private static Checkpoint checkpoint_initial = 
			new Checkpoint(Path.of(System.getProperty("user.home")), "initial");
	private static int totalTests = -1;
	private static String outputDir;
	private String lastRepeatedTestSignature;
	private String testClassName;
	private String testClassPackage;
	private Path testClassPath;
	private FilesManager testMethodManager;
	private FileManager testMethodFileManager;
	private boolean isRepeatedTest;
	private volatile static boolean success;
	
	
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
	 * Intercepts repeated tests.
	 */
	pointcut junit5_repeatedTest():
		!skipAnnotation() &&
		execution(@org.junit.jupiter.api.RepeatedTest * *.*(..));
	
	before(): junit5_repeatedTest()
	{
		isRepeatedTest = true;
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
		// Prevents repeated tests from being performed more than once
		if (finished && isRepeatedTest && 
				!thisJoinPoint.getSignature().toString().equals(lastRepeatedTestSignature)) {
			isRepeatedTest = false;
		}
		
		// If it is in another test method and it it is not a repeated test 
		// then should run the application
		if (finished && !firstTime && !isRepeatedTest) {
			finished = false;
			firstTime = false;
		}
		
		if (finished)
			return;
		
		reset();
		testMethodSignature = 
				CollectorExecutionFlow.extractMethodSignature(thisJoinPoint.getSignature().toString());
		success = false;
		
		// Defines the routine to be executed after the app ends
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() {
		    	if (success)
		    		return;
		    	
	    		File mcti = new File(ExecutionFlow.getAppRootPath().toFile(), "mcti.ef");
	    		
	    		
	    		if (JUnit4Runner.isRunning()) {
			    	try {
						JUnit4Runner.quit();
					} 
			    	catch (IOException e) {}
	    		}
		    	    		
		    	// Restores original files			    	
		    	restoreTestMethodFiles();
		    	restoreInvokedFiles();
		    	
		    	if (testMethodManager != null)
		    		testMethodManager.restoreAll();
		    	
		    	if (ExecutionFlow.getInvokedManager() != null)
		    		ExecutionFlow.getInvokedManager().deleteBackup();
		    	
		    	deleteTestMethodBackupFiles();
				disableCheckpoint();
				
				if (mcti.exists())
					while (!mcti.delete());
		    }
		});
		
		
		// Gets information about test method
		try {
			String className, classSignature, testClassSignature;
			Path testSrcPath;
			
			
			// Checks if it is not the first run
			if (checkpoint_initial.isActive())
				ExecutionFlow.init(false);
			else
				ExecutionFlow.init(true);
			
			// Gets compiled file path of the test method
			className = thisJoinPoint.getTarget().getClass().getSimpleName();
			classSignature = thisJoinPoint.getSignature().getDeclaringTypeName();
			testClassPath = CollectorExecutionFlow.findBinPath(className, classSignature);
			
			// Gets source file path of the test method
			testClassSignature = InvokedInfo.extractClassSignature(testMethodSignature);
			testClassName = CollectorExecutionFlow.getClassName(testClassSignature);
			testClassPackage = MethodInvokedInfo.extractPackage(testClassSignature);
			testSrcPath = CollectorExecutionFlow.findSrcPath(testClassName, testClassSignature);
			testMethodArgs = thisJoinPoint.getArgs();

			try {
				testMethodInfo = new MethodInvokedInfo.Builder()
					.binPath(testClassPath)
					.methodSignature(testMethodSignature)
					.args(testMethodArgs)
					.srcPath(testSrcPath)
					.build();
			} 
			catch(IllegalArgumentException e) {
				ConsoleOutput.showError("Test method info - "+e.getMessage());
				e.printStackTrace();
			}
			
			testMethodFileManager = new FileManager(
				classSignature,
				testSrcPath,
				MethodInvokedInfo.getCompiledFileDirectory(testClassPath),
				testClassPackage,
				new PreTestMethodFileProcessorFactory(testMethodSignature, testMethodArgs),
				"pre_processing.original"
			);
			
			
			// Checks if it the first execution
			if (testMethodManager == null && !checkpoint.isActive()) {
				testMethodManager = new FilesManager(ProcessorType.PRE_TEST_METHOD, false, true);
				Control.open();
			}

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
				ConsoleOutput.showWarning("On the first run this process can be slow");
				
				// Enables checkpoint
				checkpoint.enable();
				
				testMethodManager.parse(testMethodFileManager).compile(testMethodFileManager);
				
				ConsoleOutput.showInfo("Pre-processing completed");
			}
		} 
		catch(IOException | ClassNotFoundException e) {
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
		ExecutionFlow ef;

		if (finished)
			return;
		
		// Runs a new process of the application. This code block must only be
		// executed once per test file
		if (firstTime) {
			boolean hasError = false;
			List<String> classPath = new ArrayList<>();
			Path testClassRootPath = MethodInvokedInfo.extractClassRootDirectory(testClassPath, testClassPackage);
			String libPath = testClassRootPath.relativize(ExecutionFlow.getLibPath()).toString() + "\\";
			String classSignature = testClassPackage.isEmpty() ? 
					testClassName : testClassPackage + "." + testClassName;
			
			
			classPath.add(".");
			classPath.add(testClassRootPath.relativize(ExecutionFlow.getAppRootPath()).toString());
			classPath.add(libPath + "aspectjrt-1.9.2.jar");
			classPath.add(libPath + "aspectjtools.jar");
			classPath.add(libPath + "junit-4.13.jar");
			classPath.add(libPath + "hamcrest-all-1.3.jar");
			classPath.add(libPath + "..\\classes");
			classPath.add(testClassRootPath.relativize(DependencyManager.getPath()).toString() + "\\*");
					
			if (!checkpoint_initial.isActive()) {
				try {
					checkpoint_initial.enable();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			// Resets methods called by tested invoked
			new File(ExecutionFlow.getAppRootPath().toFile(), "mcti.ef").delete();

			JUnit4Runner.run(testClassRootPath, classPath, classSignature);

			finished = true;
			firstTime = false;
			isRepeatedTest = false;
			lastRepeatedTestSignature = thisJoinPoint.getSignature().toString();
			
			// Restores original test method file and its compiled file
			hasError = restoreTestMethodFiles();
			
			// Checks if there are still tests to be performed
			if (totalTests < 0) {
				totalTests = PreTestMethodFileProcessor.getTotalIgnoredMethods() ;
			}
			else {
				totalTests--;
			}

			if (totalTests == 0) {
				// Restores original method files and its compiled files
				hasError = restoreInvokedFiles();
				ExecutionFlow.getInvokedManager().deleteBackup();
				
				// Resets totalTests
				totalTests = -1;
				
				Control.close();
			}

			testMethodManager.restoreAll();
			
			// Deletes backup files
			deleteTestMethodBackupFiles();
			
			// Disables checkpoint
			disableCheckpoint();
			
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
		ef = new MethodExecutionFlow(methodCollector);
		ef.execute().export();
		// Exports tested methods to a CSV
		ef.setExporter(new TestedInvokedExporter("Testers", 
				new File(ExecutionFlow.getCurrentProjectRoot().toFile(), outputDir)))
			.export();
		
		// If constructor is declared in the same file as the test method and 
		// method, it updates its invocation line according to the modified 
		// test method file 
		updateConstructorCollectorInvocationLines();
		
		ef = new ConstructorExecutionFlow(constructorCollector.values());
		ef.execute().export();
		
		// Exports tested constructors to a CSV
		ef.setExporter(new TestedInvokedExporter("Testers", 
				new File(ExecutionFlow.getCurrentProjectRoot().toFile(), outputDir)))
			.export();
		
		reset();	// Prepares for next test
		success = true;
	}

	/**
	 * Updates constructor invocation line of each constructor collected based
	 * on the collected methods.
	 */
	private void updateConstructorCollectorInvocationLines()
	{
		for (List<CollectorInfo> mc : methodCollector.values()) {
			for (CollectorInfo collector : mc) {
				Path methodPath = collector.getMethodInfo().getSrcPath();
				
				// Checks if method is declared in the same file as the test method
				if (methodPath.equals(collector.getTestMethodInfo().getSrcPath())) {	
					// Updates constructor invocation lines
					for (CollectorInfo cc : constructorCollector.values()) {
						// If constructor is declared in the same file as the 
						// method, it updates its invocation line
						if (cc.getTestMethodInfo().getSrcPath().equals(methodPath)) {
							cc.getConstructorInfo().setInvocationLine(
									InvokedFileProcessor.getMapping()
										.get(methodPath)
										.get(cc.getConstructorInfo().getInvocationLine()));
						}
					}
				}
			}
		}
	}
	
	/**
	 * Deletes backup files related to test methods.
	 */
	private void deleteTestMethodBackupFiles()
	{
		if (ExecutionFlow.getTestMethodManager() != null)
			ExecutionFlow.getTestMethodManager().deleteBackup();
		
		if (testMethodManager != null) {
			testMethodManager.deleteBackup();
			testMethodManager = null;
		}
		
		ExecutionFlow.destroyTestMethodManager();
	}
	
	/**
	 * Disables checkpoint.
	 * 
	 * @return		If an error has occurred
	 */
	private boolean disableCheckpoint()
	{
		if (checkpoint == null)
			return false;
		
		boolean hasError = false;
		
		
		try {
			checkpoint.disable();
		} 
		catch (IOException e) {
			hasError = true;
		}
		
		return hasError;
	}
	
	/**
	 * Restores original test method files.
	 * 
	 * @return		If an error has occurred
	 */
	private boolean restoreTestMethodFiles()
	{
		if (ExecutionFlow.getTestMethodManager() == null)
			return false;
		
		boolean hasError = false;
		
		
		try {
			if (ExecutionFlow.getTestMethodManager().load())
				ExecutionFlow.getTestMethodManager().restoreAll();	
		} 
		catch (ClassNotFoundException e) {
			hasError = true;
			ConsoleOutput.showError("Class FileManager not found");
			e.printStackTrace();
		} 
		catch (IOException e) {
			hasError = true;
			ConsoleOutput.showError("Could not recover the backup file of the test method");
			ConsoleOutput.showError("See more: https://github.com/williamniemiec/"
					+ "ExecutionFlow/wiki/Solu%C3%A7%C3%A3o-de-problemas"
					+ "#could-not-recover-all-backup-files");
			e.printStackTrace();
		}
		
		return hasError;
	}
	
	/**
	 * Restores original invoked files.
	 * 
	 * @return		If an error has occurred
	 */
	private boolean restoreInvokedFiles()
	{
		if (ExecutionFlow.getInvokedManager() == null)
			return false;
		
		boolean hasError = false;
		
		
		try {
			if (ExecutionFlow.getInvokedManager().load())
				ExecutionFlow.getInvokedManager().restoreAll();	
		} 
		catch (ClassNotFoundException e) {
			hasError = true;
			ConsoleOutput.showError("Class FileManager not found");
		 	e.printStackTrace();
		} 
		catch (IOException e) {
			hasError = true;
			ConsoleOutput.showError("Could not recover all backup files for methods");
			ConsoleOutput.showError("See more: https://github.com/williamniemiec/"
					+ "ExecutionFlow/wiki/Solu%C3%A7%C3%A3o-de-problemas"
					+ "#could-not-recover-all-backup-files");
			e.printStackTrace();
		}
		
		return hasError;
	}
}
