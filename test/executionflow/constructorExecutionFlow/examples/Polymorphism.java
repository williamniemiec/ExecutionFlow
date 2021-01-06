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
 * {@link examples.polymorphism.PolymorphismTest} test using 
 * {@link ConstructorExecutionFlow} class.
 */
@SkipCollection
public class Polymorphism extends ConstructorExecutionFlowTest {
	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	/**
	 * Tests constructor used by {@link examples.polymorphism
	 * .PolymorphismTest#testInterface()} test.
	 */
	@Test
	public void testChainedMethods() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.polymorphism.PolymorphismTest" + 
								".testInterface()");
		invokedOnLine(31);
		initializeTest();
		
		computeTestPathOf("examples.polymorphism.ClassInterface(String)");
		
		assertTestPathIs(5);
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
				Path.of("bin", "examples", "polymorphism", 
						"PolymorphismTest.class")
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
		return Path.of("bin", "examples", "polymorphism", 
					   "ClassInterface.class");
	}
	
	@Override
	protected Path getSrcTestedInvoked() {
		return Path.of("examples", "examples", "polymorphism", 
					   "ClassInterface.java");
	}
}