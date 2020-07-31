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
 * Tests test path computation for the tested methods of the following tests
 * using {@link MethodExecutionFlow} class: 
 * <ul>
 * 	<li>{@link examples.junit5.TestAnnotation}</li> 
 * 	<li>{@link examples.junit5.ParameterizedTestAnnotation}</li>
 * 	<li>{@link examples.junit5.RepeatedTestAnnotation}</li>
 * </ul>
 */
@SkipCollection
public class JUnit5 extends MethodExecutionFlowTest
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
	 * Tests {@link examples.junit5.ParameterizedTestAnnotation#test1()} test
	 * method with its first argument.
	 */
//	@Test
//	public void parameterizedTestAnnotation_test1_firstArg() throws Throwable 
//	{
//		/**
//		 * Stores information about collected methods.
//		 * <ul>
//		 * 	<li><b>Key:</b> Method invocation line</li>
//		 * 	<li><b>Value:</b> List of methods invoked from this line</li>
//		 * </ul>
//		 */
//		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
//
//		List<List<Integer>> testPaths;
//		List<CollectorInfo> methodsInvoked = new ArrayList<>();
//		String testMethodSignature, methodSignature;
//		MethodInvokedInfo testMethodInfo, methodInfo;
//		CollectorInfo ci;
//		int invocationLine = 38;
//		
//		
//		// Defines which methods will be collected
//		testMethodSignature = "examples.junit5.ParameterizedTestAnnotation.test1(int)";
//		methodSignature = "examples.others.auxClasses.AuxClass.factorial(int)";
//		
//		init(
//				"examples.junit5.ParameterizedTestAnnotation", 
//				testMethodSignature,
//				Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/junit5/ParameterizedTestAnnotation.class"),
//				Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/junit5/ParameterizedTestAnnotation.java"),
//				-1
//		 );
//		
//		testMethodInfo = new MethodInvokedInfo.Builder()
//				.binPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/junit5/ParameterizedTestAnnotation.class"))
//				.methodSignature(testMethodSignature)
//				.srcPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/junit5/ParameterizedTestAnnotation.java"))
//				.args(-1)
//				.build();
//		
//		methodInfo = new MethodInvokedInfo.Builder()
//				.binPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/others/auxClasses/AuxClass.class"))
//				.srcPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/others/auxClasses/AuxClass.java"))
//				.invocationLine(invocationLine)
//				.methodSignature(methodSignature)
//				.methodName("factorial")
//				.build();
//		
//		ci = new CollectorInfo.Builder()
//				.methodInfo(methodInfo)
//				.testMethodInfo(testMethodInfo)
//				.build();
//		
//		methodsInvoked.add(ci);
//		methodCollector.put(invocationLine, methodsInvoked);
//		
//		// Computes test path
//		ExecutionFlow ef = new MethodExecutionFlow(methodCollector, false);
//		
//		// Gets test path
//		testPaths = ef.execute().getTestPaths(testMethodSignature, methodSignature);
//		
//		assertEquals(
//			Arrays.asList(
//				Arrays.asList(95,97,101)
//			),
//			testPaths
//		);
//	}
	
	/**
	 * Tests {@link examples.junit5.ParameterizedTestAnnotation#test1()} test
	 * method with its second argument.
	 */
	@Test
	public void parameterizedTestAnnotation_test1_secondArg() throws Throwable 
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
				Arrays.asList(95,97,101)
			),
			testPaths
		);
	}
	
	/**
	 * Tests {@link examples.junit5.ParameterizedTestAnnotation#test1()} test
	 * method with its third argument.
	 */
	@Test
	public void parameterizedTestAnnotation_test1_thirdArg() throws Throwable 
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
				Arrays.asList(95,97,98,97,101)
			),
			testPaths
		);
	}
	
	/**
	 * Tests {@link examples.junit5.RepeatedTestAnnotation#test1()} test
	 * method.
	 */
	@Test
	public void repeatedTestAnnotation_test1() throws Throwable 
	{
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		List<List<Integer>> testPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		MethodInvokedInfo testMethodInfo, methodInfo;
		CollectorInfo ci;
		int invocationLine = 25;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.junit5.RepeatedTestAnnotation.test1()";
		methodSignature = "examples.others.auxClasses.AuxClass.factorial(int)";
		
		init(
				"examples.junit5.RepeatedTestAnnotation", 
				testMethodSignature,
				Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/junit5/RepeatedTestAnnotation.class"),
				 Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/junit5/RepeatedTestAnnotation.java")
		 );
		
		testMethodInfo = new MethodInvokedInfo.Builder()
				.binPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/junit5/RepeatedTestAnnotation.class"))
				.methodSignature(testMethodSignature)
				.srcPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/junit5/RepeatedTestAnnotation.java"))
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
				Arrays.asList(95,97,98,97,98,97,98,97,98,97,101),
				Arrays.asList(95,97,98,97,98,97,98,97,98,97,101)
			),
			testPaths
		);
	}
	
	/**
	 * Tests {@link examples.junit5.TestAnnotation#test1()} test
	 * method.
	 */
	@Test
	public void testAnnotation_test1() throws Throwable 
	{
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();

		List<List<Integer>> testPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		MethodInvokedInfo testMethodInfo, methodInfo;
		CollectorInfo ci;
		int invocationLine = 28;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.junit5.TestAnnotation.test1()";
		methodSignature = "examples.others.auxClasses.AuxClass.factorial(int)";
		
		init(
				"examples.junit5.TestAnnotation", 
				testMethodSignature,
				Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/junit5/TestAnnotation.class"),
				Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/junit5/TestAnnotation.java")
		 );
		
		testMethodInfo = new MethodInvokedInfo.Builder()
				.binPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/junit5/TestAnnotation.class"))
				.methodSignature(testMethodSignature)
				.srcPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/junit5/TestAnnotation.java"))
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
					Arrays.asList(95,97,98,97,98,97,98,97,98,97,101)
			),
			testPaths
		);
	}
}