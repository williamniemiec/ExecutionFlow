package executionFlow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import executionFlow.core.JDB;
import executionFlow.core.file.FileManager;
import executionFlow.core.file.parser.factory.MethodFileParserFactory;
import executionFlow.core.file.parser.factory.TestMethodFileParserFactory;
import executionFlow.info.CollectorInfo;
import executionFlow.info.SignaturesInfo;
import executionFlow.runtime.MethodCollector;


/**
 * Computes test path for collected methods.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		1.5
 * @since		1.5
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
	protected Map<Integer, List<CollectorInfo>> collectedMethods;
	
	
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
		this.collectedMethods = collectedMethods;
		
		classTestPaths = new HashMap<>();
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
			ConsoleOutput.showDebug(collectedMethods.values().toString());
			ConsoleOutput.showDebug("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
		}
		// -----{ END DEBUG }-----
		
		List<List<Integer>> tp_jdb;
		
		// Generates test path for each collected method
		for (List<CollectorInfo> collectors : collectedMethods.values()) { 
			// Computes test path for each collected method that is invoked in the same line
			for (CollectorInfo collector : collectors) {
				// Checks if collected method is within test method
				if (collector.getMethodInfo().getClassPath().equals(collector.getTestMethodInfo().getClassPath())) {
					ConsoleOutput.showError("The method to be tested cannot be within the test class");
					ConsoleOutput.showError("This test path will be skipped");
					continue;
				}
				
				// Gets FileManager for method file
				FileManager methodFileManager = new FileManager(
					collector.getMethodInfo().getSrcPath(), 
					collector.getMethodInfo().getClassDirectory(),
					collector.getMethodInfo().getPackage(),
					new MethodFileParserFactory()
				);
				
				// Gets FileManager for test method file
				FileManager testMethodFileManager = new FileManager(
					collector.getTestMethodInfo().getSrcPath(), 
					collector.getTestMethodInfo().getClassDirectory(),
					collector.getTestMethodInfo().getPackage(),
					new TestMethodFileParserFactory()
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
					if (!methodManager.wasParsed(methodFileManager)) {
						ConsoleOutput.showInfo("Processing source file of method " 
							+ collector.getMethodInfo().getInvokerSignature()+"...");
						
						methodManager.parse(methodFileManager).compile(methodFileManager);
						ConsoleOutput.showInfo("Processing completed");
					}
					
					// Computes test path from JDB
					ConsoleOutput.showInfo("Computing test path of method "
						+ collector.getMethodInfo().getInvokerSignature()+"...");

					JDB jdb = new JDB(collector.getOrder());					
					tp_jdb = jdb.getTestPaths(collector.getMethodInfo(), collector.getTestMethodInfo());
					
					if (tp_jdb.isEmpty() || tp_jdb.get(0).isEmpty())
						ConsoleOutput.showWarning("Test path is empty");
					else
						ConsoleOutput.showInfo("Test path has been successfully computed");
					
					// Stores each computed test path
					storeTestPath(tp_jdb, collector);
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
		Map<SignaturesInfo, List<Integer>> classPathInfo;
		SignaturesInfo signaturesInfo = new SignaturesInfo(
			collector.getMethodInfo().getInvokerSignature(), 
			collector.getTestMethodInfo().getInvokerSignature()
		);
		String key = signaturesInfo.toString();
		
		for (List<Integer> testPath : testPaths) {
			// Checks if test path belongs to a stored test method and method
			if (classTestPaths.containsKey(key)) {
				classPathInfo = classTestPaths.get(key);
				classPathInfo.put(signaturesInfo, testPath);
			} 
			// Else stores test path with its test method and method
			else {	
				classPathInfo = new HashMap<>();
				classPathInfo.put(signaturesInfo, testPath);
				classTestPaths.put(key, classPathInfo);
			}
		}
		
		if (testPaths.isEmpty()) {
			classPathInfo = new HashMap<>();
			classPathInfo.put(signaturesInfo, new ArrayList<Integer>());
			classTestPaths.put(key, classPathInfo);
		}
	}
}
