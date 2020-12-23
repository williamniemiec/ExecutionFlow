package executionFlow.methodExecutionFlow.examples;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.Test;

import executionFlow.ExecutionFlow;
import executionFlow.ExecutionFlowTest;
import executionFlow.MethodExecutionFlow;
import executionFlow.runtime.SkipCollection;

/**
 * Tests test path computation for the tested methods of 
 * {@link examples.complexTests.ComplexTests} class using 
 * {@link MethodExecutionFlow} class.
 */
@SkipCollection
public class ComplexTests extends ExecutionFlowTest {

	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	@Test
	public void factorial_constructor() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.complexTests.ComplexTests.testForConstructorAndMethod()");
		invokedOnLine(19);
		initializeTest();
		
		computeTestPathOf("examples.complexTests.TestClass_ComplexTests.factorial_constructor()");
		
		assertTestPathIs(
				new int[] {41,42,43,44,45,43,47},
				new int[] {41,42,43,44,45,43,44,45,43,47},
				new int[] {41,42,43,44,45,43,44,45,43,44,45,43,47},
				new int[] {41,42,43,44,45,43,44,45,43,44,45,43,44,45,43,47}
		);
	}
	
	/**
	 * Tests first method used by 
	 * {@link  examples.complexTests.ComplexTests.moreOneConstructor()} test.
	 */
	@Test
	public void moreOneConstructor_first() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.complexTests.ComplexTests.moreOneConstructor()");
		invokedOnLine(34);
		initializeTest();
		
		computeTestPathOf("examples.complexTests.TestClass_ComplexTests.factorial(long)");
		
		assertTestPathIs(20,23,24,25,26,27,25,26,27,25,26,27,25,26,27,25,29);
	}
	
	/**
	 * Tests second method used by 
	 * {@link  examples.complexTests.ComplexTests.moreOneConstructor()} test.
	 */
	@Test
	public void moreOneConstructor_two() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.complexTests.ComplexTests.moreOneConstructor()");
		invokedOnLine(35);
		initializeTest();
		
		computeTestPathOf("examples.complexTests.TestClass_ComplexTests.factorial(long)");
		
		assertTestPathIs(20,21);
	}
	
	/**
	 * Tests second method used by 
	 * {@link  examples.complexTests.ComplexTests.moreOneConstructorAndStaticMethod()} test.
	 */
	@Test
	public void moreOneConstructorAndStaticMethod() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.complexTests.ComplexTests.moreOneConstructorAndStaticMethod()");
		invokedOnLine(50);
		initializeTest();
		
		computeTestPathOf("examples.complexTests.TestClass_ComplexTests.staticFactorial(int)");
		
		assertTestPathIs(32,33,34,35,36,34,35,36,34,35,36,34,35,36,34,38);
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
				Path.of("bin", "examples", "complexTests", "ComplexTests.class")
		);
	}

	@Override
	protected Path getTestMethodSrcFile() {
		return ExecutionFlow.getAppRootPath().resolve(
				Path.of("examples", "examples", "complexTests", "ComplexTests.class")
		);
	}

	@Override
	protected Path getBinTestedInvoked() {
		return ExecutionFlow.getAppRootPath().resolve(
				Path.of("bin", "examples", "complexTests", "TestClass_ComplexTests.class")
		);
	}

	@Override
	protected Path getSrcTestedInvoked() {
		return ExecutionFlow.getAppRootPath().resolve(
				Path.of("examples", "examples", "complexTests", "TestClass_ComplexTests.java")
		);
	}	
}
