package executionFlow.core.file.parser.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import examples.testClasses.TestClass;

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
		assertEquals("nine", 
				tc.threePaths(3));		// It will fail
		assertEquals("two", tc.threePaths(2));
	}
	
	@Test
	public void multilineAssert1()
	{
		assertEquals(param1, 			// Comment 1
				param2,					// Comment 2
				param3);				// Comment 3
	}
	
	@Test
	public void multilineAssert2()
	{
		assertEquals (
				param1, 				// Comment 1
				param2,					// Comment 2
				param3);				// Comment 3
	}
	
	@Test
	public void multilineAssert3()
	{
		assertEquals (
				param1, 				// Comment 1
				param2,					// Comment 2
				param3					// Comment 3
		);								// Comment 4
	}
}
