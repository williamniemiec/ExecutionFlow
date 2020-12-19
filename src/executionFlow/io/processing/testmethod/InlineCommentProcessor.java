package executionFlow.io.processing.testmethod;

import java.util.List;

public class InlineCommentProcessor {

	//---------------------------------------------------------------------
	//		Attributes
	//---------------------------------------------------------------------
	private boolean inComment;
	
	
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
		
		if (line.contains("System.out.print")) {
			processedLine = line.replaceAll(".*System\\.out\\.print(ln)?\\(.+\\);", "");
		}
		
		return processedLine;
	}
}
