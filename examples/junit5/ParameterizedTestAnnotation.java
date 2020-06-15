package junit5;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import executionFlow.runtime.SkipMethod;
import testClasses.TestClass;


/**
 * Tests that use the {@link ParameterizedTest} annotation belonging to 
 * JUnit 5.
 */
public class ParameterizedTestAnnotation 
{
	//@SkipMethod
	@ParameterizedTest
	@ValueSource(ints = {-1,0,1})
	public void test1(int num)
	{
		TestClass tc = new TestClass(4);
		assertEquals(-1, tc.factorial(num));
		assertEquals(1, tc.factorial(num));
	}
}
