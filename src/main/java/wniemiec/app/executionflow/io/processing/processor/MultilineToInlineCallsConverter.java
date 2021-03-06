package wniemiec.app.executionflow.io.processing.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import wniemiec.util.io.parser.balance.RoundBracketBalance;

/**
 * Converts method calls with arguments on multiple lines to a call with 
 * arguments on a single line.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @since 		6.0.0
 */
public class MultilineToInlineCallsConverter extends SourceCodeProcessor {

	//---------------------------------------------------------------------
	//		Attributes
	//---------------------------------------------------------------------
	private static Map<Integer, List<Integer>> mapping = new HashMap<>();
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

		return processedLine;
	}

	private String combineMultilineArgs(String line) {
		String processedLine = line;

		checkIsOutOfMethod();
		
		if (isMethodCallWithMultipleLines(line)) {
			processedLine = parseMethodCallWithMultipleLines(line);
			checkBalanceOfParentheses(processedLine);
		}
		else if (insideMultilineArgs) {
			checkBalanceOfParentheses(line);
			putOnMethodInvocationLine(line);
			insideMultilineArgs = !isParenthesesBalanced();
			
			processedLine = "";
			
			addMapping(idxMethodInvocation+1, getCurrentIndex()+1);
		}
		else if (hasTestAnnotation(processedLine) && (idxMethodInvocation > 0)) {
			putOnMethodInvocationLine(line);
			
			processedLine = "";
			idxMethodInvocation = -1;
		}
		
		return processedLine;
	}


	private void checkIsOutOfMethod() {
		if ((rbb != null) && rbb.alreadyIncreased() && rbb.isBalanceEmpty()) {
			rbb = null;
			insideMultilineArgs = false;
			idxMethodInvocation = -1;
		}
	}

	private boolean isMethodCallWithMultipleLines(String line) {
		return	isMethodCallWithMultiArgs(line)
				&& !hasClassKeywords(line) 
				&& !isLastLine();
	}

	private boolean isMethodCallWithMultiArgs(String line) {
		return	line.matches(".+,([^;{(\\[]+|[\\s\\t]*)$") 
				|| line.matches(".+\\([^)]*$");
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
			if (hasOnlyClosedCurlyBracketAndSemicolon(getNextLine())) {
				insideMultilineArgs = false;					
			}
			else {
				idxMethodInvocation = getCurrentIndex();
				insideMultilineArgs = true;
			}
			
			processedLine = processedLine + getNextLine().trim();
			eraseLine(getCurrentIndex()+1);
			
			oldLine = getCurrentIndex()+1+1;
			newLine = getCurrentIndex()+1;
		}
		
		addMapping(newLine, oldLine);
		
		return processedLine;
	}

	private void checkBalanceOfParentheses(String line) {		
		if (rbb == null) {
			rbb = new RoundBracketBalance();
		}
		
		rbb.parse(line);
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

	private boolean hasOnlyClosedCurlyBracketAndSemicolon(String line) {
		final String regexOnlyClosedCurlyBracket = "^.*[\\s\\t)}]+;[\\s\\t]*$";
		
		return line.matches(regexOnlyClosedCurlyBracket);
	}
	
	private void addMapping(int newLine, int oldLine) {
		List<Integer> oldLines;
		
		if (mapping.containsKey(newLine)) {
			oldLines = mapping.get(newLine);
			oldLines.add(oldLine);
		}
		else {
			oldLines = new ArrayList<>();
			oldLines.add(oldLine);
			
			mapping.put(newLine, oldLines);
		}
	}
	
	private boolean hasTestAnnotation(String line) {
		return line.matches("[\\s\\t]*@Test.+");
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
	 *	<li><b>Key:</b> Modified source file lines</li>
	 * 	<li><b>Value:</b> Original source file lines</li>
	 * </ul>
	 */
	public Map<Integer, List<Integer>> getMapping()
	{
		return mapping;
	}
	
	public void clearMapping()
	{
		mapping.clear();
	}
}
