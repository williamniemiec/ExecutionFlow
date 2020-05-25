package executionFlow.core.file.parser.factory;

import executionFlow.core.file.FileEncoding;
import executionFlow.core.file.parser.FileParser;

/**
 * Responsible for generating {@link FileParser} classes.
 */
public interface FileParserFactory 
{
	/**
	 * Generates a {@link FileParser} instance.
	 * 
	 * @param filepath Path of the file to be parsed
	 * @param outputDir Directory where parsed file will be saved
	 * @param outputFilename Name of the parsed file
	 * @param charset File encoding of the file to be parsed
	 * @return FileParser instance 
	 */
	FileParser newFileParser(String filepath, String outputDir, String outputFilename, FileEncoding charset);
}
