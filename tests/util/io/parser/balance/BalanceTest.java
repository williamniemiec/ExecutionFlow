package util.io.parser.balance;

import org.junit.Before;
import org.junit.Test;

public abstract class BalanceTest {

	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	protected Balance balance;
	
	
	//-------------------------------------------------------------------------
	//		Test hooks
	//-------------------------------------------------------------------------
	@Before
	public abstract void beforeEachTest();
	
	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	@Test
	public abstract void testParseBalancedBrackets();
	
	@Test
	public abstract void testParseUnbalancedBrackets();
	
	@Test
	public abstract void testAlreadyIncreasedAfterParseBrackets();
	
	@Test
	public abstract void testAlreadyIncreasedAfterParseWithoutBrackets();
	
	@Test
	public abstract void testAlreadyIncreasedAfterManualIncrease();
	
	@Test
	public abstract void testDecreaseBalance();
}
