package executionFlow.io.processor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import executionFlow.io.FileEncoding;
import executionFlow.io.processor.parser.holeplug.HolePlug;
import executionFlow.io.processor.parser.trgeneration.CodeCleanerAdapter;
import executionFlow.util.Logging;
import executionFlow.util.FileUtil;
import executionFlow.util.formatter.JavaIndenter;


/**
 * Processes java file adding instructions in parts of the code that does not 
 * exist when converting it to bytecode. Also, replaces print calls with 
 * another method that does not interfere with the code's operation.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.0
 * @since 		2.0.0
 */
public class InvokedFileProcessor extends FileProcessor
{
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
	
	private String fileExtension = "java";
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	/**
	 * Adds instructions in parts of the code that does not exist when 
	 * converting it to bytecode. Using this constructor, file encoding will be 
	 * UTF-8.
	 * 
	 * @param		file Path of the file to be parsed
	 * @param		outputDir Directory where parsed file will be saved
	 * @param		outputFilename Name of the parsed file
	 * @param		fileExtension Output file extension (without dot)
	 * (default is java)
	 */ 
	private InvokedFileProcessor(Path file, Path outputDir, 
			String outputFilename, String fileExtension)
	{
		this.file = file;
		this.outputDir = outputDir;
		this.outputFilename = outputFilename;
		this.fileExtension = fileExtension;
	}
	
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
	 */ 
	private InvokedFileProcessor(Path file, Path outputDir, String outputFilename,
			String fileExtension, FileEncoding encode)
	{
		this(file, outputDir, outputFilename, fileExtension);
		this.encode = encode;
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
	public static class Builder
	{
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
		public Builder file(Path file)
		{
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
		public Builder outputDir(Path outputDir)
		{
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
		public Builder outputFilename(String outputFilename)
		{
			if (outputFilename == null)
				throw new IllegalArgumentException("Output filename cannot be null");
			
			this.outputFilename = outputFilename;
			
			return this;
		}
		
		/**
		 * @param		encode File encoding (default is UTF-8)
		 * 
		 * @return		Itself to allow chained calls
		 */
		public Builder encode(FileEncoding encode)
		{
			if (encode != null)
				this.encode = encode;
			
			return this;
		}
		
		/**
		 * @param		fileExtension Output file extension (without dot)
		 * (default is java)
		 * 
		 * @return		Itself to allow chained calls
		 */
		public Builder fileExtension(String fileExtension)
		{
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
		public InvokedFileProcessor build()
		{
			StringBuilder nullFields = new StringBuilder();
			
			
			if (file == null)
				nullFields.append("file").append(", ");
			if (outputDir == null)
				nullFields.append("outputDir").append(", ");
			if (outputFilename == null)
				nullFields.append("outputFilename").append(", ");
			
			if (nullFields.length() > 0)
				throw new IllegalArgumentException("Required fields cannot be null: "
						+ nullFields.substring(0, nullFields.length()-2));	// Removes last comma
			
			return	encode == null ? 
					new InvokedFileProcessor(file, outputDir, outputFilename, fileExtension) : 
					new InvokedFileProcessor(file, outputDir, outputFilename, fileExtension, encode);
		}
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Processes the file adding instructions in parts of the code that does not
	 * exist when converting it to bytecode. Besides, modifies the code so that
	 * {@link executionFlow.util.core.JDB} computes the test path correctly.
	 * 
	 * @param		collectors Information about all invoked collected
	 * 
	 * @throws		IOException If file encoding is incorrect or if file cannot
	 * be read / written
	 */
	@Override
	public String processFile() throws IOException
	{
		if (file == null)
			return "";
		
		List<String> lines;
		File outputFile;
		CodeCleanerAdapter codeCleaner;
		HolePlug holePlug;
		Map<Integer, Integer> cleanupMapping;
		
		mapping = new HashMap<>();
		
		// If an output directory is specified, processed file will be saved to it
		if (outputDir != null)
			outputFile = new File(outputDir.toFile(), outputFilename + "." + fileExtension);
		// Otherwise processed file will be saved in current directory
		else	
			outputFile = new File(outputFilename + "." + fileExtension);
		
		// Reads the source file and puts its lines in a list
		lines = FileUtil.getLines(file, encode.getStandardCharset());
		
		// Processing #1 - Same processing done in TRGeneration (application)
		codeCleaner = new CodeCleanerAdapter(lines);
		lines = codeCleaner.parse();
		cleanupMapping = codeCleaner.getMapping();
		
		if (cleanupMapping != null)
			mapping = cleanupMapping;
		
		// Processing #2 - Fixes the omission of lines in compilation
		holePlug = new HolePlug(lines);
		lines = holePlug.parse();
		
		// Writes processed lines to a file
		FileUtil.putLines(lines, outputFile.toPath(), encode.getStandardCharset());

		// -----{ DEBUG }-----
		if (Logging.getLevel() == Logging.Level.DEBUG) {
			JavaIndenter indenter = new JavaIndenter();
			List<String> formatedFile = indenter.format(lines);

			
			Logging.showDebug("InvokedFileProcessor", "Processed file");
			FileUtil.printFileWithLines(formatedFile);
		}
		// -----{ END DEBUG }-----
		
		return outputFile.getAbsolutePath();
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
	public static Map<Integer, Integer> getMapping()
	{
		return mapping;
	}
}
