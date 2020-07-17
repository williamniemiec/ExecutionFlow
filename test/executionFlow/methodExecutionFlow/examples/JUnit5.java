package executionFlow.methodExecutionFlow.examples;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
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
 * Tests test path computation for the tested methods of the following tests
 * using {@link MethodExecutionFlow} class: 
 * <ul>
 * 	<li>{@link examples.junit5.TestAnnotation}</li> 
 * 	<li>{@link examples.junit5.ParameterizedTestAnnotation}</li>
 * 	<li>{@link examples.junit5.RepeatedTestAnnotation}</li>
 * </ul>
 */
@SkipCollection
public class JUnit5 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static FileManager testMethodFileManager;
	private static FilesManager testMethodManager;
	private static final String PACKAGE_TEST_METHOD = "examples.junit5";
	
	
	//-------------------------------------------------------------------------
	//		Test preparers
	//-------------------------------------------------------------------------
	/**
	 * Initializes a test method.
	 * 
	 * @param		pathBinTestMethod Test method compiled file path
	 * @param		pathSrcTestMethod Test method source file path
	 * @param		testMethodArgs Test method args (when it is a parameterized test)
	 * 
	 * @throws		IOException If an error occurs during file parsing
	 * @throws		ClassNotFoundException If class {@link FileManager} was not
	 * found
	 */
	private static void init(Path pathBinTestMethod, Path pathSrcTestMethod, Object... testMethodArgs) throws IOException, ClassNotFoundException
	{
		// Initializes ExecutionFlow
		ExecutionFlow.destroy();
		ExecutionFlow.init();
				
		// Creates backup from original files
		testMethodManager = new FilesManager(ProcessorType.PRE_TEST_METHOD, false);
		
		testMethodFileManager = new FileManager(
				pathSrcTestMethod,
			MethodInvokedInfo.getCompiledFileDirectory(pathBinTestMethod),
			PACKAGE_TEST_METHOD,
			new PreTestMethodFileProcessorFactory(testMethodArgs),
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
	@After
	public void restore()
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
	 * Tests {@link examples.junit5.ParameterizedTestAnnotation#test1()} test
	 * method with its first argument.
	 */
	@Test
	public void parameterizedTestAnnotation_test1_firstArg() throws Throwable 
	{
		init(Path.of(ExecutionFlow.getAppRootPath(), "bin/examples/junit5/ParameterizedTestAnnotation.class"),
			 Path.of(ExecutionFlow.getAppRootPath(), "examples/examples/junit5/ParameterizedTestAnnotation.java"),
			 -1);
		
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
		int invocationLine = 24;
		
		// Defines which methods will be collected
		String testMethodSignature = "examples.junit5.ParameterizedTestAnnotation.test1(int)";
		String methodSignature = "examples.others.auxClasses.AuxClass.factorial(int)";
		
		MethodInvokedInfo testMethodInfo = new MethodInvokedInfo.MethodInvokedInfoBuilder()
				.binPath(Path.of(ExecutionFlow.getAppRootPath(), "bin/examples/junit5/ParameterizedTestAnnotation.class"))
				.methodSignature(testMethodSignature)
				.srcPath(Path.of(ExecutionFlow.getAppRootPath(), "examples/examples/junit5/ParameterizedTestAnnotation.java"))
				.args(-1)
				.build();
		
		MethodInvokedInfo methodInfo = new MethodInvokedInfo.MethodInvokedInfoBuilder()
				.binPath(Path.of(ExecutionFlow.getAppRootPath(), "bin/examples/others/auxClasses/AuxClass.class"))
				.srcPath(Path.of(ExecutionFlow.getAppRootPath(), "examples/examples/others/auxClasses/AuxClass.java"))
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("factorial")
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
				Arrays.asList(91,93,97)
			),
			testPaths
		);
	}
	
	/**
	 * Tests {@link examples.junit5.ParameterizedTestAnnotation#test1()} test
	 * method with its second argument.
	 */
	@Test
	public void parameterizedTestAnnotation_test1_secondArg() throws Throwable 
	{
		init(Path.of(ExecutionFlow.getAppRootPath(), "bin/examples/junit5/ParameterizedTestAnnotation.class"),
			 Path.of(ExecutionFlow.getAppRootPath(), "examples/examples/junit5/ParameterizedTestAnnotation.java"),
			 0);
		
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();

		List<List<Integer>> testPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		int invocationLine = 24;
		
		// Defines which methods will be collected
		String testMethodSignature = "examples.junit5.ParameterizedTestAnnotation.test1(int)";
		String methodSignature = "examples.others.auxClasses.AuxClass.factorial(int)";
		
		MethodInvokedInfo testMethodInfo = new MethodInvokedInfo.MethodInvokedInfoBuilder()
				.binPath(Path.of(ExecutionFlow.getAppRootPath(), "bin/examples/junit5/ParameterizedTestAnnotation.class"))
				.methodSignature(testMethodSignature)
				.srcPath(Path.of(ExecutionFlow.getAppRootPath(), "examples/examples/junit5/ParameterizedTestAnnotation.java"))
				.args(0)
				.build();
		
		MethodInvokedInfo methodInfo = new MethodInvokedInfo.MethodInvokedInfoBuilder()
				.binPath(Path.of(ExecutionFlow.getAppRootPath(), "bin/examples/others/auxClasses/AuxClass.class"))
				.srcPath(Path.of(ExecutionFlow.getAppRootPath(), "examples/examples/others/auxClasses/AuxClass.java"))
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("factorial")
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
				Arrays.asList(91,93,97)
			),
			testPaths
		);
	}
	
	/**
	 * Tests {@link examples.junit5.ParameterizedTestAnnotation#test1()} test
	 * method with its third argument.
	 */
	@Test
	public void parameterizedTestAnnotation_test1_thirdArg() throws Throwable 
	{
		init(Path.of(ExecutionFlow.getAppRootPath(), "bin/examples/junit5/ParameterizedTestAnnotation.class"),
			 Path.of(ExecutionFlow.getAppRootPath(), "examples/examples/junit5/ParameterizedTestAnnotation.java"),
			 1
		);
		
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();

		List<List<Integer>> testPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		int invocationLine = 24;
		
		// Defines which methods will be collected
		String testMethodSignature = "examples.junit5.ParameterizedTestAnnotation.test1(int)";
		String methodSignature = "examples.others.auxClasses.AuxClass.factorial(int)";
		
		MethodInvokedInfo testMethodInfo = new MethodInvokedInfo.MethodInvokedInfoBuilder()
				.binPath(Path.of(ExecutionFlow.getAppRootPath(), "bin/examples/junit5/ParameterizedTestAnnotation.class"))
				.methodSignature(testMethodSignature)
				.srcPath(Path.of(ExecutionFlow.getAppRootPath(), "examples/examples/junit5/ParameterizedTestAnnotation.java"))
				.args(1)
				.build();
		
		MethodInvokedInfo methodInfo = new MethodInvokedInfo.MethodInvokedInfoBuilder()
				.binPath(Path.of(ExecutionFlow.getAppRootPath(), "bin/examples/others/auxClasses/AuxClass.class"))
				.srcPath(Path.of(ExecutionFlow.getAppRootPath(), "examples/examples/others/auxClasses/AuxClass.java"))
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("factorial")
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
				Arrays.asList(91,93,94,93,97)
			),
			testPaths
		);
	}
	
	/**
	 * Tests {@link examples.junit5.RepeatedTestAnnotation#test1()} test
	 * method.
	 */
	@Test
	public void repeatedTestAnnotation_test1() throws Throwable 
	{
		init(Path.of(ExecutionFlow.getAppRootPath(), "bin/examples/junit5/RepeatedTestAnnotation.class"),
			 Path.of(ExecutionFlow.getAppRootPath(), "examples/examples/junit5/RepeatedTestAnnotation.java")
		);
		
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();

		List<List<Integer>> testPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		int invocationLine = 23;
		
		// Defines which methods will be collected
		String testMethodSignature = "examples.junit5.RepeatedTestAnnotation.test1()";
		String methodSignature = "examples.others.auxClasses.AuxClass.factorial(int)";
		
		MethodInvokedInfo testMethodInfo = new MethodInvokedInfo.MethodInvokedInfoBuilder()
				.binPath(Path.of(ExecutionFlow.getAppRootPath(), "bin/examples/junit5/RepeatedTestAnnotation.class"))
				.methodSignature(testMethodSignature)
				.srcPath(Path.of(ExecutionFlow.getAppRootPath(), "examples/examples/junit5/RepeatedTestAnnotation.java"))
				.build();
		
		MethodInvokedInfo methodInfo = new MethodInvokedInfo.MethodInvokedInfoBuilder()
				.binPath(Path.of(ExecutionFlow.getAppRootPath(), "bin/examples/others/auxClasses/AuxClass.class"))
				.srcPath(Path.of(ExecutionFlow.getAppRootPath(), "examples/examples/others/auxClasses/AuxClass.java"))
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("factorial")
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
				Arrays.asList(91,93,94,93,94,93,94,93,94,93,97),
				Arrays.asList(91,93,94,93,94,93,94,93,94,93,97)
			),
			testPaths
		);
	}
	
	/**
	 * Tests {@link examples.junit5.TestAnnotation#test1()} test
	 * method.
	 */
	@Test
	public void testAnnotation_test1() throws Throwable 
	{
		init(Path.of(ExecutionFlow.getAppRootPath(), "bin/examples/junit5/TestAnnotation.class"),
			 Path.of(ExecutionFlow.getAppRootPath(), "examples/examples/junit5/TestAnnotation.java")
		);
		
		Map<Integer, List<CollectorInfo>> methodCollector = new LinkedHashMap<>();

		List<List<Integer>> testPaths;
		List<CollectorInfo> methodsInvoked = new ArrayList<>();
		int invocationLine = 28;
		
		// Defines which methods will be collected
		String testMethodSignature = "examples.junit5.TestAnnotation.test1()";
		String methodSignature = "examples.others.auxClasses.AuxClass.factorial(int)";
		
		MethodInvokedInfo testMethodInfo = new MethodInvokedInfo.MethodInvokedInfoBuilder()
				.binPath(Path.of(ExecutionFlow.getAppRootPath(), "bin/examples/junit5/TestAnnotation.class"))
				.methodSignature(testMethodSignature)
				.srcPath(Path.of(ExecutionFlow.getAppRootPath(), "examples/examples/junit5/TestAnnotation.java"))
				.build();
		
		MethodInvokedInfo methodInfo = new MethodInvokedInfo.MethodInvokedInfoBuilder()
				.binPath(Path.of(ExecutionFlow.getAppRootPath(), "bin/examples/others/auxClasses/AuxClass.class"))
				.srcPath(Path.of(ExecutionFlow.getAppRootPath(), "examples/examples/others/auxClasses/AuxClass.java"))
				.invocationLine(invocationLine)
				.methodSignature(methodSignature)
				.methodName("factorial")
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
					Arrays.asList(91,93,94,93,94,93,94,93,94,93,97)
			),
			testPaths
		);
	}
}