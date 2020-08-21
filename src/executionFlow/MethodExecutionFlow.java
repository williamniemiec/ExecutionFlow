package executionFlow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import executionFlow.exporter.ConsoleExporter;
import executionFlow.exporter.FileExporter;
import executionFlow.exporter.MethodsCalledByTestedInvokedExporter;
import executionFlow.exporter.TestPathExportType;
import executionFlow.info.CollectorInfo;
import executionFlow.io.FileManager;
import executionFlow.io.processor.factory.InvokedFileProcessorFactory;
import executionFlow.io.processor.factory.TestMethodFileProcessorFactory;
import executionFlow.util.ConsoleOutput;
import executionFlow.util.Pair;


/**
 * Generates data for collected methods. Among these data:
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
			ConsoleOutput.showDebug("MEF: " + methodCollector.toString());
			ConsoleOutput.showDebug("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
		}
		// -----{ END DEBUG }-----
		
		if (methodCollector == null || methodCollector.isEmpty())
			return this;
		
		boolean gotoNextLine = false;
		List<List<Integer>> tp;
		FileManager methodFileManager, testMethodFileManager;
		Analyzer analyzer;
		MethodsCalledByTestedInvokedExporter invokedMethodsExporter = isDevelopment() ?
				new MethodsCalledByTestedInvokedExporter("MethodsCalledByTestedMethod", "examples\\results") :
				new MethodsCalledByTestedInvokedExporter("MethodsCalledByTestedMethod", "results");
		
		
		// Generates test path for each collected method
		for (List<CollectorInfo> collectors : methodCollector.values()) {
			gotoNextLine = false;
			
			// Computes test path for each collected method that is invoked in the same line
			for (CollectorInfo collector : collectors) {
				if (gotoNextLine)
					break;
				
				// Checks if collected method is within test method
//				if (collector.getMethodInfo().getSrcPath().equals(collector.getTestMethodInfo().getSrcPath())) {
//					ConsoleOutput.showError("MethodExecutionFlow - " + collector.getMethodInfo().getInvokedSignature());
//					ConsoleOutput.showError("The method to be tested cannot be within the test class");
//					ConsoleOutput.showError("This test path will be skipped");
//					continue;
//				}
//				
//				if (collector.getMethodInfo().belongsToAnonymousClass()) {
//					ConsoleOutput.showError("MethodExecutionFlow - " + collector.getMethodInfo().getInvokedSignature());
//					ConsoleOutput.showError("The method to be tested cannot belong to an anonymous class");
//					ConsoleOutput.showError("This test path will be skipped");
//					continue;
//				}
				
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
					analyzer = analyze(
							collector.getTestMethodInfo(), testMethodFileManager, 
							collector.getMethodInfo(), methodFileManager,
							collectors
					);
					tp = analyzer.getTestPaths();
					
					if (tp.isEmpty() || tp.get(0).isEmpty())
						ConsoleOutput.showWarning("Test path is empty");
					else
						ConsoleOutput.showInfo("Test path has been successfully computed");				

					// Checks whether test path was generated inside a loop
					if (tp.size() > 1) {
						gotoNextLine = true;
					}
					
					// Stores each computed test path
					storeTestPath(tp, Pair.of(
							collector.getTestMethodInfo().getInvokedSignature(),
							collector.getMethodInfo().getInvokedSignature()
					));
					
					// Exports methods called by tested method to a CSV
					if (exportCalledMethods) {
						invokedMethodsExporter.export(
								collector.getMethodInfo().getInvokedSignature(), 
								analyzer.getMethodsCalledByTestedInvoked(), false
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
		}
		
		return this;
	}
}
