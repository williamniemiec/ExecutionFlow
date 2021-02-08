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
	private static LogLevel level;
	private static LogMessage logMessage;
	
	
	//-------------------------------------------------------------------------
	//		Initialization block
	//-------------------------------------------------------------------------
	static {
		level = LogLevel.INFO;
		logMessage = new LogMessage();
	}
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	private Logger() {
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Displays an error message. <br />
	 * <b>Format:</b> <code>[ERROR] &lt;message&gt;</code>
	 * 
	 * @param		message Message to be displayed
	 * 
	 * @implSpec	By default it is added a break line at the end of the 
	 * message
	 * 
	 * @throws		IllegalArgumentException If message is null
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
	 * 
	 * @throws		IllegalArgumentException If message is null
	 */
	public static void error(String message, boolean breakLine) {
		if (message == null)
			throw new IllegalArgumentException("Message cannot be null");
		
		if (!level.shouldDisplayError())
			return;
		
		logMessage.log("ERROR", message, breakLine);
	}
	
	/**
	 * Displays a warning message. <br />
	 * <b>Format:</b> <code>[WARNING] &lt;message&gt;</code>
	 * 
	 * @param		message Message to be displayed
	 * 
	 * @implSpec	By default it is added a break line at the end of the 
	 * message
	 * 
	 * @throws		IllegalArgumentException If message is null
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
	 * 
	 * @throws		IllegalArgumentException If message is null
	 */
	public static void warning(String message, boolean breakLine) {
		if (message == null)
			throw new IllegalArgumentException("Message cannot be null");
		
		if (!level.shouldDisplayWarning())
			return;
		
		logMessage.log("WARN", message, breakLine);
	}
	
	/**
	 * Displays an information message. <br />
	 * <b>Format:</b> <code>[INFO] &lt;message&gt;</code>
	 * 
	 * @param		message Message to be displayed
	 * 
	 * @implSpec	By default it is added a break line at the end of the 
	 * message
	 * 
	 * @throws		IllegalArgumentException If message is null
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
	 * 
	 * @throws		IllegalArgumentException If message is null
	 */
	public static void info(String message, boolean breakLine) {
		if (message == null)
			throw new IllegalArgumentException("Message cannot be null");
		
		if (!level.shouldDisplayInfo())
			return;
		
		logMessage.log("INFO", message, breakLine);
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
	 * 
	 * @throws		IllegalArgumentException If message or source is null
	 */
	public static void debug(Class<?> source, String message) {
		if (source == null)
			throw new IllegalArgumentException("Source cannot be null");
		
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
	 * 
	 * @throws		IllegalArgumentException If message is null
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
	 * 
	 * @throws		IllegalArgumentException If message is null
	 */
	public static void debug(String message, boolean breakLine) {
		if (message == null)
			throw new IllegalArgumentException("Message cannot be null");
		
		if (!level.shouldDisplayDebug())
			return;
		
		logMessage.log("DEBUG", message, breakLine);
	}
	
	public static void clearLastMessage() {
		logMessage.clear();
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
	 * 
	 * @throws		IllegalArgumentException If level is null
	 */
	public static void setLevel(LogLevel level) {
		if (level == null)
			throw new IllegalArgumentException("Level cannot be null");
		
		Logger.level = level;
	}
	
	public static String getLastMessage() {
		return logMessage.getLastMessage();
	}
	
	public static String getLastMessageType() {
		return logMessage.getLastMessageType();
	}
}
