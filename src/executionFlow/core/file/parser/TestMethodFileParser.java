package executionFlow.core.file.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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

	
	//-------------------------------------------------------------------------
	//		Initialization blocks
	//-------------------------------------------------------------------------
	/**
	 * Configures environment. If {@link #DEBUG} is true, displays processed 
	 * lines.
	 */
	static {
		DEBUG = false;
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
}
