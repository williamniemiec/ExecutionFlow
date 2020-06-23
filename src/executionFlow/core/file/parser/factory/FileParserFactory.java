package executionFlow.core.file.parser.factory;

import java.nio.file.Path;

import executionFlow.core.file.FileEncoding;
import executionFlow.core.file.parser.FileParser;


/**
 * Responsible for generating {@link FileParser} classes.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		1.5
 * @since		1.4
 */
public abstract class FileParserFactory
{
	/**
	 * Generates a {@link FileParser} instance.
	 * 
	 * @param		filepath Path of the file to be parsed
	 * @param		outputDir Directory where parsed file will be saved
	 * @param		outputFilename Name of the parsed file
	 * @param		encode File encoding of the file to be parsed
	 * 
	 * @return		FileParser instance 
	 */
	public abstract FileParser newFileParser(Path filepath, Path outputDir, 
			String outputFilename, FileEncoding encode);
}
