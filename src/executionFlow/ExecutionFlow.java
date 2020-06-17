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
	
	public static MethodManager invokerManager;
	public static MethodManager testMethodManager;
	
	
	//-------------------------------------------------------------------------
	//		Initialization block
	//-------------------------------------------------------------------------
	/**
	 * Enables or disables debug. If activated, displays collected invokers for
	 * each test method executed.
	 */
	static {
		DEBUG = false;
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
			invokerManager = new MethodManager(ParserType.METHOD, true);
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
	 * Walks the invoker recording its test paths and save the result in
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
	 * Stores test paths for an invoker. The test paths are stored in 
	 * {@link #classTestPaths}.
	 * 
	 * @param		testPaths Test paths of this invoker
	 * @param		collector Informations about this invoker
	 */
	protected void storeTestPath(List<List<Integer>> testPaths, CollectorInfo collector)
	{
		List<List<Integer>> classPathInfo;
		SignaturesInfo signaturesInfo = new SignaturesInfo(
			collector.getConstructorInfo().getInvokerSignature(), 
			collector.getTestMethodInfo().getInvokerSignature()
		);

		for (List<Integer> testPath : testPaths) {
			// Checks if test path belongs to a stored test method and method
			if (classTestPaths.containsKey(signaturesInfo)) {
				classPathInfo = classTestPaths.get(signaturesInfo);
				classPathInfo.add(testPath);
			} 
			// Else stores test path with its test method and method
			else {	
				classPathInfo = new ArrayList<>();
				classPathInfo.add(testPath);
				classTestPaths.put(signaturesInfo, classPathInfo);
			}
		}
		
		// If test path is empty, stores test method and invoker with an empty list
		if (testPaths.isEmpty() || testPaths.get(0).isEmpty()) {
			classPathInfo = new ArrayList<>();
			classPathInfo.add(new ArrayList<>());
			classTestPaths.put(signaturesInfo, classPathInfo);
		}
	}
	
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
	 * 	<li><b>Key:</b> Test method signature and invoker signature</li>
	 * 	<li><b>Value:</b> List of test paths</li>
	 * </ul>
	 * 
	 * @return		Computed test path
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
	 * @return		This object to allow chained calls
	 */
	public ExecutionFlow setExporter(ExporterExecutionFlow exporter) 
	{
		this.exporter = exporter;
		
		return this;
	}
}
