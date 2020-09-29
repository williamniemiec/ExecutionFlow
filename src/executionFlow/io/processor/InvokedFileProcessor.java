package executionFlow.io.processor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import executionFlow.info.CollectorInfo;
import executionFlow.io.FileEncoding;
import executionFlow.io.processor.parser.cleanup.Cleanup;
import executionFlow.io.processor.parser.holeplug.HolePlug;
import executionFlow.util.ConsoleOutput;
import executionFlow.util.FileUtil;
import executionFlow.util.formatter.JavaIndenter;


/**
 * Processes java file adding instructions in parts of the code that does not 
 * exist when converting it to bytecode. Also, replaces print calls with 
 * another method that does not interfere with the code's operation.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.1.0
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
	 * 	<li><b>Key:</b> Source path</li>
	 * 	<li><b>Value:</b> Map with the following format:
	 * 		<ul>
	 * 			<li><b>Key:</b> Original source file line</li>
	 * 			<li><b>Value:</b> Modified source file line</li>
	 * 		</ul>
	 * 	</li>
	 * </ul>
	 */
	private static Map<Path, Map<Integer, Integer>> mapping = new HashMap<>();
	
	private String fileExtension = "java";
	private boolean isTestMethod;
	
	
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
	 * @param		isTestMethod If file contains test methods
	 */ 
	private InvokedFileProcessor(Path file, Path outputDir, 
			String outputFilename, String fileExtension, boolean isTestMethod)
	{
		this.file = file;
		this.outputDir = outputDir;
		this.outputFilename = outputFilename;
		this.fileExtension = fileExtension;
		this.isTestMethod = isTestMethod;
	}
	
	/**
	 * Adds instructions in parts of the code that does not exist when 
	 * converting it to bytecode.
	 * 
	 * @param		file Path of the file to be parsed
	 * @param		outputDir Directory where parsed file will be saved
	 * @param		outputFilename Name of the parsed file
	 * @param		encode File encoding
	 * @param		isTestMethod If file contains test methods
	 * @param		fileExtension Output file extension (without dot)
	 * (default is java)
	 */ 
	private InvokedFileProcessor(Path file, Path outputDir, String outputFilename,
			String fileExtension, boolean isTestMethod, FileEncoding encode)
	{
		this(file, outputDir, outputFilename, fileExtension, isTestMethod);
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
		private boolean isTestMethod;

		
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
		 * @param		isTestMethod If file contains test methods
		 * 
		 * @return		Itself to allow chained calls
		 */
		public Builder isTestMethod(boolean isTestMethod)
		{
			this.isTestMethod = isTestMethod;
			
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
					new InvokedFileProcessor(file, outputDir, outputFilename, fileExtension, isTestMethod) : 
					new InvokedFileProcessor(file, outputDir, outputFilename, fileExtension, isTestMethod, encode);
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
	public String processFile(Map<Integer, List<CollectorInfo>> collectors) throws IOException
	{
		if (file == null) { return ""; }
		
		List<String> formatedFile, lines = new ArrayList<>();
		File outputFile;
		Cleanup cleanup;
		HolePlug holePlug;
		
		
		// If an output directory is specified, processed file will be saved to it
		if (outputDir != null)
			outputFile = new File(outputDir.toFile(), outputFilename + "." + fileExtension);
		// Otherwise processed file will be saved in current directory
		else	
			outputFile = new File(outputFilename + "." + fileExtension);
		
		// Reads the source file and puts its lines in a list
		lines = FileUtil.getLines(file, encode.getStandardCharset());
		
		// Processing #1
		cleanup = new Cleanup(lines);
		lines = cleanup.parse();

		// Updates invocation line of all collected invoked if it is in the same
		// file of test method is declared
		if (isTestMethod) {
			Map<Integer, Integer> mapping = cleanup.getMapping();
			int invocationLine = 0;


			for (Map.Entry<Integer, List<CollectorInfo>> e : collectors.entrySet()) {
				List<CollectorInfo> collectorList = e.getValue();
				
				for (int i=0; i<collectorList.size(); i++) {
					invocationLine = collectorList.get(i).getMethodInfo().getInvocationLine();
					
					InvokedFileProcessor.mapping.put(
							collectorList.get(i).getMethodInfo().getSrcPath(),
						mapping
					);
					
					if (mapping.containsKey(invocationLine)) {
						collectorList.get(i).getMethodInfo().setInvocationLine(mapping.get(invocationLine));
					}
				}
			}
		}
		
		// Displays processed file
		ConsoleOutput.showHeader("File after processing (test path will be computed based on it)", '=');
		ConsoleOutput.printDivision('-', 80);
		formatedFile = new JavaIndenter().format(lines);
		
		for (int i=0; i<lines.size(); i++) {
			System.out.printf("%-6d\t%s\n", i+1, formatedFile.get(i));
		}
		
		ConsoleOutput.printDivision('-', 80);
		
		// Processing #2
		holePlug = new HolePlug(lines);
		lines = holePlug.parse();
		
		// Writes processed lines to a file
		FileUtil.putLines(lines, outputFile.toPath(), encode.getStandardCharset());

		return outputFile.getAbsolutePath();
	}
	
	@Override
	public String processFile() throws IOException
	{
		return "";
	}
	
	
	//-------------------------------------------------------------------------
	//		Getters
	//-------------------------------------------------------------------------
	/**
	 * Gets the mapping of the original file with the modified file.
	 * 
	 * @return		Mapping with the following format:
	 * <ul>
	 * 	<li><b>Key:</b> Source path</li>
	 * 	<li><b>Value:</b> Map with the following format:
	 * 		<ul>
	 * 			<li><b>Key:</b> Original source file line</li>
	 * 			<li><b>Value:</b> Modified source file line</li>
	 * 		</ul>
	 * 	</li>
	 * </ul>
	 */
	public static Map<Path, Map<Integer, Integer>> getMapping()
	{
		return mapping;
	}
}
