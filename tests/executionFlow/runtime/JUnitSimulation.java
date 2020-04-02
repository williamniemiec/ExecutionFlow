package executionFlow.runtime;

import static org.junit.Assert.assertEquals;
import org.junit.Test;


/**
 * Class created to show the application working with
 * JUnit test case in real time.
 * 
 * @apiNote RuntimeCollector.aj has to be in the same package 
 */
public class JUnitSimulation 
{
	@Test
	public void testFactorial() 
	{
		int num = 4;
		long expectedResult = 24; 
		TestClass tc = new TestClass(4);
		long res = tc.factorial(num);
		
		assertEquals(expectedResult, res);
	}
}
