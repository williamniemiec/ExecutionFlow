package executionFlow;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import executionFlow.ExecutionFlow;
import executionFlow.core.JDB;
import executionFlow.info.ClassConstructorInfo;
import executionFlow.info.ClassMethodInfo;
import executionFlow.info.CollectorInfo;
import executionFlow.info.SignaturesInfo;
import executionFlow.runtime.SkipCollection;


/**
 * Tests test path computation of class {@link ControlFlowTest} from debug 
 * analysis.
 */
@SkipCollection
public class EF_ControlFlowTest 
{
	@Test
	public void ifElseTest_earlyReturn() throws Throwable 
	{
		/**
		 * Stores information about collected methods.<hr/>
		 * <ul>
		 * 		<li><b>Key:</b> Method invocation line</li>
		 * 		<li><b>Value:</b> List of methods invoked from this line</li>
		 * </ul>
		 */
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		
		/**
		 * Stores computed test paths from a class.<br />
		 * <ul>
		 * 		<li><b>Key:</b> test_method_signature + '$' + method_signature</li>
		 * 		<li>
		 * 			<b>Value:</b> 
		 * 			<ul>
		 * 				<li><b>Key:</b> Test method signature and method signature</li>
		 * 				<li><b>Value:</b> Test path</li>
		 * 			</ul>
		 * 		</li>
		 * </ul>
		 */
		Map<String, Map<SignaturesInfo, List<Integer>>> classPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		Collection<List<Integer>> testPaths;
		List<Integer> testPath;
		
		// Defines which methods will be collected
		String testMethodSignature = "controlFlow.ControlFlowTest.ifElseTest_earlyReturn()";
		String methodSignature = "controlFlow.TestClass_ControlFlow.ifElseMethod(int)";
		
		ClassMethodInfo ifElseTest_earlyReturn = new ClassMethodInfo.ClassMethodInfoBuilder()
				.classPath(new File("bin/controlFlow/TestClass_ControlFlow.class").getAbsolutePath())
				.testClassPath(new File("bin/controlFlow/ControlFlowTest.class").getAbsolutePath())
				.srcPath(new File("examples/controlFlow/TestClass_ControlFlow.java").getAbsolutePath())
				.testSrcPath(new File("examples/controlFlow/ControlFlowTest.java").getAbsolutePath())
				.invocationLine(19)
				.methodSignature(methodSignature)
				.testMethodSignature(testMethodSignature)
				.methodName("ifElseMethod")
				.build();

		methodsInvoked.add(new CollectorInfo(ifElseTest_earlyReturn));
		methodCollector.put(19, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new ExecutionFlow(methodCollector, 23);
		classPaths = ef.execute().getClassTestPaths();
		
		// Gets test path
		testPaths = getTestPaths(classPaths, testMethodSignature, methodSignature);
		testPath = getFirstTestPath(testPaths);
		
		assertEquals(Arrays.asList(25,26), testPath);
	}
	
	@Test
	public void tryCatchTest1() throws Throwable 
	{
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();
		Map<String, Map<SignaturesInfo, List<Integer>>> classPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		Collection<List<Integer>> testPaths;
		List<Integer> testPath;
		
		// Defines which methods will be collected
		String testMethodSignature = "controlFlow.ControlFlowTest.tryCatchTest1()";
		String methodSignature = "controlFlow.TestClass_ControlFlow.tryCatchMethod_try()";
		
		ClassMethodInfo ifElseTest_earlyReturn = new ClassMethodInfo.ClassMethodInfoBuilder()
				.classPath(new File("bin/controlFlow/TestClass_ControlFlow.class").getAbsolutePath())
				.testClassPath(new File("bin/controlFlow/ControlFlowTest.class").getAbsolutePath())
				.srcPath(new File("examples/controlFlow/TestClass_ControlFlow.java").getAbsolutePath())
				.testSrcPath(new File("examples/controlFlow/ControlFlowTest.java").getAbsolutePath())
				.invocationLine(58)
				.methodSignature(methodSignature)
				.testMethodSignature(testMethodSignature)
				.methodName("tryCatchMethod_try")
				.build();

		methodsInvoked.add(new CollectorInfo(ifElseTest_earlyReturn));
		methodCollector.put(58, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new ExecutionFlow(methodCollector, 59);
		classPaths = ef.execute().getClassTestPaths();
		
		// Gets test path
		testPaths = getTestPaths(classPaths, testMethodSignature, methodSignature);
		testPath = getFirstTestPath(testPaths);
		
		assertEquals(Arrays.asList(50,52,53,54,55,56,57,62), testPath);
	}
	
	/**
	 * Gets all test paths obtained from {@link ExecutionFlow}. 
	 * 
	 * @param testPaths Test paths obtained from ExecutionFlow 
	 * @param testMethodSignature Signature of the test method
	 * @param methodSignature Signature of the method
	 * @return Collection of all test paths
	 */
	private Collection<List<Integer>> getTestPaths(Map<String, Map<SignaturesInfo, List<Integer>>> testPaths, 
			String testMethodSignature, String methodSignature)
	{
		return testPaths.get(testMethodSignature+"$"+methodSignature).values();
	}
	
	/**
	 * Extracts first test path.
	 * 
	 * @param testPaths Collection with test paths
	 * @return First test path
	 */
	private List<Integer> getFirstTestPath(Collection<List<Integer>> testPaths)
	{
		Iterator<List<Integer>> it = testPaths.iterator();
		
		return it.hasNext() ? it.next() : null;
	}
}
