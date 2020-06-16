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
import executionFlow.info.MethodInvokerInfo;
import executionFlow.runtime.SkipCollection;


/**
 * Tests integration between {@link JDB} and {@link FileManager}.
 */
@SkipCollection
public class JDBWithFileManagerTest
{
	/**
	 * Tests integration using as example 
	 * {@link examples.controlFlow.ControlFlowTest#ifElseTest_earlyReturn()} method.
	 * 
	 * @throws IOException If an error occurs in file parsing or during the 
	 * computation of the test path
	 */
	@Test
	public void ControlFlowTest_ifElseTest_earlyReturn() throws IOException  
	{
		List<List<Integer>> tp_jdb;
		int invocationLine = 19;
		int lastLineTestMethod = 23;
		
		// Defines which methods will be collected
		String testMethodSignature = "controlFlow.ControlFlowTest.ifElseTest_earlyReturn()";
		String methodSignature = "controlFlow.TestClass_ControlFlow.ifElseMethod(int)";
		
		MethodInvokerInfo ifElseMethod = new ConstructorInvokerInfoBuilder.ClassMethodInfoBuilder()
				.classPath(new File("bin/controlFlow/TestClass_ControlFlow.class").getAbsolutePath())
				.testClassPath(new File("bin/controlFlow/ControlFlowTest.class").getAbsolutePath())
				.srcPath(new File("examples/controlFlow/TestClass_ControlFlow.java").getAbsolutePath())
				.testSrcPath(new File("examples/controlFlow/ControlFlowTest.java").getAbsolutePath())
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.testMethodSignature(testMethodSignature)
				.methodName("ifElseMethod")
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
			tp_jdb = jdb.getTestPaths(ifElseMethod);
		} finally {
			methodFileManager.revertParse();
			testMethodFileManager.revertCompilation();
		}
		
		assertEquals(Arrays.asList(25,26), tp_jdb.get(0));
	}
	
	/**
	 * Tests integration using as example 
	 * {@link examples.controlFlow.ControlFlowTest#ifElseTest()} method.
	 * 
	 * @throws IOException If an error occurs in file parsing or during the 
	 * computation of the test path
	 */
	@Test
	public void ControlFlowTest_ifElseTest() throws IOException  
	{
		List<List<Integer>> tp_jdb;
		int invocationLine = 29;
		int lastLineTestMethod = 33;
		
		// Defines which methods will be collected
		String testMethodSignature = "controlFlow.ControlFlowTest.ifElseTest()";
		String methodSignature = "controlFlow.TestClass_ControlFlow.ifElseMethod()";
		
		MethodInvokerInfo ifElseMethod = new ConstructorInvokerInfoBuilder.ClassMethodInfoBuilder()
				.classPath(new File("bin/controlFlow/TestClass_ControlFlow.class").getAbsolutePath())
				.testClassPath(new File("bin/controlFlow/ControlFlowTest.class").getAbsolutePath())
				.srcPath(new File("examples/controlFlow/TestClass_ControlFlow.java").getAbsolutePath())
				.testSrcPath(new File("examples/controlFlow/ControlFlowTest.java").getAbsolutePath())
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.testMethodSignature(testMethodSignature)
				.methodName("ifElseMethod")
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
			tp_jdb = jdb.getTestPaths(ifElseMethod);
		} finally {
			methodFileManager.revertParse();
			testMethodFileManager.revertCompilation();
		}
		
		assertEquals(Arrays.asList(25,29,31,32,39), tp_jdb.get(0));
	}
	
	/**
	 * Tests integration using as example 
	 * {@link examples.controlFlow.ControlFlowTest#ifElseTest2()} method.
	 * 
	 * @throws IOException If an error occurs in file parsing or during the 
	 * computation of the test path
	 */
	@Test
	public void ControlFlowTest_ifElseTest2() throws IOException  
	{
		List<List<Integer>> tp_jdb;
		int invocationLine = 39;
		int lastLineTestMethod = 43;
		
		// Defines which methods will be collected
		String testMethodSignature = "controlFlow.ControlFlowTest.ifElseTest2()";
		String methodSignature = "controlFlow.TestClass_ControlFlow.ifElseMethod()";
		
		MethodInvokerInfo ifElseMethod = new ConstructorInvokerInfoBuilder.ClassMethodInfoBuilder()
				.classPath(new File("bin/controlFlow/TestClass_ControlFlow.class").getAbsolutePath())
				.testClassPath(new File("bin/controlFlow/ControlFlowTest.class").getAbsolutePath())
				.srcPath(new File("examples/controlFlow/TestClass_ControlFlow.java").getAbsolutePath())
				.testSrcPath(new File("examples/controlFlow/ControlFlowTest.java").getAbsolutePath())
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.testMethodSignature(testMethodSignature)
				.methodName("ifElseMethod")
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
			tp_jdb = jdb.getTestPaths(ifElseMethod);
		} finally {
			methodFileManager.revertParse();
			testMethodFileManager.revertCompilation();
		}
		
		assertEquals(Arrays.asList(25,29,31,33,34,39), tp_jdb.get(0));
	}
	
	/**
	 * Tests integration using as example 
	 * {@link examples.controlFlow.ControlFlowTest#ifElseTest3()} method.
	 * 
	 * @throws IOException If an error occurs in file parsing or during the 
	 * computation of the test path
	 */
	@Test
	public void ControlFlowTest_ifElseTest3() throws IOException  
	{
		List<List<Integer>> tp_jdb;
		int invocationLine = 49;
		int lastLineTestMethod = 53;
		
		// Defines which methods will be collected
		String testMethodSignature = "controlFlow.ControlFlowTest.ifElseTest3()";
		String methodSignature = "controlFlow.TestClass_ControlFlow.ifElseMethod()";
		
		MethodInvokerInfo ifElseMethod = new ConstructorInvokerInfoBuilder.ClassMethodInfoBuilder()
				.classPath(new File("bin/controlFlow/TestClass_ControlFlow.class").getAbsolutePath())
				.testClassPath(new File("bin/controlFlow/ControlFlowTest.class").getAbsolutePath())
				.srcPath(new File("examples/controlFlow/TestClass_ControlFlow.java").getAbsolutePath())
				.testSrcPath(new File("examples/controlFlow/ControlFlowTest.java").getAbsolutePath())
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.testMethodSignature(testMethodSignature)
				.methodName("ifElseMethod")
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
			tp_jdb = jdb.getTestPaths(ifElseMethod);
		} finally {
			methodFileManager.revertParse();
			testMethodFileManager.revertCompilation();
		}
		
		assertEquals(Arrays.asList(25,29,31,33,35,36,39), tp_jdb.get(0));
	}
	
	/**
	 * Tests integration using as example 
	 * {@link examples.controlFlow.ControlFlowTest#tryCatchTest1()} method.
	 * 
	 * @throws IOException If an error occurs in file parsing or during the 
	 * computation of the test path
	 */
	@Test
	public void ControlFlowTest_tryCatchTest1() throws IOException  
	{
		List<List<Integer>> tp_jdb;
		int invocationLine = 59;
		int lastLineTestMethod = 60;
		
		// Defines which methods will be collected
		String testMethodSignature = "controlFlow.ControlFlowTest.tryCatchTest1()";
		String methodSignature = "controlFlow.TestClass_ControlFlow.tryCatchMethod_try()";
		
		MethodInvokerInfo tryCatchMethod_try = new ConstructorInvokerInfoBuilder.ClassMethodInfoBuilder()
				.classPath(new File("bin/controlFlow/TestClass_ControlFlow.class").getAbsolutePath())
				.testClassPath(new File("bin/controlFlow/ControlFlowTest.class").getAbsolutePath())
				.srcPath(new File("examples/controlFlow/TestClass_ControlFlow.java").getAbsolutePath())
				.testSrcPath(new File("examples/controlFlow/ControlFlowTest.java").getAbsolutePath())
				.invocationLine(invocationLine)
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
	
	/**
	 * Tests integration using as example 
	 * {@link examples.controlFlow.ControlFlowTest#tryCatchTest2()} method.
	 * 
	 * @throws IOException If an error occurs in file parsing or during the 
	 * computation of the test path
	 */
	@Test
	public void ControlFlowTest_tryCatchTest2() throws IOException  
	{
		List<List<Integer>> tp_jdb;
		int invocationLine = 66;
		int lastLineTestMethod = 67;
		
		// Defines which methods will be collected
		String testMethodSignature = "controlFlow.ControlFlowTest.tryCatchTest2()";
		String methodSignature = "controlFlow.TestClass_ControlFlow.tryCatchMethod_catch()";
		
		MethodInvokerInfo tryCatchMethod_catch = new ConstructorInvokerInfoBuilder.ClassMethodInfoBuilder()
				.classPath(new File("bin/controlFlow/TestClass_ControlFlow.class").getAbsolutePath())
				.testClassPath(new File("bin/controlFlow/ControlFlowTest.class").getAbsolutePath())
				.srcPath(new File("examples/controlFlow/TestClass_ControlFlow.java").getAbsolutePath())
				.testSrcPath(new File("examples/controlFlow/ControlFlowTest.java").getAbsolutePath())
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.testMethodSignature(testMethodSignature)
				.methodName("tryCatchMethod_catch")
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
			tp_jdb = jdb.getTestPaths(tryCatchMethod_catch);
		} finally {
			methodFileManager.revertParse();
			testMethodFileManager.revertCompilation();
		}
		
		assertEquals(Arrays.asList(73,75,76,77,78,79), tp_jdb.get(0));
	}
	
	/**
	 * Tests integration using as example 
	 * {@link examples.controlFlow.ControlFlowTest#switchCaseTest()} method.
	 * 
	 * @throws IOException If an error occurs in file parsing or during the 
	 * computation of the test path
	 */
	@Test
	public void ControlFlowTest_switchCaseTest() throws IOException  
	{
		List<List<Integer>> tp_jdb;
		int invocationLine = 74;
		int lastLineTestMethod = 75;
		
		// Defines which methods will be collected
		String testMethodSignature = "controlFlow.ControlFlowTest.switchCaseTest()";
		String methodSignature = "controlFlow.TestClass_ControlFlow.switchCaseMethod(char)";
		
		MethodInvokerInfo switchCaseTest = new ConstructorInvokerInfoBuilder.ClassMethodInfoBuilder()
				.classPath(new File("bin/controlFlow/TestClass_ControlFlow.class").getAbsolutePath())
				.testClassPath(new File("bin/controlFlow/ControlFlowTest.class").getAbsolutePath())
				.srcPath(new File("examples/controlFlow/TestClass_ControlFlow.java").getAbsolutePath())
				.testSrcPath(new File("examples/controlFlow/ControlFlowTest.java").getAbsolutePath())
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.testMethodSignature(testMethodSignature)
				.methodName("switchCaseMethod")
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
			tp_jdb = jdb.getTestPaths(switchCaseTest);
		} finally {
			methodFileManager.revertParse();
			testMethodFileManager.revertCompilation();
		}
		
		assertEquals(Arrays.asList(90,92,100,101,102,103,104,123), tp_jdb.get(0));
	}
	
	/**
	 * Tests integration using as example 
	 * {@link examples.controlFlow.ControlFlowTest#doWhileTest()} method.
	 * 
	 * @throws IOException If an error occurs in file parsing or during the 
	 * computation of the test path
	 */
	@Test
	public void ControlFlowTest_doWhileTest() throws IOException  
	{
		List<List<Integer>> tp_jdb;
		int invocationLine = 82;
		int lastLineTestMethod = 83;
		
		// Defines which methods will be collected
		String testMethodSignature = "controlFlow.ControlFlowTest.doWhileTest()";
		String methodSignature = "controlFlow.TestClass_ControlFlow.doWhileMethod(int,int)";
		
		MethodInvokerInfo doWhileMethod = new ConstructorInvokerInfoBuilder.ClassMethodInfoBuilder()
				.classPath(new File("bin/controlFlow/TestClass_ControlFlow.class").getAbsolutePath())
				.testClassPath(new File("bin/controlFlow/ControlFlowTest.class").getAbsolutePath())
				.srcPath(new File("examples/controlFlow/TestClass_ControlFlow.java").getAbsolutePath())
				.testSrcPath(new File("examples/controlFlow/ControlFlowTest.java").getAbsolutePath())
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.testMethodSignature(testMethodSignature)
				.methodName("doWhileMethod")
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
			tp_jdb = jdb.getTestPaths(doWhileMethod);
		} finally {
			methodFileManager.revertParse();
			testMethodFileManager.revertCompilation();
		}
		
		assertEquals(Arrays.asList(135,137,140,141,142,143,140,141,142,143,140,141,142,143,140,141,142,143,140,141,142,143,145), tp_jdb.get(0));
	}
	
	/**
	 * Tests integration using as example 
	 * {@link examples.controlFlow.ControlFlowTest#inlineWhile()} method.
	 * 
	 * @throws IOException If an error occurs in file parsing or during the 
	 * computation of the test path
	 */
	@Test
	public void ControlFlowTest_inlineWhile() throws IOException  
	{
		List<List<Integer>> tp_jdb;
		int invocationLine = 90;
		int lastLineTestMethod = 91;
		
		// Defines which methods will be collected
		String testMethodSignature = "controlFlow.ControlFlowTest.inlineWhile()";
		String methodSignature = "controlFlow.TestClass_ControlFlow.inlineWhile()";
		
		MethodInvokerInfo inlineWhile = new ConstructorInvokerInfoBuilder.ClassMethodInfoBuilder()
				.classPath(new File("bin/controlFlow/TestClass_ControlFlow.class").getAbsolutePath())
				.testClassPath(new File("bin/controlFlow/ControlFlowTest.class").getAbsolutePath())
				.srcPath(new File("examples/controlFlow/TestClass_ControlFlow.java").getAbsolutePath())
				.testSrcPath(new File("examples/controlFlow/ControlFlowTest.java").getAbsolutePath())
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.testMethodSignature(testMethodSignature)
				.methodName("inlineWhile")
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
			tp_jdb = jdb.getTestPaths(inlineWhile);
		} finally {
			methodFileManager.revertParse();
			testMethodFileManager.revertCompilation();
		}
		
		assertEquals(Arrays.asList(156,158), tp_jdb.get(0));
	}
	
	/**
	 * Tests integration using as example 
	 * {@link examples.controlFlow.ControlFlowTest#inlineDoWhile()} method.
	 * 
	 * @throws IOException If an error occurs in file parsing or during the 
	 * computation of the test path
	 */
	@Test
	public void ControlFlowTest_inlineDoWhile() throws IOException  
	{
		List<List<Integer>> tp_jdb;
		int invocationLine = 98;
		int lastLineTestMethod = 99;
		
		// Defines which methods will be collected
		String testMethodSignature = "controlFlow.ControlFlowTest.inlineDoWhile()";
		String methodSignature = "controlFlow.TestClass_ControlFlow.inlineDoWhile()";
		
		MethodInvokerInfo inlineDoWhile = new ConstructorInvokerInfoBuilder.ClassMethodInfoBuilder()
				.classPath(new File("bin/controlFlow/TestClass_ControlFlow.class").getAbsolutePath())
				.testClassPath(new File("bin/controlFlow/ControlFlowTest.class").getAbsolutePath())
				.srcPath(new File("examples/controlFlow/TestClass_ControlFlow.java").getAbsolutePath())
				.testSrcPath(new File("examples/controlFlow/ControlFlowTest.java").getAbsolutePath())
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.testMethodSignature(testMethodSignature)
				.methodName("inlineDoWhile")
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
			tp_jdb = jdb.getTestPaths(inlineDoWhile);
		} finally {
			methodFileManager.revertParse();
			testMethodFileManager.revertCompilation();
		}
		
		assertEquals(Arrays.asList(180,183,185), tp_jdb.get(0));
	}
	
	/**
	 * Tests integration using as example 
	 * {@link examples.controlFlow.ControlFlowTest#inlineIfElse()} method.
	 * 
	 * @throws IOException If an error occurs in file parsing or during the 
	 * computation of the test path
	 */
	@Test
	public void ControlFlowTest_inlineIfElse() throws IOException  
	{
		List<List<Integer>> tp_jdb;
		int invocationLine = 106;
		int lastLineTestMethod = 107;
		
		// Defines which methods will be collected
		String testMethodSignature = "controlFlow.ControlFlowTest.inlineIfElse()";
		String methodSignature = "controlFlow.TestClass_ControlFlow.inlineIfElse()";
		
		MethodInvokerInfo inlineIfElse = new ConstructorInvokerInfoBuilder.ClassMethodInfoBuilder()
				.classPath(new File("bin/controlFlow/TestClass_ControlFlow.class").getAbsolutePath())
				.testClassPath(new File("bin/controlFlow/ControlFlowTest.class").getAbsolutePath())
				.srcPath(new File("examples/controlFlow/TestClass_ControlFlow.java").getAbsolutePath())
				.testSrcPath(new File("examples/controlFlow/ControlFlowTest.java").getAbsolutePath())
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.testMethodSignature(testMethodSignature)
				.methodName("inlineIfElse")
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
			tp_jdb = jdb.getTestPaths(inlineIfElse);
		} finally {
			methodFileManager.revertParse();
			testMethodFileManager.revertCompilation();
		}
		
		assertEquals(Arrays.asList(169), tp_jdb.get(0));
	}
}
