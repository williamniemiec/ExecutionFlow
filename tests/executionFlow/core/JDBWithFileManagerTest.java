package executionFlow.core;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import executionFlow.core.file.FileManager;
import executionFlow.core.file.parser.factory.MethodFileParserFactory;
import executionFlow.core.file.parser.factory.TestMethodFileParserFactory;
import executionFlow.info.ClassMethodInfo;
import executionFlow.runtime.SkipCollection;


/**
 * Tests integration between {@link JDB} and {@link FileManager}.
 */
@SkipCollection
public class JDBWithFileManagerTest
{
	/**
	 * Tests integration using as example 
	 * {@link controlFlow.ControlFlowTest#tryCatchTest1()} method.
	 * 
	 * @throws IOException If an error occurs in file parsing or during the 
	 * computation of the test path
	 */
	@Test
	public void tryCatchTest1() throws IOException  
	{
		List<List<Integer>> tp_jdb;
		int lastLineTestMethod = 59;
		
		// Defines which methods will be collected
		String testMethodSignature = "controlFlow.ControlFlowTest.tryCatchTest1()";
		String methodSignature = "controlFlow.TestClass_ControlFlow.tryCatchMethod_try()";
		
		ClassMethodInfo tryCatchMethod_try = new ClassMethodInfo.ClassMethodInfoBuilder()
				.classPath(new File("bin/controlFlow/TestClass_ControlFlow.class").getAbsolutePath())
				.testClassPath(new File("bin/controlFlow/ControlFlowTest.class").getAbsolutePath())
				.srcPath(new File("examples/controlFlow/TestClass_ControlFlow.java").getAbsolutePath())
				.testSrcPath(new File("examples/controlFlow/ControlFlowTest.java").getAbsolutePath())
				.invocationLine(58)
				.methodSignature(methodSignature)
				.testMethodSignature(testMethodSignature)
				.methodName("tryCatchMethod_try")
				.build();

		// Creates file manager for the method file
		FileManager methodFileManager = new FileManager(
			"examples/controlFlow/TestClass_ControlFlow.java",
			new File("bin/controlFlow/").getAbsolutePath(),
			"controlFlow",
			new MethodFileParserFactory()
		);
		
		// Creates file manager for the test method file
		FileManager testMethodFileManager = new FileManager(
				"examples/controlFlow/ControlFlowTest.java",
				new File("bin/controlFlow/").getAbsolutePath(),
				"controlFlow",
				new TestMethodFileParserFactory()
			);
		
		try {
			methodFileManager.parseFile().compileFile();
			testMethodFileManager.parseFile()
				.createClassBackupFile()
				.compileFile();
			
			// Computes test path from debug
			JDB jdb = new JDB(lastLineTestMethod);
			tp_jdb = jdb.getTestPaths(tryCatchMethod_try);
		} finally {
			methodFileManager.revertParse();
			testMethodFileManager.revertCompilation();
		}
		
		assertEquals(Arrays.asList(50,52,53,54,55,56,57,62), tp_jdb.get(0));
	}
}
