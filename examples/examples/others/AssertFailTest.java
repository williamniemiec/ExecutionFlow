package examples.others;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import examples.others.auxClasses.AuxClass;


/**
 * Tests that contains at least one assert that fails.
 */
public class AssertFailTest 
{
	@Test
	public void assertFailAtTheEndTest() 
	{
		AuxClass tc = new AuxClass(99);
		
		
		assertEquals("one", tc.threePaths(1));
		assertEquals("two", tc.threePaths(2));
		assertEquals("nine", tc.threePaths(3));		// It will fail
	}
	
	@Test
	public void assertFailInTheMiddleTest() 
	{
		AuxClass tc = new AuxClass(99);
		
		
		assertEquals("one", tc.threePaths(1));
		assertEquals("nine", tc.threePaths(3));		// It will fail
		assertEquals("two", tc.threePaths(2));
	}
}
