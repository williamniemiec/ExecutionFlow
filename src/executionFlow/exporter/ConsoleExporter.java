package executionFlow.exporter;

import java.util.List;
import java.util.Map;

import executionFlow.info.SignaturesInfo;


/**
 * Exports the results on the console.
 * 
 * @author William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @since 1.0
 * @version 1.4
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
		
		System.out.println("---------------------------------------------------------------------");
		System.out.println("                                EXPORT                               ");
		System.out.println("---------------------------------------------------------------------");
		
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
				} else {	// It is the same test method
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
