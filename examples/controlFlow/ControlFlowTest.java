package controlFlow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


/**
 * Tests test path computation in control flow structures.
 */
public class ControlFlowTest 
{
	@Test
	public void ifElseTest_earlyReturn() 
	{
		TestClass_ControlFlow tccf = new TestClass_ControlFlow();
		String result = tccf.ifElseMethod(-1);
		String expectedResult = "Negative value";
		
		assertEquals(expectedResult, result);
	}

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
	@Test
	public void ifElseTest3() 
	{
		TestClass_ControlFlow tccf = new TestClass_ControlFlow();
		String result = tccf.ifElseMethod(2);
		String expectedResult = "Number 2";
		
		assertEquals(expectedResult, result);
	}

	@Test
	public void tryCatchTest1() 
	{
		TestClass_ControlFlow tccf = new TestClass_ControlFlow();
		assertTrue(tccf.tryCatchMethod_try());
	}

	@Test
	public void tryCatchTest2() 
	{
		TestClass_ControlFlow tccf = new TestClass_ControlFlow();
		assertFalse(tccf.tryCatchMethod_catch());
	}
	
	@Test
	public void switchCaseTest() 
	{
		TestClass_ControlFlow tccf = new TestClass_ControlFlow();
		
		assertEquals('E', tccf.switchCaseMethod('É'));
	}

	@Test
	public void doWhileTest()
	{
		TestClass_ControlFlow tccf = new TestClass_ControlFlow();
		
		assertEquals(7, tccf.doWhileMethod(2,5));
	}

	@Test
	public void inlineWhile()
	{
		TestClass_ControlFlow tccf = new TestClass_ControlFlow();
		
		assertEquals(0, tccf.inlineWhile(2));
	}
	
	@Test
	public void inlineDoWhile()
	{
		TestClass_ControlFlow tccf = new TestClass_ControlFlow();
		
		assertEquals(0, tccf.inlineDoWhile(2));
	}
	
	@Test
	public void inlineIfElse()
	{
		TestClass_ControlFlow tccf = new TestClass_ControlFlow();
		
		assertTrue(tccf.inlineIfElse(2));
	}
}
