package examples;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import examples.testClasses.TestClass;


/**
 * Tests that contains at least one assert that fails.
 */
public class AssertFailTest 
{
	/*
	@Test
	public void assertFailAtTheEndTest() 
	{
		TestClass tc = new TestClass(99);
		assertEquals("one", tc.threePaths(1));
		assertEquals("two", tc.threePaths(2));
		//try {assertEquals("nine", tc.threePaths(3));} catch(Throwable e) {executionFlow.ConsoleOutput.showWarning("AssertFail("+e.getStackTrace()[2].getMethodName()+") - "+e.getMessage());}		// It will fail
	}*/
	
	@Test
	public void assertFailInTheMiddleTest() 
	{
		TestClass tc = new TestClass(99);
		//assertEquals("one", tc.threePaths(1));
		assertEquals("nine", tc.threePaths(3));		// It will fail
		assertEquals("two", tc.threePaths(2));
	}
	
	@Test
	public void t()
	{
		TestClass tc = new TestClass(99);
		try {
			assertEquals("one", tc.threePaths(1));
		} catch(Throwable t) {}
		try {
			assertEquals("o3213ne", tc.threePaths(4));
		} catch(Throwable t) {}
		try {
			assertEquals("two", tc.threePaths(2));
		} catch(Throwable t) {}
	}
}
