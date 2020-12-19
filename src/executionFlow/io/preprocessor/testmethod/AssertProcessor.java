package executionFlow.io.preprocessor.testmethod;

import java.util.List;
import java.util.regex.Pattern;

import executionFlow.util.DataUtil;
import executionFlow.util.balance.RoundBracketBalance;

/**
 * Adds try-catch to all asserts so that test method execution does not
 * stop if an assert fails.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.3
 * @since 		5.2.3
 */
public class AssertProcessor {

	//---------------------------------------------------------------------
	//		Attributes
	//---------------------------------------------------------------------
	private final RoundBracketBalance roundBracketsBalance;
	private boolean inAssert;
	private boolean inComment;
	
	
	//---------------------------------------------------------------------
	//		Constructor
	//---------------------------------------------------------------------
	public AssertProcessor() {
		roundBracketsBalance = new RoundBracketBalance();
		inAssert = false;
	}
	

	//---------------------------------------------------------------------
	//		Methods
	//---------------------------------------------------------------------
	public List<String> processLines(List<String> lines) {
		List<String> processedLines = lines;
		
		for (int i = 0; i < lines.size(); i++) {
			checkComments(lines.get(i));
			
			if (!inComment) {
				String processedLine = processLine(lines.get(i));
			
				processedLines.set(i, processedLine);
			}
		}
		
		return processedLines;
	}

	private void checkComments(String line) {
		final String regexCommentFullLine = 
				"^(\\t|\\ )*(\\/\\/|\\/\\*|\\*\\/|\\*).*";
		
		if (line.matches(regexCommentFullLine))
			inComment = true;
		
		if (line.contains("/*") && !line.contains("*/")) {
			inComment = true;
		}
		else if (inComment && line.contains("*/")) {
			inComment = false;
		}
	}


	private String processLine(String line) {
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
		
		int idxLastSemicolon = line.lastIndexOf(';');
		int lastCurlyBracketInSameLine = line.lastIndexOf("}");
		int endOfAssert = idxLastSemicolon + 1;
		
		inAssert = false;
		
		if (lastCurlyBracketInSameLine != -1) {
			if ((idxLastSemicolon) > lastCurlyBracketInSameLine)
				lastCurlyBracketInSameLine = idxLastSemicolon+1;
			
			line = buildTryCatchStatement(line.substring(0, lastCurlyBracketInSameLine) , "") 
					+ line.substring(lastCurlyBracketInSameLine);
		}
		else {
			line = buildTryCatchStatement(line.substring(0, endOfAssert), "") 
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
			return "try {" + line;
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
		
		if (line.contains("}")) {
			tryContent = line.substring(0, line.lastIndexOf("}") + 1);
		}
		else {
			tryContent = line.substring(0, getStartIndexOfInlineComment(line));
		}
		
		return buildTryCatchStatement(tryContent, "");
	}

	private int getStartIndexOfInlineComment(String line) {
		int commentStart = line.indexOf("//");
		
		if (commentStart == -1)
			commentStart = line.indexOf("*/");
		
		return commentStart;
	}

	private String processLineWithoutComment(String line) {
		String tryContent;
		
		if (line.contains("}")) {
			int lastCurlyBracketInSameLine = line.lastIndexOf("}");
			int idxLastSemicolon = line.lastIndexOf(';');

			if (idxLastSemicolon > lastCurlyBracketInSameLine)
				tryContent = line.substring(0, idxLastSemicolon + 1);
			else
				tryContent = line.substring(0, lastCurlyBracketInSameLine + 1);
		}
		else {
			tryContent = line;
		}
		
		return buildTryCatchStatement(tryContent, "");
	}
	
	private String buildTryCatchStatement(String tryContent, String catchContent) {
		StringBuilder statement = new StringBuilder();
		
		String catchVariable = DataUtil.generateVarName();
		String catchType = "Throwable";
		
		statement.append("try {");
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
}
