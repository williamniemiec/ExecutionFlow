package executionflow.util.console.style;

/**
 * Responsible for displaying console headers.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 */
public class ConsoleHeader {
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	private ConsoleHeader() {
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Displays a header formed by the symbol provided.
	 * 
	 * @param		title Title of the header
	 * @param		symbol Symbol that will form the header border
	 */
	public static void printHeader(String title, char symbol) {
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
	public static void printDivision(char symbol, int width) {
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
	private static void printSpaces(int amount) {
		for (int i=0; i<amount; i++)
			System.out.print(" ");
	}
}
