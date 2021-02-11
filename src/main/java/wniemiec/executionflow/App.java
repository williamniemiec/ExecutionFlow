package wniemiec.executionflow;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import wniemiec.api.junit4.JUnit4API;
import wniemiec.executionflow.invoked.InvokedInfo;
import wniemiec.executionflow.io.processor.fileprocessor.PreTestMethodFileProcessor;
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
	
	static {
		remainingTests = -1;
	}
	
	static {
		firstRunCheckpoint = new Checkpoint(
				ExecutionFlow.getAppRootPath(), 
				"app_running"
		);
		
		currentTestMethodCheckpoint = new Checkpoint(
				ExecutionFlow.getAppRootPath(), 
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
			    	if (!success) {
			    		stopRunner();
				    	finished = true;
			    	}
			    	
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
	public static void runTestMethodWithAspectsDisabled(InvokedInfo testMethod) {
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
		File mcti = new File(ExecutionFlow.getAppRootPath().toFile(), "mcti.ef");
		mcti.delete();
	}
	
	private static void runJUnitRunner(InvokedInfo testMethod) 
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
	private static Path generateClassRootDirectory(InvokedInfo testMethod) {
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

	
	
	private void beforeEachTestMethod() {
		if (App.runningTestMethod())
			return;
		
		try {
			ProcessingManager.initializeTestMethodManager();
			App.cleanLastRun();
			App.openControlWindow();
		}
		catch(IOException | ClassNotFoundException | NoClassDefFoundError e) {
			Logger.error(e.getMessage());
			
			System.exit(-1);
		}
	}
	
	private void afterEachTestMethod() {		
		updateRemainingTests();
		
		boolean successfullRestoration = false;
		successfullRestoration = ProcessingManager.restoreTestMethodFiles();
		
		if (remainingTests == 0) {
			successfullRestoration = ProcessingManager.restoreInvokedFiles();
			ProcessingManager.deleteInvokedBackupFiles();
			App.closeControlWindow();
		}
		
		ProcessingManager.restoreAllTestMethodFiles();

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
	
	public static boolean runningFromJUnitAPI() {
		return insideJUnitRunnerCheckpoint.isEnabled();
	}
	
	
	public static boolean runningTestMethod() {
		return	(testMethodManager != null) 
				|| currentTestMethodCheckpoint.isEnabled();
	}
	
	public static void cleanLastRun() throws IOException {
		if (hasTempFilesFromLastRun()) {
			testMethodManager.deleteBackup();
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
}
