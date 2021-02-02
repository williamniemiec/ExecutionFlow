package util.logger;

public enum LogLevel {
	
	//-------------------------------------------------------------------------
	//		Enumerations
	//-------------------------------------------------------------------------
	/**
	 * Disables all messages.
	 */
	OFF(false, false, false, false),
	
	/**
	 * Displays only error messages.
	 */
	ERROR(true, false, false, false), 
	
	/**
	 * Displays error and warning messages.
	 */
	WARNING(true, true, false, false), 
	
	/**
	 * Displays error, warning and info messages.
	 */
	INFO(true, true, true, false),
	
	/**
	 * Displays error, warning, info and debug messages.
	 */
	DEBUG(true, true, true, true);
	
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private boolean error;
	private boolean warn;
	private boolean info;
	private boolean debug;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	private LogLevel(boolean error, boolean warn, boolean info, boolean debug) {
		this.error = error;
		this.warn = warn;
		this.info = info;
		this.debug = debug;
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	public boolean shouldDisplayError() {
		return error;
	}
	
	public boolean shouldDisplayWarning() {
		return warn;
	}
	
	public boolean shouldDisplayInfo() {
		return info;
	}
	
	public boolean shouldDisplayDebug() {
		return debug;
	}
}
