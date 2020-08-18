package executionFlow;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import executionFlow.exporter.ExporterExecutionFlow;
import executionFlow.exporter.TestPathExportType;
import executionFlow.info.CollectorInfo;
import executionFlow.info.InvokedInfo;
import executionFlow.io.FileManager;
import executionFlow.io.FilesManager;
import executionFlow.io.ProcessorType;
import executionFlow.util.ConsoleOutput;
import executionFlow.util.Pair;


/**
 * Generates data for each collected invoked, where an invoked can be a method
 * or a constructor. Among these data:
 * <ul>
 * 	<li>Test path</li>
 * 	<li>Methods called by this method</li>
 * 	<li>Test methods that call this method</li>
 * </ul>
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		3.2.0
 * @since		1.0
 */
@SuppressWarnings("unused")
public abstract class ExecutionFlow 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	/**
	 * If true, displays collected invoked for each test method executed.
	 */
	protected static final boolean DEBUG;
	
	protected static final TestPathExportType EXPORT;
	
	/**
	 * Sets if environment is development. This will affect
	 * {@link #getAppRootPath()} and 
	 * {@link executionFlow.io.FileCompiler#compile()}.
	 */
	private static final boolean DEVELOPMENT;
	
	/**
	 * Manages test method files.
	 */
	private static FilesManager testMethodManager;
	
	/**
	 * Manages invoked files.
	 */
	private static FilesManager invokedManager;
	
	/**
	 * Path of application libraries.
	 */
	private static Path libPath;
	
	private static Path appRoot;
	private static Path currentProjectRoot;
	
	/**
	 * Stores computed test paths from a class.<br />
	 * <ul>
	 * 	<li><b>Key:</b> {@link Pair} (test method signature, invoked signature)</li>
	 * 	<li><b>Value:</b> List of test paths</li>
	 * </ul>
	 */
	protected Map<Pair<String, String>, List<List<Integer>>> computedTestPaths;
	
	protected ExporterExecutionFlow exporter;

	
	//-------------------------------------------------------------------------
	//		Initialization block
	//-------------------------------------------------------------------------
	/**
	 * Enables or disables debug. If activated, displays collected invoked for
	 * each test method executed.
	 */
	static {
		DEBUG = true;
	}
	
	/**
	 * Sets environment. If the code is executed outside project, that is,
	 * through a jar file, it must be false.
	 */
	static {
		DEVELOPMENT = false;
	}
	
	/**
	 * Sets test path export type.
	 */
	static {
		EXPORT = TestPathExportType.CONSOLE;
//		EXPORT = TestPathExportType.FILE;
	}
	

	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Initializes invoked managers. If some error occurs, should stop the
	 * application execution; otherwise, the original files that have been 
	 * modified in the last run may be lost.
	 * 
	 * @param		restoreOriginalFiles If true and if there are backup files,
	 * restore them
	 * @throws		ClassNotFoundException If FileManager class has not been found
	 * @throws		IOException If backup files could not be restored
	 */
	public static void init(boolean restoreOriginalFiles) throws ClassNotFoundException, IOException
	{
		if (testMethodManager == null) {
			try {
					testMethodManager = new FilesManager(ProcessorType.TEST_METHOD, true, restoreOriginalFiles);
			} 
			catch (ClassNotFoundException e) {
				throw new ClassNotFoundException("Class FileManager not found");
			} 
			catch (IOException e) {
				throw new IOException(
						"Could not recover the backup file of the test method\n"
						+ "See more: https://github.com/williamniemiec/"
						+ "ExecutionFlow/wiki/Solu%C3%A7%C3%A3o-de-problemas"
						+ "#could-not-recover-all-backup-files"
				);
			}
		}

		if (invokedManager == null) {
			try {
				invokedManager = new FilesManager(ProcessorType.INVOKED, true, restoreOriginalFiles);
				
				// Loads files that have already been processed
				if (!restoreOriginalFiles)
					invokedManager.load();
			} 
			catch (ClassNotFoundException e) {
				throw new ClassNotFoundException("Class FileManager not found");
			} 
			catch (IOException e) {
				throw new IOException(
						"Could not recover all backup files for methods\n"
						+ "See more: https://github.com/williamniemiec/"
						+ "ExecutionFlow/wiki/Solu%C3%A7%C3%A3o-de-problemas"
						+ "#could-not-recover-all-backup-files"
				);
			}
		}
	}
	
	/**
	 * Sets {@link #testMethodManager} to null.
	 */
	public static void destroyTestMethodManager()
	{
		testMethodManager = null;
	}
	
	/**
	 * Sets {@link #invokedManager} to null.
	 */
	public static void destroyInvokedManager()
	{
		invokedManager = null;
	}
	
	/**
	 * Checks if it is development environment. If it is production environment,
	 * it will return false; otherwise, true.
	 * 
	 * @return		If it is development environment
	 */
	public static boolean isDevelopment()
	{
		return DEVELOPMENT;
	}
	
	/**
	 * Walks the invoked recording its test paths and save the result in
	 * {@link #computedTestPaths}.
	 * 
	 * @return		This object to allow chained calls
	 */
	public abstract ExecutionFlow execute();
	
	/**
	 * Exports the result.
	 * 
	 * @throws		IllegalArgumentException If exporter is null
	 */
	public void export() 
	{
		if (exporter == null)
			throw new IllegalArgumentException("Exporter cannot be null");
		
		exporter.export(computedTestPaths);
	}
	
	/**
	 * Stores test paths for an invoked. The test paths are stored in 
	 * {@link #computedTestPaths}.
	 * 
	 * @param		testPaths Test paths of this invoked
	 * @param		signaturesInfo Informations about test method along with 
	 * the invoked
	 */
	protected void storeTestPath(List<List<Integer>> testPaths, Pair<String, String> signaturesInfo)
	{
		List<List<Integer>> classTestPathInfo;

		
		for (List<Integer> testPath : testPaths) {
			// Checks if test path belongs to a stored test method and invoked
			if (computedTestPaths.containsKey(signaturesInfo)) {
				classTestPathInfo = computedTestPaths.get(signaturesInfo);
				classTestPathInfo.add(testPath);
			} 
			// Else stores test path with its test method and invoked
			else {	
				classTestPathInfo = new ArrayList<>();
				classTestPathInfo.add(testPath);
				computedTestPaths.put(signaturesInfo, classTestPathInfo);
			}
		}
		
		// If test path is empty, stores test method and invoked with an empty list
		if (testPaths.isEmpty() || testPaths.get(0).isEmpty()) {
			classTestPathInfo = new ArrayList<>();
			classTestPathInfo.add(new ArrayList<>());
			computedTestPaths.put(signaturesInfo, classTestPathInfo);
		}
	}
	
	/**
	 * Runs {@link Analyzer} on an invoked.
	 * 
	 * @param		testMethodInfo Test method information
	 * @param		testMethodFileManager Test method file information
	 * @param		invokedInfo Invoked information
	 * @param		invokedFileManager Invoked file information
	 * @param		collectors Information about all invoked collected
	 * 
	 * @return		Analyzer after finishing its execution
	 * 
	 * @throws		IOException If an error occurs during the instantiation of
	 * the analyzer
	 * @throws		IllegalStateException If test method manager is null
	 * 
	 * @implNote	This method will instantiate an Analyzer and call the 
	 * {@link Analyzer#run()} method
	 */
	protected Analyzer analyze(InvokedInfo testMethodInfo, FileManager testMethodFileManager, 
			InvokedInfo invokedInfo, FileManager invokedFileManager,
			List<CollectorInfo> collectors) throws IOException
	{
		if (testMethodManager == null)
			throw new IllegalStateException("testMethodManager cannot be null. "
					+ "Make sure that the 'init' method has been called before.");
		
		// Processes the source file of the test method if it has
		// not been processed yet
		if (!testMethodManager.wasProcessed(testMethodFileManager)) {
			ConsoleOutput.showInfo("Processing source file of test method "
				+ testMethodInfo.getInvokedSignature()+"...");
			
			testMethodManager.parse(testMethodFileManager).compile(testMethodFileManager);
			ConsoleOutput.showInfo("Processing completed");	
		}

		// Processes the source file of the method if it has not 
		// been processed yet
		if (!invokedManager.wasProcessed(invokedFileManager)) {
			boolean autoRestore = 
					!testMethodFileManager.getSrcFile().equals(invokedFileManager.getSrcFile());
			
			
			ConsoleOutput.showInfo("Processing source file of invoked - " 
				+ invokedInfo.getInvokedSignature()+"...");
			
			invokedManager.parse(invokedFileManager, collectors, autoRestore).compile(invokedFileManager);
			ConsoleOutput.showInfo("Processing completed");
		}
		
		// Computes test path from JDB
		ConsoleOutput.showInfo("Computing test path of method "
			+ invokedInfo.getInvokedSignature()+"...");

		return new Analyzer(invokedInfo, testMethodInfo).run();
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
	public static Path getCurrentProjectRoot()
	{
		if (currentProjectRoot != null)
			return currentProjectRoot;
		
		File tmpFile;
		String[] allFiles;
		boolean hasSrcFolder = false;
		int i=0;
		
		
		tmpFile = new File(System.getProperty("user.dir"));
		
		// Searches for a path containing a directory named 'src'
		while (!hasSrcFolder) {
			allFiles = tmpFile.list();
			
			// Checks the name of every file in current path
			i=0;
			while (!hasSrcFolder && i < allFiles.length) {
				// If there is a directory named 'src' stop the search
				if (allFiles[i].equals("src")) {
					hasSrcFolder = true;
				} else {
					i++;
				}
			}
			// If there is not a directory named 'src', it searches in the 
			// parent folder
			if (!hasSrcFolder) {
				tmpFile = new File(tmpFile.getParent());
			}
		}
		
		currentProjectRoot = tmpFile.toPath();
		
		return currentProjectRoot;
	}
	
	/**
	 * Gets application root path, based on class {@link ExecutionFlow} location.
	 * 
	 * @return		Application root path
	 * 
	 * @implSpec	Lazy initialization
	 */
	public static Path getAppRootPath()
	{
		if (appRoot != null)
			return appRoot;
		
		try {
			File executionFlowBinPath = new File(ExecutionFlow.class
					.getProtectionDomain().getCodeSource().getLocation().toURI());
			appRoot = DEVELOPMENT ? executionFlowBinPath.getAbsoluteFile().getParentFile().toPath() : 
				executionFlowBinPath.getAbsoluteFile().toPath();

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		return appRoot;
	}
	
	/**
	 * Finds directory of application libraries and stores it in {@link #libPath}.
	 * 
	 * @param		appRoot Application root path
	 * 
	 * @implSpec	Lazy initialization
	 */
	public static Path getLibPath()
	{
		if (libPath != null)
			return libPath;
		
		if (appRoot == null)
			getAppRootPath();
		
		libPath = Path.of(appRoot + "\\lib");

		return libPath;
	}
	
	public static FilesManager getTestMethodManager()
	{
		return testMethodManager;
	}
	
	public static FilesManager getInvokedManager()
	{
		return invokedManager;
	}
	
	/**
	 * Gets computed test path.It will return the following map:
	 * <ul>
	 * 	<li><b>Key:</b> Test method signature and invoked signature</li>
	 * 	<li><b>Value:</b> List of test paths</li>
	 * </ul>
	 * 
	 * @return		Computed test path
	 * 
	 * @implNote	It must only be called after method {@link #execute()} has 
	 * been executed
	 */
	public Map<Pair<String, String>, List<List<Integer>>> getTestPaths()
	{
		return computedTestPaths;
	}
	
	/**
	 * Gets a specific computed test path.
	 * 
	 * @param		testMethodSignature Test method signature
	 * @param		invokedSignature Invoked signature
	 * 
	 * @return		List of test paths for the specified invoked or empty list
	 * if specified invoked has not a test path
	 * 
	 * @implNote	It must only be called after method {@link #execute()} has 
	 * been executed
	 */
	public List<List<Integer>> getTestPaths(String testMethodSignature, String invokedSignature)
	{
		return computedTestPaths.get(Pair.of(testMethodSignature, invokedSignature));
	}
	
	/**
	 * Changes exporter that will be used to export computed test path.
	 * 
	 * @param		exporter New exporter
	 * 
	 * @return		This object to allow chained calls
	 */
	public ExecutionFlow setExporter(ExporterExecutionFlow exporter) 
	{
		this.exporter = exporter;
		
		return this;
	}
}
