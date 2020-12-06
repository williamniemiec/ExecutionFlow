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
import executionFlow.util.Logger;


/**
 * Computes the test path of a method and records the methods called by it.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.3
 * @since		2.0.0
 */
public abstract class Analyzer {
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	protected volatile static boolean timeout;
	protected String analyzedInvokedSignature = "";
	protected volatile List<List<Integer>> testPaths;
	protected JDB jdb;
	protected volatile boolean lock;
	protected InvokedInfo invoked;
	protected InvokedInfo testMethod;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------	
	/**
	 * Computes test path from code debugging along with methods called by it.
	 * 
	 * @throws		IOException If occurs an error while fetching dependencies
	 */
	protected Analyzer(InvokedInfo invokedInfo, InvokedInfo testMethodInfo) throws IOException	{
		this.invoked = invokedInfo;
		this.testMethod = testMethodInfo;

		initializeJDB(testMethodInfo, invokedInfo);
		
		testPaths = new ArrayList<>();
	}
	
	
	//-------------------------------------------------------------------------
	//		Factories
	//-------------------------------------------------------------------------	
	public static Analyzer createStandardTestPathAnalyzer(InvokedInfo invokedInfo, 
			InvokedInfo testMethodInfo) throws IOException
	{
		return new StandardJDBAnalyzer(invokedInfo, testMethodInfo);
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
		} catch (IOException e) {
			disableTimeout(timeoutID);
			closeJDB();
			
			throw e;
		}
		
		closeJDB();
		waitForTimeout();
		disableTimeout(timeoutID);
		
		return this;
	}
	
	protected abstract void run() throws IOException;
	
	private void startJDB() throws IOException {
		Logger.debug("Analyzer", "COMMAND: " + "clear ");
		Logger.debug("Analyzer", "COMMAND: " + "stop at " + testMethod.getClassSignature() + ":" + invoked.getInvocationLine());
		Logger.debug("Analyzer", "COMMAND: " + "run org.junit.runner.JUnitCore " + testMethod.getClassSignature());
		
		jdb.start().send("clear", "stop at " + testMethod.getClassSignature() + ":" + invoked.getInvocationLine(),
				"run org.junit.runner.JUnitCore "+testMethod.getClassSignature());
	}
	
	private void closeJDB() {
		Logger.debug("Analyzer", "COMMAND: " + "clear " + testMethod.getClassSignature() + ":" + invoked.getInvocationLine());
		Logger.debug("Analyzer", "COMMAND: " + "exit");
		
		jdb.send("clear " + testMethod.getClassSignature() + ":" + invoked.getInvocationLine(), "exit", "exit");
		jdb.quit();
	}

	private void waitForTimeout() {
		// Waits for timeout thread to finish running (if running)
		while (lock);
	}
	
	private void disableTimeout(final int TIMEOUT_ID) {
		Clock.clearTimeout(TIMEOUT_ID);
	}

	private void enableTimeout(final int TIMEOUT_ID, final int TIMEOUT_TIME) {
		timeout = false;
		
		Clock.setTimeout(() -> {
			File mcti = new File(ExecutionFlow.getAppRootPath().toFile(), "mcti.ef");
	
			lock = true;
			
			mcti.delete();
			jdb.quit();
			testPaths.clear();
			Analyzer.setTimeout(true);
			
			lock = false;
		}, TIMEOUT_ID, TIMEOUT_TIME);
	}
	
	private void initializeJDB(InvokedInfo testMethodInfo, InvokedInfo invokedInfo) {
		Path testClassRootPath = extractRootPathDirectory(testMethodInfo.getBinPath(), testMethodInfo.getPackage());
		
		List<Path> srcPath = getSourcePath(invokedInfo, testMethodInfo, testClassRootPath);
		List<Path> classPath = getClassPath(testClassRootPath);

		jdb = new JDB(testClassRootPath, classPath, srcPath);
		
		Logger.debug("Analyzer", "Classpath: " + classPath);
		Logger.debug("Analyzer", "Srcpath: " + srcPath);
		Logger.debug("Analyzer", "Working directory: " + testClassRootPath);
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

	private List<Path> getSourcePath(InvokedInfo invokedInfo, InvokedInfo testMethodInfo, Path testClassRootPath) {
		Path testMethodSrcPath;		// Root path where the source file of test method class is. It will be used as JDB root directory
		testMethodSrcPath = extractRootPathDirectory(testMethodInfo.getSrcPath(), testMethodInfo.getPackage());
		
		// Sets source path
		List<Path> srcPath = new ArrayList<>();
		// Root path where the source file of the invoked is
		Path srcRootPath = extractRootPathDirectory(invokedInfo.getSrcPath(), invokedInfo.getPackage());
		srcPath.add(testClassRootPath.relativize(srcRootPath));
		srcPath.add(testClassRootPath.relativize(testMethodSrcPath));
		
		// Fix source file of anonymous and inner classes
		srcPath.add(testClassRootPath.relativize(
				new File(ExecutionFlow.getCurrentProjectRoot().toFile(), "/src/main/java").toPath())
		);
		
		if (testMethodInfo.getSrcPath().equals(invokedInfo.getSrcPath())) {
			srcPath.add(
					testClassRootPath.relativize(
							new File(ExecutionFlow.getCurrentProjectRoot().toFile(), "/src/test/java").toPath())
			);
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
	@SuppressWarnings("unchecked")
	public Set<String> getMethodsCalledByTestedInvoked() {
		File f = new File(ExecutionFlow.getAppRootPath().toFile(), "mcti.ef");
		Map<String, Set<String>> invokedMethods = new HashMap<>();
		String invokedSignatureWithoutDollarSign = invoked.getInvokedSignature().replaceAll("\\$", ".");
		
		
		if (f.exists()) {
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
				invokedMethods = (Map<String, Set<String>>) ois.readObject();
			} 
			catch (IOException | ClassNotFoundException e) {
				invokedMethods = null;
				Logger.error("Called methods by tested invoked - " + e.getMessage());
				e.printStackTrace();
			}
		
			f.delete();
		}

		return invokedMethods.containsKey(invokedSignatureWithoutDollarSign) ? 
				invokedMethods.get(invokedSignatureWithoutDollarSign) : null;
	}
	
	/**
	 * Gets computed test path.
	 * 
	 * @return		Computed test path
	 */
	public List<List<Integer>> getTestPaths() { 	
		return testPaths;
	}
	
	/**
	 * Defines whether the execution time has been exceeded.
	 * 
	 * @param		status True if the execution time has been exceeded; false 
	 * otherwise
	 */
	public static void setTimeout(boolean status) {
		timeout = status;
	}
	
	/**
	 * Checks if the execution time has been exceeded.
	 * 
	 * @return		True if the execution time has been exceeded; false otherwise
	 */
	public static boolean getTimeout() {
		return timeout;
	}
}
