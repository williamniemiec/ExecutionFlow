package executionFlow.io.processor.invoked.holeplug;

import java.util.List;
import java.util.regex.Pattern;

import executionFlow.io.SourceCodeProcessor;
import executionFlow.util.DataUtil;

/**
 * Makes adjustments to lines of code to allow test paths to be
 * computed correctly. It will add an instruction in do statements.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.3
 * @since 		5.2.3
 */
public class DoWhileProcessor extends SourceCodeProcessor {

	//---------------------------------------------------------------------
	//		Constructor
	//---------------------------------------------------------------------
	protected DoWhileProcessor(List<String> sourceCode) {
		super(sourceCode, true);
	}
	
	
	//---------------------------------------------------------------------
	//		Methods
	//---------------------------------------------------------------------
	@Override
	protected String processLine(String line) {		
		if (!isDoKeyword(line))
			return line;
		
		return putVariableNextToOpenCurlyBracket(line);
	}
	
	private boolean isDoKeyword(String line) {
		final Pattern patternDo = Pattern.compile("[\\t\\s\\}]*do[\\n\\s\\t\\{]+");
		
		return patternDo.matcher(line).find();
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
