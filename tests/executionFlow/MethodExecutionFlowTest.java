package executionFlow;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import executionFlow.core.file.FileManager;
import executionFlow.core.file.MethodManager;
import executionFlow.core.file.ParserType;
import executionFlow.core.file.parser.factory.AssertFileParserFactory;
import executionFlow.info.CollectorInfo;
import executionFlow.info.MethodInvokerInfo;
import executionFlow.info.SignaturesInfo;
import executionFlow.runtime.SkipCollection;


/**
 * Tests test path computation from methods through 
 * {@link MethodExecutionFlow} class.
 * 
 * @apiNote		Tests test path computation from methods of class 
 * {@link examples.controlFlow.ControlFlowTest}.
 */
@SkipCollection
public class MethodExecutionFlowTest 
{
	private static FileManager testMethodFileManager;
	private static MethodManager testMethodManager;
	private static final Path classPath = Path.of("bin/examples/controlFlow/TestClass_ControlFlow.class");
	private static final Path testClassPath = Path.of("bin/examples/controlFlow/ControlFlowTest.class");
	private static final Path srcPath = Path.of("examples/examples/controlFlow/TestClass_ControlFlow.java");
	private static final Path testSrcPath = Path.of("examples/examples/controlFlow/ControlFlowTest.java");
	private static final String testClassPackage = "examples.controlFlow";
	
	
	@BeforeClass
	public static void parseAssert() throws IOException, ClassNotFoundException
	{
		testMethodManager = new MethodManager(ParserType.ASSERT_TEST_METHOD, false);
		
		testMethodFileManager = new FileManager(
			testSrcPath,
			MethodInvokerInfo.getCompiledFileDirectory(testClassPath),
			testClassPackage,
			new AssertFileParserFactory(),
			"original_assert"
		);
		
		try {
			testMethodManager.parse(testMethodFileManager).compile(testMethodFileManager);			
		} catch (IOException e) {
			testMethodManager.restoreAll();
			testMethodManager.deleteBackup();
			throw e;
		}
	}
	
	@AfterClass
	public static void restore()
	{
		ExecutionFlow.testMethodManager.restoreAll();
		ExecutionFlow.testMethodManager.deleteBackup();
		
		ExecutionFlow.methodManager.restoreAll();
		ExecutionFlow.methodManager.deleteBackup();
		
		testMethodManager.restoreAll();
		testMethodManager.deleteBackup();
	}
	
	@Test
	public void ifElseTest_earlyReturn() throws Throwable 
	{
		/**
		 * Stores information about collected methods.<hr/>
		 * <ul>
		 * 		<li><b>Key:</b> Method invocation line</li>
		 * 		<li><b>Value:</b> List of methods invoked from this line</li>
		 * </ul>
		 */
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		
		/**
		 * Stores computed test paths from a class.<br />
		 * <ul>
		 * 		<li><b>Key:</b> test_method_signature + '$' + method_signature</li>
		 * 		<li>
		 * 			<b>Value:</b> 
		 * 			<ul>
		 * 				<li><b>Key:</b> Test method signature and method signature</li>
		 * 				<li><b>Value:</b> Test path</li>
		 * 			</ul>
		 * 		</li>
		 * </ul>
		 */
		
		Map<String, Map<SignaturesInfo, List<Integer>>> classPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		Collection<List<Integer>> testPaths;
		List<Integer> testPath;
		int invocationLine = 19;
		
		// Defines which methods will be collected
		String testMethodSignature = "examples.controlFlow.ControlFlowTest.ifElseTest_earlyReturn()";
		String methodSignature = "examples.controlFlow.TestClass_ControlFlow.ifElseMethod(int)";
		
		MethodInvokerInfo testMethodInfo = new MethodInvokerInfo.MethodInvokerInfoBuilder()
				.classPath(testClassPath)
				.methodSignature(testMethodSignature)
				.srcPath(testSrcPath)
				.build();
		
		MethodInvokerInfo methodInfo = new MethodInvokerInfo.MethodInvokerInfoBuilder()
				.classPath(classPath)
				.srcPath(srcPath)
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
		ExecutionFlow ef = new MethodExecutionFlow(methodCollector);
		classPaths = ef.execute().getClassTestPaths();
		
		// Gets test path
		testPaths = getTestPaths(classPaths, testMethodSignature, methodSignature);
		testPath = getFirstTestPath(testPaths);
		
		assertEquals(Arrays.asList(25,26), testPath);
	}
	
	@Test
	public void ifElseTest() throws Throwable 
	{
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		Map<String, Map<SignaturesInfo, List<Integer>>> classPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		Collection<List<Integer>> testPaths;
		List<Integer> testPath;
		int invocationLine = 29;
		
		// Defines which methods will be collected
		String testMethodSignature = "examples.controlFlow.ControlFlowTest.ifElseTest()";
		String methodSignature = "examples.controlFlow.TestClass_ControlFlow.ifElseMethod()";
		
		MethodInvokerInfo testMethodInfo = new MethodInvokerInfo.MethodInvokerInfoBuilder()
				.classPath(testClassPath)
				.methodSignature(testMethodSignature)
				.srcPath(testSrcPath)
				.build();
		
		MethodInvokerInfo methodInfo = new MethodInvokerInfo.MethodInvokerInfoBuilder()
				.classPath(classPath)
				.srcPath(srcPath)
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
		ExecutionFlow ef = new MethodExecutionFlow(methodCollector);
		classPaths = ef.execute().getClassTestPaths();
		
		// Gets test path
		testPaths = getTestPaths(classPaths, testMethodSignature, methodSignature);
		testPath = getFirstTestPath(testPaths);
		
		assertEquals(Arrays.asList(25,29,31,32,39), testPath);
	}
	
	@Test
	public void ifElseTest2() throws Throwable 
	{
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		Map<String, Map<SignaturesInfo, List<Integer>>> classPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		Collection<List<Integer>> testPaths;
		List<Integer> testPath;
		int invocationLine = 39;
		
		// Defines which methods will be collected
		String testMethodSignature = "examples.controlFlow.ControlFlowTest.ifElseTest2()";
		String methodSignature = "examples.controlFlow.TestClass_ControlFlow.ifElseMethod()";
		
		MethodInvokerInfo testMethodInfo = new MethodInvokerInfo.MethodInvokerInfoBuilder()
				.classPath(testClassPath)
				.methodSignature(testMethodSignature)
				.srcPath(testSrcPath)
				.build();
		
		MethodInvokerInfo methodInfo = new MethodInvokerInfo.MethodInvokerInfoBuilder()
				.classPath(classPath)
				.srcPath(srcPath)
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
		ExecutionFlow ef = new MethodExecutionFlow(methodCollector);
		classPaths = ef.execute().getClassTestPaths();
		
		// Gets test path
		testPaths = getTestPaths(classPaths, testMethodSignature, methodSignature);
		testPath = getFirstTestPath(testPaths);
		
		assertEquals(Arrays.asList(25,29,31,33,34,39), testPath);
	}
	
	@Test
	public void ifElseTest3() throws Throwable 
	{
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		Map<String, Map<SignaturesInfo, List<Integer>>> classPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		Collection<List<Integer>> testPaths;
		List<Integer> testPath;
		int invocationLine = 49;
		
		// Defines which methods will be collected
		String testMethodSignature = "examples.controlFlow.ControlFlowTest.ifElseTest3()";
		String methodSignature = "examples.controlFlow.TestClass_ControlFlow.ifElseMethod()";
		
		MethodInvokerInfo testMethodInfo = new MethodInvokerInfo.MethodInvokerInfoBuilder()
				.classPath(testClassPath)
				.methodSignature(testMethodSignature)
				.srcPath(testSrcPath)
				.build();
		
		MethodInvokerInfo methodInfo = new MethodInvokerInfo.MethodInvokerInfoBuilder()
				.classPath(classPath)
				.srcPath(srcPath)
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
		ExecutionFlow ef = new MethodExecutionFlow(methodCollector);
		classPaths = ef.execute().getClassTestPaths();
		
		// Gets test path
		testPaths = getTestPaths(classPaths, testMethodSignature, methodSignature);
		testPath = getFirstTestPath(testPaths);
		
		assertEquals(Arrays.asList(25,29,31,33,35,36,39), testPath);
	}
	
	@Test
	public void tryCatchTest1() throws Throwable 
	{
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		Map<String, Map<SignaturesInfo, List<Integer>>> classPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		Collection<List<Integer>> testPaths;
		List<Integer> testPath;
		int invocationLine = 59;
		
		// Defines which methods will be collected
		String testMethodSignature = "examples.controlFlow.ControlFlowTest.tryCatchTest1()";
		String methodSignature = "examples.controlFlow.TestClass_ControlFlow.tryCatchMethod_try()";
		
		MethodInvokerInfo testMethodInfo = new MethodInvokerInfo.MethodInvokerInfoBuilder()
				.classPath(testClassPath)
				.methodSignature(testMethodSignature)
				.srcPath(testSrcPath)
				.build();
		
		MethodInvokerInfo methodInfo = new MethodInvokerInfo.MethodInvokerInfoBuilder()
				.classPath(classPath)
				.srcPath(srcPath)
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
		ExecutionFlow ef = new MethodExecutionFlow(methodCollector);
		classPaths = ef.execute().getClassTestPaths();
		
		// Gets test path
		testPaths = getTestPaths(classPaths, testMethodSignature, methodSignature);
		testPath = getFirstTestPath(testPaths);
		
		assertEquals(Arrays.asList(50,52,53,54,55,56,57,62), testPath);
	}
	
	@Test
	public void tryCatchTest2() throws Throwable 
	{
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		Map<String, Map<SignaturesInfo, List<Integer>>> classPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		Collection<List<Integer>> testPaths;
		List<Integer> testPath;
		int invocationLine = 66;
		
		// Defines which methods will be collected
		String testMethodSignature = "examples.controlFlow.ControlFlowTest.tryCatchTest2()";
		String methodSignature = "examples.controlFlow.TestClass_ControlFlow.tryCatchMethod_catch()";

		MethodInvokerInfo testMethodInfo = new MethodInvokerInfo.MethodInvokerInfoBuilder()
				.classPath(testClassPath)
				.methodSignature(testMethodSignature)
				.srcPath(testSrcPath)
				.build();
		
		MethodInvokerInfo methodInfo = new MethodInvokerInfo.MethodInvokerInfoBuilder()
				.classPath(classPath)
				.srcPath(srcPath)
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
		ExecutionFlow ef = new MethodExecutionFlow(methodCollector);
		classPaths = ef.execute().getClassTestPaths();
		
		// Gets test path
		testPaths = getTestPaths(classPaths, testMethodSignature, methodSignature);
		testPath = getFirstTestPath(testPaths);
		
		assertEquals(Arrays.asList(73,75,76,77,78,79), testPath);
	}
	
	@Test
	public void switchCaseTest() throws Throwable 
	{
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		Map<String, Map<SignaturesInfo, List<Integer>>> classPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		Collection<List<Integer>> testPaths;
		List<Integer> testPath;
		int invocationLine = 74;
		
		// Defines which methods will be collected
		String testMethodSignature = "examples.controlFlow.ControlFlowTest.switchCaseTest()";
		String methodSignature = "examples.controlFlow.TestClass_ControlFlow.switchCaseMethod(char)";

		MethodInvokerInfo testMethodInfo = new MethodInvokerInfo.MethodInvokerInfoBuilder()
				.classPath(testClassPath)
				.methodSignature(testMethodSignature)
				.srcPath(testSrcPath)
				.build();
		
		MethodInvokerInfo methodInfo = new MethodInvokerInfo.MethodInvokerInfoBuilder()
				.classPath(classPath)
				.srcPath(srcPath)
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
		ExecutionFlow ef = new MethodExecutionFlow(methodCollector);
		classPaths = ef.execute().getClassTestPaths();
		
		// Gets test path
		testPaths = getTestPaths(classPaths, testMethodSignature, methodSignature);
		testPath = getFirstTestPath(testPaths);
		
		assertEquals(Arrays.asList(90,92,100,101,102,103,104,123), testPath);
	}
	
	@Test
	public void doWhileTest() throws Throwable 
	{
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		Map<String, Map<SignaturesInfo, List<Integer>>> classPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		Collection<List<Integer>> testPaths;
		List<Integer> testPath;
		int invocationLine = 82;
		
		// Defines which methods will be collected
		String testMethodSignature = "examples.controlFlow.ControlFlowTest.doWhileTest()";
		String methodSignature = "examples.controlFlow.TestClass_ControlFlow.doWhileMethod(int,int)";

		MethodInvokerInfo testMethodInfo = new MethodInvokerInfo.MethodInvokerInfoBuilder()
				.classPath(testClassPath)
				.methodSignature(testMethodSignature)
				.srcPath(testSrcPath)
				.build();
		
		MethodInvokerInfo methodInfo = new MethodInvokerInfo.MethodInvokerInfoBuilder()
				.classPath(classPath)
				.srcPath(srcPath)
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
		ExecutionFlow ef = new MethodExecutionFlow(methodCollector);
		classPaths = ef.execute().getClassTestPaths();
		
		// Gets test path
		testPaths = getTestPaths(classPaths, testMethodSignature, methodSignature);
		testPath = getFirstTestPath(testPaths);
		
		assertEquals(Arrays.asList(135,137,140,141,142,143,140,141,142,143,140,141,142,143,140,141,142,143,140,141,142,143,145), testPath);
	}
	
	@Test
	public void inlineWhile() throws Throwable 
	{
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		Map<String, Map<SignaturesInfo, List<Integer>>> classPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		Collection<List<Integer>> testPaths;
		List<Integer> testPath;
		int invocationLine = 90;
		
		// Defines which methods will be collected
		String testMethodSignature = "examples.controlFlow.ControlFlowTest.inlineWhile()";
		String methodSignature = "examples.controlFlow.TestClass_ControlFlow.inlineWhile(int)";

		MethodInvokerInfo testMethodInfo = new MethodInvokerInfo.MethodInvokerInfoBuilder()
				.classPath(testClassPath)
				.methodSignature(testMethodSignature)
				.srcPath(testSrcPath)
				.build();
		
		MethodInvokerInfo methodInfo = new MethodInvokerInfo.MethodInvokerInfoBuilder()
				.classPath(classPath)
				.srcPath(srcPath)
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
		ExecutionFlow ef = new MethodExecutionFlow(methodCollector);
		classPaths = ef.execute().getClassTestPaths();
		
		// Gets test path
		testPaths = getTestPaths(classPaths, testMethodSignature, methodSignature);
		testPath = getFirstTestPath(testPaths);
		
		assertEquals(Arrays.asList(156,158), testPath);
	}
	
	@Test
	public void inlineDoWhile() throws Throwable 
	{
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		Map<String, Map<SignaturesInfo, List<Integer>>> classPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		Collection<List<Integer>> testPaths;
		List<Integer> testPath;
		int invocationLine = 98;
		
		// Defines which methods will be collected
		String testMethodSignature = "examples.controlFlow.ControlFlowTest.inlineDoWhile()";
		String methodSignature = "examples.controlFlow.TestClass_ControlFlow.inlineDoWhile(int)";

		MethodInvokerInfo testMethodInfo = new MethodInvokerInfo.MethodInvokerInfoBuilder()
				.classPath(testClassPath)
				.methodSignature(testMethodSignature)
				.srcPath(testSrcPath)
				.build();
		
		MethodInvokerInfo methodInfo = new MethodInvokerInfo.MethodInvokerInfoBuilder()
				.classPath(classPath)
				.srcPath(srcPath)
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
		ExecutionFlow ef = new MethodExecutionFlow(methodCollector);
		classPaths = ef.execute().getClassTestPaths();
		
		// Gets test path
		testPaths = getTestPaths(classPaths, testMethodSignature, methodSignature);
		testPath = getFirstTestPath(testPaths);
		
		assertEquals(Arrays.asList(180,183,185), testPath);
	}
	
	@Test
	public void inlineIfElse() throws Throwable 
	{
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		Map<String, Map<SignaturesInfo, List<Integer>>> classPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		Collection<List<Integer>> testPaths;
		List<Integer> testPath;
		int invocationLine = 106;
		
		// Defines which methods will be collected
		String testMethodSignature = "examples.controlFlow.ControlFlowTest.inlineIfElse()";
		String methodSignature = "examples.controlFlow.TestClass_ControlFlow.inlineIfElse(int)";

		MethodInvokerInfo testMethodInfo = new MethodInvokerInfo.MethodInvokerInfoBuilder()
				.classPath(testClassPath)
				.methodSignature(testMethodSignature)
				.srcPath(testSrcPath)
				.build();
		
		MethodInvokerInfo methodInfo = new MethodInvokerInfo.MethodInvokerInfoBuilder()
				.classPath(classPath)
				.srcPath(srcPath)
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
		ExecutionFlow ef = new MethodExecutionFlow(methodCollector);
		classPaths = ef.execute().getClassTestPaths();
		
		// Gets test path
		testPaths = getTestPaths(classPaths, testMethodSignature, methodSignature);
		testPath = getFirstTestPath(testPaths);
		
		assertEquals(Arrays.asList(169), testPath);
	}
	
	/**
	 * Gets all test paths obtained from {@link ExecutionFlow}. 
	 * 
	 * @param testPaths Test paths obtained from ExecutionFlow 
	 * @param testMethodSignature Signature of the test method
	 * @param methodSignature Signature of the method
	 * @return Collection of all test paths
	 */
	private Collection<List<Integer>> getTestPaths(Map<String, Map<SignaturesInfo, List<Integer>>> testPaths, 
			String testMethodSignature, String methodSignature)
	{
		return testPaths.get(testMethodSignature+"$"+methodSignature).values();
	}
	
	/**
	 * Extracts first test path.
	 * 
	 * @param testPaths Collection with test paths
	 * @return First test path
	 */
	private List<Integer> getFirstTestPath(Collection<List<Integer>> testPaths)
	{
		Iterator<List<Integer>> it = testPaths.iterator();
		
		return it.hasNext() ? it.next() : null;
	}
}
