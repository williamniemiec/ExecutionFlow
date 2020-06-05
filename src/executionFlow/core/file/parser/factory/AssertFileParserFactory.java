package executionFlow.core.file.parser.factory;

import java.nio.file.Path;

import executionFlow.core.file.FileEncoding;
import executionFlow.core.file.parser.AssertFileParser;
import executionFlow.core.file.parser.FileParser;


/**
 * Responsible for generating {@link AssertFileParser} classes.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		1.5
 * @since		1.5
 */
public class AssertFileParserFactory extends FileParserFactory
{
	@Override
	public FileParser newFileParser(Path filepath, Path outputDir, String outputFilename, FileEncoding encode) 
	{
		return new AssertFileParser(filepath, outputDir, outputFilename, encode);
	}
}
