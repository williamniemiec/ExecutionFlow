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
import executionFlow.info.InvokedInfo;
import executionFlow.io.FileManager;
import executionFlow.runtime.SkipCollection;
import executionFlow.util.Logger;


/**
 * Tests test path computation for the constructors of 
 * {@link examples.methodCalledByTestedInvoked.InvokedMethodsByTestedInvoked_Class} class using 
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
					"examples/examples/methodCalledByTestedInvoked/MethodCalledByTestedInvoked_Test.java");
	private static final Path PATH_BIN_TEST_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath().toString(), 
					"bin/examples/methodCalledByTestedInvoked/MethodCalledByTestedInvoked_Test.class");
	private static final String PACKAGE_TEST_METHOD = "examples.methodCalledByTestedInvoked";
	
	
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
	 * Tests constructor used by {@link examples.methodCalledByTestedInvoked.InvokedMethodsByTestedInvoker_Test#T()}
	 * test.
	 */
	@Test
	public void T() throws ClassNotFoundException, IOException
	{
		List<List<Integer>> testPaths;
		Map<String, CollectorInfo> constructorCollector = new LinkedHashMap<>();
		Object[] paramValues = {false};
		Class<?>[] paramTypes = {boolean.class};
		String signature = "examples.methodCalledByTestedInvoked.MethodCalledByTestedInvoked_Class(boolean)";
		String testMethodSignature = "examples.methodCalledByTestedInvoked.MethodCalledByTestedInvoked_Test.T()"; 
		String key = signature + Arrays.toString(paramValues);
		
		
		init("examples.methodCalledByTestedInvoked.MethodCalledByTestedInvoked_Test", testMethodSignature);
		
		// Informations about test method
		InvokedInfo testMethodInfo = new InvokedInfo.Builder()
			.binPath(PATH_BIN_TEST_METHOD)
			.srcPath(PATH_SRC_TEST_METHOD)
			.invokedSignature(testMethodSignature)
			.build();
		
		// Informations about constructor
		InvokedInfo cii = new InvokedInfo.Builder()
			.binPath(Path.of("bin/examples/methodCalledByTestedInvoked/MethodCalledByTestedInvoked_Class.class"))
			.srcPath(Path.of("examples/examples/methodCalledByTestedInvoked/MethodCalledByTestedInvoked_Class.java"))
			.invokedSignature(signature)
			.isConstructor(true)
			.parameterTypes(paramTypes)
			.args(paramValues)
			.invocationLine(19)
			.build();
		
		// Saves extracted data
		CollectorInfo ci = new CollectorInfo(cii, testMethodInfo);
		
		constructorCollector.put(key, ci);
		
		testPaths = computeTestPath(constructorCollector.values(), testMethodSignature, signature);
		
		assertEquals(Arrays.asList(4), testPaths.get(0));
	}
	
	/**
	 * Tests constructor used by {@link examples.methodCalledByTestedInvoked.InvokedMethodsByTestedInvoker_Test#T2()}
	 * test.
	 */
	@Test
	public void T2() throws ClassNotFoundException, IOException
	{
		List<List<Integer>> testPaths;
		Map<String, CollectorInfo> constructorCollector = new LinkedHashMap<>();
		Object[] paramValues = {true};
		Class<?>[] paramTypes = {boolean.class};
		String signature = "examples.methodCalledByTestedInvoked.MethodCalledByTestedInvoked_Class(boolean)";
		String testMethodSignature = "examples.methodCalledByTestedInvoked.MethodCalledByTestedInvoked_Test.T2()"; 
		String key = signature + Arrays.toString(paramValues);
		
		
		init("examples.methodCalledByTestedInvoked.MethodCalledByTestedInvoked_Test", testMethodSignature);
		
		// Informations about test method
		InvokedInfo testMethodInfo = new InvokedInfo.Builder()
			.binPath(PATH_BIN_TEST_METHOD)
			.srcPath(PATH_SRC_TEST_METHOD)
			.invokedSignature(testMethodSignature)
			.build();
		
		// Informations about constructor
		InvokedInfo cii = new InvokedInfo.Builder()
			.binPath(Path.of("bin/examples/methodCalledByTestedInvoked/MethodCalledByTestedInvoked_Class.class"))
			.srcPath(Path.of("examples/examples/methodCalledByTestedInvoked/MethodCalledByTestedInvoked_Class.java"))
			.invokedSignature(signature)
			.isConstructor(true)
			.isConstructor(true)
			.parameterTypes(paramTypes)
			.args(paramValues)
			.invocationLine(32)
			.build();
		
		// Saves extracted data
		CollectorInfo ci = new CollectorInfo(cii, testMethodInfo);
		
		constructorCollector.put(key, ci);
		
		testPaths = computeTestPath(constructorCollector.values(), testMethodSignature, signature);
		
		assertEquals(Arrays.asList(4,5,6), testPaths.get(0));
	}
}