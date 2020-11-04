package executionFlow.io.processor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import executionFlow.io.FileEncoding;
import executionFlow.util.ConsoleOutput;
import executionFlow.util.FileUtil;


/**
 * Processes test java file adding {@link executionFlow.runtime._SkipInvoked}
 * annotation in all tests to disable collectors while running 
 * {@link executionFlow.util.core.JDB JDB}. Also, removes print calls.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.0
 * @since		2.0.0
 */
public class TestMethodFileProcessor extends FileProcessor
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final long serialVersionUID = 400L;
		
	private String fileExtension = "java";
	private static Map<Path, Map<Integer, Integer>> mapping;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------		
	/**
	 * Adds {@link executionFlow.runtime._SkipInvoked _SkipInvoked} annotation
	 * in test methods to disable collectors during 
	 * {@link executionFlow.util.core.JDB JDB} execution. Also, removes print
	 * functions. Using this constructor, file encoding will be UTF-8.
	 * 
	 * @param		file Path of the file to be parsed
	 * @param		outputDir Directory where parsed file will be saved
	 * @param		outputFilename Name of the parsed file
	 * @param		fileExtension Output file extension (without dot)
	 * (default is java)
	 */ 
	private TestMethodFileProcessor(Path file, Path outputDir, 
			String outputFilename, String fileExtension)
	{
		this.file = file;
		this.outputDir = outputDir;
		this.outputFilename = outputFilename;
		this.fileExtension = fileExtension;
	}
	
	/**
	 * Adds {@link executionFlow.runtime._SkipInvoked _SkipInvoked} annotation
	 * in test methods to disable collectors during 
	 * {@link executionFlow.util.core.JDB JDB} execution. Also, removes print
	 * functions.
	 * 
	 * @param		file Path of the file to be parsed
	 * @param		outputDir Directory where parsed file will be saved
	 * @param		outputFilename Name of the parsed file
	 * @param		fileExtension Output file extension (without dot)
	 * (default is java)
	 * @param		encode File encoding
	 */ 
	private TestMethodFileProcessor(Path file, Path outputDir, String outputFilename,
			String fileExtension, FileEncoding encode)
	{
		this(file, outputDir, outputFilename, fileExtension);
		this.encode = encode;
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
		public TestMethodFileProcessor build()
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
					new TestMethodFileProcessor(file, outputDir, outputFilename,fileExtension) : 
					new TestMethodFileProcessor(file, outputDir, outputFilename, fileExtension, encode);
		}
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Adds {@link executionFlow.runtime._SkipInvoked _SkipInvoked} annotation
	 * in test methods and deletes print functions.
	 * 
	 * @return		Path to processed file
	 * 
	 * @throws		IOException If file encoding is incorrect or if file cannot
	 * be read / written
	 */
	@Override
	public String processFile() throws IOException
	{
		if (file == null) { return ""; }

		final String REGEX_COMMENT_FULL_LINE = "^(\\t|\\ )*(\\/\\/|\\/\\*|\\*\\/|\\*).*";
		final String REGEX_MULTILINE_ARGS = ".+,([^;]+|[\\s\\t]*)$";
		String line;
		List<String> lines = new ArrayList<>();
		File outputFile;
		boolean multilineArgs = false;
		int multilineArgsStart = -1;
		
		
		// If an output directory is specified, processed file will be saved to it
		if (outputDir != null)
			outputFile = new File(outputDir.toFile(), outputFilename + "." + fileExtension);
		// Else processed file will be saved in current directory
		else	
			outputFile = new File(outputFilename + "." + fileExtension);
		
		// Reads the source file and puts its lines in a list
		lines = FileUtil.getLines(file, encode.getStandardCharset());
		
		mapping = new HashMap<>();
		
		// Parses file line by line
		for (int i=0; i<lines.size(); i++) {
			line = lines.get(i);
			line = removeInlineComment(line);
			
			if (!(line.matches(REGEX_COMMENT_FULL_LINE) || line.isBlank())) {
				// Checks whether the line contains a test annotation
				if (line.contains("@Test") || line.contains("@org.junit.Test")) {
					line += " @executionFlow.runtime._SkipInvoked";
				}
				// Checks if there are print's
				else if (line.contains("System.out.print")) {
					String[] tmp = line.split(";");
					StringBuilder response = new StringBuilder();
					
					
					// Deletes print's from the line
					for (String term : tmp) {
						if (!term.contains("System.out.print")) {
							response.append(term);
							response.append(";");
						}
					}
					
					line = response.toString();
				}
				else if (line.matches(REGEX_MULTILINE_ARGS) && (i+1 < lines.size())) {
					String nextLine = lines.get(i+1);
					
					
					nextLine = removeInlineComment(nextLine);
					
					if (!multilineArgs) {
						multilineArgsStart = i;
						multilineArgs = true;
					}
					
					line = line + nextLine;
					lines.set(i+1, "");
					mapping.put(file, Map.of(i+1+1, i+1));
				}
				else if (multilineArgs) {
					multilineArgs = false;
					
					lines.set(multilineArgsStart, lines.get(multilineArgsStart) + line);
					line = "";
				}
			}
			
			lines.set(i, line);
		}
		
		// -----{ DEBUG }-----
		if (ConsoleOutput.getLevel() == ConsoleOutput.Level.DEBUG) {
			ConsoleOutput.showDebug("TestMethodFileProcessor", "Processed file");
			FileUtil.printFileWithLines(lines);
		}
		// -----{ END DEBUG }-----

		// Writes processed lines to a file
		FileUtil.putLines(lines, outputFile.toPath(), encode.getStandardCharset());

		return outputFile.getAbsolutePath();
	}
	
	/**
	 * Removes inline comment.
	 * 
	 * @param		line Line to which inline comment will be removed.
	 * 
	 * @return		Line without inline comment
	 */
	private String removeInlineComment(String line) 
	{
		int idxCommentStart = line.indexOf("//");
		
		
		if (idxCommentStart != -1) {
			line = line.substring(0, idxCommentStart);
		}
		
		return line;
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
