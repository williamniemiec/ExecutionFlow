package executionFlow.core.file.parser.factory;

import java.nio.file.Path;

import executionFlow.core.file.FileEncoding;
import executionFlow.core.file.parser.FileParser;
import executionFlow.core.file.parser.InvokerFileParser;


/**
 * Responsible for generating {@link InvokerFileParser} classes.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		1.5
 * @since		1.5
 */
public class InvokerFileParserFactory extends FileParserFactory
{
	@Override
	public FileParser newFileParser(Path filepath, Path outputDir, 
			String outputFilename, FileEncoding encode) 
	{
		return new InvokerFileParser(filepath, outputDir, outputFilename, encode);
	}
}
