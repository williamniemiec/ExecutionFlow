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
		File currentDir = new File("test/executionFlow/io/processor/files/TestMethodFileProcessorTest");
		String filename = "TestClass";
		File f = new File(currentDir, filename+".java.txt");
		
		FileProcessor fp = new TestMethodFileProcessor.Builder()
				.file(f.toPath())
				.outputDir(currentDir.toPath())
				.outputFilename(filename+"_parsed")
				.fileExtension("txt")
				.build();
		fp.processFile();
	}
}
