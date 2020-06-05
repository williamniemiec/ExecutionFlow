package executionFlow.core.file.parser;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import executionFlow.runtime.SkipCollection;


/**
 * Tests for class {@link AssertFileParser}.
 */
@SkipCollection
public class AssertFileParserTest 
{
	@Test
	public void testClassTest() throws IOException
	{
		String currentDir = new File("tests/executionFlow/core/file/parser/tests").getAbsolutePath();
		String filename = "TestClass";
		File f = new File(currentDir, filename+".java");
		
		FileParser fp = new AssertFileParser(f.getAbsolutePath(), currentDir, filename+"_parsed");
		fp.parseFile();
	}
}
