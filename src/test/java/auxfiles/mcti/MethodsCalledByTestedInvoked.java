package auxfiles.mcti;

import org.junit.Test;

public class MethodsCalledByTestedInvoked {
	
	/**
	 * T():	Test method
	 * A():	Tested invoker (method)
	 * B(), C():	Invoked methods by tested invoker
	 */
	@Test
	public void T()	{
		MethodsCalledByTestedInvokedAuxClass testClass = new MethodsCalledByTestedInvokedAuxClass(false);
		testClass.A();	
	}
	
	/**
	 * T2():	Test method
	 * A():	Tested invoker (invoker)
	 * C(), D():	Invoked methods by tested invoker
	 */
	@Test
	@SuppressWarnings("unused")
	public void T2() {
		MethodsCalledByTestedInvokedAuxClass testClass = new MethodsCalledByTestedInvokedAuxClass(true);
	}
}
