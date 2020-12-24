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
 * {@link examples.builderPattern.Person} class using 
 * {@link ConstructorExecutionFlow} class.
 */
@SkipCollection
public class BuilderPatternTest extends ConstructorExecutionFlowTest {
		
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	/**
	 * Tests constructor used by {@link examples.builderPattern
	 * .BuilderPatternTest#testBuilderPattern()} test.
	 */
	@Test
	public void testBuilderPattern() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.builderPattern.BuilderPatternTest" + 
								".testBuilderPattern()");
		invokedOnLine(14);
		initializeTest();
		
		computeTestPathOf("examples.builderPattern.Person$PersonBuilder()");
		
		assertTestPathIs((Integer[]) null);
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	protected String getTestMethodPackage() {
		return "examples.builderPattern";
	}
	
	@Override
	protected Path getTestMethodBinFile() {
		return ExecutionFlow.getAppRootPath().resolve(
				Path.of("bin", "examples", "builderPattern", 
						"BuilderPatternTest.class")
		);
	}
	
	@Override
	protected Path getTestMethodSrcFile() {
		return ExecutionFlow.getAppRootPath().resolve(
				Path.of("examples", "examples", "builderPattern", 
						"BuilderPatternTest.java")
		);
	}
	
	@Override
	protected Path getBinTestedInvoked() {
		return Path.of("bin", "examples", "builderPattern", 
					   "Person$PersonBuilder.class");
	}
	
	@Override
	protected Path getSrcTestedInvoked() {
		return Path.of("examples", "examples", "builderPattern", 
					   "Person.java");
	}
}