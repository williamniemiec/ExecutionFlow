package examples.againstRequirements;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;


/**
 * Tests what happens if the requirements are not met.
 */
public class AgainstRequirements 
{

	/**
	 * Test without {@link Test} annotation.
	 */
	public void testWithoutTestAnnotation() {
		AgainstRequirementsTestClass artc = new AgainstRequirementsTestClass();

		assertEquals(24, artc.factorial(4));
	}
}