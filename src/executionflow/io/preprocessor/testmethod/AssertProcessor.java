package executionflow.io.preprocessor.testmethod;

import java.util.List;
import java.util.regex.Pattern;

import executionflow.io.SourceCodeProcessor;
import executionflow.util.DataUtil;
import executionflow.util.balance.RoundBracketBalance;

/**
 * Surround asserts with try-catch so that test method execution does not
 * stop if an assert fails.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		6.0.3
 * @since 		6.0.0
 */
public class AssertProcessor extends SourceCodeProcessor {

	//---------------------------------------------------------------------
	//		Attributes
	//---------------------------------------------------------------------
	private final RoundBracketBalance roundBracketsBalance;
	private boolean inAssert;
	
	
	//---------------------------------------------------------------------
	//		Constructor
	//---------------------------------------------------------------------
	public AssertProcessor(List<String> sourceCode) {
		super(sourceCode, true);
		
		roundBracketsBalance = new RoundBracketBalance();
		inAssert = false;
	}
	

	//---------------------------------------------------------------------
	//		Methods
	//---------------------------------------------------------------------	
	@Override
	protected String processLine(String line) {
		if (!inAssert && (line.contains("try ") || insideIncompleteTryCatch()))
			return line;
		
		String processedLine = line;
		
		if (inAssert)
			processedLine = processMultilineAssert(line);
		else if (isAssertInstruction(line))
			processedLine = processAssert(line);
		
		return processedLine;
	}
	
	private boolean insideIncompleteTryCatch() {
		return	getPreviousLine().contains("try ") 
				&& !getPreviousLine().contains("catch(");
	}
	
	private String processMultilineAssert(String line) {
		if (!roundBracketsBalance.parse(line).isBalanceEmpty())
			return line;

		inAssert = false;
		
		if (line.contains("}") && (line.lastIndexOf(';')) <= line.lastIndexOf("}")) {
			int lastCurlyBracketInSameLine = line.lastIndexOf("}");
			
			line = closeTryCatchStatement(line.substring(0, lastCurlyBracketInSameLine) , "") 
					+ line.substring(lastCurlyBracketInSameLine);
		}
		else {
			int endOfAssert = line.lastIndexOf(';') + 1;
			
			line = closeTryCatchStatement(line.substring(0, endOfAssert), "") 
					+ line.substring(endOfAssert);
		}
		
		return line;
	}
	
	private boolean isAssertInstruction(String line) {
		final Pattern assertPattern = 
				Pattern.compile("^(\\ |\\t)+(Assert\\.)?assert[A-z]+(\\ |\\t)*\\((.+\\);)?");
		
		return assertPattern.matcher(line).find();
	}
	
	private String processAssert(String line) {
		if (!roundBracketsBalance.parse(line).isBalanceEmpty()) {
			inAssert = true;
			
			return openTryCatchStatement(line);
		}
		
		if (hasInlineComment(line))
			line = processLineWithComment(line);
		else
			line = processLineWithoutComment(line);
		
		return line;
	}

	private boolean hasInlineComment(String line) {
		return line.contains("//") || line.contains("*/");
	}

	private String processLineWithComment(String line) {	
		String tryContent;
		
		if (line.contains("}"))
			tryContent = line.substring(0, line.lastIndexOf("}"));
		else
			tryContent = line.substring(0, getStartIndexOfInlineComment(line));
		
		return line.contains("}") ? 
				buildTryCatchStatement(tryContent, "") + "}"
				: buildTryCatchStatement(tryContent, "");
	}

	private int getStartIndexOfInlineComment(String line) {
		int commentStart = line.indexOf("//");
		
		if (commentStart == -1)
			commentStart = line.indexOf("*/");
		
		return commentStart;
	}
	
	private String buildTryCatchStatement(String tryContent, String catchContent) {
		return	openTryCatchStatement(tryContent) 
				+ closeTryCatchStatement("", catchContent);
	}
	
	private String openTryCatchStatement(String tryContent) {
		StringBuilder statement = new StringBuilder();
		
		statement.append("try {");
		statement.append(tryContent);
		
		return statement.toString();
	}
	
	private String closeTryCatchStatement(String tryContent, String catchContent) {
		StringBuilder statement = new StringBuilder();
		
		String catchVariable = DataUtil.generateVarName();
		String catchType = "Throwable";
		
		statement.append(tryContent);
		statement.append("} catch(");
		statement.append(catchType);
		statement.append(" ");
		statement.append(catchVariable);
		statement.append("){");
		statement.append(catchContent);
		statement.append("}");
		
		return statement.toString();
	}

	private String processLineWithoutComment(String line) {
		String tryContent;
		boolean closedCurlyBracketNextToAssert = false;
		
		if (line.contains("}")) {
			if (line.lastIndexOf(';') > line.lastIndexOf("}")) {
				tryContent = line.substring(0, line.lastIndexOf(';') + 1);
			}
			else {
				tryContent = line.substring(0, line.lastIndexOf("}"));
				
				closedCurlyBracketNextToAssert = true;
			}
		}
		else {
			tryContent = line;
		}
		
		return closedCurlyBracketNextToAssert ? 
				buildTryCatchStatement(tryContent, "") + "}" 
				: buildTryCatchStatement(tryContent, "");
	}
}
