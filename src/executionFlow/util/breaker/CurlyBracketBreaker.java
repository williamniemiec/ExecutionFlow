package executionFlow.util.breaker;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import executionFlow.util.DataUtil;


/**
 * Move any code after an opening or closing curly bracket to the next line. It
 * does not break lines containing 'assert' or 'Assert.assert'.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 */
public class CurlyBracketBreaker extends Breaker
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final String REGEX_COMMENT_FULL_LINE = 
			"^(\\t|\\ )*(\\/\\/|\\/\\*|\\*\\/|\\*).*";
	private static final String REGEX_ASSERT_METHOD = 
			"(\\ |\\t)*(Assert\\.)?assert[A-z]+\\(.*";
	private static final String REGEX_EMPTY_CURLY_BRACKETS = 
			"^.+(\\s|\\t)*\\{(\\s|\\t)*\\}(\\s|\\t)*$";
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Move any code after an opening or closing curly bracket to the next line.
	 * 
	 * @param		Lines from a source file
	 * 
	 * @return		Itself to allow chained calls
	 */
	public CurlyBracketBreaker parse(List<String> lines)
	{
		// Move any code after an opening curly bracket to the next line
		openingCurlyBracketBreaker(lines);
		// Move any code after an closing curly bracket to the next line
		closingCurlyBracketBreaker(lines);

		return this;
	}
	
	/**
	 * Move any code after an opening curly bracket to the next line.
	 * 
	 * @param		Lines from a source file
	 */
	private void openingCurlyBracketBreaker(List<String> lines) 
	{
		final String REGEX_ONLY_OPENING_CURLY_BRACKET = "^(\\s|\\t)+\\{(\\s|\\t|\\/)*$";
		final String REGEX_OPENING_CURLY_BRACKET = "(\\ |\\t)*\\{(\\ |\\t)*";
		final String REGEX_STRING = "\".*\"";
		String line, rightBracket;
		int idx_curlyBracketEnd;
		Matcher m;
		
		
		if (lines == null || lines.isEmpty())
			return;
		
		for (int i=0; i<lines.size(); i++) {
			line = lines.get(i);
			
			if (line.contains("@") || line.matches(REGEX_COMMENT_FULL_LINE) || 
					line.matches(REGEX_EMPTY_CURLY_BRACKETS) || 
					line.matches(REGEX_ASSERT_METHOD))
				continue;
			
			// If the line does not contain an opening curly bracket or contains
			// but it is alone on the line, keep the original line 
			if (line.contains("{") && !line.matches(REGEX_ONLY_OPENING_CURLY_BRACKET)) {
				m = Pattern.compile(REGEX_OPENING_CURLY_BRACKET).matcher(line);
				
				if (m.find()) {
					idx_curlyBracketEnd = m.start() + m.group().indexOf("{");
					
					// Checks if curly bracket belongs to a string
					m = Pattern.compile(REGEX_STRING).matcher(line);
					
					if (m.find()) {
						if (idx_curlyBracketEnd > m.start() && idx_curlyBracketEnd < m.end()) {
							continue;
						}
					}
					
					rightBracket = line.substring(idx_curlyBracketEnd + 1);
					
					// If the line contains an opening curly bracket but there
					// is nothing to the right of it, keep the original line
					if (!rightBracket.isBlank()) {
						// Otherwise put everything to its right on a new line 
						// (not including it)
						lines.set(i, line.substring(0, idx_curlyBracketEnd + 1));
						lines.add(i + 1, DataUtil.getIndentation(line) + "\t" + line.substring(idx_curlyBracketEnd + 1));
						lineBreak.add(i);
					}
				}
			}
		}
	}
	
	/**
	 * Move any code after an closing curly bracket to the next line.
	 * 
	 * @param		Lines from a source file
	 */
	private void closingCurlyBracketBreaker(List<String> lines) 
	{
		final String REGEX_ONLY_CLOSING_CURLY_BRACKET = "^(\\s|\\t)+(\\})+(\\)|;|\\}|\\])*(\\s|\\t|\\/)*$";
		final String REGEX_CLOSING_CURLY_BRACKET = "(\\ |\\t)*\\}(\\ |\\t)*";
		final String REGEX_STRING = "\".*\"";
		String line, rightContent, leftContent;
		int idx_curlyBracketEnd;
		Matcher m;
		boolean wasBroken = false;
		
		
		if (lines == null || lines.isEmpty())
			return;
		
		for (int i=0; i<lines.size(); i++) {
			line = lines.get(i);
			if (line.contains("@") || line.matches(REGEX_COMMENT_FULL_LINE) ||
					line.matches(REGEX_EMPTY_CURLY_BRACKETS) ||
					line.matches(REGEX_ASSERT_METHOD) || wasBroken) {
				wasBroken = false;
				continue;
			}
			
			// If the line does not contain a closing curly bracket or contains
			// but it is alone on the line, keep the original line 
			if (line.contains("}") && !line.matches(REGEX_ONLY_CLOSING_CURLY_BRACKET)) {
				m = Pattern.compile(REGEX_CLOSING_CURLY_BRACKET).matcher(line);
				
				if (m.find()) {
					idx_curlyBracketEnd = m.start() + m.group().indexOf("}");	
					
					// Checks if curly bracket belongs to a string
					m = Pattern.compile(REGEX_STRING).matcher(line);
					
					if (m.find()) {
						if (idx_curlyBracketEnd > m.start() && idx_curlyBracketEnd < m.end()) {
							continue;
						}
					}
					
					rightContent = line.substring(idx_curlyBracketEnd + 1);
					leftContent = line.substring(0, idx_curlyBracketEnd);
						
					if (leftContent.isBlank()) {
						lines.set(i, leftContent + "}");
						lines.add(i + 1, DataUtil.getIndentation(lines.get(i)) + rightContent);
					}
					else {
						lines.set(i, leftContent);
						lines.add(i + 1, DataUtil.getIndentation(lines.get(i)) + "}" + rightContent);
					}
					
					lineBreak.add(i);
				}
			}
		}
	}
}
