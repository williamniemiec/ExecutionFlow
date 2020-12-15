package executionFlow.methodExecutionFlow.examples.junit5;

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
 * {@link examples.junit5.RepeatedTestAnnotation} test using 
 * {@link MethodExecutionFlow}.
 */
@SkipCollection
public class RepeatedTestAnnotation extends MethodExecutionFlowTest
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
		InvokedInfo testMethodInfo, methodInfo;
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
		
		testMethodInfo = new InvokedInfo.Builder()
				.binPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/junit5/RepeatedTestAnnotation.class"))
				.invokedSignature(testMethodSignature)
				.srcPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/junit5/RepeatedTestAnnotation.java"))
				.build();
		
		methodInfo = new InvokedInfo.Builder()
				.binPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/others/auxClasses/AuxClass.class"))
				.srcPath(Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/others/auxClasses/AuxClass.java"))
				.invocationLine(invocationLine)
				.invokedSignature(methodSignature)
				.invokedName("factorial")
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
				Arrays.asList(35,36,37,38,39,37,38,39,37,38,39,37,38,39,37,41),
				Arrays.asList(35,36,37,38,39,37,38,39,37,38,39,37,38,39,37,41)
			),
			testPaths
		);
	}
}