package examples.override;
import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class OverrideTest 
{
	@Test
	public void testOverloadedMethod3()
	{
		OverrideClass oc = new OverrideClass() {
			@Override
			public int foo(int num)
			{
				num *= 1000;
				return (int)num;
			}
		};
		
		assertEquals(10000, oc.foo(10));
	}
}
