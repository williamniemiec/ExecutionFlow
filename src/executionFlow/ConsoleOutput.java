package executionFlow;


/**
 * Responsible for the console outputs.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		1.5
 * @since		1.5
 */
public class ConsoleOutput 
{
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Displays an information message. <br />
	 * <b>Format:</b> <code>[INFO] &lt;message&gt;</code>
	 * 
	 * @param		message Message to be displayed
	 */
	public static void showInfo(String message)
	{
		System.out.println("[INFO] "+message);
	}
	
	/**
	 * Displays an error message. <br />
	 * <b>Format:</b> <code>[ERROR] &lt;message&gt;</code>
	 * 
	 * @param		message Message to be displayed
	 */
	public static void showError(String message)
	{
		System.err.println("[ERROR] "+message);
	}
	
	/**
	 * Displays a warning message. <br />
	 * <b>Format:</b> <code>[WARNING] &lt;message&gt;</code>
	 * 
	 * @param		message Message to be displayed
	 */
	public static void showWarning(String message)
	{
		System.out.println("[WARNING] "+message);
	}
	
	/**
	 * Displays a debug message. <br />
	 * <b>Format:</b> <code>[DEBUG] &lt;message&gt;</code>
	 * 
	 * @param		message Message to be displayed
	 */
	public static void showDebug(String message)
	{
		System.out.println("[DEBUG] "+message);
	}
	
	/**
	 * Displays a header formed by the symbol provided.
	 * 
	 * @param		title Title of the header
	 * @param		symbol Symbol that will form the header border
	 */
	public static void showHeader(String title, char symbol)
	{
		int titleLen = title.length();
		int width = 80;
		int center = width/2 - titleLen/2;
		
		printDivision(symbol, 80);
		printSpaces(center);
		System.out.println(title);
		printDivision(symbol, 80);
	}
	
	/**
	 * Puts a line on console with the symbol provided.
	 * 
	 * @param		symbol Symbol that the line will be composed
	 * @param		width How many occurrences of the symbols will appear on 
	 * the line
	 * @implNote	Puts a break line at the end
	 */
	private static void printDivision(char symbol, int width)
	{
		for (int i=0; i<width; i++)
			System.out.print(symbol);
		
		System.out.println();
	}
	
	/**
	 * Puts a line on console with a certain amount of spaces.
	 * 
	 * @param		amount How many occurrences of white space will appear on 
	 * the line
	 */
	private static void printSpaces(int amount)
	{
		for (int i=0; i<amount; i++)
			System.out.print(" ");
	}
}
