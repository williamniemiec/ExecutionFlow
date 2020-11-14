package executionFlow;

import java.io.IOException;
import java.nio.channels.InterruptedByTimeoutException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import executionFlow.exporter.file.ProcessedSourceFileExporter;
import executionFlow.exporter.signature.MethodsCalledByTestedInvokedExporter;
import executionFlow.exporter.testpath.ConsoleExporter;
import executionFlow.exporter.testpath.FileExporter;
import executionFlow.exporter.testpath.TestPathExportType;
import executionFlow.info.CollectorInfo;
import executionFlow.io.FileManager;
import executionFlow.io.processor.InvokedFileProcessor;
import executionFlow.io.processor.TestMethodFileProcessor;
import executionFlow.io.processor.factory.InvokedFileProcessorFactory;
import executionFlow.io.processor.factory.TestMethodFileProcessorFactory;
import executionFlow.runtime.collector.MethodCollector;
import executionFlow.util.Logger;


/**
 * Generates data for collected methods. Among these data:
 * <ul>
 * 	<li>Test path</li>
 * 	<li>Methods called by this method</li>
 * 	<li>Test methods that call this method</li>
 * </ul>
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.0
 * @since		2.0.0
 */
public class MethodExecutionFlow extends ExecutionFlow
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	/**
	 * Collected methods from {@link executionFlow.runtime.MethodCollector}.
	 * <ul>
	 * 	<li><b>Key:</b> Method invocation line</li>
	 * 	<li><b>Value:</b> List of methods invoked from this line</li>
	 * <ul> 
	 */
	private Map<Integer, List<CollectorInfo>> methodCollector;

	
	
	//-------------------------------------------------------------------------
	//		Initialization block
	//-------------------------------------------------------------------------
	/**
	 * Defines how the export will be done.
	 */
	{
		if (isDevelopment()) {
			exporter = EXPORT.equals(TestPathExportType.CONSOLE) ? new ConsoleExporter() : 
				new FileExporter("examples\\results", false);
		}
		else {
			exporter = EXPORT.equals(TestPathExportType.CONSOLE) ? new ConsoleExporter() : 
				new FileExporter("results", false);
		}
	}
	
	
	//-------------------------------------------------------------------------
	//		Constructors
	//-------------------------------------------------------------------------
	/**
	 * Computes test path for collected methods.
	 * 
	 * @param		collectedMethods Collected methods from {@link MethodCollector}
	 */
	public MethodExecutionFlow(Map<Integer, List<CollectorInfo>> collectedMethods)
	{
		this(collectedMethods, true);
	}
	
	/**
	 * Computes test path for collected methods. Using this constructor,
	 * the invoked methods by tested constructor will be exported to a CSV file.
	 * 
	 * @param		collectedMethods Collected methods from {@link MethodCollector}
	 * @param		exportCalledMethods If true, signature of methods called by tested 
	 * method will be exported to a CSV file
	 */
	public MethodExecutionFlow(Map<Integer, List<CollectorInfo>> collectedMethods, boolean exportCalledMethods)
	{
		this.methodCollector = collectedMethods;
		this.exportCalledMethods = exportCalledMethods;
		
		computedTestPaths = new HashMap<>();
		
		
		if (isDevelopment()) {
			invokedMethodsExporter = new MethodsCalledByTestedInvokedExporter(
					"MethodsCalledByTestedMethod", "examples\\results"
			);
			
			processedSourceFileExporter = new ProcessedSourceFileExporter("examples\\results");
		}
		else {
			invokedMethodsExporter = new MethodsCalledByTestedInvokedExporter(
					"MethodsCalledByTestedMethod", "results"
			);
			
			processedSourceFileExporter = new ProcessedSourceFileExporter("results");
		}
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	public ExecutionFlow execute()
	{
		if (methodCollector == null || methodCollector.isEmpty())
			return this;
		
		// -----{ DEBUG }-----
		Logger.debug("MethodExecutionFlow", "collector: " + methodCollector.toString());
		// -----{ END DEBUG }-----
		
		boolean gotoNextLine = false;
		List<List<Integer>> tp;
		FileManager methodFileManager, testMethodFileManager;
		
		
		// Generates test path for each collected method
		for (List<CollectorInfo> collectors : methodCollector.values()) {
			gotoNextLine = false;
			
			// Computes test path for each collected method that is invoked in the same line
			for (CollectorInfo collector : collectors) {
				if (gotoNextLine)
					break;
				
				// Gets FileManager for method file
				methodFileManager = new FileManager(
					collector.getMethodInfo().getClassSignature(),
					collector.getMethodInfo().getSrcPath(), 
					collector.getMethodInfo().getClassDirectory(),
					collector.getMethodInfo().getPackage(),
					new InvokedFileProcessorFactory()
				);

				// Gets FileManager for test method file
				testMethodFileManager = new FileManager(
					collector.getTestMethodInfo().getClassSignature(),
					collector.getTestMethodInfo().getSrcPath(), 
					collector.getTestMethodInfo().getClassDirectory(),
					collector.getTestMethodInfo().getPackage(),
					new TestMethodFileProcessorFactory()
				);
				
				try {
					tp = run(
						collector.getTestMethodInfo(), 
						testMethodFileManager, 
						collector.getMethodInfo(), 
						methodFileManager
					);
					
					// Checks whether test path was generated inside a loop
					gotoNextLine = tp.size() > 1;
					
					updateCollectorInvocationLines(
							TestMethodFileProcessor.getMapping(), 
							collector.getTestMethodInfo().getSrcPath()
					);
					
					if (collector.getMethodInfo().getSrcPath().equals(collector.getTestMethodInfo().getSrcPath())) {
						updateCollectorInvocationLines(
								InvokedFileProcessor.getMapping(), 
								collector.getTestMethodInfo().getSrcPath()
						);
					}
				} 
				catch (InterruptedByTimeoutException e1) {
					Logger.error("Time exceeded");
				} 
				catch (IOException e2) {
					Logger.error(e2.getMessage());
					e2.printStackTrace();
				}
			}
		}
		
		return this;
	}
	
	/**
	 * Updates the invocation line of method collector based on a mapping.
	 * 
	 * @param		mapping Mapping that will be used as base for the update
	 * @param		testMethodSrcFile Test method source file
	 */
	private void updateCollectorInvocationLines(Map<Integer, Integer> mapping, Path testMethodSrcFile)
	{
		int invocationLine;

		
		// Updates method invocation lines If it is declared in the 
		// same file as the processed test method file
		for (List<CollectorInfo> methodCollectorList : methodCollector.values()) {
			for (CollectorInfo mc : methodCollectorList) {
				invocationLine = mc.getMethodInfo().getInvocationLine();
				
				if (!mc.getTestMethodInfo().getSrcPath().equals(testMethodSrcFile) || 
						!mapping.containsKey(invocationLine))
					continue;
				
				mc.getMethodInfo().setInvocationLine(mapping.get(invocationLine));
			}
		}
	}
}
