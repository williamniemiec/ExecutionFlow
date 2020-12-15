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
import executionFlow.info.InvokedInfo;
import executionFlow.io.FileManager;
import executionFlow.runtime.SkipCollection;


/**
 * Tests test path computation for the constructors of 
 * {@link examples.chainedCalls.ChainedCalls} class using 
 * {@link ConstructorExecutionFlow} class.
 */
@SkipCollection
public class ChainedCalls extends ConstructorExecutionFlowTest
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final Path PATH_SRC_TEST_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/chainedCalls/ChainedCalls.java");
	private static final Path PATH_BIN_TEST_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/chainedCalls/ChainedCalls.class");
	private static final String PACKAGE_TEST_METHOD = "examples.chainedCalls";
	
	
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
	 * Tests constructor used by {@link examples.chainedCalls.ChainedCalls()}
	 * test.
	 */
	@Test
	public void testChainedMethods() throws ClassNotFoundException, IOException
	{
		List<List<Integer>> testPaths;
		Map<String, CollectorInfo> constructorCollector = new LinkedHashMap<>();
		Object[] paramValues = {};
		Class<?>[] paramTypes = {};
		String signature = "examples.chainedCalls.Calculator()";
		String testMethodSignature = "examples.chainedCalls.ChainedCalls.testChainedMethods()"; 
		String key = signature + Arrays.toString(paramValues);
		
		
		init("examples.chainedCalls.ChainedCalls", testMethodSignature);
		
		// Informations about test method
		InvokedInfo testMethodInfo = new InvokedInfo.Builder()
			.binPath(PATH_BIN_TEST_METHOD)
			.srcPath(PATH_SRC_TEST_METHOD)
			.invokedSignature(testMethodSignature)
			.build();
		
		// Informations about constructor
		InvokedInfo cii = new InvokedInfo.Builder()
			.binPath(Path.of("bin/examples/chainedCalls/Calculator.class"))
			.srcPath(Path.of("examples/examples/chainedCalls/Calculator.java"))
			.invokedSignature(signature)
			.isConstructor(true)
			.parameterTypes(paramTypes)
			.args(paramValues)
			.invocationLine(12)
			.build();
		
		// Saves extracted data
		CollectorInfo ci = new CollectorInfo(cii, testMethodInfo);
		
		constructorCollector.put(key, ci);
		
		testPaths = computeTestPath(constructorCollector.values(), testMethodSignature, signature);
		
		assertEquals(Arrays.asList(8), testPaths.get(0));
	}
}