package executionFlow.core.file.parser;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import executionFlow.runtime.SkipCollection;


/**
 * Tests for class {@link TestMethodFileParser}.
 */
@SkipCollection
public class TestMethodFileParserTest 
{
	@Test
	public void testClassTest() throws IOException
	{
		File currentDir = new File("tests/executionFlow/core/file/parser/files/TestMethodFileParserTest");
		String filename = "TestClass";
		File f = new File(currentDir, filename+".java.txt");
		
		FileParser fp = new TestMethodFileParser(f.toPath(), currentDir.toPath(), filename+"_parsed", "txt");
		fp.parseFile();
	}
}
