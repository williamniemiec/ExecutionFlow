package executionFlow.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import executionFlow.runtime.SkipCollection;
import math.Calculator;
import math.CalculatorNonStatic;
import math.CalculatorNonStaticWithConstructor;


/**
 * Class created for {@link JDBTest} testing.
 */
@SkipCollection
public class ExecutionFlowTest 
{
	@Test 
	public void callSum()
	{
		CalculatorNonStatic c = new CalculatorNonStatic();
		CalculatorNonStaticWithConstructor c2 = new CalculatorNonStaticWithConstructor(2);
		
		assertEquals(5, Calculator.sum(2, 3));
		assertEquals(5, c.sum(2, 3));
		assertEquals(5, c2.sum(2, 3));
	}
	
	@Test 
	public void callLoop()
	{
		Calculator.loop();
		assertTrue(true);
	}
}
