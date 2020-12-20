package executionFlow.util.logger;

public enum LogLevel {
	//-------------------------------------------------------------------------
	//		Enumerations
	//-------------------------------------------------------------------------
	/**
	 * Disables all messages.
	 */
	OFF,
	
	/**
	 * Displays only error messages.
	 */
	ERROR, 
	
	/**
	 * Displays error and warning messages.
	 */
	WARNING, 
	
	/**
	 * Displays error, warning and info messages.
	 */
	INFO,
	
	/**
	 * Displays error, warning, info and debug messages.
	 */
	DEBUG
}
