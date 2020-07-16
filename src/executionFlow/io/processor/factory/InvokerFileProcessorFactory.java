package executionFlow.io.processor.factory;

import java.nio.file.Path;

import executionFlow.io.FileEncoding;
import executionFlow.io.processor.FileProcessor;
import executionFlow.io.processor.InvokerFileProcessor;


/**
 * Responsible for generating {@link InvokerFileProcessor} classes.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		2.0.0
 * @since		2.0.0
 */
public class InvokerFileProcessorFactory extends FileProcessorFactory
{
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	public FileProcessor newFileProcessor(Path filepath, Path outputDir, 
			String outputFilename, FileEncoding encode) 
	{
		return new InvokerFileProcessor(filepath, outputDir, outputFilename, encode);
	}
}
