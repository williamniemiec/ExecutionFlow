package executionFlow.core.file.parser;

import java.io.IOException;

import executionFlow.core.file.FileEncoding;


/**
 * A file parser will add some code to an existing code if some conditions are 
 * met. Who will define these conditions will be the classes that implement
 * this interface.
 */
public interface FileParser 
{
	/**
	 * Parses file, adding some code to an existing code if some conditions are 
	 * met
	 * 
	 * @return Location of parsed file
	 * @throws IOException If it cannot parse the file
	 */
	String parseFile() throws IOException;
	
	/**
	 * Gets file encoding.
	 * 
	 * @return File encoding
	 */
	FileEncoding getCharset();
	
	/**
	 * Sets file encoding.
	 */
	void setCharset(FileEncoding charset);
}
