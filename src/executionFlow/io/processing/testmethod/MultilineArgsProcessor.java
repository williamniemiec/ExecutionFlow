package executionFlow.io.processing.testmethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultilineArgsProcessor {

	//---------------------------------------------------------------------
	//		Attributes
	//---------------------------------------------------------------------
	private boolean inComment;
	private static Map<Integer, Integer> mapping = new HashMap<>();
	
	
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
	public static Map<Integer, Integer> getMapping()
	{
		return mapping;
	}
	
	public static void clearMapping()
	{
		mapping.clear();
	}
}
