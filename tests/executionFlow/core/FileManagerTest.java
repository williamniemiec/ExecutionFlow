package executionFlow.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class FileManagerTest 
{
	@Test
	public void test_try() throws Exception
	{
		FileManager fileManager = new FileManager(
			"tests/executionFlow/core/files/test_try.java",
			"bin/executionFlow/core/files",
			"executionFlow.core.files"
		);
		
		String classPath = fileManager.parseFile().compileFile();
		fileManager.revert();
		
		assertEquals("bin\\test_try.class", classPath);
	}
}
