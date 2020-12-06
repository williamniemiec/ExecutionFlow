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
//	
//	@Test
//	public void testMethodInvokedSameLine() throws IOException
//	{
//		File currentDir = new File("test/executionFlow/io/processor/files/");
//		String filename = "test_method_tested_invoked_same_file";
//		File f = new File(currentDir, filename+"_parsed.txt");
//		
//		FileProcessor fp = new TestMethodFileProcessor.Builder()
//				.file(f.toPath())
//				.outputDir(currentDir.toPath())
//				.outputFilename(filename+"_parsed")
//				.fileExtension("txt")
//				.build();
//		fp.processFile();
//	}
//	
//	@Test
//	public void testAnonymousClass() throws IOException
//	{
//		File currentDir = new File("test/executionFlow/io/processor/files/");
//		String filename = "test_anonymous_class";
//		File f = new File(currentDir, filename+"_parsed.txt");
//		
//		FileProcessor fp = new TestMethodFileProcessor.Builder()
//				.file(f.toPath())
//				.outputDir(currentDir.toPath())
//				.outputFilename(filename+"_parsed")
//				.fileExtension("txt")
//				.build();
//		fp.processFile();
//	}
//	
//	@Test
//	public void testMultiargsTest() throws IOException
//	{
//		File currentDir = new File("test/executionFlow/io/processor/files/TestMethodFileProcessorTest");
//		String filename = "Multiargs";
//		File f = new File(currentDir, filename+".java.txt");
//		
//		FileProcessor fp = new TestMethodFileProcessor.Builder()
//				.file(f.toPath())
//				.outputDir(currentDir.toPath())
//				.outputFilename(filename+"_parsed")
//				.fileExtension("txt")
//				.build();
//		fp.processFile();
//	}
}
