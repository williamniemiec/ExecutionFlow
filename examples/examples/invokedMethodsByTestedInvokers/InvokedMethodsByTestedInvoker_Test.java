package examples.invokedMethodsByTestedInvokers;

import org.junit.Test;


/**
 * Tests export of invoked methods by tested invoker.
 */
public class InvokedMethodsByTestedInvoker_Test 
{
	/**
	 * T():	Test method
	 * A():	Tested invoker (method)
	 * B(), C():	Invoked methods by tested invoker
	 */
	@Test
	public void T()
	{
		InvokedMethodsByTestedInvoker_Class testClass = new InvokedMethodsByTestedInvoker_Class(false);
		testClass.A();	
	}
	
	/**
	 * T2():	Test method
	 * A():	Tested invoker (invoker)
	 * C(), D():	Invoked methods by tested invoker
	 */
	@SuppressWarnings("unused")
	@Test
	public void T2()
	{
		InvokedMethodsByTestedInvoker_Class testClass = new InvokedMethodsByTestedInvoker_Class(true);
	}
}
