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
	private Map<SignaturesInfo, List<Integer>> classPaths;
	
	
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
	public ConsoleExporter(Map<SignaturesInfo, List<Integer>> classPaths)
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
		boolean firstTime = true;
		
		System.out.println("---------------------------------------------------------------------");
		System.out.println("                                EXPORT                               ");
		System.out.println("---------------------------------------------------------------------");
		
		for (Map.Entry<SignaturesInfo, List<Integer>> e : classPaths.entrySet()) {
			SignaturesInfo signatures = e.getKey();
			
			if (!signatures.getTestMethodSignature().equals(currentTestMethodSignature)) {
				System.out.println(signatures.getTestMethodSignature());	// Test method signature
				System.out.println(signatures.getMethodSignature());		// Method signature
				
				currentTestMethodSignature = signatures.getTestMethodSignature();
			}
			
			if (firstTime) {
				currentTestMethodSignature = signatures.getTestMethodSignature();
				firstTime = false;
			}
			
			System.out.println(e.getValue());	// Test path
		}
		
		System.out.println();				// New line
	}
}
