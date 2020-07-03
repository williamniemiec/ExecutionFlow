package examples.complexTests;
import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class ComplexTests 
{
	/**
	 * Test method with the creation of constructors in a for loop.
	 */
	@Test
	public void testForConstructorAndMethod()
	{
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
		TestClass_ComplexTests tcct = new TestClass_ComplexTests();
		TestClass_ComplexTests tcct2 = new TestClass_ComplexTests(true);
		
		assertEquals(24, tcct.factorial(4));
		assertEquals(-1, tcct2.factorial(4));
	}
	
	/**
	 * Test methods with more than one constructor and a static method.
	 */
	@Test
	public void moreOneConstructorAndStaticMethod()
	{
		TestClass_ComplexTests tcct = new TestClass_ComplexTests();
		TestClass_ComplexTests tcct2 = new TestClass_ComplexTests(true);
		
		assertEquals(-1, tcct2.factorial(4)); 
		assertEquals(24, tcct.factorial(4));
		
		assertEquals(24, TestClass_ComplexTests.staticFactorial(4));
	}
}
