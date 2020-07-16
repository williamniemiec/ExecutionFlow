package executionFlow.io.processor.factory;

import java.nio.file.Path;

import executionFlow.io.FileEncoding;
import executionFlow.io.processor.FileProcessor;
import executionFlow.io.processor.PreTestMethodFileProcessor;


/**
 * Responsible for generating {@link PreTestMethodFileProcessor} classes.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		2.0.0
 * @since		2.0.0
 */
public class PreTestMethodFileProcessorFactory extends FileProcessorFactory
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private Object testMethodArg;
	
	
	//-------------------------------------------------------------------------
	//		Constructors
	//-------------------------------------------------------------------------
	/**
	 * Generates {@link PreTestMethodFileProcessor} factory. It will generates 
	 * {@link PreTestMethodFileProcessor} ready to handle parameterized tests.
	 * 
	 * @param		args Test method arguments
	 */
	public PreTestMethodFileProcessorFactory(Object[] args)
	{
		if (args.length > 0)
			this.testMethodArg = args[0];
	}
	
	/**
	 * Generates {@link PreTestMethodFileProcessor} factory. Use this constructor if 
	 * the method to be tested is not a parameterized test.
	 */
	public PreTestMethodFileProcessorFactory()
	{ }
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	public FileProcessor newFileProcessor(Path filepath, Path outputDir, 
			String outputFilename, FileEncoding encode) 
	{
		if (testMethodArg == null)
			return new PreTestMethodFileProcessor(filepath, outputDir, outputFilename, encode);
		
		return new PreTestMethodFileProcessor(filepath, outputDir, outputFilename, encode, testMethodArg);
	}
}
