package executionFlow;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import executionFlow.core.file.FileManager;
import executionFlow.core.file.MethodManager;
import executionFlow.core.file.ParserType;
import executionFlow.core.file.parser.factory.AssertFileParserFactory;
import executionFlow.info.MethodInvokerInfo;
import executionFlow.info.CollectorInfo;
import executionFlow.info.SignaturesInfo;
import executionFlow.runtime.SkipCollection;


/**
 * Tests test path computation of class {@link ControlFlowTest} from debug 
 * analysis.
 */
@SkipCollection
public class EF_ControlFlowTest 
{
	private static FileManager testMethodFileManager;
	private static MethodManager testMethodManager;
	private static final String classSignature = "controlFlow.TestClass_ControlFlow";
	private static final Path classPath = Path.of("bin/controlFlow/TestClass_ControlFlow.class");
	private static final Path testClassPath = Path.of("bin/controlFlow/ControlFlowTest.class");
	private static final Path srcPath = Path.of("examples/controlFlow/TestClass_ControlFlow.java");
	private static final Path testSrcPath = Path.of("examples/controlFlow/ControlFlowTest.java");
	private static final String testClassPackage = "controlFlow";
	
	
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
	
	//@Test
	//public void ifElseTest_earlyReturn() throws Throwable 
	//{
		/**
		 * Stores information about collected methods.<hr/>
		 * <ul>
		 * 		<li><b>Key:</b> Method invocation line</li>
		 * 		<li><b>Value:</b> List of methods invoked from this line</li>
		 * </ul>
		 */
		//Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		
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
		/*
		Map<String, Map<SignaturesInfo, List<Integer>>> classPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		Collection<List<Integer>> testPaths;
		List<Integer> testPath;
		int invocationLine = 19;
		
		// Defines which methods will be collected
		String testMethodSignature = "controlFlow.ControlFlowTest.ifElseTest_earlyReturn()";
		String methodSignature = "controlFlow.TestClass_ControlFlow.ifElseMethod(int)";
		String classSignature = "controlFlow.TestClass_ControlFlow";
		
		ClassMethodInfo ifElseMethod = new ClassMethodInfo.ClassMethodInfoBuilder()
				.classPath(Path.of("bin/controlFlow/TestClass_ControlFlow.class"))
				.testClassPath(Path.of("bin/controlFlow/ControlFlowTest.class"))
				.srcPath(Path.of("examples/controlFlow/TestClass_ControlFlow.java"))
				.testSrcPath(Path.of("examples/controlFlow/ControlFlowTest.java"))
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.testMethodSignature(testMethodSignature)
				.methodName("ifElseMethod")
				.classSignature(classSignature)
				.build();

		methodsInvoked.add(new CollectorInfo(ifElseMethod));
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new ExecutionFlow(methodCollector);
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
		String testMethodSignature = "controlFlow.ControlFlowTest.ifElseTest()";
		String methodSignature = "controlFlow.TestClass_ControlFlow.ifElseMethod()";
		String classSignature = "controlFlow.TestClass_ControlFlow";
		
		ClassMethodInfo ifElseMethod = new ClassMethodInfo.ClassMethodInfoBuilder()
				.classPath(Path.of("bin/controlFlow/TestClass_ControlFlow.class"))
				.testClassPath(Path.of("bin/controlFlow/ControlFlowTest.class"))
				.srcPath(Path.of("examples/controlFlow/TestClass_ControlFlow.java"))
				.testSrcPath(Path.of("examples/controlFlow/ControlFlowTest.java"))
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.testMethodSignature(testMethodSignature)
				.methodName("ifElseMethod")
				.classSignature(classSignature)
				.build();

		methodsInvoked.add(new CollectorInfo(ifElseMethod));
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new ExecutionFlow(methodCollector);
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
		String testMethodSignature = "controlFlow.ControlFlowTest.ifElseTest2()";
		String methodSignature = "controlFlow.TestClass_ControlFlow.ifElseMethod()";
		String classSignature = "controlFlow.TestClass_ControlFlow";
		
		ClassMethodInfo ifElseMethod2 = new ClassMethodInfo.ClassMethodInfoBuilder()
				.classPath(Path.of("bin/controlFlow/TestClass_ControlFlow.class"))
				.testClassPath(Path.of("bin/controlFlow/ControlFlowTest.class"))
				.srcPath(Path.of("examples/controlFlow/TestClass_ControlFlow.java"))
				.testSrcPath(Path.of("examples/controlFlow/ControlFlowTest.java"))
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.testMethodSignature(testMethodSignature)
				.methodName("ifElseMethod")
				.classSignature(classSignature)
				.build();

		methodsInvoked.add(new CollectorInfo(ifElseMethod2));
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new ExecutionFlow(methodCollector);
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
		String testMethodSignature = "controlFlow.ControlFlowTest.ifElseTest3()";
		String methodSignature = "controlFlow.TestClass_ControlFlow.ifElseMethod()";
		String classSignature = "controlFlow.TestClass_ControlFlow";
		
		ClassMethodInfo ifElseMethod3 = new ClassMethodInfo.ClassMethodInfoBuilder()
				.classPath(Path.of("bin/controlFlow/TestClass_ControlFlow.class"))
				.testClassPath(Path.of("bin/controlFlow/ControlFlowTest.class"))
				.srcPath(Path.of("examples/controlFlow/TestClass_ControlFlow.java"))
				.testSrcPath(Path.of("examples/controlFlow/ControlFlowTest.java"))
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.testMethodSignature(testMethodSignature)
				.methodName("ifElseMethod")
				.classSignature(classSignature)
				.build();

		methodsInvoked.add(new CollectorInfo(ifElseMethod3));
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new ExecutionFlow(methodCollector);
		classPaths = ef.execute().getClassTestPaths();
		
		// Gets test path
		testPaths = getTestPaths(classPaths, testMethodSignature, methodSignature);
		testPath = getFirstTestPath(testPaths);
		
		assertEquals(Arrays.asList(25,29,31,33,35,36,39), testPath);
	}
	*/
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
		String testMethodSignature = "controlFlow.ControlFlowTest.tryCatchTest1()";
		String methodSignature = "controlFlow.TestClass_ControlFlow.tryCatchMethod_try()";
		
		MethodInvokerInfo tryCatchMethod_try = new ConstructorInvokerInfoBuilder.ClassMethodInfoBuilder()
				.classPath(classPath)
				.testClassPath(testClassPath)
				.srcPath(srcPath)
				.testSrcPath(testSrcPath)
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.testMethodSignature(testMethodSignature)
				.methodName("tryCatchMethod_try")
				.classSignature(classSignature)
				.build();

		methodsInvoked.add(new CollectorInfo(tryCatchMethod_try));
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new ExecutionFlow(methodCollector);
		classPaths = ef.execute().getClassTestPaths();
		
		// Gets test path
		testPaths = getTestPaths(classPaths, testMethodSignature, methodSignature);
		testPath = getFirstTestPath(testPaths);
		
		assertEquals(Arrays.asList(50,52,53,54,55,56,57,62), testPath);
	}
	/*
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
		String testMethodSignature = "controlFlow.ControlFlowTest.tryCatchTest2()";
		String methodSignature = "controlFlow.TestClass_ControlFlow.tryCatchMethod_catch()";
		String classSignature = "controlFlow.TestClass_ControlFlow";
		
		ClassMethodInfo tryCatchMethod_catch = new ClassMethodInfo.ClassMethodInfoBuilder()
				.classPath(Path.of("bin/controlFlow/TestClass_ControlFlow.class"))
				.testClassPath(Path.of("bin/controlFlow/ControlFlowTest.class"))
				.srcPath(Path.of("examples/controlFlow/TestClass_ControlFlow.java"))
				.testSrcPath(Path.of("examples/controlFlow/ControlFlowTest.java"))
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.testMethodSignature(testMethodSignature)
				.methodName("tryCatchMethod_catch")
				.classSignature(classSignature)
				.build();

		methodsInvoked.add(new CollectorInfo(tryCatchMethod_catch));
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new ExecutionFlow(methodCollector);
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
		String testMethodSignature = "controlFlow.ControlFlowTest.switchCaseTest()";
		String methodSignature = "controlFlow.TestClass_ControlFlow.switchCaseMethod(char)";
		String classSignature = "controlFlow.TestClass_ControlFlow";
		
		ClassMethodInfo switchCaseMethod = new ClassMethodInfo.ClassMethodInfoBuilder()
				.classPath(Path.of("bin/controlFlow/TestClass_ControlFlow.class"))
				.testClassPath(Path.of("bin/controlFlow/ControlFlowTest.class"))
				.srcPath(Path.of("examples/controlFlow/TestClass_ControlFlow.java"))
				.testSrcPath(Path.of("examples/controlFlow/ControlFlowTest.java"))
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.testMethodSignature(testMethodSignature)
				.methodName("switchCaseMethod")
				.classSignature(classSignature)
				.build();

		methodsInvoked.add(new CollectorInfo(switchCaseMethod));
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new ExecutionFlow(methodCollector);
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
		String testMethodSignature = "controlFlow.ControlFlowTest.doWhileTest()";
		String methodSignature = "controlFlow.TestClass_ControlFlow.doWhileMethod(int,int)";
		String classSignature = "controlFlow.TestClass_ControlFlow";
		
		ClassMethodInfo doWhileMethod = new ClassMethodInfo.ClassMethodInfoBuilder()
				.classPath(Path.of("bin/controlFlow/TestClass_ControlFlow.class"))
				.testClassPath(Path.of("bin/controlFlow/ControlFlowTest.class"))
				.srcPath(Path.of("examples/controlFlow/TestClass_ControlFlow.java"))
				.testSrcPath(Path.of("examples/controlFlow/ControlFlowTest.java"))
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.testMethodSignature(testMethodSignature)
				.methodName("doWhileMethod")
				.classSignature(classSignature)
				.build();

		methodsInvoked.add(new CollectorInfo(doWhileMethod));
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new ExecutionFlow(methodCollector);
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
		String testMethodSignature = "controlFlow.ControlFlowTest.inlineWhile()";
		String methodSignature = "controlFlow.TestClass_ControlFlow.inlineWhile(int)";
		String classSignature = "controlFlow.TestClass_ControlFlow";
		
		ClassMethodInfo doWhileMethod = new ClassMethodInfo.ClassMethodInfoBuilder()
				.classPath(Path.of("bin/controlFlow/TestClass_ControlFlow.class"))
				.testClassPath(Path.of("bin/controlFlow/ControlFlowTest.class"))
				.srcPath(Path.of("examples/controlFlow/TestClass_ControlFlow.java"))
				.testSrcPath(Path.of("examples/controlFlow/ControlFlowTest.java"))
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.testMethodSignature(testMethodSignature)
				.methodName("inlineWhile")
				.classSignature(classSignature)
				.build();

		methodsInvoked.add(new CollectorInfo(doWhileMethod));
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new ExecutionFlow(methodCollector);
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
		String testMethodSignature = "controlFlow.ControlFlowTest.inlineDoWhile()";
		String methodSignature = "controlFlow.TestClass_ControlFlow.inlineDoWhile(int)";
		String classSignature = "controlFlow.TestClass_ControlFlow";
		
		ClassMethodInfo doWhileMethod = new ClassMethodInfo.ClassMethodInfoBuilder()
				.classPath(Path.of("bin/controlFlow/TestClass_ControlFlow.class"))
				.testClassPath(Path.of("bin/controlFlow/ControlFlowTest.class"))
				.srcPath(Path.of("examples/controlFlow/TestClass_ControlFlow.java"))
				.testSrcPath(Path.of("examples/controlFlow/ControlFlowTest.java"))
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.testMethodSignature(testMethodSignature)
				.methodName("inlineDoWhile")
				.classSignature(classSignature)
				.build();

		methodsInvoked.add(new CollectorInfo(doWhileMethod));
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new ExecutionFlow(methodCollector);
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
		String testMethodSignature = "controlFlow.ControlFlowTest.inlineIfElse()";
		String methodSignature = "controlFlow.TestClass_ControlFlow.inlineIfElse(int)";
		String classSignature = "controlFlow.TestClass_ControlFlow";
		
		ClassMethodInfo doWhileMethod = new ClassMethodInfo.ClassMethodInfoBuilder()
				.classPath(Path.of("bin/controlFlow/TestClass_ControlFlow.class"))
				.testClassPath(Path.of("bin/controlFlow/ControlFlowTest.class"))
				.srcPath(Path.of("examples/controlFlow/TestClass_ControlFlow.java"))
				.testSrcPath(Path.of("examples/controlFlow/ControlFlowTest.java"))
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.testMethodSignature(testMethodSignature)
				.methodName("inlineIfElse")
				.classSignature(classSignature)
				.build();

		methodsInvoked.add(new CollectorInfo(doWhileMethod));
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new ExecutionFlow(methodCollector);
		classPaths = ef.execute().getClassTestPaths();
		
		// Gets test path
		testPaths = getTestPaths(classPaths, testMethodSignature, methodSignature);
		testPath = getFirstTestPath(testPaths);
		
		assertEquals(Arrays.asList(169), testPath);
	}
	*/
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
