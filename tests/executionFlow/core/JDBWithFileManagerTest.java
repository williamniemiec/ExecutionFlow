package executionFlow.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class JDBWithFileManagerTest
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
		
		int lastLineTestMethod = ;
		JDB jdb = new JDB(lastLineTestMethod);
		tp_jdb = jdb.getTestPaths(collector.getMethodInfo());
	}
	
	@Test
	public void call_test_try()
	{
		executionFlow.core.files.test_try tt = new executionFlow.core.files.test_try();
		tt.tryCatchMethod_try(2);
	}
}
