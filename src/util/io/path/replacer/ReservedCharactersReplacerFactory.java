package util.io.path.replacer;

/**
 * Creates a changer of characters that are reserved from the operating system.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 */
public class ReservedCharactersReplacerFactory {
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	private ReservedCharactersReplacerFactory() {
	}
	
	
	//-------------------------------------------------------------------------
	//		Getters
	//-------------------------------------------------------------------------
	public static Replacer getStandardReplacer() {
		return new StandardReservedCharactersReplacer();
	}
}
