package executionFlow.methodExecutionFlow.examples.others;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.Test;

import executionFlow.ExecutionFlow;
import executionFlow.MethodExecutionFlow;
import executionFlow.methodExecutionFlow.MethodExecutionFlowTest;
import executionFlow.runtime.SkipCollection;

/**
 * Tests test path computation for the tested methods of 
 * {@link examples.others.MultiargsTest} test using 
 * {@link MethodExecutionFlow} class.
 */
@SkipCollection
public class MultiargsTest extends MethodExecutionFlowTest {

	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	/**
	 * Tests {@link examples.others.MultiargsTest#methodCallMultiLineArgsTest()} 
	 * test method.
	 */
	@Test
	public void methodCallMultiLineArgsTest() 
			throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.others.MultiargsTest" + 
								".methodCallMultiLineArgsTest()");
		invokedOnLine(18);
		initializeTest();
		
		computeTestPathOf("examples.others.auxClasses.AuxClass" + 
						  ".identity(int, int, int, int, int)");
		
		assertTestPathIs(102,103);
	}
	
	/**
	 * Tests {@link examples.others.MultiargsTest
	 * #simethodCallMultLineArgsWithBrokenLinesmpleTestPath()} test method.
	 */
	@Test
	public void methodCallMultLineArgsWithBrokenLines() 
			throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.others.MultiargsTest" + 
								".methodCallMultLineArgsWithBrokenLines()");
		invokedOnLine(26);
		initializeTest();
		
		computeTestPathOf("examples.others.auxClasses.AuxClass" + 
						  ".identity(int, int, int, int, int)");
		
		assertTestPathIs(102,103);
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
				Path.of("bin", "examples", "others", "MultiargsTest.class")
		);
	}
	
	@Override
	protected Path getTestMethodSrcFile() {
		return ExecutionFlow.getAppRootPath().resolve(
				Path.of("examples", "examples", "others", "MultiargsTest.java")
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
