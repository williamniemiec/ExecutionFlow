package executionFlow.runtime;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import executionFlow.runtime.testClasses.TestClass;


public class MultipleTestPaths 
{
	@Test
	public void ThreeTestPathsTest() 
	{
		TestClass tc = new TestClass(99);
		assertEquals("one", tc.threePaths(1));
		assertEquals("two", tc.threePaths(2));
		assertEquals("", tc.threePaths(3));
	}
}
