package executionFlow;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import executionFlow.exporter.*;
import executionFlow.info.CollectorInfo;
import executionFlow.info.ConstructorInvokedInfo;
import executionFlow.io.FileManager;
import executionFlow.io.processor.factory.InvokedFileProcessorFactory;
import executionFlow.io.processor.factory.TestMethodFileProcessorFactory;
import executionFlow.util.ConsoleOutput;
import executionFlow.util.Pair;


/**
 * Generates data for collected constructors. Among these data:
 * <ul>
 * 	<li>Test path</li>
 * 	<li>Methods called by this method</li>
 * 	<li>Test methods that call this method</li>
 * </ul>
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		4.0.0
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
	private boolean exportCalledMethods;
	
	
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
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	public ExecutionFlow execute()
	{
		// -----{ DEBUG }-----
		if (DEBUG) {
			ConsoleOutput.showDebug("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
			ConsoleOutput.showDebug("CEF: " + constructorCollector.toString());
			ConsoleOutput.showDebug("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
		}
		// -----{ END DEBUG }-----
		
		if (constructorCollector == null || constructorCollector.isEmpty())
			return this;
		
		List<List<Integer>> tp;
		FileManager constructorFileManager, testMethodFileManager;
		Analyzer analyzer;
		MethodsCalledByTestedInvokedExporter methodsCalledExporter = isDevelopment() ?
				new MethodsCalledByTestedInvokedExporter("MethodsCalledByTestedConstructor", "examples\\results") :
				new MethodsCalledByTestedInvokedExporter("MethodsCalledByTestedConstructor", "results");
		
		
		// Generates test path for each collected method
		for (CollectorInfo collector : constructorCollector) {
			// Checks if collected constructor is within test method
//			if (collector.getConstructorInfo().getSrcPath().equals(collector.getTestMethodInfo().getSrcPath())) {
//				ConsoleOutput.showError("ConstructorExecutionFlow - " + collector.getConstructorInfo().getInvokedSignature());
//				ConsoleOutput.showError("The constructor to be tested cannot be within the test class");
//				ConsoleOutput.showError("Anonymous classes are not supported");
//				ConsoleOutput.showError("This test path will be skipped");
//				continue;
//			}

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
				analyzer = analyze(
						collector.getTestMethodInfo(), testMethodFileManager, 
						collector.getConstructorInfo(), constructorFileManager,
						List.copyOf(constructorCollector)
				);
				tp = analyzer.getTestPaths();
				
				
				
				
				
				// Fixes anonymous class signature
				if (collector.getConstructorInfo().getInvokedSignature() != analyzer.getAnalyzedInvokedSignature()) {
					((ConstructorInvokedInfo)collector.getConstructorInfo())
						.setInvokedSignature(analyzer.getAnalyzedInvokedSignature());
				}
				
				
				
				
				
				
				if (tp.isEmpty() || tp.get(0).isEmpty())
					ConsoleOutput.showWarning("Test path is empty");
				else
					ConsoleOutput.showInfo("Test path has been successfully computed");
				
				// Stores each computed test path
				storeTestPath(tp, Pair.of(
						collector.getTestMethodInfo().getInvokedSignature(),
						collector.getConstructorInfo().getInvokedSignature()
				));
				
				// Exports called methods by tested constructor to a CSV
				if (exportCalledMethods) {
					methodsCalledExporter.export(
							collector.getConstructorInfo().getInvokedSignature(),
							analyzer.getMethodsCalledByTestedInvoked(), true
					);
				}
				else {
					analyzer.deleteMethodsCalledByTestedInvoked();
				}
			} catch (Exception e) {
				ConsoleOutput.showError(e.getMessage());
				e.printStackTrace();
			}
		}
		
		return this;
	}
}
