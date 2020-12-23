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
import executionFlow.info.InvokedContainer;
import executionFlow.info.InvokedInfo;
import executionFlow.io.manager.FileManager;
import executionFlow.info.InvokedInfo;
import executionFlow.runtime.SkipCollection;


/**
 * Tests test path computation for the constructors of 
 * {@link examples.innerClass.InnerClassTest} class using 
 * {@link ConstructorExecutionFlow} class.
 */
@SkipCollection
public class InnerClassTest extends ConstructorExecutionFlowTest
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final Path PATH_SRC_TEST_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath().toString(), "examples/examples/innerClass/InnerClassTest.java");
	private static final Path PATH_BIN_TEST_METHOD = 
			Path.of(ExecutionFlow.getAppRootPath().toString(), "bin/examples/innerClass/InnerClassTest.class");
	private static final String PACKAGE_TEST_METHOD = "examples.innerClass";
	
	
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
	 * Tests first constructor used by {@link examples.innerClass.InnerClassTest#test1()}
	 * test.
	 */
	@Test
	public void test1() throws ClassNotFoundException, IOException
	{
		List<List<Integer>> testPaths;
		Map<String, InvokedContainer> constructorCollector = new LinkedHashMap<>();
		Object[] paramValues = {"test"};
		Class<?>[] paramTypes = {String.class};
		String signature = "examples.innerClass.OuterClass(String)";
		String testMethodSignature = "examples.innerClass.InnerClassTest.test1()"; 
		String key = signature + Arrays.toString(paramValues);
		
		
		init("examples.innerClass.InnerClassTest", testMethodSignature);
		
		// Informations about test method
		InvokedInfo testMethodInfo = new InvokedInfo.Builder()
			.binPath(PATH_BIN_TEST_METHOD)
			.srcPath(PATH_SRC_TEST_METHOD)
			.invokedSignature(testMethodSignature)
			.build();
		
		// Informations about constructor
		InvokedInfo cii = new InvokedInfo.Builder()
			.binPath(Path.of("bin/examples/innerClass/OuterClass.class"))
			.srcPath(Path.of("examples/examples/innerClass/OuterClass.java"))
			.invokedSignature(signature)
			.isConstructor(true)
			.parameterTypes(paramTypes)
			.args(paramValues)
			.invocationLine(18)
			.build();
		
		// Saves extracted data
		InvokedContainer ci = new InvokedContainer(cii, testMethodInfo);
		
		constructorCollector.put(key, ci);
		
		testPaths = computeTestPath(constructorCollector.values(), testMethodSignature, signature.replaceAll("\\$", "."));
		
		assertEquals(Arrays.asList(5),	testPaths.get(0));
	}
	
	/**
	 * Tests second constructor used by {@link examples.innerClass.InnerClassTest#test1()}
	 * test.
	 */
	@Test
	public void test2() throws ClassNotFoundException, IOException
	{
		List<List<Integer>> testPaths;
		Map<String, InvokedContainer> constructorCollector = new LinkedHashMap<>();
		Object[] paramValues = {"test2"};
		Class<?>[] paramTypes = {String.class};
		String signature = "examples.innerClass.OuterClass$InnerClass(String)";
		String testMethodSignature = "examples.innerClass.InnerClassTest.test1()"; 
		String key = signature + Arrays.toString(paramValues);
		
		
		init("examples.innerClass.InnerClassTest", testMethodSignature);
		
		// Informations about test method
		InvokedInfo testMethodInfo = new InvokedInfo.Builder()
			.binPath(PATH_BIN_TEST_METHOD)
			.srcPath(PATH_SRC_TEST_METHOD)
			.invokedSignature(testMethodSignature)
			.build();
		
		// Informations about constructor
		InvokedInfo cii = new InvokedInfo.Builder()
			.binPath(Path.of("bin/examples/innerClass/OuterClass$InnerClass.class"))
			.srcPath(Path.of("examples/examples/innerClass/OuterClass.java"))
			.invokedSignature(signature)
			.isConstructor(true)
			.parameterTypes(paramTypes)
			.args(paramValues)
			.invocationLine(19)
			.build();
		
		// Saves extracted data
		InvokedContainer ci = new InvokedContainer(cii, testMethodInfo);
		
		constructorCollector.put(key, ci);
		
		testPaths = computeTestPath(constructorCollector.values(), testMethodSignature, signature.replaceAll("\\$", "."));
		
		assertEquals(Arrays.asList(10),	testPaths.get(0));
	}
}
