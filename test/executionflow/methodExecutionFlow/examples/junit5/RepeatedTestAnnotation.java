package executionflow.methodExecutionFlow.examples.junit5;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.Test;

import executionflow.ExecutionFlow;
import executionflow.MethodExecutionFlow;
import executionflow.methodExecutionFlow.MethodExecutionFlowTest;
import executionflow.runtime.SkipCollection;

/**
 * Tests test path computation for the tested methods of 
 * {@link examples.junit5.RepeatedTestAnnotation} test using 
 * {@link MethodExecutionFlow}.
 */
@SkipCollection
public class RepeatedTestAnnotation extends MethodExecutionFlowTest {
	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	/**
	 * Tests {@link examples.junit5.RepeatedTestAnnotation#test1()} test
	 * method.
	 */
	@Test
	public void repeatedTestAnnotation_test1() 
			throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.junit5.RepeatedTestAnnotation.test1()");
		invokedOnLine(25);
		initializeTest();
		
		computeTestPathOf("examples.others.auxClasses.AuxClass.factorial(int)");
		
		assertTestPathIs(
				new Integer[] {35,36,37,38,39,37,38,39,37,38,39,37,38,39,37,41},
				new Integer[] {35,36,37,38,39,37,38,39,37,38,39,37,38,39,37,41}
		);
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	protected String getTestMethodPackage() {
		return "examples.junit5";
	}
	
	@Override
	protected Path getTestMethodBinFile() {
		return ExecutionFlow.getAppRootPath().resolve(
				Path.of("bin", "examples", "junit5", 
						"RepeatedTestAnnotation.class")
		);
	}
	
	@Override
	protected Path getTestMethodSrcFile() {
		return ExecutionFlow.getAppRootPath().resolve(
				Path.of("examples", "examples", "junit5", 
						"RepeatedTestAnnotation.java")
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