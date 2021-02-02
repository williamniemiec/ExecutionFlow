package util.logger;

/**
 * Responsible for displaying log messages on the console.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 */
public class Logger {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static LogLevel level = LogLevel.INFO;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	private Logger() {
	}
	
	
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
	public static void info(String message) {
		info(message, true);
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
	public static void info(String message, boolean breakLine) {
		if (!level.shouldDisplayInfo())
			return;
		
		LogMessage.log("INFO", message, breakLine);
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
	public static void error(String message) {
		error(message, true);
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
	public static void error(String message, boolean breakLine) {
		if (!level.shouldDisplayInfo())
			return;
		
		LogMessage.log("ERROR", message, breakLine);
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
	public static void warning(String message) {
		warning(message, true);
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
	public static void warning(String message, boolean breakLine) {
		if (!level.shouldDisplayWarning())
			return;
		
		LogMessage.log("WARN", message, breakLine);
	}
	
	/**
	 * Displays a debug message. <br />
	 * <b>Format:</b> <code>[DEBUG] - classname - &lt;message&gt;</code>
	 * 
	 * @param		classname Name of the class that called the method
	 * @param		message Message to be displayed
	 * 
	 * @implSpec	By default it is added a break line at the end of the 
	 * message
	 */
	public static void debug(Class<?> source, String message) {
		debug("{ " + getClassName(source) + " } " + message, true);
	}
	
	private static String getClassName(Class<?> source) {
		String[] terms = source.getName().split("\\.");
		
		return terms[terms.length-1]; 
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
	public static void debug(String message) {
		debug(message, true);
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
	public static void debug(String message, boolean breakLine) {
		if (!level.shouldDisplayDebug())
			return;
		
		LogMessage.log("DEBUG", message, breakLine);
	}
	
	
	//-------------------------------------------------------------------------
	//		Getters & Setters
	//-------------------------------------------------------------------------
	/**
	 * Gets current log level. The log level defines what type of message will 
	 * be displayed.
	 * 
	 * @return		Current log level
	 */
	public static LogLevel getLevel() {
		return level;
	}
	
	/**
	 * Sets log level. The level defines what type of message will be 
	 * displayed.
	 * 
	 * @param		level New level
	 */
	public static void setLevel(LogLevel level) {
		Logger.level = level;
	}
}
