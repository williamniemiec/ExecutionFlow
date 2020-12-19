package executionFlow.io.processor.testmethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import executionFlow.io.processor.SourceCodeProcessor;
import executionFlow.util.balance.RoundBracketBalance;

/**
 * Converts method calls with arguments on multiple lines to a call with 
 * arguments on a single line.
 */
public class MultilineArgsProcessor /*extends SourceCodeProcessor*/ {

	//---------------------------------------------------------------------
	//		Attributes
	//---------------------------------------------------------------------
	private boolean inComment;
	private static Map<Integer, Integer> mapping = new HashMap<>();
	private boolean insideMultilineArgs = false;
	private int idxMethodInvocation = -1;
	
	
	//---------------------------------------------------------------------
	//		Constructor
	//---------------------------------------------------------------------
//	protected MultilineArgsProcessor(List<String> sourceCode) {
//		super(sourceCode, true);
//	}
	
	
	//---------------------------------------------------------------------
	//		Methods
	//---------------------------------------------------------------------
	public List<String> processLines(List<String> lines) {
		List<String> processedLines = lines;
		
		for (int i = 0; i < lines.size(); i++) {
			checkComments(lines.get(i));
			
			if (!inComment) {
//				String processedLine = processLine(lines.get(i));
				String processedLine = parseMultilineArgs(lines, i);
			
				processedLines.set(i, processedLine);
			}
		}
		
		return processedLines;
	}

	private void checkComments(String line) {
		final String regexCommentFullLine = 
				"^(\\t|\\ )*(\\/\\/|\\/\\*|\\*\\/|\\*).*";
		
		if (line.matches(regexCommentFullLine))
			inComment = true;
		
		if (line.contains("/*") && !line.contains("*/")) {
			inComment = true;
		}
		else if (inComment && line.contains("*/")) {
			inComment = false;
		}
	}


//	private String processLine(String line) {
//		String processedLine = line;
//		
//		return processedLine;
//	}
	
	private String parseMultilineArgs(List<String> lines, int currentIndex) 
	{
		String processedLine = lines.get(currentIndex);

		processedLine = combineMultilineArgs(lines, currentIndex, processedLine);
		
		if (!insideMultilineArgs)
			idxMethodInvocation = -1;

		return processedLine;
	}

	private String combineMultilineArgs(List<String> lines, int currentIndex, String processedLine) {
		if (isMethodCallWithMultipleLines(lines, currentIndex)) {
			int oldLine;
			int newLine;
			
			if (insideMultilineArgs) {	
				putOnMethodInvocationLine(lines, currentIndex);
				
				processedLine = "";
				
				oldLine = currentIndex+1;			// +1 to disregarding zero
				newLine = idxMethodInvocation+1;
			}
			else {
				if (hasOnlyClosedCurlyBracket(lines.get(currentIndex+1))) {
					insideMultilineArgs = false;					
				}
				else {
					idxMethodInvocation = currentIndex;
					insideMultilineArgs = true;
				}
				
				eraseLine(lines, currentIndex+1);
				processedLine = combineLines(lines, currentIndex, currentIndex+1);
				
				oldLine = currentIndex+1+1;
				newLine = currentIndex+1;
				
			}
			
			mapping.put(oldLine, newLine);
		}
		else if (insideMultilineArgs) {
			insideMultilineArgs = false;
			
			putOnMethodInvocationLine(lines, currentIndex);
			
			processedLine = "";
			
			mapping.put(currentIndex+1, idxMethodInvocation+1); 
		}
		else if (hasOnlyClosedCurlyBracket(lines.get(currentIndex)) && idxMethodInvocation > 0) {
			putOnMethodInvocationLine(lines, currentIndex);
			
			processedLine = "";
		}
		return processedLine;
	}

	private void putOnMethodInvocationLine(List<String> lines, int idxLine) {
		lines.set(
				idxMethodInvocation, 
				combineLines(lines, idxMethodInvocation, idxLine)
		);
	}

	private String combineLines(List<String> lines, int lineIdx1, int lineIdx2) {
		return lines.get(lineIdx1) + lines.get(lineIdx2);
	}

	private void eraseLine(List<String> lines, int idxLine) {
		lines.set(idxLine, "");
	}

	private boolean hasOnlyClosedCurlyBracket(String line) {
		final String REGEX_MULTILINE_ARGS_CLOSE = "^.*[\\s\\t)}]+;[\\s\\t]*$";
		
		return line.matches(REGEX_MULTILINE_ARGS_CLOSE);
	}
	
	private boolean isMethodCallWithMultipleLines(List<String> lines, int currentIndex) {
		return	isMethodCallWithMultiArgs(lines, currentIndex) 
				&& !isParenthesesBalanced(lines, currentIndex)
				&& !hasClassKeywords(lines, currentIndex) 
				&& !isLastLine(lines, currentIndex);
	}

	private boolean isLastLine(List<String> lines, int currentIndex) {
		return (currentIndex+1 >= lines.size());
	}

	private boolean isMethodCallWithMultiArgs(List<String> lines, int currentIndex) {
		final String REGEX_MULTILINE_ARGS = ".+,([^;{(\\[]+|[\\s\\t]*)$";
		
		return lines.get(currentIndex).matches(REGEX_MULTILINE_ARGS);
	}

	private boolean hasClassKeywords(List<String> lines, int currentIndex) {
		Pattern classKeywords = Pattern.compile("(@|class|implements|throws)");
		
		return classKeywords.matcher(lines.get(currentIndex)).find();
	}

	private boolean isParenthesesBalanced(List<String> lines, int currentIndex) {
		RoundBracketBalance rbb = new RoundBracketBalance();
		rbb.parse(lines.get(currentIndex));
		
		return rbb.isBalanceEmpty();
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
