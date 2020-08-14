package executionFlow.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Move any code after an opening or closing curly bracket to the next line. It
 * does not break lines containing 'assert' or 'Assert.assert'.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		3.2.0
 * @since 		3.2.0
 */
public class CurlyBracketBreaker 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final String REGEX_COMMENT_FULL_LINE = 
			"^(\\t|\\ )*(\\/\\/|\\/\\*|\\*\\/|\\*).*";
	private static final String REGEX_ASSERT_METHOD = 
			"(\\ |\\t)*(Assert\\.)?assert[A-z]+\\(.*";
	private List<Integer> lineBreak = new ArrayList<>();
	
	
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
		
		Collections.sort(lineBreak);
		
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
		String line, rightBracket;
		int idx_curlyBracketEnd;
		Matcher m;
		
		
		if (lines == null || lines.isEmpty())
			return;
		
		for (int i=0; i<lines.size(); i++) {
			line = lines.get(i);
			
			if (line.contains("@") || line.matches(REGEX_COMMENT_FULL_LINE) || 
					line.matches(REGEX_ASSERT_METHOD))
				continue;
			
			// If the line does not contain an opening curly bracket or contains
			// but it is alone on the line, keep the original line 
			if (line.contains("{") && !line.matches(REGEX_ONLY_OPENING_CURLY_BRACKET)) {
				m = Pattern.compile(REGEX_OPENING_CURLY_BRACKET).matcher(line);
				
				if (m.find()) {
					idx_curlyBracketEnd = m.start() + m.group().indexOf("{");	
					rightBracket = line.substring(idx_curlyBracketEnd + 1);
					
					// If the line contains an opening curly bracket but there
					// is nothing to the right of it, keep the original line
					if (!rightBracket.isBlank()) {
						// Otherwise put everything to its right on a new line 
						// (not including it)
						lines.set(i, line.substring(0, idx_curlyBracketEnd + 1));
						lines.add(i + 1, getIndentation(line) + "\t" + line.substring(idx_curlyBracketEnd + 1));
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
		final String REGEX_ONLY_CLOSING_CURLY_BRACKET = "^(\\s|\\t)+\\}(\\s|\\t|\\/)*$";
		final String REGEX_CLOSING_CURLY_BRACKET = "(\\ |\\t)*\\}(\\ |\\t)*";
		String line, rightBracket, leftContent;
		int idx_curlyBracketEnd;
		Matcher m;
		boolean wasBroken = false;
		
		
		if (lines == null || lines.isEmpty())
			return;
		
		for (int i=0; i<lines.size(); i++) {
			line = lines.get(i);
			if (line.contains("@") || line.matches(REGEX_COMMENT_FULL_LINE) ||
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
					rightBracket = line.substring(idx_curlyBracketEnd + 1);
					
					// If the line contains a closing curly bracket but there
					// is nothing to the right of it, keep the original line
					if (!rightBracket.isBlank()) {
						// Otherwise put everything to its right on a new line
						// (including it)
						leftContent = line.substring(0, idx_curlyBracketEnd);
						
						if (!leftContent.isBlank()) {
							lines.set(i, leftContent);
							lines.add(i + 1, getIndentation(lines.get(i-1)) + line.substring(idx_curlyBracketEnd));
							wasBroken = true;
							lineBreak.add(i);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Gets indentation of a source file line.
	 * 
	 * @param		line Source file line
	 * 
	 * @return		Indentation or empty string if there is no indentation
	 */
	private String getIndentation(String line)
	{
		final String regex_indent = "^(\\ |\\t)+";
		Matcher m = Pattern.compile(regex_indent).matcher(line);
		
		
		if (!m.find())
			return "";
		
		return m.group();
	}
	
	
	//-------------------------------------------------------------------------
	//		Getters
	//-------------------------------------------------------------------------
	public List<Integer> getBrokenLines()
	{
		return this.lineBreak;
	}
}
