package wniemiec.executionflow;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import wniemiec.api.junit4.JUnit4API;
import wniemiec.executionflow.collector.CallCollector;
import wniemiec.executionflow.collector.ConstructorCollector;
import wniemiec.executionflow.collector.MethodCollector;
import wniemiec.executionflow.collector.parser.InvokedCollectorParser;
import wniemiec.executionflow.collector.parser.TestedInvokedParser;
import wniemiec.executionflow.exporter.ExportManager;
import wniemiec.executionflow.invoked.Invoked;
import wniemiec.executionflow.invoked.TestedInvoked;
import wniemiec.executionflow.io.processing.file.PreTestMethodFileProcessor;
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
	private static boolean inTestMethodWithAspectsDisabled;
	private static boolean finishedTestMethodWithAspectsDisabled;
	private static boolean errorProcessingTestMethod;
	private static volatile boolean success;
	
	
	//-------------------------------------------------------------------------
	//		Initialization blocks
	//-------------------------------------------------------------------------	
	/**
	 * Sets environment. If the code is executed outside project, that is,
	 * through a jar file, it must be false.
	 */
	static {
		DEVELOPMENT = true;
	}
	
	
	static {
		testMode = false;
		remainingTests = -1;
		inTestMethodWithAspectsDisabled = true;
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
	
	private static void onShutdown() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() {
		    	try {
		    		if (!success)
			    		finishedTestMethodWithAspectsDisabled = true;
		    		
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
	
	
	
	
	public static void inEachTestMethod(Invoked testMethod, boolean isRepeatedTest) {
		if (errorProcessingTestMethod)
			return;
		
		checkDevelopmentMode();
		
		checkInTestMethodWithAspectDisabled(isRepeatedTest);
		
		if (finishedTestMethodWithAspectsDisabled)
			return;
		
		try {
			ProcessingManager.initializeManagers(!runningFromJUnitAPI());
			inTheFirstRun();
			beforeEachTestMethod();
			initializeLogger();
			
			inTestMethodWithAspectsDisabled = inTestMethodWithAspectsDisabled();
			
			if (!inTestMethodWithAspectsDisabled())
				doPreprocessing(testMethod);
		}
		catch (IOException e) {
			errorProcessingTestMethod = true;
			Logger.error(e.getMessage());
			success = false;
		}
	}
	
	private static void checkInTestMethodWithAspectDisabled(boolean isRepeatedTest) {
		if (finishedTestMethodWithAspectsDisabled && inTestMethodWithAspectsDisabled && (!isRepeatedTest || isRepeatedTest && finishedTestMethodWithAspectsDisabled)) {
			finishedTestMethodWithAspectsDisabled = false;
			inTestMethodWithAspectsDisabled = true;
		}
	}
	
	public static void afterEachTestMethod(Invoked testMethod) {
		if (errorProcessingTestMethod) {
			errorProcessingTestMethod = false;
			return;
		}
		
		if (finishedTestMethodWithAspectsDisabled)
			return;
		
		if (inTestMethodWithAspectsDisabled) {
			InvokedCollectorParser.parseMethodCollector(
					MethodCollector.getCollectorSet()
			);
			InvokedCollectorParser.parseConstructorCollector(
					ConstructorCollector.getCollectorSet()
			);

			success = true;
		}
		else {
			exportAllMethodsUsedInTestMethods();
			exportAllConstructorsUsedInTestMethods();
			
			runTestMethodWithAspectsDisabled(testMethod);
			
			afterEachTestMethod();
			
			finishedTestMethodWithAspectsDisabled = true;
			inTestMethodWithAspectsDisabled = true;
		}
	}
	
	
	
	
	public static void inTheFirstRun() {
		try {
			LogLevel level;
			
			if (!firstRunCheckpoint.isEnabled()) {
				firstRunCheckpoint.enable();
				
				onShutdown();
				success = false;
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
	
	public static void beforeEachTestMethod() {
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
			if (!insideJUnitRunnerCheckpoint.isEnabled())
				insideJUnitRunnerCheckpoint.enable();
			
			CallCollector.deleteStoredContent();
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

	private static void updateRemainingTests() {
		if (remainingTests < 0) {
			remainingTests = PreTestMethodFileProcessor.getTotalTests() - 1;
		}
		else {
			remainingTests--;
		}
	}
	
	public static boolean runningFromJUnitAPI() {
		return insideJUnitRunnerCheckpoint.isEnabled();
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
	
	
	
	
	
	
	
	
	
	
	
	
	// PROCESSING
	
	
	public static void doPreprocessing(Invoked testMethod) throws IOException {
		try {
			currentTestMethodCheckpoint.enable();
			ProcessingManager.doPreprocessingInTestMethod(testMethod);
		} 
		catch (IOException e) {
			disableCheckpoint(currentTestMethodCheckpoint);
			throw e;
		}
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
		currentProjectRoot = searchDirectoryWithName("src").toPath();
	}
	
	public static File searchDirectoryWithName(String directoryName) {
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
	 * Gets application root path, based on class {@link TestedInvokedParser} location.
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
