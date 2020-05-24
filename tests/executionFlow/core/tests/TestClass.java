package executionFlow.core.tests;

import org.junit.Test;

public class TestClass 
{
	@Test
	public void testFoo()
	{
		
	}
	
	@Test @executionFlow.runtime.SkipMethod
	public void testFoo2()
	{
		
	}
}
