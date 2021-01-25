package executionflow.methodExecutionFlow.examples;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.Test;

import executionflow.ExecutionFlow;
import executionflow.MethodExecutionFlow;
import executionflow.methodExecutionFlow.MethodExecutionFlowTest;
import executionflow.runtime.SkipCollection;

/**
 * Tests test path computation for the tested methods of 
 * {@link examples.methodCalledByTestedInvoked.MethodCalledByTestedInvoked_Test} test using 
 * {@link MethodExecutionFlow} class.
 */
@SkipCollection
public class InvokedMethodsByTestedInvoked extends MethodExecutionFlowTest {
	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	/**
	 * Tests {@link examples.methodCalledByTestedInvoked
	 * .InvokedMethodsByTestedInvoker_Class#A()} method.
	 */
	@Test
	public void A() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.methodCalledByTestedInvoked" + 
								".MethodCalledByTestedInvoked_Test.T()");
		invokedOnLine(20);
		initializeTest();
		
		computeTestPathOf("examples.methodCalledByTestedInvoked" + 
						  ".MethodCalledByTestedInvoked_Class.A()");
		
		assertTestPathIs(10, 11,12);
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	protected String getTestMethodPackage() {
		return "examples.methodCalledByTestedInvoked";
	}
	
	@Override
	protected Path getTestMethodBinFile() {
		return ExecutionFlow.getAppRootPath().resolve(
				Path.of("bin", "examples", "methodCalledByTestedInvoked", 
						"MethodCalledByTestedInvoked_Test.class")
		);
	}
	
	@Override
	protected Path getTestMethodSrcFile() {
		return ExecutionFlow.getAppRootPath().resolve(
				Path.of("examples", "examples", "methodCalledByTestedInvoked", 
						"MethodCalledByTestedInvoked_Test.java")
		);
	}
	
	@Override
	protected Path getBinTestedInvoked() {
		return Path.of("bin", "examples", "methodCalledByTestedInvoked", 
					   "MethodCalledByTestedInvoked_Class.class");
	}
	
	@Override
	protected Path getSrcTestedInvoked() {
		return Path.of("examples", "examples", "methodCalledByTestedInvoked", 
					   "MethodCalledByTestedInvoked_Class.java");
	}
}