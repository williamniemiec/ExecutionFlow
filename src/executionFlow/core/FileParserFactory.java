package executionFlow.core;


public interface FileParserFactory 
{
	FileParser newFileParser(String filepath, String outputDir, String outputFilename, FileCharset charset);
}
