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
import executionFlow.info.InvokedInfo;
import executionFlow.io.FileManager;
import executionFlow.methodExecutionFlow.MethodExecutionFlowTest;
import executionFlow.runtime.SkipCollection;


/**
 * Tests test path computation for the tested methods of 
 * {@link examples.complexTests.ComplexTests} class using 
 * {@link MethodExecutionFlow} class.
 */
@SkipCollection
public class ComplexTests extends MethodExecutionFlowTest
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final Path PATH_BIN_TEST_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/complexTests/ComplexTests.class");
	private static final Path PATH_SRC_TEST_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/complexTests/ComplexTests.java");
	private static final String PACKAGE_TEST_METHOD = "examples.complexTests";
	private static final Path PATH_BIN_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/complexTests/TestClass_ComplexTests.class");
	private static final Path PATH_SRC_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/complexTests/TestClass_ComplexTests.java");
	
	
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
	public void factorial_constructor() throws Throwable 
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
		InvokedInfo testMethodInfo, methodInfo;
		CollectorInfo ci;
		int invocationLine = 19;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.complexTests.ComplexTests.testForConstructorAndMethod()";
		methodSignature = "examples.complexTests.TestClass_ComplexTests.factorial_constructor()";
		
		init("examples.chainedCalls.TestClass_ComplexTests", testMethodSignature);
		
		testMethodInfo = new InvokedInfo.Builder()
				.binPath(PATH_BIN_TEST_METHOD)
				.invokedSignature(testMethodSignature)
				.srcPath(PATH_SRC_TEST_METHOD)
				.build();
		
		methodInfo = new InvokedInfo.Builder()
				.binPath(PATH_BIN_METHOD)
				.srcPath(PATH_SRC_METHOD)
				.invocationLine(invocationLine)
				.invokedSignature(methodSignature)
				.invokedName("factorial_constructor")
				.build();
		
		ci = new CollectorInfo(methodInfo, testMethodInfo);
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		testPaths = computeTestPath(methodCollector, testMethodSignature, methodSignature);
		
		assertEquals(
			Arrays.asList(
				Arrays.asList(41,42,43,44,45,43,47),
				Arrays.asList(41,42,43,44,45,43,44,45,43,47),
				Arrays.asList(41,42,43,44,45,43,44,45,43,44,45,43,47),
				Arrays.asList(41,42,43,44,45,43,44,45,43,44,45,43,44,45,43,47)
			), 
			testPaths
		);
	}
	
	/**
	 * Tests first method used by 
	 * {@link  examples.complexTests.ComplexTests.moreOneConstructor()} test.
	 */
	@Test
	public void moreOneConstructor_first() throws Throwable 
	{
		List<List<Integer>> testPaths;
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		InvokedInfo testMethodInfo, methodInfo;
		CollectorInfo ci;
		int invocationLine = 34;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.complexTests.ComplexTests.moreOneConstructor()";
		methodSignature = "examples.complexTests.TestClass_ComplexTests.factorial(long)";
		
		init("examples.chainedCalls.TestClass_ComplexTests", testMethodSignature);
		
		testMethodInfo = new InvokedInfo.Builder()
				.binPath(PATH_BIN_TEST_METHOD)
				.invokedSignature(testMethodSignature)
				.srcPath(PATH_SRC_TEST_METHOD)
				.build();
		
		methodInfo = new InvokedInfo.Builder()
				.binPath(PATH_BIN_METHOD)
				.srcPath(PATH_SRC_METHOD)
				.invocationLine(invocationLine)
				.invokedSignature(methodSignature)
				.invokedName("factorial")
				.build();
		
		ci = new CollectorInfo(methodInfo, testMethodInfo);
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		testPaths = computeTestPath(methodCollector, testMethodSignature, methodSignature);

		assertEquals(Arrays.asList(20,23,24,25,26,27,25,26,27,25,26,27,25,26,27,25,29), testPaths.get(0));
	}
	
	/**
	 * Tests second method used by 
	 * {@link  examples.complexTests.ComplexTests.moreOneConstructor()} test.
	 */
	@Test
	public void moreOneConstructor_two() throws Throwable 
	{
		List<List<Integer>> testPaths;
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		InvokedInfo testMethodInfo, methodInfo;
		CollectorInfo ci;
		int invocationLine = 35;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.complexTests.ComplexTests.moreOneConstructor()";
		methodSignature = "examples.complexTests.TestClass_ComplexTests.factorial(long)";
		
		init("examples.chainedCalls.TestClass_ComplexTests", testMethodSignature);
		
		testMethodInfo = new InvokedInfo.Builder()
				.binPath(PATH_BIN_TEST_METHOD)
				.invokedSignature(testMethodSignature)
				.srcPath(PATH_SRC_TEST_METHOD)
				.build();
		
		methodInfo = new InvokedInfo.Builder()
				.binPath(PATH_BIN_METHOD)
				.srcPath(PATH_SRC_METHOD)
				.invocationLine(invocationLine)
				.invokedSignature(methodSignature)
				.invokedName("factorial")
				.build();
		
		ci = new CollectorInfo(methodInfo, testMethodInfo);
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		testPaths = computeTestPath(methodCollector, testMethodSignature, methodSignature);
		
		assertEquals(Arrays.asList(20,21), testPaths.get(0));
	}
	
	/**
	 * Tests second method used by 
	 * {@link  examples.complexTests.ComplexTests.moreOneConstructorAndStaticMethod()} test.
	 */
	@Test
	public void moreOneConstructorAndStaticMethod() throws Throwable 
	{
		List<List<Integer>> testPaths;
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		InvokedInfo testMethodInfo, methodInfo;
		CollectorInfo ci;
		int invocationLine = 50;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.complexTests.ComplexTests.moreOneConstructorAndStaticMethod()";
		methodSignature = "examples.complexTests.TestClass_ComplexTests.staticFactorial(int)";
		
		init("examples.chainedCalls.TestClass_ComplexTests", testMethodSignature);
		
		testMethodInfo = new InvokedInfo.Builder()
				.binPath(PATH_BIN_TEST_METHOD)
				.invokedSignature(testMethodSignature)
				.srcPath(PATH_SRC_TEST_METHOD)
				.build();
		
		methodInfo = new InvokedInfo.Builder()
				.binPath(PATH_BIN_METHOD)
				.srcPath(PATH_SRC_METHOD)
				.invocationLine(invocationLine)
				.invokedSignature(methodSignature)
				.invokedName("staticFactorial")
				.build();
		
		ci = new CollectorInfo(methodInfo, testMethodInfo);
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		testPaths = computeTestPath(methodCollector, testMethodSignature, methodSignature);
		
		assertEquals(Arrays.asList(32,33,34,35,36,34,35,36,34,35,36,34,35,36,34,38), testPaths.get(0));
	}
}
