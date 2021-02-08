package util.io.console;

import java.util.List;

/**
 * Responsible for displaying lines from a file on console. 
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 */
public class ConsoleFilePrinter {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static int marginLeft = 5;
	private static boolean lineNumberVisibility = true;
		
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	private ConsoleFilePrinter() {
	}

	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	public static void printFileWithLines(List<String> fileContent) {
		for (int i=0; i<fileContent.size(); i++) {
			if (lineNumberVisibility)
				printLineWithLineNumber(fileContent.get(i), i+1);
			else
				printLine(fileContent.get(i));
		}
	}
	
	private static void printLineWithLineNumber(String line, int lineNumber) {
		System.out.printf("%-" + marginLeft + "d\t%s\n", lineNumber, line);
	}

	private static void printLine(String line) {
		System.out.printf("%-" + marginLeft + "s\t%s\n", "", line);
	}


	//-------------------------------------------------------------------------
	//		Setters
	//-------------------------------------------------------------------------
	/**
	 * Sets the margin between the content of the file and the left edge of the
	 * console. Default is 5.
	 *  
	 * @param		margin Margin left
	 * 
	 * @throws		IllegalArgumentException If margin is negative
	 */
	public static void setMarginLeft(int margin) {
		if (marginLeft < 0)
			throw new IllegalArgumentException("Margin left cannot be negative");
		
		marginLeft = margin;
	}
	
	/**
	 * Defines whether to display the numbering of the lines. Default is true
	 * 
	 * @param		visible Line number visibility
	 */
	public static void setLineNumberVisibility(boolean visible) {
		lineNumberVisibility = visible;
	}
}
