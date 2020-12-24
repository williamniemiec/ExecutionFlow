package executionflow.methodExecutionFlow.examples.others;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.Test;

import executionflow.ExecutionFlow;
import executionflow.MethodExecutionFlow;
import executionflow.methodExecutionFlow.MethodExecutionFlowTest;
import executionflow.runtime.SkipCollection;

/**
 * Tests test path computation for the tested methods of 
 * {@link examples.others.SimpleTestPath} test using 
 * {@link MethodExecutionFlow} class.
 */
@SkipCollection
public class SimpleTestPath extends MethodExecutionFlowTest {

	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	/**
	 * Tests {@link examples.others.SimpleTestPath#simpleTestPath()} 
	 * test method.
	 */
	@Test
	public void simpleTestPath() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.others.SimpleTestPath.simpleTestPath()");
		invokedOnLine(19);
		initializeTest();
		
		computeTestPathOf("examples.others.auxClasses.AuxClass.factorial(int)");
		
		assertTestPathIs(35,36,37,38,39,37,38,39,37,38,39,37,38,39,37,41);
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	protected String getTestMethodPackage() {
		return "examples.others";
	}
	
	@Override
	protected Path getTestMethodBinFile() {
		return ExecutionFlow.getAppRootPath().resolve(
				Path.of("bin", "examples", "others", "SimpleTestPath.class")
		);
	}
	
	@Override
	protected Path getTestMethodSrcFile() {
		return ExecutionFlow.getAppRootPath().resolve(
				Path.of("examples", "examples", "others", "SimpleTestPath.java")
		);
	}
	
	@Override
	protected Path getBinTestedInvoked() {
		return Path.of("bin", "examples", "others", "auxClasses", 
					   "AuxClass.class");
	}
	
	@Override
	protected Path getSrcTestedInvoked() {
		return Path.of("examples", "examples", "others", "auxClasses",
					   "AuxClass.java");
	}
}