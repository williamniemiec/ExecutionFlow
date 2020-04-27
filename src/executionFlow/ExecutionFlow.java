package executionFlow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import executionFlow.core.CheapCoverage;
import executionFlow.core.MethodDebugger;
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
	private List<CollectorInfo> collectorInfo;
	private ExporterExecutionFlow exporter;
	private Map<Integer, List<CollectorInfo>> mc2;
	private int lastLineMethod;
	
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
	 * Given a class path and specific methods calculate the test path for each
	 * of these methods.
	 * 
	 * @param ci List of {@link CollectorInfo methods} to be analyzed
	 */
	public ExecutionFlow(Collection<CollectorInfo> ci) 
	{
		collectorInfo = new ArrayList<>();
		collectorInfo.addAll(ci);		// It is necessary to avoid ConcurrentModificationException
	}
	
	
	public ExecutionFlow(Map<Integer, List<CollectorInfo>> methodCollector2, int lastLineMethod)
	{
		this.mc2 = methodCollector2;
		this.lastLineMethod = lastLineMethod;
	}
	

	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	public ExecutionFlow execute() throws Throwable
	{
		TestPathManager testPathManager = new TestPathManager(lastLineMethod);
		List<List<Integer>> tp_cc, tp_jdb, testPaths;
		
		for(List<CollectorInfo> collectors : mc2.values()) {
			CollectorInfo collector = collectors.get(0);
			
			tp_cc = testPathManager.testPath_cc(collectors);
			tp_jdb = testPathManager.testPath_jdb(collector);
			
			// Merges tp_cc with tp_jdb
			testPaths = testPathManager.merge_cc_jdb(tp_cc, tp_jdb);
			
			for (List<Integer> testPath : testPaths) {
				classPaths.put(collector.getMethodInfo().extractSignatures(), testPath);
			}
		}
		
		return this;
	}
	
	
	public ExecutionFlow old2_execute() throws Throwable 
	{
		List<Integer> methodPath;
		MethodExecutionFlow mef;
		List<List<Integer>> tp_cc = new ArrayList<>();
		List<List<Integer>> tp_jdb;
		
		for(Map.Entry<Integer, List<CollectorInfo>> entry : mc2.entrySet())
		{
			methodPath = new ArrayList<>();
			// Call cc for each element of the list
			for (CollectorInfo collector : entry.getValue()) {
				
				CheapCoverage.loadClass(collector.getMethodInfo().getClassPath());
				tp_cc.add(CheapCoverage.getTestPath(collector.getMethodInfo(), collector.getConstructorInfo()));
			}
			
			// call jdb getting one method of this list
			CollectorInfo collector = entry.getValue().get(0);
			ClassMethodInfo mi = collector.getMethodInfo();
			MethodDebugger md = new MethodDebugger(mi.getClassPath(), lastLineMethod);
			
			tp_jdb = md.getTestPaths(mi);
			System.out.println("return to ExecutionFlow");
			System.out.println("tp_jdb: "+tp_jdb);
			System.out.println("tp_cc: "+tp_cc);
			
			// Merges tp_cc with tp_jdb
			// Only needs to compare the end of each test path
			for (int i=0; i<tp_jdb.size(); i++) {
				List<Integer> tp_jdb_merge = tp_jdb.get(i);
				List<Integer> tp_cc_merge = tp_cc.get(i);
				
				if (tp_jdb_merge.size() > 0) {
					Integer jdb_last = tp_jdb_merge.get(tp_jdb_merge.size()-1);
					Integer cc_last = tp_cc_merge.get(tp_cc_merge.size()-1);
					
					if (jdb_last != cc_last) {
						tp_jdb_merge.remove(tp_jdb_merge.size()-1);	// Removes last element
					}
				}
				
				// Saves result
				classPaths.put(collector.getMethodInfo().extractSignatures(), tp_jdb_merge);
			}
		}
		
		return this;
	}
	
	
	
	
	
	
	
	/**
	 * Walks the method recording its execution path and save the result in
	 * {@link #classPaths}.
	 * 
	 * @return The instance (to allow chained calls)
	 * @throws Throwable If an error occurs
	 */
	public ExecutionFlow old_execute() throws Throwable 
	{
		List<Integer> methodPath = new ArrayList<>();
		MethodExecutionFlow mef;
		//System.out.println("ci: "+collectorInfo);
		
		// Generates the test path for each method that was provided in the constructor
		for (CollectorInfo collector : collectorInfo) {
			methodPath = new ArrayList<>();
			
			mef = new MethodExecutionFlow(collector);
			
			methodPath.addAll(mef.execute().getMethodPath());
			classPaths.put(collector.getMethodInfo().extractSignatures(), methodPath);
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
