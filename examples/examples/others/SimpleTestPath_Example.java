package examples.others;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import examples.others.auxClasses.AuxClass;


public class SimpleTestPath_Example 
{
	@Test
	public void simpleTestPath() 
	{
		int num = 4;
		long expectedResult = 24;
		
		AuxClass tc = new AuxClass(4);
		long res = tc.factorial(num);
		
		assertEquals(expectedResult, res);
	}
}
