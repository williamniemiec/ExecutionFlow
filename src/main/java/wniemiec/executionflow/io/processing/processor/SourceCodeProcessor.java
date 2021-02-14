package wniemiec.executionflow.io.processing.processor;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import wniemiec.util.data.encrypt.MD5;

public abstract class SourceCodeProcessor {

	//---------------------------------------------------------------------
	//		Attributes
	//---------------------------------------------------------------------
	private boolean inMultiLineComment;
	private boolean inInlineComment;
	private int currentIdx;
	private List<String> sourceCode;
	private boolean ignoreComments;
	
	
	//---------------------------------------------------------------------
	//		Constructor
	//---------------------------------------------------------------------
	protected SourceCodeProcessor(List<String> sourceCode, boolean ignoreComments) {
		this.sourceCode = sourceCode;
		this.ignoreComments = ignoreComments;
	}
	
	
	//---------------------------------------------------------------------
	//		Methods
	//---------------------------------------------------------------------
	public final List<String> processLines() {
		for (currentIdx = 0; currentIdx < sourceCode.size(); currentIdx++) {
			if (ignoreComments)
				checkComments(sourceCode.get(currentIdx));
			
			if (!inComment()) {
				String processedLine = processLine(sourceCode.get(currentIdx));
			
				sourceCode.set(currentIdx, processedLine);
			}
		}
		
		whenFinished(sourceCode);
		
		return sourceCode;
	}
	
	private void checkComments(String line) {
		checkInlineComment(line);
		checkMultiLineComment(line);
	}
	
	private void checkInlineComment(String str) {
		final String regexCommentFullLine = "^[\\t\\s]*(\\/\\/).*";
		
		inInlineComment = str.matches(regexCommentFullLine);
	}
	
	private void checkMultiLineComment(String line) {
		if (line.contains("/*") && !line.contains("*/")) {
			inMultiLineComment = true;
		}
		else if (inMultiLineComment && line.contains("*/")) {
			inMultiLineComment = false;
		}
	}
	
	private boolean inComment() {
		return	inMultiLineComment
				|| inInlineComment
				|| sourceCode.get(currentIdx).contains("*/");
	}
	
	protected abstract String processLine(String line);
	
	protected void whenFinished(List<String> processedLines) {
		return;
	}
	
	protected String extractContentBetweenParenthesis(String content) {
		Pattern patternContentInParenthesis = Pattern.compile("\\(.*\\)");
		Matcher contentBetweenParenthesis = patternContentInParenthesis.matcher(content);
		
		if (!contentBetweenParenthesis.find())
			return "";
		
		return contentBetweenParenthesis.group().replace("(", "").replace(")", "");
	}
	
	/**
	 * Generates a unique variable name. It will be:<br />
	 * <code>MD5(current_time+random_number)</code>
	 * 
	 * @return		Variable name
	 */
	public static String generateVarName() {
		return ("_" + MD5.encrypt(String.valueOf(generateRandomNumber())));
	}
	
	private static double generateRandomNumber() {
		return (new Date().getTime() + (Math.random() * 9999 + 1));
	}
	
	
	//---------------------------------------------------------------------
	//		Getters & Setters
	//---------------------------------------------------------------------
	protected int getCurrentIndex() {
		return currentIdx;
	}
	
	protected int getTotalLines() {
		return sourceCode.size();
	}
	
	protected void setLine(int idx, String content) {
		if (idx >= sourceCode.size())
			throw new IllegalArgumentException("Index out of bounds");
		
		sourceCode.set(idx, content);
	}
	
	protected String getLine(int idx) {
		if (idx >= sourceCode.size())
			throw new IllegalArgumentException("Index out of bounds");
		
		return sourceCode.get(idx);
	}

	protected String getNextLine() {
		if (currentIdx+1 >= sourceCode.size())
			return "";
		
		return sourceCode.get(currentIdx+1);
	}
	
	protected String getPreviousLine() {
		if (currentIdx-1 < 0)
			return "";
		
		return sourceCode.get(currentIdx-1);
	}
}
