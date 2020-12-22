package executionFlow.io.processor.invoked.holeplug;

import java.util.List;

import executionFlow.io.SourceCodeProcessor;
import executionFlow.util.DataUtil;

/**
 * 
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.3
 * @since 		5.2.3
 */
public class TryCatchFinallyProcessor extends SourceCodeProcessor {

	//---------------------------------------------------------------------
	//		Constructor
	//---------------------------------------------------------------------
	protected TryCatchFinallyProcessor(List<String> sourceCode) {
		super(sourceCode, true);
	}
	
	
	//---------------------------------------------------------------------
	//		Methods
	//---------------------------------------------------------------------
	@Override
	protected String processLine(String line) {
		if (!isTryOrFinallyStatement(line))
			return line;
		
		return putVariableNextToOpenCurlyBracket(line);
	}
	
	private boolean isTryOrFinallyStatement(String line) {
		final String regexTryFinally = "[\\t\\s\\}]*(try|finally)[\\s\\t\\{]+";
		
		return line.matches(regexTryFinally);
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
