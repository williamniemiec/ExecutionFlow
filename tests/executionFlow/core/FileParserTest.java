package executionFlow.core;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import executionFlow.runtime.SkipCollection;


@SkipCollection
public class FileParserTest
{
	/*
	@Test
	public void testEmptyClass()
	{
		String currentDir = new File("tests\\executionFlow\\core\\files").getAbsolutePath();
		String filename = "test_empty";
		File f = new File(currentDir, filename+".java");
		
		FileParser fp = new FileParser(f.getAbsolutePath(), currentDir, filename+"_parsed");
		fp.parseFile();
	}
	
	@Test
	public void testParseElse()
	{
		String currentDir = new File("tests\\executionFlow\\core\\files").getAbsolutePath();
		String filename = "test_else";
		File f = new File(currentDir, filename+".java");
		
		FileParser fp = new FileParser(f.getAbsolutePath(), currentDir, filename+"_parsed");
		fp.parseFile();
	}
	
	@Test
	public void testParseTry()
	{
		String currentDir = new File("tests\\executionFlow\\core\\files").getAbsolutePath();
		String filename = "test_try";
		File f = new File(currentDir, filename+".java");
		
		FileParser fp = new FileParser(f.getAbsolutePath(), currentDir, filename+"_parsed");
		fp.parseFile();
	}
	
	@Test
	public void testParseCatch()
	{
		String currentDir = new File("tests\\executionFlow\\core\\files").getAbsolutePath();
		String filename = "test_catch";
		File f = new File(currentDir, filename+".java");
		
		FileParser fp = new FileParser(f.getAbsolutePath(), currentDir, filename+"_parsed");
		fp.parseFile();
	}*/
	
	@Test
	public void testParseSwitch() throws IOException
	{
		String currentDir = new File("tests\\executionFlow\\core\\files").getAbsolutePath();
		String filename = "test_switch";
		File f = new File(currentDir, filename+".java");
		
		FileParser fp = new FileParser(f.getAbsolutePath(), currentDir, filename+"_parsed", FileCharset.ISO_8859_1);
		fp.parseFile();
	}
	/*
	@Test
	public void testDoWhile()
	{
		String currentDir = new File("tests\\executionFlow\\core\\files").getAbsolutePath();
		String filename = "test_doWhile";
		File f = new File(currentDir, filename+".java");
		
		FileParser fp = new FileParser(f.getAbsolutePath(), currentDir, filename+"_parsed");
		fp.parseFile();
	}*/
	/*
	@Test
	public void testParseElse2()
	{
		String currentDir = new File("tests\\executionFlow\\core\\files\\complex").getAbsolutePath();
		String filename = "test_else";
		File f = new File(currentDir, filename+".java");
		
		FileParser fp = new FileParser(f.getAbsolutePath(), currentDir, filename+"_parsed");
		fp.parseFile();
	}
	
	@Test
	public void testParseTry2()
	{
		String currentDir = new File("tests\\executionFlow\\core\\files\\complex").getAbsolutePath();
		String filename = "test_try";
		File f = new File(currentDir, filename+".java");
		
		FileParser fp = new FileParser(f.getAbsolutePath(), currentDir, filename+"_parsed");
		fp.parseFile();
	}
	
	@Test
	public void testParseCatch2()
	{
		String currentDir = new File("tests\\executionFlow\\core\\files\\complex").getAbsolutePath();
		String filename = "test_catch";
		File f = new File(currentDir, filename+".java");
		
		FileParser fp = new FileParser(f.getAbsolutePath(), currentDir, filename+"_parsed");
		fp.parseFile();
	}
	
	@Test
	public void testParseSwitch2()
	{
		String currentDir = new File("tests\\executionFlow\\core\\files\\complex").getAbsolutePath();
		String filename = "test_switch";
		File f = new File(currentDir, filename+".java");
		
		FileParser fp = new FileParser(f.getAbsolutePath(), currentDir, filename+"_parsed");
		fp.parseFile();
	}
	
	@Test
	public void testDoWhile2()
	{
		String currentDir = new File("tests\\executionFlow\\core\\files\\complex").getAbsolutePath();
		String filename = "test_doWhile";
		File f = new File(currentDir, filename+".java");
		
		FileParser fp = new FileParser(f.getAbsolutePath(), currentDir, filename+"_parsed");
		fp.parseFile();
	}
	
	@Test
	public void testElseNoCurlyBrackets()
	{
		String currentDir = new File("tests\\executionFlow\\core\\files\\noCurlyBraces").getAbsolutePath();
		String filename = "test_else_noCurlyBraces";
		File f = new File(currentDir, filename+".java");
		
		FileParser fp = new FileParser(f.getAbsolutePath(), currentDir, filename+"_parsed");
		fp.parseFile();
	}
	
	@Test
	public void testForeighCode1()
	{
		String currentDir = new File("tests\\executionFlow\\core\\files\\foreign").getAbsolutePath();
		String filename = "HelpFormatter";
		File f = new File(currentDir, filename+".java");
		
		FileParser fp = new FileParser(f.getAbsolutePath(), currentDir, filename+"_parsed");
		fp.parseFile();
	}

	@Test
	public void testForeighCode2()
	{
		String currentDir = new File("tests\\executionFlow\\core\\files\\foreign").getAbsolutePath();
		String filename = "MathArrays";
		File f = new File(currentDir, filename+".java");
		
		FileParser fp = new FileParser(f.getAbsolutePath(), currentDir, filename+"_parsed");
		fp.parseFile();
	}*/
}
