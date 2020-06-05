package executionFlow.core.file.parser.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import testClasses.TestClass;

public class TestClass 
{
	@Test
	public void testFoo()
	{
try {		assertEquals("1", "1");} catch(org.junit.ComparisonFailure e){executionFlow.ConsoleOutput.showWarning("AssertFail("+e.getStackTrace()[2].getMethodName()+") - "+e.getMessage());}
try {		assertEquals("1", "2");	} catch(org.junit.ComparisonFailure e){executionFlow.ConsoleOutput.showWarning("AssertFail("+e.getStackTrace()[2].getMethodName()+") - "+e.getMessage());}// It will fail
	}
	
	@Test
	public void assertFailInTheMiddleTest() 
	{
		TestClass tc = new TestClass(99);
try {		assertEquals("one", tc.threePaths(1));} catch(org.junit.ComparisonFailure e){executionFlow.ConsoleOutput.showWarning("AssertFail("+e.getStackTrace()[2].getMethodName()+") - "+e.getMessage());}
try {		assertEquals("nine", tc.threePaths(3));		} catch(org.junit.ComparisonFailure e){executionFlow.ConsoleOutput.showWarning("AssertFail("+e.getStackTrace()[2].getMethodName()+") - "+e.getMessage());}// It will fail
try {		assertEquals("two", tc.threePaths(2));} catch(org.junit.ComparisonFailure e){executionFlow.ConsoleOutput.showWarning("AssertFail("+e.getStackTrace()[2].getMethodName()+") - "+e.getMessage());}
	}
	
	@Test
	public void testFoo2()
	{
		
	}
}
