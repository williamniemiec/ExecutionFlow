package executionFlow.core.file.parser;

import java.io.IOException;

import executionFlow.core.file.FileEncoding;


/**
 * A file parser will add some code to an existing code if some conditions are 
 * met. Who will define these conditions will be the classes that implement
 * this class.
 * 
 * @author William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @since 1.4
 * @version 1.4
 */
public abstract class FileParser 
{
	/**
	 * Parses file, adding some code to an existing code if some conditions are 
	 * met
	 * 
	 * @return Location of parsed file
	 * @throws IOException If it cannot parse the file
	 */
	public abstract String parseFile() throws IOException;
	
	/**
	 * Gets file encoding.
	 * 
	 * @return File encoding
	 */
	public abstract FileEncoding getEncoding();
	
	/**
	 * Sets file encoding.
	 * 
	 * @param encoding File encoding
	 */
	public abstract void setEncoding(FileEncoding encoding);
}
