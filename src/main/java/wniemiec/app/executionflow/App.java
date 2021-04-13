package wniemiec.app.executionflow;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import wniemiec.app.executionflow.exporter.ExportManager;
import wniemiec.app.executionflow.invoked.Invoked;
import wniemiec.app.executionflow.io.processing.file.PreTestMethodFileProcessor;
import wniemiec.app.executionflow.io.processing.manager.ProcessingManager;
import wniemiec.app.executionflow.io.runner.JUnitRunner;
import wniemiec.app.executionflow.lib.LibraryManager;
import wniemiec.app.executionflow.user.User;
import wniemiec.io.consolex.Consolex;
import wniemiec.util.task.Checkpoint;

/**
 * Responsible for deciding what to do when a {@link wniemiec.app.executionflow.runtime.hook} is triggered.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @since		7.0.0
 */
public class App {

	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static volatile boolean success;
	private static boolean inTestMethodWithAspectsDisabled;
	private static boolean finishedTestMethodWithAspectsDisabled;
	private static boolean errorProcessingTestMethod;
	private static int remainingTests;
	private static Path appRoot;
	private static Path currentProjectRoot;
	private static Path targetPath;
	private static transient Checkpoint firstRunCheckpoint;
	private static transient Checkpoint currentTestMethodCheckpoint;
	private static ExportManager methodExporter;
	private static ExportManager constructorExporter;
	private static ProcessingManager processingManager;
	
	
	//-------------------------------------------------------------------------
	//		Initialization blocks
	//-------------------------------------------------------------------------	
	static {
		remainingTests = -1;
		inTestMethodWithAspectsDisabled = true;
		
		methodExporter = ExportManager.getMethodExportManager(isDevelopment());
		constructorExporter = ExportManager.getConstructorExportManager(isDevelopment());
		
		onShutdown();
	}
	
	static {
		firstRunCheckpoint = new Checkpoint(
				getAppRootPath(), 
				"first-run"
		);
		
		currentTestMethodCheckpoint = new Checkpoint(
				getAppRootPath(), 
				"running-testmethod"
		);
	}
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------	
	private App() {
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------	
	public static void inEachTestMethod(Invoked testMethod, boolean isRepeatedTest) {
		if (errorProcessingTestMethod)
			return;

		checkDevelopmentMode();
		checkInTestMethodWithAspectDisabled();
		
		if (finishedTestMethodWithAspectsDisabled)
			return;
		
		try {
			processingManager = ProcessingManager.getInstance();
			processingManager.initializeManagers(!JUnitRunner.isRunningFromJUnitAPI());
			inTheFirstRun();
			beforeEachTestMethod();
			initializeLogger();
			
			inTestMethodWithAspectsDisabled = inTestMethodWithAspectsDisabled();
			
			if (!inTestMethodWithAspectsDisabled())
				doPreprocessing(testMethod);
		}
		catch (IOException e) {
			Consolex.writeError(e.toString());
			
			errorProcessingTestMethod = true;
			success = false;
		}
	}
	
	public static void checkDevelopmentMode() {
		if (!Files.exists(LibraryManager.getLibrary("JUNIT_4"))) {
			Consolex.writeError("Libraries missing");
			Consolex.writeLine("AppRootPath: " + getAppRootPath());
			Consolex.writeLine("JUnit4Lib: " + LibraryManager.getLibrary("JUNIT_4"));
			
			System.exit(-1);
		}
	}
	
	private static void checkInTestMethodWithAspectDisabled() {
		if (finishedTestMethodWithAspectsDisabled && inTestMethodWithAspectsDisabled) {
			finishedTestMethodWithAspectsDisabled = false;
			inTestMethodWithAspectsDisabled = true;
		}
	}
	
	public static void inTheFirstRun() {
		try {
			if (!firstRunCheckpoint.isEnabled()) {
				firstRunCheckpoint.enable();
				success = false;
				
				User.openMainSelector();
			}
			
			ExportManager.setTestPathExportType(User.getSelectedTestPathExportType());
			Consolex.setLoggerLevel(User.getSelectedLogLevel());
			
			dumpPaths();
		}
		catch(IOException | NoClassDefFoundError e) {
			Consolex.writeError(e.getMessage());
			
			System.exit(-1);
		}
	}

	private static void onShutdown() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() {
		    	try {
		    		User.closeRemoteControl();
		    		
		    		if (!success)
			    		finishedTestMethodWithAspectsDisabled = true;
		    		
			    	disableCheckpoint(currentTestMethodCheckpoint);
					disableCheckpoint(firstRunCheckpoint);
					
					JUnitRunner.stopRunner();
					User.unlinkSession();
		    	}
		    	catch (Throwable t) {
		    		// As the application will have finished, it is not 
		    		// relevant to deal with any errors 
		    	}
		    }
		});
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
	
	private static void dumpPaths() {
		Consolex.writeDebug("AppRootPath: " + getAppRootPath());
		Consolex.writeDebug("CurrentProjectPath: " + getCurrentProjectRoot());
		Consolex.writeDebug("AppTargetPath: " + getAppTargetPath());
	}
	
	private static void beforeEachTestMethod() {
		if (runningTestMethod())
			return;
		
		try {
			cleanLastRun();
			User.openRemoteControl();
		}
		catch(IOException | NoClassDefFoundError e) {
			Consolex.writeError(e.getMessage());
			
			System.exit(-1);
		}
	}
	
	public static boolean runningTestMethod() {
		return	processingManager.wasPreprocessingDoneSuccessfully()
				|| currentTestMethodCheckpoint.isEnabled();
	}
	
	private static void cleanLastRun() throws IOException {
		if (!hasTempFilesFromLastRun())
			return;
		
		processingManager.deleteBackupFilesOfProcessingOfTestMethod();
		currentTestMethodCheckpoint.delete();
	}
	
	private static boolean hasTempFilesFromLastRun() {
		return	currentTestMethodCheckpoint.exists() 
				&& !currentTestMethodCheckpoint.isEnabled();
	}
	
	private static void initializeLogger() {
		Consolex.setLoggerLevel(User.getSelectedLogLevel());
	}
	
	private static boolean inTestMethodWithAspectsDisabled() {
		return currentTestMethodCheckpoint.exists();
	}
	
	private static void doPreprocessing(Invoked testMethod) throws IOException {
		try {
			currentTestMethodCheckpoint.enable();
			processingManager.doPreprocessingInTestMethod(testMethod);
		} 
		catch (IOException e) {
			disableCheckpoint(currentTestMethodCheckpoint);
			throw e;
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
			methodExporter.parseAndExportAll();
			constructorExporter.parseAndExportAll();

			success = true;
		}
		else {
			methodExporter.exportAllInvokedUsedInTestMethods();
			constructorExporter.exportAllInvokedUsedInTestMethods();
			
			runTestMethodWithAspectsDisabled(testMethod);
			checkNextTestMethods();
			
			finishedTestMethodWithAspectsDisabled = true;
			inTestMethodWithAspectsDisabled = true;
		}
	}
	
	private static void runTestMethodWithAspectsDisabled(Invoked testMethod) {
		JUnitRunner.runTestMethod(testMethod);
	}

	private static void checkNextTestMethods() {
		updateRemainingTests();
		
		boolean successfullRestoration = false;
		successfullRestoration = processingManager.restoreOriginalFilesFromTestMethod();
		
		if (remainingTests == 0) {
			remainingTests = -1;
			
			successfullRestoration = undoInvokedProcessng();
			
			//User.closeRemoteControl();
		}
		
		if (!currentTestMethodCheckpoint.isEnabled()) {
			successfullRestoration = undoPreprocessing();
		}
		
		disableCheckpoint(currentTestMethodCheckpoint);
		
		if (!successfullRestoration) {
			Consolex.writeError("Error while restoring original files");
			System.exit(-1);
		}
	}
	
	private static void updateRemainingTests() {
		if (remainingTests < 0)
			remainingTests = PreTestMethodFileProcessor.getTotalTests() - 1;
		else
			remainingTests--;
	}

	private static boolean undoInvokedProcessng() {
		boolean successfullRestoration = 
				processingManager.restoreOriginalFilesFromInvoked();
			
		processingManager.deleteBackupFilesOfProcessingOfInvoked();
		
		return successfullRestoration;
	}

	private static boolean undoPreprocessing() {
		boolean successfullRestoration = true;
		
		try {
			processingManager.undoPreprocessing();
			processingManager.deleteBackupFilesOfPreprocessingOfTestMethod();
		} 
		catch (IOException e) {
			successfullRestoration = false;
		}
		
		return successfullRestoration;
	}
	
	
	//-------------------------------------------------------------------------
	//		Getters & Setters
	//-------------------------------------------------------------------------
	public static Path getAppTargetPath() {
		if (targetPath == null)
			targetPath = initializeTargetPath();
		
		return targetPath;
	}
	
	private static Path initializeTargetPath() {
		try {
			return Path
					.of(
						App.class
							.getProtectionDomain()
							.getCodeSource()
							.getLocation()
							.toURI()
					)
					.getParent();
		} 
		catch (URISyntaxException e) {
			return null;
		}
	}

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
	
	private static File searchDirectoryWithName(String directoryName) {
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
	 * Gets application root path.
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
		appRoot = getAppBinPath().getParent().getParent().getParent().getParent();
		
		if (isDevelopment(appRoot))
			appRoot = appRoot.getParent().getParent();
		
		appRoot = appRoot.normalize().toAbsolutePath();
		appRoot = fixWhiteSpaces(appRoot);
	}
	
	private static Path getAppBinPath() {
		return urlToPath(App.class.getResource("App.class"));
	}
	
	private static Path urlToPath(URL url) {
		return new File(url.getPath()).toPath();
	}
	
	private static Path fixWhiteSpaces(Path absolutePath) {
		return Path.of(absolutePath.toString().replaceAll("%20", " "));
	}
	
	private static boolean isDevelopment(Path appRoot) {
		return	appRoot.endsWith(Path.of("ExecutionFlow", "target", "classes"))
				|| appRoot.getFileName().endsWith("ExecutionFlow");
	}
	
	/**
	 * Checks if it is development environment.
	 * 
	 * @return		 True if it is production environment; false otherwise.
	 */
	public static boolean isDevelopment() {
		return isDevelopment(getAppRootPath());
	}
}
