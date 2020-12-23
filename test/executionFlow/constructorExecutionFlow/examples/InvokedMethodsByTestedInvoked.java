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
 * {@link examples.methodCalledByTestedInvoked.InvokedMethodsByTestedInvoked_Class}
 * class using {@link ConstructorExecutionFlow} class.
 */
@SkipCollection
public class InvokedMethodsByTestedInvoked extends ConstructorExecutionFlowTest {
	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	/**
	 * Tests constructor used by {@link examples.methodCalledByTestedInvoked
	 * .InvokedMethodsByTestedInvoker_Test#T()} test.
	 */
	@Test
	public void T() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.methodCalledByTestedInvoked" + 
								".MethodCalledByTestedInvoked_Test.T()");
		invokedOnLine(19);
		initializeTest();
		
		computeTestPathOf("examples.methodCalledByTestedInvoked" + 
						  ".MethodCalledByTestedInvoked_Class(boolean)");
		
		assertTestPathIs(4);
	}
	
	/**
	 * Tests constructor used by {@link examples.methodCalledByTestedInvoked
	 * .InvokedMethodsByTestedInvoker_Test#T2()} test.
	 */
	@Test
	public void T2() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.methodCalledByTestedInvoked" + 
								".MethodCalledByTestedInvoked_Test.T2()");
		invokedOnLine(32);
		initializeTest();
		
		computeTestPathOf("examples.methodCalledByTestedInvoked" + 
						  ".MethodCalledByTestedInvoked_Class(boolean)");
		
		assertTestPathIs(4, 5, 6);
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