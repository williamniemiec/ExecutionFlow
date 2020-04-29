package executionFlow.core;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import executionFlow.core.CheapCoverage;
import executionFlow.info.ClassConstructorInfo;
import executionFlow.info.ClassMethodInfo;
import executionFlow.info.CollectorInfo;
import executionFlow.runtime.SkipCollection;


/**
 * Tests test path computation via bytecode analysis.
 */
@SkipCollection
public class CheapCoverageTest 
{
	@Test
	public void testStaticMethods() throws Throwable 
	{
		String classPath = "bin/math/Calculator.class";
		
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
		
		CheapCoverage.loadClass(classPath);
		List<Integer> testPath_sum = CheapCoverage.getTestPath(sumMethod, null);
		List<Integer> testPath_loop = CheapCoverage.getTestPath(loopMethod, null);
		
		assertEquals(testPath_sum, Arrays.asList(8, 10, 11, 10, 11, 10, 11, 10, 14));
		assertEquals(testPath_loop, Arrays.asList(19, 21, 22, 21, 22, 21, 22, 21));
	}
	
	
	@Test
	public void testNonStaticMethods() throws Throwable 
	{
		String classPath = "bin/math/CalculatorNonStatic.class";
		
		ClassMethodInfo sumMethod = new ClassMethodInfo.ClassMethodInfoBuilder()
				.testMethodSignature("executionFlow.ExecutionFlowTest()")
				.classPath(classPath)
				.methodName("sum")
				.methodSignature("math.CalculatorNonStatic")
				.returnType(int.class)
				.parameterTypes(new Class<?>[] {int.class, int.class})
				.args(2,3)
				.build();
		
		CheapCoverage.loadClass(classPath);
		List<Integer> testPath_sum = CheapCoverage.getTestPath(sumMethod, null);
		
		assertEquals(testPath_sum, Arrays.asList(8, 10, 11, 10, 11, 10, 11, 10, 14));
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
		
		CheapCoverage.loadClass(classPath);
		List<Integer> testPath_loop = CheapCoverage.getTestPath(loopMethod, loopCons);
		
		assertEquals(testPath_loop, Arrays.asList(27,29,30,29,30,29));
	}
}
