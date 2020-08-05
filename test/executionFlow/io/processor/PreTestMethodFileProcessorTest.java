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
	public void testClassTest_firstMethod() throws IOException
	{
		File currentDir = new File("test/executionFlow/io/processor/files/PreTestMethodFileProcessorTest");
		String filename = "TestClass";
		File f = new File(currentDir, filename+".java.txt");
		
		FileProcessor fp = new PreTestMethodFileProcessor.Builder()
				.file(f.toPath())
				.outputDir(currentDir.toPath())
				.outputFilename(filename+"_parsed")
				.fileExtension("txt")
				.testMethodSignature("executionFlow.core.file.parser.tests.TestClass.testLastCurlyBracketInSameLine()")
				.build();
		fp.processFile();
	}
	
	@Test
	public void parameterizedTestMethodTest_firstMethod() throws IOException
	{
		File currentDir = new File("test/executionFlow/io/processor/files/PreTestMethodFileProcessorTest");
		String filename = "ParameterizedTestMethod";
		File f = new File(currentDir, filename+".java.txt");
		
		FileProcessor fp = new PreTestMethodFileProcessor.Builder()
				.file(f.toPath())
				.outputDir(currentDir.toPath())
				.outputFilename(filename+"_parsed")
				.fileExtension("txt")
				.testMethodSignature("examples.junit5.ParameterizedTestAnnotation.test1(int)")
				.testMethodArgs(-1)
				.build();
		fp.processFile();
	}
}
