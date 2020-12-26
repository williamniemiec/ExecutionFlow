package executionflow.io.processor.factory;

import java.nio.file.Path;

import executionflow.io.FileEncoding;
import executionflow.io.processor.fileprocessor.FileProcessor;

/**
 * Responsible for generating {@link FileProcessor} classes.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		2.0.0
 * @since		2.0.0
 */
public abstract class FileProcessorFactory {
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Generates a {@link FileProcessor} instance.
	 * 
	 * @param		filepath Path of the file to be processed
	 * @param		outputDir Directory where processed file will be saved
	 * @param		outputFilename Name of the processed file
	 * @param		encode File encoding of the file to be processed
	 * 
	 * @return		FileProcessor instance 
	 */
	public abstract FileProcessor newFileProcessor(Path filepath, Path outputDir, 
												   String outputFilename, 
												   FileEncoding encode);
}
