package complexTests;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import againstRequirements.AgainstRequirementsTestClass;
import testClasses.TestClass;


public class ComplexTests 
{
	/**
	 * Test method with the creation of constructors in a for loop.
	 */
	@Test
	public void testForConstructor()
	{
		System.out.println("#####################################################################");
		System.out.println("                             testForConstructor                      ");
		System.out.println("#####################################################################");
		long before = 1;
		
		for (int i=1; i<=4; i++) {
			TestClass_ComplexTests tc = new TestClass_ComplexTests(i);
			assertEquals(i*before, tc.factorial_constructor());
			
			before = tc.factorial_constructor();
		}
	}

	/**
	 * Test methods with more than one constructor.
	 */
	@Test 
	public void moreOneConstructor()
	{
		System.out.println("#####################################################################");
		System.out.println("                             moreOneConstructor                      ");
		System.out.println("#####################################################################");
		
		TestClass_ComplexTests tcct = new TestClass_ComplexTests();
		TestClass_ComplexTests tcct2 = new TestClass_ComplexTests(true);
		
		assertEquals(-1, tcct2.factorial(4));
		assertEquals(24, tcct.factorial(4));
	}
	
	/**
	 * Test methods with more than one constructor and method.
	 */
	@Test
	public void moreOneConstructorAndMethod()
	{
		System.out.println("#####################################################################");
		System.out.println("                     moreOneConstructorAndMethod                     ");
		System.out.println("#####################################################################");
		
		TestClass_ComplexTests tcct = new TestClass_ComplexTests();
		TestClass_ComplexTests tcct2 = new TestClass_ComplexTests(true);
		
		assertEquals(-1, tcct2.factorial(4)); 
		assertEquals(24, tcct.factorial(4));
		
		assertEquals(24, TestClass_ComplexTests.staticFactorial(4));
	}
	
	/**
	 * Test methods with auxiliary methods.
	 */
	@Test
	public void testMethodWithAuxMethods() 
	{
		System.out.println("#####################################################################");
		System.out.println("                       testMethodWithAuxMethods                      ");
		System.out.println("#####################################################################");
		
		int num = 4;
		long expectedResult = 6;
		
		// Calculates factorial of n-th fibonacci term
		TestClass_ComplexTests tcct = new TestClass_ComplexTests(10);
		TestClass_ComplexTests tcct2 = new TestClass_ComplexTests();
		
		long resp = tcct.fibonacci(num);
		resp = tcct2.factorial(resp);
		
		assertEquals(expectedResult, resp);
	}
	
	/**
	 * Tests test methods that test more than one method.
	 */
	@Test
	public void testingMultipleMethods()
	{
		System.out.println("#####################################################################");
		System.out.println("                          testingMultipleMethods                     ");
		System.out.println("#####################################################################");
		TestClass_ComplexTests tcct = new TestClass_ComplexTests();
		
		assertEquals(24, tcct.factorial(4));
		assertEquals(3, tcct.fibonacci(4));
	}
}
