package executionFlow;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import executionFlow.info.ClassConstructorInfo;
import executionFlow.info.ClassMethodInfo;
import executionFlow.info.CollectorInfo;
import executionFlow.runtime.SkipCollection;


/**
 * Test whether the program is working after the collection has been made.
 */

@SkipCollection
public class ExecutionFlowTest 
{
	/*
	@Test
	public void testStaticMethods() throws Throwable 
	{
		String classPath = "bin/math/Calculator.class";
		List<CollectorInfo> collectorInfo = new ArrayList<>();
		
		ClassMethodInfo sumMethod = new ClassMethodInfo.ClassMethodInfoBuilder()
				.testMethodSignature("executionFlow.ExecutionFlowTest()")
				.classPath(classPath)
				.methodName("sum")
				.methodSignature("math.Calculator")
				.returnType(int.class)
				.parameterTypes(new Class<?>[] {int.class, int.class})
				.args(2,3)
				.build();
		
		ClassMethodInfo loopMethod = new ClassMethodInfo.ClassMethodInfoBuilder()
				.testMethodSignature("executionFlow.ExecutionFlowTest()")
				.classPath(classPath)
				.methodName("loop")
				.methodSignature("math.Calculator")
				.build();
		
		collectorInfo.add(new CollectorInfo(sumMethod));
		collectorInfo.add(new CollectorInfo(loopMethod));
		
		ExecutionFlow ef = new ExecutionFlow(collectorInfo);
		
		// Expected result
		Map<String, List<Integer>> expectedClassPaths = new HashMap<>();
		expectedClassPaths.put("math.Calculator.loop()", Arrays.asList(19, 21, 22, 21, 22, 21, 22, 21));
		expectedClassPaths.put("math.Calculator.sum(int,int)", Arrays.asList(8, 10, 11, 10, 11, 10, 11, 10, 14));
		
		// Results obtained
		ef.execute().export();
		Map<String, List<Integer>> classPathsObtained = ef.execute().getClassPaths();
		
		assertEquals(expectedClassPaths, classPathsObtained);
	}
	
	
	@Test
	public void testNonStaticMethods() throws Throwable 
	{
		String classPath = "bin/math/CalculatorNonStatic.class";
		List<CollectorInfo> collectorInfo = new ArrayList<>();
		
		ClassMethodInfo sumMethod = new ClassMethodInfo.ClassMethodInfoBuilder()
				.testMethodSignature("executionFlow.ExecutionFlowTest()")
				.classPath(classPath)
				.methodName("sum")
				.methodSignature("math.CalculatorNonStatic")
				.returnType(int.class)
				.parameterTypes(new Class<?>[] {int.class, int.class})
				.args(2,3)
				.build();
		
		collectorInfo.add(new CollectorInfo(sumMethod));
		
		//ClassConstructorInfo isn't necessary (constructor default / empty)
		
		ExecutionFlow ef = new ExecutionFlow(collectorInfo);
		
		// Expected result
		Map<String, List<Integer>> expectedClassPaths = new HashMap<>();
		expectedClassPaths.put("math.CalculatorNonStatic.sum(int,int)", Arrays.asList(8, 10, 11, 10, 11, 10, 11, 10, 14));
		
		// Results obtained
		ef.execute().export();
		Map<String, List<Integer>> classPathsObtained = ef.execute().getClassPaths();
		
		assertEquals(expectedClassPaths, classPathsObtained);
	}
	
	@Test
	public void testNonStaticMethodsWithConstructor() throws Throwable 
	{
		String classPath = "bin/math/CalculatorNonStaticWithConstructor.class";
		List<CollectorInfo> collectorInfo = new ArrayList<>();
		
		ClassMethodInfo loopMethod = new ClassMethodInfo.ClassMethodInfoBuilder()
				.testMethodSignature("executionFlow.ExecutionFlowTest()")
				.classPath(classPath)
				.methodName("loop")
				.methodSignature("math.CalculatorNonStaticWithConstructor")
				.build();
		
		var constTypes = new Class<?>[] {int.class};
		ClassConstructorInfo loopCons = new ClassConstructorInfo(constTypes, 2);
		
		collectorInfo.add(new CollectorInfo(loopMethod,loopCons));
		
		ExecutionFlow ef = new ExecutionFlow(collectorInfo);
		
		// Expected result
		Map<String, List<Integer>> expectedClassPaths = new HashMap<>();
		expectedClassPaths.put("math.CalculatorNonStaticWithConstructor.loop()", Arrays.asList(27,29,30,29,30,29));
		
		// Results obtained
		ef.execute().export();
		Map<String, List<Integer>> classPathsObtained = ef.execute().getClassPaths();
		
		assertEquals(expectedClassPaths, classPathsObtained);
	}
	*/
}
