import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import testClasses.TestClass;


/**
 * Tests that have multiple test paths that belong to the same method signature.
 */
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
