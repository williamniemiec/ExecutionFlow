package executionFlow.methodExecutionFlow.examples;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.Test;

import executionFlow.ExecutionFlow;
import executionFlow.MethodExecutionFlow;
import executionFlow.methodExecutionFlow.MethodExecutionFlowTest;
import executionFlow.runtime.SkipCollection;

/**
 * Tests test path computation for the tested methods of 
 * {@link examples.overloadedMethod.OverloadTest} test using 
 * {@link MethodExecutionFlow} class.
 */
@SkipCollection
public class OverloadedMethod extends MethodExecutionFlowTest {
	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	/**
	 * Tests {@link examples.overloadedMethod.OverloadClass.overloadedMethod()}
	 * method.
	 */
	@Test
	public void testOverloadedMethod() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.overloadedMethod.OverloadTest" + 
								".testOverloadedMethod()");
		invokedOnLine(11);
		initializeTest();
		
		computeTestPathOf("examples.overloadedMethod.OverloadClass" + 
						  ".overloadedMethod()");
		
		assertTestPathIs(15,16,17,18);
	}
	
	/**
	 * Tests {@link examples.overloadedMethod.OverloadClass.overloadedMethod(int)}
	 * method.
	 */
	@Test
	public void testOverloadedMethod2() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.overloadedMethod.OverloadTest" + 
								".testOverloadedMethod2()");
		invokedOnLine(18);
		initializeTest();
		
		computeTestPathOf("examples.overloadedMethod.OverloadClass" + 
						  ".overloadedMethod(int)");
		
		assertTestPathIs(11,12);
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