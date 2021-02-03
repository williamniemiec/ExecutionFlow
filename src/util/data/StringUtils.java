package util.data;

import java.util.List;

/**
 * Contains methods that perform string manipulation.
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
	 */
	public static <T> String implode(List<T> list, String delimiter) {
		StringBuilder response = new StringBuilder();
		
		for (T p : list) {
			response.append(p);
			response.append(delimiter);
		}
		
		// Removes last delimiter
		if (response.length() > 1) {
			response.deleteCharAt(response.length()-1);
		}
		
		return response.toString();
	}
}
