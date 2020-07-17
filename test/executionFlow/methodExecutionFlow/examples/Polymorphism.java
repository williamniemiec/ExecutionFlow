package executionFlow.methodExecutionFlow.examples;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import executionFlow.ExecutionFlow;
import executionFlow.MethodExecutionFlow;
import executionFlow.info.CollectorInfo;
import executionFlow.info.MethodInvokedInfo;
import executionFlow.io.FileManager;
import executionFlow.io.FilesManager;
import executionFlow.io.ProcessorType;
import executionFlow.io.processor.factory.PreTestMethodFileProcessorFactory;
import executionFlow.runtime.SkipCollection;
import executionFlow.util.ConsoleOutput;


/**
 * Tests test path computation for the tested methods of 
 * {@link examples.polymorphism.PolymorphismTest} test using 
 * {@link MethodExecutionFlow} class.
 */
@SkipCollection
public class Polymorphism 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static FileManager testMethodFileManager;
	private static FilesManager testMethodManager;
	private static final Path PATH_BIN_TEST_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath(), "bin/examples/polymorphism/PolymorphismTest.class");
	private static final Path PATH_SRC_TEST_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath(), "examples/examples/polymorphism/PolymorphismTest.java");
	private static final String PACKAGE_TEST_METHOD = "examples.polymorphism";
	private static final Path PATH_BIN_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath(), "bin/examples/polymorphism/ClassInterface.class");
	private static final Path PATH_SRC_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath(), "examples/examples/polymorphism/ClassInterface.java");
	
	
	//-------------------------------------------------------------------------
	//		Test preparers
	//-------------------------------------------------------------------------
	/**
	 * @throws		IOException If an error occurs during file parsing
	 * @throws		ClassNotFoundException If class {@link FileManager} was not
	 * found
	 */
	@BeforeClass
	public static void init() throws IOException, ClassNotFoundException
	{
		// Initializes ExecutionFlow
		ExecutionFlow.destroy();
		ExecutionFlow.init();
				
		// Creates backup from original files
		testMethodManager = new FilesManager(ProcessorType.PRE_TEST_METHOD, false);
		
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
	
	/**
	 * Restores original files
	 */
	@AfterClass
	public static void restore()
	{
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
	 * Tests {@link examples.polymorphism.ClassInterface.testClassParam(ClassInterface)}
	 * method.
	 */
	@Test
	public void testClassParam() throws Throwable 
	{
		/**
		 * Stores information about collected methods.
		 * <ul>
		 * 	<li><b>Key:</b> Method invocation line</li>
		 * 	<li><b>Value:</b> List of methods invoked from this line</li>
		 * </ul>
		 */
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();

		List<List<Integer>> testPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		int invocationLine = 20;
		
		// Defines which methods will be collected
		String testMethodSignature = "examples.polymorphism.PolymorphismTest.testParam()";
		String methodSignature = "examples.polymorphism.ClassInterface.testClassParam(ClassInterface)";
		
		MethodInvokedInfo testMethodInfo = new MethodInvokedInfo.MethodInvokedInfoBuilder()
				.binPath(PATH_BIN_TEST_METHOD)
				.methodSignature(testMethodSignature)
				.srcPath(PATH_SRC_TEST_METHOD)
				.build();
		
		MethodInvokedInfo methodInfo = new MethodInvokedInfo.MethodInvokedInfoBuilder()
				.binPath(PATH_BIN_METHOD)
				.srcPath(PATH_SRC_METHOD)
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("testClassParam")
				.build();
		
		CollectorInfo ci = new CollectorInfo.CollectorInfoBuilder()
				.methodInfo(methodInfo)
				.testMethodInfo(testMethodInfo)
				.build();
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new MethodExecutionFlow(methodCollector, false);
		
		// Gets test path
		testPaths = ef.execute().getTestPaths(testMethodSignature, methodSignature);
		
		assertEquals(
			Arrays.asList(
				Arrays.asList(36)
			),
			testPaths
		);
	}
	
	/**
	 * Tests {@link examples.polymorphism.ClassInterface.interfaceMethod()}
	 * method.
	 */
	@Test
	public void interfaceMethod() throws Throwable 
	{
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();

		List<List<Integer>> testPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		int invocationLine = 33;
		
		// Defines which methods will be collected
		String testMethodSignature = "examples.polymorphism.PolymorphismTest.testInterface()";
		String methodSignature = "examples.polymorphism.ClassInterface.interfaceMethod()";
		
		MethodInvokedInfo testMethodInfo = new MethodInvokedInfo.MethodInvokedInfoBuilder()
				.binPath(PATH_BIN_TEST_METHOD)
				.methodSignature(testMethodSignature)
				.srcPath(PATH_SRC_TEST_METHOD)
				.build();
		
		MethodInvokedInfo methodInfo = new MethodInvokedInfo.MethodInvokedInfoBuilder()
				.binPath(PATH_BIN_METHOD)
				.srcPath(PATH_SRC_METHOD)
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("interfaceMethod")
				.build();
		
		CollectorInfo ci = new CollectorInfo.CollectorInfoBuilder()
				.methodInfo(methodInfo)
				.testMethodInfo(testMethodInfo)
				.build();
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		// Computes test path
		ExecutionFlow ef = new MethodExecutionFlow(methodCollector, false);
		
		// Gets test path
		testPaths = ef.execute().getTestPaths(testMethodSignature, methodSignature);
		
		assertEquals(
			Arrays.asList(
				Arrays.asList(30,31)
			),
			testPaths
		);
	}
}