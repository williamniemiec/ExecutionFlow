package executionFlow;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import executionFlow.ExecutionFlow;
import executionFlow.core.JDB;
import executionFlow.info.ClassConstructorInfo;
import executionFlow.info.ClassMethodInfo;
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
		int lastLineTestMethod = 23;
		
		// Defines which methods will be collected
		String testMethodSignature = "controlFlow.ControlFlowTest.ifElseTest_earlyReturn()";
		String methodSignature = "controlFlow.TestClass_ControlFlow.ifElseMethod(int)";
		
		ClassMethodInfo ifElseMethod = new ClassMethodInfo.ClassMethodInfoBuilder()
				.classPath(new File("bin/controlFlow/TestClass_ControlFlow.class").getAbsolutePath())
				.testClassPath(new File("bin/controlFlow/ControlFlowTest.class").getAbsolutePath())
				.srcPath(new File("examples/controlFlow/TestClass_ControlFlow.java").getAbsolutePath())
				.testSrcPath(new File("examples/controlFlow/ControlFlowTest.java").getAbsolutePath())
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.testMethodSignature(testMethodSignature)
				.methodName("ifElseMethod")
				.build();

		methodsInvoked.add(new CollectorInfo(ifElseMethod));
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new ExecutionFlow(methodCollector, lastLineTestMethod);
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
		int lastLineTestMethod = 33;
		
		// Defines which methods will be collected
		String testMethodSignature = "controlFlow.ControlFlowTest.ifElseTest()";
		String methodSignature = "controlFlow.TestClass_ControlFlow.ifElseMethod()";
		
		ClassMethodInfo ifElseMethod = new ClassMethodInfo.ClassMethodInfoBuilder()
				.classPath(new File("bin/controlFlow/TestClass_ControlFlow.class").getAbsolutePath())
				.testClassPath(new File("bin/controlFlow/ControlFlowTest.class").getAbsolutePath())
				.srcPath(new File("examples/controlFlow/TestClass_ControlFlow.java").getAbsolutePath())
				.testSrcPath(new File("examples/controlFlow/ControlFlowTest.java").getAbsolutePath())
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.testMethodSignature(testMethodSignature)
				.methodName("ifElseMethod")
				.build();

		methodsInvoked.add(new CollectorInfo(ifElseMethod));
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new ExecutionFlow(methodCollector, lastLineTestMethod);
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
		int lastLineTestMethod = 43;
		
		// Defines which methods will be collected
		String testMethodSignature = "controlFlow.ControlFlowTest.ifElseTest2()";
		String methodSignature = "controlFlow.TestClass_ControlFlow.ifElseMethod()";
		
		ClassMethodInfo ifElseMethod2 = new ClassMethodInfo.ClassMethodInfoBuilder()
				.classPath(new File("bin/controlFlow/TestClass_ControlFlow.class").getAbsolutePath())
				.testClassPath(new File("bin/controlFlow/ControlFlowTest.class").getAbsolutePath())
				.srcPath(new File("examples/controlFlow/TestClass_ControlFlow.java").getAbsolutePath())
				.testSrcPath(new File("examples/controlFlow/ControlFlowTest.java").getAbsolutePath())
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.testMethodSignature(testMethodSignature)
				.methodName("ifElseMethod")
				.build();

		methodsInvoked.add(new CollectorInfo(ifElseMethod2));
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new ExecutionFlow(methodCollector, lastLineTestMethod);
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
		int lastLineTestMethod = 53;
		
		// Defines which methods will be collected
		String testMethodSignature = "controlFlow.ControlFlowTest.ifElseTest3()";
		String methodSignature = "controlFlow.TestClass_ControlFlow.ifElseMethod()";
		
		ClassMethodInfo ifElseMethod3 = new ClassMethodInfo.ClassMethodInfoBuilder()
				.classPath(new File("bin/controlFlow/TestClass_ControlFlow.class").getAbsolutePath())
				.testClassPath(new File("bin/controlFlow/ControlFlowTest.class").getAbsolutePath())
				.srcPath(new File("examples/controlFlow/TestClass_ControlFlow.java").getAbsolutePath())
				.testSrcPath(new File("examples/controlFlow/ControlFlowTest.java").getAbsolutePath())
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.testMethodSignature(testMethodSignature)
				.methodName("ifElseMethod")
				.build();

		methodsInvoked.add(new CollectorInfo(ifElseMethod3));
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new ExecutionFlow(methodCollector, lastLineTestMethod);
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
		int lastLineTestMethod = 60;
		
		// Defines which methods will be collected
		String testMethodSignature = "controlFlow.ControlFlowTest.tryCatchTest1()";
		String methodSignature = "controlFlow.TestClass_ControlFlow.tryCatchMethod_try()";
		
		ClassMethodInfo tryCatchMethod_try = new ClassMethodInfo.ClassMethodInfoBuilder()
				.classPath(new File("bin/controlFlow/TestClass_ControlFlow.class").getAbsolutePath())
				.testClassPath(new File("bin/controlFlow/ControlFlowTest.class").getAbsolutePath())
				.srcPath(new File("examples/controlFlow/TestClass_ControlFlow.java").getAbsolutePath())
				.testSrcPath(new File("examples/controlFlow/ControlFlowTest.java").getAbsolutePath())
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.testMethodSignature(testMethodSignature)
				.methodName("tryCatchMethod_try")
				.build();

		methodsInvoked.add(new CollectorInfo(tryCatchMethod_try));
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new ExecutionFlow(methodCollector, lastLineTestMethod);
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
		int lastLineTestMethod = 67;
		
		// Defines which methods will be collected
		String testMethodSignature = "controlFlow.ControlFlowTest.tryCatchTest2()";
		String methodSignature = "controlFlow.TestClass_ControlFlow.tryCatchMethod_catch()";
		
		ClassMethodInfo tryCatchMethod_catch = new ClassMethodInfo.ClassMethodInfoBuilder()
				.classPath(new File("bin/controlFlow/TestClass_ControlFlow.class").getAbsolutePath())
				.testClassPath(new File("bin/controlFlow/ControlFlowTest.class").getAbsolutePath())
				.srcPath(new File("examples/controlFlow/TestClass_ControlFlow.java").getAbsolutePath())
				.testSrcPath(new File("examples/controlFlow/ControlFlowTest.java").getAbsolutePath())
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.testMethodSignature(testMethodSignature)
				.methodName("tryCatchMethod_catch")
				.build();

		methodsInvoked.add(new CollectorInfo(tryCatchMethod_catch));
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new ExecutionFlow(methodCollector, lastLineTestMethod);
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
		int lastLineTestMethod = 75;
		
		// Defines which methods will be collected
		String testMethodSignature = "controlFlow.ControlFlowTest.switchCaseTest()";
		String methodSignature = "controlFlow.TestClass_ControlFlow.switchCaseMethod(char)";
		
		ClassMethodInfo switchCaseMethod = new ClassMethodInfo.ClassMethodInfoBuilder()
				.classPath(new File("bin/controlFlow/TestClass_ControlFlow.class").getAbsolutePath())
				.testClassPath(new File("bin/controlFlow/ControlFlowTest.class").getAbsolutePath())
				.srcPath(new File("examples/controlFlow/TestClass_ControlFlow.java").getAbsolutePath())
				.testSrcPath(new File("examples/controlFlow/ControlFlowTest.java").getAbsolutePath())
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.testMethodSignature(testMethodSignature)
				.methodName("switchCaseMethod")
				.build();

		methodsInvoked.add(new CollectorInfo(switchCaseMethod));
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new ExecutionFlow(methodCollector, lastLineTestMethod);
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
		int lastLineTestMethod = 83;
		
		// Defines which methods will be collected
		String testMethodSignature = "controlFlow.ControlFlowTest.doWhileTest()";
		String methodSignature = "controlFlow.TestClass_ControlFlow.doWhileMethod(int,int)";
		
		ClassMethodInfo doWhileMethod = new ClassMethodInfo.ClassMethodInfoBuilder()
				.classPath(new File("bin/controlFlow/TestClass_ControlFlow.class").getAbsolutePath())
				.testClassPath(new File("bin/controlFlow/ControlFlowTest.class").getAbsolutePath())
				.srcPath(new File("examples/controlFlow/TestClass_ControlFlow.java").getAbsolutePath())
				.testSrcPath(new File("examples/controlFlow/ControlFlowTest.java").getAbsolutePath())
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.testMethodSignature(testMethodSignature)
				.methodName("doWhileMethod")
				.build();

		methodsInvoked.add(new CollectorInfo(doWhileMethod));
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new ExecutionFlow(methodCollector, lastLineTestMethod);
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
		int lastLineTestMethod = 91;
		
		// Defines which methods will be collected
		String testMethodSignature = "controlFlow.ControlFlowTest.inlineWhile()";
		String methodSignature = "controlFlow.TestClass_ControlFlow.inlineWhile(int)";
		
		ClassMethodInfo doWhileMethod = new ClassMethodInfo.ClassMethodInfoBuilder()
				.classPath(new File("bin/controlFlow/TestClass_ControlFlow.class").getAbsolutePath())
				.testClassPath(new File("bin/controlFlow/ControlFlowTest.class").getAbsolutePath())
				.srcPath(new File("examples/controlFlow/TestClass_ControlFlow.java").getAbsolutePath())
				.testSrcPath(new File("examples/controlFlow/ControlFlowTest.java").getAbsolutePath())
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.testMethodSignature(testMethodSignature)
				.methodName("inlineWhile")
				.build();

		methodsInvoked.add(new CollectorInfo(doWhileMethod));
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new ExecutionFlow(methodCollector, lastLineTestMethod);
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
		int lastLineTestMethod = 99;
		
		// Defines which methods will be collected
		String testMethodSignature = "controlFlow.ControlFlowTest.inlineDoWhile()";
		String methodSignature = "controlFlow.TestClass_ControlFlow.inlineDoWhile(int)";
		
		ClassMethodInfo doWhileMethod = new ClassMethodInfo.ClassMethodInfoBuilder()
				.classPath(new File("bin/controlFlow/TestClass_ControlFlow.class").getAbsolutePath())
				.testClassPath(new File("bin/controlFlow/ControlFlowTest.class").getAbsolutePath())
				.srcPath(new File("examples/controlFlow/TestClass_ControlFlow.java").getAbsolutePath())
				.testSrcPath(new File("examples/controlFlow/ControlFlowTest.java").getAbsolutePath())
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.testMethodSignature(testMethodSignature)
				.methodName("inlineDoWhile")
				.build();

		methodsInvoked.add(new CollectorInfo(doWhileMethod));
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new ExecutionFlow(methodCollector, lastLineTestMethod);
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
		int lastLineTestMethod = 107;
		
		// Defines which methods will be collected
		String testMethodSignature = "controlFlow.ControlFlowTest.inlineIfElse()";
		String methodSignature = "controlFlow.TestClass_ControlFlow.inlineIfElse(int)";
		
		ClassMethodInfo doWhileMethod = new ClassMethodInfo.ClassMethodInfoBuilder()
				.classPath(new File("bin/controlFlow/TestClass_ControlFlow.class").getAbsolutePath())
				.testClassPath(new File("bin/controlFlow/ControlFlowTest.class").getAbsolutePath())
				.srcPath(new File("examples/controlFlow/TestClass_ControlFlow.java").getAbsolutePath())
				.testSrcPath(new File("examples/controlFlow/ControlFlowTest.java").getAbsolutePath())
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.testMethodSignature(testMethodSignature)
				.methodName("inlineIfElse")
				.build();

		methodsInvoked.add(new CollectorInfo(doWhileMethod));
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new ExecutionFlow(methodCollector, lastLineTestMethod);
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
