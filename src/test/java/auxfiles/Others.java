package auxfiles;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import auxfiles.polymorphism.ClassInterface;

/**
 * Some comment
 */
public class Others
{
	@Test
	public void testEmptyTest()
	{
		AuxClass ac = new AuxClass(4);
		ac.test2();
	}

	@Test
	public void testFactorial() 
	{
		int num = 4;
		long expectedResult = 24;
		
		AuxClass ac = new AuxClass(4);
		long res = ac.factorial(num);
		
		assertEquals(expectedResult, res);
	}
	
	@Test
	public void testFactorial_zero() 
	{
		int num = 0;
		long expectedResult = 1;
		
		AuxClass ac = new AuxClass(4);
		long res = ac.factorial(num);
		
		assertEquals(expectedResult, res);
	}
	
	@Test
	public void testFibonacci() 
	{
		int num = 5;
		long expectedResult = 5; 
		
		AuxClass ac = new AuxClass(4);
		long res = ac.fibonacci(num);
		
		assertEquals(expectedResult, res);
	}
	
	@Test
	public void testInternalCall()
	{
		char[] letters = "HELLOworld".toCharArray();
		char[] parsedLetters = AuxClass.parseLetters_withInternalCall(letters);
		char[] expectedResult = "helloWORLD".toCharArray();
		
		assertArrayEquals(expectedResult, parsedLetters);
	}
	
	@Test
	public void testStaticMethod_charSequence()
	{
		char[] parsedLetters = AuxClass.parseLetters_noInternalCall("HELLOworld".subSequence(0, 10));
		char[] expectedResult = "helloWORLD".toCharArray();
		
		assertArrayEquals(expectedResult, parsedLetters);
	}
	
	@Test
	public void testParamSignature_object()
	{
		AuxClass ac = new AuxClass(2);
		int hashCode = ac.testObjParam("test");
		
		assertEquals(3556498, hashCode);
	}
	
	/**
	 * Test methods with auxiliary methods.
	 */
	@Test
	public void testMethodWithAuxMethods() 
	{
		int num = 4;
		long expectedResult = 6;
		
		// Calculates factorial of n-th fibonacci term
		AuxClass ac = new AuxClass(10);
		AuxClass ac2 = new AuxClass(12);
		
		int resp = (int)ac.fibonacci(num);
		resp = (int)ac2.factorial(resp);
		
		assertEquals(expectedResult, resp);
	}
	
	/**
	 * Tests test methods that test more than one method.
	 */
	@Test
	public void testingMultipleMethods()
	{
		AuxClass ac = new AuxClass(1);
		
		assertEquals(24, ac.factorial(4));
		assertEquals(3, ac.fibonacci(4));
	}
	
	@Test
	public void onlyOneMethod()
	{
		AuxClass ac = new AuxClass(1);
		
		assertEquals(1, ac.getNumber());
	}
	
	@Test
	public void anonymousObjectReturn()
	{
		AuxClass ac = new AuxClass(1);
		
		assertTrue(ac.anonymousObjectReturn() instanceof ClassInterface);
		
		ac.anonymousObjectReturn().interfaceMethod(); 
	}
}
