package junit5;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import executionFlow.runtime.SkipCollection;
import testClasses.TestClass;


/**
 * Tests that use the {@link Test} annotation belonging to JUnit 5.
 */
@SkipCollection	// Just for now
public class TestAnnotation 
{
	@Test
	public void test1()
	{
		TestClass tc = new TestClass(4);
		assertEquals(24, tc.factorial(4));
	}
}
