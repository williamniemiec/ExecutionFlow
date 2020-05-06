import static org.junit.Assert.assertEquals;

import org.junit.Test;

import controlFlow.IfElse;


public class SimpleTest_IfElse 
{
	@Test
	public void testIfElse() 
	{
		IfElse ie = new IfElse();
		
		assertEquals("Number 2", ie.ifElseMethod(2));
	}
}
