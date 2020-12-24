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
	//		Tests
	//-------------------------------------------------------------------------
	/**
	 * Tests constructor used by {@link examples.complexTests.ComplexTests
	 * #testForConstructor()} test.
	 */
	@Test
	public void testForConstructorAndMethod() 
			throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.complexTests.ComplexTests" + 
								".testForConstructorAndMethod()");
		invokedOnLine(18);
		initializeTest();

		computeTestPathOf("examples.complexTests.TestClass_ComplexTests(int)");
		
		assertTestPathIs(
				new Integer[] {14}, 
				new Integer[] {14}, 
				new Integer[] {14},
				new Integer[] {14}
		);
	}


	/**
	 * Tests first constructor used by {@link examples.complexTests.ComplexTests
	 * #moreOneConstructor()} test.
	 */
	@Test
	public void moreOneConstructorWithoutParams() 
			throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.complexTests.ComplexTests" + 
								".moreOneConstructor()");
		invokedOnLine(31);
		initializeTest();
		
		computeTestPathOf("examples.complexTests.TestClass_ComplexTests()");
		
		assertTestPathIs(17);
	}
	
	/**
	 * Tests second constructor used by {@link examples.complexTests.ComplexTests
	 * #moreOneConstructor()} test.
	 */
	@Test
	public void moreOneConstructorWithBooleanParam() 
			throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.complexTests.ComplexTests" + 
								".moreOneConstructor()");
		invokedOnLine(32);
		initializeTest();
		
		computeTestPathOf("examples.complexTests.TestClass_ComplexTests(boolean)");
		
		assertTestPathIs(10, 11);
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	protected String getTestMethodPackage() {
		return "examples.complexTests";
	}
	
	@Override
	protected Path getTestMethodBinFile() {
		return ExecutionFlow.getAppRootPath().resolve(
				Path.of("bin", "examples", "complexTests", 
						"ComplexTests.class")
		);
	}
	
	@Override
	protected Path getTestMethodSrcFile() {
		return ExecutionFlow.getAppRootPath().resolve(
				Path.of("examples", "examples", "complexTests", 
						"ComplexTests.java")
		);
	}
	
	@Override
	protected Path getBinTestedInvoked() {
		return Path.of("bin", "examples", "complexTests", 
					   "TestClass_ComplexTests.class");
	}
	
	@Override
	protected Path getSrcTestedInvoked() {
		return Path.of("examples", "examples", "complexTests", 
					   "TestClass_ComplexTests.java");
	}
}
