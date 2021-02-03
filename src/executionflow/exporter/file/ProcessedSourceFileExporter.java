package executionflow.exporter.file;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import executionflow.ExecutionFlow;
import executionflow.util.DataUtil;
import executionflow.util.FileUtils;
import util.io.formatter.JavaIndenter;

/**
 * Exports the processed file that will be used as the basis for processing the
 * test path.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		6.0.0
 * @since		5.2.0
 */
public class ProcessedSourceFileExporter {
	
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
	public ProcessedSourceFileExporter(String dirName, boolean isConstructor) {
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
	 * @param		processedSourceFiles Processed source files
	 * 
	 * @throws		IOException If it is not possible to export source files
	 */
	public void export(Map<String, Path> processedSourceFiles) throws IOException {
		for (Map.Entry<String, Path> srcFiles : processedSourceFiles.entrySet()) {
			exportRegistry(srcFiles.getKey(), srcFiles.getValue());
		}
	}
	
	private void exportRegistry(String invokedSignature, Path processedFile) 
			throws IOException	{
		List<String> fileContent = getFileContent(processedFile);
		Path outputFile = generateOutputPath(invokedSignature);
		
		FileUtils.writeLines(fileContent, outputFile, Charset.defaultCharset());
	}
	
	private List<String> getFileContent(Path processedFile) throws IOException {
		List<String> fileContent = FileUtils.readLines(
				processedFile, Charset.defaultCharset()
		);
		
		JavaIndenter indenter = new JavaIndenter();
		
		fileContent = indenter.format(fileContent);
		
		return fileContent;
	}

	private Path generateOutputPath(String invokedSignature) {
		return Paths.get(
				ExecutionFlow.getCurrentProjectRoot().toString(), 
				dirName,
				DataUtil.generateDirectoryPathFromSignature(invokedSignature, isConstructor),
				"SRC.txt"
		);
	}
}
