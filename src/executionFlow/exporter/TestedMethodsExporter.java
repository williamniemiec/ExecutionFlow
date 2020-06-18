package executionFlow.exporter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
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
	 * 	<li><b>Value:</b> List of invokers tested by a test method</li>
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
		
		/**
		 * Stores lines from a CSV file.
		 * 
		 * Key: Test method signature
		 * Value: List of invokers tested by a test method
		 */
		Map<String, List<String>> csvLines = new HashMap<>();

		
		ConsoleOutput.showInfo("Exporting tested methods to CSV...");
		
		// Gets collected invokers
		testedInvokers = extractTestedInvokers(signatures);
		
		// Reads CSV (if it already exists)
		csvLines = extractCSV();
		
		// Erases CVS file
		output.delete();
		
		// Merges current CSV file with new collected invokers
		for (Map.Entry<String, List<String>> e : testedInvokers.entrySet()) {
			String testMethodSignature = e.getKey();
			List<String> csvTestedInvokers;
			
			
			if (csvLines.containsKey(testMethodSignature))
				csvTestedInvokers = csvLines.get(testMethodSignature);
			else
				csvTestedInvokers = new ArrayList<>();
			
			// Groups invoker signatures along with the test method that
			// calls it
			for (String invokerSignature : e.getValue()) {
				// If invoker belongs to the current test method and has
				// not been placed on the list, put it				
				if (!csvTestedInvokers.contains(invokerSignature))
					csvTestedInvokers.add(invokerSignature);
			}
			
			csvLines.put(testMethodSignature, csvTestedInvokers);
		}
		
		
		// Writes collected invokers along with its test method in CSV file
		try (BufferedWriter csv = new BufferedWriter(new FileWriter(output))) {
			for (Map.Entry<String, List<String>> e : csvLines.entrySet()) {
				String testMethodSignature = e.getKey();
				StringBuilder sb = new StringBuilder();
				
				
				sb.append(testMethodSignature);
				sb.append(",");
				
				for (String invokerSignature : e.getValue()) {
					sb.append(invokerSignature);
					sb.append(",");
				}
				
				// Removes last comma
				sb = sb.deleteCharAt(sb.length()-1);	
				
				// Writes test method + testedInvokers in CSV file
				csv.write(sb.toString());
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
	private Map<String, List<String>> extractTestedInvokers(Set<SignaturesInfo> signatures)
	{
		Map<String, List<String>> response = new HashMap<>();
		
		
		// Converts Set<SignaturesInfo> -> Map<String, List<String>>, where:
		// 		Key:	Test method signature
		//		Value:	List of invokers tested by this test method
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

	/**
	 * Reads exported CSV file and returns a Set with all its lines.
	 * <br />
	 * <h1>Output</h1>
	 * <ul>
	 * 	<li><b>Key:</b> Test method signature</li>
	 * 	<li><b>Value:</b> List of invokers tested by a test method</li>
	 * </ul>
	 * 
	 * @return		Set with CSV lines
	 */
	private Map<String, List<String>> extractCSV()
	{
		Map<String, List<String>> lines = new HashMap<>();
		String line, testMethod;
		String[] content;
		List<String> testedInvokers;
		
		
		try (BufferedReader csv = new BufferedReader(new FileReader(output))) {
			// Reads content from a CSV file and stores it in a Map
			while ((line = csv.readLine()) != null) {
				testedInvokers = new ArrayList<>();
				content = line.split(",");
				testMethod = content[0];
				
				for (int i=1; i<content.length; i++) {
					if (!testedInvokers.contains(content[i]))
						testedInvokers.add(content[i]);
				}
				
				lines.put(testMethod, testedInvokers);				
			}
		} catch (IOException e) { }
		
		return lines;
	}
}
