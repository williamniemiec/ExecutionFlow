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
	 */
	public void export(Map<String, List<String>> invokedMethodsByTestedMethod)
	{
		for (Map.Entry<String, List<String>> e : invokedMethodsByTestedMethod.entrySet()) {
			String invokerSignature = InvokerInfo.getInvokerSignatureWithoutReturnType(e.getKey());
			File dirPath = new File(ExecutionFlow.getAppRootPath(), dirName + "/" + DataUtils.getSavePath(invokerSignature));
			File output = new File(dirPath, filename+".csv");
			
			
			try {
				Files.createDirectories(dirPath.toPath());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			// Reads CSV file (if it exists)
			if (output.exists()) {
				try {
					Map<String, List<String>> content = new HashMap<>();
					
					
					for (List<String> line : CSV.read(output)) {
						List<String> invokedMethod = new ArrayList<>();
						
						
						for (int i=1; i<line.size(); i++) {
							invokedMethod.add(line.get(i));
						}
						
						content.put(line.get(0), invokedMethod);
					}
					
					DataUtils.mergesMaps(content, invokedMethodsByTestedMethod);
				} catch (IOException e2) {
					ConsoleOutput.showError("Failed to read CSV file - "+e2.getMessage());
				}			
			}
			
			// Writes tested method signature along with invoked methods by it
			// to CSV file
			try {
				List<String> content = e.getValue();
				
				
				content.add(0, e.getKey());
				CSV.write(content, output);
				ConsoleOutput.showInfo("Invoked methods by tested method - export: "+output.getAbsolutePath());
			} catch (IOException e2) {
				ConsoleOutput.showError("Failed to write CSV file - "+e2.getMessage());
			}	
		}
		
	}
}
