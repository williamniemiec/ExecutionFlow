package executionFlow.core;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import executionFlow.info.ClassMethodInfo;
import executionFlow.runtime.SkipMethod;

public class JDBWithFileManagerTest
{
	@SkipMethod
	@Test
	public void test_try() throws Throwable
	{
		List<List<Integer>> tp_jdb;
		
		FileManager fileManager = new FileManager(
			"tests/executionFlow/core/files/test_try.java",
			"bin/executionFlow/core/files",
			"executionFlow.core.files"
		);
		
		String classPath = fileManager.parseFile().compileFile();
		fileManager.revert();
		
		assertEquals("bin\\test_try.class", classPath);
		
		int lastLineTestMethod = 37;
		JDB jdb = new JDB(lastLineTestMethod);
		
		ClassMethodInfo cmi = new ClassMethodInfo.ClassMethodInfoBuilder()
			.testMethodSignature("executionFlow.core.call_test_try()")
			.classPath(classPath)
			.methodName("tryCatchMethod_try")
			.methodSignature("executionFlow.core.files")
			.invocationLine(47)
			.build();
		
		tp_jdb = jdb.getTestPaths(cmi);
		
		System.out.println(tp_jdb);
	}
	
	@SkipMethod
	@Test
	public void call_test_try()
	{
		executionFlow.core.files.test_try tt = new executionFlow.core.files.test_try();
		tt.tryCatchMethod_try(2);
	}
}
