package executionFlow.core;

import org.junit.Test;

public class FileParserTest {
	@Test
	public void testFile()
	{
		FileParser fp = new FileParser("test.txt");
		fp.parseFile();
	}
}
