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
 * Given a class path and specific methods calculate test path for each
 * of these methods. This is the main class of the application.
 */
@SuppressWarnings("unused")
public class ExecutionFlow 
{
	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
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
	private Map<String, Map<SignaturesInfo, List<Integer>>> classPaths;
	
	private ExporterExecutionFlow exporter;
	
	/**
	 * Collected methods from {@link MethodCollector}.
	 * <ul>
	 * 		<li><b>Key:</b> Method invocation line</li>
	 * 		<li><b>Value:</b> List of methods invoked from this line</li>
	 * <ul> 
	 */
	private Map<Integer, List<CollectorInfo>> collectedMethods;
	
	/**
	 * Last line of the test method where {@link #collectedMethods} are.
	 */
	private int lastLineTestMethod;
	
	private final boolean DEBUG; 
	
	
	//-----------------------------------------------------------------------
	//		Initialization block
	//-----------------------------------------------------------------------
	/**
	 * Defines how the export will be done.
	 */
	{
		classPaths = new HashMap<>();
		
		exporter = new ConsoleExporter(classPaths);
		//exporter = new FileExporter(classPaths);
	}
	
	/**
	 * Enables or disables debug. If activated, displays shell output during JDB 
	 * execution.
	 */
	{
		DEBUG = true;
	}
	
	
	//-----------------------------------------------------------------------
	//		Constructors
	//-----------------------------------------------------------------------	
	/**
	 * Given a class path and specific methods computes test path for each
	 * of these methods.
	 * 
	 * @param collectedMethods Collected methods from {@link MethodCollector}
	 * @param lastLineTestMethod Last line of the test method in which these methods are
	 */
	public ExecutionFlow(Map<Integer, List<CollectorInfo>> collectedMethods, int lastLineTestMethod)
	{
		this.collectedMethods = collectedMethods;
		this.lastLineTestMethod = lastLineTestMethod;
	}
	

	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	/**
	 * Walks the method recording its test paths and save the result in
	 * {@link #classPaths}.
	 * 
	 * @return Itself (to allow chained calls)
	 * @throws Throwable If an error occurs
	 */
	public ExecutionFlow execute() throws Throwable
	{
		// -----{ DEBUG }-----
		if (DEBUG) {
			System.out.println("-=-=-=-=-=-=-=-=-=-");
			System.out.println(collectedMethods.values());
			System.out.println("-=-=-=-=-=-=-=-=-=-");
			System.out.println();
		}
		// -----{ END DEBUG }-----
		
		List<List<Integer>> tp_jdb;
		
		// Generates test path for each collected method
		for (List<CollectorInfo> collectors : collectedMethods.values()) {
			// Computes test path for each collected method that is invoked in the same line
			for (CollectorInfo collector : collectors) {
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
					System.out.println("Processing source file of the method...");
					methodFileManager.parseFile().compileFile();
					
					System.out.println("Processing source file of the test method...");
					testMethodFileManager.parseFile()
										 .createClassBackupFile()
										 .compileFile();
					
					System.out.println("Processing completed");
					
					// Computes test path from JDB
					System.out.println("Computing test path...");
					JDB jdb = new JDB(lastLineTestMethod, collector.getOrder());					
					tp_jdb = jdb.getTestPaths(collector.getMethodInfo());
					System.out.println("Test path has been successfully computed");
					
					// Stores each computed test path
					storeTestPath(tp_jdb, collector);
				} catch (Exception e) {
					System.out.println("[ERROR] "+e.getMessage());
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
		exporter.export();
	}
	
	/**
	 * Stores test paths for a method.
	 * 
	 * @param testPaths Test paths of this method
	 * @param collector Informations about this method
	 */
	private void storeTestPath(List<List<Integer>> testPaths, CollectorInfo collector)
	{
		Map<SignaturesInfo, List<Integer>> classPathInfo;
		
		for (List<Integer> testPath : testPaths) {
			String key = collector.getMethodInfo().extractSignatures().toString();
			
			// Checks if test path belongs to a stored test method and method
			if (classPaths.containsKey(key)) {
				classPathInfo = classPaths.get(key);
				classPathInfo.put(collector.getMethodInfo().extractSignatures(), testPath);
			} else {	// Else, stores test path with its test method and method
				classPathInfo = new HashMap<>();
				classPathInfo.put(collector.getMethodInfo().extractSignatures(), testPath);
				classPaths.put(key, classPathInfo);
			}
		}
	}
	
	
	//-----------------------------------------------------------------------
	//		Getters & Setters
	//-----------------------------------------------------------------------
	public Map<String, Map<SignaturesInfo, List<Integer>>> getClassPaths()
	{
		return classPaths;
	}
	
	public ExecutionFlow setExporter(ExporterExecutionFlow exporter) 
	{
		this.exporter = exporter;
		
		return this;
	}
}
