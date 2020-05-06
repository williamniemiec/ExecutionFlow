package executionFlow.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;

import org.junit.Test;

import executionFlow.runtime.SkipCollection;

@SkipCollection
public class FileParserTest
{
	/*
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
	}
	
	@Test
	public void testParseSwitch()
	{
		String currentDir = new File("tests\\executionFlow\\core\\files").getAbsolutePath();
		String filename = "test_switch";
		File f = new File(currentDir, filename+".java");
		
		FileParser fp = new FileParser(f.getAbsolutePath(), currentDir, filename+"_parsed");
		fp.parseFile();
	}
	
	@Test
	public void testDoWhile()
	{
		String currentDir = new File("tests\\executionFlow\\core\\files").getAbsolutePath();
		String filename = "test_doWhile";
		File f = new File(currentDir, filename+".java");
		
		FileParser fp = new FileParser(f.getAbsolutePath(), currentDir, filename+"_parsed");
		fp.parseFile();
	}
	*/
	//--------------------------------------------------------------------------------------
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
	
}
