package wniemiec.app.executionflow.io.processing.file.factory;

import java.nio.file.Path;
import java.util.List;

import wniemiec.app.executionflow.io.FileEncoding;
import wniemiec.app.executionflow.io.processing.file.FileProcessor;
import wniemiec.app.executionflow.io.processing.file.PreTestMethodFileProcessor;

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
	private List<String> testMethodArgs;
	
	
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
	public PreTestMethodFileProcessorFactory(String testMethodSignature, List<String> args) {
		this.testMethodSignature = testMethodSignature;
		
		if (args != null && args.size() > 0)
			this.testMethodArgs = args;
	}
	
	/**
	 * Generates {@link PreTestMethodFileProcessor} factory. Use this 
	 * constructor if the method to be tested is not a parameterized test.
	 * 
	 * @param		testMethodSignature Test method signature
	 */
	public PreTestMethodFileProcessorFactory(String testMethodSignature) { 
		this(testMethodSignature, null);
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	public FileProcessor createInstance(Path filepath, Path outputDir, 
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
