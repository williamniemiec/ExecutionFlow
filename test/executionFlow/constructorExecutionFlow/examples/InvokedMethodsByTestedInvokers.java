package executionFlow.constructorExecutionFlow.examples;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import executionFlow.ConstructorExecutionFlow;
import executionFlow.ExecutionFlow;
import executionFlow.info.CollectorInfo;
import executionFlow.info.ConstructorInvokedInfo;
import executionFlow.info.MethodInvokedInfo;
import executionFlow.io.FileManager;
import executionFlow.io.FilesManager;
import executionFlow.io.ProcessorType;
import executionFlow.io.processor.factory.PreTestMethodFileProcessorFactory;
import executionFlow.runtime.SkipCollection;
import executionFlow.util.ConsoleOutput;


/**
 * Tests test path computation for the constructors of 
 * {@link examples.methodCalledByTestedInvokeds.InvokedMethodsByTestedInvoker_Class} class using 
 * {@link ConstructorExecutionFlow} class.
 */
@SkipCollection
public class InvokedMethodsByTestedInvokers 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static FileManager testMethodFileManager;
	private static FilesManager testMethodManager;
	private static final Path PATH_SRC_TEST_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath(), "examples/examples/invokedMethodsByTestedInvokers/InvokedMethodsByTestedInvoker_Test.java");
	private static final Path PATH_BIN_TEST_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath(), "bin/examples/invokedMethodsByTestedInvokers/InvokedMethodsByTestedInvoker_Test.class");
	private static final String PACKAGE_TEST_METHOD = "examples.invokedMethodsByTestedInvokers";
	
	
	//-------------------------------------------------------------------------
	//		Test preparers
	//-------------------------------------------------------------------------
	/**
	 * @throws		IOException If an error occurs during file parsing
	 * @throws		ClassNotFoundException If class {@link FileManager} was not
	 * found
	 */
	@BeforeClass
	public static void init() throws ClassNotFoundException, IOException
	{
		// Initializes ExecutionFlow
		ExecutionFlow.init();
		
		// Creates backup from original files
		testMethodManager = new FilesManager(ProcessorType.TEST_METHOD, false);
		
		testMethodFileManager = new FileManager(
			PATH_SRC_TEST_METHOD,
			MethodInvokedInfo.getCompiledFileDirectory(PATH_BIN_TEST_METHOD),
			PACKAGE_TEST_METHOD,
			new PreTestMethodFileProcessorFactory(),
			"original_pre_processing"
		);
		
		// Parses test method
		try {
			ConsoleOutput.showInfo("Pre-processing test method...");
			testMethodManager.parse(testMethodFileManager).compile(testMethodFileManager);
			ConsoleOutput.showInfo("Pre-processing completed");
		} catch (IOException e) {
			testMethodManager.restoreAll();
			testMethodManager.deleteBackup();
			throw e;
		}
	}
	
	@AfterClass
	public static void clean()
	{
		// Restore original files
		ExecutionFlow.testMethodManager.restoreAll();
		ExecutionFlow.testMethodManager.deleteBackup();
		
		ExecutionFlow.invokedManager.restoreAll();
		ExecutionFlow.invokedManager.deleteBackup();
		
		testMethodManager.restoreAll();
		testMethodManager.deleteBackup();
		
		ExecutionFlow.destroy();
	}
	
	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	/**
	 * Tests constructor used by {@link examples.methodCalledByTestedInvokeds.InvokedMethodsByTestedInvoker_Test#T()}
	 * test.
	 */
	@Test
	public void T()
	{
		List<List<Integer>> testPaths;
		Map<String, CollectorInfo> constructorCollector = new LinkedHashMap<>();
		Object[] paramValues = {false};
		Class<?>[] paramTypes = {boolean.class};
		String signature = "examples.invokedMethodsByTestedInvokers.InvokedMethodsByTestedInvoker_Class(boolean)";
		String testMethodSignature = "examples.invokedMethodsByTestedInvokers.InvokedMethodsByTestedInvoker_Test.T()"; 
		String key = signature + Arrays.toString(paramValues);
		
		
		// Informations about test method
		MethodInvokedInfo testMethodInfo = new MethodInvokedInfo.MethodInvokedInfoBuilder()
			.binPath(PATH_BIN_TEST_METHOD)
			.srcPath(PATH_SRC_TEST_METHOD)
			.methodSignature(testMethodSignature)
			.build();
		
		// Informations about constructor
		ConstructorInvokedInfo cii = new ConstructorInvokedInfo.ConstructorInvokerInfoBuilder()
			.classPath(Path.of("bin/examples/invokedMethodsByTestedInvokers/InvokedMethodsByTestedInvoker_Class.class"))
			.srcPath(Path.of("examples/examples/invokedMethodsByTestedInvokers/InvokedMethodsByTestedInvoker_Class.java"))
			.constructorSignature(signature)
			.parameterTypes(paramTypes)
			.args(paramValues)
			.invocationLine(19)
			.build();
		
		// Saves extracted data
		CollectorInfo ci = new CollectorInfo.CollectorInfoBuilder()
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
	public void T2()
	{
		List<List<Integer>> testPaths;
		Map<String, CollectorInfo> constructorCollector = new LinkedHashMap<>();
		Object[] paramValues = {true};
		Class<?>[] paramTypes = {boolean.class};
		String signature = "examples.invokedMethodsByTestedInvokers.InvokedMethodsByTestedInvoker_Class(boolean)";
		String testMethodSignature = "examples.invokedMethodsByTestedInvokers.InvokedMethodsByTestedInvoker_Test.T2()"; 
		String key = signature + Arrays.toString(paramValues);
		
		
		// Informations about test method
		MethodInvokedInfo testMethodInfo = new MethodInvokedInfo.MethodInvokedInfoBuilder()
			.binPath(PATH_BIN_TEST_METHOD)
			.srcPath(PATH_SRC_TEST_METHOD)
			.methodSignature(testMethodSignature)
			.build();
		
		// Informations about constructor
		ConstructorInvokedInfo cii = new ConstructorInvokedInfo.ConstructorInvokerInfoBuilder()
			.classPath(Path.of("bin/examples/invokedMethodsByTestedInvokers/InvokedMethodsByTestedInvoker_Class.class"))
			.srcPath(Path.of("examples/examples/invokedMethodsByTestedInvokers/InvokedMethodsByTestedInvoker_Class.java"))
			.constructorSignature(signature)
			.parameterTypes(paramTypes)
			.args(paramValues)
			.invocationLine(32)
			.build();
		
		// Saves extracted data
		CollectorInfo ci = new CollectorInfo.CollectorInfoBuilder()
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