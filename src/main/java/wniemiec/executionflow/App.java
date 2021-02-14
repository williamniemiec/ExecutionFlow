package wniemiec.executionflow;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.channels.InterruptedByTimeoutException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import wniemiec.api.junit4.JUnit4API;
import wniemiec.executionflow.analyzer.DebuggerAnalyzer;
import wniemiec.executionflow.analyzer.DebuggerAnalyzerFactory;
import wniemiec.executionflow.collector.ConstructorCollector;
import wniemiec.executionflow.collector.InvokedCollector;
import wniemiec.executionflow.collector.MethodCollector;
import wniemiec.executionflow.collector.parser.InvokedCollectorParser;
import wniemiec.executionflow.exporter.ExportManager;
import wniemiec.executionflow.invoked.Invoked;
import wniemiec.executionflow.invoked.TestedInvoked;
import wniemiec.executionflow.io.processing.file.InvokedFileProcessor;
import wniemiec.executionflow.io.processing.file.PreTestMethodFileProcessor;
import wniemiec.executionflow.io.processing.file.TestMethodFileProcessor;
import wniemiec.executionflow.io.processing.file.factory.InvokedFileProcessorFactory;
import wniemiec.executionflow.io.processing.file.factory.TestMethodFileProcessorFactory;
import wniemiec.executionflow.io.processing.manager.FileProcessingManager;
import wniemiec.executionflow.lib.LibraryManager;
import wniemiec.executionflow.runtime.hook.ProcessingManager;
import wniemiec.executionflow.user.RemoteControl;
import wniemiec.executionflow.user.User;
import wniemiec.util.data.storage.Session;
import wniemiec.util.logger.LogLevel;
import wniemiec.util.logger.Logger;
import wniemiec.util.task.Checkpoint;

public class App {

	private static Checkpoint firstRunCheckpoint;
	private static Checkpoint currentTestMethodCheckpoint;
	private static Checkpoint insideJUnitRunnerCheckpoint;
	private static int remainingTests;
	private static final boolean DEVELOPMENT;	
	private static Path appRoot;
	private static Path currentProjectRoot;
	private static boolean testMode;
	
	private static Set<String> alreadyChanged;
	
	
	
	//-------------------------------------------------------------------------
	//		Initialization blocks
	//-------------------------------------------------------------------------	
	/**
	 * Sets environment. If the code is executed outside project, that is,
	 * through a jar file, it must be false. It will affect
	 * {@link #getAppRootPath()} and 
	 * {@link executionflow.io.compiler.aspectj.StandardAspectJCompiler#compile()}.
	 */
	static {
		DEVELOPMENT = true;
	}
	
	
	static {
		testMode = false;
		remainingTests = -1;
	}
	
	static {
		firstRunCheckpoint = new Checkpoint(
				getAppRootPath(), 
				"app_running"
		);
		
		currentTestMethodCheckpoint = new Checkpoint(
				getAppRootPath(), 
				"Test_Method"
		);
		
		insideJUnitRunnerCheckpoint = new Checkpoint(
				Path.of(System.getProperty("user.home")),
				"initial"
		);
	}
	
	public static void onFirstRun() {
		try {
			LogLevel level;
			
			if (!firstRunCheckpoint.isEnabled()) {
				firstRunCheckpoint.enable();
				
				onShutdown();
				level = User.askUserForLogLevel();
				
			}
			else {
				 level = User.loadLogLevel();		
			}
			
			Logger.setLevel(level);
		}
		catch(IOException | NoClassDefFoundError e) {
			Logger.error(e.getMessage());
			
			System.exit(-1);
		}
	}
	
	private static void onShutdown() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() {
		    	try {
			    	stopRunner();
			    	
			    	disableCheckpoint(currentTestMethodCheckpoint);
					disableCheckpoint(insideJUnitRunnerCheckpoint);
					disableCheckpoint(firstRunCheckpoint);
		    	}
		    	catch (Throwable t) {
		    	}
		    }
			
			private void stopRunner() {
				if (!Session.hasKeyShared("JUNIT4_RUNNER"))
					return;
				
				try {
					JUnit4API runner = 
							(JUnit4API) Session.readShared("JUNIT4_RUNNER");
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
	
	public static void initializeLogger() {
		Logger.setLevel(User.getSelectedLogLevel());
	}
	
	public static void checkDevelopmentMode() {
		if (!Files.exists(LibraryManager.getLibrary("JUNIT_4"))) {
			Logger.error("Development mode is off even in a development "
					+ "environment. Turn it on in the ExecutionFlow class");
			
			System.exit(-1);
		}
	}

	public static void openControlWindow() {
		RemoteControl.open();
	}
	
	public static void closeControlWindow() {
		RemoteControl.close();
	}
	
	
	
	
	
	
	private static boolean disableCheckpoint(Checkpoint checkpoint) {
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
	 * Restarts the application by starting it in CLI mode.
	 * 
	 * @throws		IOException If an error occurs when creating the process 
	 * containing {@link JUnit4API}
	 * @throws		InterruptedException If the process containing 
	 * {@link JUnit4API} is interrupted while it is waiting  
	 */
	public static void runTestMethodWithAspectsDisabled(Invoked testMethod) {
		try {
			if (!insideJUnitRunnerCheckpoint.isEnabled()) {
				insideJUnitRunnerCheckpoint.enable();
			}
			
			resetMethodsCalledByTestedInvoked();
			runJUnitRunner(testMethod);
			waitForJUnit4API();
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
	
	private static void resetMethodsCalledByTestedInvoked() {
		File mcti = new File(getAppRootPath().toFile(), "mcti.ef");
		mcti.delete();
	}
	
	private static void runJUnitRunner(Invoked testMethod) 
			throws IOException, InterruptedException {
		JUnit4API junit4API = new JUnit4API.Builder()
				.workingDirectory(generateClassRootDirectory(testMethod))
				.classPath(generateClasspaths())
				.classSignature(testMethod.getClassSignature())
				.build();
		
		Session.saveShared("JUNIT4_RUNNER", junit4API);
		
		junit4API.run();
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
	private static Path generateClassRootDirectory(Invoked testMethod) {
		Path binRootPath = testMethod.getBinPath();
		String classPackage = testMethod.getPackage();
		int packageFolders = 0;
		
		if (!(classPackage.isEmpty() || (classPackage == null)))
			packageFolders = classPackage.split("\\.").length;

		binRootPath = binRootPath.getParent();

		for (int i=0; i<packageFolders; i++) {
			binRootPath = binRootPath.getParent();
		}
		
		return binRootPath;
	}
	
//	private String getClassPackage() {
//		if ((testMethodSignature == null) || testMethodSignature.isEmpty())
//			return "";
//		
//		String[] terms = testMethodSignature.split("\\.");
//		StringBuilder classPackage = new StringBuilder();
//		
//		for (int i=0; i<terms.length-2; i++) {
//			classPackage.append(terms[i]);
//			classPackage.append(".");
//		}
//		
//		// Removes last dot
//		if (classPackage.length() > 0) {
//			classPackage.deleteCharAt(classPackage.length()-1);
//		}
//		
//		return classPackage.toString();
//	}
	
	private static List<Path> generateClasspaths() {
		List<Path> classpaths = LibraryManager.getJavaClassPath();
		classpaths.add(LibraryManager.getLibrary("JUNIT_4"));
		classpaths.add(LibraryManager.getLibrary("HAMCREST"));
		
		return classpaths;
	}
	
	private static void waitForJUnit4API() throws IOException, InterruptedException {
		JUnit4API junit4API = (JUnit4API) Session.readShared("JUNIT4_RUNNER");
		
		while (junit4API.isRunning()) {
			Thread.sleep(2000);
			
			if (!insideJUnitRunnerCheckpoint.isEnabled())
				junit4API.quit();
		}
		
		junit4API.quit();
	}

	
	
	public static void beforeEachTestMethod() {
		ProcessingManager.initializeManagers(!insideJUnitRunnerCheckpoint.isEnabled());
		
		if (App.runningTestMethod())
			return;
		
		try {
			cleanLastRun();
			openControlWindow();
		}
		catch(IOException | NoClassDefFoundError e) {
			Logger.error(e.getMessage());
			
			System.exit(-1);
		}
	}
	
	public static void afterEachTestMethod() {
		updateRemainingTests();
		
		boolean successfullRestoration = false;
		successfullRestoration = ProcessingManager.restoreOriginalFilesFromTestMethod();
		
		if (remainingTests == 0) {
			successfullRestoration = ProcessingManager.restoreOriginalFilesFromInvoked();
			ProcessingManager.deleteInvokedBackupFiles();
			App.closeControlWindow();
		}
		
		if (remainingTests == 0) {
			remainingTests = -1;
		}
		
		try {
			ProcessingManager.restoreAllTestMethodFiles();
		} 
		catch (IOException e) {
			successfullRestoration = false;
		}

		disableCheckpoint(currentTestMethodCheckpoint);
		
		if (!successfullRestoration) {
			Logger.error("Error while restoring original files");
			System.exit(-1);
		}
	}
	
	private static void updateRemainingTests() {
		if (remainingTests < 0) {
			remainingTests = PreTestMethodFileProcessor.getTotalTests() - 1;
		}
		else {
			remainingTests--;
		}

//		if (remainingTests == 0) {
//			remainingTests = -1;
//		}
	}
	
	public static boolean runningFromJUnitAPI() {
		return insideJUnitRunnerCheckpoint.isEnabled();
	}
	
	
	public static boolean runningTestMethod() {
		return	ProcessingManager.wasPreprocessingDoneSuccessfully()
				|| currentTestMethodCheckpoint.isEnabled();
	}
	
	public static void cleanLastRun() throws IOException {
		if (hasTempFilesFromLastRun()) {
			ProcessingManager.deleteTestMethodBackupFiles();
			currentTestMethodCheckpoint.delete();
		}
	}
	
	public static boolean hasTempFilesFromLastRun() {
		return	currentTestMethodCheckpoint.exists() 
				&& !currentTestMethodCheckpoint.isEnabled();
	}
	
	public static boolean inTestMethodWithAspectsDisabled() {
		return currentTestMethodCheckpoint.exists();
	}
	
	
	
	
	// EXPORT
	public static void exportAllMethodsUsedInTestMethods() {
		List<TestedInvoked> collectors = new ArrayList<>();
		
		for (List<TestedInvoked> collector : MethodCollector.getCollector().values()) {
			collectors.add(collector.get(0));
		}
		
		exporMethodsAndConstructorsUsedInTestMethods(false, collectors);
	}
	
	public static void exporMethodsAndConstructorsUsedInTestMethods(boolean isConstructor, 
															  Collection<TestedInvoked> invokedCollector) {
		Set<TestedInvoked> invokedSet = new HashSet<>();
		ExportManager exportManager = new ExportManager(
				isDevelopment(), 
				isConstructor
		);
		
		for (TestedInvoked collector : invokedCollector) {
			invokedSet.add(new TestedInvoked(
					collector.getTestedInvoked(),
					collector.getTestMethod()
			));
		}
		
		exportManager.exportAllMethodsAndConstructorsUsedInTestMethods(invokedSet);
	}
	
	public static void exportAllConstructorsUsedInTestMethods() {
		exporMethodsAndConstructorsUsedInTestMethods(true,
				ConstructorCollector.getCollector().values());
	}
	
	public static void doPreprocessing(Invoked testMethod) throws IOException {
		try {
			if (!inTestMethodWithAspectsDisabled()) {
				currentTestMethodCheckpoint.enable();
				ProcessingManager.doPreprocessingInTestMethod(testMethod);
			}
		} 
		catch (IOException e) {
			disableCheckpoint(currentTestMethodCheckpoint);
			throw e;
		}
	}
	
	
	// PARSE COLLECTORS
	/**
	 * Runs the application by performing the following tasks: 
	 * <ul>
	 * 	<li>Computes test path</li>
	 * 	<li>Exports test path</li>
	 * 	<li>Exports methods called by tested invoked</li>
	 * 	<li>Exports test methods that test the invoked</li>
	 * 	<li>Exports processed source file</li>
	 * </ul>
	 */
	public void parseInvokedCollector(Set<TestedInvoked> invokedCollector, boolean isConstructor) {
		if ((invokedCollector == null) || invokedCollector.isEmpty())
			return;
		
		
		InvokedCollectorParser parser = new InvokedCollectorParser();
		dump();

		for (TestedInvoked collector : invokedCollector) {
			FileProcessingManager invokedFileManager = createInvokedFileManager(collector);
			FileProcessingManager testMethodFileManager = createTestMethodFileManager(collector);
			
			try {	
				processTestMethod(collector, testMethodFileManager);
				processInvokedMethod(collector, invokedFileManager, testMethodFileManager);
				
				DebuggerAnalyzer debuggerAnalyzer = DebuggerAnalyzerFactory.createStandardTestPathAnalyzer(
						collector.getTestedInvoked(), 
						collector.getTestMethod()
				);
				
				
				parser.parseCollector(collector, debuggerAnalyzer);
				
				if (isTestedInvokedInTheSameFileAsTestMethod(collector)) {
					resetProcessing(invokedFileManager, testMethodFileManager);
				}
			}
			catch (InterruptedByTimeoutException e1) {
				Logger.error("Time exceeded");
			} 
			catch (IllegalStateException e2) {
				Logger.error(e2.getMessage());
			}
			catch (IOException e3) {
				Logger.error(e3.getMessage());
				
				ProcessingManager.restoreInvokedToBeforeProcessing(invokedFileManager);
				ProcessingManager.restoreTestMethodToBeforeProcessing(testMethodFileManager);
			}
		}
		
		export(parser, isConstructor);
		
//		parseMethodCollector();
//		parseConstructorCollector();
	}
	
	private void export(InvokedCollectorParser parser, boolean isConstructor) {
		ExportManager exportManager;
		exportManager = new ExportManager(App.isDevelopment(), isConstructor);
		
		exportManager.exportTestPaths(parser.getTestPaths());
		exportManager.exportEffectiveMethodsAndConstructorsUsedInTestMethods(
				parser.getMethodsAndConstructorsUsedInTestMethod()
		);
		exportManager.exportProcessedSourceFiles(parser.getProcessedSourceFiles());
		exportManager.exportMethodsCalledByTestedInvoked(
				parser.getMethodsCalledByTestedInvoked()
		);
	}
	
	private boolean isTestedInvokedInTheSameFileAsTestMethod(TestedInvoked collector) {
		return collector.getTestedInvoked().getSrcPath().equals(
				collector.getTestMethod().getSrcPath());
	}
	
	private void resetProcessing(FileProcessingManager invokedFileManager, 
			 					 FileProcessingManager testMethodFileManager) {
		ProcessingManager.restoreInvokedToBeforeProcessing(invokedFileManager);
		ProcessingManager.restoreTestMethodToBeforeProcessing(testMethodFileManager);
		
		InvokedCollector.restoreCollectorInvocationLine();
		
		alreadyChanged.clear();
	}

	private void processInvokedMethod(TestedInvoked collector,
									  FileProcessingManager invokedFileManager,
									  FileProcessingManager testMethodFileManager) throws IOException {
		Logger.info("Processing source file of invoked - " 
				+ collector.getTestedInvoked().getConcreteSignature() 
				+ "..."
		);
		
		ProcessingManager.doProcessingInInvoked(invokedFileManager, testMethodFileManager);
		
		updateInvocationLineAfterInvokedProcessing(collector);
		
		Logger.info("Processing completed");
	}

	private void updateInvocationLineAfterInvokedProcessing(TestedInvoked collector) {
		if (App.isTestMode()) {
			if (collector.getTestedInvoked().getSrcPath().equals(
					collector.getTestMethod().getSrcPath())) {
				updateCollector(collector, InvokedFileProcessor.getMapping());
			}
		}
		else {
			updateCollectors(
					InvokedFileProcessor.getMapping(),
					collector.getTestMethod().getSrcPath(), 
					collector.getTestedInvoked().getSrcPath()
			);
		}
	}

	private void updateCollector(TestedInvoked collector, Map<Integer, Integer> mapping) {
		int invocationLine = collector.getTestedInvoked().getInvocationLine();
		
		if (mapping.containsKey(invocationLine))
			collector.getTestedInvoked().setInvocationLine(mapping.get(invocationLine));
	}

	private void updateCollectors(Map<Integer, Integer> mapping, Path testMethodSrcPath,
								  Path invokedSrcPath) {
		if (alreadyChanged.contains(testMethodSrcPath.toString()) && 
				!invokedSrcPath.equals(testMethodSrcPath))
			return;

		ConstructorCollector.updateInvocationLines(
				mapping, 
				testMethodSrcPath
		);
		
		MethodCollector.updateInvocationLines(
				mapping, 
				testMethodSrcPath
		);
		
		alreadyChanged.add(testMethodSrcPath.toString());
	}

	private void processTestMethod(TestedInvoked collector, 
								   FileProcessingManager testMethodFileManager) throws IOException {
		Logger.info(
				"Processing source file of test method "
				+ collector.getTestMethod().getConcreteSignature() 
				+ "..."
		);
		
		ProcessingManager.doProcessingInTestMethod(testMethodFileManager);
		
		updateInvocationLineAfterTestMethodProcessing(collector);
		
		Logger.info("Processing completed");
	}

	private void updateInvocationLineAfterTestMethodProcessing(TestedInvoked collector) {
		if (App.isTestMode()) {
			updateCollector(collector, TestMethodFileProcessor.getMapping());
		}
		else {
			updateCollectors(
					TestMethodFileProcessor.getMapping(),
					collector.getTestMethod().getSrcPath(), 
					collector.getTestedInvoked().getSrcPath()
			);
		}
	}

	private FileProcessingManager createTestMethodFileManager(TestedInvoked collector) {
		return new FileManager.Builder()
				.srcPath(collector.getTestMethod().getSrcPath())
				.binDirectory(collector.getTestMethod().getClassDirectory())
				.classPackage(collector.getTestMethod().getPackage())
				.backupExtensionName("testMethod.bkp")
				.fileParserFactory(new TestMethodFileProcessorFactory())
				.build();
	}

	private FileProcessingManager createInvokedFileManager(TestedInvoked collector) {
		return new FileManager.Builder()
				.srcPath(collector.getTestedInvoked().getSrcPath())
				.binDirectory(collector.getTestedInvoked().getClassDirectory())
				.classPackage(collector.getTestedInvoked().getPackage())
				.backupExtensionName("invoked.bkp")
				.fileParserFactory(new InvokedFileProcessorFactory())
				.build();
	}
	
	private void dump() {
		Logger.debug(
				this.getClass(), 
				"collector: " + invokedCollector.toString()
		);
	}
	
	private void parseMethodCollector() {
		parseInvokedCollector(MethodCollector.getCollectorSet(), false);
//		CollectorParser methodExecutionFlow = new MethodCollectorParser(
//				MethodCollector.getCollector()
//		);
//		methodExecutionFlow.run();
	}
//	
	private void parseConstructorCollector() {
		parseInvokedCollector(ConstructorCollector.getCollectorSet(), true);
//		CollectorParser methodExecutionFlow = new MethodCollectorParser(
//				MethodCollector.getCollector()
//		);
//		methodExecutionFlow.run();
	}
	
	
	
	
	
	//-------------------------------------------------------------------------
	//		Getters & Setters
	//-------------------------------------------------------------------------
	/**
	 * Finds current project root (project that is running the application). It
	 * will return the path that contains a directory with name 'src'. 
	 * 
	 * @return		Project root path
	 * 
	 * @implSpec	Lazy initialization
	 */
	public static Path getCurrentProjectRoot() {
		if (currentProjectRoot == null)
			initializeCurrentProjectRoot();
		
		return currentProjectRoot;
	}
	
	private static void initializeCurrentProjectRoot() {		
		currentProjectRoot = search("src").toPath();
	}
	
	public static File search(String directoryName) {
		File currentDirectory = new File(System.getProperty("user.dir"));
		boolean hasDirectoryWithProvidedName = false;
		
		while (!hasDirectoryWithProvidedName) {
			hasDirectoryWithProvidedName = hasFileWithName(directoryName, currentDirectory);

			if (!hasDirectoryWithProvidedName)
				currentDirectory = new File(currentDirectory.getParent());
		}
		
		return currentDirectory;
	}

	private static boolean hasFileWithName(String name, File workingDirectory) {
		String[] files = workingDirectory.list();
		
		for (int i=0; i<files.length; i++) {
			if (files[i].equals(name))
				return true;
		}
		
		return false;
	}

	/**
	 * Gets application root path, based on class {@link InvokedCollectorParser} location.
	 * 
	 * @return		Application root path
	 * 
	 * @implSpec	Lazy initialization
	 */
	public static Path getAppRootPath() {
		if (appRoot == null)
			initializeAppRoot();
		
		return appRoot;
	}
	
	private static void initializeAppRoot() {
		try {
			File executionFlowBinPath = new File(
					App.class
						.getProtectionDomain()
						.getCodeSource()
						.getLocation()
						.toURI()
			);
			
			if (isDevelopment()) {
				appRoot = executionFlowBinPath
						.getAbsoluteFile()
						.getParentFile()
						.getParentFile()
						.toPath();
			}
			else {
				appRoot = executionFlowBinPath
						.getAbsoluteFile()
						.getParentFile()
						.toPath();
			}
		} 
		catch (URISyntaxException e) {
			Logger.error("Error initializing application root path");
			
			appRoot = null;
		}
	}
	
	/**
	 * Checks if it is development environment. If it is production environment,
	 * it will return false; otherwise, true.
	 * 
	 * @return		If it is development environment
	 */
	public static boolean isDevelopment() {
		return DEVELOPMENT;
	}
	
	public static boolean isTestMode() {
		return testMode;
	}
	
	public static void setTestMode(boolean status) {
		testMode = status;
	}
	
}
