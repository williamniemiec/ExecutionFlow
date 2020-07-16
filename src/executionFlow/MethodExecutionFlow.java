package executionFlow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import executionFlow.exporter.ConsoleExporter;
import executionFlow.exporter.FileExporter;
import executionFlow.exporter.InvokedMethodsByTestedInvokerExporter;
import executionFlow.exporter.TestPathExport;
import executionFlow.info.CollectorInfo;
import executionFlow.info.SignaturesInfo;
import executionFlow.io.FileManager;
import executionFlow.io.processor.factory.InvokerFileProcessorFactory;
import executionFlow.io.processor.factory.TestMethodFileProcessorFactory;
import executionFlow.runtime.collector.MethodCollector;
import executionFlow.util.ConsoleOutput;


/**
 * Computes test path for collected methods.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		2.0.0
 * @since		2.0.0
 */
public class MethodExecutionFlow extends ExecutionFlow
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	/**
	 * Collected methods from {@link MethodCollector}.
	 * <ul>
	 * 	<li><b>Key:</b> Method invocation line</li>
	 * 	<li><b>Value:</b> List of methods invoked from this line</li>
	 * <ul> 
	 */
	protected Map<Integer, List<CollectorInfo>> methodCollector;
	
	private boolean exportInvokedMethods;
	
	
	//-------------------------------------------------------------------------
	//		Initialization block
	//-------------------------------------------------------------------------
	/**
	 * Defines how the export will be done.
	 */
	{
		exporter = EXPORT.equals(TestPathExport.CONSOLE) ? new ConsoleExporter() : 
			new FileExporter("results", false);
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
	 */
	public MethodExecutionFlow(Map<Integer, List<CollectorInfo>> collectedMethods, boolean exportInvokedMethods)
	{
		this.methodCollector = collectedMethods;
		this.exportInvokedMethods = exportInvokedMethods;
		
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
		
		List<List<Integer>> tp_jdb;
		InvokedMethodsByTestedInvokerExporter invokedMethodsExporter = isDevelopment() ?
				new InvokedMethodsByTestedInvokerExporter("InvokedMethodsByTestedMethod", "examples\\results") :
				new InvokedMethodsByTestedInvokerExporter("InvokedMethodsByTestedMethod", "results");
		
		
		// Generates test path for each collected method
		for (List<CollectorInfo> collectors : methodCollector.values()) { 
			// Computes test path for each collected method that is invoked in the same line
			for (CollectorInfo collector : collectors) {
				// Checks if collected method is within test method
				//if (collector.getMethodInfo().getClassPath().equals(collector.getTestMethodInfo().getClassPath())) {
				if (collector.getMethodInfo().getSrcPath().equals(collector.getTestMethodInfo().getSrcPath())) {
					ConsoleOutput.showError("MethodExecutionFlow - " + collector.getMethodInfo().getInvokerSignature());
					ConsoleOutput.showError("The method to be tested cannot be within the test class");
					ConsoleOutput.showError("This test path will be skipped");
					continue;
				}
				
				if (collector.getMethodInfo().belongsToAnonymousClass()) {
					ConsoleOutput.showError("MethodExecutionFlow - " + collector.getMethodInfo().getInvokerSignature());
					ConsoleOutput.showError("The method to be tested cannot belong to an anonymous class");
					ConsoleOutput.showError("This test path will be skipped");
					continue;
				}
				
				// Gets FileManager for method file
				FileManager methodFileManager = new FileManager(
					collector.getMethodInfo().getSrcPath(), 
					collector.getMethodInfo().getClassDirectory(),
					collector.getMethodInfo().getPackage(),
					new InvokerFileProcessorFactory()
				);

				// Gets FileManager for test method file
				FileManager testMethodFileManager = new FileManager(
					collector.getTestMethodInfo().getSrcPath(), 
					collector.getTestMethodInfo().getClassDirectory(),
					collector.getTestMethodInfo().getPackage(),
					new TestMethodFileProcessorFactory()
				);
				
				try {
					// Processes the source file of the test method if it has
					// not been processed yet
					if (!testMethodManager.wasParsed(testMethodFileManager)) {
						ConsoleOutput.showInfo("Processing source file of test method "
							+ collector.getTestMethodInfo().getInvokerSignature()+"...");
						
						testMethodManager.parse(testMethodFileManager).compile(testMethodFileManager);
						ConsoleOutput.showInfo("Processing completed");	
					}

					// Processes the source file of the method if it has not 
					// been processed yet
					if (!invokerManager.wasParsed(methodFileManager)) {
						ConsoleOutput.showInfo("Processing source file of method " 
							+ collector.getMethodInfo().getInvokerSignature()+"...");
						
						invokerManager.parse(methodFileManager).compile(methodFileManager);
						ConsoleOutput.showInfo("Processing completed");
					}
					
					// Computes test path from JDB
					ConsoleOutput.showInfo("Computing test path of method "
						+ collector.getMethodInfo().getInvokerSignature()+"...");

					JDB jdb = new JDB();	
					tp_jdb = jdb.run(collector.getMethodInfo(), collector.getTestMethodInfo()).getTestPaths();
					
					if (tp_jdb.isEmpty() || tp_jdb.get(0).isEmpty())
						ConsoleOutput.showWarning("Test path is empty");
					else
						ConsoleOutput.showInfo("Test path has been successfully computed");				
					
					// Stores each computed test path
					storeTestPath(tp_jdb, collector);
					
					// Exports invoked methods by tested method to a CSV
					if (exportInvokedMethods) {
						invokedMethodsExporter.export(collector.getMethodInfo().getInvokerSignature(), 
								jdb.getInvokedMethodsByTestedInvoker(), false);
					}
				} catch (Exception e) {
					ConsoleOutput.showError(e.getMessage());
					e.printStackTrace();
				}
			}
		}
		
		return this;
	}
	
	@Override
	protected void storeTestPath(List<List<Integer>> testPaths, CollectorInfo collector)
	{
		List<List<Integer>> classPathInfo;
		SignaturesInfo signaturesInfo = new SignaturesInfo(
			collector.getMethodInfo().getInvokerSignature(), 
			collector.getTestMethodInfo().getInvokerSignature()
		);

		
		for (List<Integer> testPath : testPaths) {
			// Checks if test path belongs to a stored test method and method
			if (computedTestPaths.containsKey(signaturesInfo)) {
				classPathInfo = computedTestPaths.get(signaturesInfo);
				classPathInfo.add(testPath);
			} 
			// Else stores test path with its test method and method
			else {	
				classPathInfo = new ArrayList<>();
				
				
				classPathInfo.add(testPath);
				computedTestPaths.put(signaturesInfo, classPathInfo);
			}
		}
		
		// If test path is empty, stores test method and invoker with an empty list
		if (testPaths.isEmpty() || testPaths.get(0).isEmpty()) {
			if (computedTestPaths.containsKey(signaturesInfo)) {
				classPathInfo = computedTestPaths.get(signaturesInfo);
				computedTestPaths.put(signaturesInfo, classPathInfo);
			}
			else {
				classPathInfo = new ArrayList<>();
				classPathInfo.add(new ArrayList<>());
				computedTestPaths.put(signaturesInfo, classPathInfo);
			}
		}
	}
}
