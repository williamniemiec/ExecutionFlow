package wniemiec.executionflow.io.processor.factory;

import java.nio.file.Path;

import wniemiec.executionflow.io.FileEncoding;
import wniemiec.executionflow.io.processor.fileprocessor.FileProcessor;
import wniemiec.executionflow.io.processor.fileprocessor.InvokedFileProcessor;

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
	public FileProcessor newFileProcessor(Path filepath, Path outputDir, 
										  String outputFilename, 
										  FileEncoding encode) {
		return new InvokedFileProcessor.Builder()
				.file(filepath)
				.outputDir(outputDir)
				.outputFilename(outputFilename)
				.encoding(encode)
				.build();
	}
}
