package executionFlow.methodExecutionFlow.examples;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.Test;

import executionFlow.ExecutionFlow;
import executionFlow.MethodExecutionFlow;
import executionFlow.methodExecutionFlow.MethodExecutionFlowTest;
import executionFlow.runtime.SkipCollection;

/**
 * Tests test path computation for the tested methods of 
 * {@link examples.controlFlow.ControlFlowTest} class using 
 * {@link MethodExecutionFlow} class.
 */
@SkipCollection
public class ControlFlowTest extends MethodExecutionFlowTest {
	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	@Test
	public void ifElseTest_earlyReturn() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.controlFlow.ControlFlowTest" +
								".ifElseTest_earlyReturn()");
		invokedOnLine(19);
		initializeTest();
		
		computeTestPathOf("examples.controlFlow.TestClass_ControlFlow" +
						  ".ifElseMethod(int)");
		
		assertTestPathIs(7, 8);
	}
	
	@Test
	public void ifElseTest() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.controlFlow.ControlFlowTest" +
								".ifElseTest()");
		invokedOnLine(29);
		initializeTest();
		
		computeTestPathOf("examples.controlFlow.TestClass_ControlFlow" +
						  ".ifElseMethod()");
		
		assertTestPathIs(7,10,11,12,20);
	}
	
	@Test
	public void ifElseTest2() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.controlFlow.ControlFlowTest" +
								".ifElseTest2()");
		invokedOnLine(39);
		initializeTest();
		
		computeTestPathOf("examples.controlFlow.TestClass_ControlFlow" +
						  ".ifElseMethod()");
		
		assertTestPathIs(7,10,11,14,15,20);
	}
	
	@Test
	public void ifElseTest3() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.controlFlow.ControlFlowTest" +
								".ifElseTest3()");
		invokedOnLine(49);
		initializeTest();
		
		computeTestPathOf("examples.controlFlow.TestClass_ControlFlow" +
						  ".ifElseMethod()");
		
		assertTestPathIs(7,10,11,14,17,18,20);
	}
	
	@Test
	public void tryCatchTest1() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.controlFlow.ControlFlowTest" +
								".tryCatchTest1()");
		invokedOnLine(59);
		initializeTest();
		
		computeTestPathOf("examples.controlFlow.TestClass_ControlFlow" +
						  ".tryCatchMethod_try()");
		
		assertTestPathIs(23,24,25,26,27,28,29,34);
	}
	
	@Test
	public void tryCatchTest2() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.controlFlow.ControlFlowTest" +
								".tryCatchTest2()");
		invokedOnLine(66);
		initializeTest();
		
		computeTestPathOf("examples.controlFlow.TestClass_ControlFlow" +
						  ".tryCatchMethod_catch()");
		
		assertTestPathIs(37,38,39,40,42,43);
	}
	
	@Test
	public void switchCaseTest() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.controlFlow.ControlFlowTest" +
								".switchCaseTest()");
		invokedOnLine(74);
		initializeTest();
		
		computeTestPathOf("examples.controlFlow.TestClass_ControlFlow" +
						  ".switchCaseMethod(char)");
		
		assertTestPathIs(47,48,55,56,57,58,59,77);
	}
	
	@Test
	public void doWhileTest() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.controlFlow.ControlFlowTest" +
								".doWhileTest()");
		invokedOnLine(82);
		initializeTest();

		computeTestPathOf("examples.controlFlow.TestClass_ControlFlow" +
						  ".doWhileMethod(int,int)");
		
		assertTestPathIs(80,83,86,87,88,90,86,87,88,90,86,87,88,90,86,87,88,
						 90,86,87,88,90,91);
	}
	
	@Test
	public void inlineWhile() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.controlFlow.ControlFlowTest" +
								".inlineWhile()");
		invokedOnLine(92);
		initializeTest();
		
		computeTestPathOf("examples.controlFlow.TestClass_ControlFlow" +
						  ".inlineWhile(int)");
		
		assertTestPathIs(94,95,94,95,94,97);
	}
	
	@Test
	public void inlineDoWhile() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.controlFlow.ControlFlowTest" +
								".inlineDoWhile()");
		invokedOnLine(100);
		initializeTest();
		
		computeTestPathOf("examples.controlFlow.TestClass_ControlFlow" +
						  ".inlineDoWhile(int)");
		
		assertTestPathIs(108,111,112,114,111,112,114,111,115);
	}
	
	@Test
	public void inlineIfElse() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.controlFlow.ControlFlowTest" +
								".inlineIfElse()");
		invokedOnLine(108);
		initializeTest();
		
		computeTestPathOf("examples.controlFlow.TestClass_ControlFlow" +
						  ".inlineIfElse(int)");
		
		assertTestPathIs(100,101);
	}
	
	/**
	 * Tests first method used by {@link  examples.complexTests.ComplexTests
	 * .ifElseSameLine()} test.
	 */
	@Test
	public void ifElseSameLine() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.controlFlow.ControlFlowTest" +
								".ifElseSameLine()");
		invokedOnLine(116);
		initializeTest();
		
		computeTestPathOf("examples.controlFlow.TestClass_ControlFlow" +
						  ".ifElseSameLine(int)");
		
		assertTestPathIs(118,119,124);
	}
	
	/**
	 * Tests second method used by {@link  examples.complexTests.ComplexTests
	 * .ifElseSameLine()} test.
	 */
	@Test
	public void ifElseSameLine2() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.controlFlow.ControlFlowTest" +
								".ifElseSameLine()");
		invokedOnLine(117);
		initializeTest();
		
		computeTestPathOf("examples.controlFlow.TestClass_ControlFlow" +
						  ".ifElseSameLine(int)");
		
		assertTestPathIs(118,121,122,124);
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	protected String getTestMethodPackage() {
		return "examples.controlFlow";
	}
	
	@Override
	protected Path getTestMethodBinFile() {
		return ExecutionFlow.getAppRootPath().resolve(
				Path.of("bin", "examples", "controlFlow", "ControlFlowTest.class")
		);
	}
	
	@Override
	protected Path getTestMethodSrcFile() {
		return ExecutionFlow.getAppRootPath().resolve(
				Path.of("examples", "examples", "controlFlow", 
						"ControlFlowTest.java")
		);
	}
	
	@Override
	protected Path getBinTestedInvoked() {
		return Path.of("bin", "examples", "controlFlow", 
					   "TestClass_ControlFlow.class");
	}
	
	@Override
	protected Path getSrcTestedInvoked() {
		return Path.of("examples", "examples", "controlFlow", 
					   "TestClass_ControlFlow.java");
	}
}
