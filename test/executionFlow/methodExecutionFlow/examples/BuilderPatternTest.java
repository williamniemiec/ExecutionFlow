package executionFlow.methodExecutionFlow.examples;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import executionFlow.ExecutionFlow;
import executionFlow.MethodExecutionFlow;
import executionFlow.info.InvokedContainer;
import executionFlow.info.InvokedInfo;
import executionFlow.io.manager.FileManager;
import executionFlow.methodExecutionFlow.MethodExecutionFlowTest;
import executionFlow.runtime.SkipCollection;


/**
 * Tests test path computation for the tested methods of 
 * {@link examples.builderPattern.BuilderPatternTest} test using 
 * {@link MethodExecutionFlow} class.
 */
@SkipCollection
public class BuilderPatternTest extends MethodExecutionFlowTest
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final Path PATH_BIN_TEST_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/builderPattern/BuilderPatternTest.class");
	private static final Path PATH_SRC_TEST_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/builderPattern/BuilderPatternTest.java");
	private static final String PACKAGE_TEST_METHOD = "examples.builderPattern";
	private static final Path PATH_BIN_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/builderPattern/Person.class");
	private static final Path PATH_SRC_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/builderPattern/Person.java");
	
	
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
		Map<Integer, List<InvokedContainer>> methodCollector = new LinkedHashMap<>();

		List<List<Integer>> testPaths;
		List<InvokedContainer> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		InvokedInfo testMethodInfo, methodInfo;
		InvokedContainer ci;
		int invocationLine = 15;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.builderPattern.BuilderPatternTest.testBuilderPattern()";
		methodSignature = "examples.builderPattern.Person$PersonBuilder.firstName(String)";
		
		init("examples.builderPattern.Person", testMethodSignature);
		
		testMethodInfo = new InvokedInfo.Builder()
				.binPath(PATH_BIN_TEST_METHOD)
				.invokedSignature(testMethodSignature)
				.srcPath(PATH_SRC_TEST_METHOD)
				.build();
		
		methodInfo = new InvokedInfo.Builder()
				.binPath(PATH_BIN_METHOD)
				.srcPath(PATH_SRC_METHOD)
				.invocationLine(invocationLine)
				.invokedSignature(methodSignature)
				.invokedName("firstName")
				.build();
		
		ci = new InvokedContainer(methodInfo, testMethodInfo);
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		testPaths = computeTestPath(methodCollector, ci);
		
		assertEquals(
			Arrays.asList(
				Arrays.asList(19,20)
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
		Map<Integer, List<InvokedContainer>> methodCollector = new LinkedHashMap<>();
		List<List<Integer>> testPaths;
		List<InvokedContainer> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		InvokedInfo testMethodInfo, methodInfo;
		InvokedContainer ci;
		int invocationLine = 16;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.builderPattern.BuilderPatternTest.testBuilderPattern()";
		methodSignature = "examples.builderPattern.Person$PersonBuilder.lastName(String)";
		
		init("examples.builderPattern.Person", testMethodSignature);
		
		testMethodInfo = new InvokedInfo.Builder()
				.binPath(PATH_BIN_TEST_METHOD)
				.invokedSignature(testMethodSignature)
				.srcPath(PATH_SRC_TEST_METHOD)
				.build();
		
		methodInfo = new InvokedInfo.Builder()
				.binPath(PATH_BIN_METHOD)
				.srcPath(PATH_SRC_METHOD)
				.invocationLine(invocationLine)
				.invokedSignature(methodSignature)
				.invokedName("lastName")
				.build();
		
		ci = new InvokedContainer(methodInfo, testMethodInfo);
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		testPaths = computeTestPath(methodCollector, ci);
		
		assertEquals(
			Arrays.asList(
				Arrays.asList(23,24)
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
		Map<Integer, List<InvokedContainer>> methodCollector = new LinkedHashMap<>();
		List<List<Integer>> testPaths;
		List<InvokedContainer> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		InvokedInfo testMethodInfo, methodInfo;
		InvokedContainer ci;
		int invocationLine = 17;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.builderPattern.BuilderPatternTest.testBuilderPattern()";
		methodSignature = "examples.builderPattern.Person$PersonBuilder.age(int)";
		
		init("examples.builderPattern.Person", testMethodSignature);
		
		testMethodInfo = new InvokedInfo.Builder()
				.binPath(PATH_BIN_TEST_METHOD)
				.invokedSignature(testMethodSignature)
				.srcPath(PATH_SRC_TEST_METHOD)
				.build();
		
		methodInfo = new InvokedInfo.Builder()
				.binPath(PATH_BIN_METHOD)
				.srcPath(PATH_SRC_METHOD)
				.invocationLine(invocationLine)
				.invokedSignature(methodSignature)
				.invokedName("age")
				.build();
		
		ci = new InvokedContainer(methodInfo, testMethodInfo);
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		testPaths = computeTestPath(methodCollector, ci);
		
		assertEquals(
			Arrays.asList(
				Arrays.asList(31,32)
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
		Map<Integer, List<InvokedContainer>> methodCollector = new LinkedHashMap<>();
		List<List<Integer>> testPaths;
		List<InvokedContainer> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		InvokedInfo testMethodInfo, methodInfo;
		InvokedContainer ci;
		int invocationLine = 18;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.builderPattern.BuilderPatternTest.testBuilderPattern()";
		methodSignature = "examples.builderPattern.Person$PersonBuilder.email(String)";
		
		init("examples.builderPattern.Person", testMethodSignature);
		
		testMethodInfo = new InvokedInfo.Builder()
				.binPath(PATH_BIN_TEST_METHOD)
				.invokedSignature(testMethodSignature)
				.srcPath(PATH_SRC_TEST_METHOD)
				.build();
		
		methodInfo = new InvokedInfo.Builder()
				.binPath(PATH_BIN_METHOD)
				.srcPath(PATH_SRC_METHOD)
				.invocationLine(invocationLine)
				.invokedSignature(methodSignature)
				.invokedName("email")
				.build();
		
		ci = new InvokedContainer(methodInfo, testMethodInfo);
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		testPaths = computeTestPath(methodCollector, ci);
		
		assertEquals(
			Arrays.asList(
				Arrays.asList(27,28)
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
		Map<Integer, List<InvokedContainer>> methodCollector = new LinkedHashMap<>();
		List<List<Integer>> testPaths;
		List<InvokedContainer> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		InvokedInfo testMethodInfo, methodInfo;
		InvokedContainer ci;
		int invocationLine = 19;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.builderPattern.BuilderPatternTest.testBuilderPattern()";
		methodSignature = "examples.builderPattern.Person$PersonBuilder.build()";
		
		init("examples.builderPattern.Person", testMethodSignature);
		
		testMethodInfo = new InvokedInfo.Builder()
				.binPath(PATH_BIN_TEST_METHOD)
				.invokedSignature(testMethodSignature)
				.srcPath(PATH_SRC_TEST_METHOD)
				.build();
		
		methodInfo = new InvokedInfo.Builder()
				.binPath(PATH_BIN_METHOD)
				.srcPath(PATH_SRC_METHOD)
				.invocationLine(invocationLine)
				.invokedSignature(methodSignature)
				.invokedName("build")
				.build();
		
		ci = new InvokedContainer(methodInfo, testMethodInfo);
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		testPaths = computeTestPath(methodCollector, ci);
		
		assertEquals(
			Arrays.asList(
				Arrays.asList(35)
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
		Map<Integer, List<InvokedContainer>> methodCollector = new LinkedHashMap<>();
		List<List<Integer>> testPaths;
		List<InvokedContainer> methodsInvoked = new ArrayList<>();
		String testMethodSignature, methodSignature;
		InvokedInfo testMethodInfo, methodInfo;
		InvokedContainer ci;
		int invocationLine = 21;
		
		
		// Defines which methods will be collected
		testMethodSignature = "examples.builderPattern.BuilderPatternTest.testBuilderPattern()";
		methodSignature = "examples.builderPattern.Person.print()";
		
		init("examples.builderPattern.Person", testMethodSignature);
		
		testMethodInfo = new InvokedInfo.Builder()
				.binPath(PATH_BIN_TEST_METHOD)
				.invokedSignature(testMethodSignature)
				.srcPath(PATH_SRC_TEST_METHOD)
				.build();
		
		methodInfo = new InvokedInfo.Builder()
				.binPath(PATH_BIN_METHOD)
				.srcPath(PATH_SRC_METHOD)
				.invocationLine(invocationLine)
				.invokedSignature(methodSignature)
				.invokedName("print")
				.build();
		
		ci = new InvokedContainer(methodInfo, testMethodInfo);
		
		methodsInvoked.add(ci);
		methodCollector.put(invocationLine, methodsInvoked);
		
		testPaths = computeTestPath(methodCollector, ci);
		
		assertEquals(
			Arrays.asList(
				Arrays.asList(39,40,41,42)
			),
			testPaths
		);
	}
}