package executionFlow;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import executionFlow.core.JDB;
import executionFlow.core.file.FileManager;
import executionFlow.core.file.InvokerManager;
import executionFlow.core.file.ParserType;
import executionFlow.core.file.parser.factory.InvokerFileParserFactory;
import executionFlow.core.file.parser.factory.TestMethodFileParserFactory;
import executionFlow.exporter.*;
import executionFlow.info.CollectorInfo;
import executionFlow.info.SignaturesInfo;
import executionFlow.runtime.MethodCollector;


/**
 * Computes test path for collected invokers, where an invoker can be a method
 * or a constructor. This is the main class of the application.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		1.5
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
	 * Stores computed test paths from a class.<br />
	 * <ul>
	 * 	<li><b>Key:</b> Test method signature and invoker signature</li>
	 * 	<li><b>Value:</b> List of test paths</li>
	 * </ul>
	 */
	protected Map<SignaturesInfo, List<List<Integer>>> classTestPaths;
	
	protected ExporterExecutionFlow exporter;
	
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
	 * Initializes method managers. If some exception is thrown, stop the
	 * execution, otherwise the original files that have been modified in the 
	 * last run may be lost.
	 */
	static {
		boolean error = false;
		
		try {
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
		
		if (error)
			System.exit(-1);
	}
	

	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Walks the invoker recording its test paths and save the result in
	 * {@link #classTestPaths}.
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
		
		exporter.export(classTestPaths);
	}
	
	/**
	 * Stores test paths for an invoker. The test paths are stored in 
	 * {@link #classTestPaths}.
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
			appRoot = new File(ExecutionFlow.class.getProtectionDomain().getCodeSource().getLocation()
				    .toURI()).getPath();
			
			appRoot = appRoot.charAt(appRoot.length()-1) == '.' ? 
					Path.of(appRoot).getParent().getParent().toAbsolutePath().toString() : 
					Path.of(appRoot).getParent().toAbsolutePath().toString();
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
	public Map<SignaturesInfo, List<List<Integer>>> getClassTestPaths()
	{
		return classTestPaths;
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
