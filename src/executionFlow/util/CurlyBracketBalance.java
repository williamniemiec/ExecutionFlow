package executionFlow.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Curly bracket balance, to which its balance is calculated as follows:
 * <code>Current balance - Amount of open curly brackets - Amount of closed curly brackets</code>
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		4.0.0
 * @since		2.0.0
 */
public class CurlyBracketBalance 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	/**
	 * Balance of curly brackets.
	 */
	private int curlyBracketsBalance;
	
	/**
	 * Flag indicating whether the balance has already been increased at any 
	 * time.
	 */
	private boolean alreadyIncreased;
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Parses line by counting the number of curly brackets and performing the
	 * following calculation: 
	 * <code>Current balance - Amount of open curly brackets - Amount of closed curly brackets</code>
	 * 
	 * @param		text Text to be parsed
	 * 
	 * @return		This object to allows chained calls
	 */
	public CurlyBracketBalance parse(String text)
	{
		int amountOpenCurlyBrackets = countOpenCurlyBrackets(text);
		int amountClosedCurlyBrackets = countClosedCurlyBrackets(text);
		
		
		curlyBracketsBalance = curlyBracketsBalance + amountOpenCurlyBrackets - amountClosedCurlyBrackets;
		alreadyIncreased = alreadyIncreased ? alreadyIncreased : amountOpenCurlyBrackets > 0;
		
		return this;
	}
	
	/**
	 * Increments balance.
	 * 
	 * @apiNote		Must be called only when an open curly bracket is found
	 */
	public void increaseBalance()
	{
		alreadyIncreased = true;
		curlyBracketsBalance += 1;
	}
	
	/**
	 * Decrements balance.
	 * 
	 * @apiNote		Must be called only when a closed curly bracket is found
	 */
	public void decreaseBalance()
	{
		curlyBracketsBalance -= 1;
	}
	
	/**
	 * Returns balance.
	 * 
	 * @return		Current balance
	 */
	public int getBalance()
	{
		return curlyBracketsBalance;
	}
	
	/**
	 * Checks if balance is empty
	 * 
	 * @return		If balance is zero
	 */
	public boolean isBalanceEmpty()
	{
		return curlyBracketsBalance == 0;
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
	
	/**
	 * Counts how many open curly brackets are in a text.
	 * 
	 * @param		text Text to be analyzed
	 * 
	 * @return		Amount of open curly brackets in the text
	 */
	private int countOpenCurlyBrackets(String text)
	{
		final Pattern pattern_openCurlyBrackets = Pattern.compile("\\{");
		Matcher openCBMatcher = pattern_openCurlyBrackets.matcher(text);
		
		int size;
		for (size = 0; openCBMatcher.find(); size++);
		
		return size;
	}
	
	/**
	 * Counts how many closed curly brackets are in a text.
	 * 
	 * @param		text Text to be analyzed
	 * 
	 * @return		Amount of closed curly brackets in the text
	 */
	private int countClosedCurlyBrackets(String text)
	{
		final Pattern pattern_closedCurlyBrackets = Pattern.compile("\\}");
		Matcher openCBMatcher = pattern_closedCurlyBrackets.matcher(text);
		
		int size;
		for (size = 0; openCBMatcher.find(); size++);
		
		return size;
	}
}
