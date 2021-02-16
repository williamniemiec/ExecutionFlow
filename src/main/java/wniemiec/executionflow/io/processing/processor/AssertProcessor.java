package wniemiec.executionflow.io.processing.processor;

import java.util.List;
import java.util.regex.Pattern;

import wniemiec.util.io.parser.balance.RoundBracketBalance;

/**
 * Surround asserts with try-catch so that test method execution does not
 * stop if an assert fails.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		7.0.0
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
		String processedLine = line;
		
		if (inAssert)
			processedLine = processMultilineAssert(line);
		else if (isAssertInstruction(line))
			processedLine = processAssert(line);
		
		return processedLine;
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
				Pattern.compile("(\\ |\\t|\\{)+(Assert\\.)?assert[A-z]+" +
								"(\\ |\\t)*\\((.+\\);)?");
		
		return	assertPattern.matcher(line).find()
				&& !isDeclaration(line);
	}

	private boolean isDeclaration(String line) {
		return line.matches(".*(public|protected|private|static|transient|" +
							"class|new)[\\s\\t]+.+");
	}

	private String processAssert(String line) {
		if (!roundBracketsBalance.parse(line).isBalanceEmpty()) {
			inAssert = true;
			
			return openTryCatchStatement(line);
		}
		
		if (hasInlineTryCatch(line))
			line = processLineWithInlineTryCatch(line);
		else if (hasInlineComment(line))
			line = processLineWithComment(line);
		else
			line = processLineWithoutComment(line);
		
		return line;
	}
	
	private boolean hasInlineTryCatch(String line) {
		return	line.matches("[\\s\\t]*try([\\s\\t]+\\{|\\{)[^;]+;\\}[\\s\\t]*"
				+ "catch([\\s\\t]+\\(|\\()[^)]+\\).*");
	}

	private String processLineWithInlineTryCatch(String line) {
		int idxStartTryContent = line.indexOf("{");
		int idxEndTryContent = line.substring(0, line.indexOf("catch")).lastIndexOf(";");
		
		String tryContent = line.substring(idxStartTryContent+1, idxEndTryContent+1);

		return	line.substring(0, idxStartTryContent+1) 
				+ buildTryCatchStatement(tryContent, "") 
				+ line.substring(idxEndTryContent+1);
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
		
		String catchVariable = generateVarName();
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
