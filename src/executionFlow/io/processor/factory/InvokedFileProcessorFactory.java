package executionFlow.io.processor.factory;

import java.nio.file.Path;

import executionFlow.io.FileEncoding;
import executionFlow.io.processor.FileProcessor;
import executionFlow.io.processor.InvokedFileProcessor;


/**
 * Responsible for generating {@link InvokedFileProcessor} classes.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		3.0.0
 * @since		2.0.0
 */
public class InvokedFileProcessorFactory extends FileProcessorFactory
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private boolean isTestMethod;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	public InvokedFileProcessorFactory(boolean isTestMethod)
	{
		this.isTestMethod = isTestMethod;
	}
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	public FileProcessor newFileProcessor(Path filepath, Path outputDir, 
			String outputFilename, FileEncoding encode) 
	{
		return new InvokedFileProcessor.Builder()
				.file(filepath)
				.outputDir(outputDir)
				.outputFilename(outputFilename)
				.encode(encode)
				.isTestMethod(this.isTestMethod)
				.build();
	}
}
