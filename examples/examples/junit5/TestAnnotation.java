package examples.junit5;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import examples.others.auxClasses.AuxClass;


/**
 * Tests that use the {@link Test} annotation belonging to JUnit 5.
 */
public class TestAnnotation 
{
	@BeforeEach
	public void bef()
	{
		System.out.println("BeforeEach");
	}
	
	@Test
	public void test1()
	{
		AuxClass tc = new AuxClass(4);
		
		
		assertEquals(24, tc.factorial(4));
	}
}
