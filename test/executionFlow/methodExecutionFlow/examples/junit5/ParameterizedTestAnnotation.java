package executionFlow.methodExecutionFlow.examples.junit5;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.time.temporal.ChronoUnit;
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
 * {@link examples.junit5.ParameterizedTestAnnotation} test using 
 * {@link MethodExecutionFlow}.
 */
@SkipCollection
public class ParameterizedTestAnnotation extends MethodExecutionFlowTest
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final String PACKAGE_TEST_METHOD = "examples.junit5";
	
	
	//-------------------------------------------------------------------------
	//		Test preparers
	//-------------------------------------------------------------------------
	/**
	 * Initializes a test method.
	 * 
	 * @param		classSignature Test class signature
	 * @param		testMethodSignature Test method signature
	 * @param		pathBinTestMethod Test method compiled file path
	 * @param		pathSrcTestMethod Test method source file path
	 * @param		testMethodArgs Test method arguments (when it is a parameterized test)
	 * 
	 * @throws		IOException If an error occurs during file parsing
	 * @throws		ClassNotFoundException If class {@link FileManager} was not
	 * found
	 */
	private void init(String classSignature, String testMethodSignature, 
			Path pathBinTestMethod, Path pathSrcTestMethod, Object... testMethodArgs) 
			throws IOException, ClassNotFoundException
	{
		init(classSignature, testMethodSignature, pathSrcTestMethod, 
				pathBinTestMethod, PACKAGE_TEST_METHOD, testMethodArgs);
		
	}
	
	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	/**
	 * Tests {@link examples.junit5.ParameterizedTestAnnotation#test1(int)} test
	 * method with its first argument.
	 */
	@Test
	public void test1_int_arg1() throws Throwable 
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
		int invocationLine = 38;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.junit5.ParameterizedTestAnnotation.test1(int)";
		methodSignature = "examples.others.auxClasses.AuxClass.factorial(int)";
		
		init(
				"examples.junit5.ParameterizedTestAnnotation", 
				testMethodSignature,
				Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/junit5/ParameterizedTestAnnotation.class"),
				Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/junit5/ParameterizedTestAnnotation.java"),
				-1
		 );
		
		testMethodInfo = new MethodInvokedInfo.Builder()
				.binPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/junit5/ParameterizedTestAnnotation.class"))
				.methodSignature(testMethodSignature)
				.srcPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/junit5/ParameterizedTestAnnotation.java"))
				.args(-1)
				.build();
		
		methodInfo = new MethodInvokedInfo.Builder()
				.binPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/others/auxClasses/AuxClass.class"))
				.srcPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/others/auxClasses/AuxClass.java"))
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("factorial")
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
				Arrays.asList(35,36,39)
			),
			testPaths
		);
	}
	
	/**
	 * Tests {@link examples.junit5.ParameterizedTestAnnotation#test1(int)} test
	 * method with its second argument.
	 */
	@Test
	public void test1_int_arg2() throws Throwable 
	{
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		List<List<Integer>> testPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		MethodInvokedInfo testMethodInfo, methodInfo;
		CollectorInfo ci;
		int invocationLine = 38;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.junit5.ParameterizedTestAnnotation.test1(int)";
		methodSignature = "examples.others.auxClasses.AuxClass.factorial(int)";
		
		init(
				"examples.junit5.ParameterizedTestAnnotation", 
				testMethodSignature,
				Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/junit5/ParameterizedTestAnnotation.class"),
				Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/junit5/ParameterizedTestAnnotation.java"),
				0
		 );
		
		testMethodInfo = new MethodInvokedInfo.Builder()
				.binPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/junit5/ParameterizedTestAnnotation.class"))
				.methodSignature(testMethodSignature)
				.srcPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/junit5/ParameterizedTestAnnotation.java"))
				.args(0)
				.build();
		
		methodInfo = new MethodInvokedInfo.Builder()
				.binPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/others/auxClasses/AuxClass.class"))
				.srcPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/others/auxClasses/AuxClass.java"))
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("factorial")
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
				Arrays.asList(35,36,39)
			),
			testPaths
		);
	}
	
	/**
	 * Tests {@link examples.junit5.ParameterizedTestAnnotation#test1(int)} test
	 * method with its third argument.
	 */
	@Test
	public void test1_int_arg3() throws Throwable 
	{
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		List<List<Integer>> testPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		MethodInvokedInfo testMethodInfo, methodInfo;
		CollectorInfo ci;
		int invocationLine = 38;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.junit5.ParameterizedTestAnnotation.test1(int)";
		methodSignature = "examples.others.auxClasses.AuxClass.factorial(int)";
		
		init(
				"examples.junit5.ParameterizedTestAnnotation", 
				testMethodSignature,
				Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/junit5/ParameterizedTestAnnotation.class"),
				Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/junit5/ParameterizedTestAnnotation.java"),
				1
		 );
		
		testMethodInfo = new MethodInvokedInfo.Builder()
				.binPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/junit5/ParameterizedTestAnnotation.class"))
				.methodSignature(testMethodSignature)
				.srcPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/junit5/ParameterizedTestAnnotation.java"))
				.args(1)
				.build();
		
		methodInfo = new MethodInvokedInfo.Builder()
				.binPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/others/auxClasses/AuxClass.class"))
				.srcPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/others/auxClasses/AuxClass.java"))
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("factorial")
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
				Arrays.asList(35,36,37,36,39)
			),
			testPaths
		);
	}
	
	/**
	 * Tests {@link examples.junit5.ParameterizedTestAnnotation#nullEmptyAndBlankStrings(String)} 
	 * test method with its first argument.
	 */
	@Test
	public void nullEmptyAndBlankStrings_arg1() throws Throwable 
	{
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		List<List<Integer>> testPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		MethodInvokedInfo testMethodInfo, methodInfo;
		CollectorInfo ci;
		int invocationLine = 45;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.junit5.ParameterizedTestAnnotation.nullEmptyAndBlankStrings(String)";
		methodSignature = "examples.others.auxClasses.AuxClass.trim(String)";
		
		init(
				"examples.junit5.ParameterizedTestAnnotation", 
				testMethodSignature,
				Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/junit5/ParameterizedTestAnnotation.class"),
				Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/junit5/ParameterizedTestAnnotation.java"),
				new Object[] {" "}
		 );
		
		testMethodInfo = new MethodInvokedInfo.Builder()
				.binPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/junit5/ParameterizedTestAnnotation.class"))
				.methodSignature(testMethodSignature)
				.srcPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/junit5/ParameterizedTestAnnotation.java"))
				.args(" ")
				.build();
		
		methodInfo = new MethodInvokedInfo.Builder()
				.binPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/others/auxClasses/AuxClass.class"))
				.srcPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/others/auxClasses/AuxClass.java"))
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("trim")
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
				Arrays.asList(105,106)
			),
			testPaths
		);
	}
	
	/**
	 * Tests {@link examples.junit5.ParameterizedTestAnnotation#nullEmptyAndBlankStrings(String)} 
	 * test method with its second argument.
	 */
	@Test
	public void nullEmptyAndBlankStrings_arg2() throws Throwable 
	{
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		List<List<Integer>> testPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		MethodInvokedInfo testMethodInfo, methodInfo;
		CollectorInfo ci;
		int invocationLine = 45;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.junit5.ParameterizedTestAnnotation.nullEmptyAndBlankStrings(String)";
		methodSignature = "examples.others.auxClasses.AuxClass.trim(String)";
		
		init(
				"examples.junit5.ParameterizedTestAnnotation", 
				testMethodSignature,
				Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/junit5/ParameterizedTestAnnotation.class"),
				Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/junit5/ParameterizedTestAnnotation.java"),
				new Object[] {"   "}
		 );
		
		testMethodInfo = new MethodInvokedInfo.Builder()
				.binPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/junit5/ParameterizedTestAnnotation.class"))
				.methodSignature(testMethodSignature)
				.srcPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/junit5/ParameterizedTestAnnotation.java"))
				.args("   ")
				.build();
		
		methodInfo = new MethodInvokedInfo.Builder()
				.binPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/others/auxClasses/AuxClass.class"))
				.srcPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/others/auxClasses/AuxClass.java"))
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("trim")
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
				Arrays.asList(105,106)
			),
			testPaths
		);
	}
	
	/**
	 * Tests {@link examples.junit5.ParameterizedTestAnnotation#nullEmptyAndBlankStrings(String)} 
	 * test method with its third argument.
	 */
	@Test
	public void nullEmptyAndBlankStrings_arg3() throws Throwable 
	{
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		List<List<Integer>> testPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		MethodInvokedInfo testMethodInfo, methodInfo;
		CollectorInfo ci;
		int invocationLine = 45;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.junit5.ParameterizedTestAnnotation.nullEmptyAndBlankStrings(String)";
		methodSignature = "examples.others.auxClasses.AuxClass.trim(String)";
		
		init(
				"examples.junit5.ParameterizedTestAnnotation", 
				testMethodSignature,
				Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/junit5/ParameterizedTestAnnotation.class"),
				Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/junit5/ParameterizedTestAnnotation.java"),
				new Object[] {"\t"}
		 );
		
		testMethodInfo = new MethodInvokedInfo.Builder()
				.binPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/junit5/ParameterizedTestAnnotation.class"))
				.methodSignature(testMethodSignature)
				.srcPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/junit5/ParameterizedTestAnnotation.java"))
				.args("\t")
				.build();
		
		methodInfo = new MethodInvokedInfo.Builder()
				.binPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/others/auxClasses/AuxClass.class"))
				.srcPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/others/auxClasses/AuxClass.java"))
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("trim")
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
				Arrays.asList(105,106)
			),
			testPaths
		);
	}
	
	/**
	 * Tests {@link examples.junit5.ParameterizedTestAnnotation#nullEmptyAndBlankStrings(String)} 
	 * test method with its fourth argument.
	 */
	@Test
	public void nullEmptyAndBlankStrings_arg4() throws Throwable 
	{
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		List<List<Integer>> testPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		MethodInvokedInfo testMethodInfo, methodInfo;
		CollectorInfo ci;
		int invocationLine = 45;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.junit5.ParameterizedTestAnnotation.nullEmptyAndBlankStrings(String)";
		methodSignature = "examples.others.auxClasses.AuxClass.trim(String)";
		
		init(
				"examples.junit5.ParameterizedTestAnnotation", 
				testMethodSignature,
				Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/junit5/ParameterizedTestAnnotation.class"),
				Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/junit5/ParameterizedTestAnnotation.java"),
				new Object[] {"\n"}
		 );
		
		testMethodInfo = new MethodInvokedInfo.Builder()
				.binPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/junit5/ParameterizedTestAnnotation.class"))
				.methodSignature(testMethodSignature)
				.srcPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/junit5/ParameterizedTestAnnotation.java"))
				.args("\n")
				.build();
		
		methodInfo = new MethodInvokedInfo.Builder()
				.binPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/others/auxClasses/AuxClass.class"))
				.srcPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/others/auxClasses/AuxClass.java"))
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("trim")
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
				Arrays.asList(105,106)
			),
			testPaths
		);
	}
	
	/**
	 * Tests {@link examples.junit5.ParameterizedTestAnnotation#nullTest(String)} 
	 * test method.
	 */
	@Test
	public void nullTest() throws Throwable 
	{
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		List<List<Integer>> testPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		MethodInvokedInfo testMethodInfo, methodInfo;
		CollectorInfo ci;
		int invocationLine = 52;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.junit5.ParameterizedTestAnnotation.nullTest(String)";
		methodSignature = "examples.others.auxClasses.AuxClass.trim(String)";
		
		init(
				"examples.junit5.ParameterizedTestAnnotation", 
				testMethodSignature,
				Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/junit5/ParameterizedTestAnnotation.class"),
				Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/junit5/ParameterizedTestAnnotation.java"),
				new Object[] {null}
		 );
		
		testMethodInfo = new MethodInvokedInfo.Builder()
				.binPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/junit5/ParameterizedTestAnnotation.class"))
				.methodSignature(testMethodSignature)
				.srcPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/junit5/ParameterizedTestAnnotation.java"))
				.args(new Object[] {null})
				.build();
		
		methodInfo = new MethodInvokedInfo.Builder()
				.binPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/others/auxClasses/AuxClass.class"))
				.srcPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/others/auxClasses/AuxClass.java"))
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("trim")
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
				Arrays.asList(105)
			),
			testPaths
		);
	}
	
	/**
	 * Tests {@link examples.junit5.ParameterizedTestAnnotation#test1(String, int)} test
	 * method with its first argument.
	 */
	@Test
	public void test1_String_int_arg1() throws Throwable 
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
		int invocationLine = 59;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.junit5.ParameterizedTestAnnotation.test1(String, int)";
		methodSignature = "examples.others.auxClasses.AuxClass.countTotalArguments(Object[])";
		
		init(
				"examples.junit5.ParameterizedTestAnnotation", 
				testMethodSignature,
				Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/junit5/ParameterizedTestAnnotation.class"),
				Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/junit5/ParameterizedTestAnnotation.java"),
				new Object[] {"I", -1}
		 );
		
		testMethodInfo = new MethodInvokedInfo.Builder()
				.binPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/junit5/ParameterizedTestAnnotation.class"))
				.methodSignature(testMethodSignature)
				.srcPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/junit5/ParameterizedTestAnnotation.java"))
				.args("I", -1)
				.build();
		
		methodInfo = new MethodInvokedInfo.Builder()
				.binPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/others/auxClasses/AuxClass.class"))
				.srcPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/others/auxClasses/AuxClass.java"))
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("countTotalArguments")
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
				Arrays.asList(112, 113, 114, 115, 114, 115, 114, 117)
			),
			testPaths
		);
	}
	
	/**
	 * Tests {@link examples.junit5.ParameterizedTestAnnotation#test1(String, int)} test
	 * method with its second argument.
	 */
	@Test
	public void test1_String_int_arg2() throws Throwable 
	{
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		List<List<Integer>> testPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		MethodInvokedInfo testMethodInfo, methodInfo;
		CollectorInfo ci;
		int invocationLine = 59;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.junit5.ParameterizedTestAnnotation.test1(String, int)";
		methodSignature = "examples.others.auxClasses.AuxClass.countTotalArguments(Object[])";
		
		init(
				"examples.junit5.ParameterizedTestAnnotation", 
				testMethodSignature,
				Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/junit5/ParameterizedTestAnnotation.class"),
				Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/junit5/ParameterizedTestAnnotation.java"),
				new Object[] {"II", 0}
		 );
		
		testMethodInfo = new MethodInvokedInfo.Builder()
				.binPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/junit5/ParameterizedTestAnnotation.class"))
				.methodSignature(testMethodSignature)
				.srcPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/junit5/ParameterizedTestAnnotation.java"))
				.args("II", 0)
				.build();
		
		methodInfo = new MethodInvokedInfo.Builder()
				.binPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/others/auxClasses/AuxClass.class"))
				.srcPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/others/auxClasses/AuxClass.java"))
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("countTotalArguments")
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
				Arrays.asList(112,113,114,115,114,115,114,117)
			),
			testPaths
		);
	}
	
	/**
	 * Tests {@link examples.junit5.ParameterizedTestAnnotation#test1(String, int)} test
	 * method with its third argument.
	 */
	@Test
	public void test1_String_int_arg3() throws Throwable 
	{
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		List<List<Integer>> testPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		MethodInvokedInfo testMethodInfo, methodInfo;
		CollectorInfo ci;
		int invocationLine = 59;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.junit5.ParameterizedTestAnnotation.test1(String, int)";
		methodSignature = "examples.others.auxClasses.AuxClass.countTotalArguments(Object[])";
		
		init(
				"examples.junit5.ParameterizedTestAnnotation", 
				testMethodSignature,
				Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/junit5/ParameterizedTestAnnotation.class"),
				Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/junit5/ParameterizedTestAnnotation.java"),
				new Object[] {"III", 1}
		 );
		
		testMethodInfo = new MethodInvokedInfo.Builder()
				.binPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/junit5/ParameterizedTestAnnotation.class"))
				.methodSignature(testMethodSignature)
				.srcPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/junit5/ParameterizedTestAnnotation.java"))
				.args("III", 1)
				.build();
		
		methodInfo = new MethodInvokedInfo.Builder()
				.binPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/others/auxClasses/AuxClass.class"))
				.srcPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/others/auxClasses/AuxClass.java"))
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("countTotalArguments")
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
				Arrays.asList(112,113,114,115,114,115,114,117)
			),
			testPaths
		);
	}
	
	/**
	 * Tests {@link examples.junit5.ParameterizedTestAnnotation#withMethodSource(String, int)} test
	 * method with its first argument.
	 */
	@Test
	public void withMethodSource_arg1() throws Throwable 
	{
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		List<List<Integer>> testPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		MethodInvokedInfo testMethodInfo, methodInfo;
		CollectorInfo ci;
		int invocationLine = 66;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.junit5.ParameterizedTestAnnotation.withMethodSource(String, int)";
		methodSignature = "examples.others.auxClasses.AuxClass.concatStrNum(String, int)";
		
		init(
				"examples.junit5.ParameterizedTestAnnotation", 
				testMethodSignature,
				Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/junit5/ParameterizedTestAnnotation.class"),
				Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/junit5/ParameterizedTestAnnotation.java"),
				new Object[] {"Hello", 5}
		 );
		
		testMethodInfo = new MethodInvokedInfo.Builder()
				.binPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/junit5/ParameterizedTestAnnotation.class"))
				.methodSignature(testMethodSignature)
				.srcPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/junit5/ParameterizedTestAnnotation.java"))
				.args("Hello", 5)
				.build();
		
		methodInfo = new MethodInvokedInfo.Builder()
				.binPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/others/auxClasses/AuxClass.class"))
				.srcPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/others/auxClasses/AuxClass.java"))
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("concatStrNum")
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
				Arrays.asList(109)
			),
			testPaths
		);
	}
	
	/**
	 * Tests {@link examples.junit5.ParameterizedTestAnnotation#withMethodSource(String, int)} test
	 * method with its second argument.
	 */
	@Test
	public void withMethodSource_arg2() throws Throwable 
	{
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		List<List<Integer>> testPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		MethodInvokedInfo testMethodInfo, methodInfo;
		CollectorInfo ci;
		int invocationLine = 66;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.junit5.ParameterizedTestAnnotation.withMethodSource(String, int)";
		methodSignature = "examples.others.auxClasses.AuxClass.concatStrNum(String, int)";
		
		init(
				"examples.junit5.ParameterizedTestAnnotation", 
				testMethodSignature,
				Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/junit5/ParameterizedTestAnnotation.class"),
				Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/junit5/ParameterizedTestAnnotation.java"),
				new Object[] {"Hello", 5}
		 );
		
		testMethodInfo = new MethodInvokedInfo.Builder()
				.binPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/junit5/ParameterizedTestAnnotation.class"))
				.methodSignature(testMethodSignature)
				.srcPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/junit5/ParameterizedTestAnnotation.java"))
				.args("JUnit 5", 7)
				.build();
		
		methodInfo = new MethodInvokedInfo.Builder()
				.binPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/others/auxClasses/AuxClass.class"))
				.srcPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/others/auxClasses/AuxClass.java"))
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("concatStrNum")
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
				Arrays.asList(109)
			),
			testPaths
		);
	}
	
	/**
	 * Tests {@link examples.junit5.ParameterizedTestAnnotation#testWithEnumSourceInclude(ChronoUnit)} test
	 * method with its first argument.
	 */
	@Test
	public void testWithEnumSourceInclude_arg1() throws Throwable 
	{
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		List<List<Integer>> testPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		MethodInvokedInfo testMethodInfo, methodInfo;
		CollectorInfo ci;
		int invocationLine = 80;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.junit5.ParameterizedTestAnnotation.testWithEnumSourceInclude(ChronoUnit)";
		methodSignature = "examples.others.auxClasses.AuxClass.countTotalArguments(Object[])";
		
		init(
				"examples.junit5.ParameterizedTestAnnotation", 
				testMethodSignature,
				Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/junit5/ParameterizedTestAnnotation.class"),
				Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/junit5/ParameterizedTestAnnotation.java"),
				ChronoUnit.DAYS
		 );
		
		testMethodInfo = new MethodInvokedInfo.Builder()
				.binPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/junit5/ParameterizedTestAnnotation.class"))
				.methodSignature(testMethodSignature)
				.srcPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/junit5/ParameterizedTestAnnotation.java"))
				.args(ChronoUnit.DAYS)
				.build();
		
		methodInfo = new MethodInvokedInfo.Builder()
				.binPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/others/auxClasses/AuxClass.class"))
				.srcPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/others/auxClasses/AuxClass.java"))
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("countTotalArguments")
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
				Arrays.asList(112,113,114,115,114,117)
			),
			testPaths
		);
	}
	
	/**
	 * Tests {@link examples.junit5.ParameterizedTestAnnotation#testWithEnumSourceInclude(ChronoUnit)} test
	 * method with its second argument.
	 */
	@Test
	public void testWithEnumSourceInclude_arg2() throws Throwable 
	{
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		List<List<Integer>> testPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		MethodInvokedInfo testMethodInfo, methodInfo;
		CollectorInfo ci;
		int invocationLine = 80;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.junit5.ParameterizedTestAnnotation.testWithEnumSourceInclude(ChronoUnit)";
		methodSignature = "examples.others.auxClasses.AuxClass.countTotalArguments(Object[])";
		
		init(
				"examples.junit5.ParameterizedTestAnnotation", 
				testMethodSignature,
				Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/junit5/ParameterizedTestAnnotation.class"),
				Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/junit5/ParameterizedTestAnnotation.java"),
				ChronoUnit.HOURS
		 );
		
		testMethodInfo = new MethodInvokedInfo.Builder()
				.binPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/junit5/ParameterizedTestAnnotation.class"))
				.methodSignature(testMethodSignature)
				.srcPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/junit5/ParameterizedTestAnnotation.java"))
				.args(ChronoUnit.HOURS)
				.build();
		
		methodInfo = new MethodInvokedInfo.Builder()
				.binPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/others/auxClasses/AuxClass.class"))
				.srcPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/others/auxClasses/AuxClass.java"))
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("countTotalArguments")
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
				Arrays.asList(112,113,114,115,114,117)
			),
			testPaths
		);
	}
	
	/**
	 * Tests {@link examples.junit5.ParameterizedTestAnnotation#testWithEnumSourceIncludeUsingInterface(TemporalUnit)} test
	 * method with its first argument.
	 */
	@Test
	public void testWithEnumSourceIncludeUsingInterface_arg1() throws Throwable 
	{
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		List<List<Integer>> testPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		MethodInvokedInfo testMethodInfo, methodInfo;
		CollectorInfo ci;
		int invocationLine = 87;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.junit5.ParameterizedTestAnnotation.testWithEnumSourceIncludeUsingInterface(TemporalUnit)";
		methodSignature = "examples.others.auxClasses.AuxClass.countTotalArguments(Object[])";
		
		init(
				"examples.junit5.ParameterizedTestAnnotation", 
				testMethodSignature,
				Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/junit5/ParameterizedTestAnnotation.class"),
				Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/junit5/ParameterizedTestAnnotation.java"),
				ChronoUnit.DAYS
		 );
		
		testMethodInfo = new MethodInvokedInfo.Builder()
				.binPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/junit5/ParameterizedTestAnnotation.class"))
				.methodSignature(testMethodSignature)
				.srcPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/junit5/ParameterizedTestAnnotation.java"))
				.args(ChronoUnit.DAYS)
				.build();
		
		methodInfo = new MethodInvokedInfo.Builder()
				.binPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/others/auxClasses/AuxClass.class"))
				.srcPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/others/auxClasses/AuxClass.java"))
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("countTotalArguments")
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
				Arrays.asList(112,113,114,115,114,117)
			),
			testPaths
		);
	}
	
	/**
	 * Tests {@link examples.junit5.ParameterizedTestAnnotation#testWithEnumSourceIncludeUsingInterface(TemporalUnit)} test
	 * method with its second argument.
	 */
	@Test
	public void testWithEnumSourceIncludeUsingInterface_arg2() throws Throwable 
	{
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		List<List<Integer>> testPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		MethodInvokedInfo testMethodInfo, methodInfo;
		CollectorInfo ci;
		int invocationLine = 87;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.junit5.ParameterizedTestAnnotation.testWithEnumSourceIncludeUsingInterface(TemporalUnit)";
		methodSignature = "examples.others.auxClasses.AuxClass.countTotalArguments(Object[])";
		
		init(
				"examples.junit5.ParameterizedTestAnnotation", 
				testMethodSignature,
				Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/junit5/ParameterizedTestAnnotation.class"),
				Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/junit5/ParameterizedTestAnnotation.java"),
				ChronoUnit.HOURS
		 );
		
		testMethodInfo = new MethodInvokedInfo.Builder()
				.binPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/junit5/ParameterizedTestAnnotation.class"))
				.methodSignature(testMethodSignature)
				.srcPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/junit5/ParameterizedTestAnnotation.java"))
				.args(ChronoUnit.HOURS)
				.build();
		
		methodInfo = new MethodInvokedInfo.Builder()
				.binPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/others/auxClasses/AuxClass.class"))
				.srcPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/others/auxClasses/AuxClass.java"))
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("countTotalArguments")
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
				Arrays.asList(112,113,114,115,114,117)
			),
			testPaths
		);
	}
}