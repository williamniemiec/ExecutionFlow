package examples.polymorphism;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


/**
 * Tests methods that use polymorphism and inheritance.
 */
public class PolymorphismTest 
{
	/**
	 * Tests method that takes as an argument an object that uses inheritance.
	 */
	@Test
	public void testParam()
	{
		boolean resp = ClassInterface.testClassParam(new ClassInterface(""));
		
		assertTrue(resp);
	}
	
	/**
	 * Tests method that uses polymorphism.
	 */
	@Test
	public void testInterface()
	{
		Interface ci = new ClassInterface("Hello world");
		
		assertEquals("Hello world", ci.interfaceMethod());
	}
}
