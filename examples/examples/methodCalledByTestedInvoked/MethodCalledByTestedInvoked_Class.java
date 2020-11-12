package examples.methodCalledByTestedInvokeds;


public class MethodCalledByTestedInvoked_Class 
{
	public MethodCalledByTestedInvoked_Class(boolean callMethods)
	{
		if (callMethods) {
			A();
			D();
		}
	}

	public void A()
	{
		System.out.println("a");
		B();
		C();
	}
	
	
	public void B()
	{
		System.out.println("b");
	}
	
	public void C()
	{
		System.out.println("c");
		D();
	}
	
	public void D()
	{
		System.out.println("d");
	}
}
