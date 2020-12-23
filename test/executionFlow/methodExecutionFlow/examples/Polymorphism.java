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
 * {@link examples.polymorphism.PolymorphismTest} test using 
 * {@link MethodExecutionFlow} class.
 */
@SkipCollection
public class Polymorphism extends ExecutionFlowTest {
	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	/**
	 * Tests {@link examples.polymorphism.ClassInterface
	 * .testClassParam(ClassInterface)} method.
	 */
	@Test
	public void testClassParam() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.polymorphism.PolymorphismTest" + 
								".testParam()");
		invokedOnLine(20);
		initializeTest();
		
		computeTestPathOf("examples.polymorphism.ClassInterface" + 
						  ".testClassParam(ClassInterface)");
		
		assertTestPathIs(12);
	}
	
	/**
	 * Tests {@link examples.polymorphism.ClassInterface.interfaceMethod()}
	 * method.
	 */
	@Test
	public void interfaceMethod() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.polymorphism.PolymorphismTest" + 
								".testInterface()");
		invokedOnLine(33);
		initializeTest();
		
		computeTestPathOf("examples.polymorphism.ClassInterface" + 
						  ".interfaceMethod()");
		
		assertTestPathIs(8,9);
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	protected String getTestMethodPackage() {
		return "examples.polymorphism";
	}
	
	@Override
	protected Path getTestMethodBinFile() {
		return ExecutionFlow.getAppRootPath().resolve(
				Path.of("bin", "examples", "polymorphism", "PolymorphismTest.class")
		);
	}
	
	@Override
	protected Path getTestMethodSrcFile() {
		return ExecutionFlow.getAppRootPath().resolve(
				Path.of("examples", "examples", "polymorphism", 
						"PolymorphismTest.java")
		);
	}
	
	@Override
	protected Path getBinTestedInvoked() {
		return Path.of("bin", "examples", "polymorphism", "ClassInterface.class");
	}
	
	@Override
	protected Path getSrcTestedInvoked() {
		return Path.of("examples", "examples", "polymorphism", "ClassInterface.java");
	}
}