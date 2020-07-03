package examples.overloadedMethod;
import org.junit.Test;


public class OverloadTest 
{
	@Test
	public void testOverloadedMethod()
	{
		OverloadClass oc = new OverloadClass();
		oc.overloadedMethod();
	}
	
	@Test
	public void testOverloadedMethod2()
	{
		OverloadClass oc = new OverloadClass();
		oc.overloadedMethod(10);
	}
}
