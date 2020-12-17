package executionFlow.exporter.file;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import executionFlow.ExecutionFlow;
import executionFlow.util.DataUtil;
import executionFlow.util.FileUtil;
import executionFlow.util.formatter.JavaIndenter;


/**
 * Exports the processed file that will be used as the basis for processing the
 * test path.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.3
 * @since		5.2.0
 */
public class ProcessedSourceFileExporter 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private String dirName;
	private boolean isConstructor;

	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	/**
	 * Exports the processed file that will be used as the basis for processing the
	 * test path.
	 * 
	 * @param		dirName Directory name
	 */
	public ProcessedSourceFileExporter(String dirName, boolean isConstructor)
	{
		this.dirName = dirName;
		this.isConstructor = isConstructor;
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Exports processed file to the following path: <br />
	 * 	<code>dirName/package1/package2/.../className.invokedName(parameterTypes)/SRC.txt</code>
	 * 
	 * @param		processedFile Processed file
	 * @param		invokedSignature Method or constructor signature
	 * @param		isConstructor Indicates whether the invoked signature 
	 * belongs to a constructor
	 * 
	 * @throws		IOException If it is not possible to store file
	 */
	public void export(Path processedFile, String invokedSignature) throws IOException
	{
		List<String> fileContent = getFileContent(processedFile);
		Path outputFile = generateOutputPath(invokedSignature);
		
		FileUtil.putLines(fileContent, outputFile, Charset.defaultCharset());
	}


	private Path generateOutputPath(String invokedSignature) {
		return Paths.get(
				ExecutionFlow.getCurrentProjectRoot().toString(), 
				dirName,
				DataUtil.generateDirectoryPathFromSignature(invokedSignature, isConstructor),
				"SRC.txt"
		);
	}


	private List<String> getFileContent(Path processedFile) throws IOException {
		List<String> fileContent = FileUtil.getLines(
				processedFile, Charset.defaultCharset()
		);
		
		JavaIndenter indenter = new JavaIndenter();
		
		fileContent = indenter.format(fileContent);
		
		return fileContent;
	}
}
