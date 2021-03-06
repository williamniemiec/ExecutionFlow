package wniemiec.app.executionflow.io.processing.file.factory;

import java.nio.file.Path;

import wniemiec.app.executionflow.io.FileEncoding;
import wniemiec.app.executionflow.io.processing.file.FileProcessor;

/**
 * Responsible for generating {@link FileProcessor} classes.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
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
	public abstract FileProcessor createInstance(Path filepath, Path outputDir, 
												   String outputFilename, 
												   FileEncoding encode);
}
