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
	
	@Test
	public void testChainedMethodsInForLoop()
	{
		Calculator c = new Calculator();
		c.setNumber(0);
		
		for (int i=0; i<10; i++) {
			c.sum(10).mult(10).div(10);	// 10 * 10 / 100
		}
		
		assertEquals(100f, c.ans(), 0.01);
	}
}
