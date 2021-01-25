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
 * {@link examples.innerClass.InnerClassTest} test using 
 * {@link MethodExecutionFlow} class.
 */
@SkipCollection
public class InnerClassTest extends MethodExecutionFlowTest {
	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	/**
	 * Tests first method used by {@link examples.innerClass
	 * .InnerClassTest#test1()} test.
	 */
	@Test
	public void test1() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.innerClass.InnerClassTest" +
								".test1()");
		invokedOnLine(22);
		initializeTest();
		
		computeTestPathOf("examples.innerClass.OuterClass.getText()");
		
		assertTestPathIs(17);
	}
	
	/**
	 * Tests second method used by {@link examples.innerClass
	 * .InnerClassTest#test1()} test.
	 */
	@Test
	public void test2() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.innerClass.InnerClassTest.test1()");
		invokedOnLine(23);
		initializeTest();
		
		computeTestPathOf("examples.innerClass.OuterClass$InnerClass.getText()");
		
		assertTestPathIs(13);
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
				Path.of("examples", "examples", "innerClass", 
						"InnerClassTest.java")
		);
	}
	
	@Override
	protected Path getBinTestedInvoked() {
		return Path.of("bin", "examples", "innerClass", 
					   "OuterClass.class");
	}
	
	@Override
	protected Path getSrcTestedInvoked() {
		return Path.of("examples", "examples", "innerClass", "OuterClass.java");
	}
}
