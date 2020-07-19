package executionFlow.io.processor;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import executionFlow.io.FileEncoding;
import executionFlow.runtime.SkipCollection;


@SkipCollection
public class InvokedFileProcessorTest
{
	@Test
	public void testEmptyClass() throws IOException
	{
		File currentDir = new File("test/executionFlow/io/processor/files/InvokedFileProcessorTest");
		String filename = "test_empty";
		File f = new File(currentDir, filename+".java.txt");
		
		FileProcessor fp = new InvokedFileProcessor(f.toPath(), currentDir.toPath(), filename+"_parsed", "txt");
		fp.processFile();
	}

	@Test
	public void testParseElse() throws IOException
	{
		File currentDir = new File("test/executionFlow/io/processor/files/InvokedFileProcessorTest");
		String filename = "test_else";
		File f = new File(currentDir, filename+".java.txt");
		
		FileProcessor fp = new InvokedFileProcessor(f.toPath(), currentDir.toPath(), filename+"_parsed", "txt");
		fp.processFile();
	}

	@Test
	public void testParseTry() throws IOException
	{
		File currentDir = new File("test/executionFlow/io/processor/files/InvokedFileProcessorTest");
		String filename = "test_try";
		File f = new File(currentDir, filename+".java.txt");
		
		FileProcessor fp = new InvokedFileProcessor(f.toPath(), currentDir.toPath(), filename+"_parsed", "txt");
		fp.processFile();
	}
	
	@Test
	public void testParseCatch() throws IOException
	{
		File currentDir = new File("test/executionFlow/io/processor/files/InvokedFileProcessorTest");
		String filename = "test_catch";
		File f = new File(currentDir, filename+".java.txt");
		
		FileProcessor fp = new InvokedFileProcessor(f.toPath(), currentDir.toPath(), filename+"_parsed", "txt");
		fp.processFile();
	}
	
	@Test
	public void testParseSwitch() throws IOException
	{
		File currentDir = new File("test/executionFlow/io/processor/files/InvokedFileProcessorTest");
		String filename = "test_switch";
		File f = new File(currentDir, filename+".java.txt");
		
		InvokedFileProcessor fp = new InvokedFileProcessor(f.toPath(), currentDir.toPath(), filename+"_parsed", FileEncoding.ISO_8859_1, "txt");
		fp.processFile();
	}
	
	@Test
	public void testDoWhile() throws IOException
	{
		File currentDir = new File("test/executionFlow/io/processor/files/InvokedFileProcessorTest");
		String filename = "test_doWhile";
		File f = new File(currentDir, filename+".java.txt");
		
		FileProcessor fp = new InvokedFileProcessor(f.toPath(), currentDir.toPath(), filename+"_parsed", "txt");
		fp.processFile();
	}
	
	@Test
	public void testParseElse2() throws IOException
	{
		File currentDir = new File("test/executionFlow/io/processor/files/InvokedFileProcessorTest/complex");
		String filename = "test_else";
		File f = new File(currentDir, filename+".java.txt");
		
		FileProcessor fp = new InvokedFileProcessor(f.toPath(), currentDir.toPath(), filename+"_parsed", "txt");
		fp.processFile();
	}
	
	@Test
	public void testParseTry2() throws IOException
	{
		File currentDir = new File("test/executionFlow/io/processor/files/InvokedFileProcessorTest/complex");
		String filename = "test_try";
		File f = new File(currentDir, filename+".java.txt");
		
		FileProcessor fp = new InvokedFileProcessor(f.toPath(), currentDir.toPath(), filename+"_parsed", "txt");
		fp.processFile();
	}
	
	@Test
	public void testParseCatch2() throws IOException
	{
		File currentDir = new File("test/executionFlow/io/processor/files/InvokedFileProcessorTest/complex");
		String filename = "test_catch";
		File f = new File(currentDir, filename+".java.txt");
		
		FileProcessor fp = new InvokedFileProcessor(f.toPath(), currentDir.toPath(), filename+"_parsed", "txt");
		fp.processFile();
	}
	
	@Test
	public void testParseSwitch2() throws IOException
	{
		File currentDir = new File("test/executionFlow/io/processor/files/InvokedFileProcessorTest/complex");
		String filename = "test_switch";
		File f = new File(currentDir, filename+".java.txt");
		
		FileProcessor fp = new InvokedFileProcessor(f.toPath(), currentDir.toPath(), filename+"_parsed", FileEncoding.ISO_8859_1, "txt");
		fp.processFile();
	}
	
	@Test
	public void testDoWhile2() throws IOException
	{
		File currentDir = new File("test/executionFlow/io/processor/files/InvokedFileProcessorTest/complex");
		String filename = "test_doWhile";
		File f = new File(currentDir, filename+".java.txt");
		
		FileProcessor fp = new InvokedFileProcessor(f.toPath(), currentDir.toPath(), filename+"_parsed", "txt");
		fp.processFile();
	}
	
	@Test
	public void testElseNoCurlyBrackets() throws IOException
	{
		File currentDir = new File("test/executionFlow/io/processor/files/InvokedFileProcessorTest");
		String filename = "test_else_noCurlyBraces";
		File f = new File(currentDir, filename+".java.txt");
		
		FileProcessor fp = new InvokedFileProcessor(f.toPath(), currentDir.toPath(), filename+"_parsed", "txt");
		fp.processFile();
	}
	
	@Test
	public void testForeighCode1() throws IOException
	{
		File currentDir = new File("test/executionFlow/io/processor/files/InvokedFileProcessorTest/foreign");
		String filename = "HelpFormatter";
		File f = new File(currentDir, filename+".java.txt");
		
		FileProcessor fp = new InvokedFileProcessor(f.toPath(), currentDir.toPath(), filename+"_parsed", "txt");
		fp.processFile();
	}

	@Test
	public void testForeighCode2() throws IOException
	{
		File currentDir = new File("test/executionFlow/io/processor/files/InvokedFileProcessorTest/foreign");
		String filename = "MathArrays";
		File f = new File(currentDir, filename+".java.txt");
		
		FileProcessor fp = new InvokedFileProcessor(f.toPath(), currentDir.toPath(), filename+"_parsed", "txt");
		fp.processFile();
	}
	
	@Test
	public void testConstructor() throws IOException
	{
		File currentDir = new File("test/executionFlow/io/processor/files/InvokedFileProcessorTest");
		String filename = "test_constructor";
		File f = new File(currentDir, filename+".java.txt");
		
		FileProcessor fp = new InvokedFileProcessor(f.toPath(), currentDir.toPath(), filename+"_parsed", "txt");
		fp.processFile();
	}
}
