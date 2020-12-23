package executionFlow.methodExecutionFlow.examples.others;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.Test;

import executionFlow.ExecutionFlow;
import executionFlow.ExecutionFlowTest;
import executionFlow.MethodExecutionFlow;
import executionFlow.runtime.SkipCollection;

/**
 * Tests test path computation for the tested methods of 
 * {@link examples.others.MultipleTestPaths} test using 
 * {@link MethodExecutionFlow} class.
 */
@SkipCollection
public class MultipleTestPaths extends ExecutionFlowTest {
	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	/**
	 * Tests first method of {@link examples.others.MultipleTestPaths
	 * .ThreeTestPathsTest()} test method.
	 */
	@Test
	public void ThreeTestPathsTest_m1() 
			throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.others.MultipleTestPaths" + 
								".ThreeTestPathsTest()");
		invokedOnLine(21);
		initializeTest();
		
		computeTestPathOf("examples.others.auxClasses.AuxClass.threePaths(int)");
		
		assertTestPathIs(87,88);
	}
	
	/**
	 * Tests second method of {@link examples.others.MultipleTestPaths
	 * .ThreeTestPathsTest()} test method.
	 */
	@Test
	public void ThreeTestPathsTest_m2() 
			throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.others.MultipleTestPaths" + 
								".ThreeTestPathsTest()");
		invokedOnLine(22);
		initializeTest();
		
		computeTestPathOf("examples.others.auxClasses.AuxClass.threePaths(int)");
		
		assertTestPathIs(87,90,91);
	}
	
	/**
	 * Tests third method of {@link examples.others.MultipleTestPaths
	 * .ThreeTestPathsTest()} test method.
	 */
	@Test
	public void ThreeTestPathsTest_m3() 
			throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.others.MultipleTestPaths" + 
								".ThreeTestPathsTest()");
		invokedOnLine(23);
		initializeTest();
		
		computeTestPathOf("examples.others.auxClasses.AuxClass.threePaths(int)");
		
		assertTestPathIs(87,90,93);
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
				Path.of("bin", "examples", "others", "MultipleTestPaths.class")
		);
	}
	
	@Override
	protected Path getTestMethodSrcFile() {
		return ExecutionFlow.getAppRootPath().resolve(
				Path.of("examples", "examples", "others", "MultipleTestPaths.java")
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