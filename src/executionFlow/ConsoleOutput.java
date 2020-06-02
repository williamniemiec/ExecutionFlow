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
	 * @implSpec	By default it is added a break line at the end of the 
	 * message
	 */
	public static void showInfo(String message)
	{
		showInfo(message, true);
	}
	
	/**
	 * Displays an information message. <br />
	 * <b>Format:</b> <code>[INFO] &lt;message&gt;</code>
	 * 
	 * @param		message Message to be displayed
	 * @param		breakLine If a break line is added at the end of the
	 * message
	 * @implSpec	By default it is added a break line at the end of the 
	 * message
	 * @implNote	If breakLine is false, will not be added a break line at 
	 * the end of the message
	 */
	public static void showInfo(String message, boolean breakLine)
	{
		if (breakLine)
			System.out.println("[INFO] "+message);
		else
			System.out.print("[INFO] "+message);
	}
	
	/**
	 * Displays an error message. <br />
	 * <b>Format:</b> <code>[ERROR] &lt;message&gt;</code>
	 * 
	 * @param		message Message to be displayed
	 * @implSpec	By default it is added a break line at the end of the 
	 * message
	 */
	public static void showError(String message)
	{
		showError(message, true);
	}
	
	/**
	 * Displays an error message. <br />
	 * <b>Format:</b> <code>[ERROR] &lt;message&gt;</code>
	 * 
	 * @param		message Message to be displayed
	 * @param		breakLine If a break line is added at the end of the
	 * message
	 * @implSpec	By default it is added a break line at the end of the 
	 * message
	 * @implNote	If breakLine is false, will not be added a break line at 
	 * the end of the message
	 */
	public static void showError(String message, boolean breakLine)
	{
		if (breakLine)
			System.err.println("[ERROR] "+message);
		else
			System.err.print("[ERROR] "+message);
	}
	
	/**
	 * Displays a warning message. <br />
	 * <b>Format:</b> <code>[WARNING] &lt;message&gt;</code>
	 * 
	 * @param		message Message to be displayed
	 * @implSpec	By default it is added a break line at the end of the 
	 * message
	 */
	public static void showWarning(String message)
	{
		showWarning(message, true);
	}
	
	/**
	 * Displays a warning message. <br />
	 * <b>Format:</b> <code>[WARNING] &lt;message&gt;</code>
	 * 
	 * @param		message Message to be displayed
	 * @param		breakLine If a break line is added at the end of the
	 * message
	 * @implSpec	By default it is added a break line at the end of the 
	 * message
	 * @implNote	If breakLine is false, will not be added a break line at 
	 * the end of the message
	 */
	public static void showWarning(String message, boolean breakLine)
	{
		if (breakLine)
			System.out.println("[WARNING] "+message);
		else
			System.out.print("[WARNING] "+message);
	}
	
	/**
	 * Displays a debug message. <br />
	 * <b>Format:</b> <code>[DEBUG] &lt;message&gt;</code>
	 * 
	 * @param		message Message to be displayed
	 * @implSpec	By default it is added a break line at the end of the 
	 * message
	 */
	public static void showDebug(String message)
	{
		showDebug(message, true);
	}
	
	/**
	 * Displays a debug message. <br />
	 * <b>Format:</b> <code>[DEBUG] &lt;message&gt;</code>
	 * 
	 * @param		message Message to be displayed
	 * @param		breakLine If a break line is added at the end of the
	 * message
	 * @implSpec	By default it is added a break line at the end of the 
	 * message
	 * @implNote	If breakLine is false, will not be added a break line at 
	 * the end of the message	
	 */
	public static void showDebug(String message, boolean breakLine)
	{
		if (breakLine)
			System.out.println("[DEBUG] "+message);
		else
			System.out.print("[DEBUG] "+message);
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
	 * @implSpec	Puts a break line at the end
	 */
	public static void printDivision(char symbol, int width)
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
