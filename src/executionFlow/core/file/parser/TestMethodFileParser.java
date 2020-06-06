package executionFlow.core.file.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;

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
	private transient Path file;
	private transient Path outputDir;
	private String outputFilename;

	
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
		else	// Else processed file will be saved in current directory
			outputFile = new File(outputFilename+".java");
		
		// Opens file streams (file to be parsed and output file / processed file)
		try (BufferedReader br = Files.newBufferedReader(file, encode.getStandardCharset());
			 BufferedWriter bw = Files.newBufferedWriter(outputFile.toPath(), encode.getStandardCharset())) { 
			
			// Parses file line by line
			while ((line = br.readLine()) != null) {
				// Checks if line with @Test contains @SkipMethod
				if ( line.contains("@Test") && 
					 !line.contains("@executionFlow.runtime.SkipMethod") &&
					 !line.contains("@SkipMethod") ) {
					line += " @executionFlow.runtime._SkipMethod";
				}
				
				bw.write(line);
				bw.newLine();
			}
		}

		return outputFile.getAbsolutePath();
	}
	
	private void writeObject(ObjectOutputStream oos)
	{
		try {
			oos.defaultWriteObject();
			oos.writeUTF(file.toAbsolutePath().toString());
			oos.writeUTF(outputDir.toAbsolutePath().toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void readObject(ObjectInputStream ois)
	{
		try {
			ois.defaultReadObject();
			this.file = Path.of(ois.readUTF());
			this.outputDir = Path.of(ois.readUTF());
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}
}
