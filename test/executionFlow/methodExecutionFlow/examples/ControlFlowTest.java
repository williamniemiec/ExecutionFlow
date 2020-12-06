package executionFlow.methodExecutionFlow.examples;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import executionFlow.ExecutionFlow;
import executionFlow.MethodExecutionFlow;
import executionFlow.info.CollectorInfo;
import executionFlow.info.MethodInvokedInfo;
import executionFlow.io.FileManager;
import executionFlow.methodExecutionFlow.MethodExecutionFlowTest;
import executionFlow.runtime.SkipCollection;


/**
 * Tests test path computation for the tested methods of 
 * {@link examples.controlFlow.ControlFlowTest} class using 
 * {@link MethodExecutionFlow} class.
 */
@SkipCollection
public class ControlFlowTest extends MethodExecutionFlowTest
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final Path PATH_BIN_TEST_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/controlFlow/ControlFlowTest.class");
	private static final Path PATH_SRC_TEST_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/controlFlow/ControlFlowTest.java");
	private static final String PACKAGE_TEST_METHOD = "examples.controlFlow";
	private static final Path PATH_BIN_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/controlFlow/TestClass_ControlFlow.class");
	private static final Path PATH_SRC_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/controlFlow/TestClass_ControlFlow.java");
	
	
	//-------------------------------------------------------------------------
	//		Test preparers
	//-------------------------------------------------------------------------
	/**
	 * @param		classSignature Test class signature
	 * @param		testMethodSignature Test method signature
	 * 
	 * @throws		IOException If an error occurs during file parsing
	 * @throws		ClassNotFoundException If class {@link FileManager} was not
	 * found
	 */
	public void init(String classSignature, String testMethodSignature) 
			throws IOException, ClassNotFoundException
	{
		init(classSignature, testMethodSignature, PATH_SRC_TEST_METHOD, 
				PATH_BIN_TEST_METHOD, PACKAGE_TEST_METHOD);
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
		String testMethodSignature, methodSignature;
		MethodInvokedInfo testMethodInfo, methodInfo;
		CollectorInfo ci;
		int invocationLine = 19;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.controlFlow.ControlFlowTest.ifElseTest_earlyReturn()";
		methodSignature = "examples.controlFlow.TestClass_ControlFlow.ifElseMethod(int)";
		
		init("examples.controlFlow.TestClass_ControlFlow", testMethodSignature);
		
		testMethodInfo = new MethodInvokedInfo.Builder()
				.binPath(PATH_BIN_TEST_METHOD)
				.methodSignature(testMethodSignature)
				.srcPath(PATH_SRC_TEST_METHOD)
				.build();
		
		methodInfo = new MethodInvokedInfo.Builder()
				.binPath(PATH_BIN_METHOD)
				.srcPath(PATH_SRC_METHOD)
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("ifElseTest_earlyReturn")
				.build();
		
		ci = new CollectorInfo.Builder()
				.methodInfo(methodInfo)
				.testMethodInfo(testMethodInfo)
				.build();
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new MethodExecutionFlow(methodCollector, false);
		
		// Gets test path
		testPaths = ef.execute().getTestPaths(testMethodSignature, methodSignature);
		
		assertEquals(Arrays.asList(7,8), testPaths.get(0));
	}
	
	@Test
	public void ifElseTest() throws Throwable 
	{
		List<List<Integer>> testPaths;
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		MethodInvokedInfo testMethodInfo, methodInfo;
		CollectorInfo ci;
		int invocationLine = 29;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.controlFlow.ControlFlowTest.ifElseTest()";
		methodSignature = "examples.controlFlow.TestClass_ControlFlow.ifElseMethod()";
		
		init("examples.controlFlow.TestClass_ControlFlow", testMethodSignature);
		
		testMethodInfo = new MethodInvokedInfo.Builder()
				.binPath(PATH_BIN_TEST_METHOD)
				.methodSignature(testMethodSignature)
				.srcPath(PATH_SRC_TEST_METHOD)
				.build();
		
		methodInfo = new MethodInvokedInfo.Builder()
				.binPath(PATH_BIN_METHOD)
				.srcPath(PATH_SRC_METHOD)
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("ifElseMethod")
				.build();
		
		ci = new CollectorInfo.Builder()
				.methodInfo(methodInfo)
				.testMethodInfo(testMethodInfo)
				.build();
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new MethodExecutionFlow(methodCollector, false);
		
		// Gets test path
		testPaths = ef.execute().getTestPaths(testMethodSignature, methodSignature);
		
		assertEquals(Arrays.asList(7,10,11,12,20), testPaths.get(0));
	}
	
	@Test
	public void ifElseTest2() throws Throwable 
	{
		List<List<Integer>> testPaths;
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		MethodInvokedInfo testMethodInfo, methodInfo;
		CollectorInfo ci;
		int invocationLine = 39;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.controlFlow.ControlFlowTest.ifElseTest2()";
		methodSignature = "examples.controlFlow.TestClass_ControlFlow.ifElseMethod()";
		
		init("examples.controlFlow.TestClass_ControlFlow", testMethodSignature);
		
		testMethodInfo = new MethodInvokedInfo.Builder()
				.binPath(PATH_BIN_TEST_METHOD)
				.methodSignature(testMethodSignature)
				.srcPath(PATH_SRC_TEST_METHOD)
				.build();
		
		methodInfo = new MethodInvokedInfo.Builder()
				.binPath(PATH_BIN_METHOD)
				.srcPath(PATH_SRC_METHOD)
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("ifElseMethod2")
				.build();
		
		ci = new CollectorInfo.Builder()
				.methodInfo(methodInfo)
				.testMethodInfo(testMethodInfo)
				.build();
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new MethodExecutionFlow(methodCollector, false);
		
		// Gets test path
		testPaths = ef.execute().getTestPaths(testMethodSignature, methodSignature);
		
		assertEquals(Arrays.asList(7,10,11,14,15,20), testPaths.get(0));
	}
	
	@Test
	public void ifElseTest3() throws Throwable 
	{
		List<List<Integer>> testPaths;
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		MethodInvokedInfo testMethodInfo, methodInfo;
		CollectorInfo ci;
		int invocationLine = 49;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.controlFlow.ControlFlowTest.ifElseTest3()";
		methodSignature = "examples.controlFlow.TestClass_ControlFlow.ifElseMethod()";
		
		init("examples.controlFlow.TestClass_ControlFlow", testMethodSignature);
		
		testMethodInfo = new MethodInvokedInfo.Builder()
				.binPath(PATH_BIN_TEST_METHOD)
				.methodSignature(testMethodSignature)
				.srcPath(PATH_SRC_TEST_METHOD)
				.build();
		
		methodInfo = new MethodInvokedInfo.Builder()
				.binPath(PATH_BIN_METHOD)
				.srcPath(PATH_SRC_METHOD)
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("ifElseMethod3")
				.build();
		
		ci = new CollectorInfo.Builder()
				.methodInfo(methodInfo)
				.testMethodInfo(testMethodInfo)
				.build();
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new MethodExecutionFlow(methodCollector, false);

		// Gets test path
		testPaths = ef.execute().getTestPaths(testMethodSignature, methodSignature);
		
		assertEquals(Arrays.asList(7,10,11,14,17,18,20), testPaths.get(0));
	}
	
	@Test
	public void tryCatchTest1() throws Throwable 
	{
		List<List<Integer>> testPaths;
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		MethodInvokedInfo testMethodInfo, methodInfo;
		CollectorInfo ci;
		int invocationLine = 59;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.controlFlow.ControlFlowTest.tryCatchTest1()";
		methodSignature = "examples.controlFlow.TestClass_ControlFlow.tryCatchMethod_try()";
		
		init("examples.controlFlow.TestClass_ControlFlow", testMethodSignature);
		
		testMethodInfo = new MethodInvokedInfo.Builder()
				.binPath(PATH_BIN_TEST_METHOD)
				.methodSignature(testMethodSignature)
				.srcPath(PATH_SRC_TEST_METHOD)
				.build();
		
		methodInfo = new MethodInvokedInfo.Builder()
				.binPath(PATH_BIN_METHOD)
				.srcPath(PATH_SRC_METHOD)
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("tryCatchMethod_try")
				.build();
		
		ci = new CollectorInfo.Builder()
				.methodInfo(methodInfo)
				.testMethodInfo(testMethodInfo)
				.build();
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new MethodExecutionFlow(methodCollector, false);
		
		// Gets test path
		testPaths = ef.execute().getTestPaths(testMethodSignature, methodSignature);
		
		assertEquals(Arrays.asList(23,24,25,26,27,28,29,34), testPaths.get(0));
	}
	
	@Test
	public void tryCatchTest2() throws Throwable 
	{
		List<List<Integer>> testPaths;
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		MethodInvokedInfo testMethodInfo, methodInfo;
		CollectorInfo ci;
		int invocationLine = 66;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.controlFlow.ControlFlowTest.tryCatchTest2()";
		methodSignature = "examples.controlFlow.TestClass_ControlFlow.tryCatchMethod_catch()";

		init("examples.controlFlow.TestClass_ControlFlow", testMethodSignature);		
		
		testMethodInfo = new MethodInvokedInfo.Builder()
				.binPath(PATH_BIN_TEST_METHOD)
				.methodSignature(testMethodSignature)
				.srcPath(PATH_SRC_TEST_METHOD)
				.build();
		
		methodInfo = new MethodInvokedInfo.Builder()
				.binPath(PATH_BIN_METHOD)
				.srcPath(PATH_SRC_METHOD)
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("tryCatchMethod_catch")
				.build();
		
		ci = new CollectorInfo.Builder()
				.methodInfo(methodInfo)
				.testMethodInfo(testMethodInfo)
				.build();
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new MethodExecutionFlow(methodCollector, false);
		
		// Gets test path
		testPaths = ef.execute().getTestPaths(testMethodSignature, methodSignature);
		
		assertEquals(Arrays.asList(37,38,39,40,42,43), testPaths.get(0));
	}
	
	@Test
	public void switchCaseTest() throws Throwable 
	{
		List<List<Integer>> testPaths;
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		MethodInvokedInfo testMethodInfo, methodInfo;
		CollectorInfo ci;
		int invocationLine = 74;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.controlFlow.ControlFlowTest.switchCaseTest()";
		methodSignature = "examples.controlFlow.TestClass_ControlFlow.switchCaseMethod(char)";

		init("examples.controlFlow.TestClass_ControlFlow", testMethodSignature);
		
		testMethodInfo = new MethodInvokedInfo.Builder()
				.binPath(PATH_BIN_TEST_METHOD)
				.methodSignature(testMethodSignature)
				.srcPath(PATH_SRC_TEST_METHOD)
				.build();
		
		methodInfo = new MethodInvokedInfo.Builder()
				.binPath(PATH_BIN_METHOD)
				.srcPath(PATH_SRC_METHOD)
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("switchCaseMethod")
				.build();
		
		ci = new CollectorInfo.Builder()
				.methodInfo(methodInfo)
				.testMethodInfo(testMethodInfo)
				.build();

		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new MethodExecutionFlow(methodCollector, false);
		
		// Gets test path
		testPaths = ef.execute().getTestPaths(testMethodSignature, methodSignature);
		
		assertEquals(Arrays.asList(47,48,55,56,57,58,59,77), testPaths.get(0));
	}
	
	@Test
	public void doWhileTest() throws Throwable 
	{
		List<List<Integer>> testPaths;
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		MethodInvokedInfo testMethodInfo, methodInfo;
		CollectorInfo ci;
		int invocationLine = 82;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.controlFlow.ControlFlowTest.doWhileTest()";
		methodSignature = "examples.controlFlow.TestClass_ControlFlow.doWhileMethod(int,int)";

		init("examples.controlFlow.TestClass_ControlFlow", testMethodSignature);
		
		testMethodInfo = new MethodInvokedInfo.Builder()
				.binPath(PATH_BIN_TEST_METHOD)
				.methodSignature(testMethodSignature)
				.srcPath(PATH_SRC_TEST_METHOD)
				.build();
		
		methodInfo = new MethodInvokedInfo.Builder()
				.binPath(PATH_BIN_METHOD)
				.srcPath(PATH_SRC_METHOD)
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("doWhileMethod")
				.build();
		
		ci = new CollectorInfo.Builder()
				.methodInfo(methodInfo)
				.testMethodInfo(testMethodInfo)
				.build();
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new MethodExecutionFlow(methodCollector, false);
		
		// Gets test path
		testPaths = ef.execute().getTestPaths(testMethodSignature, methodSignature);
		
		assertEquals(Arrays.asList(80,83,86,87,88,90,86,87,88,90,86,87,88,90,86,87,88,90,86,87,88,90,91), testPaths.get(0));
	}
	
	@Test
	public void inlineWhile() throws Throwable 
	{
		List<List<Integer>> testPaths;
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		MethodInvokedInfo testMethodInfo, methodInfo;
		CollectorInfo ci;
		int invocationLine = 92;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.controlFlow.ControlFlowTest.inlineWhile()";
		methodSignature = "examples.controlFlow.TestClass_ControlFlow.inlineWhile(int)";

		init("examples.controlFlow.TestClass_ControlFlow", testMethodSignature);
		
		testMethodInfo = new MethodInvokedInfo.Builder()
				.binPath(PATH_BIN_TEST_METHOD)
				.methodSignature(testMethodSignature)
				.srcPath(PATH_SRC_TEST_METHOD)
				.build();
		
		methodInfo = new MethodInvokedInfo.Builder()
				.binPath(PATH_BIN_METHOD)
				.srcPath(PATH_SRC_METHOD)
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("inlineWhile")
				.build();
		
		ci = new CollectorInfo.Builder()
				.methodInfo(methodInfo)
				.testMethodInfo(testMethodInfo)
				.build();
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new MethodExecutionFlow(methodCollector, false);
		
		// Gets test path
		testPaths = ef.execute().getTestPaths(testMethodSignature, methodSignature);
		
		assertEquals(Arrays.asList(95,96,95,96,95,98), testPaths.get(0));
	}
	
	@Test
	public void inlineDoWhile() throws Throwable 
	{
		List<List<Integer>> testPaths;
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		MethodInvokedInfo testMethodInfo, methodInfo;
		CollectorInfo ci;
		int invocationLine = 100;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.controlFlow.ControlFlowTest.inlineDoWhile()";
		methodSignature = "examples.controlFlow.TestClass_ControlFlow.inlineDoWhile(int)";

		init("examples.controlFlow.TestClass_ControlFlow", testMethodSignature);
		
		testMethodInfo = new MethodInvokedInfo.Builder()
				.binPath(PATH_BIN_TEST_METHOD)
				.methodSignature(testMethodSignature)
				.srcPath(PATH_SRC_TEST_METHOD)
				.build();
		
		methodInfo = new MethodInvokedInfo.Builder()
				.binPath(PATH_BIN_METHOD)
				.srcPath(PATH_SRC_METHOD)
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("inlineDoWhile")
				.build();
		
		ci = new CollectorInfo.Builder()
				.methodInfo(methodInfo)
				.testMethodInfo(testMethodInfo)
				.build();
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new MethodExecutionFlow(methodCollector, false);
		
		// Gets test path
		testPaths = ef.execute().getTestPaths(testMethodSignature, methodSignature);
		
		assertEquals(Arrays.asList(109,112,113,115,112,113,115,112,116), testPaths.get(0));
	}
	
	@Test
	public void inlineIfElse() throws Throwable 
	{
		List<List<Integer>> testPaths;
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		MethodInvokedInfo testMethodInfo, methodInfo;
		CollectorInfo ci;
		int invocationLine = 108;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.controlFlow.ControlFlowTest.inlineIfElse()";
		methodSignature = "examples.controlFlow.TestClass_ControlFlow.inlineIfElse(int)";

		init("examples.controlFlow.TestClass_ControlFlow", testMethodSignature);
		
		testMethodInfo = new MethodInvokedInfo.Builder()
				.binPath(PATH_BIN_TEST_METHOD)
				.methodSignature(testMethodSignature)
				.srcPath(PATH_SRC_TEST_METHOD)
				.build();
		
		methodInfo = new MethodInvokedInfo.Builder()
				.binPath(PATH_BIN_METHOD)
				.srcPath(PATH_SRC_METHOD)
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("inlineIfElse")
				.build();
		
		ci = new CollectorInfo.Builder()
				.methodInfo(methodInfo)
				.testMethodInfo(testMethodInfo)
				.build();
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new MethodExecutionFlow(methodCollector, false);
		
		// Gets test path
		testPaths = ef.execute().getTestPaths(testMethodSignature, methodSignature);
		
		assertEquals(Arrays.asList(101,102), testPaths.get(0));
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
		String testMethodSignature, methodSignature;
		MethodInvokedInfo testMethodInfo, methodInfo;
		CollectorInfo ci;
		int invocationLine = 116;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.controlFlow.ControlFlowTest.ifElseSameLine()";
		methodSignature = "examples.controlFlow.TestClass_ControlFlow.ifElseSameLine(int)";
		
		init("examples.controlFlow.TestClass_ControlFlow", testMethodSignature);
		
		testMethodInfo = new MethodInvokedInfo.Builder()
				.binPath(PATH_BIN_TEST_METHOD)
				.methodSignature(testMethodSignature)
				.srcPath(PATH_SRC_TEST_METHOD)
				.build();
		
		methodInfo = new MethodInvokedInfo.Builder()
				.binPath(PATH_BIN_METHOD)
				.srcPath(PATH_SRC_METHOD)
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("ifElseSameLine")
				.build();
		
		ci = new CollectorInfo.Builder()
				.methodInfo(methodInfo)
				.testMethodInfo(testMethodInfo)
				.build();
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new MethodExecutionFlow(methodCollector, false);
		
		// Gets test path
		testPaths = ef.execute().getTestPaths(testMethodSignature, methodSignature);
		
		assertEquals(Arrays.asList(119,120,125), testPaths.get(0));
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
		String testMethodSignature, methodSignature;
		MethodInvokedInfo testMethodInfo, methodInfo;
		CollectorInfo ci;
		int invocationLine = 117;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.controlFlow.ControlFlowTest.ifElseSameLine()";
		methodSignature = "examples.controlFlow.TestClass_ControlFlow.ifElseSameLine(int)";
		
		init("examples.controlFlow.TestClass_ControlFlow", testMethodSignature);
		
		testMethodInfo = new MethodInvokedInfo.Builder()
				.binPath(PATH_BIN_TEST_METHOD)
				.methodSignature(testMethodSignature)
				.srcPath(PATH_SRC_TEST_METHOD)
				.build();
		
		methodInfo = new MethodInvokedInfo.Builder()
				.binPath(PATH_BIN_METHOD)
				.srcPath(PATH_SRC_METHOD)
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("ifElseSameLine")
				.build();
		
		ci = new CollectorInfo.Builder()
				.methodInfo(methodInfo)
				.testMethodInfo(testMethodInfo)
				.build();
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new MethodExecutionFlow(methodCollector, false);
		
		// Gets test path
		testPaths = ef.execute().getTestPaths(testMethodSignature, methodSignature);
		
		assertEquals(Arrays.asList(119,122,123,125), testPaths.get(0));
	}
}
