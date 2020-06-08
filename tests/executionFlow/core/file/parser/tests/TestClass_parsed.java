package executionFlow.core.file.parser.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import testClasses.TestClass;

public class TestClass 
{
	@Test
	public void testFoo()
	{
try {		assertEquals("1", "1");} catch(org.junit.ComparisonFailure e){}
try {		assertEquals("1", "2");	} catch(org.junit.ComparisonFailure e){}// It will fail
	}
	
	@Test
try {	public void assertFailInTheMiddleTest() } catch(org.junit.ComparisonFailure e){}
	{
		TestClass tc = new TestClass(99);
try {		assertEquals("one", tc.threePaths(1));} catch(org.junit.ComparisonFailure e){}
try {		assertEquals("nine", 
				tc.threePaths(3));} catch(org.junit.ComparisonFailure e){}		// It will fail
try {		assertEquals("two", tc.threePaths(2));} catch(org.junit.ComparisonFailure e){}
	}
	
	@Test
	public void multilineAssert1()
	{
try {		assertEquals(param1, 			// Comment 1
				param2,					// Comment 2
				param3);} catch(org.junit.ComparisonFailure e){}				// Comment 3
	}
	
	@Test
	public void multilineAssert2()
	{
try {		assertEquals (
				param1, 				// Comment 1
				param2,					// Comment 2
				param3);} catch(org.junit.ComparisonFailure e){}				// Comment 3
	}
	
	@Test
	public void multilineAssert3()
	{
try {		assertEquals (
				param1, 				// Comment 1
				param2,					// Comment 2
				param3					// Comment 3
		);} catch(org.junit.ComparisonFailure e){}								// Comment 4
	}
}
