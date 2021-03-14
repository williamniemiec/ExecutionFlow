package wniemiec.app.executionflow.analyzer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import wniemiec.api.jdb.JDB;
import wniemiec.app.executionflow.App;
import wniemiec.app.executionflow.collector.CallCollector;
import wniemiec.app.executionflow.invoked.Invoked;
import wniemiec.app.executionflow.invoked.TestedInvoked;
import wniemiec.app.executionflow.lib.LibraryManager;
import wniemiec.util.logger.Logger;
import wniemiec.util.task.Scheduler;

/**
 * Computes the test path for a method or constructor using a debugger.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		7.0.0
 * @since		2.0.0
 */
public abstract class DebuggerAnalyzer {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	protected volatile boolean timeout;
	protected volatile JDB jdb;
	protected volatile List<List<Integer>> testPaths;
	protected boolean stopJDB;
	protected String analyzedInvokedSignature;
	protected Invoked testedInvoked;
	protected Invoked testMethod;
	private List<String> commands;
	private int timeoutTime;
	private CallCollector callCollector;
	private Object lock = new Object();

	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------	
	/**
	 * Computes test path from code debugging along with methods called by it.
	 * 
	 * @throws		IOException If occurs an error while fetching dependencies
	 */
	protected DebuggerAnalyzer(TestedInvoked testedInvoked) 
			throws IOException	{
		this.testedInvoked = testedInvoked.getTestedInvoked();
		this.testMethod = testedInvoked.getTestMethod();
		this.analyzedInvokedSignature = "";
		
		callCollector = CallCollector.getInstance();
		timeoutTime = 10 * 60 * 1000;
		
		initializeJDB();
	}


	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Runs test method through JDB, registering its test path along with the
	 * methods called by all methods or constructors called from this test
	 * method.
	 * 
	 * @return		Itself to allow chained calls
	 * 
	 * @throws		IOException If an error occurs while reading the output 
	 * @throws		IllegalStateException If JDB has not been initialized or 
	 * if invocation line is incorrect
	 */
	public DebuggerAnalyzer analyze() throws IOException {
		if (jdb == null)
			throw new IllegalStateException("JDB has not been initialized");
		
		final int timeoutID = 1;

		try {
			startJDB();
			enableTimeout(timeoutID);
			run();
		}
		finally {
			try {
				Thread.sleep(200);
			} 
			catch (InterruptedException e) {
			}
			
			synchronized(lock) {
				disableTimeout(timeoutID);
				closeJDB();
			}
		}
		
		callCollector.mergeMethodsCalledByTestedInvoked();
		
		return this;
	}
	
	private void startJDB() throws IOException {
		jdb.run().send(buildInitCommand());
	}
	
	private String[] buildInitCommand() {
		commands = new ArrayList<>();
		
		clearBreakpoints();
		initializeBreakpoint();
		initializeRunClass();
		
		return commands.toArray(new String[] {});
	}
	
	private void clearBreakpoints() {
		commands.add("clear");
		
		Logger.debug(this.getClass(), "COMMAND: clear");
	}
	
	private void initializeBreakpoint() {
		StringBuilder command = new StringBuilder();
		
		command.append("run org.junit.runner.JUnitCore");
		command.append(" ");
		command.append(testMethod.getClassSignature());
		
		commands.add(command.toString());
		
		Logger.debug(this.getClass(), "COMMAND: " + command.toString());
	}
	
	private void initializeRunClass() {
		StringBuilder command = new StringBuilder();
		
		command.append("stop at");
		command.append(" ");
		command.append(testMethod.getClassSignature());
		command.append(":");
		command.append(testedInvoked.getInvocationLine());
		
		commands.add(command.toString());
		
		Logger.debug(this.getClass(), "COMMAND: " + command.toString());
	}
	
	private void enableTimeout(final int TIMEOUT_ID) {
		timeout = false;
		
		Scheduler.setTimeout(() -> {
			synchronized(lock) {
				callCollector.deleteStoredContent();
				closeJDBImmediately();
				testPaths.clear();
				
				timeout = true;
			}
		}, TIMEOUT_ID, timeoutTime);
	}

	private void closeJDBImmediately() {
		if (jdb == null)
			return;
		
		if (!jdb.isRunning())
			return;
		
		stopJDB = true;
		
		try {
			jdb.forceQuit();
		} 
		catch (IOException e) {
			Logger.error(e.getMessage());
		}
	}

	protected abstract void run() throws IOException;

	private void disableTimeout(final int TIMEOUT_ID) {
		Scheduler.clearTimeout(TIMEOUT_ID);
	}
	
	private void closeJDB() {
		if ((jdb == null) || !jdb.isRunning())
			return;
		
		stopJDB = true;
		
		try {
			jdb.send(buildExitCommand());
			jdb.quit();
		} 
		catch (IllegalStateException | InterruptedException e) {
			// If an exception occurs, JDB will have ended 
		}
		
		stopJDB = true;
	}
	
	private String[] buildExitCommand() {
		commands = new ArrayList<>();
		
		clearLastBreakpoint();
		exitCommand();
		
		return commands.toArray(new String[] {});
	}

	private void clearLastBreakpoint() {
		StringBuilder command = new StringBuilder();
		
		command.append("clear");
		command.append(" ");
		command.append(testMethod.getClassSignature());
		command.append(":");
		command.append(testedInvoked.getInvocationLine());
		
		commands.add(command.toString());
		
		Logger.debug(this.getClass(), "COMMAND: " + command.toString());
	}

	private void exitCommand() {
		commands.add("exit");
		commands.add("exit");
		
		Logger.debug(this.getClass(), "COMMAND: exit");
	}
	
	private void initializeJDB() {
		Path testClassRootPath = extractRootPathDirectory(
				testMethod.getBinPath(), 
				testMethod.getPackage()
		);
		
		List<Path> srcPath = getSourcePath(testedInvoked, testMethod);
		List<Path> classPath = getClassPath();

		jdb = new JDB.Builder()
				.workingDirectory(testClassRootPath)
				.classPath(classPath)
				.srcPath(srcPath)
				.build();
		
		Logger.debug(this.getClass(), "Classpath: " + classPath);
		Logger.debug(this.getClass(), "Srcpath: " + srcPath);
		Logger.debug(this.getClass(), "Working directory: " + testClassRootPath);
	}
	
	/**
	 * Gets root path directory from a class package. 
	 * <h2>Example</h2>
	 * <li>Class path: <code>C:/myProgram/src/name1/name2/name3</code></li>
	 * <li>Class package: <code>name1.name2.name3</code></li>
	 * <li><b>Return:</b> <code>C:/myProgram/src/</code></li>
	 * 
	 * @param		classPath Class path
	 * @param		classPackage Class package
	 * 
	 * @return		Root path directory
	 * 
	 * @throws		IllegalStateException If source file path is null
	 */
	private Path extractRootPathDirectory(Path classPath, String classPackage) {
		if (classPath == null) 
			throw new IllegalStateException("Source file path cannot be null");

		int packageFolders = 0;
		
		if (!classPackage.isEmpty()) {
			packageFolders = classPackage.split("\\.").length;
		}
		
		classPath = classPath.getParent();
		
		for (int i=0; i<packageFolders; i++) {
			classPath = classPath.getParent();
		}
		
		return classPath;
	}
	
	private List<Path> getSourcePath(Invoked invokedInfo, 
			Invoked testMethodInfo) {
		List<Path> srcPath = new ArrayList<>();
		
		srcPath.add(invokedInfo.getSrcPath());
		srcPath.add(testMethodInfo.getSrcPath());
		
		Path srcRootPath = extractRootPathDirectory(
				invokedInfo.getSrcPath(), 
				invokedInfo.getPackage()
				);
		srcPath.add(srcRootPath);
		
		Path testMethodSrcPath = extractRootPathDirectory(
				testMethodInfo.getSrcPath(), 
				testMethodInfo.getPackage()
				);
		srcPath.add(testMethodSrcPath);
		
		// Fix source file of anonymous and inner classes
		Path mavenSrcPath = App.getCurrentProjectRoot().resolve(
				Path.of("src", "main", "java")
		);
		srcPath.add(mavenSrcPath);
		
		Path mavenTestPath = App.getCurrentProjectRoot().resolve(
				Path.of("src", "test", "java")
		);
		
		if (testMethodInfo.getSrcPath().equals(invokedInfo.getSrcPath())) {
			srcPath.add(mavenTestPath);
		}
		
		return srcPath;
	}

	private List<Path> getClassPath() {
		List<Path> classPath = LibraryManager.getJavaClassPath();
		
		classPath.add(testMethod.getBinPath());
		classPath.add(testedInvoked.getBinPath());
		classPath.add(LibraryManager.getLibrary("JUNIT_4"));
		classPath.add(LibraryManager.getLibrary("HAMCREST"));
		
		return classPath;
	}
	
	
	//-------------------------------------------------------------------------
	//		Getters & Setters
	//-------------------------------------------------------------------------
	/**
	 * Gets invoked signature of the invoked analyzed by the analyzer.
	 * 
	 * @return		Invoked signature
	 */
	public String getAnalyzedInvokedSignature()	{
		return analyzedInvokedSignature.replace('$', '.');
	}
	
	/**
	 * Gets methods called by tested invoked. It will return signature of all 
	 * called methods from tested methods.
	 * 
	 * @return		Null if tested invoked does not call methods; otherwise, 
	 * returns list of signature of methods called by tested invoked
	 * 
	 * @implSpec	Only calling methods with the annotation '{@link 
	 * wniemiec.app.executionflow.runtime.CollectCalls}' will be considered 
	 */
	public Map<Invoked, Set<String>> getMethodsCalledByTestedInvoked() {
		return callCollector.getMethodsCalledByTestedInvoked();
	}
	
	public List<List<Integer>> getTestPaths() { 	
		return testPaths;
	}
	
	/**
	 * Enables 10-minute timeout.
	 */
	public void enableTimeout() {
		timeout = true;
	}
	
	/**
	 * Disables 10-minute timeout.
	 */
	public void disableTimeout() {
		timeout = false;
	}
	
	public void setTimeout(int ms) {
		if (ms < 0)
			throw new IllegalArgumentException("Timeout cannot be negative");
		
		timeoutTime = ms;
	}
	
	/**
	 * @return		True if runtime has been exceeded; false otherwise
	 */
	public boolean checkTimeout() {
		return timeout;
	}

	public boolean wasTestPathObtainedInALoop() {
		if (testPaths == null)
			return false;
		
		return testPaths.size() > 1;
	}

	public boolean hasTestPaths() {
		if (testPaths == null)
			return false;
		
		return	!testPaths.isEmpty() 
				&& !testPaths.get(0).isEmpty();
	}
}
