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
 * Given a class path and specific methods calculate the execution path for each
 * of these methods. This is the main class of execution flow.
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
	 * Defines how the export will be done and debug configuration.
	 */
	{
		DEBUG = false;
		classPaths = new HashMap<>();
		
		exporter = new ConsoleExporter(classPaths);
		//exporter = new FileExporter(classPaths);
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
		List<List<Integer>> tp_jdb;
		System.out.println("-=-=--=-=-=-=-=-");
		System.out.println(collectedMethods.values());
		System.out.println("-=-=--=-=-=-=-=-");
		System.out.println();
		
		// Generates test path for each collected method
		for (List<CollectorInfo> collectors : collectedMethods.values()) {
			// Computes test path for each collected method that is invoked in the same line
			// JDB will take care computing test path for each method that is invoked in the same line
			CollectorInfo collector = collectors.get(0);	
			//for (CollectorInfo collector : collectors) {
				// Parses file
				System.out.println("Processing source file...");
				FileManager methodFileManager = new FileManager(
					collector.getMethodInfo().getSrcPath(), 
					collector.getMethodInfo().getClassDirectory(),
					collector.getMethodInfo().getPackage(),
					new MethodFileParserFactory()
				);
				
				FileManager testMethodFileManager = new FileManager(
					collector.getMethodInfo().getTestSrcPath(), 
					collector.getMethodInfo().getTestClassDirectory(),
					collector.getMethodInfo().getTestClassPackage(),
					new TestMethodFileParserFactory()
				);
				
				try {
					methodFileManager.parseFile().compileFile();
					testMethodFileManager.parseFile().compileFile();
					System.out.println("Processing completed");
				} catch (Exception e) {
					System.out.println("[ERROR] "+e.getMessage());
				} finally {
					// Reverts parsed file to its original state
					methodFileManager.revert();
					testMethodFileManager.revert();
				}
				
				JDB jdb = new JDB(lastLineTestMethod, collector.getOrder());
				
				// Computes test path from JDB
				tp_jdb = jdb.getTestPaths(collector.getMethodInfo());
				
				// Stores each computed test path
				storeTestPath(tp_jdb, collector);
			//}
		}
		System.out.println("RETORNOU");
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
