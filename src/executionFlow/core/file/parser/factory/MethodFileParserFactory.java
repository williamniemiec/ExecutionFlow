package executionFlow.core.file.parser.factory;

import executionFlow.core.file.FileEncoding;
import executionFlow.core.file.parser.FileParser;
import executionFlow.core.file.parser.MethodFileParser;

/**
 * Responsible for generating {@link MethodFileParser} classes.
 */
public class MethodFileParserFactory implements FileParserFactory
{
	@Override
	public FileParser newFileParser(String filepath, String outputDir, String outputFilename, FileEncoding charset) 
	{
		return new MethodFileParser(filepath, outputDir, outputFilename, charset);
	}
}
