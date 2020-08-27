package executionFlow.util.balance;

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
public class CurlyBracketBalance extends Balance
{
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	public String toString() 
	{
		return "CurlyBracketBalance ["
				+ "curlyBracketsBalance=" + balance 
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
	public CurlyBracketBalance parse(String text)
	{
		int amountOpenCurlyBrackets = countOpenCurlyBrackets(text);
		int amountClosedCurlyBrackets = countClosedCurlyBrackets(text);
		
		
		balance = balance + amountOpenCurlyBrackets - amountClosedCurlyBrackets;
		alreadyIncreased = alreadyIncreased ? alreadyIncreased : amountOpenCurlyBrackets > 0;
		
		return this;
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
