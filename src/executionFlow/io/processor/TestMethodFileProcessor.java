package executionFlow.io.processor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import executionFlow.io.FileEncoding;
import executionFlow.util.FileUtil;
import executionFlow.util.Logger;
import executionFlow.util.balance.RoundBracketBalance;


/**
 * Processes test java file adding {@link executionFlow.runtime._SkipInvoked}
 * annotation in all tests to disable collectors while running 
 * {@link executionFlow.util.core.JDB JDB}. Also, removes print calls.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.3
 * @since		2.0.0
 */
public class TestMethodFileProcessor extends FileProcessor
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final long serialVersionUID = 400L;
		
	private String fileExtension = "java";
	private static Map<Integer, Integer> mapping = new HashMap<>();;
	private boolean insideMultilineArgs = false;
	private int multilineArgsStartIndex = -1;
	
	
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
		if (file == null)
			return "";

		final String REGEX_COMMENT_FULL_LINE = "^(\\t|\\ )*(\\/\\/|\\/\\*|\\*\\/|\\*).*";
		String line;
		List<String> lines = new ArrayList<>();
		File outputFile;

		
		// If an output directory is specified, processed file will be saved to it
		if (outputDir != null)
			outputFile = new File(outputDir.toFile(), outputFilename + "." + fileExtension);
		// Else processed file will be saved in current directory
		else	
			outputFile = new File(outputFilename + "." + fileExtension);
		
		// Reads the source file and puts its lines in a list
		lines = FileUtil.getLines(file, encode.getStandardCharset());
		
		// Parses file line by line
		for (int i=0; i<lines.size(); i++) {
			line = lines.get(i);
			line = removeInlineComment(line);
			
			if (!(line.matches(REGEX_COMMENT_FULL_LINE) || line.isBlank())) {
				line = parseClassDeclaration(line);
				line = parsePrints(line);
				line = parseMultilineArgs(line, lines, i);
			}
			
			lines.set(i, line);
		}
		
		// -----{ DEBUG }-----
		if (Logger.getLevel() == Logger.Level.DEBUG) {
			Logger.debug("TestMethodFileProcessor", "Processed file");
			FileUtil.printFileWithLines(lines);
		}
		// -----{ END DEBUG }-----

		// Writes processed lines to a file
		FileUtil.putLines(lines, outputFile.toPath(), encode.getStandardCharset());
		
		return outputFile.getAbsolutePath();
	}
	
	/**
	 * Adds {@link executionFlow.runtime.SkipCollection} annotation next to 
	 * class declarations.
	 * 
	 * @param		line Line to be analyzed
	 * 
	 * @return		Line with {@link executionFlow.runtime.SkipCollection} if it
	 * contains a class declaration. Otherwise, it returns the line sent by 
	 * parameter
	 */
	private String parseClassDeclaration(String line) 
	{
		final String REGEX_SKIP_COLLECTION = ".*(@.+\\.SkipCollection).*";
		String skipCollectionAnnotation = "@executionFlow.runtime.SkipCollection";
		boolean isClassDeclaration = line.contains("class ") && !line.contains("new ");
		
		
		if (isClassDeclaration && !line.matches(REGEX_SKIP_COLLECTION)) {
			line =  skipCollectionAnnotation + " " + line;
		}
		
		return line;
	}
	
	/**
	 * Converts method calls with arguments on multiple lines to a call with 
	 * arguments on a single line.
	 * 
	 * @param		currentLine Line corresponding to the current index
	 * @param		lines File lines
	 * @param		currentIndex Current line index
	 * 
	 * @return		Line with arguments on a single line
	 */
	private String parseMultilineArgs(String currentLine, List<String> lines, int currentIndex) 
	{
		final String REGEX_MULTILINE_ARGS = ".+,([^;{(\\[]+|[\\s\\t]*)$";
		final String REGEX_MULTILINE_ARGS_CLOSE = "^.*[\\s\\t)}]+;[\\s\\t]*$";
		RoundBracketBalance rbb = new RoundBracketBalance();
		
		Pattern classKeywords = Pattern.compile("(@|class|implements|throws)");
		
		rbb.parse(currentLine);
		
		boolean isMethodCallWithMultipleLinesArgument = 
				!rbb.isBalanceEmpty() &&
				!classKeywords.matcher(currentLine).find() && 
				currentLine.matches(REGEX_MULTILINE_ARGS) && 
				(currentIndex+1 < lines.size());
		

		if (isMethodCallWithMultipleLinesArgument) {
			int oldLine;
			int newLine;
			String nextLine = lines.get(currentIndex+1);
			
			nextLine = removeInlineComment(nextLine);
			
			if (!insideMultilineArgs) {	
				lines.set(currentIndex+1, "");
				currentLine = currentLine + nextLine;
				
				oldLine = currentIndex+1+1;
				newLine = currentIndex+1;
				
				if (!nextLine.matches(REGEX_MULTILINE_ARGS_CLOSE)) {
					multilineArgsStartIndex = currentIndex;
					insideMultilineArgs = true;
				}
				else {
					insideMultilineArgs = false;
				}
			}
			else {
				lines.set(multilineArgsStartIndex, lines.get(multilineArgsStartIndex) + currentLine);
				currentLine = "";
				
				oldLine = currentIndex+1;
				newLine = multilineArgsStartIndex+1;
			}
			
			mapping.put(oldLine, newLine);
		}
		else if (insideMultilineArgs) {
			insideMultilineArgs = false;
			
			lines.set(multilineArgsStartIndex, lines.get(multilineArgsStartIndex) + currentLine);
			currentLine = "";
			mapping.put(currentIndex+1, multilineArgsStartIndex+1);
		}
		else if (currentLine.matches(REGEX_MULTILINE_ARGS_CLOSE) && multilineArgsStartIndex > 0) {
			insideMultilineArgs = false;
			lines.set(multilineArgsStartIndex, lines.get(multilineArgsStartIndex) + currentLine);
			currentLine = "";
		}
		
		if (!insideMultilineArgs)
			multilineArgsStartIndex = -1;

		return currentLine;
	}

	/**
	 * Removes calls to print methods.
	 * 
	 * @param		line Line to be analyzed
	 * 
	 * @return		Line without calls to print methods
	 */
	private String parsePrints(String line) 
	{
		if (line.contains("System.out.print")) {
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
		
		return line;
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
		String lineWithBlankStrings = line;
		int idxCommentStart = line.indexOf("//");
		StringBuilder strWithBlankSpaces = new StringBuilder();
		Matcher m = Pattern.compile("\"[^\"]*\"").matcher(line);
		
		while (m.find()) {
			int strLen = m.group().length()-2;
			int idxStart = m.start();
			int idxEnd = m.end();
			
			strWithBlankSpaces.append("\"");
			for (int i=0; i<strLen; i++) {
				strWithBlankSpaces.append(" ");
			}
			strWithBlankSpaces.append("\"");
			
			lineWithBlankStrings = lineWithBlankStrings.substring(0, idxStart) + strWithBlankSpaces + lineWithBlankStrings.substring(idxEnd);
			strWithBlankSpaces = new StringBuilder();
		}
		
		if (idxCommentStart != -1) {
			lineWithBlankStrings = lineWithBlankStrings.substring(0, idxCommentStart);
		}
		
		return lineWithBlankStrings;
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
	public static Map<Integer, Integer> getMapping()
	{
		return mapping;
	}
	
	public static void clearMapping()
	{
		mapping.clear();
	}
}
