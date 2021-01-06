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
 * {@link examples.controlFlow.ControlFlowTest} class using 
 * {@link ConstructorExecutionFlow} class.
 */
@SkipCollection
public class ControlFlowTest extends ConstructorExecutionFlowTest {

	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	/**
	 * Tests constructor used by {@link examples.controlFlow.ControlFlowTest}
	 * test.
	 * 
	 * @apiNote		{@link examples.controlFlow.ControlFlowTest} uses only one
	 * constructor, so it is possible choose any test method that uses the
	 * constructor
	 */
	@Test
	public void controlFlowTest() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.controlFlow.ControlFlowTest" + 
								".ifElseTest_earlyReturn()");
		invokedOnLine(18);
		initializeTest();
		
		computeTestPathOf("examples.controlFlow.TestClass_ControlFlow()");
		
		assertTestPathIsEmpty();
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	protected String getTestMethodPackage() {
		return "examples.controlFlow";
	}
	
	@Override
	protected Path getTestMethodBinFile() {
		return ExecutionFlow.getAppRootPath().resolve(
				Path.of("bin", "examples", "controlFlow", 
						"ControlFlowTest.class")
		);
	}
	
	@Override
	protected Path getTestMethodSrcFile() {
		return ExecutionFlow.getAppRootPath().resolve(
				Path.of("examples", "examples", "controlFlow", 
						"ControlFlowTest.java")
		);
	}
	
	@Override
	protected Path getBinTestedInvoked() {
		return Path.of("bin", "examples", "controlFlow", 
					   "TestClass_ControlFlow.class");
	}
	
	@Override
	protected Path getSrcTestedInvoked() {
		return Path.of("examples", "examples", "controlFlow", 
					  "TestClass_ControlFlow.java");
	}

}
