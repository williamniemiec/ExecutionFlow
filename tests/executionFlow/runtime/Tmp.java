package executionFlow.runtime;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import executionFlow.runtime.testClasses.TestClass2;

public class Tmp {
	@Test
	public void testForConstructor()
	{
		System.out.println("#####################################################################");
		System.out.println("                             testForConstructor                      ");
		System.out.println("#####################################################################");
		long before = 1;
		
			TestClass2 tc = new TestClass2(2);
			assertEquals(2*before, tc.factorial());
			
			before = tc.factorial();
	}
}
