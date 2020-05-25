package executionFlow.core;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.Test;

import executionFlow.core.file.FileManager;
import executionFlow.core.file.parser.factory.MethodFileParserFactory;
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
			new File("tests/executionFlow/core/files/test_try.java").getAbsolutePath(),
			new File("bin/executionFlow/core/files").getAbsolutePath(),
			"executionFlow.core.files",
			new MethodFileParserFactory()
		);
		
		String classPath = fileManager.parseFile().compileFile();
		fileManager.revert();
		
		int lastLineTestMethod = 56;
		JDB jdb = new JDB(lastLineTestMethod);
		
		ClassMethodInfo cmi = new ClassMethodInfo.ClassMethodInfoBuilder()
			.testMethodSignature("executionFlow.core.JDBWithFileManagerTest.call_test_try()")
			.classPath(classPath)
			.srcPath(new File("tests/executionFlow/core/files/test_try.java").getAbsolutePath())
			.methodName("tryCatchMethod_try")
			.methodSignature("executionFlow.core.files.test_try.tryCatchMethod_try(int)")
			.invocationLine(55)
			.testClassPath(new File("bin/executionFlow/core/JDBWithFileManagerTest.class").getAbsolutePath())
			.testSrcPath(new File("tests/executionFlow/core/JDBWithFileManagerTest.java").getAbsolutePath())
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
