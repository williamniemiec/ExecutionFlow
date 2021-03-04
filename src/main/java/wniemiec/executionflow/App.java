package wniemiec.executionflow;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

import wniemiec.executionflow.exporter.ExportManager;
import wniemiec.executionflow.invoked.Invoked;
import wniemiec.executionflow.io.processing.file.PreTestMethodFileProcessor;
import wniemiec.executionflow.io.processing.manager.ProcessingManager;
import wniemiec.executionflow.io.runner.JUnitRunner;
import wniemiec.executionflow.lib.LibraryManager;
import wniemiec.executionflow.user.User;
import wniemiec.util.logger.LogLevel;
import wniemiec.util.logger.Logger;
import wniemiec.util.task.Checkpoint;

/**
 * Responsible for deciding what to do when a {@link wniemiec.executionflow
 * .runtime.hook} is triggered.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		7.0.0
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
	private static Checkpoint firstRunCheckpoint;
	private static Checkpoint currentTestMethodCheckpoint;
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
			Logger.error(e.toString());
			
			errorProcessingTestMethod = true;
			success = false;
		}
	}
	
	public static void checkDevelopmentMode() {
		if (!Files.exists(LibraryManager.getLibrary("JUNIT_4"))) {
			Logger.error("Development mode is off even in a development "
					+ "environment. Turn it on in the ExecutionFlow class");
			
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
	
	private static void onShutdown() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() {
		    	try {
		    		if (!success)
			    		finishedTestMethodWithAspectsDisabled = true;
		    		
			    	disableCheckpoint(currentTestMethodCheckpoint);
					disableCheckpoint(firstRunCheckpoint);
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
	
	private static void beforeEachTestMethod() {
		if (runningTestMethod())
			return;
		
		try {
			cleanLastRun();
			User.openRemoteControl();
		}
		catch(IOException | NoClassDefFoundError e) {
			Logger.error(e.getMessage());
			
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
		
		processingManager.deleteTestMethodBackupFiles();
		currentTestMethodCheckpoint.delete();
	}
	
	private static boolean hasTempFilesFromLastRun() {
		return	currentTestMethodCheckpoint.exists() 
				&& !currentTestMethodCheckpoint.isEnabled();
	}
	
	private static void initializeLogger() {
		Logger.setLevel(User.getSelectedLogLevel());
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
			methodExporter.exportAll();
			constructorExporter.exportAll();

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
			successfullRestoration = processingManager.restoreOriginalFilesFromInvoked();
			processingManager.deleteInvokedBackupFiles();
			User.closeRemoteControl();
		}
		
		if (remainingTests == 0) {
			remainingTests = -1;
		}
		
		try {
			processingManager.restoreAllTestMethodFiles();
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
	}
	
	
	//-------------------------------------------------------------------------
	//		Getters & Setters
	//-------------------------------------------------------------------------
	public static Path getTargetPath() {
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
		try {
			appRoot = Path.of(
					App.class
						.getProtectionDomain()
						.getCodeSource()
						.getLocation()
						.toURI()
			);
			
			if (isDevelopment(appRoot))
				appRoot = appRoot.getParent().getParent();
		} 
		catch (URISyntaxException e) {
			Logger.error("Error initializing application root path");
			
			appRoot = null;
		}
	}
	
	private static boolean isDevelopment(Path appRoot) {
		return appRoot.endsWith(Path.of("ExecutionFlow", "target", "classes"));
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
