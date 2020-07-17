package executionFlow.io.processor;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import executionFlow.runtime.SkipCollection;


/**
 * Tests for class {@link PreTestMethodFileProcessor}.
 */
@SkipCollection
public class PreTestMethodFileProcessorTest 
{
	@Test
	public void testClassTest() throws IOException
	{
		File currentDir = new File("tests/executionFlow/io/processor/files/PreTestMethodFileProcessorTest");
		String filename = "TestClass";
		File f = new File(currentDir, filename+".java.txt");
		
		FileProcessor fp = new PreTestMethodFileProcessor(f.toPath(), currentDir.toPath(), filename+"_parsed", "txt");
		fp.processFile();
	}
	
	@Test
	public void parameterizedTestMethodTest() throws IOException
	{
		File currentDir = new File("tests/executionFlow/io/processor/files/PreTestMethodFileProcessorTest");
		String filename = "ParameterizedTestMethod";
		File f = new File(currentDir, filename+".java.txt");
		
		FileProcessor fp = new PreTestMethodFileProcessor(f.toPath(), currentDir.toPath(), filename+"_parsed", "txt");
		fp.processFile();
	}
}
