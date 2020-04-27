package executionFlow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import executionFlow.core.CheapCoverage;
import executionFlow.core.JDB;
import executionFlow.core.TestPathManager;
import executionFlow.exporter.ConsoleExporter;
import executionFlow.exporter.ExporterExecutionFlow;
import executionFlow.info.ClassMethodInfo;
import executionFlow.info.CollectorInfo;
import executionFlow.info.SignaturesInfo;


/**
 * Given a class path and specific methods calculate the execution path for each
 * of these methods. This is the main class of execution flow.
 */
public class ExecutionFlow 
{
	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
	private Map<SignaturesInfo, List<Integer>> classPaths;
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
		TestPathManager testPathManager = new TestPathManager();
		List<List<Integer>> tp_cc, tp_jdb, testPaths;
		
		// Generates test path for each collected method
		for (List<CollectorInfo> collectors : collectedMethods.values()) {
			CollectorInfo collector = collectors.get(0);
			
			// Computes test path from CheapCoverage
			tp_cc = testPathManager.testPath_cc(collectors);
			
			// Computes test path from JDB
			tp_jdb = testPathManager.testPath_jdb(collector, lastLineTestMethod);
			
			// Merges test paths obtained from CheapCoverange and JDB
			testPaths = testPathManager.merge_cc_jdb(tp_cc, tp_jdb);
			
			// Stores each computed test path
			for (List<Integer> testPath : testPaths) {
				classPaths.put(collector.getMethodInfo().extractSignatures(), testPath);
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
	
	
	//-----------------------------------------------------------------------
	//		Getters & Setters
	//-----------------------------------------------------------------------
	/**
	 * Returns method's execution path.
	 * 
	 * @return Map where key is method's signature and value is method's test path
	 */
	public Map<String, List<Integer>> getClassPaths() 
	{
		Map<String, List<Integer>> response = new HashMap<>();
		
		for (Map.Entry<SignaturesInfo, List<Integer>> entry : classPaths.entrySet()) {
			SignaturesInfo signatures = entry.getKey();
			
			response.put(signatures.getMethodSignature(), entry.getValue());
		}
		
		return response; 
	}
	
	public ExecutionFlow setExporter(ExporterExecutionFlow exporter) 
	{
		this.exporter = exporter;
		
		return this;
	}
}
