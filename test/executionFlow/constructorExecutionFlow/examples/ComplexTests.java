package executionFlow.constructorExecutionFlow.examples;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.Test;

import executionFlow.ConstructorExecutionFlow;
import executionFlow.ExecutionFlow;
import executionFlow.constructorExecutionFlow.ConstructorExecutionFlowTest;
import executionFlow.runtime.SkipCollection;

/**
 * Tests test path computation for the constructors of 
 * {@link examples.complexTests.ComplexTests} class using 
 * {@link ConstructorExecutionFlow} class.
 */
@SkipCollection
public class ComplexTests extends ConstructorExecutionFlowTest {	
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	protected static final Path PATH_SRC_TEST_METHOD;
	protected static final Path PATH_BIN_TEST_METHOD;
	protected static final String PACKAGE_TEST_METHOD;
	protected static final String CLASS_SIGNATURE;
	
	
	//-------------------------------------------------------------------------
	//		Initialization block
	//-------------------------------------------------------------------------
	static {
		PATH_SRC_TEST_METHOD = ExecutionFlow.getAppRootPath().resolve(
				Path.of("examples", "examples", "complexTests", "ComplexTests.java")
		);
		PATH_BIN_TEST_METHOD = ExecutionFlow.getAppRootPath().resolve(
				Path.of("bin", "examples", "complexTests", "ComplexTests.class")
		);
		PACKAGE_TEST_METHOD = "examples.complexTests";
		CLASS_SIGNATURE = "examples.complexTests.ComplexTests";
	}
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	public ComplexTests() {
		super(
				CLASS_SIGNATURE, 
				PACKAGE_TEST_METHOD, 
				PATH_SRC_TEST_METHOD, 
				PATH_BIN_TEST_METHOD
		);
	}
	
	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	/**
	 * Tests constructor used by {@link examples.complexTests.ComplexTests#testForConstructor()}
	 * test.
	 */
	@Test
	public void testForConstructorAndMethod() 
			throws ClassNotFoundException, IOException {
		testMethodSignature = "examples.complexTests.ComplexTests.testForConstructorAndMethod()"; 
		paramValues = new Object[] {1, 2, 3, 4};
		paramTypes = new Class[] {int.class, int.class, int.class, int.class};
		invocationLine = 18;
		withSignature("examples.complexTests.ComplexTests.testForConstructorAndMethod()");
		withParameterTypes(int.class, int.class, int.class, int.class);
		withParameterValues(1, 2, 3, 4);
		invokedOnLine(18)
		
		initializeTest();
		computeTestPathOf("examples.complexTests.TestClass_ComplexTests(int)");
		assertTestPathIs(14, 14, 14, 14);
	}
	
	/**
	 * Tests first constructor used by 
	 * {@link examples.complexTests.ComplexTests#moreOneConstructor()} test.
	 */
	@Test
	public void moreOneConstructorWithoutParams() 
			throws ClassNotFoundException, IOException {
		testMethodSignature = "examples.complexTests.ComplexTests.moreOneConstructor()";
		paramValues = new Object[] {};
		paramTypes = new Class[] {};
		invocationLine = 31;
		
		initializeTest(testMethodSignature);
		computeTestPathOf("examples.complexTests.TestClass_ComplexTests()");
		assertTestPathIs(17);
	}
	
	/**
	 * Tests second constructor used by 
	 * {@link examples.complexTests.ComplexTests#moreOneConstructor()} test.
	 */
	@Test
	public void moreOneConstructorWithBooleanParam() 
			throws ClassNotFoundException, IOException {
		testMethodSignature = "examples.complexTests.ComplexTests.moreOneConstructor()";
		paramValues = new Object[] {true};
		paramTypes = new Class[] {boolean.class};
		invocationLine = 32;
		
		initializeTest(testMethodSignature);
		computeTestPathOf("examples.complexTests.TestClass_ComplexTests(boolean)");
		assertTestPathIs(10, 11);
	}
}
