package executionFlow.util.balance;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Round bracket balance, to which its balance is calculated as follows:
 * <code>Current balance - Amount of open round brackets - Amount of closed round brackets</code>
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		4.0.1
 * @since		4.0.1
 */
public class RoundBracketBalance extends Balance
{
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	public String toString() 
	{
		return "RoundBracketBalance ["
				+ "roundBracketsBalance=" + balance 
				+ ", alreadyIncreased="	+ alreadyIncreased 
			+ "]";
	}
	
	/**
	 * Parses line by counting the number of curly brackets and performing the
	 * following calculation: 
	 * <code>Current balance - Amount of open curly brackets - Amount of closed curly brackets</code>
	 * 
	 * @param		text Text to be parsed
	 * 
	 * @return		This object to allows chained calls
	 */
	public RoundBracketBalance parse(String text)
	{
		int amountOpenRoundBrackets = countOpenRoundBrackets(text);
		int amountClosedRoundBrackets = countClosedRoundBrackets(text);
		
		
		balance = balance + amountOpenRoundBrackets - amountClosedRoundBrackets;
		alreadyIncreased = alreadyIncreased ? alreadyIncreased : amountOpenRoundBrackets > 0;
		
		return this;
	}
	
	/**
	 * Counts how many open round brackets are in a text.
	 * 
	 * @param		text Text to be analyzed
	 * 
	 * @return		Amount of open round brackets in the text
	 */
	private int countOpenRoundBrackets(String text)
	{
		final Pattern pattern_openRoundBrackets = Pattern.compile("\\(");
		Matcher openCBMatcher = pattern_openRoundBrackets.matcher(text);
		
		int size;
		for (size = 0; openCBMatcher.find(); size++);
		
		return size;
	}
	
	/**
	 * Counts how many closed round brackets are in a text.
	 * 
	 * @param		text Text to be analyzed
	 * 
	 * @return		Amount of closed round brackets in the text
	 */
	private int countClosedRoundBrackets(String text)
	{
		final Pattern pattern_closedRoundBrackets = Pattern.compile("\\)");
		Matcher openCBMatcher = pattern_closedRoundBrackets.matcher(text);
		
		int size;
		for (size = 0; openCBMatcher.find(); size++);
		
		return size;
	}
}
