package executionFlow.io.processing.testmethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import executionFlow.util.balance.RoundBracketBalance;

public class MultilineArgsProcessor {

	//---------------------------------------------------------------------
	//		Attributes
	//---------------------------------------------------------------------
	private boolean inComment;
	private static Map<Integer, Integer> mapping = new HashMap<>();
	private boolean insideMultilineArgs = false;
	private int multilineArgsStartIndex = -1;
	
	
	//---------------------------------------------------------------------
	//		Methods
	//---------------------------------------------------------------------
	public List<String> processLines(List<String> lines) {
		List<String> processedLines = lines;
		
		for (int i = 0; i < lines.size(); i++) {
			checkComments(lines.get(i));
			
			if (!inComment) {
				String processedLine = processLine(lines.get(i));
			
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


	private String processLine(String line) {
		String processedLine = line;
		
		return processedLine;
	}
	
	/**
	 * Converts method calls with arguments on multiple lines to a call with 
	 * arguments on a single line.
	 * 
	 * @param		currentLine Line corresponding to the current index
	 * @param		lines File lines
	 * @param		currentIndex Current line index
	 * 
	 * @return		Line with arguments on a single line
	 */
	private String parseMultilineArgs(String currentLine, List<String> lines, int currentIndex) 
	{
		final String REGEX_MULTILINE_ARGS = ".+,([^;{(\\[]+|[\\s\\t]*)$";
		final String REGEX_MULTILINE_ARGS_CLOSE = "^.*[\\s\\t)}]+;[\\s\\t]*$";
		
		Pattern classKeywords = Pattern.compile("(@|class|implements|throws)");
		
		RoundBracketBalance rbb = new RoundBracketBalance();
		rbb.parse(currentLine);
		
		boolean isMethodCallWithMultipleLinesArgument = 
				!rbb.isBalanceEmpty() &&
				!classKeywords.matcher(currentLine).find() && 
				currentLine.matches(REGEX_MULTILINE_ARGS) && 
				(currentIndex+1 < lines.size());
		

		if (isMethodCallWithMultipleLinesArgument) {
			int oldLine;
			int newLine;
			String nextLine = lines.get(currentIndex+1);
			
			if (!insideMultilineArgs) {	
				lines.set(currentIndex+1, "");
				currentLine = currentLine + nextLine;
				
				oldLine = currentIndex+1+1;
				newLine = currentIndex+1;
				
				if (!nextLine.matches(REGEX_MULTILINE_ARGS_CLOSE)) {
					multilineArgsStartIndex = currentIndex;
					insideMultilineArgs = true;
				}
				else {
					insideMultilineArgs = false;
				}
			}
			else {
				lines.set(multilineArgsStartIndex, lines.get(multilineArgsStartIndex) + currentLine);
				currentLine = "";
				
				oldLine = currentIndex+1;
				newLine = multilineArgsStartIndex+1;
			}
			
			mapping.put(oldLine, newLine);
		}
		else if (insideMultilineArgs) {
			insideMultilineArgs = false;
			
			lines.set(multilineArgsStartIndex, lines.get(multilineArgsStartIndex) + currentLine);
			currentLine = "";
			mapping.put(currentIndex+1, multilineArgsStartIndex+1);
		}
		else if (currentLine.matches(REGEX_MULTILINE_ARGS_CLOSE) && multilineArgsStartIndex > 0) {
			insideMultilineArgs = false;
			lines.set(multilineArgsStartIndex, lines.get(multilineArgsStartIndex) + currentLine);
			currentLine = "";
		}
		
		if (!insideMultilineArgs)
			multilineArgsStartIndex = -1;

		return currentLine;
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
