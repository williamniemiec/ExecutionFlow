package executionFlow.runtime.collector;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.aspectj.lang.JoinPoint;

import executionFlow.ConstructorExecutionFlow;
import executionFlow.ExecutionFlow;
import executionFlow.LibraryManager;
import executionFlow.MethodExecutionFlow;
import executionFlow.RemoteControl;
import executionFlow.info.CollectorInfo;
import executionFlow.info.InvokedInfo;
import executionFlow.io.FileManager;
import executionFlow.io.FilesManager;
import executionFlow.io.ProcessingManager;
import executionFlow.io.ProcessorType;
import executionFlow.io.processor.PreTestMethodFileProcessor;
import executionFlow.io.processor.factory.PreTestMethodFileProcessorFactory;
import executionFlow.user.Session;
import executionFlow.util.Checkpoint;
import executionFlow.util.JUnit4Runner;
import executionFlow.util.Logger;
import executionFlow.util.Logger.Level;


/**
 * Run in each test method
 * 
 * @apiNote		Ignores methods with {@link executionFlow.runtime.SkipInvoked}
 * annotation, methods with {@link executionFlow.runtime._SkipInvoked} and all
 * methods from classes with {@link executionFlow.runtime.SkipCollection} 
 * annotation
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.3
 * @since		1.0
 */
public aspect TestMethodCollector extends RuntimeCollector
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static boolean inTestMethod = true;
	private static boolean finished;
	private static Checkpoint checkpoint =
			new Checkpoint(ExecutionFlow.getAppRootPath(), "Test_Method");
	private static Checkpoint checkpoint_initial = 
			new Checkpoint(Path.of(System.getProperty("user.home")), "initial");
	private static Checkpoint checkpoint_appRunning =
			new Checkpoint(ExecutionFlow.getAppRootPath(), "app_running");
	private static Session session = 
			new Session("session.ef", new File(System.getProperty("user.home")));
	private static int totalTests = -1;
	private String lastRepeatedTestSignature;
	private String testClassName;
	private String testClassPackage;
	private Path testClassPath;
	private FilesManager testMethodManager;
	private FileManager testMethodFileManager;
	private boolean isRepeatedTest;
	private static boolean skipTestMethod;
	private volatile static boolean success;
	
	private static ProcessingManager processingManager;
	
	
	//-------------------------------------------------------------------------
	//		Pointcuts
	//-------------------------------------------------------------------------
	/**
	 * Intercepts JUnit 4 test methods.
	 */
	pointcut junit4():
		!skipAnnotation() &&
		!junit4_internal() && 
		execution(@org.junit.Test * *.*());
	
	/**
	 * Intercepts JUnit 4 test methods.
	 */
	pointcut junit5():
		!skipAnnotation() &&
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
		if (skipTestMethod) {
			return;
		}
		
		if (!Files.exists(LibraryManager.getLibrary("JUNIT_4"))) {
			Logger.error("Development mode is off even in a development environment. "
					+ "Turn it on in the ExecutionFlow class");
			System.exit(-1);
		}
		
		// Prevents repeated tests from being performed more than once
		if (finished && isRepeatedTest && 
				!thisJoinPoint.getSignature().toString().equals(lastRepeatedTestSignature)) {
			isRepeatedTest = false;
		}
		
		// If it is in another test method and it it is not a repeated test 
		// then should run the application
		if (finished && inTestMethod && !isRepeatedTest) {
			finished = false;
			inTestMethod = true;
		}
		
		if (finished)
			return;
		
		reset();
		success = false;
		
		onShutdown();

		try {
//			ExecutionFlow.init(!checkpoint_initial.isActive());
			processingManager = new ProcessingManager(!checkpoint_initial.isActive());
			setTestMethodInfo(thisJoinPoint);
			onEachTestMethod();
			onFirstRun();
			setLogLevel();
			clean();
		} 
		catch(IOException | ClassNotFoundException | NoClassDefFoundError e) {
			Logger.error(e.getMessage());
			e.printStackTrace();
			
			System.exit(-1);	// Stops execution if a problem occurs
		}
		
		try {
			processTestMethod();
		} 
		catch (IOException e) {
			Logger.error(e.getMessage());
			
			testMethodManager.restoreAll();
			deleteTestMethodBackupFiles();
			disableCheckpoint(checkpoint);
			reset();
			
			skipTestMethod = true;
		}
	}
	
	/**
	 * Executed after the end of a test method.
	 */
	after(): testMethodCollector() 
	{
		if (skipTestMethod) {
			skipTestMethod = false;
			return;
		}
		
		if (finished)
			return;
		
		// Runs a new process of the application. This code block must only be
		// executed once per test file
		if (!inTestMethod) {
			boolean hasError = false;
			
			try {
				restart();
			}
			catch (IOException | InterruptedException e) {
				Logger.error("Restart - " + e.getMessage());
				System.exit(-1);
			}

			finished = true;
			inTestMethod = true;
			isRepeatedTest = false;
			lastRepeatedTestSignature = thisJoinPoint.getSignature().toString();
			
			hasError = restoreTestMethodFiles();
			hasError = onExitTestMethod();

			testMethodManager.restoreAll();
			
			deleteTestMethodBackupFiles();
			disableCheckpoint(checkpoint);
			
			// Stops execution if an error occurs
			if (hasError) {
				System.exit(-1);
			}
			
			return;
		}
		
		// If the execution of the process of the application has been 
		// completed all test paths have been computed
		if (finished)
			return;
		
		processCollectedInvoked();
		
		// Prepares for next test
		reset();	
		success = true;
	}
	
	/**
	 * Updates the invocation line of constructor and method collector based on
	 * a mapping.
	 * 
	 * @param		mapping Mapping that will be used as base for the update
	 * @param		testMethodSrcFile Test method source file
	 */
	public static void updateCollectorInvocationLines(Map<Integer, Integer> mapping, Path testMethodSrcFile)
	{
		int invocationLine;

		
		// Updates constructor invocation lines If it is declared in the 
		// same file as the processed test method file
		for (CollectorInfo cc : constructorCollector.values()) {
			invocationLine = cc.getInvokedInfo().getInvocationLine();
			
			if (!cc.getTestMethodInfo().getSrcPath().equals(testMethodSrcFile) || 
					!mapping.containsKey(invocationLine))
				continue;
			
			cc.getInvokedInfo().setInvocationLine(mapping.get(invocationLine));
		}
		
		// Updates method invocation lines If it is declared in the 
		// same file as the processed test method file
		for (List<CollectorInfo> methodCollectorList : methodCollector.values()) {
			for (CollectorInfo mc : methodCollectorList) {
				invocationLine = mc.getInvokedInfo().getInvocationLine();
				
				if (!mc.getTestMethodInfo().getSrcPath().equals(testMethodSrcFile) || 
						!mapping.containsKey(invocationLine))
					continue;
				
				mc.getInvokedInfo().setInvocationLine(mapping.get(invocationLine));
			}
		}
	}
	
	/**
	 * Restarts the application by starting it in CLI mode.
	 * 
	 * @throws		IOException If an error occurs when creating the process 
	 * containing {@link JUnit4Runner}
	 * @throws		InterruptedException If the process containing 
	 * {@link JUnit4Runner} is interrupted while it is waiting  
	 */
	private void restart() throws IOException, InterruptedException
	{
		File mcti = new File(ExecutionFlow.getAppRootPath().toFile(), "mcti.ef");
		Path libJUnit4 = LibraryManager.getLibrary("JUNIT_4");
		Path libHamcrest = LibraryManager.getLibrary("HAMCREST");
		Path testClassRootPath = InvokedInfo.extractClassRootDirectory(testClassPath, testClassPackage);
		
		String classSignature = testClassPackage.isEmpty() ? 
				testClassName : testClassPackage + "." + testClassName;
		String classPath = System.getProperty("java.class.path") + ";" + libJUnit4 + ";" + libHamcrest;


		if (!checkpoint_initial.isActive()) {
			checkpoint_initial.enable();
		}
		
		// Resets methods called by tested invoked
		mcti.delete();
		
		JUnit4Runner.run(testClassRootPath, classPath, classSignature);
		
		while (JUnit4Runner.isRunning()) {
			Thread.sleep(2000);
			
			if (!checkpoint_initial.isActive())
				JUnit4Runner.quit();
		}
	}
	
	/**
	 * Defines what to do when leaving a test method.
	 * 
	 * @return		True if an error has been occurred while restoring invoked 
	 * files
	 */
	private boolean onExitTestMethod()
	{
		boolean hasError = false;
		
		
		if (totalTests < 0) {
			totalTests = PreTestMethodFileProcessor.getTotalIgnoredMethods() ;
		}
		else {
			totalTests--;
		}

		if (totalTests == 0) {
			// Restores original method files and its compiled files
			hasError = restoreInvokedFiles();
			processingManager.deleteInvokedFileManagerBackup();
			
			// Resets totalTests
			totalTests = -1;
			RemoteControl.close();
		}
		
		return hasError;
	}
	
	public static void processCollectedInvoked()
	{
		ExecutionFlow methodExecutionFlow = 
				new MethodExecutionFlow(processingManager, methodCollector);
//		ExecutionFlow constructorExecutionFlow = 
//				new ConstructorExecutionFlow(processingManager, constructorCollector.values());

		methodExecutionFlow.execute();
//		constructorExecutionFlow.execute();
	}

	/**
	 * Defines what to do when the application is closed.
	 */
	private void onShutdown()
	{
		try {
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
			    	
			    	processingManager.deleteInvokedFileManagerBackup();
			    	
			    	deleteTestMethodBackupFiles();
					disableCheckpoint(checkpoint);
					disableCheckpoint(checkpoint_initial);
					disableCheckpoint(checkpoint_appRunning);
					
					if (mcti.exists())
						while (!mcti.delete());
					
					finished = true;
					
					//session.destroy();
			    }
			});
		}
		catch (IllegalStateException e) {}
	}
	
	/**
	 * Sets test method info, which are:
	 * <ul>
	 * 	<li>Signature</li>
	 * 	<li>Binary path</li>
	 * 	<li>Source path</li>
	 * 	<li>Class name</li>
	 * 	<li>Class package</li>
	 * 	<li>Test method args</li>
	 * 	<li>{@link executionFlow.info.MethodInvokedInfo testMethodInfo}</li>
	 * 	<li>{@link executionFlow.io.FileManager.FileManager testMethodFileManager}</li>
	 * </ul>
	 * 
	 * @param		jp Join point
	 * 
	 * @throws		IOException If an error occurs while searching for test 
	 * method source and binary file
	 */
	@SuppressWarnings("unused")
	private void setTestMethodInfo(JoinPoint jp) throws IOException
	{
		String classSignature, testClassSignature;
		Path testSrcPath;
		
		
		testMethodSignature = CollectorExecutionFlow.extractMethodSignature(jp.getSignature().toString());
		classSignature = jp.getSignature().getDeclaringTypeName();
		testClassPath = CollectorExecutionFlow.findBinPath(classSignature);
		
		// Gets source file path of the test method
		testClassSignature = InvokedInfo.extractClassSignature(testMethodSignature);
		testClassName = CollectorExecutionFlow.getClassName(testClassSignature);
		testClassPackage = InvokedInfo.extractPackage(testClassSignature);
		testSrcPath = CollectorExecutionFlow.findSrcPath(testClassSignature);
		testMethodArgs = jp.getArgs();

		try {
			testMethodInfo = new InvokedInfo.Builder()
				.binPath(testClassPath)
				.invokedSignature(testMethodSignature)
				.args(testMethodArgs)
				.srcPath(testSrcPath)
				.build();
		} 
		catch(IllegalArgumentException e) {
			Logger.error("Test method info - "+e.getMessage());
			e.printStackTrace();
		}
		
		testMethodFileManager = new FileManager(
			classSignature,
			testSrcPath,
			InvokedInfo.getCompiledFileDirectory(testClassPath),
			testClassPackage,
			new PreTestMethodFileProcessorFactory(testMethodSignature, testMethodArgs),
			"pre_processing.original"
		);
		
		Logger.debug("Test method collector: "+testMethodInfo);
	}
	
	/**
	 * Performs actions for each test method performed.
	 * 
	 * @throws		IOException If an error occurs while restoring backup files
	 * @throws		ClassNotFoundException If class {@link FileManager} is not
	 * found 
	 */
	private void onEachTestMethod() throws IOException, ClassNotFoundException
	{
		if (!((testMethodManager == null) && !checkpoint.isActive()))
			return;
		
		testMethodManager = new FilesManager(ProcessorType.PRE_TEST_METHOD, false, true);
		RemoteControl.open();
	}
	
	/**
	 * Performs actions in the first run of the application. Amongst them:
	 * <ul>
	 * 	<li>Creates session</li>
	 * 	<li>Displays remote control</li>
	 * 	<li>Asks the user logging level</li>
	 * </ul>
	 * 
	 * @throws		IOException If an error occurred while storing the session
	 * or if checkpoint file cannot be created
	 * 
	 */
	private void onFirstRun() throws IOException
	{
		if (checkpoint_appRunning.isActive())
			return;
		
		Logger.Level logLevel = askLog();
		
		
		checkpoint_appRunning.enable();
		
		session.destroy(); // Removes last session
		session.save("LOG_LEVEL", logLevel);
	}
	
	/**
	 * Sets logging level using data stored in the session.
	 * 
	 * @throws		IOException If an error occurred while loading the session
	 */
	private void setLogLevel() throws IOException
	{
		Logger.Level logLevel = (Logger.Level)session.read("LOG_LEVEL");
		
		
		Logger.setLevel(logLevel);
	}
	
	/**
	 * Checks if there are files that were not restored in the last execution.
	 * 
	 * @throws		IOException If the checkpoint is active
	 */
	private void clean() throws IOException
	{
		if (checkpoint.exists() && !checkpoint.isActive()) {
			testMethodManager.deleteBackup();
			checkpoint.delete();
		}
	}
	
	/**
	 * Performs pre-processing of the file containing the test method.
	 * 
	 * @throws		IOException If an error occurs while processing test method
	 * or if checkpoint file cannot be created
	 */
	private void processTestMethod() throws IOException
	{
		inTestMethod = checkpoint.exists();
		
		Logger.debug("Test method collector: "+testMethodInfo);
		
		if (!inTestMethod) {
			Logger.info("Pre-processing test method...");
			
			checkpoint.enable();
			
			testMethodManager.parse(testMethodFileManager).compile(testMethodFileManager);
			
			Logger.info("Pre-processing completed");
		}
	}
	
	/**
	 * Deletes backup files related to test methods.
	 */
	private void deleteTestMethodBackupFiles()
	{
		if (processingManager.isTestMethodManagerInitialized())
			processingManager.deleteTestMethodFileManagerBackup();
		
		if (testMethodManager != null) {
			testMethodManager.deleteBackup();
			testMethodManager = null;
		}
		
		processingManager.destroyTestMethodManager();
	}
	
	/**
	 * Disables a checkpoint.
	 * 
	 * @return		If an error has occurred
	 */
	private boolean disableCheckpoint(Checkpoint checkpoint)
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
		if (!processingManager.isTestMethodManagerInitialized())
			return false;
		
		boolean hasError = false;
		
		
		try {
			processingManager.restoreTestMethodOriginalFiles();
		} 
		catch (ClassNotFoundException e) {
			hasError = true;
			Logger.error("Class FileManager not found");
			e.printStackTrace();
		} 
		catch (IOException e) {
			hasError = true;
			Logger.error("Could not recover the backup file of the test method");
			Logger.error("See more: https://github.com/williamniemiec/"
					+ "ExecutionFlow/wiki/Solu%C3%A7%C3%A3o-de-problemas"
					+ "#could-not-recover-all-backup-files");
			e.printStackTrace();
		}
		catch (NullPointerException e) {
			hasError = true;
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
		if (!processingManager.isInvokedManagerInitialized())
			return false;
		
		boolean hasError = false;
		
		
		try {
			processingManager.restoreInvokedOriginalFiles();
		} 
		catch (ClassNotFoundException e) {
			hasError = true;
			Logger.error("Class FileManager not found");
		 	e.printStackTrace();
		} 
		catch (IOException e) {
			hasError = true;
			Logger.error("Could not recover all backup files for methods");
			Logger.error("See more: https://github.com/williamniemiec/"
					+ "ExecutionFlow/wiki/Solu%C3%A7%C3%A3o-de-problemas"
					+ "#could-not-recover-all-backup-files");
			e.printStackTrace();
		}
		
		return hasError;
	}
	
	/**
	 * Asks the user what level of logging will be used.
	 * 
	 * @return		Selected log level
	 */
	private Level askLog()
	{
		Logger.Level logLevel;
		String[] options = {
				"<html><body><div align='center'>None<br>(not recommended \u274C)</div></body></html>",
				"Error", 
				"Warning",
				"<html><body><div align='center'>Info<br>(recommended \u2714)</div></body></html>", 
				"Debug"
		};
		
		int response = JOptionPane.showOptionDialog(
				null, "Choose log level", "Log option", 
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, 
				null, options, options[3]
		);
		
		
		switch (response) {
			case 0:
				logLevel = Logger.Level.OFF;
				break;
			case 1:
				logLevel = Logger.Level.ERROR;
				break;
			case 2:
				logLevel = Logger.Level.WARNING;
				break;
			case 4:
				logLevel = Logger.Level.DEBUG;
				break;
			case 3:
			default:
				logLevel = Logger.Level.INFO;
		}
		
		return logLevel;
	}
}
