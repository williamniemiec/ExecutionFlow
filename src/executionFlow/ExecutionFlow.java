package executionFlow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import executionFlow.core.JDB;
import executionFlow.core.file.FileManager;
import executionFlow.core.file.parser.factory.MethodFileParserFactory;
import executionFlow.core.file.parser.factory.TestMethodFileParserFactory;
import executionFlow.exporter.ConsoleExporter;
import executionFlow.exporter.ExporterExecutionFlow;
import executionFlow.exporter.FileExporter;
import executionFlow.info.CollectorInfo;
import executionFlow.info.SignaturesInfo;


/**
 * Computes test path for collected methods. This is the main class of the 
 * application.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		1.4
 * @since		1.0
 */
@SuppressWarnings("unused")
public class ExecutionFlow 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	/**
	 * Stores computed test paths from a class.<br />
	 * <ul>
	 * 		<li><b>Key:</b> test_method_signature + '$' + method_signature</li>
	 * 		<li>
	 * 			<b>Value:</b> 
	 * 			<ul>
	 * 				<li><b>Key:</b> Test method signature and method signature</li>
	 * 				<li><b>Value:</b> Test path</li>
	 * 			</ul>
	 * 		</li>
	 * </ul>
	 */
	private Map<String, Map<SignaturesInfo, List<Integer>>> classTestPaths;
	
	private ExporterExecutionFlow exporter;
	
	/**
	 * Collected methods from {@link MethodCollector}.
	 * <ul>
	 * 		<li><b>Key:</b> Method invocation line</li>
	 * 		<li><b>Value:</b> List of methods invoked from this line</li>
	 * <ul> 
	 */
	private Map<Integer, List<CollectorInfo>> collectedMethods;
	
	private final boolean DEBUG; 
	
	
	//-------------------------------------------------------------------------
	//		Initialization block
	//-------------------------------------------------------------------------
	/**
	 * Enables or disables debug. If activated, displays collected methods for
	 * each test method executed.
	 */
	{
		DEBUG = false;
	}
	
	/**
	 * Defines how the export will be done.
	 */
	{
		exporter = new ConsoleExporter();
		//exporter = new FileExporter("testPaths");
	}
	
	
	//-------------------------------------------------------------------------
	//		Constructors
	//-------------------------------------------------------------------------
	/**
	 * Computes test path for collected methods.
	 * 
	 * @param		collectedMethods Collected methods from {@link MethodCollector}
	 */
	public ExecutionFlow(Map<Integer, List<CollectorInfo>> collectedMethods)
	{
		this.collectedMethods = collectedMethods;
		
		classTestPaths = new HashMap<>();
	}
	

	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Walks the method recording its test paths and save the result in
	 * {@link #classTestPaths}.
	 * 
	 * @return		Itself (to allow chained calls)
	 * @throws		Throwable If an error occurs
	 */
	public ExecutionFlow execute() throws Throwable
	{
		// -----{ DEBUG }-----
		if (DEBUG) {
			ConsoleOutput.showDebug("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
			ConsoleOutput.showDebug(collectedMethods.values().toString());
			ConsoleOutput.showDebug("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
		}
		// -----{ END DEBUG }-----
		
		List<List<Integer>> tp_jdb;
		
		// Generates test path for each collected method
		for (List<CollectorInfo> collectors : collectedMethods.values()) {
			// Computes test path for each collected method that is invoked in the same line
			for (CollectorInfo collector : collectors) {
				// Checks if collected method is within test method
				if (collector.getMethodInfo().getClassPath().equals(collector.getMethodInfo().getTestClassPath())) {
					ConsoleOutput.showError("The method to be tested cannot be within the test class");
					ConsoleOutput.showError("This test path will be skipped");
					continue;
				}
				
				// Gets FileManager for method file
				FileManager methodFileManager = new FileManager(
					collector.getMethodInfo().getSrcPath(), 
					collector.getMethodInfo().getClassDirectory(),
					collector.getMethodInfo().getPackage(),
					new MethodFileParserFactory()
				);
				
				// Gets FileManager for test method file
				FileManager testMethodFileManager = new FileManager(
					collector.getMethodInfo().getTestSrcPath(), 
					collector.getMethodInfo().getTestClassDirectory(),
					collector.getMethodInfo().getTestClassPackage(),
					new TestMethodFileParserFactory()
				);
				
				try {
					ConsoleOutput.showInfo("Processing source file of method" 
						+ collector.getMethodInfo().getMethodSignature()+"...");
					
					methodFileManager.parseFile().compileFile();
					
					ConsoleOutput.showInfo("Processing source file of test method "
						+ collector.getMethodInfo().getTestMethodSignature()+"...");
					
					testMethodFileManager.parseFile()
										 .createClassBackupFile()
										 .compileFile();
					
					ConsoleOutput.showInfo("Processing completed");
					
					// Computes test path from JDB
					ConsoleOutput.showInfo("Computing test path of method "
						+ collector.getMethodInfo().getMethodSignature()+"...");

					JDB jdb = new JDB(collector.getOrder());					
					tp_jdb = jdb.getTestPaths(collector.getMethodInfo());
					
					if (tp_jdb.isEmpty() || tp_jdb.get(0).isEmpty())
						ConsoleOutput.showWarning("Test path is empty");
					else
						ConsoleOutput.showInfo("Test path has been successfully computed");
					
					// Stores each computed test path
					storeTestPath(tp_jdb, collector);
				} catch (Exception e) {
					ConsoleOutput.showError(e.getMessage());
					e.printStackTrace();
				} finally {
					// Reverts parsed file to its original state
					methodFileManager.revertParse();
					testMethodFileManager.revertParse().revertCompilation();
				}
			}
		}
		
		return this;
	}
	
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
	private void storeTestPath(List<List<Integer>> testPaths, CollectorInfo collector)
	{
		Map<SignaturesInfo, List<Integer>> classPathInfo;
		
		for (List<Integer> testPath : testPaths) {
			String key = collector.getMethodInfo().extractSignatures().toString();
			
			// Checks if test path belongs to a stored test method and method
			if (classTestPaths.containsKey(key)) {
				classPathInfo = classTestPaths.get(key);
				classPathInfo.put(collector.getMethodInfo().extractSignatures(), testPath);
			} else {	// Else, stores test path with its test method and method
				classPathInfo = new HashMap<>();
				classPathInfo.put(collector.getMethodInfo().extractSignatures(), testPath);
				classTestPaths.put(key, classPathInfo);
			}
		}
		
		if (testPaths.isEmpty()) {
			classPathInfo = new HashMap<>();
			classPathInfo.put(collector.getMethodInfo().extractSignatures(), new ArrayList<Integer>());
			classTestPaths.put(collector.getMethodInfo().extractSignatures().toString(), classPathInfo);
		}
	}
	
	
	//-------------------------------------------------------------------------
	//		Getters & Setters
	//-------------------------------------------------------------------------
	/**
	 * Gets computed test path. It will return the following map:
	 * <ul>
	 * 		<li><b>Key:</b> test_method_signature + '$' + method_signature</li>
	 * 		<li>
	 * 			<b>Value:</b> 
	 * 			<ul>
	 * 				<li><b>Key:</b> Test method signature and method signature</li>
	 * 				<li><b>Value:</b> Test path</li>
	 * 			</ul>
	 * 		</li>
	 * </ul>
	 * 
	 * @return		Computed test path
	 */
	public Map<String, Map<SignaturesInfo, List<Integer>>> getClassTestPaths()
	{
		return classTestPaths;
	}
	
	public ExecutionFlow setExporter(ExporterExecutionFlow exporter) 
	{
		this.exporter = exporter;
		
		return this;
	}
}
