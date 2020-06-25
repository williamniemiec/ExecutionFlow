package executionFlow.core.file.parser;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import executionFlow.core.file.FileEncoding;
import executionFlow.runtime.SkipCollection;


@SkipCollection
public class MethodFileParserTest
{
	@Test
	public void testEmptyClass() throws IOException
	{
		File currentDir = new File("tests/executionFlow/core/file/parser/files/MethodFileParser");
		String filename = "test_empty";
		File f = new File(currentDir, filename+".java.txt");
		
		FileParser fp = new MethodFileParser(f.toPath(), currentDir.toPath(), filename+"_parsed", "txt");
		fp.parseFile();
	}
	
	@Test
	public void testParseElse() throws IOException
	{
		File currentDir = new File("tests/executionFlow/core/file/parser/files/MethodFileParser");
		String filename = "test_else";
		File f = new File(currentDir, filename+".java.txt");
		
		FileParser fp = new MethodFileParser(f.toPath(), currentDir.toPath(), filename+"_parsed", "txt");
		fp.parseFile();
	}
	
	@Test
	public void testParseTry() throws IOException
	{
		File currentDir = new File("tests/executionFlow/core/file/parser/files/MethodFileParser");
		String filename = "test_try";
		File f = new File(currentDir, filename+".java.txt");
		
		FileParser fp = new MethodFileParser(f.toPath(), currentDir.toPath(), filename+"_parsed", "txt");
		fp.parseFile();
	}
	
	@Test
	public void testParseCatch() throws IOException
	{
		File currentDir = new File("tests/executionFlow/core/file/parser/files/MethodFileParser");
		String filename = "test_catch";
		File f = new File(currentDir, filename+".java.txt");
		
		FileParser fp = new MethodFileParser(f.toPath(), currentDir.toPath(), filename+"_parsed", "txt");
		fp.parseFile();
	}
	
	@Test
	public void testParseSwitch() throws IOException
	{
		File currentDir = new File("tests/executionFlow/core/file/parser/files/MethodFileParser");
		String filename = "test_switch";
		File f = new File(currentDir, filename+".java.txt");
		
		MethodFileParser fp = new MethodFileParser(f.toPath(), currentDir.toPath(), filename+"_parsed", FileEncoding.ISO_8859_1, "txt");
		fp.parseFile();
	}
	
	@Test
	public void testDoWhile() throws IOException
	{
		File currentDir = new File("tests/executionFlow/core/file/parser/files/MethodFileParser");
		String filename = "test_doWhile";
		File f = new File(currentDir, filename+".java.txt");
		
		FileParser fp = new MethodFileParser(f.toPath(), currentDir.toPath(), filename+"_parsed", "txt");
		fp.parseFile();
	}
	
	@Test
	public void testParseElse2() throws IOException
	{
		File currentDir = new File("tests/executionFlow/core/file/parser/files/MethodFileParser/complex");
		String filename = "test_else";
		File f = new File(currentDir, filename+".java.txt");
		
		FileParser fp = new MethodFileParser(f.toPath(), currentDir.toPath(), filename+"_parsed", "txt");
		fp.parseFile();
	}
	
	@Test
	public void testParseTry2() throws IOException
	{
		File currentDir = new File("tests/executionFlow/core/file/parser/files/MethodFileParser/complex");
		String filename = "test_try";
		File f = new File(currentDir, filename+".java.txt");
		
		FileParser fp = new MethodFileParser(f.toPath(), currentDir.toPath(), filename+"_parsed", "txt");
		fp.parseFile();
	}
	
	@Test
	public void testParseCatch2() throws IOException
	{
		File currentDir = new File("tests/executionFlow/core/file/parser/files/MethodFileParser/complex");
		String filename = "test_catch";
		File f = new File(currentDir, filename+".java.txt");
		
		FileParser fp = new MethodFileParser(f.toPath(), currentDir.toPath(), filename+"_parsed", "txt");
		fp.parseFile();
	}
	
	@Test
	public void testParseSwitch2() throws IOException
	{
		File currentDir = new File("tests/executionFlow/core/file/parser/files/MethodFileParser/complex");
		String filename = "test_switch";
		File f = new File(currentDir, filename+".java.txt");
		
		FileParser fp = new MethodFileParser(f.toPath(), currentDir.toPath(), filename+"_parsed", FileEncoding.ISO_8859_1, "txt");
		fp.parseFile();
	}
	
	@Test
	public void testDoWhile2() throws IOException
	{
		File currentDir = new File("tests/executionFlow/core/file/parser/files/MethodFileParser/complex");
		String filename = "test_doWhile";
		File f = new File(currentDir, filename+".java.txt");
		
		FileParser fp = new MethodFileParser(f.toPath(), currentDir.toPath(), filename+"_parsed", "txt");
		fp.parseFile();
	}
	
	@Test
	public void testElseNoCurlyBrackets() throws IOException
	{
		File currentDir = new File("tests/executionFlow/core/file/parser/files/MethodFileParser");
		String filename = "test_else_noCurlyBraces";
		File f = new File(currentDir, filename+".java.txt");
		
		FileParser fp = new MethodFileParser(f.toPath(), currentDir.toPath(), filename+"_parsed", "txt");
		fp.parseFile();
	}
	
	@Test
	public void testForeighCode1() throws IOException
	{
		File currentDir = new File("tests/executionFlow/core/file/parser/files/MethodFileParser/foreign");
		String filename = "HelpFormatter";
		File f = new File(currentDir, filename+".java.txt");
		
		FileParser fp = new MethodFileParser(f.toPath(), currentDir.toPath(), filename+"_parsed", "txt");
		fp.parseFile();
	}

	@Test
	public void testForeighCode2() throws IOException
	{
		File currentDir = new File("tests/executionFlow/core/file/parser/files/MethodFileParser/foreign");
		String filename = "MathArrays";
		File f = new File(currentDir, filename+".java.txt");
		
		FileParser fp = new MethodFileParser(f.toPath(), currentDir.toPath(), filename+"_parsed", "txt");
		fp.parseFile();
	}
	
	@Test
	public void testConstructor() throws IOException
	{
		File currentDir = new File("tests/executionFlow/core/file/parser/files/MethodFileParser");
		String filename = "test_constructor";
		File f = new File(currentDir, filename+".java.txt");
		
		FileParser fp = new MethodFileParser(f.toPath(), currentDir.toPath(), filename+"_parsed", "txt");
		fp.parseFile();
	}
}
