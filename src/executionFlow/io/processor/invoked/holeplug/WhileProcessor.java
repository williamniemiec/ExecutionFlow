package executionFlow.io.processor.invoked.holeplug;

import java.util.List;

import executionFlow.io.SourceCodeProcessor;
import executionFlow.util.DataUtil;

/**
 * Process 'while' code block, adding variable declarations initialized on 
 * lines with 'while' keyword.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.3
 * @since 		5.2.3
 */
public class WhileProcessor extends SourceCodeProcessor {

	//---------------------------------------------------------------------
	//		Constructor
	//---------------------------------------------------------------------
	protected WhileProcessor(List<String> sourceCode) {
		super(sourceCode, true);
	}
	
	
	//---------------------------------------------------------------------
	//		Methods
	//---------------------------------------------------------------------
	@Override
	protected String processLine(String line) {		
		if (!isWhileStatement(line))
			return line;
		
		return putVariableNextToOpenCurlyBracket(line);
	}
	
	private boolean isWhileStatement(String line) {
		final String regexWhileStatement = 
				".*while[\\s\\t]+\\([\\s\\t]*true[\\s\\t]*\\)[\\s\\t{]*[^}]";
		
		return line.matches(regexWhileStatement);
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
		statement.append("int " + DataUtil.generateVarName() + "=0;"); 
		statement.append(line.substring(idxCurlyBrackets+1));
		
		return statement.toString();
	}
}
