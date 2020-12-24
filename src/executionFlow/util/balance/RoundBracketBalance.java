package executionflow.util.balance;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Round bracket balance, to which its balance is calculated as follows:
 * <code>Current balance - Amount of open round brackets - Amount of closed round brackets</code>
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 */
public class RoundBracketBalance extends Balance {
	
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
	public RoundBracketBalance parse(String text)
	{
		String textWithoutString = removeStrings(text);
		
		int amountOpenRoundBrackets = countOpenRoundBrackets(textWithoutString);
		int amountClosedRoundBrackets = countClosedRoundBrackets(textWithoutString);
	
		currentBalance += amountOpenRoundBrackets - amountClosedRoundBrackets;
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
		final Pattern patternOpenRoundBrackets = Pattern.compile("\\(");
		Matcher openCBMatcher = patternOpenRoundBrackets.matcher(text);
		
		int size;
		for (size = 0; openCBMatcher.find(); size++)
			;
		
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
		final Pattern patternClosedRoundBrackets = Pattern.compile("\\)");
		Matcher openCBMatcher = patternClosedRoundBrackets.matcher(text);
		
		int size;
		for (size = 0; openCBMatcher.find(); size++);
		
		return size;
	}
	
	@Override
	public String toString() 
	{
		return "RoundBracketBalance ["
				+ "currentBalance=" + currentBalance 
				+ ", alreadyIncreased="	+ alreadyIncreased 
			+ "]";
	}
}
