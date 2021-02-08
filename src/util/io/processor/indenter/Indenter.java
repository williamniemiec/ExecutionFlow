package util.io.processor.indenter;

import java.util.List;

/**
 * Indents codes.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 */
public abstract class Indenter {
	
	/**
	 * Indent lines code.
	 * 
	 * @param		lines Code
	 * 
	 * @return		Indented code
	 * 
	 * @throws		IllegalArgumentException If lines is null
	 */
	public abstract List<String> indent(List<String> lines);
}
