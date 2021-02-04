package util.io.processor;

import java.util.List;

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
