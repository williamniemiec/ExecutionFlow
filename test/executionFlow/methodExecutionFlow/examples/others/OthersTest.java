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
import executionFlow.info.InvokedContainer;
import executionFlow.info.InvokedInfo;
import executionFlow.io.manager.FileManager;
import executionFlow.methodExecutionFlow.MethodExecutionFlowTest;
import executionFlow.runtime.SkipCollection;


/**
 * Tests test path computation for the tested methods of 
 * {@link examples.others.OthersTest} test using 
 * {@link MethodExecutionFlow} class.
 */
@SkipCollection
public class OthersTest extends MethodExecutionFlowTest
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final Path PATH_BIN_TEST_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/others/OthersTest.class");
	private static final Path PATH_SRC_TEST_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/others/OthersTest.java");
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
	 * Tests {@link examples.others.OthersTest.testEmptyTest()} test
	 * method.
	 */
	@Test
	public void testEmptyTest() throws Throwable 
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
		int invocationLine = 24;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.others.OthersTest.testEmptyTest()";
		methodSignature = "examples.others.auxClasses.AuxClass.factorial(int)";
		
		init("examples.others.auxClasses.AuxClass", testMethodSignature);
		
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
		
		ci = new InvokedContainer(methodInfo, testMethodInfo);
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		testPaths = computeTestPath(methodCollector, testMethodSignature, methodSignature);
		
		assertEquals(
			Arrays.asList(
				Arrays.asList()
			),
			testPaths
		);
	}
	
	/**
	 * Tests {@link examples.others.OthersTest.testFactorial()} test
	 * method.
	 */
	@Test
	public void testFactorial() throws Throwable 
	{
		Map<Integer, List<InvokedContainer>> methodCollector = new LinkedHashMap<>();
		List<List<Integer>> testPaths;
		List<InvokedContainer> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		InvokedInfo testMethodInfo, methodInfo;
		InvokedContainer ci;
		int invocationLine = 34;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.others.OthersTest.testFactorial()";
		methodSignature = "examples.others.auxClasses.AuxClass.factorial(int)";
		
		init("examples.others.auxClasses.AuxClass", testMethodSignature);
		
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
		
		ci = new InvokedContainer(methodInfo, testMethodInfo);
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		testPaths = computeTestPath(methodCollector, testMethodSignature, methodSignature);
		
		assertEquals(
			Arrays.asList(
				Arrays.asList(35,36,37,38,39,37,38,39,37,38,39,37,38,39,37,41)
			),
			testPaths
		);
	}
	
	/**
	 * Tests {@link examples.others.OthersTest.testFactorial_zero()} test
	 * method.
	 */
	@Test
	public void testFactorial_zero() throws Throwable 
	{
		Map<Integer, List<InvokedContainer>> methodCollector = new LinkedHashMap<>();
		List<List<Integer>> testPaths;
		List<InvokedContainer> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		InvokedInfo testMethodInfo, methodInfo;
		InvokedContainer ci;
		int invocationLine = 46;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.others.OthersTest.testFactorial_zero()";
		methodSignature = "examples.others.auxClasses.AuxClass.factorial(int)";
		
		init("examples.others.auxClasses.AuxClass", testMethodSignature);
		
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
		
		ci = new InvokedContainer(methodInfo, testMethodInfo);
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		testPaths = computeTestPath(methodCollector, testMethodSignature, methodSignature);
		
		assertEquals(
			Arrays.asList(
				Arrays.asList(35,36,37,41)
			),
			testPaths
		);
	}
	
	/**
	 * Tests {@link examples.others.OthersTest.testFibonacci()} test
	 * method.
	 */
	@Test
	public void testFibonacci() throws Throwable 
	{
		Map<Integer, List<InvokedContainer>> methodCollector = new LinkedHashMap<>();
		List<List<Integer>> testPaths;
		List<InvokedContainer> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		InvokedInfo testMethodInfo, methodInfo;
		InvokedContainer ci;
		int invocationLine = 58;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.others.OthersTest.testFibonacci()";
		methodSignature = "examples.others.auxClasses.AuxClass.fibonacci(int)";
		
		init("examples.others.auxClasses.AuxClass", testMethodSignature);
		
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
				.invokedName("fibonacci")
				.build();
		
		ci = new InvokedContainer(methodInfo, testMethodInfo);
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		testPaths = computeTestPath(methodCollector, testMethodSignature, methodSignature);
		
		assertEquals(
			Arrays.asList(
				Arrays.asList(44,45,46,47,48,49,50,51,52,48,49,50,51,52,48,49,50,51,52,48,54)
			),
			testPaths
		);
	}
	
	/**
	 * Tests {@link examples.others.OthersTest.testInternalCall()} test
	 * method.
	 */
	@Test
	public void testInternalCall() throws Throwable 
	{
		Map<Integer, List<InvokedContainer>> methodCollector = new LinkedHashMap<>();

		List<List<Integer>> testPaths;
		List<InvokedContainer> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		InvokedInfo testMethodInfo, methodInfo;
		InvokedContainer ci;
		int invocationLine = 67;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.others.OthersTest.testInternalCall()";
		methodSignature = "examples.others.auxClasses.AuxClass.parseLetters_withInternalCall(char[])";
		
		init("examples.others.auxClasses.AuxClass", testMethodSignature);
		
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
				.invokedName("parseLetters_withInternalCall")
				.build();
		
		ci = new InvokedContainer(methodInfo, testMethodInfo);
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		testPaths = computeTestPath(methodCollector, testMethodSignature, methodSignature);
		
		assertEquals(
			Arrays.asList(
				Arrays.asList(72,73,74,75,76,74,75,76,74,75,76,74,75,76,74,75,76,74,75,76,74,75,76,74,75,76,74,75,76,74,75,76,74,78)
			),
			testPaths
		);
	}
	
	/**
	 * Tests {@link examples.others.OthersTest.testStaticMethod_charSequence()} test
	 * method.
	 */
	@Test
	public void testStaticMethod_charSequence() throws Throwable 
	{
		Map<Integer, List<InvokedContainer>> methodCollector = new LinkedHashMap<>();
		List<List<Integer>> testPaths;
		List<InvokedContainer> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		InvokedInfo testMethodInfo, methodInfo;
		InvokedContainer ci;
		int invocationLine = 76;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.others.OthersTest.testStaticMethod_charSequence()";
		methodSignature = "examples.others.auxClasses.AuxClass.parseLetters_noInternalCall(CharSequence)";
		
		init("examples.others.auxClasses.AuxClass", testMethodSignature);
		
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
				.invokedName("parseLetters_noInternalCall")
				.build();
		
		ci = new InvokedContainer(methodInfo, testMethodInfo);
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		testPaths = computeTestPath(methodCollector, testMethodSignature, methodSignature);
		
		assertEquals(
			Arrays.asList(
				Arrays.asList(
						57,58,59,60,61,62,67,60,61,62,67,60,61,62,67,60,61,
						62,67,60,61,62,67,60,61,62,67,60,61,62,67,60,61,62,
						67,60,61,62,67,60,61,62,67,60,69
				)
			),
			testPaths
		);
	}
	
	/**
	 * Tests {@link examples.others.OthersTest.testParamSignature_object()} test
	 * method.
	 */
	@Test
	public void testParamSignature_object() throws Throwable 
	{
		Map<Integer, List<InvokedContainer>> methodCollector = new LinkedHashMap<>();
		List<List<Integer>> testPaths;
		List<InvokedContainer> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		InvokedInfo testMethodInfo, methodInfo;
		InvokedContainer ci;
		int invocationLine = 86;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.others.OthersTest.testParamSignature_object()";
		methodSignature = "examples.others.auxClasses.AuxClass.testObjParam(String)";
		
		init("examples.others.auxClasses.AuxClass", testMethodSignature);
		
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
				.invokedName("parseLetters_noInternalCall")
				.build();
		
		ci = new InvokedContainer(methodInfo, testMethodInfo);
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		testPaths = computeTestPath(methodCollector, testMethodSignature, methodSignature);
		
		assertEquals(
			Arrays.asList(
				Arrays.asList(96)
			),
			testPaths
		);
	}
	
	/**
	 * Tests first tested method of
	 * {@link examples.others.OthersTest.testMethodWithAuxMethods()} test method.
	 */
	@Test
	public void testMethodWithAuxMethods_m1() throws Throwable 
	{
		Map<Integer, List<InvokedContainer>> methodCollector = new LinkedHashMap<>();
		List<List<Integer>> testPaths;
		List<InvokedContainer> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		InvokedInfo testMethodInfo, methodInfo;
		InvokedContainer ci;
		int invocationLine = 104;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.others.OthersTest.testMethodWithAuxMethods()";
		methodSignature = "examples.others.auxClasses.AuxClass.fibonacci(int)";
		
		init("examples.others.auxClasses.AuxClass", testMethodSignature);
		
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
				.invokedName("fibonacci")
				.build();
		
		ci = new InvokedContainer(methodInfo, testMethodInfo);
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		testPaths = computeTestPath(methodCollector, testMethodSignature, methodSignature);
		
		assertEquals(
			Arrays.asList(
				Arrays.asList(44,45,46,47,48,49,50,51,52,48,49,50,51,52,48,54)
			),
			testPaths
		);
	}
	
	/**
	 * Tests second tested method of
	 * {@link examples.others.OthersTest.testMethodWithAuxMethods()} test method.
	 */
	@Test
	public void testMethodWithAuxMethods_m2() throws Throwable 
	{
		Map<Integer, List<InvokedContainer>> methodCollector = new LinkedHashMap<>();
		List<List<Integer>> testPaths;
		List<InvokedContainer> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		InvokedInfo testMethodInfo, methodInfo;
		InvokedContainer ci;
		int invocationLine = 105;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.others.OthersTest.testMethodWithAuxMethods()";
		methodSignature = "examples.others.auxClasses.AuxClass.factorial(int)";
		
		init("examples.others.auxClasses.AuxClass", testMethodSignature);
		
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
		
		ci = new InvokedContainer(methodInfo, testMethodInfo);
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		testPaths = computeTestPath(methodCollector, testMethodSignature, methodSignature);
		
		assertEquals(
			Arrays.asList(
				Arrays.asList(35,36,37,38,39,37,38,39,37,38,39,37,41)
			),
			testPaths
		);
	}
	
	/**
	 * Tests first tested method of
	 * {@link examples.others.OthersTest.testMethodWithAuxMethods()} test method.
	 */
	@Test
	public void testingMultipleMethods_m1() throws Throwable 
	{
		Map<Integer, List<InvokedContainer>> methodCollector = new LinkedHashMap<>();
		List<List<Integer>> testPaths;
		List<InvokedContainer> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		InvokedInfo testMethodInfo, methodInfo;
		InvokedContainer ci;
		int invocationLine = 118;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.others.OthersTest.testingMultipleMethods()";
		methodSignature = "examples.others.auxClasses.AuxClass.factorial(int)";
		
		init("examples.others.auxClasses.AuxClass", testMethodSignature);
		
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
		
		ci = new InvokedContainer(methodInfo, testMethodInfo);
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		testPaths = computeTestPath(methodCollector, testMethodSignature, methodSignature);
		
		assertEquals(
			Arrays.asList(
				Arrays.asList(35,36,37,38,39,37,38,39,37,38,39,37,38,39,37,41)
			),
			testPaths
		);
	}
	
	/**
	 * Tests second tested method of
	 * {@link examples.others.OthersTest.testMethodWithAuxMethods()} test method.
	 */
	@Test
	public void testingMultipleMethods_m2() throws Throwable 
	{
		Map<Integer, List<InvokedContainer>> methodCollector = new LinkedHashMap<>();
		List<List<Integer>> testPaths;
		List<InvokedContainer> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		InvokedInfo testMethodInfo, methodInfo;
		InvokedContainer ci;
		int invocationLine = 119;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.others.OthersTest.testingMultipleMethods()";
		methodSignature = "examples.others.auxClasses.AuxClass.fibonacci(int)";
		
		init("examples.others.auxClasses.AuxClass", testMethodSignature);
		
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
				.invokedName("fibonacci")
				.build();
		
		ci = new InvokedContainer(methodInfo, testMethodInfo);
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		testPaths = computeTestPath(methodCollector, testMethodSignature, methodSignature);
		
		assertEquals(
			Arrays.asList(
				Arrays.asList(44,45,46,47,48,49,50,51,52,48,49,50,51,52,48,54)
			),
			testPaths
		);
	}
	
	/**
	 * Tests {@link examples.others.OthersTest.onlyOneMethod()} test method.
	 */
	@Test
	public void onlyOneMethod() throws Throwable 
	{
		Map<Integer, List<InvokedContainer>> methodCollector = new LinkedHashMap<>();
		List<List<Integer>> testPaths;
		List<InvokedContainer> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		InvokedInfo testMethodInfo, methodInfo;
		InvokedContainer ci;
		int invocationLine = 127;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.others.OthersTest.onlyOneMethod()";
		methodSignature = "examples.others.auxClasses.AuxClass.getNumber()";
		
		init("examples.others.auxClasses.AuxClass", testMethodSignature);
		
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
				.invokedName("getNumber")
				.build();
		
		ci = new InvokedContainer(methodInfo, testMethodInfo);
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		testPaths = computeTestPath(methodCollector, testMethodSignature, methodSignature);
		
		assertEquals(
			Arrays.asList(
				Arrays.asList(99)
			),
			testPaths
		);
	}
	
	/**
	 * Tests {@link examples.others.OthersTest.methodCallMultiLineArgs()} test method.
	 */
	@Test
	public void methodCallMultiLineArgs_1() throws Throwable 
	{
		Map<Integer, List<InvokedContainer>> methodCollector = new LinkedHashMap<>();
		List<List<Integer>> testPaths;
		List<InvokedContainer> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		InvokedInfo testMethodInfo, methodInfo;
		InvokedContainer ci;
		int invocationLine = 137;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.others.OthersTest.anonymousObjectReturn()";
		methodSignature = "examples.others.auxClasses.ClassInterface.interfaceMethod()";
		
		init("examples.others.auxClasses.AuxClass", testMethodSignature);
		
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
				.invokedName("interfaceMethod")
				.build();
		
		ci = new InvokedContainer(methodInfo, testMethodInfo);
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		testPaths = computeTestPath(methodCollector, testMethodSignature, methodSignature);
		
		assertEquals(
			Arrays.asList(
				Arrays.asList(108,109)
			),
			testPaths
		);
	}
}