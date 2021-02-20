package wniemiec.executionflow.io.processing.file;

import java.nio.file.Path;
import java.util.List;

import wniemiec.executionflow.io.FileEncoding;
import wniemiec.executionflow.io.processing.processor.AssertProcessor;
import wniemiec.executionflow.io.processing.processor.JUnit5ToJUnit4Processor;
import wniemiec.executionflow.io.processing.processor.SourceCodeProcessor;
import wniemiec.executionflow.io.processing.processor.TestMethodHighlighter;

/**
 * Responsible for pre-processing test method file. Handles exceptions
 * generated by asserts and converts JUnit 5 tests to JUnit 4 tests.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		6.0.0
 * @since		2.0.0
 */
public class PreTestMethodFileProcessor extends FileProcessor {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final long serialVersionUID = 600L;
	private static int totalTests;
	private String testMethodSignature;
	private transient Object[] testMethodArgs;
	private List<String> processedLines;
		
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	/**
	 * Handles exceptions generated by asserts. Specifically, adds a try-catch
	 * structure for each assert, so that execution does not stop even if an 
	 * assert fails. Also, converts tests JUnit 5 to JUnit 4.
	 * 
	 * @param		file Path of the file to be parsed
	 * @param		outputDir Directory where parsed file will be saved
	 * @param		outputFilename Processed file name
	 * @param		testMethodSignature Test method signature
	 * @param		testMethodArgs Test method arguments (for parameterized tests)
	 * @param		fileExtension Output file extension (without dot)
	 * (default is java)
	 * @param		encoding File encoding
	 * 
	 * @throws		IllegalArgumentException If any required field is null
	 */ 
	private PreTestMethodFileProcessor(Path file, Path outputDir, 
			String outputFilename, String testMethodSignature, 
			Object[] testMethodArgs, String fileExtension, FileEncoding encoding) {
		checkRequiredFields(file, outputDir, outputFilename, testMethodSignature);
		
		this.file = file;
		this.outputFilename = outputFilename;
		this.testMethodArgs = testMethodArgs;
		this.testMethodSignature = testMethodSignature;
		
		if (outputDir != null)
			outputFile = outputDir.resolve(outputFilename + "." + fileExtension);
		else	
			outputFile = Path.of(outputFilename +  "." + fileExtension);
		
		if (encoding != null)
			this.encoding = encoding;
	}

	
	//-------------------------------------------------------------------------
	//		Builder
	//-------------------------------------------------------------------------
	/**
	 * Builder for {@link PreTestMethodFileProcessor}. It is necessary to
	 * provide all required fields. The required fields are: <br />
	 * <ul>
	 * 	<li>file</li>
	 * 	<li>outputDir</li>
	 * 	<li>outputFilename</li>
	 * 	<li>testMethodSignature</li>
	 * </ul>
	 */
	public static class Builder {
		
		private FileEncoding encoding;
		private String fileExtension = "java";
		private Path file;
		private Path outputDir;
		private String outputFilename;
		private String testMethodSignature;
		private Object[] testMethodArgs;

		
		/**
		 * @param		file Path of the file to be parsed
		 * 
		 * @return		Itself to allow chained calls
		 * 
		 * @throws		IllegalArgumentException If file is null
		 */
		public Builder targetFile(Path file) {
			if (file == null)
				throw new IllegalArgumentException("File cannot be null");
			
			this.file = file;
			
			return this;
		}
		
		/**
		 * @param		outputDir Directory where parsed file will be saved
		 * 
		 * @return		Itself to allow chained calls
		 * 
		 * @throws		IllegalArgumentException If Output directory is null
		 */
		public Builder outputDir(Path outputDir) {
			if (outputDir == null) {
				throw new IllegalArgumentException("Output directory cannot "
						+ "be null");
			}
			
			this.outputDir = outputDir;
			
			return this;
		}
		
		/**
		 * @param		outputFilename Processed file name
		 * 
		 * @return		Itself to allow chained calls
		 * 
		 * @throws		IllegalArgumentException If output filename is null
		 */
		public Builder outputFilename(String outputFilename) {
			if (outputFilename == null) {
				throw new IllegalArgumentException("Output filename cannot "
						+ "be null");
			}
			
			this.outputFilename = outputFilename;
			
			return this;
		}
		
		/**
		 * @param		encoding File encoding (default is UTF-8)
		 * 
		 * @return		Itself to allow chained calls
		 */
		public Builder encoding(FileEncoding encoding) {
			if (encoding != null)
				this.encoding = encoding;
			
			return this;
		}
		
		/**
		 * @param		testMethodSignature Test method signature
		 * 
		 * @return		Itself to allow chained calls
		 * 
		 * @throws		IllegalArgumentException If test method signature is 
		 * null
		 */
		public Builder testMethodSignature(String testMethodSignature) {
			if (testMethodSignature == null) {
				throw new IllegalArgumentException("Test method signature "
						+ "cannot be null");
			}
			
			this.testMethodSignature = testMethodSignature;
			
			return this;
		}
		
		/**
		 * @param		testMethodArgs Test method arguments (for parameterized
		 * tests)
		 * 
		 * @return		Itself to allow chained calls
		 */
		public Builder testMethodArgs(Object... testMethodArgs) {
			this.testMethodArgs = testMethodArgs;
			
			return this;
		}
		
		/**
		 * @param		fileExtension Output file extension (without dot)
		 * (default is java)
		 * 
		 * @return		Itself to allow chained calls
		 */
		public Builder outputFileExtension(String fileExtension) {
			if (fileExtension != null)
				this.fileExtension = fileExtension;
			
			return this;
		}
		
		/**
		 * Creates {@link PreTestMethodFileProcessor} with provided information.
		 * It is necessary to provide all required fields. The required fields 
		 * are: <br />
		 * <ul>
		 * 	<li>file</li>
		 * 	<li>outputDir</li>
		 * 	<li>outputFilename</li>
		 * 	<li>testMethodSignature</li>
		 * </ul>
		 * 
		 * @return		PreTestMethodFileProcessor with provided information
		 * 
		 * @throws		IllegalArgumentException If any required field is null
		 */
		public PreTestMethodFileProcessor build() {
			return new PreTestMethodFileProcessor(
					file, outputDir, outputFilename, testMethodSignature, 
					testMethodArgs, fileExtension, encoding
			);
		}
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	private void checkRequiredFields(Path file, Path outputDir, 
									 String outputFilename, 
									 String testMethodSignature) {
		StringBuilder nullFields = new StringBuilder();
		
		if (file == null)
			nullFields.append("file").append(", ");
		if (outputDir == null)
			nullFields.append("outputDir").append(", ");
		if (outputFilename == null)
			nullFields.append("outputFilename").append(", ");
		if (testMethodSignature == null)
			nullFields.append("testMethodSignature").append(", ");
		
		if (nullFields.length() > 0) {
			throw new IllegalArgumentException("Required fields cannot be null: "
			+ nullFields.substring(0, nullFields.length()-2)); // Removes last comma
		}
	}

	@Override
	protected List<String> doProcessing(List<String> sourceCode) {
		processedLines = sourceCode;
		
		commentAllTestMethodsExcept(testMethodSignature);
		surroundAssertsWithTryCatch();
		convertJUnit5ToJUnit4();
		
		return processedLines;
	}

	private void commentAllTestMethodsExcept(String signature) {
		SourceCodeProcessor testMethodHighlighter = 
				new TestMethodHighlighter(processedLines, signature);
		
		processedLines = testMethodHighlighter.processLines();
	}
	
	private void surroundAssertsWithTryCatch() {
		SourceCodeProcessor assertProcessor = new AssertProcessor(processedLines);
		
		processedLines = assertProcessor.processLines();
	}
	
	private void convertJUnit5ToJUnit4() {
		JUnit5ToJUnit4Processor annotationProcessor = 
				new JUnit5ToJUnit4Processor(processedLines, testMethodArgs);
		
		processedLines = annotationProcessor.processLines();
		totalTests = annotationProcessor.getTotalTests();
	}
	
	
	//-------------------------------------------------------------------------
	//		Getters
	//-------------------------------------------------------------------------
	public static int getTotalTests() {
		return totalTests;
	}
}
