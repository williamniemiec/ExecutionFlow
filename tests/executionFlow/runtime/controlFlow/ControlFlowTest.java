package executionFlow.runtime.controlFlow;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class ControlFlowTest 
{
	/*
	@Test
	public void ifElseTest() 
	{
		TestClass_ControlFlow tccf = new TestClass_ControlFlow();
		String result = tccf.ifElseMethod(0);
		String expectedResult = "Number zero";
		
		assertEquals(expectedResult, result);
	}
	
	@Test
	public void ifElseTest2() 
	{
		TestClass_ControlFlow tccf = new TestClass_ControlFlow();
		String result = tccf.ifElseMethod(1);
		String expectedResult = "Number one";
		
		assertEquals(expectedResult, result);
	}
	*/
	@Test
	public void ifElseTest3() 
	{
		IfElse ie = new IfElse();
		String result = ie.ifElseMethod(2);
		String expectedResult = "Number 2";
		
		assertEquals(expectedResult, result);
	}
	
	
	
//	@Test
//	public void tryCatchTest1() 
//	{
//		TestClass_ControlFlow tccf = new TestClass_ControlFlow();
//		assertTrue(tccf.tryCatchMethod_try());
//	}
	
//	@Test
//	public void tryCatchTest2() 
//	{
//		TestClass_ControlFlow tccf = new TestClass_ControlFlow();
//		assertFalse(tccf.tryCatchMethod_catch());
//	}
	
	
	@Test
	public void switchCaseTest() 
	{
		TestClass_ControlFlow tccf = new TestClass_ControlFlow();
		
		assertEquals('E', tccf.switchCaseMethod('�'));
	}
	
}
