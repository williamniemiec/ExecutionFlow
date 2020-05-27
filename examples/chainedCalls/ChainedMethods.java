package chainedCalls;
import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class ChainedMethods 
{
	@Test
	public void testChainedMethods()
	{
		Calculator c = new Calculator();
		
		// ( ( (15 + 15 + 15 - 15) * 2) / 10) / 2
		assertEquals(3f, c.setNumber(15).sum(15).sum(15).sub(15).mult(2).div(10).div(2).ans(), 0.01);
	}
}
