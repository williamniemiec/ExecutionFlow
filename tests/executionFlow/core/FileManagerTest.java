package executionFlow.core;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import executionFlow.core.file.FileManager;
import executionFlow.core.file.parser.factory.MethodFileParserFactory;
import executionFlow.core.file.parser.factory.TestMethodFileParserFactory;
import executionFlow.runtime.SkipCollection;


@SkipCollection
public class FileManagerTest 
{
	@Test
	public void test_try() throws Exception
	{
		FileManager fileManager = new FileManager(
			"tests/executionFlow/core/files/test_try.java",
			"bin/executionFlow/core/files",
			"executionFlow.core.files",
			new MethodFileParserFactory()
		);
		
		String classPath = fileManager.parseFile().compileFile();
		//fileManager.revert();
		
		assertEquals("bin\\test_try.class", classPath);
	}
	
	@Test
	public void test_switch() throws Exception
	{
		FileManager fileManager = new FileManager(
			"tests/executionFlow/core/files/test_switch.java",
			"bin/executionFlow/core/files",
			"executionFlow.core.files",
			new MethodFileParserFactory()
		);
		
		String classPath = fileManager.parseFile().compileFile();
		//fileManager.revert();
		
		assertEquals("bin\\test_switch.class", classPath);
	}
	
	@Test
	public void testMethodFileParserTest() throws IOException
	{
		FileManager fileManager = new FileManager(
			"tests/executionFlow/core/tests/JUnitTest.java",
			"bin/executionFlow/core/tests",
			"executionFlow.core.files.tests",
			new TestMethodFileParserFactory()
		);
		
		fileManager.createClassBackupFile().parseFile().revertCompilation();
	}
}
