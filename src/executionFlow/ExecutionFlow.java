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
import executionFlow.core.file.MethodManager;
import executionFlow.core.file.ParserType;
import executionFlow.core.file.parser.factory.MethodFileParserFactory;
import executionFlow.core.file.parser.factory.TestMethodFileParserFactory;
import executionFlow.exporter.*;
import executionFlow.info.CollectorInfo;
import executionFlow.info.SignaturesInfo;
import executionFlow.runtime.MethodCollector;


/**
 * Computes test path for collected invokers, where a invoker is a method or a
 * constructor. This is the main class of the application.
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
	 * If true, displays collected methods for each test method executed.
	 */
	protected static final boolean DEBUG;
	
	/**
	 * Stores computed test paths from a class.<br />
	 * <ul>
	 * 	<li><b>Key:</b> test_method_signature + '$' + method_signature</li>
	 * 	<li>
	 * 		<b>Value:</b> 
	 * 		<ul>
	 * 			<li><b>Key:</b> Test method signature and method signature</li>
	 * 			<li><b>Value:</b> Test path</li>
	 * 		</ul>
	 * 	</li>
	 * </ul>
	 */
	protected Map<String, Map<SignaturesInfo, List<Integer>>> classTestPaths;
	
	protected ExporterExecutionFlow exporter;
	
	public static MethodManager methodManager;
	public static MethodManager testMethodManager;
	
	
	//-------------------------------------------------------------------------
	//		Initialization block
	//-------------------------------------------------------------------------
	/**
	 * Enables or disables debug. If activated, displays collected methods for
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
			testMethodManager = new MethodManager(ParserType.TEST_METHOD, true);
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
			methodManager = new MethodManager(ParserType.METHOD, true);
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
	
	/**
	 * Defines how the export will be done.
	 */
	{
		exporter = new ConsoleExporter();
		//exporter = new FileExporter("testPaths");
	}
	

	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Walks the method recording its test paths and save the result in
	 * {@link #classTestPaths}.
	 * 
	 * @return		This object to allow chained calls
	 */
	public abstract ExecutionFlow execute();
	
	/**
	 * Exports the result.
	 */
	public void export() 
	{
		exporter.export(classTestPaths);
	}
	
	/**
	 * Stores test paths for a method. The test paths are stored in 
	 * {@link #classTestPaths}.
	 * 
	 * @param		testPaths Test paths of this method
	 * @param		collector Informations about this method
	 */
	protected abstract void storeTestPath(List<List<Integer>> testPaths, CollectorInfo collector);
	
	
	/**
	 * Computes and stores application root path, based on class 
	 * {@link ExecutionFlow} location.
	 * 
	 * @return		Application root path
	 */
	public static String getAppRootPath()
	{
		String response = null;
		
		try {
			response = new File(ExecutionFlow.class.getProtectionDomain().getCodeSource().getLocation()
				    .toURI()).getPath();
			
			response = response.charAt(response.length()-1) == '.' ? 
					Path.of(response).getParent().getParent().toAbsolutePath().toString() : 
					Path.of(response).getParent().toAbsolutePath().toString();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		return response;
	}
	
	
	//-------------------------------------------------------------------------
	//		Getters & Setters
	//-------------------------------------------------------------------------
	/**
	 * Gets computed test path.It will return the following map:
	 * <ul>
	 * 	<li><b>Key:</b> test_method_signature + '$' + method_signature</li>
	 * 	<li>
	 * 		<b>Value:</b> 
	 * 		<ul>
	 * 			<li><b>Key:</b> Test method signature and method signature</li>
	 * 			<li><b>Value:</b> Test path</li>
	 * 		</ul>
	 * 	</li>
	 * </ul>
	 * 
	 * @return		Computed test path
	 * 
	 * @implNote	It must only be called after method {@link #execute()} has 
	 * been executed
	 */
	public Map<String, Map<SignaturesInfo, List<Integer>>> getClassTestPaths()
	{
		return classTestPaths;
	}
	
	/**
	 * Changes exporter that will be used to export computed test path.
	 * 
	 * @param		exporter New exporter
	 * @return		This object to allow chained calls
	 */
	public ExecutionFlow setExporter(ExporterExecutionFlow exporter) 
	{
		this.exporter = exporter;
		
		return this;
	}
}
