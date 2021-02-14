package wniemiec.executionflow.exporter.signature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import wniemiec.executionflow.invoked.TestedInvoked;
import wniemiec.util.io.manager.CSVFileManager;
import wniemiec.util.logger.Logger;

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
 * @version		6.0.5
 * @since		2.0.0
 */
public class TestedInvokedExporter {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	/**
	 * Stores tested invoked signature along with its invoked method
	 * signatures.
	 * 
	 * <ul>
	 * 	<li><b>Key:</b> Invoked signature</li>
	 * 	<li><b>Value:</b> List of test methods that tests an invoked</li>
	 * </ul>
	 */
	private Map<String, List<String>> invokedMethodSignatures;
	
	private CSVFileManager csvFile;
	

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
	public TestedInvokedExporter(String filename, File output) {
		if (!output.exists()) {
			output.mkdir();
		}
		
		this.csvFile = new CSVFileManager(output, filename);
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	public void export(Set<TestedInvoked> invokedContainer) {
		if (invokedContainer == null || invokedContainer.isEmpty())
			return;

		mergeWithStoredExport(invokedContainer);
		storeExportFile();
	}

	private void mergeWithStoredExport(Set<TestedInvoked> invokedContainer) {
		try {
			invokedMethodSignatures = readStoredExportFile();
		} 
		catch (IOException e) {
			e.printStackTrace();
			invokedMethodSignatures = new HashMap<>();
		}
		
		mergesMaps(
				extractInvokedWithTesters(invokedContainer), 
				invokedMethodSignatures
		);
	}
	
	/**
	 * Given two Maps, adds all content from the first Map to the second.
	 * 
	 * @param		source Some map
	 * @param		target Map that will be merge with map1 
	 */
	private void mergesMaps(Map<String, List<String>> source, 
								  Map<String, List<String>> target) {
		if ((source == null) || source.isEmpty())
			return;
		
		for (Map.Entry<String, List<String>> e : source.entrySet()) {
			String keyMap1 = e.getKey();

			// Adds content from first Map to the second
			for (String contentMap1 : e.getValue()) {
				List<String> contentMap2;
				// If second Map contains the same key as the first, add all
				// the content of this key from first Map in the second
				if (target.containsKey(keyMap1)) {
					contentMap2 = target.get(keyMap1);
					
					if (!contentMap2.contains(contentMap1)) {
						contentMap2.add(contentMap1);
					}
				}
				else {
					contentMap2 = new ArrayList<>();
					contentMap2.add(contentMap1);
					target.put(keyMap1, contentMap2);
				}
			}
		}
	}

	private Map<String, List<String>>  readStoredExportFile() throws IOException {
		if (!csvFile.exists())
			return new HashMap<>();
		
		Map<String, List<String>> invokedMethodSignatures = new HashMap<>();
		
		for (List<String> line : csvFile.read(";")) {
			List<String> invokedMethod = new ArrayList<>();
			
			for (int i=1; i<line.size(); i++) {
				invokedMethod.add(line.get(i));
			}
			
			invokedMethodSignatures.put(line.get(0), invokedMethod);
		}			
		
		
		return invokedMethodSignatures;
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
	private Map<String, List<String>> extractInvokedWithTesters(
			Set<TestedInvoked> invokedContainer) {
		Map<String, List<String>> invokedWithTesters = new HashMap<>();
		
		for (TestedInvoked container : invokedContainer) {
			if (invokedWithTesters.containsKey(
					container.getTestedInvoked().getConcreteSignature())) {
				storeExistingInvoked(invokedWithTesters, container);
			}
			else {
				storeNewInvoked(invokedWithTesters, container);
			}
		}
		
		return invokedWithTesters;
	}

	private void storeNewInvoked(Map<String, List<String>> invokedWithTesters, 
								 TestedInvoked container) {
		List<String> testMethodSignatures = new ArrayList<>();
		testMethodSignatures.add(container.getTestMethod().getInvokedSignature());
		
		invokedWithTesters.put(
				container.getTestedInvoked().getConcreteSignature(), 
				testMethodSignatures
		);
	}

	private void storeExistingInvoked(Map<String, List<String>> invokedWithTesters,
									  TestedInvoked container) {
		List<String> testMethodSignatures = invokedWithTesters.get(
				container.getTestedInvoked().getConcreteSignature()
		);
		testMethodSignatures.add(container.getTestMethod().getInvokedSignature());
	}

	private void storeExportFile() {
		Logger.debug("Exporting all invoked along with test methods that " + 
					 "test them to CSV...");
		
		csvFile.delete();
		
		try {
			for (Map.Entry<String, List<String>> e : invokedMethodSignatures.entrySet()) {
				List<String> content = e.getValue();
				content.add(0, e.getKey());
				
				csvFile.writeLine(content, ";");
			}
		} 
		catch (IOException e1) {
			Logger.debug("CSV - " + e1.getMessage());
		}
		
		Logger.debug("The export has been successful");
		Logger.debug("Location: " + csvFile.getAbsolutePath());
	}
}
