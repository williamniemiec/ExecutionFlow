package executionFlow.util.balance;


/**
 * Responsible for managing balances. 
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		4.0.1
 * @since		4.0.1
 */
public abstract class Balance 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	protected int balance;
	
	/**
	 * Flag indicating whether the balance has already been increased at any 
	 * time.
	 */
	protected boolean alreadyIncreased;
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	public String toString() 
	{
		return "Balance ["
				+ "balance=" + balance 
				+ ", alreadyIncreased="	+ alreadyIncreased 
			+ "]";
	}
	
	/**
	 * Increments balance.
	 */
	public void increaseBalance()
	{
		alreadyIncreased = true;
		balance += 1;
	}
	
	/**
	 * Decrements balance.
	 */
	public void decreaseBalance()
	{
		balance -= 1;
	}
	
	/**
	 * Returns balance.
	 * 
	 * @return		Current balance
	 */
	public int getBalance()
	{
		return balance;
	}
	
	/**
	 * Checks if the balance is empty.
	 * 
	 * @return		If balance is zero
	 */
	public boolean isBalanceEmpty()
	{
		return balance == 0;
	}
	
	/**
	 * Checks whether the balance has already been increased at any time.
	 * 
	 * @return		If the balance has already been increased at any time
	 */
	public boolean alreadyIncreased()
	{
		return alreadyIncreased;
	}
}
