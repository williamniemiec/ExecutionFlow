package api.util;

import java.util.List;

/**
 * Responsible for strings manipulation.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 */
public class StringUtils {
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	private StringUtils() {
	}
	

	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Converts elements of a list into a string by separating each element
	 * with a delimiter. 
	 * 
	 * @param		list List to be converted
	 * 
	 * @return		List elements separated by the given delimiter
	 * 
	 * @throws		IllegalArgumentException If list or delimiter is null
	 */
	public static <T> String implode(List<T> list, String delimiter) {
		if ((list == null))
			throw new IllegalArgumentException("List cannot be null");
		
		if ((delimiter == null))
			throw new IllegalArgumentException("Delimiter cannot be null");
		
		StringBuilder response = new StringBuilder();
		
		for (T p : list) {
			response.append(p);
			response.append(delimiter);
		}
		
		// Removes last delimiter
		if (!delimiter.isBlank() && (response.length() > 1)) {
			response.deleteCharAt(response.length()-1);
		}
		
		return response.toString();
	}
}
