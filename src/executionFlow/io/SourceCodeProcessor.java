package executionFlow.io;

import java.util.List;

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
}
