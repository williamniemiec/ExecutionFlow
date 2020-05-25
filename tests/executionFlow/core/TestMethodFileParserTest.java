package executionFlow.core;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import executionFlow.core.file.parser.FileParser;
import executionFlow.core.file.parser.TestMethodFileParser;
import executionFlow.runtime.SkipCollection;


@SkipCollection
public class TestMethodFileParserTest 
{
	@Test
	public void testClassTest() throws IOException
	{
		String currentDir = new File("tests\\executionFlow\\core\\tests").getAbsolutePath();
		String filename = "TestClass";
		File f = new File(currentDir, filename+".java");
		
		FileParser fp = new TestMethodFileParser(f.getAbsolutePath(), currentDir, filename+"_parsed");
		fp.parseFile();
	}
}
