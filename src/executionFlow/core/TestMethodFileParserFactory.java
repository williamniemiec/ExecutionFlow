package executionFlow.core;


/**
 * Responsible for generating {@link TestMethodFileParser} classes.
 */
public class TestMethodFileParserFactory implements FileParserFactory
{
	@Override
	public FileParser newFileParser(String filepath, String outputDir, String outputFilename, FileCharset charset) 
	{
		return new TestMethodFileParser(filepath, outputDir, outputFilename, charset);
	}
}
