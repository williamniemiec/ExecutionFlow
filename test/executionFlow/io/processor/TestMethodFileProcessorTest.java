package executionFlow.io.processor;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import executionFlow.runtime.SkipCollection;


/**
 * Tests for class {@link TestMethodFileProcessor}.
 */
@SkipCollection
public class TestMethodFileProcessorTest 
{
	@Test
	public void testClassTest() throws IOException
	{
		File currentDir = new File("tests/executionFlow/io/processor/files/TestMethodFileProcessorTest");
		String filename = "TestClass";
		File f = new File(currentDir, filename+".java.txt");
		
		FileProcessor fp = new TestMethodFileProcessor(f.toPath(), currentDir.toPath(), filename+"_parsed", "txt");
		fp.processFile();
	}
}
