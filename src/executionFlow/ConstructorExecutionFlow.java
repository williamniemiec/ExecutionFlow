package executionFlow;

import java.io.IOException;
import java.nio.channels.InterruptedByTimeoutException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
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
import executionFlow.util.Logger;


/**
 * Generates data for collected constructors. Among these data:
 * <ul>
 * 	<li>Test path</li>
 * 	<li>Methods called by this method</li>
 * 	<li>Test methods that call this method</li>
 * </ul>
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.2
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
		Logger.debug("ConstructorExecutionFlow", "collector: " + constructorCollector.toString());
		// -----{ END DEBUG }-----
		
		FileManager constructorFileManager;
		FileManager testMethodFileManager;
		
		
		// Generates test path for each collected method
		for (CollectorInfo collector : constructorCollector) {
			// Gets FileManager for method file
			constructorFileManager = new FileManager(
				collector.getConstructorInfo().getClassSignature(),
				collector.getConstructorInfo().getSrcPath(), 
				collector.getConstructorInfo().getClassDirectory(),
				collector.getConstructorInfo().getPackage(),
				new InvokedFileProcessorFactory(),
				"invoked.bkp"
			);
			
			// Gets FileManager for test method file
			testMethodFileManager = new FileManager(
				collector.getTestMethodInfo().getClassSignature(),
				collector.getTestMethodInfo().getSrcPath(), 
				collector.getTestMethodInfo().getClassDirectory(),
				collector.getTestMethodInfo().getPackage(),
				new TestMethodFileProcessorFactory(),
				"testMethod.bkp"
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
				Logger.error("Time exceeded");
			} 
			catch (IllegalStateException e2) {
				Logger.error(e2.getMessage());
			}
			catch (IOException e3) {
				Logger.error(e3.getMessage());
				
				try {
					constructorFileManager.revertCompilation();
					constructorFileManager.revertParse();
					testMethodFileManager.revertCompilation();
					testMethodFileManager.revertParse();
				} 
				catch (IOException e) {
					Logger.error("An error occurred while restoring the original files - " + e.getMessage());
				}
			}
			
			updateCollectorInvocationLines(
					TestMethodFileProcessor.getMapping(), 
					collector.getTestMethodInfo().getSrcPath()
			);
			
			if (collector.getConstructorInfo().getSrcPath().equals(collector.getTestMethodInfo().getSrcPath())) {
				updateCollectorInvocationLines(
						InvokedFileProcessor.getMapping(), 
						collector.getTestMethodInfo().getSrcPath()
				);
			}
		}
		
		return this;
	}
	
	/**
	 * Updates the invocation line of constructor collector based on a mapping.
	 * 
	 * @param		mapping Mapping that will be used as base for the update
	 * @param		testMethodSrcFile Test method source file
	 */
	private void updateCollectorInvocationLines(Map<Integer, Integer> mapping, Path testMethodSrcFile)
	{
		int invocationLine;
		
		// Updates constructor invocation lines If it is declared in the 
		// same file as the processed test method file
		for (CollectorInfo cc : constructorCollector) {
			invocationLine = cc.getConstructorInfo().getInvocationLine();
			
			if (!cc.getTestMethodInfo().getSrcPath().equals(testMethodSrcFile) || 
					!mapping.containsKey(invocationLine))
				continue;
			
			cc.getConstructorInfo().setInvocationLine(mapping.get(invocationLine));
		}
	}
}
