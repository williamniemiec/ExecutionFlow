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
 * {@link examples.innerClass.InnerClassTest} class using 
 * {@link ConstructorExecutionFlow} class.
 */
@SkipCollection
public class InnerClassTest extends ConstructorExecutionFlowTest {
	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	/**
	 * Tests first constructor used by {@link examples.innerClass.InnerClassTest#test1()}
	 * test.
	 */
	@Test
	public void test1() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.innerClass.InnerClassTest.test1()");
		invokedOnLine(18);
		initializeTest();
		
		computeTestPathOf("examples.innerClass.OuterClass(String)");
		
		assertTestPathIs(5);
	}
	
	/**
	 * Tests second constructor used by {@link examples.innerClass.InnerClassTest#test1()}
	 * test.
	 */
	@Test
	public void test2() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.innerClass.InnerClassTest.test1()");
		invokedOnLine(19);
		initializeTest();
		
		computeTestPathOf("examples.innerClass.OuterClass$InnerClass(String)");
		
		assertTestPathIs(10);
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	protected String getTestMethodPackage() {
		return "examples.innerClass";
	}
	
	@Override
	protected Path getTestMethodBinFile() {
		return ExecutionFlow.getAppRootPath().resolve(
				Path.of("bin", "examples", "innerClass", "InnerClassTest.class")
		);
	}
	
	@Override
	protected Path getTestMethodSrcFile() {
		return ExecutionFlow.getAppRootPath().resolve(
				Path.of("examples", "examples", "innerClass", "InnerClassTest.java")
		);
	}
	
	@Override
	protected Path getBinTestedInvoked() {
		return Path.of("bin", "examples", "innerClass", "OuterClass.class");
	}
	
	@Override
	protected Path getSrcTestedInvoked() {
		return Path.of("examples", "examples", "innerClass", "OuterClass.java");
	}
}
