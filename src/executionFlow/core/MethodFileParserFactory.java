package executionFlow.core;


/**
 * Responsible for generating {@link MethodFileParser} classes.
 */
public class MethodFileParserFactory implements FileParserFactory
{
	@Override
	public FileParser newFileParser(String filepath, String outputDir, String outputFilename, FileCharset charset) 
	{
		return new MethodFileParser(filepath, outputDir, outputFilename, charset);
	}
}
