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
 * {@link examples.methodCalledByTestedInvoked.MethodCalledByTestedInvoked_Test} test using 
 * {@link MethodExecutionFlow} class.
 */
@SkipCollection
public class InvokedMethodsByTestedInvoked extends MethodExecutionFlowTest
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final Path PATH_BIN_TEST_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath().toString(), 
					"bin/examples/methodCalledByTestedInvoked/MethodCalledByTestedInvoked_Test.class");
	private static final Path PATH_SRC_TEST_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath().toString(), 
					"examples/examples/methodCalledByTestedInvoked/MethodCalledByTestedInvoked_Test.java");
	private static final String PACKAGE_TEST_METHOD = "examples.methodCalledByTestedInvoked";
	private static final Path PATH_BIN_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath().toString(), 
					"bin/examples/methodCalledByTestedInvoked/MethodCalledByTestedInvoked_Class.class");
	private static final Path PATH_SRC_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath().toString(), 
					"examples/examples/methodCalledByTestedInvoked/MethodCalledByTestedInvoked_Class.java");
	
	
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
	 * Tests {@link examples.methodCalledByTestedInvoked.InvokedMethodsByTestedInvoker_Class#A()}
	 * method.
	 */
	@Test
	public void A() throws Throwable 
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
		int invocationLine = 20;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.methodCalledByTestedInvoked.MethodCalledByTestedInvoked_Test.T()";
		methodSignature = "examples.methodCalledByTestedInvoked.MethodCalledByTestedInvoked_Class.A()";
		
		init("examples.methodCalledByTestedInvoked.MethodCalledByTestedInvoked_Test", testMethodSignature);
		
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
				.invokedName("A")
				.build();
		
		ci = new CollectorInfo(methodInfo, testMethodInfo);
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new MethodExecutionFlow(processingManager, methodCollector);
		
		// Gets test path
		testPaths = ef.execute().getTestPaths(testMethodSignature, methodSignature);
		
		assertEquals(
			Arrays.asList(
				Arrays.asList(10,11,12)
			),
			testPaths
		);
	}
}