package executionflow.exporter.signature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import executionflow.info.InvokedContainer;
import executionflow.util.CSV;
import executionflow.util.DataUtil;
import executionflow.util.logger.Logger;

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
 * @version		6.0.0
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
	public TestedInvokedExporter(String filename, File output) {
		if (!output.exists()) {
			output.mkdir();
		}
		
		this.output = new File(output, filename+".csv");
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	public void export(Set<InvokedContainer> invokedContainer) {
		if (invokedContainer == null || invokedContainer.isEmpty())
			return;

		mergeWithStoredExport(invokedContainer);
		storeExportFile();
	}

	private void mergeWithStoredExport(Set<InvokedContainer> invokedContainer) {
		try {
			invokedMethodSignatures = readStoredExportFile();
		} 
		catch (IOException e) {
			e.printStackTrace();
			invokedMethodSignatures = new HashMap<>();
		}
		
		DataUtil.mergesMaps(
				extractInvokedWithTesters(invokedContainer), 
				invokedMethodSignatures
		);
	}

	private Map<String, List<String>>  readStoredExportFile() throws IOException {
		if (!output.exists())
			return new HashMap<>();
		
		Map<String, List<String>> invokedMethodSignatures = new HashMap<>();
		
		for (List<String> line : CSV.read(output, ";")) {
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
			Set<InvokedContainer> invokedContainer) {
		Map<String, List<String>> invokedWithTesters = new HashMap<>();
		
		for (InvokedContainer container : invokedContainer) {
			if (invokedWithTesters.containsKey(
					container.getInvokedInfo().getConcreteInvokedSignature())) {
				storeExistingInvoked(invokedWithTesters, container);
			}
			else {
				storeNewInvoked(invokedWithTesters, container);
			}
		}
		
		return invokedWithTesters;
	}

	private void storeNewInvoked(Map<String, List<String>> invokedWithTesters, 
								 InvokedContainer container) {
		List<String> testMethodSignatures = new ArrayList<>();
		testMethodSignatures.add(container.getTestMethodInfo().getInvokedSignature());
		
		invokedWithTesters.put(
				container.getInvokedInfo().getConcreteInvokedSignature(), 
				testMethodSignatures
		);
	}

	private void storeExistingInvoked(Map<String, List<String>> invokedWithTesters,
									  InvokedContainer container) {
		List<String> testMethodSignatures = invokedWithTesters.get(
				container.getInvokedInfo().getConcreteInvokedSignature()
		);
		testMethodSignatures.add(container.getTestMethodInfo().getInvokedSignature());
	}

	private void storeExportFile() {
		Logger.debug("Exporting all invoked along with test methods that " + 
					 "test them to CSV...");
		
		output.delete();
		
		try {
			for (Map.Entry<String, List<String>> e : invokedMethodSignatures.entrySet()) {
				List<String> content = e.getValue();
				content.add(0, e.getKey());
				
				CSV.write(content, output, ";");
			}
		} 
		catch (IOException e1) {
			Logger.debug("CSV - " + e1.getMessage());
		}
		
		Logger.debug("The export has been successful");
		Logger.debug("Location: " + output.getAbsolutePath());
	}
}
