package util.io.path.replacer;

/**
 * Replace characters in a string according to some criteria.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 */
public interface Replacer {

	/**
	 * Replaces all reserved characters with others.
	 * 
	 * @param		str Path, filename or directory name to be processed
	 * 
	 * @return		String without reserved characters
	 * 
	 * @throws		IllegalArgumentException If str is null
	 */
	public String replace(String str);
}
