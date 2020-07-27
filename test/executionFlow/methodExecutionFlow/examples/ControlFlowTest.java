package executionFlow.methodExecutionFlow.examples;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import executionFlow.ExecutionFlow;
import executionFlow.MethodExecutionFlow;
import executionFlow.info.CollectorInfo;
import executionFlow.info.MethodInvokedInfo;
import executionFlow.io.FileManager;
import executionFlow.io.FilesManager;
import executionFlow.io.ProcessorType;
import executionFlow.io.processor.factory.PreTestMethodFileProcessorFactory;
import executionFlow.runtime.SkipCollection;
import executionFlow.util.ConsoleOutput;


/**
 * Tests test path computation for the tested methods of 
 * {@link examples.controlFlow.ControlFlowTest} class using 
 * {@link MethodExecutionFlow} class.
 */
@SkipCollection
public class ControlFlowTest 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static FileManager testMethodFileManager;
	private static FilesManager testMethodManager;
	private static final Path PATH_BIN_TEST_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath(), "bin/examples/controlFlow/ControlFlowTest.class");
	private static final Path PATH_SRC_TEST_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath(), "examples/examples/controlFlow/ControlFlowTest.java");
	private static final String PACKAGE_TEST_METHOD = "examples.controlFlow";
	private static final Path PATH_BIN_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath(), "bin/examples/controlFlow/TestClass_ControlFlow.class");
	private static final Path PATH_SRC_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath(), "examples/examples/controlFlow/TestClass_ControlFlow.java");
	
	
	//-------------------------------------------------------------------------
	//		Test preparers
	//-------------------------------------------------------------------------
	/**
	 * @throws		IOException If an error occurs during file parsing
	 * @throws		ClassNotFoundException If class {@link FileManager} was not
	 * found
	 */
	@BeforeClass
	public static void init() throws IOException, ClassNotFoundException
	{
		// Initializes ExecutionFlow
		ExecutionFlow.init();
				
		// Creates backup from original files
		testMethodManager = new FilesManager(ProcessorType.PRE_TEST_METHOD, false);
		
		testMethodFileManager = new FileManager(
			PATH_SRC_TEST_METHOD,
			MethodInvokedInfo.getCompiledFileDirectory(PATH_BIN_TEST_METHOD),
			PACKAGE_TEST_METHOD,
			new PreTestMethodFileProcessorFactory(),
			"original_pre_processing"
		);
		
		// Parses test method
		try {
			ConsoleOutput.showInfo("Pre-processing test method...");
			testMethodManager.parse(testMethodFileManager).compile(testMethodFileManager);
			ConsoleOutput.showInfo("Pre-processing completed");
		} catch (IOException e) {
			testMethodManager.restoreAll();
			testMethodManager.deleteBackup();
			throw e;
		}
	}
	
	/**
	 * Restores original files
	 */
	@AfterClass
	public static void restore()
	{
		ExecutionFlow.testMethodManager.restoreAll();
		ExecutionFlow.testMethodManager.deleteBackup();
		
		ExecutionFlow.invokedManager.restoreAll();
		ExecutionFlow.invokedManager.deleteBackup();
		
		testMethodManager.restoreAll();
		testMethodManager.deleteBackup();
		
		ExecutionFlow.destroy();
	}
	
	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	@Test
	public void ifElseTest_earlyReturn() throws Throwable 
	{
		/**
		 * Stores information about collected methods.
		 * <ul>
		 * 	<li><b>Key:</b> Method invocation line</li>
		 * 	<li><b>Value:</b> List of methods invoked from this line</li>
		 * </ul>
		 */
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();

		List<List<Integer>> testPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		int invocationLine = 19;
		
		// Defines which methods will be collected
		String testMethodSignature = "examples.controlFlow.ControlFlowTest.ifElseTest_earlyReturn()";
		String methodSignature = "examples.controlFlow.TestClass_ControlFlow.ifElseMethod(int)";
		
		MethodInvokedInfo testMethodInfo = new MethodInvokedInfo.MethodInvokedInfoBuilder()
				.binPath(PATH_BIN_TEST_METHOD)
				.methodSignature(testMethodSignature)
				.srcPath(PATH_SRC_TEST_METHOD)
				.build();
		
		MethodInvokedInfo methodInfo = new MethodInvokedInfo.MethodInvokedInfoBuilder()
				.binPath(PATH_BIN_METHOD)
				.srcPath(PATH_SRC_METHOD)
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("ifElseTest_earlyReturn")
				.build();
		
		CollectorInfo ci = new CollectorInfo.CollectorInfoBuilder()
				.methodInfo(methodInfo)
				.testMethodInfo(testMethodInfo)
				.build();
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new MethodExecutionFlow(methodCollector, false);
		
		// Gets test path
		testPaths = ef.execute().getTestPaths(testMethodSignature, methodSignature);
		
		assertEquals(Arrays.asList(25,26), testPaths.get(0));
	}
	
	@Test
	public void ifElseTest() throws Throwable 
	{
		List<List<Integer>> testPaths;
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		int invocationLine = 29;
		
		// Defines which methods will be collected
		String testMethodSignature = "examples.controlFlow.ControlFlowTest.ifElseTest()";
		String methodSignature = "examples.controlFlow.TestClass_ControlFlow.ifElseMethod()";
		
		MethodInvokedInfo testMethodInfo = new MethodInvokedInfo.MethodInvokedInfoBuilder()
				.binPath(PATH_BIN_TEST_METHOD)
				.methodSignature(testMethodSignature)
				.srcPath(PATH_SRC_TEST_METHOD)
				.build();
		
		MethodInvokedInfo methodInfo = new MethodInvokedInfo.MethodInvokedInfoBuilder()
				.binPath(PATH_BIN_METHOD)
				.srcPath(PATH_SRC_METHOD)
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("ifElseMethod")
				.build();
		
		CollectorInfo ci = new CollectorInfo.CollectorInfoBuilder()
				.methodInfo(methodInfo)
				.testMethodInfo(testMethodInfo)
				.build();
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new MethodExecutionFlow(methodCollector, false);
		
		// Gets test path
		testPaths = ef.execute().getTestPaths(testMethodSignature, methodSignature);
		
		assertEquals(Arrays.asList(25,29,31,32,39), testPaths.get(0));
	}
	
	@Test
	public void ifElseTest2() throws Throwable 
	{
		List<List<Integer>> testPaths;
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		int invocationLine = 39;
		
		// Defines which methods will be collected
		String testMethodSignature = "examples.controlFlow.ControlFlowTest.ifElseTest2()";
		String methodSignature = "examples.controlFlow.TestClass_ControlFlow.ifElseMethod()";
		
		MethodInvokedInfo testMethodInfo = new MethodInvokedInfo.MethodInvokedInfoBuilder()
				.binPath(PATH_BIN_TEST_METHOD)
				.methodSignature(testMethodSignature)
				.srcPath(PATH_SRC_TEST_METHOD)
				.build();
		
		MethodInvokedInfo methodInfo = new MethodInvokedInfo.MethodInvokedInfoBuilder()
				.binPath(PATH_BIN_METHOD)
				.srcPath(PATH_SRC_METHOD)
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("ifElseMethod2")
				.build();
		
		CollectorInfo ci = new CollectorInfo.CollectorInfoBuilder()
				.methodInfo(methodInfo)
				.testMethodInfo(testMethodInfo)
				.build();
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new MethodExecutionFlow(methodCollector, false);
		
		// Gets test path
		testPaths = ef.execute().getTestPaths(testMethodSignature, methodSignature);
		
		assertEquals(Arrays.asList(25,29,31,33,34,39), testPaths.get(0));
	}
	
	@Test
	public void ifElseTest3() throws Throwable 
	{
		List<List<Integer>> testPaths;
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		int invocationLine = 49;
		
		// Defines which methods will be collected
		String testMethodSignature = "examples.controlFlow.ControlFlowTest.ifElseTest3()";
		String methodSignature = "examples.controlFlow.TestClass_ControlFlow.ifElseMethod()";
		
		MethodInvokedInfo testMethodInfo = new MethodInvokedInfo.MethodInvokedInfoBuilder()
				.binPath(PATH_BIN_TEST_METHOD)
				.methodSignature(testMethodSignature)
				.srcPath(PATH_SRC_TEST_METHOD)
				.build();
		
		MethodInvokedInfo methodInfo = new MethodInvokedInfo.MethodInvokedInfoBuilder()
				.binPath(PATH_BIN_METHOD)
				.srcPath(PATH_SRC_METHOD)
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("ifElseMethod3")
				.build();
		
		CollectorInfo ci = new CollectorInfo.CollectorInfoBuilder()
				.methodInfo(methodInfo)
				.testMethodInfo(testMethodInfo)
				.build();
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new MethodExecutionFlow(methodCollector, false);

		// Gets test path
		testPaths = ef.execute().getTestPaths(testMethodSignature, methodSignature);
		
		assertEquals(Arrays.asList(25,29,31,33,35,36,39), testPaths.get(0));
	}
	
	@Test
	public void tryCatchTest1() throws Throwable 
	{
		List<List<Integer>> testPaths;
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		int invocationLine = 59;
		
		// Defines which methods will be collected
		String testMethodSignature = "examples.controlFlow.ControlFlowTest.tryCatchTest1()";
		String methodSignature = "examples.controlFlow.TestClass_ControlFlow.tryCatchMethod_try()";
		
		MethodInvokedInfo testMethodInfo = new MethodInvokedInfo.MethodInvokedInfoBuilder()
				.binPath(PATH_BIN_TEST_METHOD)
				.methodSignature(testMethodSignature)
				.srcPath(PATH_SRC_TEST_METHOD)
				.build();
		
		MethodInvokedInfo methodInfo = new MethodInvokedInfo.MethodInvokedInfoBuilder()
				.binPath(PATH_BIN_METHOD)
				.srcPath(PATH_SRC_METHOD)
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("tryCatchMethod_try")
				.build();
		
		CollectorInfo ci = new CollectorInfo.CollectorInfoBuilder()
				.methodInfo(methodInfo)
				.testMethodInfo(testMethodInfo)
				.build();
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new MethodExecutionFlow(methodCollector, false);
		
		// Gets test path
		testPaths = ef.execute().getTestPaths(testMethodSignature, methodSignature);
		
		assertEquals(Arrays.asList(50,52,53,54,55,56,57,62), testPaths.get(0));
	}
	
	@Test
	public void tryCatchTest2() throws Throwable 
	{
		List<List<Integer>> testPaths;
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		int invocationLine = 66;
		
		// Defines which methods will be collected
		String testMethodSignature = "examples.controlFlow.ControlFlowTest.tryCatchTest2()";
		String methodSignature = "examples.controlFlow.TestClass_ControlFlow.tryCatchMethod_catch()";

		MethodInvokedInfo testMethodInfo = new MethodInvokedInfo.MethodInvokedInfoBuilder()
				.binPath(PATH_BIN_TEST_METHOD)
				.methodSignature(testMethodSignature)
				.srcPath(PATH_SRC_TEST_METHOD)
				.build();
		
		MethodInvokedInfo methodInfo = new MethodInvokedInfo.MethodInvokedInfoBuilder()
				.binPath(PATH_BIN_METHOD)
				.srcPath(PATH_SRC_METHOD)
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("tryCatchMethod_catch")
				.build();
		
		CollectorInfo ci = new CollectorInfo.CollectorInfoBuilder()
				.methodInfo(methodInfo)
				.testMethodInfo(testMethodInfo)
				.build();
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new MethodExecutionFlow(methodCollector, false);
		
		// Gets test path
		testPaths = ef.execute().getTestPaths(testMethodSignature, methodSignature);
		
		assertEquals(Arrays.asList(73,75,76,77,78,79), testPaths.get(0));
	}
	
	@Test
	public void switchCaseTest() throws Throwable 
	{
		List<List<Integer>> testPaths;
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		int invocationLine = 74;
		
		// Defines which methods will be collected
		String testMethodSignature = "examples.controlFlow.ControlFlowTest.switchCaseTest()";
		String methodSignature = "examples.controlFlow.TestClass_ControlFlow.switchCaseMethod(char)";

		MethodInvokedInfo testMethodInfo = new MethodInvokedInfo.MethodInvokedInfoBuilder()
				.binPath(PATH_BIN_TEST_METHOD)
				.methodSignature(testMethodSignature)
				.srcPath(PATH_SRC_TEST_METHOD)
				.build();
		
		MethodInvokedInfo methodInfo = new MethodInvokedInfo.MethodInvokedInfoBuilder()
				.binPath(PATH_BIN_METHOD)
				.srcPath(PATH_SRC_METHOD)
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("switchCaseMethod")
				.build();
		
		CollectorInfo ci = new CollectorInfo.CollectorInfoBuilder()
				.methodInfo(methodInfo)
				.testMethodInfo(testMethodInfo)
				.build();

		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new MethodExecutionFlow(methodCollector, false);
		
		// Gets test path
		testPaths = ef.execute().getTestPaths(testMethodSignature, methodSignature);
		
		assertEquals(Arrays.asList(90,92,100,101,102,103,104,123), testPaths.get(0));
	}
	
	@Test
	public void doWhileTest() throws Throwable 
	{
		List<List<Integer>> testPaths;
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		int invocationLine = 82;
		
		// Defines which methods will be collected
		String testMethodSignature = "examples.controlFlow.ControlFlowTest.doWhileTest()";
		String methodSignature = "examples.controlFlow.TestClass_ControlFlow.doWhileMethod(int,int)";

		MethodInvokedInfo testMethodInfo = new MethodInvokedInfo.MethodInvokedInfoBuilder()
				.binPath(PATH_BIN_TEST_METHOD)
				.methodSignature(testMethodSignature)
				.srcPath(PATH_SRC_TEST_METHOD)
				.build();
		
		MethodInvokedInfo methodInfo = new MethodInvokedInfo.MethodInvokedInfoBuilder()
				.binPath(PATH_BIN_METHOD)
				.srcPath(PATH_SRC_METHOD)
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("doWhileMethod")
				.build();
		
		CollectorInfo ci = new CollectorInfo.CollectorInfoBuilder()
				.methodInfo(methodInfo)
				.testMethodInfo(testMethodInfo)
				.build();
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new MethodExecutionFlow(methodCollector, false);
		
		// Gets test path
		testPaths = ef.execute().getTestPaths(testMethodSignature, methodSignature);
		
		assertEquals(Arrays.asList(135,137,140,141,142,143,140,141,142,143,140,141,142,143,140,141,142,143,140,141,142,143,145), testPaths.get(0));
	}
	
	@Test
	public void inlineWhile() throws Throwable 
	{
		List<List<Integer>> testPaths;
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		int invocationLine = 90;
		
		// Defines which methods will be collected
		String testMethodSignature = "examples.controlFlow.ControlFlowTest.inlineWhile()";
		String methodSignature = "examples.controlFlow.TestClass_ControlFlow.inlineWhile(int)";

		MethodInvokedInfo testMethodInfo = new MethodInvokedInfo.MethodInvokedInfoBuilder()
				.binPath(PATH_BIN_TEST_METHOD)
				.methodSignature(testMethodSignature)
				.srcPath(PATH_SRC_TEST_METHOD)
				.build();
		
		MethodInvokedInfo methodInfo = new MethodInvokedInfo.MethodInvokedInfoBuilder()
				.binPath(PATH_BIN_METHOD)
				.srcPath(PATH_SRC_METHOD)
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("inlineWhile")
				.build();
		
		CollectorInfo ci = new CollectorInfo.CollectorInfoBuilder()
				.methodInfo(methodInfo)
				.testMethodInfo(testMethodInfo)
				.build();
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new MethodExecutionFlow(methodCollector, false);
		
		// Gets test path
		testPaths = ef.execute().getTestPaths(testMethodSignature, methodSignature);
		
		assertEquals(Arrays.asList(156,158), testPaths.get(0));
	}
	
	@Test
	public void inlineDoWhile() throws Throwable 
	{
		List<List<Integer>> testPaths;
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		int invocationLine = 98;
		
		// Defines which methods will be collected
		String testMethodSignature = "examples.controlFlow.ControlFlowTest.inlineDoWhile()";
		String methodSignature = "examples.controlFlow.TestClass_ControlFlow.inlineDoWhile(int)";

		MethodInvokedInfo testMethodInfo = new MethodInvokedInfo.MethodInvokedInfoBuilder()
				.binPath(PATH_BIN_TEST_METHOD)
				.methodSignature(testMethodSignature)
				.srcPath(PATH_SRC_TEST_METHOD)
				.build();
		
		MethodInvokedInfo methodInfo = new MethodInvokedInfo.MethodInvokedInfoBuilder()
				.binPath(PATH_BIN_METHOD)
				.srcPath(PATH_SRC_METHOD)
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("inlineDoWhile")
				.build();
		
		CollectorInfo ci = new CollectorInfo.CollectorInfoBuilder()
				.methodInfo(methodInfo)
				.testMethodInfo(testMethodInfo)
				.build();
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new MethodExecutionFlow(methodCollector, false);
		
		// Gets test path
		testPaths = ef.execute().getTestPaths(testMethodSignature, methodSignature);
		
		assertEquals(Arrays.asList(180,183,185), testPaths.get(0));
	}
	
	@Test
	public void inlineIfElse() throws Throwable 
	{
		List<List<Integer>> testPaths;
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		int invocationLine = 106;
		
		// Defines which methods will be collected
		String testMethodSignature = "examples.controlFlow.ControlFlowTest.inlineIfElse()";
		String methodSignature = "examples.controlFlow.TestClass_ControlFlow.inlineIfElse(int)";

		MethodInvokedInfo testMethodInfo = new MethodInvokedInfo.MethodInvokedInfoBuilder()
				.binPath(PATH_BIN_TEST_METHOD)
				.methodSignature(testMethodSignature)
				.srcPath(PATH_SRC_TEST_METHOD)
				.build();
		
		MethodInvokedInfo methodInfo = new MethodInvokedInfo.MethodInvokedInfoBuilder()
				.binPath(PATH_BIN_METHOD)
				.srcPath(PATH_SRC_METHOD)
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("inlineIfElse")
				.build();
		
		CollectorInfo ci = new CollectorInfo.CollectorInfoBuilder()
				.methodInfo(methodInfo)
				.testMethodInfo(testMethodInfo)
				.build();
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new MethodExecutionFlow(methodCollector, false);
		
		// Gets test path
		testPaths = ef.execute().getTestPaths(testMethodSignature, methodSignature);
		
		assertEquals(Arrays.asList(169), testPaths.get(0));
	}
	
	/**
	 * Tests first method used by 
	 * {@link  examples.complexTests.ComplexTests.ifElseSameLine()} test.
	 */
	@Test
	public void ifElseSameLine() throws Throwable 
	{
		List<List<Integer>> testPaths;
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		int invocationLine = 114;
		
		// Defines which methods will be collected
		String testMethodSignature = "examples.complexTests.ComplexTests.ifElseSameLine()";
		String methodSignature = "examples.complexTests.TestClass_ComplexTests.ifElseSameLine(int)";
		
		MethodInvokedInfo testMethodInfo = new MethodInvokedInfo.MethodInvokedInfoBuilder()
				.binPath(PATH_BIN_TEST_METHOD)
				.methodSignature(testMethodSignature)
				.srcPath(PATH_SRC_TEST_METHOD)
				.build();
		
		MethodInvokedInfo methodInfo = new MethodInvokedInfo.MethodInvokedInfoBuilder()
				.binPath(PATH_BIN_METHOD)
				.srcPath(PATH_SRC_METHOD)
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("ifElseSameLine")
				.build();
		
		CollectorInfo ci = new CollectorInfo.CollectorInfoBuilder()
				.methodInfo(methodInfo)
				.testMethodInfo(testMethodInfo)
				.build();
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new MethodExecutionFlow(methodCollector, false);
		
		// Gets test path
		testPaths = ef.execute().getTestPaths(testMethodSignature, methodSignature);
		
		assertEquals(Arrays.asList(197, 201), testPaths.get(0));
	}
	
	/**
	 * Tests second method used by 
	 * {@link  examples.complexTests.ComplexTests.ifElseSameLine()} test.
	 */
	@Test
	public void ifElseSameLine2() throws Throwable 
	{
		List<List<Integer>> testPaths;
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		int invocationLine = 115;
		
		// Defines which methods will be collected
		String testMethodSignature = "examples.complexTests.ComplexTests.ifElseSameLine()";
		String methodSignature = "examples.complexTests.TestClass_ComplexTests.ifElseSameLine(int)";
		
		MethodInvokedInfo testMethodInfo = new MethodInvokedInfo.MethodInvokedInfoBuilder()
				.binPath(PATH_BIN_TEST_METHOD)
				.methodSignature(testMethodSignature)
				.srcPath(PATH_SRC_TEST_METHOD)
				.build();
		
		MethodInvokedInfo methodInfo = new MethodInvokedInfo.MethodInvokedInfoBuilder()
				.binPath(PATH_BIN_METHOD)
				.srcPath(PATH_SRC_METHOD)
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("ifElseSameLine")
				.build();
		
		CollectorInfo ci = new CollectorInfo.CollectorInfoBuilder()
				.methodInfo(methodInfo)
				.testMethodInfo(testMethodInfo)
				.build();
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new MethodExecutionFlow(methodCollector, false);
		
		// Gets test path
		testPaths = ef.execute().getTestPaths(testMethodSignature, methodSignature);
		
		assertEquals(Arrays.asList(197, 198, 201), testPaths.get(0));
	}
}
