package examples.overloadedMethod;
import org.junit.Test;


public class OverloadTest 
{
	@Test
	public void testOverloadedMethod()
	{
		Overload oc = new Overload();
		oc.overloadedMethod();
	}
	
	@Test
	public void testOverloadedMethod2()
	{
		Overload oc = new Overload();
		oc.overloadedMethod(10);
	}
}
