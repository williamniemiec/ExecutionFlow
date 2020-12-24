package executionflow.util.balance;

/**
 * Responsible for managing balances. 
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 */
public abstract class Balance {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	protected int currentBalance;
	
	/**
	 * Flag indicating whether the balance has already been increased at any 
	 * time.
	 */
	protected boolean alreadyIncreased;
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	public void increaseBalance() {
		alreadyIncreased = true;
		currentBalance += 1;
	}
	
	public void decreaseBalance() {
		currentBalance -= 1;
	}
	
	public int getBalance() {
		return currentBalance;
	}
	
	public boolean isBalanceEmpty() {
		return currentBalance == 0;
	}
	
	/**
	 * Checks whether the balance has already been increased at any time.
	 * 
	 * @return		If the balance has already been increased at any time
	 */
	public boolean alreadyIncreased() {
		return alreadyIncreased;
	}
	
	@Override
	public String toString() {
		return "Balance ["
				+ "currentBalance=" + currentBalance 
				+ ", alreadyIncreased="	+ alreadyIncreased 
			+ "]";
	}
	
	protected String removeStrings(String text) {
		return text.replaceAll("\\\\\"", "").replaceAll("\"[^\"]*\"", "");
	}
}
