package executionFlow;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import executionFlow.core.file.InvokerManager;
import executionFlow.core.file.ParserType;
import executionFlow.exporter.ExporterExecutionFlow;
import executionFlow.info.CollectorInfo;
import executionFlow.info.SignaturesInfo;
import executionFlow.util.ConsoleOutput;


/**
 * Computes test path for collected invokers, where an invoker can be a method
 * or a constructor. This is the main class of the application.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		2.0.0
 * @since		1.0
 */
@SuppressWarnings("unused")
public abstract class ExecutionFlow 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	/**
	 * If true, displays collected invokers for each test method executed.
	 */
	protected static final boolean DEBUG;
	
	/**
	 * Sets if environment is development. This will affect
	 * {@link #getAppRootPath()} and 
	 * {@link executionFlow.core.file.FileCompiler#compile()}.
	 */
	private static final boolean DEVELOPMENT;
	
	/**
	 * Stores computed test paths from a class.<br />
	 * <ul>
	 * 	<li><b>Key:</b> Test method signature and invoker signature</li>
	 * 	<li><b>Value:</b> List of test paths</li>
	 * </ul>
	 */
	protected Map<SignaturesInfo, List<List<Integer>>> computedTestPaths;
	
	protected ExporterExecutionFlow exporter;
	
	/**
	 * Path of application libraries.
	 */
	private static Path libPath;
	
	private static String appRoot;
	private static File currentProjectRoot;
	public static InvokerManager invokerManager;
	public static InvokerManager testMethodManager;

	
	//-------------------------------------------------------------------------
	//		Initialization block
	//-------------------------------------------------------------------------
	/**
	 * Enables or disables debug. If activated, displays collected invokers for
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
		DEVELOPMENT = true;
	}
	

	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Walks the invoker recording its test paths and save the result in
	 * {@link #computedTestPaths}.
	 * 
	 * @return		This object to allow chained calls
	 */
	public abstract ExecutionFlow execute();
	
	/**
	 * Initializes method managers. If some error occurs, should stop the
	 * application execution; otherwise, the original files that have been 
	 * modified in the last run may be lost.
	 * 
	 * @return		If an error occurred
	 */
	public static boolean init()
	{
		boolean error = false;
		
		
		try {
			if (testMethodManager == null)
				testMethodManager = new InvokerManager(ParserType.TEST_METHOD, true);
		} catch (ClassNotFoundException e) {
			error = true;
			ConsoleOutput.showError("Class FileManager not found");
			e.printStackTrace();
		} catch (IOException e) {
			error = true;
			ConsoleOutput.showError("Could not recover the backup file of the test method");
			ConsoleOutput.showError("See more: https://github.com/williamniemiec/ExecutionFlow/wiki/Solu%C3%A7%C3%A3o-de-problemas#could-not-recover-all-backup-files");
			e.printStackTrace();
		}
		
		try {
			if (invokerManager == null)
				invokerManager = new InvokerManager(ParserType.INVOKER, true);
		} catch (ClassNotFoundException e) {
			error = true;;
			ConsoleOutput.showError("Class FileManager not found");
			e.printStackTrace();
		} catch (IOException e) {
			error = true;
			ConsoleOutput.showError("Could not recover all backup files for methods");
			ConsoleOutput.showError("See more: https://github.com/williamniemiec/ExecutionFlow/wiki/Solu%C3%A7%C3%A3o-de-problemas#could-not-recover-all-backup-files");
			e.printStackTrace();
		}
		
		return error;
	}
	
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
	 * Stores test paths for an invoker. The test paths are stored in 
	 * {@link #computedTestPaths}.
	 * 
	 * @param		testPaths Test paths of this invoker
	 * @param		collector Informations about this invoker
	 */
	protected abstract void storeTestPath(List<List<Integer>> testPaths, CollectorInfo collector);
	
	/**
	 * Computes and stores application root path, based on class 
	 * {@link ExecutionFlow} location.
	 * 
	 * @return		Application root path
	 * 
	 * @implSpec	Lazy initialization
	 */
	public static String getAppRootPath()
	{
		if (appRoot != null)
			return appRoot;
		
		try {
			File executionFlowBinPath = new File(ExecutionFlow.class.getProtectionDomain().getCodeSource().getLocation()
				    .toURI());
			appRoot = DEVELOPMENT ? executionFlowBinPath.getAbsoluteFile().getParent() : executionFlowBinPath.getAbsolutePath();

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		return appRoot;
	}
	
	/**
	 * Finds current project root (project that is running the application). It
	 * will return the path that contains a directory with name 'src'. 
	 * 
	 * @return		Project root path
	 * 
	 * @implSpec	Lazy initialization
	 */
	public static File getCurrentProjectRoot()
	{
		if (currentProjectRoot != null)
			return currentProjectRoot;
		
		String[] allFiles;
		boolean hasSrcFolder = false;
		int i=0;
		
		
		currentProjectRoot = new File(System.getProperty("user.dir"));
		
		// Searches for a path containing a directory named 'src'
		while (!hasSrcFolder) {
			allFiles = currentProjectRoot.list();
			
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
				currentProjectRoot = new File(currentProjectRoot.getParent());
			}
		}
		
		return currentProjectRoot;
	}
	
	/**
	 * Sets {@link #invokerManager} and {@link #testMethodManager} to null.
	 */
	public static void destroy()
	{
		invokerManager = null;
		testMethodManager = null;
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
		
		libPath = Path.of(appRoot + "\\lib");

		return libPath;
	}
	
	
	//-------------------------------------------------------------------------
	//		Getters & Setters
	//-------------------------------------------------------------------------
	/**
	 * Gets computed test path.It will return the following map:
	 * <ul>
	 * 	<li><b>Key:</b> Test method signature and invoker signature</li>
	 * 	<li><b>Value:</b> List of test paths</li>
	 * </ul>
	 * 
	 * @return		Computed test path
	 * 
	 * @implNote	It must only be called after method {@link #execute()} has 
	 * been executed
	 */
	public Map<SignaturesInfo, List<List<Integer>>> getTestPaths()
	{
		return computedTestPaths;
	}
	
	/**
	 * Gets a specific computed test path.
	 * 
	 * @param		testMethodSignature Test method signature
	 * @param		constructorSignature Invoker signature
	 * 
	 * @return		List of test paths for the specified invoker or empty list
	 * if specified invoker has not a test path
	 * 
	 * @implNote	It must only be called after method {@link #execute()} has 
	 * been executed
	 */
	public List<List<Integer>> getTestPaths(String testMethodSignature, String methodSignature)
	{
		return computedTestPaths.get(new SignaturesInfo(methodSignature, testMethodSignature));
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
}
