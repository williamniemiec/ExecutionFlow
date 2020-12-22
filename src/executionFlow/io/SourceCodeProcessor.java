package executionFlow.io;

import java.util.List;

public abstract class SourceCodeProcessor {

	//---------------------------------------------------------------------
	//		Attributes
	//---------------------------------------------------------------------
	private boolean inComment;
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
			if (!ignoreComments)
				checkComments(sourceCode.get(currentIdx));
			
			if (!inComment) {
				String processedLine = processLine(sourceCode.get(currentIdx));
			
				sourceCode.set(currentIdx, processedLine);
			}
		}
		
		whenFinished(sourceCode);
		
		return sourceCode;
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
	
	protected abstract String processLine(String line);
	
	protected void whenFinished(List<String> processedLines) {
		return;
	}
	
	
	//---------------------------------------------------------------------
	//		Getters
	//---------------------------------------------------------------------
	protected int getCurrentIdx() {
		return currentIdx;
	}
	
	protected void modifySourceCode(int idx, String content) {
		if (idx <= sourceCode.size())
			throw new IllegalArgumentException("Index out of bounds");
		
		sourceCode.set(idx, content);
	}

	protected String getNextLine() {
		if (currentIdx+1 >= sourceCode.size())
			return "";
		
		return sourceCode.get(currentIdx+1);
	}
}