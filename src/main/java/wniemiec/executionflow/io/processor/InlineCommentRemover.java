package wniemiec.executionflow.io.processor;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import wniemiec.executionflow.io.SourceCodeProcessor;

/**
 * Adds {@link wniemiec.executionflow.runtime.SkipCollection} annotation next to 
 * class declarations.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		6.0.1
 * @since 		6.0.0
 */
public class InlineCommentRemover extends SourceCodeProcessor {

	//---------------------------------------------------------------------
	//		Constructor
	//---------------------------------------------------------------------
	public InlineCommentRemover(List<String> sourceCode) {
		super(sourceCode, true);
	}
	
	
	//---------------------------------------------------------------------
	//		Methods
	//---------------------------------------------------------------------
	@Override
	protected String processLine(String line) {
		if (!line.contains("//"))
			return line;
		
		String processedLine = replaceStringWithBlankSpaces(line);
		
		if (processedLine.contains("//"))
			processedLine = line.substring(0, processedLine.indexOf("//"));
		else
			processedLine = line;
		
		return processedLine;
	}

	private String replaceStringWithBlankSpaces(String line) {
		String lineWithBlankStrings = line;
		StringBuilder strWithBlankSpaces = new StringBuilder();
		
		Matcher matcherContentBetweenQuotes = Pattern.compile("\"[^\"]*\"").matcher(line);
		
		while (matcherContentBetweenQuotes.find()) {
			int strLen = matcherContentBetweenQuotes.group().length() - 2; // -2 to disregard slashes
			int idxStart = matcherContentBetweenQuotes.start();
			int idxEnd = matcherContentBetweenQuotes.end();
			
			strWithBlankSpaces.append("\"");
			
			for (int i=0; i<strLen; i++) {
				strWithBlankSpaces.append(" ");
			}
			
			strWithBlankSpaces.append("\"");
			
			lineWithBlankStrings = lineWithBlankStrings.substring(0, idxStart) 
					+ strWithBlankSpaces.toString()
					+ lineWithBlankStrings.substring(idxEnd);
			
			strWithBlankSpaces = new StringBuilder();
		}
		
		return lineWithBlankStrings;
	}
}