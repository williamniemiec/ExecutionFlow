package executionFlow.core.file.parser.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import testClasses.TestClass;

public class TestClass 
{
	@Test
	public void testFoo()
	{
		assertEquals("1", "1");
		assertEquals("1", "2");	// It will fail
	}
	
	@Test
	public void assertFailInTheMiddleTest() 
	{
		TestClass tc = new TestClass(99);
		assertEquals("one", tc.threePaths(1));
		assertEquals("nine", tc.threePaths(3));		// It will fail
		assertEquals("two", tc.threePaths(2));
	}
	
	@Test
	public void testFoo2()
	{
		
	}
}
