package executionFlow;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import executionFlow.core.file.FileManager;
import executionFlow.core.file.InvokerManager;
import executionFlow.core.file.ParserType;
import executionFlow.core.file.parser.factory.PreTestMethodFileParserFactory;
import executionFlow.info.CollectorInfo;
import executionFlow.info.ConstructorInvokerInfo;
import executionFlow.info.MethodInvokerInfo;
import executionFlow.info.SignaturesInfo;
import executionFlow.runtime.SkipCollection;


/**
 * Tests test path computation from constructors through 
 * {@link ConstructorExecutionFlow} class.
 */
@SkipCollection
public class ConstructorExecutionFlowTest 
{
	private FileManager testMethodFileManager;
	private InvokerManager testMethodManager;
	
	
	/**
	 * Tests constructor used by {@link examples.controlFlow.ControlFlowTest}
	 * test.
	 * 
	 * @throws		IOException If an error occurs during file parsing
	 * @throws		ClassNotFoundException If class {@link FileManager} is not
	 * found
	 * 
	 * @apiNote		{@link examples.controlFlow.ControlFlowTest} uses only one
	 * constructor, so it is possible choose any test method that uses the
	 * constructor
	 */
	@Test
	public void controlFlowTest() throws IOException, ClassNotFoundException
	{
		List<List<Integer>> testPaths;
		Map<String, CollectorInfo> constructorCollector = new LinkedHashMap<>();
		Path testSrcPath = Path.of("examples/examples/controlFlow/ControlFlowTest.java");
		Path testClassPath = Path.of("bin/examples/controlFlow/ControlFlowTest.class");
		Object[] paramValues = {};
		Class<?>[] paramTypes = {};
		String signature = "examples.controlFlow.TestClass_ControlFlow()";
		String testMethodSignature = "examples.controlFlow.ControlFlowTest.ifElseTest_earlyReturn()";
		String testClassPackage = "examples.controlFlow";
		String key = signature + Arrays.toString(paramValues);
		
		
		// Creates backup from original files
		testMethodManager = new InvokerManager(ParserType.PRE_TEST_METHOD, false);
		
		testMethodFileManager = new FileManager(
			testSrcPath,
			MethodInvokerInfo.getCompiledFileDirectory(testClassPath),
			testClassPackage,
			new PreTestMethodFileParserFactory(),
			"original_assert"
		);
		
		// Parses test method
		try {
			testMethodManager.parse(testMethodFileManager).compile(testMethodFileManager);			
		} catch (IOException e) {
			testMethodManager.restoreAll();
			testMethodManager.deleteBackup();
			throw e;
		}
		
		// Informations about test method
		MethodInvokerInfo testMethodInfo = new MethodInvokerInfo.MethodInvokerInfoBuilder()
			.classPath(testClassPath)
			.srcPath(testSrcPath)
			.methodSignature(testMethodSignature)
			.build();
		
		// Informations about constructor
		ConstructorInvokerInfo cii = new ConstructorInvokerInfo.ConstructorInvokerInfoBuilder()
			.classPath(Path.of("bin/examples/controlFlow/TestClass_ControlFlow.class"))
			.srcPath(Path.of("examples/examples/controlFlow/TestClass_ControlFlow.java"))
			.constructorSignature(signature)
			.parameterTypes(paramTypes)
			.args(paramValues)
			.invocationLine(18)
			.build();
		
		// Saves extracted data
		CollectorInfo ci = new CollectorInfo.CollectorInfoBuilder()
			.constructorInfo(cii)
			.testMethodInfo(testMethodInfo)
			.build();
		
		constructorCollector.put(key, ci);
		
		// Gets test paths of the collected constructors and export them
		ExecutionFlow ef = new ConstructorExecutionFlow(constructorCollector.values());
		testPaths = getTestPaths(ef.execute().getClassTestPaths(), testMethodSignature, signature);
		
		// Restore original files
		ExecutionFlow.testMethodManager.restoreAll();
		ExecutionFlow.testMethodManager.deleteBackup();
		
		ExecutionFlow.invokerManager.restoreAll();
		ExecutionFlow.invokerManager.deleteBackup();
		
		testMethodManager.restoreAll();
		testMethodManager.deleteBackup();
		
		assertEquals(Arrays.asList(), testPaths.get(0));
	}
	
	/**
	 * Tests constructor used by {@link examples.complexTests.ComplexTests}
	 * test.
	 * 
	 * @throws		IOException If an error occurs during file parsing
	 * @throws		ClassNotFoundException If class {@link FileManager} is not
	 * found
	 */
	@Test
	public void ComplexTests1() throws ClassNotFoundException, IOException
	{
		Map<String, Map<SignaturesInfo, List<Integer>>> testPaths;
		Map<String, CollectorInfo> constructorCollector = new LinkedHashMap<>();
		Path testSrcPath = Path.of("examples/examples/complexTests/ComplexTests.java");
		Path testClassPath = Path.of("bin/examples/complexTests/ComplexTests.class");
		Object[] paramValues = {};
		Class<?>[] paramTypes = {};
		String signature = "examples.complexTests.TestClass_ComplexTests(int)";
		String testMethodSignature = "examples.complexTests.ComplexTests.ifElseTest_earlyReturn()";
		String testClassPackage = "examples.complexTests";
		String key = signature + Arrays.toString(paramValues);
		
		
		// Creates backup from original files
		testMethodManager = new InvokerManager(ParserType.PRE_TEST_METHOD, false);
		
		testMethodFileManager = new FileManager(
			testSrcPath,
			MethodInvokerInfo.getCompiledFileDirectory(testClassPath),
			testClassPackage,
			new PreTestMethodFileParserFactory(),
			"original_assert"
		);
		
		// Parses test method
		try {
			testMethodManager.parse(testMethodFileManager).compile(testMethodFileManager);			
		} catch (IOException e) {
			testMethodManager.restoreAll();
			testMethodManager.deleteBackup();
			throw e;
		}
		
		// Informations about test method
		MethodInvokerInfo testMethodInfo = new MethodInvokerInfo.MethodInvokerInfoBuilder()
			.classPath(testClassPath)
			.srcPath(testSrcPath)
			.methodSignature(testMethodSignature)
			.build();
		
		// Informations about constructor
		ConstructorInvokerInfo cii = new ConstructorInvokerInfo.ConstructorInvokerInfoBuilder()
			.classPath(Path.of("bin/examples/complexTests/TestClass_ComplexTests.class"))
			.srcPath(Path.of("examples/examples/complexTests/TestClass_ComplexTests.java"))
			.constructorSignature(signature)
			.parameterTypes(paramTypes)
			.args(paramValues)
			.invocationLine(24)
			.build();
		
		// Saves extracted data
		CollectorInfo ci = new CollectorInfo.CollectorInfoBuilder()
			.constructorInfo(cii)
			.testMethodInfo(testMethodInfo)
			.build();
		
		constructorCollector.put(key, ci);
		
		// Gets test paths of the collected constructors and export them
		ExecutionFlow ef = new ConstructorExecutionFlow(constructorCollector.values());
		ef.execute().export();
		
		// Restore original files
		ExecutionFlow.testMethodManager.restoreAll();
		ExecutionFlow.testMethodManager.deleteBackup();
		
		ExecutionFlow.invokerManager.restoreAll();
		ExecutionFlow.invokerManager.deleteBackup();
		
		testMethodManager.restoreAll();
		testMethodManager.deleteBackup();
	}
	
	
	/**
	 * Gets all test paths obtained from {@link ExecutionFlow}. 
	 * 
	 * @param		testPaths Test paths obtained from ExecutionFlow 
	 * @param		testMethodSignature Signature of the test method
	 * @param		constructorSignature Constructor signature
	 * 
	 * @return		Collection of all test paths
	 */
	private List<List<Integer>> getTestPaths(Map<SignaturesInfo, List<List<Integer>>> testPaths, 
			String testMethodSignature, String constructorSignature)
	{
		SignaturesInfo si = new SignaturesInfo(constructorSignature, testMethodSignature);

		return testPaths.get(si);
	}
}
