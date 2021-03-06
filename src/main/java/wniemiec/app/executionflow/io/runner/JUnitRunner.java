package wniemiec.app.executionflow.io.runner;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import wniemiec.api.junit4.JUnit4API;
import wniemiec.app.executionflow.collector.CallCollector;
import wniemiec.app.executionflow.invoked.Invoked;
import wniemiec.app.executionflow.lib.LibraryManager;
import wniemiec.io.consolex.Consolex;
import wniemiec.util.data.storage.Session;
import wniemiec.util.task.Checkpoint;

/**
 * Responsible for running test methods using JUnit API.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @since		7.0.0
 */
public class JUnitRunner {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static Checkpoint insideJUnitAPICheckpoint;
	
	
	//-------------------------------------------------------------------------
	//		Initialization blocks
	//-------------------------------------------------------------------------
	static {
		insideJUnitAPICheckpoint = new Checkpoint(
				Path.of(System.getProperty("java.io.tmpdir")),
				"running-junit-api"
		);
		
		onShutdown();
	}
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	private JUnitRunner() {
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	private static void onShutdown() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() {
		    	try {
		    		stopRunner();
				} 
		    	catch (IOException e) {
					// As the application will have finished, it is not 
		    		// relevant to deal with any errors 
				}
		    }
	    });
	}
	
	public static void stopRunner() throws IOException {
		if (!Session.hasKeyShared("JUNIT4_RUNNER"))
			return;
		
		try {
			JUnit4API runner = 
					(JUnit4API) Session.readShared("JUNIT4_RUNNER");
			
			if (runner.isRunning())
				runner.quit();
		}
		finally {
			Session.removeShared("JUNIT4_RUNNER");						
		}
		
		disableJUnitRunnerCheckpoint();
	}
	
	public static void runTestMethod(Invoked testMethod) {
		try {
			if (!insideJUnitAPICheckpoint.isEnabled())
				insideJUnitAPICheckpoint.enable();
			
			resetCallsCollection();
			run(testMethod);
			waitForJUnitAPI();
		}
		catch (IOException | InterruptedException e) {
			Consolex.writeError("Restart - " + e.toString());
		}
		finally {
			disableJUnitRunnerCheckpoint();
		}
	}
	
	private static void resetCallsCollection() {
		CallCollector callCollector = CallCollector.getInstance();
		callCollector.deleteStoredContent();
	}
	
	private static void run(Invoked testMethod) 
			throws IOException, InterruptedException {
		JUnit4API junit4API = new JUnit4API.Builder()
				.workingDirectory(generateClassRootDirectory(testMethod))
				.classPath(generateClasspaths())
				.classSignature(testMethod.getClassSignature())
				.build();
		
		Session.saveShared("JUNIT4_RUNNER", junit4API);
		
		dump(testMethod);
		
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
	
	private static void dump(Invoked testMethod) {
		Consolex.writeDebug("Working directory: " + generateClassRootDirectory(testMethod));
		Consolex.writeDebug("Classpath: " + generateClasspaths());
		Consolex.writeDebug("Class signature: " + testMethod.getClassSignature());
	}
	
	private static void waitForJUnitAPI() throws IOException, InterruptedException {
		JUnit4API junit4API = (JUnit4API) Session.readShared("JUNIT4_RUNNER");
		
		while (junit4API.isRunning()) {
			Thread.sleep(2000);
			
			if (!insideJUnitAPICheckpoint.isEnabled())
				junit4API.quit();
		}
		
		junit4API.quit();
	}
	
	private static void disableJUnitRunnerCheckpoint() {
		try {
			insideJUnitAPICheckpoint.disable();
		}
		catch (IOException | InterruptedException e) {
			Consolex.writeError(e.toString());
		}
	}
	
	public static boolean isRunningFromJUnitAPI() {
		return insideJUnitAPICheckpoint.isEnabled();
	}
}
