package executionflow.io.processor.fileprocessor;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import executionflow.io.FileEncoding;
import executionflow.io.processor.InlineCommentRemover;
import executionflow.io.processor.PrintCallDeactivator;
import executionflow.io.processor.invoked.holeplug.HolePlug;
import executionflow.io.processor.invoked.trgeneration.CodeCleanerAdapter;

/**
 * Processes java file adding instructions in parts of the code that does not 
 * exist when converting it to bytecode. Also, replaces print calls with 
 * another method that does not interfere with the code's operation.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		6.0.0
 * @since 		2.0.0
 */
public class InvokedFileProcessor extends FileProcessor {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final long serialVersionUID = 510L;
	
	/**
	 * Stores the mapping of the original file with the modified file.
	 * 
	 * <ul>
	 * 	<li><b>Key:</b> Original source file line</li>
	 * 	<li><b>Value:</b> Modified source file line</li>
	 * </ul>
	 */
	private static Map<Integer, Integer> mapping = new HashMap<>();
	private List<String> processedLines;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	/**
	 * Adds instructions in parts of the code that does not exist when 
	 * converting it to bytecode.
	 * 
	 * @param		file Path of the file to be parsed
	 * @param		outputDir Directory where parsed file will be saved
	 * @param		outputFilename Name of the parsed file
	 * @param		encode File encoding
	 * @param		fileExtension Output file extension (without dot)
	 * (default is java)
	 * 
	 * @throws		IllegalArgumentException If any required field is null
	 */ 
	private InvokedFileProcessor(Path file, Path outputDir, String outputFilename,
								 String fileExtension, FileEncoding encode) {
		checkRequiredFields(file, outputDir, outputFilename);
		
		this.file = file;
		this.outputFilename = outputFilename;
		
		if (outputDir == null)
			outputFile = Path.of(outputFilename + "." + fileExtension);
		else	
			outputFile = outputDir.resolve(outputFilename + "." + fileExtension);
		
		if (encode != null)
			this.encoding = encode;
	}
	
	
	//-------------------------------------------------------------------------
	//		Builder
	//-------------------------------------------------------------------------
	/**
	 * Builder for {@link InvokedFileProcessor}. It is necessary to provide all
	 * required fields. The required fields are: <br />
	 * <ul>
	 * 	<li>file</li>
	 * 	<li>outputDir</li>
	 * 	<li>outputFilename</li>
	 * 	<li>isTestMethod</li>
	 * </ul>
	 */
	public static class Builder	{
		
		private FileEncoding encode;
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
				this.encode = encoding;
			
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
		 * Creates {@link InvokedFileProcessor} with provided information.
		 * It is necessary to provide all required fields. The required 
		 * fields are: <br />
		 * <ul>
		 * 	<li>file</li>
		 * 	<li>outputDir</li>
		 * 	<li>outputFilename</li>
		 * 	<li>isTestMethod</li>
		 * </ul>
		 * 
		 * @return		InvokedFileProcessor with provided information
		 * 
		 * @throws		IllegalArgumentException If any required field is null
		 */
		public InvokedFileProcessor build() {
			return new InvokedFileProcessor(
					file, outputDir, outputFilename, fileExtension, encode
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
		doTRGenerationProcesing();
		doHolePlugProcessing();
		disablePrintCalls();
		
		return processedLines;
	}
	
	private void removeInlineComments() {
		InlineCommentRemover inlineCommentProcessor = 
				new InlineCommentRemover(processedLines);
		
		processedLines = inlineCommentProcessor.processLines();
	}

	private void doTRGenerationProcesing() {
		CodeCleanerAdapter codeCleaner = new CodeCleanerAdapter(processedLines);
		processedLines = codeCleaner.processLines();
		
		Map<Integer, Integer> cleanupMapping = codeCleaner.getMapping();
		
		if (cleanupMapping != null)
			mapping = cleanupMapping;
	}

	private void doHolePlugProcessing() {
		HolePlug holePlug = new HolePlug(processedLines);
		processedLines = holePlug.processLines();
	}
	
	private void disablePrintCalls() {
		PrintCallDeactivator printCallProcessor = 
				new PrintCallDeactivator(processedLines);
		
		processedLines = printCallProcessor.processLines();
	}
	
	
	//-------------------------------------------------------------------------
	//		Getters
	//-------------------------------------------------------------------------
	/**
	 * Gets the mapping of the original file with the modified file.
	 * 
	 * @return		Mapping with the following format:
	 * <ul>
	 * 	<li><b>Key:</b> Original source file line</li>
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
