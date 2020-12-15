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
 * {@link examples.controlFlow.ControlFlowTest} class using 
 * {@link ConstructorExecutionFlow} class.
 */
@SkipCollection
public class ControlFlowTest extends ConstructorExecutionFlowTest
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final Path PATH_SRC_TEST_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/controlFlow/ControlFlowTest.java");
	private static final Path PATH_BIN_TEST_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/controlFlow/ControlFlowTest.class");
	private static final String PACKAGE_TEST_METHOD = "examples.controlFlow";
	

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
	 * Tests constructor used by {@link examples.controlFlow.ControlFlowTest}
	 * test.
	 * 
	 * @apiNote		{@link examples.controlFlow.ControlFlowTest} uses only one
	 * constructor, so it is possible choose any test method that uses the
	 * constructor
	 */
	@Test
	public void controlFlowTest() throws ClassNotFoundException, IOException
	{
		List<List<Integer>> testPaths;
		Map<String, CollectorInfo> constructorCollector = new LinkedHashMap<>();
		Object[] paramValues = {};
		Class<?>[] paramTypes = {};
		String signature = "examples.controlFlow.TestClass_ControlFlow()";
		String testMethodSignature = "examples.controlFlow.ControlFlowTest.ifElseTest_earlyReturn()"; // First test method
		String key = signature + Arrays.toString(paramValues);
		
		
		init("examples.controlFlow.ControlFlowTest", testMethodSignature);
		
		// Informations about test method
		InvokedInfo testMethodInfo = new InvokedInfo.Builder()
			.binPath(PATH_BIN_TEST_METHOD)
			.srcPath(PATH_SRC_TEST_METHOD)
			.invokedSignature(testMethodSignature)
			.build();
		
		// Informations about constructor
		InvokedInfo cii = new InvokedInfo.Builder()
			.binPath(Path.of("bin/examples/controlFlow/TestClass_ControlFlow.class"))
			.srcPath(Path.of("examples/examples/controlFlow/TestClass_ControlFlow.java"))
			.invokedSignature(signature)
			.parameterTypes(paramTypes)
			.args(paramValues)
			.invocationLine(18)
			.build();
		
		// Saves extracted data
		CollectorInfo ci = new CollectorInfo(cii, testMethodInfo);
		
		constructorCollector.put(key, ci);
		
		// Gets test paths of the collected constructors and export them
		ExecutionFlow ef = new ConstructorExecutionFlow(processingManager, constructorCollector.values());
		testPaths = ef.execute().getTestPaths(testMethodSignature, signature);
		
		assertEquals(Arrays.asList(Arrays.asList()), testPaths);
	}
}
