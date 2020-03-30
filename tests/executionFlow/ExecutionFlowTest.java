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


public class ExecutionFlowTest 
{
	@Test
	public void testStaticMethods() throws Throwable 
	{
		String classPath = "bin/Math/Calculator.class";
		Map<String, Object[]> methods = new HashMap<>();
		
		methods.put("sum", Arrays.asList(2,3).toArray());
		methods.put("loop", null);
		
		List<ClassMethodInfo> classMethodInfo = new ArrayList<>();
		classMethodInfo.add(new ClassMethodInfo("sum", new Class<?>[] {int.class, int.class}, 2,3));
		classMethodInfo.add(new ClassMethodInfo("loop"));
		
		ClassConstructorInfo classConstructorInfo = null;
		
		ExecutionFlow ef = new ExecutionFlow(classPath, classMethodInfo, classConstructorInfo);
		
		// Expected result
		Map<String, List<Integer>> expectedClassPaths = new HashMap<>();
		expectedClassPaths.put("Math.Calculator.loop()", Arrays.asList(19, 21, 22, 21, 22, 21, 22, 21));
		expectedClassPaths.put("Math.Calculator.sum(int,int)", Arrays.asList(8, 10, 11, 10, 11, 10, 11, 10, 14));
		
		// Results obtained
		ef.execute().export();
		Map<String, List<Integer>> classPathsObtained = ef.execute().getClassPaths();
		
		assertEquals(expectedClassPaths, classPathsObtained);
	}
	
	@Test
	public void testNonStaticMethods() throws Throwable 
	{
		String classPath = "bin/Math/CalculatorNonStatic.class";
		Map<String, Object[]> methods = new HashMap<>();
				
		// ClassMethodInfo init
		methods.put("sum", Arrays.asList(2,3).toArray());
		List<ClassMethodInfo> classMethodInfo = new ArrayList<>();
		classMethodInfo.add(new ClassMethodInfo("sum", new Class<?>[] {int.class, int.class}, 2,3));
		
		//ClassMethodInfo isn't necessary (constructor default / empty)
		
		ExecutionFlow ef = new ExecutionFlow(classPath, classMethodInfo);
		
		// Expected result
		Map<String, List<Integer>> expectedClassPaths = new HashMap<>();
		expectedClassPaths.put("Math.CalculatorNonStatic.sum(int,int)", Arrays.asList(8, 10, 11, 10, 11, 10, 11, 10, 14));
		
		// Results obtained
		ef.execute().export();
		Map<String, List<Integer>> classPathsObtained = ef.execute().getClassPaths();
		
		assertEquals(expectedClassPaths, classPathsObtained);
	}
	
	@Test
	public void testNonStaticMethodsWithConstructor() throws Throwable 
	{
		String classPath = "bin/Math/CalculatorNonStaticWithConstructor.class";
		Map<String, Object[]> methods = new HashMap<>();
				
		// ClassMethodInfo init
		methods.put("loop", null);
		List<ClassMethodInfo> classMethodInfo = new ArrayList<>();
		classMethodInfo.add(new ClassMethodInfo("loop"));
		
		// ClassConstructorInfo init
		var constTypes = new Class<?>[] {int.class};
		ClassConstructorInfo classConstructorInfo = new ClassConstructorInfo(constTypes, 2);
		
		ExecutionFlow ef = new ExecutionFlow(classPath, classMethodInfo, classConstructorInfo);
		
		// Expected result
		Map<String, List<Integer>> expectedClassPaths = new HashMap<>();
		expectedClassPaths.put("Math.CalculatorNonStaticWithConstructor.loop()", Arrays.asList(27,29,30,29,30,29));
		
		// Results obtained
		ef.execute().export();
		Map<String, List<Integer>> classPathsObtained = ef.execute().getClassPaths();
		
		assertEquals(expectedClassPaths, classPathsObtained);
	}
}
