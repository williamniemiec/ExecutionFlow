package executionFlow.exporter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import executionFlow.ConsoleOutput;
import executionFlow.info.SignaturesInfo;


/**
 * Responsible for exporting tested Invoker in CSV with the following format: 
 * <br /> <br />
 * <code>
 * TestMethodSignature1, testedInvokerSignature11, testedInvokerSignature12,...
 * TestMethodSignature2, testedInvokerSignature21, testedInvokerSignature22,... 
 * <br />...
 * </code>
 * <br />
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
	 * Stores the signature of the tested invokers along with the signature of
	 * the test method.
	 * <ul>
	 * 	<li><b>Key:</b> Test method signature</li>
	 * 	<li><b>Value:</b> List of invokers tested by this test method</li>
	 * </ul>
	 */
	private Map<String, List<String>> testedInvokers;
	
	private File output;
	

	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------	
	/**
	 * Exports tested invokers along with the signature of the test method that
	 * tests this invoker.
	 * 
	 * @param		filename Filename without extension
	 * @param		output Path where the file will be saved
	 */
	public TestedMethodsExporter(String filename, File output)
	{
		this.output = new File(output, filename+".csv");

		if (!output.exists()) {
			output.mkdir();
		}
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	public void export(Map<SignaturesInfo, List<List<Integer>>> classTestPaths) 
	{
		Set<SignaturesInfo> signatures = classTestPaths.keySet();
		StringBuilder line = new StringBuilder();
		
		// Fill testedMethods
		testedInvokers = extractTestedMethods(signatures);
		
		// Exports to CSV
		ConsoleOutput.showInfo("Exporting tested methods to CSV...");
		
		try (BufferedWriter csv = new BufferedWriter(new FileWriter(output, true))) {
			for (Map.Entry<String, List<String>> e : testedInvokers.entrySet()) {
				String testMethodSignature = e.getKey();
				line.append(testMethodSignature);
				line.append(",");
	
				for (String invokerSignature : e.getValue()) {
					line.append(invokerSignature);
					line.append(",");
				}
				
				// Removes last comma
				line.deleteCharAt(line.length()-1);	
								
				// Writes line in CSV file
				csv.write(line.toString());
				csv.newLine();
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
	 * 	<li><b>Key:</b> Test method signature</li>
	 * 	<li><b>Value:</b> List of invokers tested by this test method</li>
	 * </ul>
	 * 
	 * @param		signatures Set of {@link SignaturesInfo} to be converted
	 * 
	 * @return		Map with the above structure
	 */
	private Map<String, List<String>> extractTestedMethods(Set<SignaturesInfo> signatures)
	{
		Map<String, List<String>> response = new HashMap<>();
		
		for (SignaturesInfo signaturesInfo : signatures) {
			// If the test method signature is already in response, add the 
			// invoker signature in it
			if (response.containsKey(signaturesInfo.getTestMethodSignature())) {
				List<String> invokerSignatures = response.get(signaturesInfo.getTestMethodSignature());
				invokerSignatures.add(signaturesInfo.getInvokerSignature());
			}
			// Else adds the test method signature along with the invoker
			// signature
			else {
				List<String> invokers = new ArrayList<>();
				invokers.add(signaturesInfo.getInvokerSignature());
				response.put(signaturesInfo.getTestMethodSignature(), invokers);
			}
		}
		
		return response;
	}
}
