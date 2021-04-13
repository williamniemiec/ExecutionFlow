package wniemiec.app.executionflow.io.processing.processor.holeplug;

import java.util.List;
import java.util.regex.Pattern;

import wniemiec.app.executionflow.io.processing.processor.SourceCodeProcessor;

/**
 * Process else keywords, performing the following procedures:
 * <ul>
 * 	<li>Adds an instruction in else statements</li>
 * 	<li>Adjusts lines containing closing curly bracket + instruction</li>
 * </ul>
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @since 		6.0.0
 */
public class ElseProcessor extends SourceCodeProcessor {

	//---------------------------------------------------------------------
	//		Constructor
	//---------------------------------------------------------------------
	protected ElseProcessor(List<String> sourceCode) {
		super(sourceCode, true);
	}
	
	
	//---------------------------------------------------------------------
	//		Methods
	//---------------------------------------------------------------------
	@Override
	protected String processLine(String line) {	
		if (!isElseKeyword(line))
			return line;
		
		return putVariableNextToOpenCurlyBracket(line);
	}
	
	private boolean isElseKeyword(String line) {
		final Pattern patternElse = 
				Pattern.compile("[\\s\\t\\}]*else(\\ |\\t|\\{|$)+.*");
		
		return !line.contains("if") && patternElse.matcher(line).find();
	}
	
	private String putVariableNextToOpenCurlyBracket(String line) {
		if (!line.contains("{")) {
			throw new IllegalStateException(
					"Code block must be enclosed in curly brackets; line: " 
					+ line
			);
		}
		
		StringBuilder statement = new StringBuilder();
		int idxCurlyBrackets = line.indexOf('{');
		 
		statement.append(line.substring(0, idxCurlyBrackets+1));
		statement.append("int " + generateVarName() + "=0;"); 
		statement.append(line.substring(idxCurlyBrackets+1));

		return statement.toString();
	}
}
