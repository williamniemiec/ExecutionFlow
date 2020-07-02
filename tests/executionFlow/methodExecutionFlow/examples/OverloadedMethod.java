package executionFlow.methodExecutionFlow.examples;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import executionFlow.ConsoleOutput;
import executionFlow.ExecutionFlow;
import executionFlow.MethodExecutionFlow;
import executionFlow.core.file.FileManager;
import executionFlow.core.file.InvokerManager;
import executionFlow.core.file.ParserType;
import executionFlow.core.file.parser.factory.PreTestMethodFileParserFactory;
import executionFlow.info.CollectorInfo;
import executionFlow.info.MethodInvokerInfo;
import executionFlow.runtime.SkipCollection;


/**
 * Tests test path computation for the tested methods of 
 * {@link examples.overloadedMethod.OverloadTest} test using 
 * {@link MethodExecutionFlow} class.
 */
@SkipCollection
public class OverloadedMethod 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static FileManager testMethodFileManager;
	private static InvokerManager testMethodManager;
	private static final Path PATH_BIN_TEST_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath(), "bin/examples/overloadedMethod/OverloadTest.class");
	private static final Path PATH_SRC_TEST_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath(), "examples/examples/overloadedMethod/OverloadTest.java");
	private static final String PACKAGE_TEST_METHOD = "examples.builderPattern";
	private static final Path PATH_BIN_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath(), "bin/examples/overloadedMethod/OverloadClass.class");
	private static final Path PATH_SRC_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath(), "examples/examples/overloadedMethod/OverloadClass.java");
	
	
	//-------------------------------------------------------------------------
	//		Test preparers
	//-------------------------------------------------------------------------
	/**
	 * @throws		IOException If an error occurs during file parsing
	 * @throws		ClassNotFoundException If class {@link FileManager} was not
	 * found
	 */
	@BeforeClass
	public static void init() throws IOException, ClassNotFoundException
	{
		// Initializes ExecutionFlow
		ExecutionFlow.destroy();
		ExecutionFlow.init();
				
		// Creates backup from original files
		testMethodManager = new InvokerManager(ParserType.PRE_TEST_METHOD, false);
		
		testMethodFileManager = new FileManager(
			PATH_SRC_TEST_METHOD,
			MethodInvokerInfo.getCompiledFileDirectory(PATH_BIN_TEST_METHOD),
			PACKAGE_TEST_METHOD,
			new PreTestMethodFileParserFactory(),
			"original_pre_processing"
		);
		
		// Parses test method
		try {
			ConsoleOutput.showInfo("Pre-processing test method...");
			testMethodManager.parse(testMethodFileManager).compile(testMethodFileManager);
			ConsoleOutput.showInfo("Pre-processing completed");
		} catch (IOException e) {
			testMethodManager.restoreAll();
			testMethodManager.deleteBackup();
			throw e;
		}
	}
	
	/**
	 * Restores original files
	 */
	@AfterClass
	public static void restore()
	{
		ExecutionFlow.testMethodManager.restoreAll();
		ExecutionFlow.testMethodManager.deleteBackup();
		
		ExecutionFlow.invokerManager.restoreAll();
		ExecutionFlow.invokerManager.deleteBackup();
		
		testMethodManager.restoreAll();
		testMethodManager.deleteBackup();
		
		ExecutionFlow.destroy();
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
		int invocationLine = 11;
		
		// Defines which methods will be collected
		String testMethodSignature = "examples.overloadedMethod.OverloadTest.testOverloadedMethod()";
		String methodSignature = "examples.overloadedMethod.OverloadClass.overloadedMethod()";
		
		MethodInvokerInfo testMethodInfo = new MethodInvokerInfo.MethodInvokerInfoBuilder()
				.classPath(PATH_BIN_TEST_METHOD)
				.methodSignature(testMethodSignature)
				.srcPath(PATH_SRC_TEST_METHOD)
				.build();
		
		MethodInvokerInfo methodInfo = new MethodInvokerInfo.MethodInvokerInfoBuilder()
				.classPath(PATH_BIN_METHOD)
				.srcPath(PATH_SRC_METHOD)
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("overloadedMethod")
				.build();
		
		CollectorInfo ci = new CollectorInfo.CollectorInfoBuilder()
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
				Arrays.asList(40,41,42,43)
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
		int invocationLine = 18;
		
		// Defines which methods will be collected
		String testMethodSignature = "examples.overloadedMethod.OverloadTest.testOverloadedMethod2()";
		String methodSignature = "examples.overloadedMethod.OverloadClass.overloadedMethod(int)";
		
		MethodInvokerInfo testMethodInfo = new MethodInvokerInfo.MethodInvokerInfoBuilder()
				.classPath(PATH_BIN_TEST_METHOD)
				.methodSignature(testMethodSignature)
				.srcPath(PATH_SRC_TEST_METHOD)
				.build();
		
		MethodInvokerInfo methodInfo = new MethodInvokerInfo.MethodInvokerInfoBuilder()
				.classPath(PATH_BIN_METHOD)
				.srcPath(PATH_SRC_METHOD)
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("overloadedMethod")
				.build();
		
		CollectorInfo ci = new CollectorInfo.CollectorInfoBuilder()
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
				Arrays.asList(34,35)
			),
			testPaths
		);
	}
}