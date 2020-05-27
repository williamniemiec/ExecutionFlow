package againstRequirements;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


/**
 * Tests what happens if the requirements are not met.
 */
public class AgainstRequirements 
{
	/**
	 * Test with curly bracket in the same line of last instruction.
	 */
	
	
	/**
	 * Test without {@link Test} annotation.
	 */
	public void testWithoutTestAnnotation()
	{
		System.out.println("#####################################################################");
		System.out.println("                       testWithoutTestAnnotation                     ");
		System.out.println("#####################################################################");
		
		AgainstRequirementsTestClass artc = new AgainstRequirementsTestClass();
		
		assertEquals(24, artc.factorial(4));
	}
}
