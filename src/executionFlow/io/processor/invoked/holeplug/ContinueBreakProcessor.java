package executionFlow.io.processor.invoked.holeplug;

import java.util.List;

import executionFlow.io.processor.SourceCodeProcessor;

/**
 * Process line with 'continue' or 'break' keyword. It will add the following
 * code: <br /> 
 * 
 * <code>if (Boolean.parseBoolean("True")) { &lt;line&gt; }</code>. <br />
 * 
 * This method cannot add an if clause like "if (true) {line}" because it
 * is ignored when class is compiled. The function 'parseBoolean' is just 
 * a randomly chosen function and can be replaced by any other function
 * that returns true.
 *
 */
public class ContinueBreakProcessor extends SourceCodeProcessor {
	
	//---------------------------------------------------------------------
	//		Constructor
	//---------------------------------------------------------------------
	protected ContinueBreakProcessor(List<String> sourceCode) {
		super(sourceCode, true);
	}
	
	
	//---------------------------------------------------------------------
	//		Methods
	//---------------------------------------------------------------------
	@Override
	protected String processLine(String line) {		
		if (!isContinueOrBreakKeyword(line))
			return line;
		
		String processedLine = "if (Boolean.parseBoolean(\"True\")) {" + line + "}";
		
		if (processedLine.contains("break;"))
			processedLine += "break;";
		
		return processedLine;
	}

	private boolean isContinueOrBreakKeyword(String line) {
		final String regexContinueBreak = 
				"^(\\ |\\t)*(continue|break)(\\ |\\t)*[^;\\s\\t\\(\\)\\.]*;";
		
		return line.matches(regexContinueBreak);
	}
}
