package executionFlow.runtime;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Test;


/**
 * Class created to show the application working with
 * JUnit test case in real time.
 */
public class JUnitSimulation 
{
	@Test
	public void testFactorial() 
	{
		System.out.println("#####################################################################");
		System.out.println("                             testFactorial                           ");
		System.out.println("#####################################################################");
		
		int num = 4;
		long expectedResult = 24;
		
		TestClass tc = new TestClass(4);
		long res = tc.factorial(num);
		
		assertEquals(expectedResult, res);
	}
	
	@Test
	public void testFibonacci() 
	{
		System.out.println("#####################################################################");
		System.out.println("                            testFibonacci                            ");
		System.out.println("#####################################################################");
		
		int num = 5;
		long expectedResult = 5; 
		
		TestClass tc = new TestClass(4);
		long res = tc.fibonacci(num);
		
		assertEquals(expectedResult, res);
	}
	
	@Test
	public void testStaticMethod()
	{
		System.out.println("#####################################################################");
		System.out.println("                          testStaticMethod                           ");
		System.out.println("#####################################################################");
		
		char[] letters = "HELLOworld".toCharArray();
		char[] parsedLetters = TestClass.parseLetters_noInternalCall(letters);
		char[] expectedResult = "helloWORLD".toCharArray();
		
		assertArrayEquals(expectedResult, parsedLetters);
	}
	
	@Test
	public void testInternalCall()
	{
		System.out.println("#####################################################################");
		System.out.println("                          testInternalCall                           ");
		System.out.println("#####################################################################");
		
		char[] letters = "HELLOworld".toCharArray();
		char[] parsedLetters = TestClass.parseLetters_withInternalCall(letters);
		char[] expectedResult = "helloWORLD".toCharArray();
		
		assertArrayEquals(expectedResult, parsedLetters);
	}
}
