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
	 * Shows the results on the console in the following format:
	 * <li>Test method's signature</li>
	 * <li>Method's signature</li>
	 * <li>Method's test path</li>
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
		System.out.println("---------------------------------------------------------------------");
		System.out.println("                                EXPORT                               ");
		System.out.println("---------------------------------------------------------------------");
		
		for (Map.Entry<SignaturesInfo, List<Integer>> e : classPaths.entrySet()) {
			SignaturesInfo signatures = e.getKey();
			
			System.out.println(signatures.getTestMethodSignature());	// Test method signature
			System.out.println(signatures.getMethodSignature());		// Method signature
			System.out.println(e.getValue());							// Test path
			System.out.println();										// New line
		}
	}
}
