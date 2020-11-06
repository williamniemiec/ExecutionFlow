package executionFlow.methodExecutionFlow.examples.others;

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
 * {@link examples.others.AssertFailTest} test using 
 * {@link MethodExecutionFlow} class.
 */
@SkipCollection
public class AssertFailTest extends MethodExecutionFlowTest
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final Path PATH_BIN_TEST_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/others/AssertFailTest.class");
	private static final Path PATH_SRC_TEST_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/others/AssertFailTest.java");
	private static final String PACKAGE_TEST_METHOD = "examples.others";
	private static final Path PATH_BIN_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/others/auxClasses/AuxClass.class");
	private static final Path PATH_SRC_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/others/auxClasses/AuxClass.java");
	
	
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
	/**
	 * Tests first tested method of 
	 * {@link examples.others.AssertFailTest#assertFailAtTheEndTest()} test method.
	 */
	@Test
	public void assertFailAtTheEndTest_m1() throws Throwable 
	{
		/**
		 * Stores information about collected methods.
		 * <ul>
		 * 	<li><b>Key:</b> Method invocation line</li>
		 * 	<li><b>Value:</b> List of methods invoked from this line</li>
		 * </ul>
		 */
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();

		String testMethodSignature, methodSignature;
		List<List<Integer>> testPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		MethodInvokedInfo testMethodInfo, methodInfo;
		CollectorInfo ci;
		int invocationLine = 21;
		
				
		// Defines which methods will be collected
		testMethodSignature = "examples.others.AssertFailTest.assertFailAtTheEndTest()";
		methodSignature = "examples.others.auxClasses.AuxClass.threePaths(int)";
		
		init("examples.others.auxClasses.AuxClass", testMethodSignature);
		
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
				.methodName("threePaths")
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
		
		assertEquals(
			Arrays.asList(
				Arrays.asList(87,88)
			),
			testPaths
		);
	}
	
	/**
	 * Tests second tested method of 
	 * {@link examples.others.AssertFailTest#assertFailAtTheEndTest()} test method.
	 */
	@Test
	public void assertFailAtTheEndTest_m2() throws Throwable 
	{
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		List<List<Integer>> testPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		MethodInvokedInfo testMethodInfo, methodInfo;
		CollectorInfo ci;
		int invocationLine = 22;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.others.AssertFailTest.assertFailAtTheEndTest()";
		methodSignature = "examples.others.auxClasses.AuxClass.threePaths(int)";
		
		init("examples.others.auxClasses.AuxClass", testMethodSignature);
		
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
				.methodName("threePaths")
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
		
		assertEquals(
			Arrays.asList(
				Arrays.asList(87,90,91)
			),
			testPaths
		);
	}
	
	/**
	 * Tests third tested method of 
	 * {@link examples.others.AssertFailTest#assertFailAtTheEndTest()} test method.
	 */
	@Test
	public void assertFailAtTheEndTest_m3() throws Throwable 
	{
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		List<List<Integer>> testPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		MethodInvokedInfo testMethodInfo, methodInfo;
		CollectorInfo ci;
		int invocationLine = 23;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.others.AssertFailTest.assertFailAtTheEndTest()";
		methodSignature = "examples.others.auxClasses.AuxClass.threePaths(int)";
		
		init("examples.others.auxClasses.AuxClass", testMethodSignature);
		
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
				.methodName("threePaths")
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
		
		assertEquals(
			Arrays.asList(
				Arrays.asList(87,90,93)
			),
			testPaths
		);
	}
	
	/**
	 * Tests first tested method of 
	 * {@link examples.others.AssertFailTest#assertFailInTheMiddleTest()} test method.
	 */
	@Test
	public void assertFailInTheMiddleTest_m1() throws Throwable 
	{
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		List<List<Integer>> testPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		MethodInvokedInfo testMethodInfo, methodInfo;
		CollectorInfo ci;
		int invocationLine = 32;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.others.AssertFailTest.assertFailInTheMiddleTest()";
		methodSignature = "examples.others.auxClasses.AuxClass.threePaths(int)";
		
		init("examples.others.auxClasses.AuxClass", testMethodSignature);
		
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
				.methodName("threePaths")
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
		
		assertEquals(
			Arrays.asList(
				Arrays.asList(87,88)
			),
			testPaths
		);
	}
	
	/**
	 * Tests first tested method of 
	 * {@link examples.others.AssertFailTest#assertFailInTheMiddleTest()} test method.
	 */
	@Test
	public void assertFailInTheMiddleTest_m2() throws Throwable 
	{
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		List<List<Integer>> testPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		MethodInvokedInfo testMethodInfo, methodInfo;
		CollectorInfo ci;
		int invocationLine = 33;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.others.AssertFailTest.assertFailInTheMiddleTest()";
		methodSignature = "examples.others.auxClasses.AuxClass.threePaths(int)";
		
		init("examples.others.auxClasses.AuxClass", testMethodSignature);
		
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
				.methodName("threePaths")
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
		
		assertEquals(
			Arrays.asList(
				Arrays.asList(87,90,93)
			),
			testPaths
		);
	}
	
	/**
	 * Tests first tested method of 
	 * {@link examples.others.AssertFailTest#assertFailInTheMiddleTest()} test method.
	 */
	@Test
	public void assertFailInTheMiddleTest_m3() throws Throwable 
	{
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		List<List<Integer>> testPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		MethodInvokedInfo testMethodInfo, methodInfo;
		CollectorInfo ci;
		int invocationLine = 34;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.others.AssertFailTest.assertFailInTheMiddleTest()";
		methodSignature = "examples.others.auxClasses.AuxClass.threePaths(int)";
		
		init("examples.others.auxClasses.AuxClass", testMethodSignature);
		
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
				.methodName("threePaths")
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
		
		assertEquals(
			Arrays.asList(
				Arrays.asList(87,90,91)
			),
			testPaths
		);
	}
}