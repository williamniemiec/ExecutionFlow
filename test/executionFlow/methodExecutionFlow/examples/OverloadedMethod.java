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
 * {@link examples.overloadedMethod.OverloadTest} test using 
 * {@link MethodExecutionFlow} class.
 */
@SkipCollection
public class OverloadedMethod extends MethodExecutionFlowTest
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final Path PATH_BIN_TEST_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/overloadedMethod/OverloadTest.class");
	private static final Path PATH_SRC_TEST_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/overloadedMethod/OverloadTest.java");
	private static final String PACKAGE_TEST_METHOD = "examples.builderPattern";
	private static final Path PATH_BIN_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/overloadedMethod/OverloadClass.class");
	private static final Path PATH_SRC_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/overloadedMethod/OverloadClass.java");
	
	
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
	 * Tests {@link examples.overloadedMethod.OverloadClass.overloadedMethod()}
	 * method.
	 */
	@Test
	public void testOverloadedMethod() throws Throwable 
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
		int invocationLine = 11;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.overloadedMethod.OverloadTest.testOverloadedMethod()";
		methodSignature = "examples.overloadedMethod.OverloadClass.overloadedMethod()";
		
		init("examples.overloadedMethod.OverloadClass", testMethodSignature);
		
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
				.invokedName("overloadedMethod")
				.build();
		
		ci = new CollectorInfo(methodInfo, testMethodInfo);
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		testPaths = computeTestPath(methodCollector, testMethodSignature, methodSignature);
		
		assertEquals(
			Arrays.asList(
				Arrays.asList(15,16,17,18)
			),
			testPaths
		);
	}
	
	/**
	 * Tests {@link examples.overloadedMethod.OverloadClass.overloadedMethod(int)}
	 * method.
	 */
	@Test
	public void testOverloadedMethod2() throws Throwable 
	{
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		List<List<Integer>> testPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		InvokedInfo testMethodInfo, methodInfo;
		CollectorInfo ci;
		int invocationLine = 18;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.overloadedMethod.OverloadTest.testOverloadedMethod2()";
		methodSignature = "examples.overloadedMethod.OverloadClass.overloadedMethod(int)";
		
		init("examples.overloadedMethod.OverloadClass", testMethodSignature);
		
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
				.invokedName("overloadedMethod")
				.build();
		
		ci = new CollectorInfo(methodInfo, testMethodInfo);
		
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
}