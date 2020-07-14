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
 * Exports invoked method signatures by a tested invoker in a CSV file with the
 * following format: <br /> <br /> 
 * 
 * <code>
 * 	TestedMethodSignature1,invokedMethod11, invokedMethod12, ...
 * 	TestedMethodSignature2,invokedMethod21, invokedMethod22, ... <br />
 * 	...
 * </code>
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		1.5
 * @since		1.5
 */
public class InvokedMethodsByTestedInvokerExporter 
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
	 * Exports invoked method signatures by a tested invoker in a CSV file with
	 * the following format: <br /> <br /> 
	 * 
	 * <code>
	 * 	TestedMethodSignature1,invokedMethod11, invokedMethod12, ...
	 * 	TestedMethodSignature2,invokedMethod21, invokedMethod22, ... <br />
	 * 	...
	 * </code>
	 * 
	 * <br /> <br />
	 * 
	 * The file will be exported to the following path:
	 * <code>dirName/package1/package2/.../className.invokerName(parameterTypes)/filename.csv</code>
	 * 
	 * @param		filename CSV filename (without '.csv')
	 * @param		dirName Directory name
	 */
	public InvokedMethodsByTestedInvokerExporter(String filename, String dirName)
	{
		this.filename = filename;
		this.dirName = dirName;
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Exports invoked method signatures by a tested invoker in a CSV file.
	 * 
	 * @param		invokerSignature Invoker signature
	 * @param		invokedMethodsByTestedInvoker List of invoked method 
	 * signatures by a tested invoker
	 * @param		isConstructor If the invoker is a constructor
	 */
	public void export(String invokerSignature, List<String> invokedMethodsByTestedInvoker, boolean isConstructor)
	{
		if (invokedMethodsByTestedInvoker == null || invokedMethodsByTestedInvoker.isEmpty()) {
			ConsoleOutput.showWarning("There are no invoked methods by tested invoker");
			return;
		}
		
		ConsoleOutput.showInfo("Exporting invoked methods by tested invoker...");

		//List<String> content = e.getValue();
		File dirPath = new File(ExecutionFlow.getCurrentProjectRoot(), dirName + "/" + DataUtils.generateDirectoryPath(invokerSignature, isConstructor));
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
						if (!invokedMethodsByTestedInvoker.contains(line.get(i)))
							invokedMethodsByTestedInvoker.add(line.get(i));
					}
				}
				output.delete();
			} catch (IOException e2) {
				ConsoleOutput.showError("Failed to read CSV file - "+e2.getMessage());
			}			
		}
		
		// Writes tested method signature along with invoked methods by it
		// to CSV file
		try {
			invokedMethodsByTestedInvoker.add(0, invokerSignature);
			CSV.write(invokedMethodsByTestedInvoker, output);
		} catch (IOException e2) {
			ConsoleOutput.showError("Failed to write CSV file - "+e2.getMessage());
		}
		
		ConsoleOutput.showInfo("The export was successful");
		ConsoleOutput.showInfo("Location: "+output.getAbsolutePath());
	}
}
