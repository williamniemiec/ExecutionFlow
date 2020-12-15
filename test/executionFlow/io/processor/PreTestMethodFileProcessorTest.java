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
	public void testMethodWithInnerClass() throws IOException
	{
		File currentDir = new File("test/executionFlow/io/processor/files/PreTestMethodFileProcessorTest");
		String filename = "TestMethodWithInnerClass";
		File f = new File(currentDir, filename+".java.txt");
		
		FileProcessor fp = new PreTestMethodFileProcessor.Builder()
				.file(f.toPath())
				.outputDir(currentDir.toPath())
				.outputFilename(filename+"_parsed")
				.fileExtension("txt")
				.testMethodSignature("org.jfree.chart.AreaChartTest.testDrawWithNullInfo()")
				.build();
		fp.processFile();
	}
	
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
	
	@Test
	public void testMethodInvokedSameLine() throws IOException
	{
		File currentDir = new File("test/executionFlow/io/processor/files/");
		String filename = "test_method_tested_invoked_same_file";
		File f = new File(currentDir, filename+".java.txt");
		
		FileProcessor fp = new PreTestMethodFileProcessor.Builder()
				.file(f.toPath())
				.outputDir(currentDir.toPath())
				.outputFilename(filename+"_parsed")
				.fileExtension("txt")
				.testMethodSignature("examples.againstRequirements.methodInSameFileOfTestMethod()")
				.build();
		fp.processFile();
	}
	
	@Test
	public void testAnonymousClass() throws IOException
	{
		File currentDir = new File("test/executionFlow/io/processor/files/");
		String filename = "test_anonymous_class";
		File f = new File(currentDir, filename+".java.txt");
		
		FileProcessor fp = new PreTestMethodFileProcessor.Builder()
				.file(f.toPath())
				.outputDir(currentDir.toPath())
				.outputFilename(filename+"_parsed")
				.fileExtension("txt")
				.testMethodSignature("examples.override.OverrideTest.testOverloadedMethod3()")
				.build();
		fp.processFile();
	}
}
