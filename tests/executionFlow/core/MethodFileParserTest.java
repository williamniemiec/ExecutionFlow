package executionFlow.core;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import executionFlow.core.file.FileEncoding;
import executionFlow.core.file.parser.FileParser;
import executionFlow.core.file.parser.MethodFileParser;
import executionFlow.runtime.SkipCollection;


@SkipCollection
public class MethodFileParserTest
{/*
	@Test
	public void testEmptyClass() throws IOException
	{
		String currentDir = new File("tests\\executionFlow\\core\\files").getAbsolutePath();
		String filename = "test_empty";
		File f = new File(currentDir, filename+".java");
		
		FileParser fp = new MethodFileParser(f.getAbsolutePath(), currentDir, filename+"_parsed");
		fp.parseFile();
	}
	
	@Test
	public void testParseElse() throws IOException
	{
		String currentDir = new File("tests\\executionFlow\\core\\files").getAbsolutePath();
		String filename = "test_else";
		File f = new File(currentDir, filename+".java");
		
		FileParser fp = new MethodFileParser(f.getAbsolutePath(), currentDir, filename+"_parsed");
		fp.parseFile();
	}
	
	@Test
	public void testParseTry() throws IOException
	{
		String currentDir = new File("tests\\executionFlow\\core\\files").getAbsolutePath();
		String filename = "test_try";
		File f = new File(currentDir, filename+".java");
		
		FileParser fp = new MethodFileParser(f.getAbsolutePath(), currentDir, filename+"_parsed");
		fp.parseFile();
	}
	
	@Test
	public void testParseCatch() throws IOException
	{
		String currentDir = new File("tests\\executionFlow\\core\\files").getAbsolutePath();
		String filename = "test_catch";
		File f = new File(currentDir, filename+".java");
		
		FileParser fp = new MethodFileParser(f.getAbsolutePath(), currentDir, filename+"_parsed");
		fp.parseFile();
	}
	
	@Test
	public void testParseSwitch() throws IOException
	{
		String currentDir = new File("tests\\executionFlow\\core\\files").getAbsolutePath();
		String filename = "test_switch";
		File f = new File(currentDir, filename+".java");
		
		MethodFileParser fp = new MethodFileParser(f.getAbsolutePath(), currentDir, filename+"_parsed", FileEncoding.ISO_8859_1);
		fp.parseFile();
	}
	*/
	@Test
	public void testDoWhile() throws IOException
	{
		String currentDir = new File("tests\\executionFlow\\core\\files").getAbsolutePath();
		String filename = "test_doWhile";
		File f = new File(currentDir, filename+".java");
		
		FileParser fp = new MethodFileParser(f.getAbsolutePath(), currentDir, filename+"_parsed");
		fp.parseFile();
	}
	/*
	@Test
	public void testParseElse2() throws IOException
	{
		String currentDir = new File("tests\\executionFlow\\core\\files\\complex").getAbsolutePath();
		String filename = "test_else";
		File f = new File(currentDir, filename+".java");
		
		FileParser fp = new MethodFileParser(f.getAbsolutePath(), currentDir, filename+"_parsed");
		fp.parseFile();
	}
	
	@Test
	public void testParseTry2() throws IOException
	{
		String currentDir = new File("tests\\executionFlow\\core\\files\\complex").getAbsolutePath();
		String filename = "test_try";
		File f = new File(currentDir, filename+".java");
		
		FileParser fp = new MethodFileParser(f.getAbsolutePath(), currentDir, filename+"_parsed");
		fp.parseFile();
	}
	
	@Test
	public void testParseCatch2() throws IOException
	{
		String currentDir = new File("tests\\executionFlow\\core\\files\\complex").getAbsolutePath();
		String filename = "test_catch";
		File f = new File(currentDir, filename+".java");
		
		FileParser fp = new MethodFileParser(f.getAbsolutePath(), currentDir, filename+"_parsed");
		fp.parseFile();
	}
	
	@Test
	public void testParseSwitch2() throws IOException
	{
		String currentDir = new File("tests\\executionFlow\\core\\files\\complex").getAbsolutePath();
		String filename = "test_switch";
		File f = new File(currentDir, filename+".java");
		
		FileParser fp = new MethodFileParser(f.getAbsolutePath(), currentDir, filename+"_parsed");
		fp.parseFile();
	}
	
	@Test
	public void testDoWhile2() throws IOException
	{
		String currentDir = new File("tests\\executionFlow\\core\\files\\complex").getAbsolutePath();
		String filename = "test_doWhile";
		File f = new File(currentDir, filename+".java");
		
		FileParser fp = new MethodFileParser(f.getAbsolutePath(), currentDir, filename+"_parsed");
		fp.parseFile();
	}
	
	@Test
	public void testElseNoCurlyBrackets() throws IOException
	{
		String currentDir = new File("tests\\executionFlow\\core\\files\\noCurlyBraces").getAbsolutePath();
		String filename = "test_else_noCurlyBraces";
		File f = new File(currentDir, filename+".java");
		
		FileParser fp = new MethodFileParser(f.getAbsolutePath(), currentDir, filename+"_parsed");
		fp.parseFile();
	}
	
	@Test
	public void testForeighCode1() throws IOException
	{
		String currentDir = new File("tests\\executionFlow\\core\\files\\foreign").getAbsolutePath();
		String filename = "HelpFormatter";
		File f = new File(currentDir, filename+".java");
		
		FileParser fp = new MethodFileParser(f.getAbsolutePath(), currentDir, filename+"_parsed");
		fp.parseFile();
	}

	@Test
	public void testForeighCode2() throws IOException
	{
		String currentDir = new File("tests\\executionFlow\\core\\files\\foreign").getAbsolutePath();
		String filename = "MathArrays";
		File f = new File(currentDir, filename+".java");
		
		FileParser fp = new MethodFileParser(f.getAbsolutePath(), currentDir, filename+"_parsed");
		fp.parseFile();
	}*/
}
