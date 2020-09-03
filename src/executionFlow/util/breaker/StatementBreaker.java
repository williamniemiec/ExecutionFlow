package executionFlow.util.breaker;

import java.util.List;

import executionFlow.util.DataUtil;


/**
 * Acts on strings of statements leaving each statement on different lines.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		4.1.0
 * @since 		4.1.0
 */
public class StatementBreaker extends Breaker
{
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Breaks strings of statements leaving each statement on a new line.
	 */
	@Override
	public Breaker parse(List<String> lines) 
	{
		if (lines == null || lines.isEmpty())
			return this;
		
		final String REGEX_FOR_STATEMENT = "^[\\ \\t]*for[\\ \\t]*\\(.+$";
		String line, indentation;
		String[] statements;
		
		
		for (int i=0; i<lines.size(); i++) {
			line = lines.get(i);
			
			if (!line.matches(REGEX_FOR_STATEMENT) && line.contains(";")) {
				statements = line.split(";");
				
				if (statements.length > 1) {
					// Puts first statement on the current line
					lines.set(i, statements[0] + ";");
					lineBreak.add(i);
					indentation = DataUtil.getIndentation(line);
					
					// Puts next statements on new lines
					for (int j = 1; j<statements.length; j++) {
						lines.add(i+j, indentation + statements[j] + ";");
						lineBreak.add(i+j);
					}
				}
			}
		}
		
		return this;
	}
}
