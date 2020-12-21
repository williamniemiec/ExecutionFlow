package executionFlow.runtime.collector;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
@SuppressWarnings("unused")
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
		
		collectSourceAndBinaryPaths(thisJoinPoint);
		collectTestMethod(thisJoinPoint);
		
		if ((classPath == null) || (srcPath == null))
			return;

		initializeManagers(thisJoinPoint);
		
		onFirstRun();
		onEachTestMethod();
		
		initializeLogger();
		
		dump();
		
		doPreprocessing();
	}
	
	after(): insideTestMethod() {
		if (errorProcessingTestMethod) {
			errorProcessingTestMethod = false;
			return;
		}
		
		if ((classPath == null) || (srcPath == null))
			return;
		
		if (finished)
			return;
		
		if (inTestMethod) {
			processCollectedInvoked();
			reset();
			
			success = true;			
		}
		else {
			restart(thisJoinPoint);
			afterEachTestMethod(thisJoinPoint);
		}
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	private void checkDevelopmentMode() {
		if (!Files.exists(LibraryManager.getLibrary("JUNIT_4"))) {
			Logger.error("Development mode is off even in a development "
					+ "environment. Turn it on in the ExecutionFlow class");
			
			System.exit(-1);
		}
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
	
	@Override
	protected void reset() {
		super.reset();
		
		success = false;
	}
	
	private void onShutdown() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() {
		    	if (success)
		    		return;
		 
		    	if (Session.hasKeyShared("JUNIT4_RUNNER")) {
					try {
						JUnit4Runner runner = (JUnit4Runner)Session.readShared("JUNIT4_RUNNER");
						runner.quit();
					} 
					catch (IOException e) {
					}
					finally {
						Session.removeShared("JUNIT4_RUNNER");						
					}
		    	}
		    	    				    	
	    		restoreOriginalFiles();
		    	
		    	processingManager.deleteInvokedFileManagerBackup();
		    	deleteTestMethodBackupFiles();
				disableCheckpoint(currentTestMethodCheckpoint);
				disableCheckpoint(checkpoint_initial);
				disableCheckpoint(firstRunCheckpoint);;
				
				finished = true;
		    }
		});
	}
	
	private void restoreOriginalFiles() {
		restoreTestMethodFiles();
    	restoreInvokedFiles();
    	
    	if (testMethodManager != null)
    		testMethodManager.restoreAll();
	}
	
	private boolean restoreTestMethodFiles() {
		if (processingManager == null)
			return true;
		
		boolean success = true;
		
		try {
			processingManager.restoreTestMethodOriginalFiles();
		} 
		catch (ClassNotFoundException e) {
			success = false;
			Logger.error("Class FileManager not found");
		} 
		catch (IOException e) {
			success = false;
			Logger.error(e.getMessage());
			Logger.error("Could not recover the backup file of the test method");
			Logger.error("See more: https://github.com/williamniemiec/"
					+ "ExecutionFlow/wiki/Solu%C3%A7%C3%A3o-de-problemas"
					+ "#could-not-recover-all-backup-files");
		}
		catch (NullPointerException e) {
			success = false;
		}
		
		return success;
	}
	
	private boolean restoreInvokedFiles() {
		if (processingManager == null)
			return true;
		
		boolean success = true;
		
		try {
			processingManager.restoreInvokedOriginalFiles();
		} 
		catch (ClassNotFoundException e) {
			success = false;
			Logger.error("Class FileManager not found");
		} 
		catch (IOException e) {
			success = false;
			Logger.error(e.getMessage());
			Logger.error("Could not recover all backup files for methods");
			Logger.error("See more: https://github.com/williamniemiec/"
					+ "ExecutionFlow/wiki/Solu%C3%A7%C3%A3o-de-problemas"
					+ "#could-not-recover-all-backup-files");
		}
		
		return success;
	}
	
	private void deleteTestMethodBackupFiles() {
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
	
	private void initializeManagers(JoinPoint thisJoinPoint) {
		try {
			processingManager = new ProcessingManager(!checkpoint_initial.isEnabled());
			initializeFileManager(thisJoinPoint);
		}
		catch(IOException | ClassNotFoundException | NoClassDefFoundError e) {
			Logger.error(e.getMessage());
			
			System.exit(-1);
		}
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
	}
	
	private void onFirstRun() {
		try {
			LogLevel level;
			
			if (!firstRunCheckpoint.isEnabled()) {
				level = askUserForLogLevel();
		
				firstRunCheckpoint.enable();
			}
			else {
				 level = loadLogLevel();		
			}
			
			Logger.setLevel(level);
		}
		catch(IOException | NoClassDefFoundError e) {
			Logger.error(e.getMessage());
			
			System.exit(-1);
		}
	}
	
	private LogLevel askUserForLogLevel() throws IOException {
		LogLevel level = LogView.askLogLevel();
		
		session.destroy();
		session.save("LOG_LEVEL", level); 
		
		return level;
	}
	
	private LogLevel loadLogLevel() throws IOException {
		LogLevel level;
		
		try {
			level = (LogLevel)session.read("LOG_LEVEL");
		} 
		catch (IOException e) {
			Logger.error("Corrupted session");
			
			level = LogView.askLogLevel();
			session.save("LOG_LEVEL", level);
		}
		
		return level;
	}
	
	private void onEachTestMethod() {
		if (runningTestMethod())
			return;
		
		try {
			cleanLastRun();
			initializeTestMethodManager();
			RemoteControl.open();
		}
		catch(IOException | ClassNotFoundException | NoClassDefFoundError e) {
			Logger.error(e.getMessage());
			
			System.exit(-1);
		}
	}
	
	private boolean runningTestMethod() {
		return	(testMethodManager != null) 
				|| currentTestMethodCheckpoint.isEnabled();
	}
	
	private void cleanLastRun() throws IOException {
		if (hasTempFilesFromLastRun()) {
			testMethodManager.deleteBackup();
			currentTestMethodCheckpoint.delete();
		}
	}
	
	private boolean hasTempFilesFromLastRun() {
		return	currentTestMethodCheckpoint.exists() 
				&& !currentTestMethodCheckpoint.isEnabled();
	}
	
	private void initializeTestMethodManager() throws ClassNotFoundException, IOException {
		testMethodManager = new FilesManager(
				ProcessorType.PRE_TEST_METHOD, 
				false, 
				true
		);
	}
	
	private void initializeLogger() {
		LogLevel logLevel;
		
		try {
			logLevel = (LogLevel)session.read("LOG_LEVEL");
		} 
		catch (IOException e) {
			Logger.error("Corrupted session");
			
			logLevel = LogView.askLogLevel();
			
			try {
				session.save("LOG_LEVEL", logLevel);
			} 
			catch (IOException e1) {
				Logger.error(e1.getMessage());
				
				session.destroy();
			}
		}
		
		Logger.setLevel(logLevel);
	}
	
	private void dump() {
		Logger.debug("Test method collector: " + testMethodInfo);
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
	
	private void processTestMethod() throws IOException	{
		inTestMethod = currentTestMethodCheckpoint.exists();
		
		if (!inTestMethod) {
			currentTestMethodCheckpoint.enable();
			
			Logger.info("Pre-processing test method...");
			
			testMethodManager.processFile(testMethodFileManager);
			testMethodManager.compile(testMethodFileManager);
			
			Logger.info("Pre-processing completed");
		}
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
	
	/**
	 * Restarts the application by starting it in CLI mode.
	 * 
	 * @throws		IOException If an error occurs when creating the process 
	 * containing {@link JUnit4Runner}
	 * @throws		InterruptedException If the process containing 
	 * {@link JUnit4Runner} is interrupted while it is waiting  
	 */
	private void restart(JoinPoint jp) {
		try {
			if (!checkpoint_initial.isEnabled()) {
				checkpoint_initial.enable();
			}
			
			// Resets methods called by tested invoked
			File mcti = new File(ExecutionFlow.getAppRootPath().toFile(), "mcti.ef");
			mcti.delete();

			runJUnitRunner(jp);
			waitForJUnit4Runner();
		}
		catch (IOException | InterruptedException e) {
			Logger.error("Restart - " + e.getMessage());
			System.exit(-1);
		}
	}
	
	private void runJUnitRunner(JoinPoint jp) throws IOException, InterruptedException {
		JUnit4Runner junit4Runner = new JUnit4Runner.Builder()
				.workingDirectory(generateClassRootDirectory())
				.classPath(generateClasspaths())
				.classSignature(getClassSignature(jp))
				.build();
		
		Session.saveShared("JUNIT4_RUNNER", junit4Runner);
		
		junit4Runner.run();
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
	
	private void waitForJUnit4Runner() throws IOException, InterruptedException {
		JUnit4Runner junit4Runner = (JUnit4Runner) Session.readShared("JUNIT4_RUNNER");
		
		while (junit4Runner.isRunning()) {
			Thread.sleep(2000);
			
			if (!checkpoint_initial.isEnabled())
				junit4Runner.quit();
		}
		
		Session.removeShared("JUNIT4_RUNNER");
	}
	
	private void afterEachTestMethod(JoinPoint jp) {
		finished = true;
		inTestMethod = true;
		isRepeatedTest = false;
		lastRepeatedTestSignature = jp.getSignature().toString();
		
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
		
		if (!successfullRestoration) {
			System.exit(-1);
		}
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
	
	/**
	 * Updates the invocation line of constructor and method collector based on
	 * a mapping.
	 * 
	 * @param		mapping Mapping that will be used as base for the update
	 * @param		testMethodSrcFile Test method source file
	 */
	public static void updateCollectorInvocationLines(Map<Integer, Integer> mapping, 
			Path testMethodSrcFile)	{
		updateConstructorInvocationLines(mapping, testMethodSrcFile);
		updateMethodInvocationLines(mapping, testMethodSrcFile);
	}
	
	private static void updateConstructorInvocationLines(Map<Integer, Integer> mapping, 
			Path testMethodSrcFile) {
		updateInvokedInvocationLines(
				mapping, 
				testMethodSrcFile, 
				constructorCollector.values()
		);
	}
	
	private static void updateMethodInvocationLines(Map<Integer, Integer> mapping, 
			Path testMethodSrcFile) {
		// Updates method invocation lines If it is declared in the 
		// same file as the processed test method file
		for (List<InvokedContainer> methodCollectorList : methodCollector.values()) {
			updateInvokedInvocationLines(
					mapping, 
					testMethodSrcFile,
					methodCollectorList
			);
		}
	}
	
	private static void updateInvokedInvocationLines(Map<Integer, Integer> mapping, 
			Path testMethodSrcFile, Collection<InvokedContainer> collector) {
		for (InvokedContainer cc : collector) {
			int invocationLine = cc.getInvokedInfo().getInvocationLine();
			
			if (!cc.getTestMethodInfo().getSrcPath().equals(testMethodSrcFile) || 
					!mapping.containsKey(invocationLine))
				continue;
			
			cc.getInvokedInfo().setInvocationLine(mapping.get(invocationLine));
		}
	}
}
