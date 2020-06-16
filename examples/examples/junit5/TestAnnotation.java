package examples.junit5;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import examples.testClasses.TestClass;


/**
 * Tests that use the {@link Test} annotation belonging to JUnit 5.
 */
public class TestAnnotation 
{
	private long x;
	
	@BeforeEach
	public void bef()
	{
		TestClass tc = new TestClass(4);
		x = tc.factorial(3);
		//System.out.println("BeforeEach");
	}
	
	@Test
	public void test1()
	{
		TestClass tc = new TestClass(4);
		assertEquals(24, tc.factorial(4));
	}
}
