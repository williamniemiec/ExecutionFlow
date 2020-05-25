package executionFlow.core.file.parser.factory;

import executionFlow.core.file.FileEncoding;
import executionFlow.core.file.parser.FileParser;
import executionFlow.core.file.parser.TestMethodFileParser;

/**
 * Responsible for generating {@link TestMethodFileParser} classes.
 */
public class TestMethodFileParserFactory implements FileParserFactory
{
	@Override
	public FileParser newFileParser(String filepath, String outputDir, String outputFilename, FileEncoding charset) 
	{
		return new TestMethodFileParser(filepath, outputDir, outputFilename, charset);
	}
}
