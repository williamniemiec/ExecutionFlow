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
import executionFlow.util.Logger;


/**
 * Tests test path computation for the tested methods of 
 * {@link examples.others.MultiargsTest} test using 
 * {@link MethodExecutionFlow} class.
 */
@SkipCollection
public class MultiargsTest extends MethodExecutionFlowTest
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final Path PATH_BIN_TEST_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/others/MultiargsTest.class");
	private static final Path PATH_SRC_TEST_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/others/MultiargsTest.java");
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
	 * Tests {@link examples.others.MultiargsTest#methodCallMultiLineArgsTest()} 
	 * test method.
	 */
//	@Test
//	public void methodCallMultiLineArgsTest() throws Throwable 
//	{
//		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
//
//		List<List<Integer>> testPaths;
//		List<CollectorInfo> methodsInvoked = new ArrayList<>();
//		String testMethodSignature, methodSignature;
//		MethodInvokedInfo testMethodInfo, methodInfo;
//		CollectorInfo ci;
//		int invocationLine = 18;
//		
//		
//		// Defines which methods will be collected
//		testMethodSignature = "examples.others.MultiargsTest.methodCallMultiLineArgsTest()";
//		methodSignature = "examples.others.auxClasses.AuxClass.identity(int, int, int, int, int)";
//		
//		init("examples.others.auxClasses.AuxClass", testMethodSignature);
//		
//		testMethodInfo = new MethodInvokedInfo.Builder()
//				.binPath(PATH_BIN_TEST_METHOD)
//				.methodSignature(testMethodSignature)
//				.srcPath(PATH_SRC_TEST_METHOD)
//				.build();
//		
//		methodInfo = new MethodInvokedInfo.Builder()
//				.binPath(PATH_BIN_METHOD)
//				.srcPath(PATH_SRC_METHOD)
//				.invocationLine(invocationLine)
//				.methodSignature(methodSignature)
//				.methodName("identity")
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
//				Arrays.asList(102,103)
//			),
//			testPaths
//		);
//	}
	
	/**
	 * Tests {@link examples.others.MultiargsTest#simethodCallMultLineArgsWithBrokenLinesmpleTestPath()} 
	 * test method.
	 */
	@Test
	public void methodCallMultLineArgsWithBrokenLines() throws Throwable 
	{
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();

		List<List<Integer>> testPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		MethodInvokedInfo testMethodInfo, methodInfo;
		CollectorInfo ci;
		int invocationLine = 26;
		
		Logger.setLevel(Logger.Level.DEBUG);
		// Defines which methods will be collected
		testMethodSignature = "examples.others.MultiargsTest.methodCallMultLineArgsWithBrokenLines()";
		methodSignature = "examples.others.auxClasses.AuxClass.identity(int, int, int, int, int)";
		
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
				.methodName("identity")
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
				Arrays.asList(102,103)
			),
			testPaths
		);
	}
}
