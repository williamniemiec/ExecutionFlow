package executionFlow.util;


/**
 * Responsible for displaying information on the console.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.1.0
 * @since		2.0.0
 */
public class ConsoleOutput 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final int WIDTH = 8;
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Displays an information message. <br />
	 * <b>Format:</b> <code>[INFO] &lt;message&gt;</code>
	 * 
	 * @param		message Message to be displayed
	 * 
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
	 * 
	 * @implSpec	By default it is added a break line at the end of the 
	 * message
	 * 
	 * @implNote	If breakLine is false, will not be added a break line at 
	 * the end of the message
	 */
	public static void showInfo(String message, boolean breakLine)
	{
		if (breakLine)
			System.out.printf("%-" + WIDTH + "s%s\n", "[INFO] ", message);
		else
			System.out.printf("%-" + WIDTH + "s%s", "[INFO] ", message);
	}
	
	/**
	 * Displays an error message. <br />
	 * <b>Format:</b> <code>[ERROR] &lt;message&gt;</code>
	 * 
	 * @param		message Message to be displayed
	 * 
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
	 * 
	 * @implSpec	By default it is added a break line at the end of the 
	 * message
	 * 
	 * @implNote	If breakLine is false, will not be added a break line at 
	 * the end of the message
	 */
	public static void showError(String message, boolean breakLine)
	{
		if (breakLine)
			System.err.printf("%-" + WIDTH + "s%s\n", "[ERROR] ", message);
		else
			System.err.printf("%-" + WIDTH + "s%s", "[ERROR] ", message);
	}
	
	/**
	 * Displays a warning message. <br />
	 * <b>Format:</b> <code>[WARNING] &lt;message&gt;</code>
	 * 
	 * @param		message Message to be displayed
	 * 
	 * @implSpec	By default it is added a break line at the end of the 
	 * message
	 */
	public static void showWarning(String message)
	{
		showWarning(message, true);
	}
	
	/**
	 * Displays a warning message. <br />
	 * <b>Format:</b> <code>[WARN] &lt;message&gt;</code>
	 * 
	 * @param		message Message to be displayed
	 * @param		breakLine If a break line is added at the end of the
	 * message
	 * 
	 * @implSpec	By default it is added a break line at the end of the 
	 * message
	 * 
	 * @implNote	If breakLine is false, will not be added a break line at 
	 * the end of the message
	 */
	public static void showWarning(String message, boolean breakLine)
	{
		if (breakLine)
			System.out.printf("%-" + WIDTH + "s%s\n", "[WARN] ", message);
		else
			System.out.printf("%-" + WIDTH + "s%s", "[WARN] ", message);
	}
	
	/**
	 * Displays a debug message. <br />
	 * <b>Format:</b> <code>[DEBUG] &lt;message&gt;</code>
	 * 
	 * @param		message Message to be displayed
	 * 
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
	 * 
	 * @implSpec	By default it is added a break line at the end of the 
	 * message
	 * 
	 * @implNote	If breakLine is false, will not be added a break line at 
	 * the end of the message	
	 */
	public static void showDebug(String message, boolean breakLine)
	{
		if (breakLine)
			System.out.printf("%-" + WIDTH + "s%s\n", "[DEBUG] ", message);
		else
			System.out.printf("%-" + WIDTH + "s%s", "[DEBUG] ", message);
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
	 * 
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
