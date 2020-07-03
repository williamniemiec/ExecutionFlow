package examples.junit5;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

import examples.others.auxClasses.AuxClass;


/**
 * Tests that use the {@link RepeatedTest} annotation belonging to JUnit 5.
 */
public class RepeatedTestAnnotation 
{
	@RepeatedTest(2)
	@DisplayName(RepeatedTest.LONG_DISPLAY_NAME)
	public void test1()
	{
		AuxClass tc = new AuxClass(4);
		
		
		assertEquals(24, tc.factorial(4));
	}
}
