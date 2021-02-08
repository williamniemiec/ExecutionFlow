package util.io.parser.balance;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CurlyBracketBalanceTest extends BalanceTest {

	@Override
	public void beforeEachTest() {
		balance = new CurlyBracketBalance();
	}

	@Override
	public void testParseBalancedBrackets() {
		balance.parse("{hello");
		balance.parse("world}");
		
		assertTrue(balance.isBalanceEmpty());
	}
	
	@Override
	public void testParseUnbalancedBrackets() {
		balance.parse("{hello");
		
		assertFalse(balance.isBalanceEmpty());
	}
	
	@Override
	public void testAlreadyIncreasedAfterParseBrackets() {
		balance.parse("{hello}");
		
		assertTrue(balance.alreadyIncreased());
	}
	
	@Override
	public void testAlreadyIncreasedAfterParseWithoutBrackets() {
		balance.parse("hello");
		
		assertFalse(balance.alreadyIncreased());
	}
	
	@Override
	public void testAlreadyIncreasedAfterManualIncrease() {
		balance.increaseBalance();
		
		assertTrue(balance.alreadyIncreased());
	}
	
	@Override
	public void testDecreaseBalance() {
		balance.decreaseBalance();
		
		assertTrue(balance.getBalance() == -1);
	}
}
