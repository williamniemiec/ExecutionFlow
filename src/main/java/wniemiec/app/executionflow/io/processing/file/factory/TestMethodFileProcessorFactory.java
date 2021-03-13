package wniemiec.app.executionflow.io.processing.file.factory;

import java.nio.file.Path;

import wniemiec.app.executionflow.io.FileEncoding;
import wniemiec.app.executionflow.io.processing.file.FileProcessor;
import wniemiec.app.executionflow.io.processing.file.TestMethodFileProcessor;

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
	public FileProcessor createInstance(Path filepath, Path outputDir, 
										  String outputFilename, 
										  FileEncoding encode) {
		return new TestMethodFileProcessor.Builder()
				.targetFile(filepath)
				.outputDir(outputDir)
				.outputFilename(outputFilename)
				.encoding(encode)
				.build();
	}
}
