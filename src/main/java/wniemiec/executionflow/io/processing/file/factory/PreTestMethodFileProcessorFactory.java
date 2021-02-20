package wniemiec.executionflow.io.processing.file.factory;

import java.nio.file.Path;

import wniemiec.executionflow.io.FileEncoding;
import wniemiec.executionflow.io.processing.file.FileProcessor;
import wniemiec.executionflow.io.processing.file.PreTestMethodFileProcessor;

/**
 * Responsible for generating {@link PreTestMethodFileProcessor} classes.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		3.0.0
 * @since		2.0.0
 */
public class PreTestMethodFileProcessorFactory extends FileProcessorFactory {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private String testMethodSignature;	
	private Object[] testMethodArgs;
	
	
	//-------------------------------------------------------------------------
	//		Constructors
	//-------------------------------------------------------------------------
	/**
	 * Generates {@link PreTestMethodFileProcessor} factory. It will generates 
	 * {@link PreTestMethodFileProcessor} ready to handle parameterized tests.
	 * 
	 * @param		testMethodSignature Test method signature
	 * @param		args Test method arguments
	 */
	public PreTestMethodFileProcessorFactory(String testMethodSignature, Object[] args) {
		this(testMethodSignature);
		
		if (args != null && args.length > 0)
			this.testMethodArgs = args;
	}
	
	/**
	 * Generates {@link PreTestMethodFileProcessor} factory. Use this 
	 * constructor if the method to be tested is not a parameterized test.
	 * 
	 * @param		testMethodSignature Test method signature
	 */
	public PreTestMethodFileProcessorFactory(String testMethodSignature) { 
		this.testMethodSignature = testMethodSignature;
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	public FileProcessor newFileProcessor(Path filepath, Path outputDir, 
										  String outputFilename, 
										  FileEncoding encode) {
		return new PreTestMethodFileProcessor.Builder()
				.targetFile(filepath)
				.outputDir(outputDir)
				.outputFilename(outputFilename)
				.testMethodSignature(testMethodSignature)
				.testMethodArgs(testMethodArgs)
				.encoding(encode)
				.build();
	}
}
