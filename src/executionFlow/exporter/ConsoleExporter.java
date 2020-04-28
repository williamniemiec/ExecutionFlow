package executionFlow.exporter;

import java.util.List;
import java.util.Map;

import executionFlow.info.SignaturesInfo;


/**
 * Exports the results on the console.
 */
public class ConsoleExporter implements ExporterExecutionFlow 
{
	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
	private Map<String, Map<SignaturesInfo, List<Integer>>> classPaths;
	
	
	//-----------------------------------------------------------------------
	//		Constructor
	//-----------------------------------------------------------------------
	/** 
	 * Shows test paths of a method on the console in the following format:
	 * <li>Test method's signature</li>
	 * <li>Method's signature</li>
	 * <li>Method's test path</li>
	 * 
	 * @param classPaths Test path of each tested method of a class
	 */	
	public ConsoleExporter(Map<String, Map<SignaturesInfo, List<Integer>>> classPaths)
	{
		this.classPaths = classPaths;
	}
	
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	@Override
	public void export() 
	{
		String currentTestMethodSignature = null;
		String currentMethod = "";
		
		System.out.println("---------------------------------------------------------------------");
		System.out.println("                                EXPORT                               ");
		System.out.println("---------------------------------------------------------------------");
		
		for (Map<SignaturesInfo, List<Integer>> classPathInfo : classPaths.values()) {
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
