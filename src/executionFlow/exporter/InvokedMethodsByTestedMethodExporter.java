package executionFlow.exporter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import executionFlow.ConsoleOutput;
import executionFlow.ExecutionFlow;
import executionFlow.info.ConstructorInvokerInfo;
import executionFlow.info.InvokerInfo;
import executionFlow.util.CSV;
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
public class InvokedMethodsByTestedMethodExporter 
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
	public InvokedMethodsByTestedMethodExporter(String filename, String dirName)
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
	 * @param		invokedMethodsByTestedMethod Tested invokers signatures 
	 * along with its invoked method signatures. It has the following structure:
	 * <ul>
	 * 	<li><b>Key:</b> Invoker signature</li>
	 * 	<li><b>Value:</b> List of invoked method signatures by a tested invoker</li>
	 * </ul>
	 * @param		isConstructor If the invoker is a constructor
	 */
	public void export(Map<String, List<String>> invokedMethodsByTestedMethod, boolean isConstructor)
	{
		ConsoleOutput.showInfo("Exporting invoked methods by tested method...");
		
		for (Map.Entry<String, List<String>> e : invokedMethodsByTestedMethod.entrySet()) {
			String invokerSignature = InvokerInfo.getInvokerSignatureWithoutReturnType(e.getKey());
			List<String> content = e.getValue();
			File dirPath = new File(ExecutionFlow.getAppRootPath(), dirName + "/" + DataUtils.generateDirectoryPath(invokerSignature, isConstructor));
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
							if (!content.contains(line.get(i)))
								content.add(line.get(i));
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
				content.add(0, e.getKey());
				CSV.write(content, output);
			} catch (IOException e2) {
				ConsoleOutput.showError("Failed to write CSV file - "+e2.getMessage());
			}
			
			ConsoleOutput.showInfo("The export was successful");
			ConsoleOutput.showInfo("Location: "+output.getAbsolutePath());
		}
	}
}
