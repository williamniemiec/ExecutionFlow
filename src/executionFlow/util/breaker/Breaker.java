package executionFlow.util.breaker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Looks for patterns in lines of a file and performs a certain action.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		4.1.0
 * @since 		4.1.0
 */
public abstract class Breaker 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	/**
	 * Stores the number of lines that have been placed on a new line.
	 */
	protected List<Integer> lineBreak = new ArrayList<>();
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Looks for patterns in lines of a file and performs a certain action.
	 * 
	 * @param		lines File lines
	 * 
	 * @return		Itself to allow chained calls
	 */
	public abstract Breaker parse(List<String> lines);
	
	
	//-------------------------------------------------------------------------
	//		Getters
	//-------------------------------------------------------------------------
	/**
	 * Gets the number of lines that have been placed on a new line.
	 * 
	 * @return		List containing line numbers
	 */
	public List<Integer> getBrokenLines()
	{
		Collections.sort(lineBreak);
		
		return lineBreak;
	}
	
}
