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
 * {@link examples.others.OthersTest} test using 
 * {@link MethodExecutionFlow} class.
 */
@SkipCollection
public class OthersTest extends MethodExecutionFlowTest {

	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	/**
	 * Tests {@link examples.others.OthersTest.testEmptyTest()} test
	 * method.
	 */
	@Test
	public void testEmptyTest() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.others.OthersTest.testEmptyTest()");
		invokedOnLine(24);
		initializeTest();
		
		computeTestPathOf("examples.others.auxClasses.AuxClass.factorial(int)");
		
		assertTestPathIsEmpty();
	}
	
	/**
	 * Tests {@link examples.others.OthersTest.testFactorial()} test
	 * method.
	 */
	@Test
	public void testFactorial() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.others.OthersTest.testFactorial()");
		invokedOnLine(34);
		initializeTest();
		
		computeTestPathOf("examples.others.auxClasses.AuxClass.factorial(int)");
		
		assertTestPathIs(35,36,37,38,39,37,38,39,37,38,39,37,38,39,37,41);
	}
	
	/**
	 * Tests {@link examples.others.OthersTest.testFactorial_zero()} test
	 * method.
	 */
	@Test
	public void testFactorial_zero() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.others.OthersTest" + 
								".testFactorial_zero()");
		invokedOnLine(46);
		initializeTest();
		
		computeTestPathOf("examples.others.auxClasses.AuxClass.factorial(int)");
		
		assertTestPathIs(35,36,37,41);
	}
	
	/**
	 * Tests {@link examples.others.OthersTest.testFibonacci()} test
	 * method.
	 */
	@Test
	public void testFibonacci() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.others.OthersTest.testFibonacci()");
		invokedOnLine(58);
		initializeTest();
		
		computeTestPathOf("examples.others.auxClasses.AuxClass.fibonacci(int)");
		
		assertTestPathIs(44,45,46,47,48,49,50,51,52,48,49,50,51,52,48,49,
						 50,51,52,48,54);
	}
	
	/**
	 * Tests {@link examples.others.OthersTest.testInternalCall()} test
	 * method.
	 */
	@Test
	public void testInternalCall() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.others.OthersTest.testInternalCall()");
		invokedOnLine(67);
		initializeTest();
		
		computeTestPathOf("examples.others.auxClasses.AuxClass" + 
						  ".parseLetters_withInternalCall(char[])");
		
		assertTestPathIs(72,73,74,75,76,74,75,76,74,75,76,74,75,76,74,75,76,
						 74,75,76,74,75,76,74,75,76,74,75,76,74,75,76,74,78);
	}
	
	/**
	 * Tests {@link examples.others.OthersTest.testStaticMethod_charSequence()}
	 * test method.
	 */
	@Test
	public void testStaticMethod_charSequence() 
			throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.others.OthersTest" + 
								".testStaticMethod_charSequence()");
		invokedOnLine(76);
		initializeTest();
		
		computeTestPathOf("examples.others.auxClasses.AuxClass." + 
						  "parseLetters_noInternalCall(CharSequence)");
		
		assertTestPathIs(57,58,59,60,61,62,67,60,61,62,67,60,61,62,67,60,61,62,
						 67,60,61,62,67,60,61,64,65,67,60,61,64,65,67,60,61,64,
						 65,67,60,61,64,65,67,60,61,64,65,67,60,69);
	}
	
	/**
	 * Tests {@link examples.others.OthersTest.testParamSignature_object()} test
	 * method.
	 */
	@Test
	public void testParamSignature_object() 
			throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.others.OthersTest" + 
								".testParamSignature_object()");
		invokedOnLine(86);
		initializeTest();
		
		computeTestPathOf("examples.others.auxClasses.AuxClass" + 
						  ".testObjParam(String)");
		
		assertTestPathIs(96);
	}
	
	/**
	 * Tests first tested method of {@link examples.others.OthersTest
	 * .testMethodWithAuxMethods()} test method.
	 */
	@Test
	public void testMethodWithAuxMethods_m1() 
			throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.others.OthersTest" + 
								".testMethodWithAuxMethods()");
		invokedOnLine(104);
		initializeTest();
		
		computeTestPathOf("examples.others.auxClasses.AuxClass.fibonacci(int)");
		
		assertTestPathIs(44,45,46,47,48,49,50,51,52,48,49,50,51,52,48,54);
	}
	
	/**
	 * Tests second tested method of {@link examples.others.OthersTest
	 * .testMethodWithAuxMethods()} test method.
	 */
	@Test
	public void testMethodWithAuxMethods_m2() 
			throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.others.OthersTest" + 
								".testMethodWithAuxMethods()");
		invokedOnLine(105);
		initializeTest();
		
		computeTestPathOf("examples.others.auxClasses.AuxClass.factorial(int)");
		
		assertTestPathIs(35,36,37,38,39,37,38,39,37,38,39,37,41);
	}
	
	/**
	 * Tests first tested method of
	 * {@link examples.others.OthersTest.testMethodWithAuxMethods()} test method.
	 */
	@Test
	public void testingMultipleMethods_m1() 
			throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.others.OthersTest" + 
								".testingMultipleMethods()");
		invokedOnLine(118);
		initializeTest();
		
		computeTestPathOf("examples.others.auxClasses.AuxClass.factorial(int)");
		
		assertTestPathIs(35,36,37,38,39,37,38,39,37,38,39,37,38,39,37,41);
	}
	
	/**
	 * Tests second tested method of {@link examples.others.OthersTest
	 * .testMethodWithAuxMethods()} test method.
	 */
	@Test
	public void testingMultipleMethods_m2() 
			throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.others.OthersTest" + 
								".testingMultipleMethods()");
		invokedOnLine(119);
		initializeTest();
		
		computeTestPathOf("examples.others.auxClasses.AuxClass.fibonacci(int)");
		
		assertTestPathIs(44,45,46,47,48,49,50,51,52,48,49,50,51,52,48,54);
	}
	
	/**
	 * Tests {@link examples.others.OthersTest.onlyOneMethod()} test method.
	 */
	@Test
	public void onlyOneMethod() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.others.OthersTest.onlyOneMethod()");
		invokedOnLine(127);
		initializeTest();
		
		computeTestPathOf("examples.others.auxClasses.AuxClass.getNumber()");
		
		assertTestPathIs(99);
	}
	
	/**
	 * Tests {@link examples.others.OthersTest.methodCallMultiLineArgs()} test 
	 * method.
	 */
	@Test
	public void methodCallMultiLineArgs_1() 
			throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.others.OthersTest" + 
								".anonymousObjectReturn()");
		invokedOnLine(137);
		initializeTest();
		
		computeTestPathOf("examples.others.auxClasses.ClassInterface" + 
						  ".interfaceMethod()");
		
		assertTestPathIs(108,109);
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
				Path.of("bin", "examples", "others", "OthersTest.class")
		);
	}
	
	@Override
	protected Path getTestMethodSrcFile() {
		return ExecutionFlow.getAppRootPath().resolve(
				Path.of("examples", "examples", "others", "OthersTest.java")
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