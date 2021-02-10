package wniemiec.executionflow.io.processor.fileprocessor;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wniemiec.executionflow.io.FileEncoding;
import wniemiec.executionflow.io.SourceCodeProcessor;
import wniemiec.executionflow.io.processor.InlineCommentRemover;
import wniemiec.executionflow.io.processor.PrintCallDeactivator;
import wniemiec.executionflow.io.processor.testmethod.ClassDeclarationProcessor;
import wniemiec.executionflow.io.processor.testmethod.MultilineToInlineCallsConverter;
import wniemiec.executionflow.io.processor.testmethod.TestAnnotationProcessor;

/**
 * Processes test java file adding annotations to disable collectors while 
 * running {@link executionflow.util.core.JDB JDB}. Also, disables print calls,
 * removes inline comments and converts multiline calls to inline calls.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		6.0.0
 * @since		2.0.0
 */
public class TestMethodFileProcessor extends FileProcessor {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final long serialVersionUID = 600L;
	private static Map<Integer, Integer> mapping = new HashMap<>();
	private List<String> processedLines;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------		
	/**
	 * Processes test java file adding annotations to disable collectors while 
	 * running {@link executionflow.util.core.JDB JDB}. Also, disables print calls,
	 * removes inline comments and converts multiline calls to inline calls.
	 * 
	 * @param		file Path of the file to be parsed
	 * @param		outputDir Directory where parsed file will be saved
	 * @param		outputFilename Name of the parsed file
	 * @param		fileExtension Output file extension (without dot)
	 * (default is java)
	 * @param		encoding File encoding
	 * 
	 * @throws		IllegalArgumentException If any required field is null
	 */ 
	private TestMethodFileProcessor(Path file, Path outputDir, String outputFilename, 
									String fileExtension, FileEncoding encoding) {
		checkRequiredFields(file, outputDir, outputFilename);
		
		this.file = file;
		this.outputFilename = outputFilename;
		
		if (outputDir != null)
			outputFile = outputDir.resolve(outputFilename + "." + fileExtension);
		else	
			outputFile = Path.of(outputFilename + "." + fileExtension);
		
		if (encoding != null)
			this.encoding = encoding;
	}
	
	
	//-------------------------------------------------------------------------
	//		Builder
	//-------------------------------------------------------------------------
	/**
	 * Builder for {@link TestMethodFileProcessor}. It is necessary to provide
	 * all required fields. The required fields are: <br />
	 * <ul>
	 * 	<li>file</li>
	 * 	<li>outputDir</li>
	 * 	<li>outputFilename</li>
	 * </ul>
	 */
	public static class Builder	{
		
		private FileEncoding encoding;
		private String fileExtension = "java";
		private Path file;
		private Path outputDir;
		private String outputFilename;

		
		/**
		 * @param		file Path of the file to be parsed
		 * 
		 * @return		Itself to allow chained calls
		 * 
		 * @throws		IllegalArgumentException If file is null
		 */
		public Builder file(Path file) {
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
			if (file == null)
				throw new IllegalArgumentException("Output directory cannot be null");
			
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
			if (outputFilename == null)
				throw new IllegalArgumentException("Output filename cannot be null");
			
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
		 * @param		fileExtension Output file extension (without dot)
		 * (default is java)
		 * 
		 * @return		Itself to allow chained calls
		 */
		public Builder fileExtension(String fileExtension) {
			if (fileExtension != null)
				this.fileExtension = fileExtension;
			
			return this;
		}
		
		/**
		 * Creates {@link TestMethodFileProcessor} with provided information.
		 * It is necessary to provide all required fields. The required fields
		 * are: <br />
		 * <ul>
		 * 	<li>file</li>
		 * 	<li>outputDir</li>
		 * 	<li>outputFilename</li>
		 * </ul>
		 * 
		 * @return		TestMethodFileProcessor with provided information
		 * 
		 * @throws		IllegalArgumentException If any required field is null
		 */
		public TestMethodFileProcessor build() {
			return new TestMethodFileProcessor(
					file, outputDir, outputFilename, fileExtension, encoding
			);
		}
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	private void checkRequiredFields(Path file, Path outputDir, String outputFilename) {
		StringBuilder nullFields = new StringBuilder();
		
		if (file == null)
			nullFields.append("file").append(", ");
		if (outputDir == null)
			nullFields.append("outputDir").append(", ");
		if (outputFilename == null)
			nullFields.append("outputFilename").append(", ");
		
		if (nullFields.length() > 0) {
			throw new IllegalArgumentException("Required fields cannot be null: "
					+ nullFields.substring(0, nullFields.length()-2));	
		}
	}

	@Override
	protected List<String> doProcessing(List<String> sourceCode) {
		processedLines = sourceCode;
		
		removeInlineComments();
		putSkipCollectionAnnotation();
		disablePrintCalls();
		convertMultiLineCallsToInlineCalls();
		addCollectMethodsCalledAnnotation();
		
		return processedLines;
	}
	
	private void addCollectMethodsCalledAnnotation() {
		SourceCodeProcessor processor = new TestAnnotationProcessor(processedLines);
		
		processedLines = processor.processLines();
	}

	private void removeInlineComments() {
		InlineCommentRemover inlineCommentProcessor = 
				new InlineCommentRemover(processedLines);
		
		processedLines = inlineCommentProcessor.processLines();
	}

	private void putSkipCollectionAnnotation() {
		ClassDeclarationProcessor classDeclarationProcessor = 
				new ClassDeclarationProcessor(processedLines);
		
		processedLines = classDeclarationProcessor.processLines();
	}

	private void disablePrintCalls() {
		PrintCallDeactivator printCallProcessor = 
				new PrintCallDeactivator(processedLines);
		
		processedLines = printCallProcessor.processLines();
	}
	
	private void convertMultiLineCallsToInlineCalls() {
		MultilineToInlineCallsConverter multilineArgsProcessor = 
				new MultilineToInlineCallsConverter(processedLines);
		
		processedLines = multilineArgsProcessor.processLines();
		mapping = multilineArgsProcessor.getMapping();
	}
	
	
	//-------------------------------------------------------------------------
	//		Getters
	//-------------------------------------------------------------------------
	/**
	 * Gets the mapping of the original file with the modified file.
	 * 
	 * @return		Mapping with the following format:
	 * <ul>
	 *	<li><b>Key:</b> Original source file line</li>
	 * 	<li><b>Value:</b> Modified source file line</li>
	 * </ul>
	 */
	public static Map<Integer, Integer> getMapping() {
		return mapping;
	}
	
	public static void clearMapping() {
		mapping.clear();
	}
}
