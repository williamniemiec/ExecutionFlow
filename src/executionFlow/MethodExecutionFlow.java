package executionFlow;

import java.util.Collection;
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
import executionFlow.io.processor.factory.InvokedFileProcessorFactory;
import executionFlow.runtime.collector.MethodCollector;


/**
 * Generates data for collected methods. Among these data:
 * <ul>
 * 	<li>Test path</li>
 * 	<li>Methods called by this method</li>
 * 	<li>Test methods that call this method</li>
 * </ul>
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.3
 * @since		2.0.0
 */
public class MethodExecutionFlow extends ExecutionFlow {
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private Collection<List<CollectorInfo>> methodCollectors;
	

	//-------------------------------------------------------------------------
	//		Constructors
	//-------------------------------------------------------------------------
	/**
	 * Computes test path for collected methods.
	 * 
	 * @param		collectedMethods Collected methods from {@link MethodCollector}
	 */
	public MethodExecutionFlow(Map<Integer, List<CollectorInfo>> collectedMethods) {
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
	public MethodExecutionFlow(Map<Integer, List<CollectorInfo>> collectedMethods, boolean exportCalledMethods) {
		this.methodCollectors = collectedMethods.values();
		this.exportCalledMethods = exportCalledMethods;
		
		computedTestPaths = new HashMap<>();
		
		
		setMethodsCalledByTestedInvokedExporter();
		setTestPathExporter();
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	private void setMethodsCalledByTestedInvokedExporter() {
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
	
	private void setTestPathExporter() {
		if (isDevelopment()) {
			exporter = EXPORT.equals(TestPathExportType.CONSOLE) ? new ConsoleExporter() : 
				new FileExporter("examples\\results", false);
		}
		else {
			exporter = EXPORT.equals(TestPathExportType.CONSOLE) ? new ConsoleExporter() : 
				new FileExporter("results", false);
		}
	}
	
	protected Collection<List<CollectorInfo>> getCollectors() {
		return methodCollectors;
	}
	
	protected FileManager createInvokedFileManager(CollectorInfo collector) {
		return new FileManager(
			collector.getInvokedInfo().getClassSignature(),
			collector.getInvokedInfo().getSrcPath(), 
			collector.getInvokedInfo().getClassDirectory(),
			collector.getInvokedInfo().getPackage(),
			new InvokedFileProcessorFactory(),
			"invoked.bkp"
		);
	}
}
