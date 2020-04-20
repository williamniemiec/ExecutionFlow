package executionFlow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import executionFlow.exporter.ConsoleExporter;
import executionFlow.exporter.ExporterExecutionFlow;
import executionFlow.exporter.FileExporter;
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
	
	
	//-----------------------------------------------------------------------
	//		Initialization block
	//-----------------------------------------------------------------------
	/**
	 * Defines how the export will be done.
	 */
	{
		classPaths = new HashMap<>();
		
		//exporter = new ConsoleExporter(classPaths);
		exporter = new FileExporter(classPaths);
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
	

	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	/**
	 * Walks the method recording its execution path and save the result in
	 * {@link #classPaths}.
	 * 
	 * @return The instance (to allow chained calls)
	 * @throws Throwable If an error occurs
	 */
	public ExecutionFlow execute() throws Throwable 
	{
		List<Integer> methodPath = new ArrayList<>();
		MethodExecutionFlow mef;
		
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
