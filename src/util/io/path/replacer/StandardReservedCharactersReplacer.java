package util.io.path.replacer;

import java.util.HashMap;
import java.util.Map;

/**
 * Replaces characters reserved by the operating system, for file and 
 * directory names, using the following mapping:
 * 
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
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 */
class StandardReservedCharactersReplacer extends ReservedCharactersReplacer {
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	public StandardReservedCharactersReplacer() {
		super(generateMapping());
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	private static Map<ReservedCharacter, String> generateMapping() {
		Map<ReservedCharacter, String> mapping = new HashMap<>();
		
		mapping.put(ReservedCharacter.LESS_THAN, "(");
		mapping.put(ReservedCharacter.GREATER_THAN, ")");
		mapping.put(ReservedCharacter.FORWARD_SLASH, "-");
		mapping.put(ReservedCharacter.BACK_SLASH, "-");
		mapping.put(ReservedCharacter.COLON, ";");
		mapping.put(ReservedCharacter.DOUBLE_QUOTES, "'");
		mapping.put(ReservedCharacter.PIPE, "-");
		mapping.put(ReservedCharacter.QUESTION_MARK, "+");
		mapping.put(ReservedCharacter.ASTERISK, "+");
		
		return mapping;
	}
}
