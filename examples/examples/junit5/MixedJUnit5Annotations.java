package examples.junit5;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import examples.others.auxClasses.AuxClass;


/**
 * Tests that use the following JUnit 5 annotations:
 * <ul>
 * 	<li>{@link Test}</li>
 * 	<li>{@link RepeatedTest}</li>
 * 	<li>{@link ParameterizedTest}</li>
 * </ul>
 */
public class MixedJUnit5Annotations 
{
	@BeforeEach
	public void bef()
	{
		System.out.println("BeforeEach");
	}
	
	@Test
	public void testAnnotation()
	{
		AuxClass tc = new AuxClass(4);
		
		
		assertEquals(24, tc.factorial(4));
	}
	
	@ParameterizedTest
	@ValueSource(ints = {-1,0,1})
	public void parameterizedTestAnnotation(int num)
	{
		AuxClass tc = new AuxClass(4);


		assertEquals(1, tc.factorial(num));
	}
	
	@RepeatedTest(2)
	@DisplayName(RepeatedTest.LONG_DISPLAY_NAME)
	public void repeatedTestAnnotation()
	{
		AuxClass tc = new AuxClass(4);
		
		
		assertEquals(24, tc.factorial(4));
	}
}
