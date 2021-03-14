package wniemiec.app.executionflow.exporter.signature;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import wniemiec.app.executionflow.App;
import wniemiec.app.executionflow.exporter.SignatureToPath;
import wniemiec.app.executionflow.invoked.Invoked;
import wniemiec.util.io.manager.CSVFileManager;
import wniemiec.util.logger.Logger;

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
 * @version		6.0.5
 * @since		2.0.0
 */
public class MethodsCalledByTestedInvokedExporter 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private String dirName;
	private String filename;
	private Set<String> methodsCalledByTestedInvoked;
	private CSVFileManager csvFile;
	
	
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
	public MethodsCalledByTestedInvokedExporter(String filename, String dirName) {
		this.filename = filename;
		this.dirName = dirName;
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Exports signature of methods called by all tested invoked in a CSV file.
	 * 
	 * @param		methodsCalledByAllTestedInvoked Methods called by all tested
	 * invoked
	 * 
	 * @throws		IOException If it is not possible to generate CSV file
	 */
	public void export(Map<Invoked, Set<String>> methodsCalledByAllTestedInvoked) 
			throws IOException {
		
		for (Map.Entry<Invoked, Set<String>> mcti : methodsCalledByAllTestedInvoked.entrySet()) {
			exportRegistry(mcti.getKey(), mcti.getValue());
		}
	}
	
	private void exportRegistry(Invoked invoked, Set<String> methodsCalledByTestedInvoked) 
			throws IOException {
		if (methodsCalledByTestedInvoked == null || methodsCalledByTestedInvoked.isEmpty()) {
			Logger.debug("There are no methods called by tested invoked");
			return;
		}

		this.methodsCalledByTestedInvoked = methodsCalledByTestedInvoked;
		createExportFile(invoked);
		
		mergeWithStoredExport();
		storeExportFile(invoked);
	}

	private void createExportFile(Invoked invoked) throws IOException {
		Path directory = generateDirectoryFromSignature(invoked);
		
		Files.createDirectories(directory);
		
		csvFile = new CSVFileManager(directory.toFile(), filename);
	}

	private Path generateDirectoryFromSignature(Invoked invoked) {
		String signaturePath = SignatureToPath.generateDirectoryPathFromSignature(
				invoked.getConcreteSignature(), invoked.isConstructor()
		);
	
		return Path.of(
				App.getCurrentProjectRoot().toString(),
				dirName,
				signaturePath
		);
	}

	private void mergeWithStoredExport() {
		if (!csvFile.exists())
			return;
		
		try {
			for (List<String> line : csvFile.read(";")) {
				// Merges CSV content with collected content
				for (int i=1; i<line.size(); i++) {
					if (!methodsCalledByTestedInvoked.contains(line.get(i)))
						methodsCalledByTestedInvoked.add(line.get(i));
				}
			}
			
			csvFile.delete();
		} 
		catch (IOException e2) {
			Logger.debug("Failed to read CSV file - "+e2.getMessage());
		}
	}
	
	private void storeExportFile(Invoked invoked) {
		Logger.debug("Exporting methods called by tested invoked...");
		
		try {
			List<String> mcti = methodsCalledByTestedInvoked.stream()
					.collect(Collectors.toList());
			
			mcti.add(0, invoked.getConcreteSignature());
			csvFile.writeLine(mcti, ";");
		} 
		catch (IOException e2) {
			Logger.debug("Failed to write CSV file - "+e2.getMessage());
		}
	}
}
