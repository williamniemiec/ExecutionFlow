package executionFlow.exporter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import executionFlow.util.CSV;
import executionFlow.util.ConsoleOutput;
import executionFlow.util.DataUtil;
import executionFlow.util.Pair;


/**
 * Responsible for exporting invoked along with the test method that tests it
 * in CSV file with the following format: 
 * <br /> <br />
 * 
 * <code>
 * InvokedSignature1, TestMethodSignature11, TestMethodSignature12,...
 * InvokedSignature2, TestMethodSignature21, TestMethodSignature22,... <br /> 
 * ...
 * </code>
 * 
 * <br />
 * 
 * <b>Note:</b> An invoked can be a method or a constructor
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.0
 * @since		2.0.0
 */
public class TestedInvokedExporter implements ExporterExecutionFlow
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	/**
	 * Stores the signature of the tested invoked along with test methods that
	 * tests these invoked.
	 * <ul>
	 * 	<li><b>Key:</b> Invoked signature</li>
	 * 	<li><b>Value:</b> List of test methods that tests the invoked</li>
	 * </ul>
	 */
	private Map<String, List<String>> invoked_testMethods;
	
	private File output;
	

	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------	
	/**
	 * Exports tested invoked along with test methods that tests these
	 * invoked.
	 * 
	 * @param		filename Filename without extension
	 * @param		output Path where the file will be saved
	 */
	public TestedInvokedExporter(String filename, File output)
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
	public void export(Map<Pair<String, String>, List<List<Integer>>> classTestPaths) 
	{
		if (classTestPaths == null || classTestPaths.isEmpty())
			return;
		
		Set<Pair<String, String>> signatures = classTestPaths.keySet();
		
		/**
		 * Stores tested invoked signature along with its invoked method
		 * signatures.
		 * 
		 * <ul>
		 * 	<li><b>Key:</b> Invoked signature</li>
		 * 	<li><b>Value:</b> List of test methods that tests an invoked</li>
		 * </ul>
		 */
		Map<String, List<String>> invokedMethodSignatures = new HashMap<>();

		
		ConsoleOutput.showInfo("Exporting invokers along with test methods that test them to CSV...");
		
		// Gets invoked along with test methods that test it
		invoked_testMethods = extractTestedInvoked(signatures);
		
		// Reads CSV (if it already exists)
		try {
			for (List<String> line : CSV.read(output, ";")) {
				List<String> invokedMethod = new ArrayList<>();
				
				
				for (int i=1; i<line.size(); i++) {
					invokedMethod.add(line.get(i));
				}
				
				invokedMethodSignatures.put(line.get(0), invokedMethod);
			}			
		} 
		catch (IOException e) { }
		
		// Erases CSV file
		output.delete();
		
		// Merges current CSV file with new collected invoked and its test methods
		DataUtil.mergesMaps(invoked_testMethods, invokedMethodSignatures);
		
		// Writes collected invoked along with a list of test methods that 
		// call them to a CSV file
		try {
			for (Map.Entry<String, List<String>> e : invokedMethodSignatures.entrySet()) {
				List<String> content = e.getValue();
				
				
				content.add(0, e.getKey());
				CSV.write(content, output, ";");
			}
		} 
		catch (IOException e1) {
			ConsoleOutput.showError("CSV - " + e1.getMessage());
			e1.printStackTrace();
		}
		
		ConsoleOutput.showInfo("The export was successful");
		ConsoleOutput.showInfo("Location: " + output.getAbsolutePath().toString());
	}
	
	
	/**
	 * Converts a set of {@link SignaturesInfo} in the following Map:
	 * <ul>
	 * 	<li><b>Key:</b> Invoked signature</li>
	 * 	<li><b>Value:</b> List of test methods that tests the invoked</li>
	 * </ul>
	 * 
	 * @param		signatures Set of {@link SignaturesInfo} to be converted
	 * 
	 * @return		Map with the above structure
	 */
	private Map<String, List<String>> extractTestedInvoked(Set<Pair<String, String>> signatures)
	{
		Map<String, List<String>> response = new HashMap<>();
		
		
		// Converts Set<SignaturesInfo> -> Map<String, List<String>>, where:
		// 		Key:	Invoked signature
		//		Value:	List of test methods that tests an invoked
		for (Pair<String, String> signaturesInfo : signatures) {
			// If the invoked signature is already in response, add the 
			// test method signature in it
			if (response.containsKey(signaturesInfo.second)) {
				List<String> testMethodSignatures = response.get(signaturesInfo.second);
				
				
				testMethodSignatures.add(signaturesInfo.first);
			}
			// Else adds the invoked signature along with the test method
			// signatures
			else {
				List<String> testMethodSignatures = new ArrayList<>();
				
				
				testMethodSignatures.add(signaturesInfo.first);
				response.put(signaturesInfo.second, testMethodSignatures);
			}
		}
		
		return response;
	}
}
