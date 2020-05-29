package executionFlow.core.file.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import executionFlow.core.JDB;
import executionFlow.core.file.FileEncoding;
import executionFlow.runtime._SkipMethod;


/**
 * Parses test java file adding {@link _SkipMethod} annotation in all tests to 
 * disable collectors during JDB.
 * 
 * @author William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @since 1.4
 * @version 1.4
 */
public class TestMethodFileParser extends FileParser
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private File file;
	private File outputDir;
	private String outputFilename;
	
	/**
	 * If true, displays processed lines.
	 */
	private static final boolean DEBUG;
	
	private FileEncoding encode = FileEncoding.UTF_8;
	
	
	//-------------------------------------------------------------------------
	//		Initialization blocks
	//-------------------------------------------------------------------------
	/**
	 * Configures environment. If {@link DEBUG} is true, displays processed 
	 * lines.
	 */
	static {
		DEBUG = false;
	}
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	/**
	 * Adds {@link SkipMethod} annotation in test methods to disable collectors
	 * during {@link JDB} execution. Using this constructor, file encoding will
	 * be UTF-8.
	 * 
	 * @param filename Path of the file to be parsed
	 * @param outputDir Directory where parsed file will be saved
	 * @param outputFilename Name of the parsed file
	 */ 
	public TestMethodFileParser(String filepath, String outputDir, String outputFilename)
	{
		this.file = new File(filepath);
		
		if (outputDir != null)
			this.outputDir = new File(outputDir);
		
		this.outputFilename = outputFilename;
	}
	
	/**
	 * Adds {@link SkipMethod} annotation in test methods to disable collectors
	 * during {@link JDB} execution. Using this constructor, the directory 
	 * where parsed file will be saved will be in current directory. Also, file 
	 * encoding will be UTF-8.
	 * 
	 * @param filename Path of the file to be parsed
	 * @param outputFilename Name of the parsed file
	 */ 
	public TestMethodFileParser(String filepath, String outputFilename)
	{
		this(filepath, null, outputFilename);
	}
	
	/**
	 * Adds {@link SkipMethod} annotation in test methods to disable collectors
	 * during {@link JDB} execution.
	 * 
	 * @param filename Path of the file to be parsed
	 * @param outputDir Directory where parsed file will be saved
	 * @param outputFilename Name of the parsed file
	 * @param encode File encoding
	 */ 
	public TestMethodFileParser(String filepath, String outputDir, String outputFilename, FileEncoding encode)
	{
		this(filepath, outputDir, outputFilename);
		this.encode = encode;
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Adds {@link SkipMethod} annotation in test methods.
	 * 
	 * @throws IOException If file encoding is incorrect or if file cannot be 
	 * read / written
	 */
	@Override
	public String parseFile() throws IOException
	{
		if (file == null) { return ""; }

		String line;
		File outputFile;
		
		// If an output directory is specified, processed file will be saved to it
		if (outputDir != null)
			outputFile = new File(outputDir, outputFilename+".java");
		else	// Else processed file will be saved in current directory
			outputFile = new File(outputFilename+".java");
		
		// Opens file streams (file to be parsed and output file / processed file)
		try (BufferedReader br = Files.newBufferedReader(file.toPath(), encode.getStandardCharset());
			 BufferedWriter bw = Files.newBufferedWriter(outputFile.toPath(), encode.getStandardCharset())) { 
			
			// Parses file line by line
			while ((line = br.readLine()) != null) {
				// Checks if line with @Test contains @SkipMethod
				if ( line.contains("@Test") && !line.contains("@executionFlow.runtime.SkipMethod") && 
					 !line.contains("@SkipMethod") ) {
					line += " @executionFlow.runtime._SkipMethod";
				}
				
				// -----{ DEBUG }-----
				if (DEBUG) { System.out.println(line); }
				// -----{ END DEBUG }-----	
				
				bw.write(line);
				bw.newLine();
			}
		}

		return outputFile.getAbsolutePath();
	}
	
	
	//-------------------------------------------------------------------------
	//		Getters & Setters
	//-------------------------------------------------------------------------
	@Override
	public FileEncoding getEncoding()
	{
		return encode;
	}
	
	@Override
	public void setEncoding(FileEncoding encode)
	{
		this.encode = encode; 
	}
}
