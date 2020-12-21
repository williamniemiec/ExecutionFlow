package executionFlow.exporter.signature;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import executionFlow.ExecutionFlow;
import executionFlow.util.CSV;
import executionFlow.util.DataUtil;
import executionFlow.util.logger.Logger;


/**
 * Exports signature of methods called by a tested invoked in a CSV file with
 * the following format: <br /> <br /> 
 * 
 * <code>
 * 	TestedMethodSignature1, calledMethod11, calledMethod12, ...
 * 	TestedMethodSignature2, calledMethod21, calledMethod22, ... <br />
 * 	...
 * </code>
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.3
 * @since		2.0.0
 */
public class MethodsCalledByTestedInvokedExporter 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private String dirName;
	private String filename;
	private boolean isConstructor;
	private Set<String> methodsCalledByTestedInvoked;
	private File exportFile;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	/**
	 * Exports signature of method called by a tested invoked in a CSV file with
	 * the following format: <br /> <br /> 
	 * 
	 * <code>
	 * 	TestedMethodSignature1, calledMethod11, calledMethod12, ...
	 * 	TestedMethodSignature2, calledMethod21, calledMethod22, ... <br />
	 * 	...
	 * </code>
	 * 
	 * <br /> <br />
	 * 
	 * The file will be exported to the following path:
	 * <code>dirName/package1/package2/.../className.invokedName(parameterTypes)/filename.csv</code>
	 * 
	 * @param		filename CSV filename (without '.csv')
	 * @param		dirName Directory name
	 */
	public MethodsCalledByTestedInvokedExporter(String filename, String dirName, 
			boolean isConstructor)
	{
		this.filename = filename;
		this.dirName = dirName;
		this.isConstructor = isConstructor;
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	public void export(Map<String, Set<String>> methodsCalledByAllTestedInvoked) 
			throws IOException {
		for (Map.Entry<String, Set<String>> mcti : methodsCalledByAllTestedInvoked.entrySet()) {
			exportRegistry(mcti.getKey(), mcti.getValue());
		}
	}
	
	/**
	 * Exports signature of methods called by a tested invoked in a CSV file.
	 * 
	 * @param		invokedSignature Invoked signature
	 * @param		methodsCalledByTestedInvoked List of signature of methods 
	 * called by a tested invoked (parameters should be separated by semicolons)
	 * @param		isConstructor If the invoked is a constructor
	 * @throws IOException 
	 */
	private void exportRegistry(String invokedSignature, Set<String> methodsCalledByTestedInvoked) 
			throws IOException
	{
		if (methodsCalledByTestedInvoked == null || methodsCalledByTestedInvoked.isEmpty()) {
			Logger.debug("There are no methods called by tested invoked");
			return;
		}

		this.methodsCalledByTestedInvoked = methodsCalledByTestedInvoked;
		this.exportFile = createExportFile(invokedSignature);
		
		mergeWithStoredExport();
		storeExportFile(invokedSignature);
	}

	private File createExportFile(String invokedSignature) throws IOException {
		Path directory = generateDirectoryFromSignature(invokedSignature);

		Files.createDirectories(directory);
		
		return new File(directory.toFile(), filename + ".csv");
	}


	private Path generateDirectoryFromSignature(String invokedSignature) {
		String signaturePath = DataUtil.generateDirectoryPathFromSignature(
				invokedSignature, isConstructor
		);
		
		return Path.of(
				ExecutionFlow.getCurrentProjectRoot().toString(),
				dirName,
				signaturePath
		);
	}

	private void storeExportFile(String invokedSignature) {
		Logger.debug("Exporting methods called by tested invoked...");
		
		try {
			List<String> mcti = methodsCalledByTestedInvoked.stream().collect(Collectors.toList());
			
			
			mcti.add(0, invokedSignature);
			CSV.write(mcti, exportFile, ";");
		} 
		catch (IOException e2) {
			Logger.debug("Failed to write CSV file - "+e2.getMessage());
		}
	}

	private void mergeWithStoredExport() {
		if (!exportFile.exists())
			return;
		
		try {
			for (List<String> line : CSV.read(exportFile, ";")) {
				// Merges CSV content with collected content
				for (int i=1; i<line.size(); i++) {
					if (!methodsCalledByTestedInvoked.contains(line.get(i)))
						methodsCalledByTestedInvoked.add(line.get(i));
				}
			}
			
			exportFile.delete();
		} 
		catch (IOException e2) {
			Logger.debug("Failed to read CSV file - "+e2.getMessage());
		}
	}
}
