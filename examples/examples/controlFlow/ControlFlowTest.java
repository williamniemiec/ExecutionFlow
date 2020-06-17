package examples.controlFlow;

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
		
try {		assertEquals(expectedResult, result);} catch(Throwable e){}
	}

	@Test
	public void ifElseTest() 
	{
		TestClass_ControlFlow tccf = new TestClass_ControlFlow();
		String result = tccf.ifElseMethod(0);
		String expectedResult = "Number zero";
		
try {		assertEquals(expectedResult, result);} catch(Throwable e){}
	}
	
	@Test
	public void ifElseTest2() 
	{
		TestClass_ControlFlow tccf = new TestClass_ControlFlow();
		String result = tccf.ifElseMethod(1);
		String expectedResult = "Number one";
		
try {		assertEquals(expectedResult, result);} catch(Throwable e){}
	}
	
	@Test
	public void ifElseTest3() 
	{
		TestClass_ControlFlow tccf = new TestClass_ControlFlow();
		String result = tccf.ifElseMethod(2);
		String expectedResult = "Number 2";
		
try {		assertEquals(expectedResult, result);} catch(Throwable e){}
	}
	
	@Test
	public void tryCatchTest1() 
	{
		TestClass_ControlFlow tccf = new TestClass_ControlFlow();
try {		assertTrue(tccf.tryCatchMethod_try());} catch(Throwable e){}
	}

	@Test
	public void tryCatchTest2() 
	{
		TestClass_ControlFlow tccf = new TestClass_ControlFlow();
try {		assertFalse(tccf.tryCatchMethod_catch());} catch(Throwable e){}
	}
	
	@Test
	public void switchCaseTest() 
	{
		TestClass_ControlFlow tccf = new TestClass_ControlFlow();
		
try {		assertEquals('E', tccf.switchCaseMethod('É'));} catch(Throwable e){}
	}

	@Test
	public void doWhileTest()
	{
		TestClass_ControlFlow tccf = new TestClass_ControlFlow();
		
try {		assertEquals(7, tccf.doWhileMethod(2,5));} catch(Throwable e){}
	}

	@Test
	public void inlineWhile()
	{
		TestClass_ControlFlow tccf = new TestClass_ControlFlow();
		
try {		assertEquals(0, tccf.inlineWhile(2));} catch(Throwable e){}
	}
	
	@Test
	public void inlineDoWhile()
	{
		TestClass_ControlFlow tccf = new TestClass_ControlFlow();
		
try {		assertEquals(0, tccf.inlineDoWhile(2));} catch(Throwable e){}
	}
	
	@Test
	public void inlineIfElse()
	{
		TestClass_ControlFlow tccf = new TestClass_ControlFlow();
		
try {		assertTrue(tccf.inlineIfElse(2));} catch(Throwable e){}
	}
}
