package executionFlow.core;

import java.io.IOException;

public interface FileParser 
{
	String parseFile() throws IOException;
	FileCharset getCharset();
	void setCharset(FileCharset charset);
}
