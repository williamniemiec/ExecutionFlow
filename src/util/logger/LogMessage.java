package util.logger;

/**
 * Responsible for displaying log messages.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 */
public class LogMessage {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final int PADDING_LEFT = 8;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	private LogMessage() {
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Display a log message.
	 * 
	 * @param		type Message type (error, warn, info...)
	 * @param		message Message to be displayed
	 * @param		breakline Indicates whether there should be a line break 
	 * after the log message. Default is true.
	 */
	public static void log(String type, String message, boolean breakline) {
		System.out.printf("%-" + PADDING_LEFT + "s %s", formatType(type), message);
		
		if (breakline)
			System.out.println();
	}
	
	/**
	 * Display a log message followed by a line break.
	 * 
	 * @param		type Message type (error, warn, info...)
	 * @param		message Message to be displayed
	 */
	public static void log(String type, String message) {
		log(type, message, true);
	}
	
	private static String formatType(String type) {
		return "[" + type + "]";
	}
}
