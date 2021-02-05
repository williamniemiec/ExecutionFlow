package util.io.path;

/**
 * Responsible for replacing reserved characters by the operating system.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 */
public class ReservedCharactersReplacer {
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	private ReservedCharactersReplacer() {
	}
	
	/**
	 * Replaces characters reserved by the operating system, for file and 
	 * directory names, using the following mapping:
	 * <table border=1 width='100%'>
	 * 	<tr align='center'>
	 * 		<th>Original</th>
	 * 		<th>New</th>
	 * 	</tr>
	 * 	<tr align='center'>
	 * 		<td>&lt;</td>
	 * 		<td>(</td>
	 * 	</tr>
	 * 	<tr align='center'>
	 * 		<td>&gt;</td>
	 * 		<td>)</td>
	 * 	</tr>
	 * 	<tr align='center'>
	 * 		<td>\</td>
	 * 		<td>-</td>
	 * 	</tr>
	 * <tr align='center'>
	 * 		<td>/</td>
	 * 		<td>-</td>
	 * 	</tr>
	 * 	<tr align='center'>
	 * 		<td>:</td>
	 * 		<td>;</td>
	 * 	</tr>
	 * 	<tr align='center'>
	 * 		<td>"</td>
	 * 		<td>'</td>
	 * 	</tr>
	 * 	<tr align='center'>
	 * 		<td>|</td>
	 * 		<td>-</td>
	 * 	</tr>
	 * 	<tr align='center'>
	 * 		<td>?</td>
	 * 		<td>+</td>
	 * 	</tr>
	 * 	<tr align='center'>
	 * 		<td>*</td>
	 * 		<td>+</td>
	 * 	</tr>
	 * <table>
	 * 
	 * @param		str Path, filename or directory name to be processed
	 * 
	 * @return		String without reserved characters
	 * 
	 * @throws		IllegalArgumentException If str is null
	 */
	public static String replaceReservedCharacters(String str) {
		if (str == null)
			throw new IllegalArgumentException("String cannot be null");
		
		String processedStr = str;
		
		processedStr = processedStr.replaceAll("\\<", "(");
		processedStr = processedStr.replaceAll("\\>", ")");
		processedStr = processedStr.replaceAll("\\/", "-");
		processedStr = processedStr.replaceAll("\\\\", "-");
		processedStr = processedStr.replaceAll("\\:", ";");
		processedStr = processedStr.replaceAll("\"", "'");
		processedStr = processedStr.replaceAll("\\|", "-");
		processedStr = processedStr.replaceAll("\\?", "+");
		processedStr = processedStr.replaceAll("\\*", "+");
		
		return processedStr;
	}
	
	
}
