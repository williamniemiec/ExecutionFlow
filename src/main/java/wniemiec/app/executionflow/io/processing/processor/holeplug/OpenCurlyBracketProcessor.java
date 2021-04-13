package wniemiec.app.executionflow.io.processing.processor.holeplug;

import java.util.List;

import wniemiec.app.executionflow.io.processing.processor.SourceCodeProcessor;

/**
 * Moves alone opening curly bracket to the previous line.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @since 		6.0.0
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
		
		if (hasOnlyOpeningCurlyBracket(getNextLine())) {
			processedLine = line + " {";
			setLine(getCurrentIndex()+1, "");
		}
		
		return processedLine;
	}

	private boolean hasOnlyOpeningCurlyBracket(String line) {
		final String regexOnlyOpeningCurlyBracket = "^(\\s|\\t)*\\{(\\s|\\t|\\/)*$";
		
		return line.matches(regexOnlyOpeningCurlyBracket);
	}
}
