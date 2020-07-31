package executionFlow.constructorExecutionFlow.examples;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import executionFlow.ConstructorExecutionFlow;
import executionFlow.ExecutionFlow;
import executionFlow.constructorExecutionFlow.ConstructorExecutionFlowTest;
import executionFlow.info.CollectorInfo;
import executionFlow.info.ConstructorInvokedInfo;
import executionFlow.info.MethodInvokedInfo;
import executionFlow.io.FileManager;
import executionFlow.runtime.SkipCollection;


/**
 * Tests test path computation for the constructors of 
 * {@link examples.complexTests.ComplexTests} class using 
 * {@link ConstructorExecutionFlow} class.
 */
@SkipCollection
public class ComplexTests extends ConstructorExecutionFlowTest
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final Path PATH_SRC_TEST_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/complexTests/ComplexTests.java");
	private static final Path PATH_BIN_TEST_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/complexTests/ComplexTests.class");
	private static final String PACKAGE_TEST_METHOD = "examples.complexTests";
	
	
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
	 * Tests constructor used by {@link examples.complexTests.ComplexTests#testForConstructor()}
	 * test.
	 */
	@Test
	public void testForConstructorAndMethod() throws ClassNotFoundException, IOException
	{
		List<List<Integer>> testPaths;
		Map<String, CollectorInfo> constructorCollector = new LinkedHashMap<>();
		Object[] paramValues = {1, 2, 3, 4};
		Class<?>[] paramTypes = {int.class, int.class, int.class, int.class};
		String signature = "examples.complexTests.TestClass_ComplexTests(int)";
		String testMethodSignature = "examples.complexTests.ComplexTests.testForConstructorAndMethod()"; 
		String key = signature + Arrays.toString(paramValues);
		
		
		init("examples.complexTests.ComplexTests", testMethodSignature);
		
		// Informations about test method
		MethodInvokedInfo testMethodInfo = new MethodInvokedInfo.Builder()
			.binPath(PATH_BIN_TEST_METHOD)
			.srcPath(PATH_SRC_TEST_METHOD)
			.methodSignature(testMethodSignature)
			.build();
		
		// Informations about constructor
		ConstructorInvokedInfo cii = new ConstructorInvokedInfo.Builder()
			.binPath(Path.of("bin/examples/complexTests/TestClass_ComplexTests.class"))
			.srcPath(Path.of("examples/examples/complexTests/TestClass_ComplexTests.java"))
			.constructorSignature(signature)
			.parameterTypes(paramTypes)
			.args(paramValues)
			.invocationLine(18)
			.build();
		
		// Saves extracted data
		CollectorInfo ci = new CollectorInfo.Builder()
			.constructorInfo(cii)
			.testMethodInfo(testMethodInfo)
			.build();
		
		constructorCollector.put(key, ci);
		
		// Gets test paths of the collected constructors and export them
		ExecutionFlow ef = new ConstructorExecutionFlow(constructorCollector.values(), false);
		testPaths = ef.execute().getTestPaths(testMethodSignature, signature);
		
		assertEquals(
			Arrays.asList(
				Arrays.asList(33),
				Arrays.asList(33), 
				Arrays.asList(33), 
				Arrays.asList(33)
			), 
			testPaths
		);
	}
	
	/**
	 * Tests first constructor used by 
	 * {@link examples.complexTests.ComplexTests#moreOneConstructor()} test.
	 */
	@Test
	public void moreOneConstructor_noParams() throws ClassNotFoundException, IOException
	{
		List<List<Integer>> testPaths;
		Map<String, CollectorInfo> constructorCollector = new LinkedHashMap<>();
		Object[] paramValues = {};
		Class<?>[] paramTypes = {};
		String signature = "examples.complexTests.TestClass_ComplexTests()";
		String testMethodSignature = "examples.complexTests.ComplexTests.moreOneConstructor()"; 
		String key = signature + Arrays.toString(paramValues);
		
		
		init("examples.complexTests.ComplexTests", testMethodSignature);
		
		// Informations about test method
		MethodInvokedInfo testMethodInfo = new MethodInvokedInfo.Builder()
			.binPath(PATH_BIN_TEST_METHOD)
			.srcPath(PATH_SRC_TEST_METHOD)
			.methodSignature(testMethodSignature)
			.build();
		
		// Informations about constructor
		ConstructorInvokedInfo cii = new ConstructorInvokedInfo.Builder()
			.binPath(Path.of("bin/examples/complexTests/TestClass_ComplexTests.class"))
			.srcPath(Path.of("examples/examples/complexTests/TestClass_ComplexTests.java"))
			.constructorSignature(signature)
			.parameterTypes(paramTypes)
			.args(paramValues)
			.invocationLine(31)
			.build();
		
		// Saves extracted data
		CollectorInfo ci = new CollectorInfo.Builder()
			.constructorInfo(cii)
			.testMethodInfo(testMethodInfo)
			.build();
		
		constructorCollector.put(key, ci);
		
		// Gets test paths of the collected constructors and export them
		ExecutionFlow ef = new ConstructorExecutionFlow(constructorCollector.values(), false);
		testPaths = ef.execute().getTestPaths(testMethodSignature, signature);
		
		assertEquals(
			Arrays.asList(Arrays.asList(38)), 
			testPaths
		);
	}
	
	/**
	 * Tests second constructor used by 
	 * {@link examples.complexTests.ComplexTests#moreOneConstructor()} test.
	 */
	@Test
	public void moreOneConstructor_booleanParam() throws ClassNotFoundException, IOException
	{
		List<List<Integer>> testPaths;
		Map<String, CollectorInfo> constructorCollector = new LinkedHashMap<>();
		Object[] paramValues = {true};
		Class<?>[] paramTypes = {boolean.class};
		String signature = "examples.complexTests.TestClass_ComplexTests(boolean)";
		String testMethodSignature = "examples.complexTests.ComplexTests.moreOneConstructor()"; 
		String key = signature + Arrays.toString(paramValues);
		
		
		init("examples.complexTests.ComplexTests", testMethodSignature);
		
		// Informations about test method
		MethodInvokedInfo testMethodInfo = new MethodInvokedInfo.Builder()
			.binPath(PATH_BIN_TEST_METHOD)
			.srcPath(PATH_SRC_TEST_METHOD)
			.methodSignature(testMethodSignature)
			.build();
		
		// Informations about constructor
		ConstructorInvokedInfo cii = new ConstructorInvokedInfo.Builder()
			.binPath(Path.of("bin/examples/complexTests/TestClass_ComplexTests.class"))
			.srcPath(Path.of("examples/examples/complexTests/TestClass_ComplexTests.java"))
			.constructorSignature(signature)
			.parameterTypes(paramTypes)
			.args(paramValues)
			.invocationLine(32)
			.build();
		
		// Saves extracted data
		CollectorInfo ci = new CollectorInfo.Builder()
			.constructorInfo(cii)
			.testMethodInfo(testMethodInfo)
			.build();
		
		constructorCollector.put(key, ci);
		
		// Gets test paths of the collected constructors and export them
		ExecutionFlow ef = new ConstructorExecutionFlow(constructorCollector.values(), false);
		testPaths = ef.execute().getTestPaths(testMethodSignature, signature);
		
		assertEquals(
			Arrays.asList(Arrays.asList(27, 28)), 
			testPaths
		);
	}
}
