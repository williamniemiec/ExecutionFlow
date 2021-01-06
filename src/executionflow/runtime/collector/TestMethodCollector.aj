package executionflow.runtime.collector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aspectj.lang.JoinPoint;

import executionflow.ConstructorExecutionFlow;
import executionflow.ExecutionFlow;
import executionflow.MethodExecutionFlow;
import executionflow.info.InvokedContainer;
import executionflow.info.InvokedInfo;
import executionflow.io.manager.FileManager;
import executionflow.io.manager.FilesManager;
import executionflow.io.manager.InvokedManager;
import executionflow.io.processor.ProcessorType;
import executionflow.io.processor.factory.PreTestMethodFileProcessorFactory;
import executionflow.io.processor.fileprocessor.PreTestMethodFileProcessor;
import executionflow.lib.LibraryManager;
import executionflow.user.RemoteControl;
import executionflow.user.Session;
import executionflow.util.Checkpoint;
import executionflow.util.JUnit4Runner;
import executionflow.util.logger.LogLevel;
import executionflow.util.logger.LogView;
import executionflow.util.logger.Logger;

/**
 * Run in each test method
 * 
 * @apiNote		Ignores methods and constructors with {@link executionflow
 * .runtime.SkipInvoked} annotation and all methods from classes with 
 * {@link executionflow.runtime.SkipCollection} annotation.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		6.0.3
 * @since		1.0
 */
@SuppressWarnings("unused")
public aspect TestMethodCollector extends RuntimeCollector {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static boolean inTestMethod;
	private static boolean finished;
	private static Checkpoint currentTestMethodCheckpoint;
	private static Checkpoint insideJUnitRunnerCheckpoint;
	private static Checkpoint firstRunCheckpoint;
	private static Session session;
	private static int remainingTests;
	private static boolean errorProcessingTestMethod;
	private static volatile boolean success;
	private static InvokedManager processingManager;
	private boolean isRepeatedTest;
	private String lastRepeatedTestSignature;
	private Path classPath;
	private Path srcPath;
	private FileManager testMethodFileManager;
	private FilesManager testMethodManager;
	private static Map<InvokedInfo, Integer> modifiedCollectorInvocationLine;
	
	
	//-------------------------------------------------------------------------
	//		Initialization block
	//-------------------------------------------------------------------------
	static {
		inTestMethod = true;
		remainingTests = -1;
		
		currentTestMethodCheckpoint = new Checkpoint(
				ExecutionFlow.getAppRootPath(), 
				"Test_Method"
		);
		
		insideJUnitRunnerCheckpoint = new Checkpoint(
				Path.of(System.getProperty("user.home")),
				"initial"
		);
		
		firstRunCheckpoint = new Checkpoint(
				ExecutionFlow.getAppRootPath(), 
				"app_running"
		);
		
		session = new Session(
				"session.ef", 
				new File(System.getProperty("user.home")
		));
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
		    	if (!success) {
		    		stopRunner();
			    	finished = true;
		    	}
		    	
		    	cleanup();
		    }

			private void cleanup() {
				restoreOriginalFiles();
		    	
				if ((processingManager != null) && processingManager.isInvokedManagerInitialized()) {
					processingManager.deleteInvokedFileManagerBackup();
					deleteTestMethodBackupFiles();					
				}
				
				disableCheckpoint(currentTestMethodCheckpoint);
				disableCheckpoint(insideJUnitRunnerCheckpoint);
				disableCheckpoint(firstRunCheckpoint);
			}
			
			private void stopRunner() {
				if (!Session.hasKeyShared("JUNIT4_RUNNER"))
					return;
				
				try {
					JUnit4Runner runner = 
							(JUnit4Runner) Session.readShared("JUNIT4_RUNNER");
					runner.quit();
				} 
				catch (IOException e) {
				}
				finally {
					Session.removeShared("JUNIT4_RUNNER");						
				}
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
			processingManager = new InvokedManager(
					!insideJUnitRunnerCheckpoint.isEnabled()
			);
			
			initializeFileManager(thisJoinPoint);
		}
		catch(IOException | ClassNotFoundException | NoClassDefFoundError e) {
			Logger.error(e.getMessage());
			
			System.exit(-1);
		}
	}
	
	private void initializeFileManager(JoinPoint jp) {	
		testMethodFileManager = new FileManager.Builder()
				.srcPath(srcPath)
				.binDirectory(InvokedInfo.getCompiledFileDirectory(classPath))
				.classPackage(InvokedInfo.extractPackage(getClassSignature(jp)))
				.backupExtensionName("pretestmethod.bkp")
				.fileParserFactory(new PreTestMethodFileProcessorFactory(
						testMethodSignature, getParameterValues(jp)
				))
				.build();
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
			initializeTestMethodManager();
			cleanLastRun();
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
	
	private void initializeTestMethodManager() 
			throws ClassNotFoundException, IOException {
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
			
			if (testMethodManager != null) {
				testMethodManager.restoreAll();
				deleteTestMethodBackupFiles();
			}
			
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
	
	private void processCollectedInvoked() {
		ExecutionFlow methodExecutionFlow = new MethodExecutionFlow(
				processingManager, 
				methodCollector
		);
		methodExecutionFlow.run();
		
		ExecutionFlow constructorExecutionFlow = new ConstructorExecutionFlow(
				processingManager, 
				constructorCollector.values()
		);
		constructorExecutionFlow.run();
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
			if (!insideJUnitRunnerCheckpoint.isEnabled()) {
				insideJUnitRunnerCheckpoint.enable();
			}
			
			resetMethodsCalledByTestedInvoked();
			runJUnitRunner(jp);
			waitForJUnit4Runner();
		}
		catch (IOException | InterruptedException e) {
			Logger.error("Restart - " + e.getMessage());
			e.printStackTrace();
		}
		finally {
			Session.removeShared("JUNIT4_RUNNER");
			disableCheckpoint(insideJUnitRunnerCheckpoint);
		}
	}
	
	private void resetMethodsCalledByTestedInvoked() {
		File mcti = new File(ExecutionFlow.getAppRootPath().toFile(), "mcti.ef");
		mcti.delete();
	}
	
	private void runJUnitRunner(JoinPoint jp) 
			throws IOException, InterruptedException {
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
			
			if (!insideJUnitRunnerCheckpoint.isEnabled())
				junit4Runner.quit();
		}
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
		
		if (testMethodManager != null) {
			testMethodManager.restoreAll();
			deleteTestMethodBackupFiles();
		}
		
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
		if (modifiedCollectorInvocationLine == null)
			modifiedCollectorInvocationLine = new HashMap<>();
		
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
		for (List<InvokedContainer> methodCollectorList : methodCollector.values()) {
			updateInvokedInvocationLines(
					mapping, 
					testMethodSrcFile,
					methodCollectorList
			);
		}
	}
	
	private static void updateInvokedInvocationLines(Map<Integer, Integer> mapping, 
													 Path testMethodSrcFile, 
													 Collection<InvokedContainer> collector) {
		for (InvokedContainer cc : collector) {
			int invocationLine = cc.getInvokedInfo().getInvocationLine();
			
			if (!cc.getTestMethodInfo().getSrcPath().equals(testMethodSrcFile)  
					|| !mapping.containsKey(invocationLine))
				continue;
			
			cc.getInvokedInfo().setInvocationLine(mapping.get(invocationLine));
			
			if (!modifiedCollectorInvocationLine.containsKey(cc.getInvokedInfo()))
				modifiedCollectorInvocationLine.put(cc.getInvokedInfo(), invocationLine);
		}
	}
	
	public static void restoreCollectorInvocationLine() {
		if (modifiedCollectorInvocationLine == null)
			return;
		
		for (Map.Entry<InvokedInfo, Integer> e : modifiedCollectorInvocationLine.entrySet()) {
			e.getKey().setInvocationLine(e.getValue());
		}
		
		modifiedCollectorInvocationLine = null;
	}
}
