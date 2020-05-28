package againstRequirements;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;


/**
 * Tests what happens if the requirements are not met.
 */
public class AgainstRequirements {
	/**
	 * Test with curly bracket in the same line of last instruction. No test path
	 * will be computed.
	 */
	@Test
	public void testLastCurlyBracketInSameLine() {
		System.out.println("#####################################################################");
		System.out.println("                    testLastCurlyBracketInSameLine                   ");
		System.out.println("#####################################################################");

		AgainstRequirementsTestClass artc = new AgainstRequirementsTestClass();

		assertEquals(24, artc.factorial(4));
	}

	/**
	 * Test without {@link Test} annotation.
	 */
	public void testWithoutTestAnnotation() {
		System.out.println("#####################################################################");
		System.out.println("                       testWithoutTestAnnotation                     ");
		System.out.println("#####################################################################");

		AgainstRequirementsTestClass artc = new AgainstRequirementsTestClass();

		assertEquals(24, artc.factorial(4));
	}
	
	/**
	 * Test a method defined within the test class. An error will be generated.
	 * Execution will continue if there are more test methods to be executed.
	 */
	@Test
    public void methodInSameFileOfTestMethod() {
		System.out.println("#####################################################################");
		System.out.println("                       methodInSameFileOfTestMethod                  ");
		System.out.println("#####################################################################");
		
		int a = 2, b = 3;
		
        Assert.assertEquals(5, sum(a, b));
    }
    
	/**
	 * Example method for {@link #methodInSameFileOfTestMethod()} method.
	 * 
	 * @param a A number
	 * @param b Another number
	 * @return a + b
	 */
    private int sum(int a, int b) 
    {
        return a + b;
    }
}