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
 * {@link examples.chainedCalls.ChainedCalls} test using 
 * {@link MethodExecutionFlow} class.
 */
@SkipCollection
public class ChainedCalls extends ExecutionFlowTest {
	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	/**
	 * Tests {@link examples.chainedCalls.Calculator#setNumber(float)}
	 * method.
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	@Test
	public void setNumber() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.chainedCalls.ChainedCalls" +
								".testChainedMethods()");
		invokedOnLine(15);
		initializeTest();
		
		computeTestPathOf("examples.chainedCalls.Calculator.setNumber(float)");
		
		assertTestPathIs(11, 12);
	}
	
	/**
	 * Tests {@link examples.chainedCalls.Calculator#sum(float)}
	 * method.
	 */
	@Test
	public void sum() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.chainedCalls.ChainedCalls" +
								".testChainedMethods()");
		invokedOnLine(15);
		initializeTest();
		
		computeTestPathOf("examples.chainedCalls.Calculator.sum(float)");
		
		assertTestPathIs(14, 14, 14, 14);
	}
	
	/**
	 * Tests {@link examples.chainedCalls.Calculator#sub(float)}
	 * method.
	 */
	@Test
	public void sub() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.chainedCalls.ChainedCalls" +
								".testChainedMethods()");
		invokedOnLine(15);
		initializeTest();
		
		computeTestPathOf("examples.chainedCalls.Calculator.sub(float)");
		
		assertTestPathIs(19, 20);
	}
	
	/**
	 * Tests {@link examples.chainedCalls.Calculator#mult(float)}
	 * method.
	 */
	@Test
	public void mult() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.chainedCalls.ChainedCalls" +
								".testChainedMethods()");
		invokedOnLine(15);
		initializeTest();
		
		computeTestPathOf("examples.chainedCalls.Calculator.mult(float)");
		
		assertTestPathIs(23, 24);
	}
	
	/**
	 * Tests {@link examples.chainedCalls.Calculator#div(float)}
	 * method.
	 */
	@Test
	public void div() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.chainedCalls.ChainedCalls" +
								".testChainedMethods()");
		invokedOnLine(15);
		initializeTest();
		
		computeTestPathOf("examples.chainedCalls.Calculator.div(float)");
		
		assertTestPathIs(27, 28);
	}
	
	/**
	 * Tests {@link examples.chainedCalls.Calculator#ans()}
	 * method.
	 */
	@Test
	public void ans() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.chainedCalls.ChainedCalls" +
								".testChainedMethods()");
		invokedOnLine(15);
		initializeTest();
		
		computeTestPathOf("examples.chainedCalls.Calculator.ans()");
		
		assertTestPathIs(35);
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	protected String getTestMethodPackage() {
		return "examples.chainedCalls";
	}
	
	@Override
	protected Path getTestMethodBinFile() {
		return ExecutionFlow.getAppRootPath().resolve(
				Path.of("bin", "examples", "chainedCalls", 
						"ChainedCalls.class")
		);
	}
	
	@Override
	protected Path getTestMethodSrcFile() {
		return ExecutionFlow.getAppRootPath().resolve(
				Path.of("examples", "examples", "chainedCalls", 
						"ChainedCalls.java")
		);
	}
	
	@Override
	protected Path getBinTestedInvoked() {
		return Path.of("bin", "examples", "chainedCalls", 
					   "Calculator.class");
	}
	
	@Override
	protected Path getSrcTestedInvoked() {
		return Path.of("examples", "examples", "chainedCalls", 
					   "Calculator.java");
	}
}