package executionFlow.io.processor.testmethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import executionFlow.io.SourceCodeProcessor;
import executionFlow.util.balance.RoundBracketBalance;

/**
 * Converts method calls with arguments on multiple lines to a call with 
 * arguments on a single line.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.3
 * @since 		5.2.3
 */
public class MultilineToInlineCallsConverter extends SourceCodeProcessor {

	//---------------------------------------------------------------------
	//		Attributes
	//---------------------------------------------------------------------
	private static Map<Integer, Integer> mapping = new HashMap<>();
	private boolean insideMultilineArgs = false;
	private int idxMethodInvocation = -1;
	private RoundBracketBalance rbb;
	
	
	//---------------------------------------------------------------------
	//		Constructor
	//---------------------------------------------------------------------
	public MultilineToInlineCallsConverter(List<String> sourceCode) {
		super(sourceCode, true);
	}
	
	
	//---------------------------------------------------------------------
	//		Methods
	//---------------------------------------------------------------------
	@Override
	protected String processLine(String line) {
		if (line.isBlank()) 
			return line;
		
		String processedLine = line;
		
		processedLine = combineMultilineArgs(line);
		
		if (!insideMultilineArgs)
			idxMethodInvocation = -1;
		
		return processedLine;
	}

	private String combineMultilineArgs(String line) {
		String processedLine = line;
		
		if (isMethodCallWithMultipleLines(line)) {
			processedLine = parseMethodCallWithMultipleLines(line);
			
			checkBalanceOfParentheses(processedLine);
		}
		else if (insideMultilineArgs) {
			checkBalanceOfParentheses(line);
			putOnMethodInvocationLine(line);
		
			insideMultilineArgs = !isParenthesesBalanced();
			
			processedLine = "";
			
			mapping.put(getCurrentIndex()+1, idxMethodInvocation+1);
		}
		else if (hasOnlyClosedCurlyBracket(processedLine) && (idxMethodInvocation > 0)) {
			putOnMethodInvocationLine(line);
			
			processedLine = "";
		}
		
		return processedLine;
	}

	private boolean isMethodCallWithMultipleLines(String line) {
		return	isMethodCallWithMultiArgs(line)
				&& !hasClassKeywords(line) 
				&& !isLastLine();
	}

	private boolean isMethodCallWithMultiArgs(String line) {
		final String regexMultilineArgs = ".+,([^;{(\\[]+|[\\s\\t]*)$";
		
		return line.matches(regexMultilineArgs);
	}
	
	private boolean hasClassKeywords(String line) {
		Pattern classKeywords = Pattern.compile("(@|class|implements|throws)");
		
		return classKeywords.matcher(line).find();
	}

	private boolean isLastLine() {
		return (getCurrentIndex()+1 >= getTotalLines());
	}

	private String parseMethodCallWithMultipleLines(String line) {
		String processedLine = line;
		int oldLine;
		int newLine;
		
		if (insideMultilineArgs) {	
			putOnMethodInvocationLine(line);
			
			processedLine = "";
			
			// +1 to disregarding zero
			oldLine = getCurrentIndex()+1;	
			newLine = idxMethodInvocation+1;
		}
		else {
			if (hasOnlyClosedCurlyBracket(getNextLine())) {
				insideMultilineArgs = false;					
			}
			else {
				idxMethodInvocation = getCurrentIndex();
				insideMultilineArgs = true;
			}
			
			processedLine = processedLine + getNextLine();
			eraseLine(getCurrentIndex()+1);
			
			oldLine = getCurrentIndex()+1+1;
			newLine = getCurrentIndex()+1;
		}
		
		mapping.put(oldLine, newLine);
		
		return processedLine;
	}

	private void checkBalanceOfParentheses(String line) {		
		if (rbb == null) {
			rbb = new RoundBracketBalance();
		}
	}
	
	private boolean isParenthesesBalanced() {
		return rbb.isBalanceEmpty();
	}
	
	private void putOnMethodInvocationLine(String line) {
		setLine(
			idxMethodInvocation, 
			getLine(idxMethodInvocation) + line
		);
	}

	private boolean hasOnlyClosedCurlyBracket(String line) {
		final String regexOnlyClosedCurlyBracket = "^.*[\\s\\t)}]+;[\\s\\t]*$";
		
		return line.matches(regexOnlyClosedCurlyBracket);
	}
	
	private void eraseLine(int idxLine) {
		setLine(idxLine, "");
	}
	
	
	//-------------------------------------------------------------------------
	//		Getters
	//-------------------------------------------------------------------------
	/**
	 * Gets the mapping of the original file with the modified file.
	 * 
	 * @return		Mapping with the following format:
	 * <ul>
	 *	<li><b>Key:</b> Original source file line</li>
	 * 	<li><b>Value:</b> Modified source file line</li>
	 * </ul>
	 */
	public Map<Integer, Integer> getMapping()
	{
		return mapping;
	}
	
	public void clearMapping()
	{
		mapping.clear();
	}
}
