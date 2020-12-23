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
import executionFlow.info.InvokedContainer;
import executionFlow.info.InvokedInfo;
import executionFlow.io.manager.FileManager;
import executionFlow.methodExecutionFlow.MethodExecutionFlowTest;
import executionFlow.runtime.SkipCollection;


/**
 * Tests test path computation for the tested methods of 
 * {@link examples.chainedCalls.ChainedCalls} test using 
 * {@link MethodExecutionFlow} class.
 */
@SkipCollection
public class ChainedCalls extends MethodExecutionFlowTest
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final Path PATH_BIN_TEST_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/chainedCalls/ChainedCalls.class");
	private static final Path PATH_SRC_TEST_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/chainedCalls/ChainedCalls.java");
	private static final String PACKAGE_TEST_METHOD = "examples.chainedCalls";
	private static final Path PATH_BIN_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/chainedCalls/Calculator.class");
	private static final Path PATH_SRC_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/chainedCalls/Calculator.java");
	
	
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
	 * Tests {@link examples.chainedCalls.Calculator#setNumber(float)}
	 * method.
	 */
	@Test
	public void setNumber() throws Throwable 
	{
		/**
		 * Stores information about collected methods.
		 * <ul>
		 * 	<li><b>Key:</b> Method invocation line</li>
		 * 	<li><b>Value:</b> List of methods invoked from this line</li>
		 * </ul>
		 */
		Map<Integer, List<InvokedContainer>> methodCollector = new LinkedHashMap<>();

		List<List<Integer>> testPaths;
		List<InvokedContainer> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		InvokedInfo testMethodInfo, methodInfo;
		InvokedContainer ci;
		int invocationLine = 15;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.chainedCalls.ChainedCalls.testChainedMethods()";
		methodSignature = "examples.chainedCalls.Calculator.setNumber(float)";
		
		init("examples.chainedCalls.Calculator", testMethodSignature);
		
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
				.invokedName("setNumber")
				.build();
		
		ci = new InvokedContainer(methodInfo, testMethodInfo);
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		testPaths = computeTestPath(methodCollector, testMethodSignature, methodSignature);
		
		assertEquals(
			Arrays.asList(
				Arrays.asList(11,12)
			),
			testPaths
		);
	}
	
	/**
	 * Tests {@link examples.chainedCalls.Calculator#sum(float)}
	 * method.
	 */
	@Test
	public void sum() throws Throwable 
	{
		Map<Integer, List<InvokedContainer>> methodCollector = new LinkedHashMap<>();
		List<List<Integer>> testPaths;
		List<InvokedContainer> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		InvokedInfo testMethodInfo, methodInfo;
		InvokedContainer ci;
		int invocationLine = 15;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.chainedCalls.ChainedCalls.testChainedMethods()";
		methodSignature = "examples.chainedCalls.Calculator.sum(float)";
		
		init("examples.chainedCalls.Calculator", testMethodSignature);
		
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
				.invokedName("sum")
				.build();
		
		ci = new InvokedContainer(methodInfo, testMethodInfo);
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		testPaths = computeTestPath(methodCollector, testMethodSignature, methodSignature);
		
		assertEquals(
			Arrays.asList(
				Arrays.asList(15,16)
			),
			testPaths
		);
	}
	
	/**
	 * Tests {@link examples.chainedCalls.Calculator#sub(float)}
	 * method.
	 */
	@Test
	public void sub() throws Throwable 
	{
		Map<Integer, List<InvokedContainer>> methodCollector = new LinkedHashMap<>();
		List<List<Integer>> testPaths;
		List<InvokedContainer> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		InvokedInfo testMethodInfo, methodInfo;
		InvokedContainer ci;
		int invocationLine = 15;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.chainedCalls.ChainedCalls.testChainedMethods()";
		methodSignature = "examples.chainedCalls.Calculator.sub(float)";
		
		init("examples.chainedCalls.Calculator", testMethodSignature);
		
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
				.invokedName("sub")
				.build();
		
		ci = new InvokedContainer(methodInfo, testMethodInfo);
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		testPaths = computeTestPath(methodCollector, testMethodSignature, methodSignature);
		
		assertEquals(
			Arrays.asList(
				Arrays.asList(19,20)
			),
			testPaths
		);
	}
	
	/**
	 * Tests {@link examples.chainedCalls.Calculator#mult(float)}
	 * method.
	 */
	@Test
	public void mult() throws Throwable 
	{
		Map<Integer, List<InvokedContainer>> methodCollector = new LinkedHashMap<>();
		List<List<Integer>> testPaths;
		List<InvokedContainer> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		InvokedInfo testMethodInfo, methodInfo;
		InvokedContainer ci;
		int invocationLine = 15;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.chainedCalls.ChainedCalls.testChainedMethods()";
		methodSignature = "examples.chainedCalls.Calculator.mult(float)";
		
		init("examples.chainedCalls.Calculator", testMethodSignature);
		
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
				.invokedName("mult")
				.build();
		
		ci = new InvokedContainer(methodInfo, testMethodInfo);
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		testPaths = computeTestPath(methodCollector, testMethodSignature, methodSignature);
		
		assertEquals(
			Arrays.asList(
				Arrays.asList(23,24)
			),
			testPaths
		);
	}
	
	/**
	 * Tests {@link examples.chainedCalls.Calculator#div(float)}
	 * method.
	 */
	@Test
	public void div() throws Throwable 
	{
		Map<Integer, List<InvokedContainer>> methodCollector = new LinkedHashMap<>();
		List<List<Integer>> testPaths;
		List<InvokedContainer> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		InvokedInfo testMethodInfo, methodInfo;
		InvokedContainer ci;
		int invocationLine = 15;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.chainedCalls.ChainedCalls.testChainedMethods()";
		methodSignature = "examples.chainedCalls.Calculator.div(float)";
		
		init("examples.chainedCalls.Calculator", testMethodSignature);
		
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
				.invokedName("div")
				.build();
		
		ci = new InvokedContainer(methodInfo, testMethodInfo);
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		testPaths = computeTestPath(methodCollector, testMethodSignature, methodSignature);
		
		assertEquals(
			Arrays.asList(
				Arrays.asList(27,28)
			),
			testPaths
		);
	}
	
	/**
	 * Tests {@link examples.chainedCalls.Calculator#ans()}
	 * method.
	 */
	@Test
	public void ans() throws Throwable 
	{
		Map<Integer, List<InvokedContainer>> methodCollector = new LinkedHashMap<>();
		List<List<Integer>> testPaths;
		List<InvokedContainer> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		InvokedInfo testMethodInfo, methodInfo;
		InvokedContainer ci;
		int invocationLine = 15;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.chainedCalls.ChainedCalls.testChainedMethods()";
		methodSignature = "examples.chainedCalls.Calculator.ans()";
		
		init("examples.chainedCalls.Calculator", testMethodSignature);
		
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
				.invokedName("ans")
				.build();
		
		ci = new InvokedContainer(methodInfo, testMethodInfo);
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		testPaths = computeTestPath(methodCollector, testMethodSignature, methodSignature);
		
		assertEquals(
			Arrays.asList(
				Arrays.asList(35)
			),
			testPaths
		);
	}
}