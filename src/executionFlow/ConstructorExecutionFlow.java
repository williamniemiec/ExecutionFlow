package executionFlow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import executionFlow.exporter.file.ProcessedSourceFileExporter;
import executionFlow.exporter.signature.MethodsCalledByTestedInvokedExporter;
import executionFlow.exporter.testpath.ConsoleExporter;
import executionFlow.exporter.testpath.FileExporter;
import executionFlow.exporter.testpath.TestPathExportType;
import executionFlow.info.CollectorInfo;
import executionFlow.io.FileManager;
import executionFlow.io.processor.factory.InvokedFileProcessorFactory;


/**
 * Generates data for collected constructors. Among these data:
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
public class ConstructorExecutionFlow extends ExecutionFlow {
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private Collection<List<CollectorInfo>> collectors;	
	
	
	//-------------------------------------------------------------------------
	//		Constructors
	//-------------------------------------------------------------------------
	/**
	 * Computes test path for collected constructors. Using this constructor,
	 * methods called by tested constructor will be exported to a CSV file.
	 * 
	 * @param		constructorCollector Collected constructors from 
	 * {@link executionFlow.runtime.ConstructorCollector}
	 */
	public ConstructorExecutionFlow(Collection<CollectorInfo> constructorCollector) {
		this(constructorCollector, true);
	}
	
	/**
	 * Computes test path for collected constructors.
	 * 
	 * @param		constructorCollector Collected constructors from 
	 * {@link executionFlow.runtime.ConstructorCollector}
	 * @param		exportCalledMethods If true, signature of methods called by tested 
	 * constructor will be exported to a CSV file
	 */
	public ConstructorExecutionFlow(Collection<CollectorInfo> constructorCollector, boolean exportCalledMethods) {
		List<CollectorInfo> constructorCollectorList = new ArrayList<>(constructorCollector); 
		Collection<List<CollectorInfo>> c = new ArrayList<>();
		c.add(constructorCollectorList);
		this.collectors = c;
		
		this.exportCalledMethods = exportCalledMethods;
		
		computedTestPaths = new HashMap<>();
		
		setMethodsCalledByTestedInvokedExporter();
		setTestPathExporter();
	}


	private void setMethodsCalledByTestedInvokedExporter() {
		if (isDevelopment()) {
			invokedMethodsExporter = new MethodsCalledByTestedInvokedExporter(
					"MethodsCalledByTestedConstructor", "examples\\results"
			);
			
			processedSourceFileExporter = new ProcessedSourceFileExporter("examples\\results");
		}
		else {
			invokedMethodsExporter = new MethodsCalledByTestedInvokedExporter(
					"MethodsCalledByTestedConstructor", "results"
			);
			
			processedSourceFileExporter = new ProcessedSourceFileExporter("results");
		}
	}
	
	private void setTestPathExporter() {
		if (isDevelopment()) {
			exporter = EXPORT.equals(TestPathExportType.CONSOLE) ? new ConsoleExporter() : 
				new FileExporter("examples\\results", true);
		}
		else {
			exporter = EXPORT.equals(TestPathExportType.CONSOLE) ? new ConsoleExporter() : 
				new FileExporter("results", true);
		}
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	protected Collection<List<CollectorInfo>> getCollectors() {
		return collectors;
	}
	
	protected FileManager createInvokedFileManager(CollectorInfo collector) {
		// Gets FileManager for method file
		return new FileManager(
			collector.getConstructorInfo().getClassSignature(),
			collector.getConstructorInfo().getSrcPath(), 
			collector.getConstructorInfo().getClassDirectory(),
			collector.getConstructorInfo().getPackage(),
			new InvokedFileProcessorFactory(),
			"invoked.bkp"
		);
	}
}
