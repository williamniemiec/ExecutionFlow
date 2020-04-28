package executionFlow.runtime.againstRequirements;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


/**
 * Tests what happens if the requirements are not met.
 */
public class AgainstRequirements 
{
	/**
	 * If more than one constructor is used to test a method, only the test
	 * path of the last one will be taken.
	 */
	@Test
	public void moreOneConstructor()
	{
		System.out.println("#####################################################################");
		System.out.println("                             moreOneConstructor                      ");
		System.out.println("#####################################################################");
		
		AgainstRequirementsTestClass artc = new AgainstRequirementsTestClass();
		AgainstRequirementsTestClass artc2 = new AgainstRequirementsTestClass(true);
		
		assertEquals(24, artc.factorial(4));
		assertEquals(-1, artc2.factorial(4));
	}
	
	/**
	 * If the test method tests more than one method, the test path will be 
	 * calculated only on the first.
	 */
	@Test
	public void testingMultipleMethods()
	{
		System.out.println("#####################################################################");
		System.out.println("                          testingMultipleMethods                     ");
		System.out.println("#####################################################################");
		AgainstRequirementsTestClass artc = new AgainstRequirementsTestClass();
		
		assertEquals(24, artc.factorial(4));
		assertEquals(3, artc.fibonacci(4));
	}
	
	public void testWithoutTestAnnotation()
	{
		System.out.println("#####################################################################");
		System.out.println("                       testWithoutTestAnnotation                     ");
		System.out.println("#####################################################################");
		
		AgainstRequirementsTestClass artc = new AgainstRequirementsTestClass();
		
		assertEquals(24, artc.factorial(4));
	}
}
