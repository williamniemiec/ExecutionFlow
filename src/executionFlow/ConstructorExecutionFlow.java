package executionFlow;

import java.io.IOException;
import java.nio.channels.InterruptedByTimeoutException;
import java.util.Collection;
import java.util.HashMap;

import executionFlow.exporter.ConsoleExporter;
import executionFlow.exporter.FileExporter;
import executionFlow.exporter.MethodsCalledByTestedInvokedExporter;
import executionFlow.exporter.ProcessedSourceFileExporter;
import executionFlow.exporter.TestPathExportType;
import executionFlow.info.CollectorInfo;
import executionFlow.io.FileManager;
import executionFlow.io.processor.factory.InvokedFileProcessorFactory;
import executionFlow.io.processor.factory.TestMethodFileProcessorFactory;
import executionFlow.util.Logging;


/**
 * Generates data for collected constructors. Among these data:
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
public class ConstructorExecutionFlow extends ExecutionFlow
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	/**
	 * Stores information about collected constructors.
	 */
	private Collection<CollectorInfo> constructorCollector;
	
	
	
	//-------------------------------------------------------------------------
	//		Initialization block
	//-------------------------------------------------------------------------
	/**
	 * Defines how the export will be done.
	 */
	{
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
	//		Constructors
	//-------------------------------------------------------------------------
	/**
	 * Computes test path for collected constructors. Using this constructor,
	 * methods called by tested constructor will be exported to a CSV file.
	 * 
	 * @param		constructorCollector Collected constructors from 
	 * {@link executionFlow.runtime.ConstructorCollector}
	 */
	public ConstructorExecutionFlow(Collection<CollectorInfo> constructorCollector)
	{
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
	public ConstructorExecutionFlow(Collection<CollectorInfo> constructorCollector, boolean exportCalledMethods)
	{
		this.constructorCollector = constructorCollector;
		this.exportCalledMethods = exportCalledMethods;
		
		computedTestPaths = new HashMap<>();
		
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
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	public ExecutionFlow execute()
	{
		if (constructorCollector == null || constructorCollector.isEmpty())
			return this;
		
		// -----{ DEBUG }-----
		Logging.showDebug("ConstructorExecutionFlow", "collector: " + constructorCollector.toString());
		// -----{ END DEBUG }-----
		
		FileManager constructorFileManager, testMethodFileManager;
		
		
		// Generates test path for each collected method
		for (CollectorInfo collector : constructorCollector) {
			// Gets FileManager for method file
			constructorFileManager = new FileManager(
				collector.getConstructorInfo().getClassSignature(),
				collector.getConstructorInfo().getSrcPath(), 
				collector.getConstructorInfo().getClassDirectory(),
				collector.getConstructorInfo().getPackage(),
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
				run(
					collector.getTestMethodInfo(), 
					testMethodFileManager, 
					collector.getConstructorInfo(), 
					constructorFileManager
				);

			} 
			catch (InterruptedByTimeoutException e1) {
				Logging.showError("Time exceeded");
			} 
			catch (IOException e2) {
				Logging.showError(e2.getMessage());
				e2.printStackTrace();
			}
		}
		
		return this;
	}
}
