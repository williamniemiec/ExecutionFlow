package executionFlow.exporter;

import java.util.List;
import java.util.Map;

import executionFlow.ConsoleOutput;
import executionFlow.info.SignaturesInfo;


/**
 * Exports the results on the console.
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
	public void export(Map<String, Map<SignaturesInfo, List<Integer>>> classTestPaths) 
	{
		String currentTestMethodSignature = null;
		String currentMethod = "";
		
		ConsoleOutput.showHeader("EXPORT", '-');
		
		for (Map<SignaturesInfo, List<Integer>> classPathInfo : classTestPaths.values()) {
			for (Map.Entry<SignaturesInfo, List<Integer>> e : classPathInfo.entrySet()) {
				SignaturesInfo signatures = e.getKey();
				String testMethodSignature = signatures.getTestMethodSignature();
				
				if (testMethodSignature == null) { testMethodSignature = ""; }
				
				// Test path from another test method
				if (!testMethodSignature.equals(currentTestMethodSignature)) {
					System.out.println(signatures.getTestMethodSignature());	// Test method signature
					System.out.println(signatures.getMethodSignature());		// Method signature
					currentTestMethodSignature = signatures.getTestMethodSignature();
					currentMethod = signatures.getMethodSignature();
				} 
				// It is the same test method
				else {	
					// Checks if the test path belongs to current method
					if (!signatures.getMethodSignature().equals(currentMethod)) {
						System.out.println();
						System.out.println(signatures.getTestMethodSignature());
						System.out.println(signatures.getMethodSignature());
						
						currentMethod = signatures.getMethodSignature();
					}
				}
				
				System.out.println(e.getValue());	// Test path
			}
			
			System.out.println();		// New line
			currentTestMethodSignature = null;
		}
	}
}
