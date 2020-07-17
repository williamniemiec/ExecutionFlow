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
 * {@link examples.builderPattern.BuilderPatternTest} test using 
 * {@link MethodExecutionFlow} class.
 */
@SkipCollection
public class BuilderPatternTest 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static FileManager testMethodFileManager;
	private static FilesManager testMethodManager;
	private static final Path PATH_BIN_TEST_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath(), "bin/examples/builderPattern/BuilderPatternTest.class");
	private static final Path PATH_SRC_TEST_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath(), "examples/examples/builderPattern/BuilderPatternTest.java");
	private static final String PACKAGE_TEST_METHOD = "examples.builderPattern";
	private static final Path PATH_BIN_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath(), "bin/examples/builderPattern/Person.class");
	private static final Path PATH_SRC_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath(), "examples/examples/builderPattern/Person.java");
	
	
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
	 * Tests {@link examples.builderPattern.Person#PersonBuilder.firstName(String)}
	 * method.
	 */
	@Test
	public void firstName() throws Throwable 
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
		int invocationLine = 15;
		
		// Defines which methods will be collected
		String testMethodSignature = "examples.builderPattern.BuilderPatternTest.testBuilderPattern()";
		String methodSignature = "examples.builderPattern.Person$PersonBuilder.firstName(String)";
		
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
				.methodName("firstName")
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
				Arrays.asList(42,43)
			),
			testPaths
		);
	}
	
	/**
	 * Tests {@link examples.builderPattern.Person#PersonBuilder.lastName(String)}
	 * method.
	 */
	@Test
	public void lastName() throws Throwable 
	{
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();

		List<List<Integer>> testPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		int invocationLine = 16;
		
		// Defines which methods will be collected
		String testMethodSignature = "examples.builderPattern.BuilderPatternTest.testBuilderPattern()";
		String methodSignature = "examples.builderPattern.Person$PersonBuilder.lastName(String)";
		
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
				.methodName("lastName")
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
				Arrays.asList(48,49)
			),
			testPaths
		);
	}
	
	/**
	 * Tests {@link examples.builderPattern.Person#PersonBuilder.age(int)}
	 * method.
	 */
	@Test
	public void age() throws Throwable 
	{
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();

		List<List<Integer>> testPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		int invocationLine = 17;
		
		// Defines which methods will be collected
		String testMethodSignature = "examples.builderPattern.BuilderPatternTest.testBuilderPattern()";
		String methodSignature = "examples.builderPattern.Person$PersonBuilder.age(int)";
		
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
				.methodName("age")
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
				Arrays.asList(60,61)
			),
			testPaths
		);
	}
	
	/**
	 * Tests {@link examples.builderPattern.Person#PersonBuilder.email(String)}
	 * method.
	 */
	@Test
	public void email() throws Throwable 
	{
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();

		List<List<Integer>> testPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		int invocationLine = 18;
		
		// Defines which methods will be collected
		String testMethodSignature = "examples.builderPattern.BuilderPatternTest.testBuilderPattern()";
		String methodSignature = "examples.builderPattern.Person$PersonBuilder.email(String)";
		
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
				.methodName("email")
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
				Arrays.asList(54,55)
			),
			testPaths
		);
	}
	
	/**
	 * Tests {@link examples.builderPattern.Person#PersonBuilder.build()}
	 * method.
	 */
	@Test
	public void build() throws Throwable 
	{
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();

		List<List<Integer>> testPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		int invocationLine = 19;
		
		// Defines which methods will be collected
		String testMethodSignature = "examples.builderPattern.BuilderPatternTest.testBuilderPattern()";
		String methodSignature = "examples.builderPattern.Person$PersonBuilder.build()";
		
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
				.methodName("build")
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
				Arrays.asList(66)
			),
			testPaths
		);
	}
	
	/**
	 * Tests {@link examples.builderPattern.Person#print()} method.
	 */
	@Test
	public void print() throws Throwable 
	{
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();

		List<List<Integer>> testPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		int invocationLine = 21;
		
		// Defines which methods will be collected
		String testMethodSignature = "examples.builderPattern.BuilderPatternTest.testBuilderPattern()";
		String methodSignature = "examples.builderPattern.Person.print()";
		
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
				.methodName("print")
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
				Arrays.asList(76,77,78,79)
			),
			testPaths
		);
	}
}