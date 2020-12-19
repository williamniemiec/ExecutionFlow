package executionFlow.io.processor.invoked.holeplug;

import java.util.List;

import executionFlow.io.processor.SourceCodeProcessor;
import executionFlow.util.DataUtil;

public class TryCatchFinallyProcessor extends SourceCodeProcessor {

	//---------------------------------------------------------------------
	//		Attributes
	//---------------------------------------------------------------------
	
//	private boolean removedClosedCB;
	
	
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
	
	/**
	 * Adjust lines containing closed curly bracket + catch statement. Also, it
	 * will add an instruction in try and finally statements.
	 * 
	 * @param		line Line to be parsed
	 * @param		nextLine Line following the line to be parsed
	 * 
	 * @return		Parsed line		
	 */
//	public String parse(String line, String nextLine)
//	{
//		if (removedClosedCB) {
//			removedClosedCB = false;
//			line = line.substring(line.indexOf("}")+1);
//		}
//		
//		if (line.matches(REGEX_TRY_FINALLY)) {
//			line = putVariableNextToOpenCurlyBracket(line);
//		}
//		
//		final String REGEX_CATCH_NEXT_TO_CLOSED_CURLY_BRACKET = 
//				"(\\t|\\ )*\\}(\\t|\\ )*catch(\\t|\\ )*[\\(|\\{]+.*";
//		if (nextLine.matches(REGEX_CATCH_NEXT_TO_CLOSED_CURLY_BRACKET)) {
//			if (line.contains("//")) {
//				int idx_comment = line.indexOf("//");
//				
//				line = line.substring(0, idx_comment) + "}" + line.substring(idx_comment);
//			}
//			else
//				line += "}";
//			
//			removedClosedCB = true;
//		}
//		
//		return line;
//	}
}
