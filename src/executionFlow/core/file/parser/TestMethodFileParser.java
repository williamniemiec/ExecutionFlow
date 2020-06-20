package executionFlow.core.file.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import executionFlow.ConsoleOutput;
import executionFlow.core.file.FileEncoding;


/**
 * Parses test java file adding {@link executionFlow.runtime._SkipMethod
 * _SkipMethod} annotation in all tests to disable collectors during the 
 * execution of {@link executionFlow.core.JDB JDB}.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		1.5
 * @since		1.4
 */
public class TestMethodFileParser extends FileParser
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final long serialVersionUID = 105L;
	
	/**
	 * If true, displays processed lines.
	 */
	private static final boolean DEBUG;
	
	
	private Object arg;
	
	
	//-------------------------------------------------------------------------
	//		Initialization blocks
	//-------------------------------------------------------------------------
	/**
	 * Configures environment. If {@link #DEBUG} is true, displays processed 
	 * lines.
	 */
	static {
		DEBUG = true;
	}
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	/**
	 * Adds {@link executionFlow.runtime.SkipMethod SkipMethod} annotation in 
	 * test methods to disable collectors during {@link executionFlow.core.JDB 
	 * JDB} execution. Using this constructor, file encoding will be UTF-8.
	 * 
	 * @param		filename Path of the file to be parsed
	 * @param		outputDir Directory where parsed file will be saved
	 * @param		outputFilename Name of the parsed file
	 */ 
	public TestMethodFileParser(Path filepath, Path outputDir, String outputFilename)
	{
		this.file = filepath;
		this.outputDir = outputDir;
		this.outputFilename = outputFilename;
	}
	
	/**
	 * Adds {@link executionFlow.runtime.SkipMethod SkipMethod} annotation in
	 * test methods to disable collectors during {@link executionFlow.core.JDB
	 * JDB} execution. Using this constructor, the directory where parsed file
	 * will be saved will be in current directory. Also, file encoding will be
	 * UTF-8.
	 * 
	 * @param		filename Path of the file to be parsed
	 * @param		outputFilename Name of the parsed file
	 */ 
	public TestMethodFileParser(Path filepath, String outputFilename)
	{
		this(filepath, null, outputFilename);
	}
	
	/**
	 * Adds {@link executionFlow.runtime.SkipMethod SkipMethod} annotation in 
	 * test methods to disable collectors during {@link executionFlow.core.JDB
	 * JDB} execution.
	 * 
	 * @param		filename Path of the file to be parsed
	 * @param		outputDir Directory where parsed file will be saved
	 * @param		outputFilename Name of the parsed file
	 * @param		encode File encoding
	 */ 
	public TestMethodFileParser(Path filepath, Path outputDir, String outputFilename, FileEncoding encode)
	{
		this(filepath, outputDir, outputFilename);
		this.encode = encode;
	}
	
	
	
	
	
	
	
	public TestMethodFileParser(Object arg, Path filepath, Path outputDir, String outputFilename, FileEncoding encode)
	{
		this(filepath, outputDir, outputFilename, encode);
		this.arg = arg;
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Adds {@link executionFlow.runtime.SkipMethod SkipMethod} annotation in 
	 * test methods.
	 * 
	 * @return		Path to parsed file
	 * 
	 * @throws		IOException If file encoding is incorrect or if file cannot
	 * be read / written
	 */
	@Override
	public String parseFile() throws IOException
	{
		if (file == null) { return ""; }

		String line;
		File outputFile;
		boolean inParameterizedTest = false, inTestMethodSignature = false;
		final Pattern pattern_methodDeclaration = Pattern.compile("(\\ |\\t)*([A-z0-9\\-_$<>\\[\\]\\ \\t]+(\\s|\\t))+[A-z0-9\\-_$]+\\(([A-z0-9\\-_$,<>\\[\\]\\ \\t])*\\)(\\{|(\\s\\{)||\\/)*");
		String params = null;
		
		
		// If an output directory is specified, processed file will be saved to it
		if (outputDir != null)
			outputFile = new File(outputDir.toFile(), outputFilename+".java");
		// Else processed file will be saved in current directory
		else	
			outputFile = new File(outputFilename+".java");
		
		// Opens file streams (file to be parsed and output file / processed file)
		try (BufferedReader br = Files.newBufferedReader(file, encode.getStandardCharset());
			 BufferedWriter bw = Files.newBufferedWriter(outputFile.toPath(), encode.getStandardCharset())) { 
			
			// Parses file line by line
			while ((line = br.readLine()) != null) {
				// Checks whether the line contains a test annotation
				if (line.contains("@Test") || line.contains("@org.junit.Test")) {
					line += " @executionFlow.runtime._SkipMethod";
					inTestMethodSignature = true;
				}
				
				if (arg != null && inTestMethodSignature) {
					if (line.matches(pattern_methodDeclaration.toString())) {
						inTestMethodSignature = false;
						// Extracts parameters
						Pattern p = Pattern.compile("\\(.*\\)");
						Matcher m = p.matcher(line);
						
						
						if (m.find()) {
							params = m.group();
							params = params.replace("(", "").replace(")", ""); // Removes parentheses
							line = line.replace(params, ""); // Deletes params from method
						}
					}
					// Converts parameters to local variables
					else if (params != null) {
						params = params + "=" + arg + ";";
						
						
						if (line.contains("{")) {	// Problem: anonymous class at first time
							int index = line.indexOf("{");
							
							
							line = line.substring(0, index+1) + params + line.substring(index+1);
						}
						else {
							line = params + line;
						}
						inParameterizedTest = false;
					}
				}
				
				// -----{ DEBUG }-----
				if (DEBUG) { ConsoleOutput.showDebug(line); }
				// -----{ END DEBUG }-----	
				
				bw.write(line);
				bw.newLine();
			}
		}

		return outputFile.getAbsolutePath();
	}
	
	/**
	 * Checks whether a line contains a test annotation.
	 * 
	 * @param		line Line to be analyzed
	 * 
	 * @return		If line contains a test annotation
	 */
	private boolean isTestMethod(String line)
	{
		return	line.contains("@Test"); //||
				//line.contains("@BeforeEach");
				//line.contains("@ParameterizedTest") ||
				//line.contains("@RepeatedTest");
	}
}
