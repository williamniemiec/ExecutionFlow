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
 * InvokerSignature1, TestMethodSignature11, TestMethodSignature12,...
 * InvokerSignature2, TestMethodSignature21, TestMethodSignature22,... 
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
		 * <ul>
		 * 	<li><b>Key:</b> Invoker signature</li>
		 * 	<li><b>Value:</b> List of test methods that tests an invoker</li>
		 * </ul>
		 */
		Map<String, List<String>> csvLines = new HashMap<>();

		
		ConsoleOutput.showInfo("Exporting invokers along with test methods to CSV...");
		
		// Gets invoker along with test methods that test it
		invoker_testMethods = extractTestedInvokers(signatures);
		
		// Reads CSV (if it already exists)
		csvLines = extractCSV();
		
		// Erases CSV file
		output.delete();
		
		// Merges current CSV file with new collected invokers and its test methods
		mergesMaps(invoker_testMethods, csvLines);
		
		// Writes collected invokers along with a list of test methods that 
		// call them to a CSV file
		try {
			writeCSV(csvLines);
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
	
	/**
	 * Given two Maps, adds all content from the first Map to the second.
	 * 
	 * @param		map1 Some map
	 * @param		map2 Map that will be merge with map1 
	 */
	private void mergesMaps(Map<String, List<String>> map1, Map<String, List<String>> map2)
	{
		for (Map.Entry<String, List<String>> e : map1.entrySet()) {
			String keyMap1 = e.getKey();
			List<String> contentMap2;

			
			// Adds content from first Map to the second
			for (String contentMap1 : e.getValue()) {
				// If second Map contains the same key as the first, add all
				// the content of this key from first Map in the second
				if (map2.containsKey(keyMap1)) {
					contentMap2 = map2.get(keyMap1);
					
					if (!contentMap2.contains(contentMap1)) {
						contentMap2.add(contentMap1);
					}
				}
				else {
					contentMap2 = new ArrayList<>();
					contentMap2.add(contentMap1);
					map2.put(keyMap1, contentMap2);
				}
			}
		}
	}
	
	/**
	 * Writes a Map to a CSV file.
	 * 
	 * @param		content Map with the content to be written, with the
	 * following structure:
	 * <ul>
	 * 	<li><b>Key:</b> First column element</li>
	 * 	<li><b>Value:</b> List with subsequent elements of the first column
	 * 	element</li>
	 * </ul>
	 * 
	 * @throws		IOException If an error occurs while writing the file 
	 */
	private void writeCSV(Map<String, List<String>> content) throws IOException
	{
		try (BufferedWriter csv = new BufferedWriter(new FileWriter(output))) {
			for (Map.Entry<String, List<String>> e : content.entrySet()) {
				String firstColumn = e.getKey();
				StringBuilder sb = new StringBuilder();
				
				
				sb.append(firstColumn);
				sb.append(",");
				
				for (String subsequentElement : e.getValue()) {
					sb.append(subsequentElement);
					sb.append(",");
				}
				
				// Removes last comma
				sb = sb.deleteCharAt(sb.length()-1);	
				
				// Writes the content to CSV file
				csv.write(sb.toString());
				csv.newLine();
			}
		}
	}

	/**
	 * Reads exported CSV file and returns a Map with its content.
	 * 
	 * @return		Map with CSV content with the following structure:
	 * <ul>
	 * 	<li><b>Key:</b> First column element</li>
	 * 	<li><b>Value:</b> List with subsequent elements of the first column
	 * 	element</li>
	 * </ul>
	 */
	private Map<String, List<String>> extractCSV()
	{
		Map<String, List<String>> lines = new HashMap<>();
		String line, firstColumnElement;
		String[] content;
		List<String> subsequentElements;
		
		
		try (BufferedReader csv = new BufferedReader(new FileReader(output))) {
			// Reads content from a CSV file and stores it in a Map
			while ((line = csv.readLine()) != null) {
				subsequentElements = new ArrayList<>();
				content = line.split(",");
				firstColumnElement = content[0];
				
				for (int i=1; i<content.length; i++) {
					if (!subsequentElements.contains(content[i]))
						subsequentElements.add(content[i]);
				}
				
				lines.put(firstColumnElement, subsequentElements);				
			}
		} catch (IOException e) { }
		
		return lines;
	}
}
