package examples.methodCalledByTestedInvoked;

import org.junit.Test;


/**
 * Tests export of invoked methods by tested invoker.
 */
public class MethodCalledByTestedInvoked_Test 
{
	/**
	 * T():	Test method
	 * A():	Tested invoker (method)
	 * B(), C():	Invoked methods by tested invoker
	 */
	@Test
	public void T()
	{
		MethodCalledByTestedInvoked_Class testClass = new MethodCalledByTestedInvoked_Class(false);
		testClass.A();	
	}
	
	/**
	 * T2():	Test method
	 * A():	Tested invoker (invoker)
	 * C(), D():	Invoked methods by tested invoker
	 */
	@Test
	@SuppressWarnings("unused")
	public void T2()
	{
		MethodCalledByTestedInvoked_Class testClass = new MethodCalledByTestedInvoked_Class(true);
	}
}
