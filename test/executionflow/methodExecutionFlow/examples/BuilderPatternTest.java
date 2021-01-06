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
 * {@link examples.builderPattern.BuilderPatternTest} test using 
 * {@link MethodExecutionFlow} class.
 */
@SkipCollection
public class BuilderPatternTest extends MethodExecutionFlowTest {
	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	/**
	 * Tests {@link examples.builderPattern.Person#PersonBuilder.firstName(String)}
	 * method.
	 */
	@Test
	public void firstName() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.builderPattern.BuilderPatternTest" +
								".testBuilderPattern()");
		invokedOnLine(15);
		initializeTest();
		
		computeTestPathOf("examples.builderPattern.Person$PersonBuilder" +
						  ".firstName(String)");
		
		assertTestPathIs(19,20);
	}
	
	/**
	 * Tests {@link examples.builderPattern.Person#PersonBuilder.lastName(String)}
	 * method.
	 */
	@Test
	public void lastName() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.builderPattern.BuilderPatternTest" +
								".testBuilderPattern()");
		invokedOnLine(16);
		initializeTest();
		
		computeTestPathOf("examples.builderPattern.Person$PersonBuilder" +
						  ".lastName(String)");
		
		assertTestPathIs(23, 24);
	}
	
	/**
	 * Tests {@link examples.builderPattern.Person#PersonBuilder.age(int)}
	 * method.
	 */
	@Test
	public void age() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.builderPattern.BuilderPatternTest" +
								".testBuilderPattern()");
		invokedOnLine(17);
		initializeTest();
		
		computeTestPathOf("examples.builderPattern.Person$PersonBuilder.age(int)");
		
		assertTestPathIs(31, 32);
	}
	
	/**
	 * Tests {@link examples.builderPattern.Person#PersonBuilder.email(String)}
	 * method.
	 */
	@Test
	public void email() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.builderPattern.BuilderPatternTest" +
								".testBuilderPattern()");
		invokedOnLine(18);
		initializeTest();
		
		computeTestPathOf("examples.builderPattern.Person$PersonBuilder" +
					 	  ".email(String)");
		
		assertTestPathIs(27, 28);
	}
	
	/**
	 * Tests {@link examples.builderPattern.Person#PersonBuilder.build()}
	 * method.
	 */
	@Test
	public void build() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.builderPattern.BuilderPatternTest" +
								".testBuilderPattern()");
		invokedOnLine(19);
		initializeTest();
		
		computeTestPathOf("examples.builderPattern.Person$PersonBuilder.build()");
		
		assertTestPathIs(35);
	}
	
	/**
	 * Tests {@link examples.builderPattern.Person#print()} method.
	 */
	@Test
	public void print() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.builderPattern.BuilderPatternTest" +
								".testBuilderPattern()");
		invokedOnLine(21);
		initializeTest();
		
		computeTestPathOf("examples.builderPattern.Person.print()");
		
		assertTestPathIs(39, 40, 41, 42);
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
		return Path.of("bin", "examples", "builderPattern", "Person.class");
	}
	
	@Override
	protected Path getSrcTestedInvoked() {
		return Path.of("examples", "examples", "builderPattern", 
				"Person.java");
	}
}