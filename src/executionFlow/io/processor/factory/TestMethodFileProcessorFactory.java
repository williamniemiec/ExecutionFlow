package executionFlow.io.processor.factory;

import java.nio.file.Path;

import executionFlow.io.FileEncoding;
import executionFlow.io.processor.fileprocessor.FileProcessor;
import executionFlow.io.processor.fileprocessor.TestMethodFileProcessor;

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
