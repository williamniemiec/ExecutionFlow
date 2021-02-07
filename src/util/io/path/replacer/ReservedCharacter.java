package util.io.path.replacer;

/**
 * Characters reserved by the Windows operating system.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * 
 * @see			https://docs.microsoft.com/en-us/windows/win32/fileio/naming-a-file
 */
enum ReservedCharacter {
	
	//-------------------------------------------------------------------------
	//		Enumeration
	//-------------------------------------------------------------------------
	LESS_THAN("<", "\\<"),
	GREATER_THAN(">", "\\>"),
	FORWARD_SLASH("/", "\\/"),
	BACK_SLASH("\\", "\\\\"),
	COLON(":", "\\:"),
	DOUBLE_QUOTES("\"", "\\\""),
	PIPE("|", "\\|"),
	QUESTION_MARK("?", "\\?"),
	ASTERISK("*", "\\*");
	
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private String character;
	private String regex;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	private ReservedCharacter(String character, String regex) {
		this.character = character;
		this.regex = regex;
	}
	
	
	//-------------------------------------------------------------------------
	//		Getters
	//-------------------------------------------------------------------------
	public String getCharacter() {
		return character;
	}
	
	public String getRegex() {
		return regex;
	}
}