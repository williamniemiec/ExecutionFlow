package executionflow.io.processor.factory;

import java.nio.file.Path;

import executionflow.io.FileEncoding;
import executionflow.io.processor.fileprocessor.FileProcessor;
import executionflow.io.processor.fileprocessor.TestMethodFileProcessor;

/**
 * Responsible for generating {@link TestMethodFileProcessor} classes.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		3.0.0
 * @since		2.0.0
 */
public class TestMethodFileProcessorFactory extends FileProcessorFactory {
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	public FileProcessor newFileProcessor(Path filepath, Path outputDir, 
										  String outputFilename, 
										  FileEncoding encode) {
		return new TestMethodFileProcessor.Builder()
				.file(filepath)
				.outputDir(outputDir)
				.outputFilename(outputFilename)
				.encoding(encode)
				.build();
	}
}
