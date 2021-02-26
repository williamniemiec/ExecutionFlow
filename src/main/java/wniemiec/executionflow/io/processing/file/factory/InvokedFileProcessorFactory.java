package wniemiec.executionflow.io.processing.file.factory;

import java.nio.file.Path;

import wniemiec.executionflow.io.FileEncoding;
import wniemiec.executionflow.io.processing.file.FileProcessor;
import wniemiec.executionflow.io.processing.file.InvokedFileProcessor;

/**
 * Responsible for generating {@link InvokedFileProcessor} classes.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.0
 * @since		2.0.0
 */
public class InvokedFileProcessorFactory extends FileProcessorFactory {
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	public FileProcessor createInstance(Path filepath, Path outputDir, 
										  String outputFilename, 
										  FileEncoding encode) {
		return new InvokedFileProcessor.Builder()
				.targetFile(filepath)
				.outputDir(outputDir)
				.outputFilename(outputFilename)
				.encoding(encode)
				.build();
	}
}
