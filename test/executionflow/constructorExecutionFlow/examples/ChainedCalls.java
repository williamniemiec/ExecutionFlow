package executionflow.constructorExecutionFlow.examples;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.Test;

import executionflow.ConstructorExecutionFlow;
import executionflow.ExecutionFlow;
import executionflow.constructorExecutionFlow.ConstructorExecutionFlowTest;
import executionflow.runtime.SkipCollection;

/**
 * Tests test path computation for the constructors of 
 * {@link examples.chainedCalls.ChainedCalls} class using 
 * {@link ConstructorExecutionFlow} class.
 */
@SkipCollection
public class ChainedCalls extends ConstructorExecutionFlowTest {
	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	/**
	 * Tests constructor used by {@link examples.chainedCalls.ChainedCalls()}
	 * test.
	 */
	@Test
	public void testChainedMethods() throws ClassNotFoundException, IOException	{
		withTestMethodSignature("examples.chainedCalls.ChainedCalls" + 
								".testChainedMethods()");
		invokedOnLine(12);
		initializeTest();
		
		computeTestPathOf("examples.chainedCalls.Calculator()");
		
		assertTestPathIs(8);
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
				Path.of("bin", "examples", "chainedCalls", "ChainedCalls.class")
		);
	}
	
	@Override
	protected Path getTestMethodSrcFile() {
		return ExecutionFlow.getAppRootPath().resolve(
				Path.of("examples", "examples", "chainedCalls", "ChainedCalls.java")
		);
	}
	
	@Override
	protected Path getBinTestedInvoked() {
		return Path.of("bin", "examples", "chainedCalls", "Calculator.class");
	}
	
	@Override
	protected Path getSrcTestedInvoked() {
		return Path.of("examples", "examples", "chainedCalls", "Calculator.java");
	}
}