import static org.junit.Assert.assertEquals;

import org.junit.Test;

import testClasses.TestClass;


public class SimpleTest 
{
	@Test @executionFlow.runtime._SkipMethod
	public void testFactorial() 
	{
		int num = 4;
		long expectedResult = 24;
		
		TestClass tc = new TestClass(4);
		long res = tc.factorial(num);
		
		assertEquals(expectedResult, res);
	}
}
