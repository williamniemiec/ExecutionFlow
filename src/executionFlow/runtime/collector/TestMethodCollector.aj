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
import executionFlow.info.InvokedContainer;
import executionFlow.info.InvokedInfo;
import executionFlow.io.manager.FileManager;
import executionFlow.io.manager.FilesManager;
import executionFlow.io.manager.ProcessingManager;
import executionFlow.io.preprocessor.PreTestMethodFileProcessor;
import executionFlow.io.processor.ProcessorType;
import executionFlow.io.processor.factory.PreTestMethodFileProcessorFactory;
import executionFlow.user.Session;
import executionFlow.util.Checkpoint;
import executionFlow.util.JUnit4Runner;
import executionFlow.util.logger.LogLevel;
import executionFlow.util.logger.LogView;
import executionFlow.util.logger.Logger;


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
//@SuppressWarnings("unused")
public aspect TestMethodCollector extends RuntimeCollector
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static boolean inTestMethod = true;
	private static boolean finished;
	
	
	private static Checkpoint currentTestMethodCheckpoint =
			new Checkpoint(ExecutionFlow.getAppRootPath(), "Test_Method");
	private static Checkpoint checkpoint_initial = 
			new Checkpoint(Path.of(System.getProperty("user.home")), "initial");
	private static Checkpoint firstRunCheckpoint =
			new Checkpoint(ExecutionFlow.getAppRootPath(), "app_running");
	private static Session session = 
			new Session("session.ef", new File(System.getProperty("user.home")));
	
	
	private static int remainingTests = -1;
	private String lastRepeatedTestSignature;
//	private String testClassName;
//	private String testClassPackage;
//	private Path testClassPath;
	private FilesManager testMethodManager;
	private FileManager testMethodFileManager;
	private boolean isRepeatedTest;
	private static boolean errorProcessingTestMethod;
	private volatile static boolean success;
	
	private static ProcessingManager processingManager;
	
	private Path classPath;
	private Path srcPath;
	
	
	//-------------------------------------------------------------------------
	//		Pointcuts
	//-------------------------------------------------------------------------
	pointcut insideJUnit5RepeatedTest():
		!skipAnnotation() 
		&& execution(@org.junit.jupiter.api.RepeatedTest * *.*(..));
	
	pointcut insideTestMethod():
		!skipAnnotation() 
		&& insideJUnitTest()
		&& !withincode(@org.junit.Test * *.*());
	
	protected pointcut JUnit4Annotation():
		execution(@org.junit.Test * *.*());
	
	protected pointcut JUnit5Annotation():
		execution(@org.junit.jupiter.api.Test * *.*())
		|| execution(@org.junit.jupiter.params.ParameterizedTest * *.*(..))
		|| execution(@org.junit.jupiter.api.RepeatedTest * *.*(..));
	
	
	//-------------------------------------------------------------------------
	//		Join points
	//-------------------------------------------------------------------------	
	before(): insideJUnit5RepeatedTest() {
		isRepeatedTest = true;
	}
	
	private void checkDevelopmentMode() {
		if (!Files.exists(LibraryManager.getLibrary("JUNIT_4"))) {
			Logger.error("Development mode is off even in a development "
					+ "environment. Turn it on in the ExecutionFlow class");
			
			System.exit(-1);
		}
	}
	
	before(): insideTestMethod() {	
		if (errorProcessingTestMethod)
			return;
		
		checkDevelopmentMode();
		checkRepeatedTest(thisJoinPoint);
		
		if (finished)
			return;
		
		reset();
		onShutdown();
		
		testMethodSignature = getSignature(thisJoinPoint);
		
		try {
			findSrcAndBinPath(thisJoinPoint);
			processingManager = new ProcessingManager(!checkpoint_initial.isActive());
			initializeTestMethodInfo(thisJoinPoint);
			initializeFileManager(thisJoinPoint);
			onEachTestMethod();
			onFirstRun();
			initializeLogger();
			cleanLastRun();
		} 
		catch(IOException | ClassNotFoundException | NoClassDefFoundError e) {
			Logger.error(e.getMessage());
			
			System.exit(-1);
		}
		
		dump();
		
		doPreprocessing();
	}

	after(): insideTestMethod() {
		if (errorProcessingTestMethod) {
			errorProcessingTestMethod = false;
			return;
		}
		
		if (finished)
			return;
		
		// Runs a new process of the application. This code block must only be
		// executed once per test file
		if (!inTestMethod) {
			
			try {
				restart(thisJoinPoint);
			}
			catch (IOException | InterruptedException e) {
				Logger.error("Restart - " + e.getMessage());
				System.exit(-1);
			}

			finished = true;
			inTestMethod = true;
			isRepeatedTest = false;
			lastRepeatedTestSignature = thisJoinPoint.getSignature().toString();
			
			updateRemainingTests();
			boolean successfullRestoration = false;
			successfullRestoration = restoreTestMethodFiles();
			
			if (remainingTests == 0) {
				successfullRestoration = restoreInvokedFiles();
				processingManager.deleteInvokedFileManagerBackup();
			}
			
			testMethodManager.restoreAll();
			deleteTestMethodBackupFiles();
			
			if (remainingTests == 0)				
				RemoteControl.close();
			
			disableCheckpoint(currentTestMethodCheckpoint);
			
			// Stops execution if an error occurs
			if (!successfullRestoration) {
				System.exit(-1);
			}
			
			return;
		}
		
		// If the execution of the process of the application has been 
		// completed all test paths have been computed
		if (finished)
			return;
		
		processCollectedInvoked();
		
		reset();	
		success = true;
	}
	
	private void updateRemainingTests() {
		if (remainingTests < 0) {
			remainingTests = PreTestMethodFileProcessor.getTotalTests() - 1;
		}
		else {
			remainingTests--;
		}

		if (remainingTests == 0) {
			remainingTests = -1;
		}
	}

	private void dump() {
		Logger.debug("Test method collector: " + testMethodInfo);
	}
	
	private void checkRepeatedTest(JoinPoint jp) {
		// Prevents repeated tests from being performed more than once
		if (finished && isRepeatedTest && 
				!jp.getSignature().toString().equals(lastRepeatedTestSignature)) {
			isRepeatedTest = false;
		}
		
		if (finished && inTestMethod && !isRepeatedTest) {
			finished = false;
			inTestMethod = true;
		}
	}
	
	private void doPreprocessing() {
		try {
			processTestMethod();
		} 
		catch (IOException e) {
			Logger.error(e.getMessage());
			
			testMethodManager.restoreAll();
			deleteTestMethodBackupFiles();
			disableCheckpoint(currentTestMethodCheckpoint);
			reset();
			
			errorProcessingTestMethod = true;
		}
	}
	
	@Override
	protected void reset() {
		super.reset();
		
		success = false;
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
		for (InvokedContainer cc : constructorCollector.values()) {
			invocationLine = cc.getInvokedInfo().getInvocationLine();
			
			if (!cc.getTestMethodInfo().getSrcPath().equals(testMethodSrcFile) || 
					!mapping.containsKey(invocationLine))
				continue;
			
			cc.getInvokedInfo().setInvocationLine(mapping.get(invocationLine));
		}
		
		// Updates method invocation lines If it is declared in the 
		// same file as the processed test method file
		for (List<InvokedContainer> methodCollectorList : methodCollector.values()) {
			for (InvokedContainer mc : methodCollectorList) {
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
	private void restart(JoinPoint jp) throws IOException, InterruptedException
	{
		if (!checkpoint_initial.isActive()) {
			checkpoint_initial.enable();
		}
		
		// Resets methods called by tested invoked
		File mcti = new File(ExecutionFlow.getAppRootPath().toFile(), "mcti.ef");
		mcti.delete();
		
		JUnit4Runner.run(
				generateClassRootDirectory(), 
				generateClasspaths(), 
				getClassSignature(jp)
		);
		
		waitForJUnit4Runner();
	}
	
	private void waitForJUnit4Runner() throws IOException, InterruptedException {
		while (JUnit4Runner.isRunning()) {
			Thread.sleep(2000);
			
			if (!checkpoint_initial.isActive())
				JUnit4Runner.quit();
		}
	}
	
	/**
	 * Extracts class root directory. <br />
	 * Example: <br />
	 * <li><b>Class path:</b> C:/app/bin/packageName1/packageName2/className.java</li>
	 * <li><b>Class root directory:</b> C:/app/bin</li>
	 * 
	 * @param		classPath Path where compiled file is
	 * @param		classPackage Package of this class
	 * @return		Class root directory
	 */
	private Path generateClassRootDirectory() {
		Path binRootPath = classPath;
		String classPackage = getClassPackage();
		int packageFolders = 0;
		
		if (!(classPackage.isEmpty() || (classPackage == null)))
			packageFolders = classPackage.split("\\.").length;

		binRootPath = binRootPath.getParent();

		for (int i=0; i<packageFolders; i++) {
			binRootPath = binRootPath.getParent();
		}
		
		return binRootPath;
	}
	
	private String getClassPackage() {
		if ((testMethodSignature == null) || testMethodSignature.isEmpty())
			return "";
		
		String[] terms = testMethodSignature.split("\\.");
		StringBuilder classPackage = new StringBuilder();
		
		for (int i=0; i<terms.length-2; i++) {
			classPackage.append(terms[i]);
			classPackage.append(".");
		}
		
		// Removes last dot
		if (classPackage.length() > 0) {
			classPackage.deleteCharAt(classPackage.length()-1);
		}
		
		return classPackage.toString();
	}
	
	private List<Path> generateClasspaths() {
		List<Path> classpaths = LibraryManager.getJavaClassPath();
		classpaths.add(LibraryManager.getLibrary("JUNIT_4"));
		classpaths.add(LibraryManager.getLibrary("HAMCREST"));
		
		return classpaths;
	}
	
	public static void processCollectedInvoked() {
		ExecutionFlow methodExecutionFlow = new MethodExecutionFlow(
				processingManager, 
				methodCollector
		);
		methodExecutionFlow.execute();
		
		ExecutionFlow constructorExecutionFlow = new ConstructorExecutionFlow(
				processingManager, 
				constructorCollector.values()
		);
		constructorExecutionFlow.execute();
	}

	private void onShutdown() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() {
		    	if (success)
		    		return;
		 
	    		if (JUnit4Runner.isRunning()) {
			    	try {
						JUnit4Runner.quit();
					} 
			    	catch (IOException e) {}
	    		}
		    	    				    	
	    		restoreOriginalFiles();
		    	
		    	processingManager.deleteInvokedFileManagerBackup();
		    	deleteTestMethodBackupFiles();
				disableCheckpoint(currentTestMethodCheckpoint);
				disableCheckpoint(checkpoint_initial);
				disableCheckpoint(firstRunCheckpoint);
				
				// WaitRestartEnd
//					File mcti = new File(ExecutionFlow.getAppRootPath().toFile(), "mcti.ef");
//					if (mcti.exists())
//						while (!mcti.delete());
				
				finished = true;
				
				//session.destroy();
		    }
		});
	}
	
	private void restoreOriginalFiles() {
		restoreTestMethodFiles();
    	restoreInvokedFiles();
    	
    	if (testMethodManager != null)
    		testMethodManager.restoreAll();
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
	private void initializeTestMethodInfo(JoinPoint jp) throws IOException {
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
	}
	
	private void initializeFileManager(JoinPoint jp) {
		testMethodFileManager = new FileManager(
			getClassSignature(jp),
			srcPath,
			InvokedInfo.getCompiledFileDirectory(classPath),
			InvokedInfo.extractPackage(getClassSignature(jp)),
			new PreTestMethodFileProcessorFactory(testMethodSignature, getParameterValues(jp)),
			"pretestmethod.bkp"
		);
	}
	
	private void findSrcAndBinPath(JoinPoint jp) throws IOException {
		String classSignature = getClassSignature(jp);
				
		classPath = CollectorUtil.findBinPath(classSignature);
		srcPath = CollectorUtil.findSrcPath(classSignature);
		
		if ((srcPath == null) || (classPath == null)) {
			Logger.warning("The method with the following signature" 
					+ " will be skiped because its source file and / or " 
					+ " binary file cannot be found: " + testMethodSignature);
		}
	}
	
	private String getSignature(JoinPoint jp) {
		return removeReturnTypeFromSignature(jp.getSignature().toString());
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
		
		return classSignature.toString();
	}
	
	private Object[] getParameterValues(JoinPoint jp) {
		return jp.getArgs();
	}
	
	private String removeReturnTypeFromSignature(String signature) {
		return signature.substring(signature.indexOf(' ') + 1);
	}
	
	private void onEachTestMethod() throws IOException, ClassNotFoundException {
		if (runningTestMethod())
			return;
		
		initializeTestMethodManager();
		RemoteControl.open();
	}
	
	private boolean runningTestMethod() {
		return	(testMethodManager != null) 
				|| currentTestMethodCheckpoint.isActive();
	}
	
	private void initializeTestMethodManager() throws ClassNotFoundException, IOException {
		testMethodManager = new FilesManager(ProcessorType.PRE_TEST_METHOD, false, true);
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
		if (firstRunCheckpoint.isActive())
			return;
		
		session.destroy();
		session.save("LOG_LEVEL", LogView.askLogLevel());		
		
		firstRunCheckpoint.enable();
	}
	
	/**
	 * Sets logging level using data stored in the session.
	 * 
	 * @throws		IOException If an error occurred while loading the session
	 */
	private void initializeLogger() throws IOException
	{
		LogLevel logLevel = (LogLevel)session.read("LOG_LEVEL");
		Logger.setLevel(logLevel);
	}
	
	/**
	 * Checks if there are files that were not restored in the last execution.
	 * 
	 * @throws		IOException If the checkpoint is active
	 */
	private void cleanLastRun() throws IOException
	{
		if (currentTestMethodCheckpoint.exists() && !currentTestMethodCheckpoint.isActive()) {
			testMethodManager.deleteBackup();
			currentTestMethodCheckpoint.delete();
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
		inTestMethod = currentTestMethodCheckpoint.exists();
		
		if (!inTestMethod) {
			currentTestMethodCheckpoint.enable();
			
			Logger.info("Pre-processing test method...");
			
			testMethodManager.processFile(testMethodFileManager);
			testMethodManager.compile(testMethodFileManager);
			
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
	
	private boolean disableCheckpoint(Checkpoint checkpoint) {
		if (checkpoint == null)
			return true;
		
		boolean success = true;
		
		try {
			checkpoint.disable();
		} 
		catch (IOException e) {
			success = false;
		}
		
		return success;
	}
	
	/**
	 * Restores original test method files.
	 * 
	 * @return		If an error has occurred
	 */
	private boolean restoreTestMethodFiles()
	{
		boolean success = true;
		
		try {
			processingManager.restoreTestMethodOriginalFiles();
		} 
		catch (ClassNotFoundException e) {
			success = false;
			Logger.error("Class FileManager not found");
			e.printStackTrace();
		} 
		catch (IOException e) {
			success = false;
			Logger.error("Could not recover the backup file of the test method");
			Logger.error("See more: https://github.com/williamniemiec/"
					+ "ExecutionFlow/wiki/Solu%C3%A7%C3%A3o-de-problemas"
					+ "#could-not-recover-all-backup-files");
			e.printStackTrace();
		}
		catch (NullPointerException e) {
			success = false;
		}
		
		return success;
	}
	
	private boolean restoreInvokedFiles() {
		boolean success = true;
		
		try {
			processingManager.restoreInvokedOriginalFiles();
		} 
		catch (ClassNotFoundException e) {
			success = false;
			Logger.error("Class FileManager not found");
		 	e.printStackTrace();
		} 
		catch (IOException e) {
			success = false;
			Logger.error("Could not recover all backup files for methods");
			Logger.error("See more: https://github.com/williamniemiec/"
					+ "ExecutionFlow/wiki/Solu%C3%A7%C3%A3o-de-problemas"
					+ "#could-not-recover-all-backup-files");
			e.printStackTrace();
		}
		
		return success;
	}
}
