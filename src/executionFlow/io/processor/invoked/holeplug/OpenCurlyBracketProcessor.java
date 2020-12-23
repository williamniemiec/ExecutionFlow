package executionFlow.io.processor.invoked.holeplug;

import java.util.List;

import executionFlow.io.SourceCodeProcessor;

/**
 * Moves aloonen opening curly bracket to the previous line.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.3
 * @since 		5.2.3
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
			setLine(getCurrentIndex()+1, "");
		}
		
		return processedLine;
	}

	private boolean hasOnlyOpeningCurlyBracket(String line) {
		final String regexOnlyOpeningCurlyBracket = "^(\\s|\\t)*\\{(\\s|\\t|\\/)*$";
		
		return line.matches(regexOnlyOpeningCurlyBracket);
	}
}
