package executionFlow.io.processor.invoked.holeplug;

import java.util.List;

import executionFlow.io.processor.SourceCodeProcessor;

/**
 * Checks if an opening curly bracket is in next line or not. If it is,
 * moves it to the end of current line.
 */
public class OpenCurlyBracketProcessor extends SourceCodeProcessor {

	//---------------------------------------------------------------------
	//		Constructor
	//---------------------------------------------------------------------
	protected OpenCurlyBracketProcessor(List<String> sourceCode) {
		super(sourceCode, true);
	}

	
	//---------------------------------------------------------------------
	//		Methods
	//---------------------------------------------------------------------
	@Override
	protected String processLine(String line) {
		String processedLine = line;
		
		if (hasOnlyOpeningCurlyBracket(line)) {
			processedLine = line + " {";
			modifySourceCode(getCurrentIdx() + 1, "");
		}
		
		return processedLine;
	}

	private boolean hasOnlyOpeningCurlyBracket(String line) {
		final String regexOnlyOpeningCurlyBracket = "^(\\s|\\t)*\\{(\\s|\\t|\\/)*$";
		
		return line.matches(regexOnlyOpeningCurlyBracket);
	}
}
