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
 * {@link examples.overloadedMethod.OverloadClass} class using 
 * {@link ConstructorExecutionFlow} class.
 */
@SkipCollection
public class OverloadedMethod extends ConstructorExecutionFlowTest {
	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	/**
	 * Tests constructor used by {@link examples.overloadedMethod.OverloadTest()}
	 * test.
	 */
	@Test
	public void testChainedMethods() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.overloadedMethod.OverloadTest" + 
								".testOverloadedMethod()");
		invokedOnLine(10);
		initializeTest();
		
		computeTestPathOf("examples.overloadedMethod.OverloadClass()");
		
		assertTestPathIs(8);
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	protected String getTestMethodPackage() {
		return "examples.overloadedMethod";
	}
	
	@Override
	protected Path getTestMethodBinFile() {
		return ExecutionFlow.getAppRootPath().resolve(
				Path.of("bin", "examples", "overloadedMethod", 
						"OverloadTest.class")
		);
	}
	
	@Override
	protected Path getTestMethodSrcFile() {
		return ExecutionFlow.getAppRootPath().resolve(
				Path.of("examples", "examples", "overloadedMethod", 
						"OverloadTest.java")
		);
	}
	
	@Override
	protected Path getBinTestedInvoked() {
		return Path.of("bin", "examples", "overloadedMethod", 
					   "OverloadClass.class");
	}
	
	@Override
	protected Path getSrcTestedInvoked() {
		return Path.of("examples", "examples", "overloadedMethod", 
					   "OverloadClass.java");
	}
}