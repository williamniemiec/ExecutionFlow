package executionFlow.core.file.parser.factory;

import java.nio.file.Path;

import executionFlow.core.file.FileEncoding;
import executionFlow.core.file.parser.PreTestMethodFileParser;
import executionFlow.core.file.parser.FileParser;


/**
 * Responsible for generating {@link PreTestMethodFileParser} classes.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		1.5
 * @since		1.5
 */
public class PreTestMethodFileParserFactory extends FileParserFactory
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private Object testMethodArg;
	
	
	//-------------------------------------------------------------------------
	//		Constructors
	//-------------------------------------------------------------------------
	/**
	 * Generates {@link PreTestMethodFileParser} factory. It will generates 
	 * {@link PreTestMethodFileParser} ready to handle parameterized tests.
	 * 
	 * @param		args Test method arguments
	 */
	public PreTestMethodFileParserFactory(Object[] args)
	{
		if (args.length > 0)
			this.testMethodArg = args[0];
	}
	
	/**
	 * Generates {@link PreTestMethodFileParser} factory. Use this constructor if 
	 * the method to be tested is not a parameterized test.
	 */
	public PreTestMethodFileParserFactory()
	{ }
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	public FileParser newFileParser(Path filepath, Path outputDir, 
			String outputFilename, FileEncoding encode) 
	{
		if (testMethodArg == null)
			return new PreTestMethodFileParser(filepath, outputDir, outputFilename, encode);
		else
			return new PreTestMethodFileParser(testMethodArg, filepath, outputDir, outputFilename, encode);
	}
}
