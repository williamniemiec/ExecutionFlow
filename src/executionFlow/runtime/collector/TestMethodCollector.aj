package executionFlow.runtime.collector;

import java.io.File;
import java.io.IOException;
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
import executionFlow.exporter.TestedInvokedExporter;
import executionFlow.info.CollectorInfo;
import executionFlow.info.InvokedInfo;
import executionFlow.info.MethodInvokedInfo;
import executionFlow.io.FileManager;
import executionFlow.io.FilesManager;
import executionFlow.io.ProcessorType;
import executionFlow.io.processor.PreTestMethodFileProcessor;
import executionFlow.io.processor.factory.PreTestMethodFileProcessorFactory;
import executionFlow.user.Session;
import executionFlow.util.Checkpoint;
import executionFlow.util.Logging;
import executionFlow.util.Logging.Level;
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
 * @version		5.2.0
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
			ExecutionFlow.init(!checkpoint_initial.isActive());
			setTestMethodInfo(thisJoinPoint);
			onEachTestMethod();
			onFirstRun();
			setLogLevel();
			clean();
			processTestMethod();
		} 
		catch(IOException | ClassNotFoundException | NoClassDefFoundError e) {
			Logging.showError(e.getMessage());
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
		if (!inTestMethod) {
			boolean hasError = false;
			
			try {
				restart();
			}
			catch (IOException | InterruptedException e) {
				Logging.showError("Restart - " + e.getMessage());
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
			invocationLine = cc.getConstructorInfo().getInvocationLine();
			
			if (!cc.getTestMethodInfo().getSrcPath().equals(testMethodSrcFile) || 
					!mapping.containsKey(invocationLine))
				continue;
			
			cc.getConstructorInfo().setInvocationLine(mapping.get(invocationLine));
		}
		
		// Updates method invocation lines If it is declared in the 
		// same file as the processed test method file
		for (List<CollectorInfo> methodCollectorList : methodCollector.values()) {
			for (CollectorInfo mc : methodCollectorList) {
				invocationLine = mc.getMethodInfo().getInvocationLine();
				
				if (!mc.getTestMethodInfo().getSrcPath().equals(testMethodSrcFile) || 
						!mapping.containsKey(invocationLine))
					continue;
				
				mc.getMethodInfo().setInvocationLine(mapping.get(invocationLine));
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
		Path argumentFile;
		Path testClassRootPath = MethodInvokedInfo.extractClassRootDirectory(testClassPath, testClassPackage);
		String classSignature = testClassPackage.isEmpty() ? 
				testClassName : testClassPackage + "." + testClassName;


		LibraryManager.addLibrary(testClassRootPath);
		LibraryManager.addLibrary(testClassRootPath.resolve("..\\classes").normalize());

		argumentFile = LibraryManager.getArgumentFile();
	
		if (!checkpoint_initial.isActive()) {
			checkpoint_initial.enable();
		}
		
		// Resets methods called by tested invoked
		mcti.delete();
		JUnit4Runner.run(testClassRootPath, argumentFile, classSignature);
		
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
			ExecutionFlow.getInvokedManager().deleteBackup();
			
			// Resets totalTests
			totalTests = -1;
			RemoteControl.close();
		}
		
		return hasError;
	}
	
	public static void processCollectedInvoked()
	{
		ExecutionFlow ef;
		
		
		// Gets test paths of the collected methods and export them
		ef = new MethodExecutionFlow(methodCollector);
		ef.execute().export();
		// Exports tested methods to a CSV
		ef.setExporter(new TestedInvokedExporter("Testers", 
				new File(ExecutionFlow.getCurrentProjectRoot().toFile(), outputDir)))
			.export();
		
		ef = new ConstructorExecutionFlow(constructorCollector.values());
		ef.execute().export();
		
		// Exports tested constructors to a CSV
		ef.setExporter(new TestedInvokedExporter("Testers", 
				new File(ExecutionFlow.getCurrentProjectRoot().toFile(), outputDir)))
			.export();
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
		    		Session session = new Session("session", ExecutionFlow.getAppRootPath().toFile());
		    		
		    		
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
					disableCheckpoint(checkpoint);
					disableCheckpoint(checkpoint_initial);
					disableCheckpoint(checkpoint_appRunning);
					
					if (mcti.exists())
						while (!mcti.delete());
					
					session.destroy();
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
//		className = jp.getTarget().getClass().getSimpleName();
		classSignature = jp.getSignature().getDeclaringTypeName();
		testClassPath = CollectorExecutionFlow.findBinPath(classSignature);
		
		// Gets source file path of the test method
		testClassSignature = InvokedInfo.extractClassSignature(testMethodSignature);
		testClassName = CollectorExecutionFlow.getClassName(testClassSignature);
		testClassPackage = MethodInvokedInfo.extractPackage(testClassSignature);
		testSrcPath = CollectorExecutionFlow.findSrcPath(testClassSignature);
		testMethodArgs = jp.getArgs();

		try {
			testMethodInfo = new MethodInvokedInfo.Builder()
				.binPath(testClassPath)
				.methodSignature(testMethodSignature)
				.args(testMethodArgs)
				.srcPath(testSrcPath)
				.build();
		} 
		catch(IllegalArgumentException e) {
			Logging.showError("Test method info - "+e.getMessage());
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
		
		Logging.showDebug("Test method collector: "+testMethodInfo);
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
		
		Session session = new Session("session", ExecutionFlow.getAppRootPath().toFile());
		Logging.Level logLevel = askLog();
		
		
		checkpoint_appRunning.enable();
		
		session.save("LOG_LEVEL", logLevel);
	}
	
	/**
	 * Sets logging level using data stored in the session.
	 * 
	 * @throws		IOException If an error occurred while loading the session
	 */
	private void setLogLevel() throws IOException
	{
		Session session = new Session("session", ExecutionFlow.getAppRootPath().toFile());
		Logging.Level logLevel = (Logging.Level)session.read("LOG_LEVEL");
		
		
		Logging.setLevel(logLevel);
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
		
		Logging.showDebug("Test method collector: "+testMethodInfo);
		
		if (!inTestMethod) {
			Logging.showInfo("Pre-processing test method...");
			
			checkpoint.enable();
			
			testMethodManager.parse(testMethodFileManager).compile(testMethodFileManager);
			
			Logging.showInfo("Pre-processing completed");
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
		if (ExecutionFlow.getTestMethodManager() == null)
			return false;
		
		boolean hasError = false;
		
		
		try {
			if (ExecutionFlow.getTestMethodManager().load())
				ExecutionFlow.getTestMethodManager().restoreAll();	
		} 
		catch (ClassNotFoundException e) {
			hasError = true;
			Logging.showError("Class FileManager not found");
			e.printStackTrace();
		} 
		catch (IOException e) {
			hasError = true;
			Logging.showError("Could not recover the backup file of the test method");
			Logging.showError("See more: https://github.com/williamniemiec/"
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
			Logging.showError("Class FileManager not found");
		 	e.printStackTrace();
		} 
		catch (IOException e) {
			hasError = true;
			Logging.showError("Could not recover all backup files for methods");
			Logging.showError("See more: https://github.com/williamniemiec/"
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
		Logging.Level logLevel;
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
				logLevel = Logging.Level.OFF;
				break;
			case 1:
				logLevel = Logging.Level.ERROR;
				break;
			case 2:
				logLevel = Logging.Level.WARNING;
				break;
			case 4:
				logLevel = Logging.Level.DEBUG;
				break;
			case 3:
			default:
				logLevel = Logging.Level.INFO;
		}
		
		return logLevel;
	}
}
