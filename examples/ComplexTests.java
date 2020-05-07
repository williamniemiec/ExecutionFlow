import static org.junit.Assert.assertEquals;



import org.junit.Test;

import againstRequirements.AgainstRequirementsTestClass;
import testClasses.TestClass;
import testClasses.TestClass2;


/**
 * Test methods that did not work previously.
 */
public class ComplexTests 
{
	/**
	 * Test method with the creation of constructors in a for loop.
	 */
	/*
	@Test
	public void testForConstructor()
	{
		System.out.println("#####################################################################");
		System.out.println("                             testForConstructor                      ");
		System.out.println("#####################################################################");
		long before = 1;
		
		for (int i=1; i<=4; i++) {
			TestClass2 tc = new TestClass2(i);
			assertEquals(i*before, tc.factorial());
			
			before = tc.factorial();
		}
	}
	*/
	/**
	 * Test methods with more than one constructor.
	 */
	
	@Test
	public void moreOneConstructor()
	{
		System.out.println("#####################################################################");
		System.out.println("                             moreOneConstructor                      ");
		System.out.println("#####################################################################");
		
		AgainstRequirementsTestClass artc = new AgainstRequirementsTestClass();
		AgainstRequirementsTestClass artc2 = new AgainstRequirementsTestClass(true);
		
		assertEquals(-1, artc2.factorial(4));
		assertEquals(24, artc.factorial(4));
	}
	
	/**
	 * Test methods with more than one constructor and method.
	 */
	/*
	@Test
	public void moreOneConstructorAndMethod()
	{
		System.out.println("#####################################################################");
		System.out.println("                     moreOneConstructorAndMethod                     ");
		System.out.println("#####################################################################");
		
		AgainstRequirementsTestClass artc = new AgainstRequirementsTestClass();
		AgainstRequirementsTestClass artc2 = new AgainstRequirementsTestClass(true);
		
		assertEquals(-1, artc2.factorial(4));
		assertEquals(24, artc.factorial(4));
		
		assertEquals(24, AgainstRequirementsTestClass.staticFactorial(4));
	}
	*/
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
		TestClass tc = new TestClass(10);
		AgainstRequirementsTestClass artc = new AgainstRequirementsTestClass();
		
		long resp = tc.fibonacci(num);
		resp = artc.factorial(resp);
		
		assertEquals(expectedResult, resp);
	}
	
}
