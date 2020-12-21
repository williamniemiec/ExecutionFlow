package executionFlow.analyzer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import executionFlow.ExecutionFlow;
import executionFlow.LibraryManager;
import executionFlow.info.InvokedInfo;
import executionFlow.util.Clock;
import executionFlow.util.JDB;
import executionFlow.util.logger.Logger;


/**
 * Computes the test path for a method or constructor using a debugger.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.3
 * @since		2.0.0
 */
public abstract class Analyzer {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	protected static volatile boolean timeout;
	protected volatile List<List<Integer>> testPaths;
	protected String analyzedInvokedSignature;
	protected Map<String, Set<String>> methodsCalledByTestedInvoked;
	protected InvokedInfo invoked;
	protected InvokedInfo testMethod;
	protected JDB jdb;
	protected boolean stopJDB;
	private List<String> commands;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------	
	/**
	 * Computes test path from code debugging along with methods called by it.
	 * 
	 * @throws		IOException If occurs an error while fetching dependencies
	 */
	protected Analyzer(InvokedInfo invokedInfo, InvokedInfo testMethodInfo) 
			throws IOException	{
		this.invoked = invokedInfo;
		this.testMethod = testMethodInfo;
		this.methodsCalledByTestedInvoked = new HashMap<>();
		this.analyzedInvokedSignature = "";

		initializeJDB(testMethodInfo, invokedInfo);
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
	public Analyzer analyze() throws IOException {
		if (jdb == null)
			throw new IllegalStateException("JDB has not been initialized");
		
		final int timeoutID = 1;
		final int timeoutTime = 10 * 60 * 1000;

		try {
			startJDB();
			enableTimeout(timeoutID, timeoutTime);
			run();
		}
		finally {
			disableTimeout(timeoutID);
			closeJDB();
		}
		
		mergeMethodsCalledByTestedInvoked();
		
		return this;
	}

	protected abstract void run() throws IOException;
	
	private void startJDB() throws IOException {
		jdb.start().send(buildInitCommand());
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
	
	private void initializeRunClass() {
		StringBuilder command = new StringBuilder();
		
		command.append("stop at");
		command.append(" ");
		command.append(testMethod.getClassSignature());
		command.append(":");
		command.append(invoked.getInvocationLine());
		
		commands.add(command.toString());
		
		Logger.debug(this.getClass(), "COMMAND: " + command.toString());
	}

	private void initializeBreakpoint() {
		StringBuilder command = new StringBuilder();
	
		command.append("run org.junit.runner.JUnitCore");
		command.append(" ");
		command.append(testMethod.getClassSignature());
		
		commands.add(command.toString());
		
		Logger.debug(this.getClass(), "COMMAND: " + command.toString());
	}

	private void closeJDB() {
		stopJDB = true;
		jdb.send(buildExitCommand());
		jdb.quit();
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
		command.append(invoked.getInvocationLine());
		
		commands.add(command.toString());
		
		Logger.debug(this.getClass(), "COMMAND: " + command.toString());
	}

	private void exitCommand() {
		commands.add("exit");
		commands.add("exit");
		
		Logger.debug(this.getClass(), "COMMAND: exit");
	}
	
	private void disableTimeout(final int TIMEOUT_ID) {
		Clock.clearTimeout(TIMEOUT_ID);
	}

	private void enableTimeout(final int TIMEOUT_ID, final int TIMEOUT_TIME) {
		timeout = false;
		
		Clock.setTimeout(() -> {
			File mcti = new File(ExecutionFlow.getAppRootPath().toFile(), "mcti.ef");

			mcti.delete();
			jdb.quit();
			testPaths.clear();
			Analyzer.setTimeout(true);
		}, TIMEOUT_ID, TIMEOUT_TIME);
	}
	
	private void initializeJDB(InvokedInfo testMethodInfo, InvokedInfo invokedInfo) {
		Path testClassRootPath = extractRootPathDirectory(
				testMethodInfo.getBinPath(), 
				testMethodInfo.getPackage()
		);
		
		List<Path> srcPath = getSourcePath(invokedInfo, testMethodInfo, testClassRootPath);
		List<Path> classPath = getClassPath(testClassRootPath);

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
	 * Deletes file containing methods called by tested invoked.
	 * 
	 * @return		If file has been successfully removed
	 */
	public boolean deleteMethodsCalledByTestedInvoked()	{
		return new File(ExecutionFlow.getAppRootPath().toFile(), "mcti.ef").delete();
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

	private List<Path> getClassPath(Path testClassRootPath) {
		List<Path> classPath = new ArrayList<>();
		
		for (String cp : System.getProperty("java.class.path").split(";")) {
			classPath.add(testClassRootPath.relativize(Path.of(cp)));
		}
		
		classPath.add(testClassRootPath.relativize(LibraryManager.getLibrary("JUNIT_4")));
		classPath.add(testClassRootPath.relativize(LibraryManager.getLibrary("HAMCREST")));
		
		return classPath;
	}

	private List<Path> getSourcePath(InvokedInfo invokedInfo, 
									 InvokedInfo testMethodInfo,
									 Path testClassRootPath) {
		List<Path> srcPath = new ArrayList<>();
		
		Path srcRootPath = extractRootPathDirectory(
				invokedInfo.getSrcPath(), 
				invokedInfo.getPackage()
		);
		srcPath.add(testClassRootPath.relativize(srcRootPath));
		
		Path testMethodSrcPath = extractRootPathDirectory(
				testMethodInfo.getSrcPath(), 
				testMethodInfo.getPackage()
		);
		srcPath.add(testClassRootPath.relativize(testMethodSrcPath));
		
		// Fix source file of anonymous and inner classes
		Path mavenSrcPath = ExecutionFlow.getCurrentProjectRoot().resolve(Path.of("src", "main", "java"));
		srcPath.add(testClassRootPath.relativize(mavenSrcPath));
		
		Path mavenTestPath = ExecutionFlow.getCurrentProjectRoot().resolve(Path.of("src", "test", "java"));
		if (testMethodInfo.getSrcPath().equals(invokedInfo.getSrcPath())) {
			srcPath.add(testClassRootPath.relativize(mavenTestPath));
		}
		
		return srcPath;
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
	 * @implSpec	After call this method, the file containing methods called
	 * by tested invoked will be deleted. Therefore, this method can only be 
	 * called once for each {@link #run JDB execution}
	 */
	public Map<String, Set<String>> getMethodsCalledByTestedInvoked() {
		return methodsCalledByTestedInvoked;
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Set<String>> loadMethodsCalledByTestedInvoked() {
		Map<String, Set<String>> invokedMethods = new HashMap<>();
		File file = new File(ExecutionFlow.getAppRootPath().toFile(), "mcti.ef");
		
		if (file.exists()) {
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
				invokedMethods = (Map<String, Set<String>>) ois.readObject();
			} 
			catch (IOException | ClassNotFoundException e) {
				Logger.error("Methods called by tested invoked - " + e.getMessage());
			}
		
			file.delete();
		}
		
		return invokedMethods;
	}
	
	private void mergeMethodsCalledByTestedInvoked() {
		Map<String, Set<String>> invokedMethods = loadMethodsCalledByTestedInvoked();
		
		for (Map.Entry<String, Set<String>> mcti : invokedMethods.entrySet()) {
			if (methodsCalledByTestedInvoked.containsKey(mcti.getKey())) {
				methodsCalledByTestedInvoked.get(mcti.getKey()).addAll(mcti.getValue());
			}
		}
	}
	
	public List<List<Integer>> getTestPaths() { 	
		return testPaths;
	}
	
	/**
	 * Enables 10-minute timeout.
	 */
	public static void enableTimeout() {
		timeout = true;
	}
	
	/**
	 * Disables 10-minute timeout.
	 */
	public static void disableTimeout() {
		timeout = false;
	}
	
	static void setTimeout(boolean status) {
		timeout = status;
	}
	
	/**
	 * @return		True if runtime has been exceeded; false otherwise
	 */
	public static boolean checkTimeout() {
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
		
		return !testPaths.isEmpty() && !testPaths.get(0).isEmpty();
	}
}
