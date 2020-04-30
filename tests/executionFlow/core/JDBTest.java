package executionFlow.core;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

import executionFlow.info.ClassConstructorInfo;
import executionFlow.info.ClassMethodInfo;
import executionFlow.info.CollectorInfo;
import executionFlow.runtime.SkipCollection;


/**
 * Tests test path computation via debugging analysis.
 */
@SkipCollection
public class JDBTest 
{
	@Test
	public void testStaticMethods() throws Throwable 
	{
		String classPath = new File("bin/math/Calculator.class").getAbsolutePath();
		List<List<Integer>> testPath_sum = new ArrayList<>();
		List<List<Integer>> testPath_loop = new ArrayList<>();
		
		ClassMethodInfo sumMethod = new ClassMethodInfo.ClassMethodInfoBuilder()
				.testMethodSignature("executionFlow.core.ExecutionFlowTest.callSum()")
				.classPath(classPath)
				.methodName("sum")
				.methodSignature("math.Calculator")
				.returnType(int.class)
				.parameterTypes(new Class<?>[] {int.class, int.class})
				.args(2,3)
				.invocationLine(26)
				.build();
		
		JDB md = new JDB(29, false);
		testPath_sum = md.getTestPaths(sumMethod);
		
		ClassMethodInfo loopMethod = new ClassMethodInfo.ClassMethodInfoBuilder()
				.testMethodSignature("executionFlow.core.ExecutionFlowTest.callLoop()")
				.classPath(classPath)
				.methodName("loop")
				.methodSignature("math.Calculator")
				.invocationLine(34)
				.build();
		
		md = new JDB(36, false);
		testPath_loop = md.getTestPaths(loopMethod);
		
		assertEquals(Arrays.asList(8, 10, 11, 10, 11, 10, 11, 10, 14), testPath_sum.get(0));
		
		// Note: test path ends with 24 because JDB always considers the last line as an executed line
		assertEquals(Arrays.asList(19, 21, 22, 21, 22, 21, 22, 21, 24), testPath_loop.get(0));
		
	}
	
	@Test
	public void testNonStaticMethods() throws Throwable 
	{
		String classPath = new File("bin/math/CalculatorNonStatic.class").getAbsolutePath();
		List<List<Integer>> testPath_sum = new ArrayList<>();
		
		ClassMethodInfo sumMethod = new ClassMethodInfo.ClassMethodInfoBuilder()
				.testMethodSignature("executionFlow.core.ExecutionFlowTest.callSum()")
				.classPath(classPath)
				.methodName("sum")
				.methodSignature("math.CalculatorNonStatic")
				.returnType(int.class)
				.parameterTypes(new Class<?>[] {int.class, int.class})
				.args(2,3)
				.invocationLine(27)
				.build();
		
		JDB md = new JDB(29, false);
		testPath_sum = md.getTestPaths(sumMethod);
		
		assertEquals(Arrays.asList(8, 10, 11, 10, 11, 10, 11, 10, 14), testPath_sum.get(0));
	}
	
	@Test
	public void testNonStaticMethodsWithConstructor() throws Throwable 
	{
		String classPath = new File("bin/math/CalculatorNonStaticWithConstructor.class").getAbsolutePath();
		List<CollectorInfo> collectorInfo = new ArrayList<>();
		List<List<Integer>> testPath_loop = new ArrayList<>();
		
		ClassMethodInfo sumMethod = new ClassMethodInfo.ClassMethodInfoBuilder()
				.testMethodSignature("executionFlow.core.ExecutionFlowTest.callSum()")
				.classPath(classPath)
				.methodName("sum")
				.methodSignature("math.CalculatorNonStaticWithConstructor")
				.invocationLine(28)
				.build();
		
		var constTypes = new Class<?>[] {int.class};
		ClassConstructorInfo loopCons = new ClassConstructorInfo(constTypes, 2);
		
		collectorInfo.add(new CollectorInfo(sumMethod,loopCons));
		
		JDB md = new JDB(29, false);
		testPath_loop = md.getTestPaths(sumMethod);
		
		assertEquals(Arrays.asList(16,18,19,18,19,18,19,18,22), testPath_loop.get(0));
	}
}
