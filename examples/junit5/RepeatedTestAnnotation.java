package junit5;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

import executionFlow.runtime.SkipCollection;
import testClasses.TestClass;


/**
 * Tests that use the {@link RepeatedTest} annotation belonging to JUnit 5.
 */
@SkipCollection	// Just for now
public class RepeatedTestAnnotation 
{
	@RepeatedTest(5)
	@DisplayName(RepeatedTest.LONG_DISPLAY_NAME)
	public void test1()
	{
		TestClass tc = new TestClass(4);
		assertEquals(24, tc.factorial(4));
	}
}
