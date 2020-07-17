package executionFlow.exporter;

import java.util.List;
import java.util.Map;

import executionFlow.util.ConsoleOutput;
import executionFlow.util.Pair;


/**
 * Displays the results on the console.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		2.0.0
 * @since		1.0
 */
public class ConsoleExporter implements ExporterExecutionFlow 
{
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	public void export(Map<Pair<String, String>, List<List<Integer>>> classTestPaths) 
	{
		if (classTestPaths == null || classTestPaths.isEmpty())
			return;
		
		String currentTestMethodSignature = null;
		String currentInvoked = "";
		
		
		ConsoleOutput.showHeader("EXPORT", '-');
		
		for (Map.Entry<Pair<String, String>, List<List<Integer>>> e : classTestPaths.entrySet()) {
			Pair<String, String> signatures = e.getKey();
			String testMethodSignature = signatures.first;
			
			
			if (testMethodSignature == null) { testMethodSignature = ""; }
			
			// Test path from another test method
			if (!testMethodSignature.equals(currentTestMethodSignature)) {
				System.out.println(signatures.first);	// Test method signature
				System.out.println(signatures.second);		// Invoked signature
				currentTestMethodSignature = signatures.first;
				currentInvoked = signatures.second;
			} 
			// It is the same test method
			else {	
				// Checks if the test path belongs to current invoked
				if (!signatures.second.equals(currentInvoked)) {
					System.out.println();
					System.out.println(signatures.first);
					System.out.println(signatures.second);
					
					currentInvoked = signatures.second;
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
