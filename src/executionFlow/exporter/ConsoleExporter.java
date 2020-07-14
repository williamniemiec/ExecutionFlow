package executionFlow.exporter;

import java.util.List;
import java.util.Map;

import executionFlow.info.SignaturesInfo;
import executionFlow.util.ConsoleOutput;


/**
 * Displays the results on the console.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		1.5
 * @since		1.0
 */
public class ConsoleExporter implements ExporterExecutionFlow 
{
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	public void export(Map<SignaturesInfo, List<List<Integer>>> classTestPaths) 
	{
		if (classTestPaths == null || classTestPaths.isEmpty())
			return;
		
		String currentTestMethodSignature = null;
		String currentInvoker = "";
		
		
		ConsoleOutput.showHeader("EXPORT", '-');
		
		for (Map.Entry<SignaturesInfo, List<List<Integer>>> e : classTestPaths.entrySet()) {
			SignaturesInfo signatures = e.getKey();
			String testMethodSignature = signatures.getTestMethodSignature();
			
			
			if (testMethodSignature == null) { testMethodSignature = ""; }
			
			// Test path from another test method
			if (!testMethodSignature.equals(currentTestMethodSignature)) {
				System.out.println(signatures.getTestMethodSignature());	// Test method signature
				System.out.println(signatures.getInvokerSignature());		// Invoker signature
				currentTestMethodSignature = signatures.getTestMethodSignature();
				currentInvoker = signatures.getInvokerSignature();
			} 
			// It is the same test method
			else {	
				// Checks if the test path belongs to current invoker
				if (!signatures.getInvokerSignature().equals(currentInvoker)) {
					System.out.println();
					System.out.println(signatures.getTestMethodSignature());
					System.out.println(signatures.getInvokerSignature());
					
					currentInvoker = signatures.getInvokerSignature();
				}
			}
			
			for (List<Integer> testPath : e.getValue()) {
				System.out.println(testPath);	// Test path
			}
		}
		
		System.out.println();		// New line
		currentTestMethodSignature = null;
	}
}
