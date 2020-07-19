package examples.others;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import examples.complexTests.TestClass_ComplexTests;
import examples.others.auxClasses.AuxClass;


/**
 * Tests various methods
 */
public class OthersTest 
{
//	@Test
//	public void testEmptyTest()
//	{
//		AuxClass tc = new AuxClass(4);
//		tc.test2();
//	}
//
//	@Test
//	public void testFactorial() 
//	{
//		int num = 4;
//		long expectedResult = 24;
//		
//		AuxClass tc = new AuxClass(4);
//		long res = tc.factorial(num);
//		
//		assertEquals(expectedResult, res);
//	}
//	
//	@Test
//	public void testFactorial_zero() 
//	{
//		int num = 0;
//		long expectedResult = 1;
//		
//		AuxClass tc = new AuxClass(4);
//		long res = tc.factorial(num);
//		
//		assertEquals(expectedResult, res);
//	}
//	
//	@Test
//	public void testFibonacci() 
//	{
//		int num = 5;
//		long expectedResult = 5; 
//		
//		AuxClass tc = new AuxClass(4);
//		long res = tc.fibonacci(num);
//		
//		assertEquals(expectedResult, res);
//	}
//	
//	@Test
//	public void testInternalCall()
//	{
//		char[] letters = "HELLOworld".toCharArray();
//		char[] parsedLetters = AuxClass.parseLetters_withInternalCall(letters);
//		char[] expectedResult = "helloWORLD".toCharArray();
//		
//		assertArrayEquals(expectedResult, parsedLetters);
//	}
//	
//	@Test
//	public void testStaticMethod_charSequence()
//	{
//		char[] parsedLetters = AuxClass.parseLetters_noInternalCall("HELLOworld".subSequence(0, 10));
//		char[] expectedResult = "helloWORLD".toCharArray();
//		
//		assertArrayEquals(expectedResult, parsedLetters);
//	}
//	
//	@Test
//	public void testParamSignature_object()
//	{
//		AuxClass tc = new AuxClass(2);
//		int hashCode = tc.testObjParam("test");
//		
//		assertEquals(3556498, hashCode);
//	}
//	
//	/**
//	 * Test methods with auxiliary methods.
//	 */
//	@Test
//	public void testMethodWithAuxMethods() 
//	{
//		int num = 4;
//		long expectedResult = 6;
//		
//		// Calculates factorial of n-th fibonacci term
//		AuxClass tcct = new AuxClass(10);
//		AuxClass tcct2 = new AuxClass(12);
//		
//		int resp = (int)tcct.fibonacci(num);
//		resp = (int)tcct2.factorial(resp);
//		
//		assertEquals(expectedResult, resp);
//	}
//	
//	/**
//	 * Tests test methods that test more than one method.
//	 */
//	@Test
//	public void testingMultipleMethods()
//	{
//		AuxClass tcct = new AuxClass(1);
//		
//		assertEquals(24, tcct.factorial(4));
//		assertEquals(3, tcct.fibonacci(4));
//	}
	
	@Test
	public void onlyOneMethod()
	{
		AuxClass tcct = new AuxClass(1);
		
		assertEquals(24, tcct.getNumber());
	}
}
