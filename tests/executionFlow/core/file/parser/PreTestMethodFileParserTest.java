package executionFlow.core.file.parser;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import executionFlow.runtime.SkipCollection;


/**
 * Tests for class {@link PreTestMethodFileParser}.
 */
@SkipCollection
public class PreTestMethodFileParserTest 
{
	@Test
	public void testClassTest() throws IOException
	{
		File currentDir = new File("tests/executionFlow/core/file/parser/files/PreTestMethodFileParserTest");
		String filename = "TestClass";
		File f = new File(currentDir, filename+".java.txt");
		
		FileParser fp = new PreTestMethodFileParser(f.toPath(), currentDir.toPath(), filename+"_parsed", "txt");
		fp.parseFile();
	}
	
	@Test
	public void parameterizedTestMethodTest() throws IOException
	{
		File currentDir = new File("tests/executionFlow/core/file/parser/files/PreTestMethodFileParserTest");
		String filename = "ParameterizedTestMethod";
		File f = new File(currentDir, filename+".java.txt");
		
		FileParser fp = new PreTestMethodFileParser(f.toPath(), currentDir.toPath(), filename+"_parsed", "txt");
		fp.parseFile();
	}
}
