package executionFlow.core.file.parser.factory;

import executionFlow.core.file.FileEncoding;
import executionFlow.core.file.parser.FileParser;
import executionFlow.core.file.parser.MethodFileParser;

/**
 * Responsible for generating {@link MethodFileParser} classes.
 * 
 * @author William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @since 1.4
 * @version 1.4
 */
public class MethodFileParserFactory extends FileParserFactory
{
	@Override
	public FileParser newFileParser(String filepath, String outputDir, String outputFilename, FileEncoding encode) 
	{
		return new MethodFileParser(filepath, outputDir, outputFilename, encode);
	}
}
