package wniemiec.executionflow.exporter.file;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import wniemiec.executionflow.App;
import wniemiec.executionflow.exporter.SignatureToPath;
import wniemiec.util.io.manager.TextFileManager;
import wniemiec.util.io.processor.indenter.Indenter;
import wniemiec.util.io.processor.indenter.JavaCodeIndenter;

/**
 * Exports the processed file that will be used as the basis for processing the
 * test path.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		6.0.5
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
		TextFileManager outputFileManager = new TextFileManager(
				generateOutputPath(invokedSignature), 
				Charset.defaultCharset()
		);
		
		outputFileManager.writeLines(getFileContent(processedFile));
	}
	
	private List<String> getFileContent(Path processedFile) throws IOException {
		TextFileManager txtFileManager = new TextFileManager(
				processedFile, 
				Charset.defaultCharset()
		);

		return indentFileContent(txtFileManager.readLines());
	}
	
	private List<String> indentFileContent(List<String> fileContent) {
		Indenter indenter = new JavaCodeIndenter();
		
		return indenter.indent(fileContent);
	}

	private Path generateOutputPath(String invokedSignature) {
		return Paths.get(
				App.getCurrentProjectRoot().toString(), 
				dirName,
				SignatureToPath.generateDirectoryPathFromSignature(invokedSignature, 
																   isConstructor),
				"SRC.txt"
		);
	}
}
