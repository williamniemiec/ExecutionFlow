package executionFlow.exporter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import executionFlow.ExecutionFlow;
import executionFlow.util.CSV;
import executionFlow.util.ConsoleOutput;
import executionFlow.util.DataUtils;


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
 * @version		2.0.0
 * @since		2.0.0
 */
public class MethodsCalledByTestedInvokedExporter 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private String dirName;
	private String filename;
	
	
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
	public MethodsCalledByTestedInvokedExporter(String filename, String dirName)
	{
		this.filename = filename;
		this.dirName = dirName;
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Exports signature of methods called by a tested invoked in a CSV file.
	 * 
	 * @param		invokedSignature Invoked signature
	 * @param		methodsCalledByTestedInvoked List of signature of methods 
	 * called by a tested invoked
	 * @param		isConstructor If the invoked is a constructor
	 */
	public void export(String invokedSignature, List<String> methodsCalledByTestedInvoked, boolean isConstructor)
	{
		if (methodsCalledByTestedInvoked == null || methodsCalledByTestedInvoked.isEmpty()) {
			ConsoleOutput.showWarning("There are no methods called by tested invoked");
			return;
		}
		
		ConsoleOutput.showInfo("Exporting methods called by tested invoked...");

		File dirPath = new File(ExecutionFlow.getCurrentProjectRoot(), 
				dirName + "/" + DataUtils.generateDirectoryPath(invokedSignature, isConstructor));
		File output = new File(dirPath, filename+".csv");
		
		
		try {
			Files.createDirectories(dirPath.toPath());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		// Reads CSV file (if it exists)
		if (output.exists()) {
			try {
				for (List<String> line : CSV.read(output)) {
					// Merges CSV content with collected content
					for (int i=1; i<line.size(); i++) {
						if (!methodsCalledByTestedInvoked.contains(line.get(i)))
							methodsCalledByTestedInvoked.add(line.get(i));
					}
				}
				output.delete();
			} catch (IOException e2) {
				ConsoleOutput.showError("Failed to read CSV file - "+e2.getMessage());
			}			
		}
		
		// Writes tested invoked signature along with methods called by it
		// to CSV file
		try {
			methodsCalledByTestedInvoked.add(0, invokedSignature);
			CSV.write(methodsCalledByTestedInvoked, output);
		} catch (IOException e2) {
			ConsoleOutput.showError("Failed to write CSV file - "+e2.getMessage());
		}
		
		ConsoleOutput.showInfo("The export was successful");
		ConsoleOutput.showInfo("Location: "+output.getAbsolutePath());
	}
}
