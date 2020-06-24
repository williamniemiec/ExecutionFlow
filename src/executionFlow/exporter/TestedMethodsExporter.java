package executionFlow.exporter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import executionFlow.ConsoleOutput;
import executionFlow.info.SignaturesInfo;
import executionFlow.util.CSV;
import executionFlow.util.DataUtils;


/**
 * Responsible for exporting invokers along with the test method that tests it
 * in CSV file with the following format: 
 * <br /> <br />
 * 
 * <code>
 * InvokerSignature1, TestMethodSignature11, TestMethodSignature12,...
 * InvokerSignature2, TestMethodSignature21, TestMethodSignature22,... <br /> 
 * ...
 * </code>
 * 
 * <br />
 * 
 * <b>Note:</b> An invoker can be a method or a constructor
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		1.5
 * @since		1.5
 */
public class TestedMethodsExporter implements ExporterExecutionFlow
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	/**
	 * Stores the signature of the tested invokers along with test methods that
	 * tests these invokers.
	 * <ul>
	 * 	<li><b>Key:</b> Invoker signature</li>
	 * 	<li><b>Value:</b> List of test methods that tests an invoker</li>
	 * </ul>
	 */
	private Map<String, List<String>> invoker_testMethods;
	
	private File output;
	

	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------	
	/**
	 * Exports tested invokers along with test methods that tests these
	 * invokers.
	 * 
	 * @param		filename Filename without extension
	 * @param		output Path where the file will be saved
	 */
	public TestedMethodsExporter(String filename, File output)
	{
		if (!output.exists()) {
			output.mkdir();
		}
		
		this.output = new File(output, filename+".csv");
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	public void export(Map<SignaturesInfo, List<List<Integer>>> classTestPaths) 
	{
		Set<SignaturesInfo> signatures = classTestPaths.keySet();
		
		/**
		 * Stores tested invoker signature along with its invoked method
		 * signatures.
		 * 
		 * <ul>
		 * 	<li><b>Key:</b> Invoker signature</li>
		 * 	<li><b>Value:</b> List of test methods that tests an invoker</li>
		 * </ul>
		 */
		Map<String, List<String>> invokedMethodSignatures = new HashMap<>();

		
		ConsoleOutput.showInfo("Exporting invokers along with test methods that test them to CSV...");
		
		// Gets invoker along with test methods that test it
		invoker_testMethods = extractTestedInvokers(signatures);
		
		// Reads CSV (if it already exists)
		try {
			for (List<String> line : CSV.read(output)) {
				List<String> invokedMethod = new ArrayList<>();
				
				
				for (int i=1; i<line.size(); i++) {
					invokedMethod.add(line.get(i));
				}
				
				invokedMethodSignatures.put(line.get(0), invokedMethod);
			}			
		} catch (IOException e) { }
		
		// Erases CSV file
		output.delete();
		
		// Merges current CSV file with new collected invokers and its test methods
		DataUtils.mergesMaps(invoker_testMethods, invokedMethodSignatures);
		
		// Writes collected invokers along with a list of test methods that 
		// call them to a CSV file
		try {
			for (Map.Entry<String, List<String>> e : invokedMethodSignatures.entrySet()) {
				List<String> content = e.getValue();
				
				
				content.add(0, e.getKey());
				CSV.write(content, output);
			}
		} catch (IOException e1) {
			ConsoleOutput.showError("CSV - " + e1.getMessage());
			e1.printStackTrace();
		}
		
		ConsoleOutput.showInfo("The export was successful");
		ConsoleOutput.showInfo("Location: " + output.getAbsolutePath().toString());
	}
	
	
	/**
	 * Converts a set of {@link SignaturesInfo} in the following Map:
	 * <ul>
	 * 	<li><b>Key:</b> Invoker signature</li>
	 * 	<li><b>Value:</b> List of test methods that tests an invoker</li>
	 * </ul>
	 * 
	 * @param		signatures Set of {@link SignaturesInfo} to be converted
	 * 
	 * @return		Map with the above structure
	 */
	private Map<String, List<String>> extractTestedInvokers(Set<SignaturesInfo> signatures)
	{
		Map<String, List<String>> response = new HashMap<>();
		
		
		// Converts Set<SignaturesInfo> -> Map<String, List<String>>, where:
		// 		Key:	Invoker signature
		//		Value:	List of test methods that tests an invoker
		for (SignaturesInfo signaturesInfo : signatures) {
			// If the invoker signature is already in response, add the 
			// test method signature in it
			if (response.containsKey(signaturesInfo.getInvokerSignature())) {
				List<String> testMethodSignatures = response.get(signaturesInfo.getInvokerSignature());
				
				
				testMethodSignatures.add(signaturesInfo.getTestMethodSignature());
			}
			// Else adds the invoker signature along with the test method
			// signatures
			else {
				List<String> testMethodSignatures = new ArrayList<>();
				
				
				testMethodSignatures.add(signaturesInfo.getTestMethodSignature());
				response.put(signaturesInfo.getInvokerSignature(), testMethodSignatures);
			}
		}
		
		return response;
	}
}
