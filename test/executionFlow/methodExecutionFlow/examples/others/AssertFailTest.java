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
 * {@link examples.others.AssertFailTest} test using 
 * {@link MethodExecutionFlow} class.
 */
@SkipCollection
public class AssertFailTest extends MethodExecutionFlowTest {
	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	/**
	 * Tests first tested method of {@link examples.others.AssertFailTest
	 * #assertFailAtTheEndTest()} test method.
	 */
	@Test
	public void assertFailAtTheEndTest_m1() 
			throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.others.AssertFailTest" + 
								".assertFailAtTheEndTest()");
		invokedOnLine(21);
		initializeTest();
		
		computeTestPathOf("examples.others.auxClasses.AuxClass.threePaths(int)");
		
		assertTestPathIs(87,88);
	}
	
	/**
	 * Tests second tested method of {@link examples.others.AssertFailTest
	 * #assertFailAtTheEndTest()} test method.
	 */
	@Test
	public void assertFailAtTheEndTest_m2() 
			throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.others.AssertFailTest" + 
								".assertFailAtTheEndTest()");
		invokedOnLine(22);
		initializeTest();
		
		computeTestPathOf("examples.others.auxClasses.AuxClass.threePaths(int)");
		
		assertTestPathIs(87,90,91);
	}
	
	/**
	 * Tests third tested method of {@link examples.others.AssertFailTest
	 * #assertFailAtTheEndTest()} test method.
	 */
	@Test
	public void assertFailAtTheEndTest_m3() 
			throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.others.AssertFailTest" + 
								".assertFailAtTheEndTest()");
		invokedOnLine(23);
		initializeTest();
		
		computeTestPathOf("examples.others.auxClasses.AuxClass.threePaths(int)");
		
		assertTestPathIs(87,90,93);
	}
	
	/**
	 * Tests first tested method of {@link examples.others.AssertFailTest
	 * #assertFailInTheMiddleTest()} test method.
	 */
	@Test
	public void assertFailInTheMiddleTest_m1() 
			throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.others.AssertFailTest" + 
								".assertFailInTheMiddleTest()");
		invokedOnLine(32);
		initializeTest();
		
		computeTestPathOf("examples.others.auxClasses.AuxClass.threePaths(int)");
		
		assertTestPathIs(87,88);
	}
	
	/**
	 * Tests first tested method of {@link examples.others.AssertFailTest
	 * #assertFailInTheMiddleTest()} test method.
	 */
	@Test
	public void assertFailInTheMiddleTest_m2() 
			throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.others.AssertFailTest" + 
								".assertFailInTheMiddleTest()");
		invokedOnLine(33);
		initializeTest();
		
		computeTestPathOf("examples.others.auxClasses.AuxClass.threePaths(int)");
		
		assertTestPathIs(87,90,93);
	}
	
	/**
	 * Tests first tested method of {@link examples.others.AssertFailTest
	 * #assertFailInTheMiddleTest()} test method.
	 */
	@Test
	public void assertFailInTheMiddleTest_m3() 
			throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.others.AssertFailTest" + 
								".assertFailInTheMiddleTest()");
		invokedOnLine(34);
		initializeTest();
		
		computeTestPathOf("examples.others.auxClasses.AuxClass.threePaths(int)");
		
		assertTestPathIs(87,90,91);
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
				Path.of("bin", "examples", "others", "AssertFailTest.class")
		);
	}
	
	@Override
	protected Path getTestMethodSrcFile() {
		return ExecutionFlow.getAppRootPath().resolve(
				Path.of("examples", "examples", "others", "AssertFailTest.java")
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