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
 * {@link examples.methodCalledByTestedInvokeds.InvokedMethodsByTestedInvoked_Class} class using 
 * {@link ConstructorExecutionFlow} class.
 */
@SkipCollection
public class InvokedMethodsByTestedInvoked extends ConstructorExecutionFlowTest
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final Path PATH_SRC_TEST_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath().toString(), 
					"examples/examples/methodCalledByTestedInvokeds/MethodCalledByTestedInvoked_Test.java");
	private static final Path PATH_BIN_TEST_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath().toString(), 
					"bin/examples/methodCalledByTestedInvokeds/MethodCalledByTestedInvoked_Test.class");
	private static final String PACKAGE_TEST_METHOD = "examples.methodCalledByTestedInvokeds";
	
	
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
	 * Tests constructor used by {@link examples.methodCalledByTestedInvokeds.InvokedMethodsByTestedInvoker_Test#T()}
	 * test.
	 */
	@Test
	public void T() throws ClassNotFoundException, IOException
	{
		List<List<Integer>> testPaths;
		Map<String, CollectorInfo> constructorCollector = new LinkedHashMap<>();
		Object[] paramValues = {false};
		Class<?>[] paramTypes = {boolean.class};
		String signature = "examples.methodCalledByTestedInvokeds.MethodCalledByTestedInvoked_Class(boolean)";
		String testMethodSignature = "examples.methodCalledByTestedInvokeds.MethodCalledByTestedInvoked_Test.T()"; 
		String key = signature + Arrays.toString(paramValues);
		
		
		init("examples.methodCalledByTestedInvokeds.MethodCalledByTestedInvoked_Test", testMethodSignature);
		
		// Informations about test method
		MethodInvokedInfo testMethodInfo = new MethodInvokedInfo.Builder()
			.binPath(PATH_BIN_TEST_METHOD)
			.srcPath(PATH_SRC_TEST_METHOD)
			.methodSignature(testMethodSignature)
			.build();
		
		// Informations about constructor
		ConstructorInvokedInfo cii = new ConstructorInvokedInfo.Builder()
			.binPath(Path.of("bin/examples/methodCalledByTestedInvokeds/MethodCalledByTestedInvoked_Class.class"))
			.srcPath(Path.of("examples/examples/methodCalledByTestedInvokeds/MethodCalledByTestedInvoked_Class.java"))
			.constructorSignature(signature)
			.parameterTypes(paramTypes)
			.args(paramValues)
			.invocationLine(19)
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
		
		assertEquals(Arrays.asList(8), testPaths.get(0));
	}
	
	/**
	 * Tests constructor used by {@link examples.methodCalledByTestedInvokeds.InvokedMethodsByTestedInvoker_Test#T2()}
	 * test.
	 */
	@Test
	public void T2() throws ClassNotFoundException, IOException
	{
		List<List<Integer>> testPaths;
		Map<String, CollectorInfo> constructorCollector = new LinkedHashMap<>();
		Object[] paramValues = {true};
		Class<?>[] paramTypes = {boolean.class};
		String signature = "examples.methodCalledByTestedInvokeds.MethodCalledByTestedInvoked_Class(boolean)";
		String testMethodSignature = "examples.methodCalledByTestedInvokeds.MethodCalledByTestedInvoked_Test.T2()"; 
		String key = signature + Arrays.toString(paramValues);
		
		
		init("examples.methodCalledByTestedInvokeds.MethodCalledByTestedInvoked_Test", testMethodSignature);
		
		// Informations about test method
		MethodInvokedInfo testMethodInfo = new MethodInvokedInfo.Builder()
			.binPath(PATH_BIN_TEST_METHOD)
			.srcPath(PATH_SRC_TEST_METHOD)
			.methodSignature(testMethodSignature)
			.build();
		
		// Informations about constructor
		ConstructorInvokedInfo cii = new ConstructorInvokedInfo.Builder()
			.binPath(Path.of("bin/examples/methodCalledByTestedInvokeds/MethodCalledByTestedInvoked_Class.class"))
			.srcPath(Path.of("examples/examples/methodCalledByTestedInvokeds/MethodCalledByTestedInvoked_Class.java"))
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
		
		assertEquals(Arrays.asList(8,9,10), testPaths.get(0));
	}
}