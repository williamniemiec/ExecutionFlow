package examples.junit5;


import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import examples.others.auxClasses.AuxClass;


/**
 * Tests that use the {@link ParameterizedTest} annotation belonging to 
 * JUnit 5.
 */
public class ParameterizedTestAnnotation 
{
	@ParameterizedTest
	@ValueSource(ints = {-1,0,1})
	public void test1(int num)
	{
		AuxClass tc = new AuxClass(4);


		assertEquals(1, tc.factorial(num));
	}
}
