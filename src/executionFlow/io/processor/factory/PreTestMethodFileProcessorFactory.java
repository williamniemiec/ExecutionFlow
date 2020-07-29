package executionFlow.io.processor.factory;

import java.nio.file.Path;

import executionFlow.io.FileEncoding;
import executionFlow.io.processor.FileProcessor;
import executionFlow.io.processor.PreTestMethodFileProcessor;


/**
 * Responsible for generating {@link PreTestMethodFileProcessor} classes.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		2.1.0
 * @since		2.0.0
 */
public class PreTestMethodFileProcessorFactory extends FileProcessorFactory
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private Object[] testMethodArgs;
	private String testMethodSignature;	
	
	
	//-------------------------------------------------------------------------
	//		Constructors
	//-------------------------------------------------------------------------
	/**
	 * Generates {@link PreTestMethodFileProcessor} factory. It will generates 
	 * {@link PreTestMethodFileProcessor} ready to handle parameterized tests.
	 * 
	 * @param		args Test method arguments
	 * @param		testMethodSignature Test method signature
	 */
	public PreTestMethodFileProcessorFactory(Object[] args, String testMethodSignature)
	{
		this(testMethodSignature);
		
		if (args != null && args.length > 0)
			this.testMethodArgs = args;
	}
	
	/**
	 * Generates {@link PreTestMethodFileProcessor} factory. Use this constructor if 
	 * the method to be tested is not a parameterized test.
	 * 
	 * @param		testMethodSignature Test method signature
	 */
	public PreTestMethodFileProcessorFactory(String testMethodSignature)
	{ 
		this.testMethodSignature = testMethodSignature;
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	public FileProcessor newFileProcessor(Path filepath, Path outputDir, 
			String outputFilename, FileEncoding encode) 
	{
		return new PreTestMethodFileProcessor(filepath, outputDir, outputFilename, 
				encode, testMethodArgs, testMethodSignature);
	}
}
