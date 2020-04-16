package executionFlow.runtime;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import executionFlow.runtime.testClasses.ClassInterface;
import executionFlow.runtime.testClasses.Interface;
import executionFlow.runtime.testClasses.TestClass;


/**
 * Class created to show the application working with
 * JUnit test case in real time.
 */
public class JUnitSimulation 
{
	@Test
	public void testEmptyTest()
	{
		System.out.println("#####################################################################");
		System.out.println("                             testEmptyTest                           ");
		System.out.println("#####################################################################");
		
		TestClass tc = new TestClass(4);
		tc.test2();
	}
	
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
	public void testFactorial_zero() 
	{
		System.out.println("#####################################################################");
		System.out.println("                           testFactorial_zero                       ");
		System.out.println("#####################################################################");
		
		int num = 0;
		long expectedResult = 1;
		
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
	
	@Test
	public void testInterface()
	{
		System.out.println("#####################################################################");
		System.out.println("                             testInterface                           ");
		System.out.println("#####################################################################");
		
		Interface ci = new ClassInterface("Hello world");
		
		assertEquals("Hello world", ci.test());
	}
	
	@Test
	public void testStaticMethod_charSequence()
	{
		System.out.println("#####################################################################");
		System.out.println("                    testStaticMethod_charSequence                    ");
		System.out.println("#####################################################################");
		
		char[] parsedLetters = TestClass.parseLetters_noInternalCall("HELLOworld".subSequence(0, 10));
		char[] expectedResult = "helloWORLD".toCharArray();
		
		assertArrayEquals(expectedResult, parsedLetters);
	}
	
	@Test
	public void testParamSignature_object()
	{
		System.out.println("#####################################################################");
		System.out.println("                      testParamSignature_object                      ");
		System.out.println("#####################################################################");
		
		TestClass tc = new TestClass(2);
		int hashCode = tc.testObjParam("test");
		
		assertEquals(3556498, hashCode);
	}
	
	@Test
	public void testParamSignature_createdObject()
	{
		System.out.println("#####################################################################");
		System.out.println("                      testParamSignature_createdObject               ");
		System.out.println("#####################################################################");
		
		TestClass tc = new TestClass(2);
		boolean resp = tc.testClassParam(new ClassInterface(""));
		
		assertTrue(resp);
	}
}
