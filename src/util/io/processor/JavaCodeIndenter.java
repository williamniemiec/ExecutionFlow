package util.io.processor;

import java.util.ArrayList;
import java.util.List;

import util.io.parser.balance.CurlyBracketBalance;

/**
 * Indents Java codes.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @see			https://github.com/williamniemiec/code-indenter/java
 */
public class JavaCodeIndenter extends Indenter {

	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private boolean inMultiLineComment;
	private boolean inInlineComment;
	private boolean inCaseBody;
	private boolean inIfElseWithoutBody;
	private CurlyBracketBalance curlyBracketBalance;
	private String indentationWithFactor;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	/**
	 * Indents Java code using a type of indentation multiplied by a certain 
	 * factor.
	 * 
	 * @param		type Type of indentation to be used
	 * @param		factor Number of times the type of indentation will appear
	 * at an indentation level 
	 * 
	 * @throws		IllegalArgumentException If type is null or factor is negative
	 */
	public JavaCodeIndenter(IndentationType type, int factor) {
		if (type == null)
			throw new IllegalArgumentException("Type cannot be null");
		
		if (factor < 0) {
			throw new IllegalArgumentException("Factor cannot be negative");
		}
		
		curlyBracketBalance = new CurlyBracketBalance();
		indentationWithFactor = createIndentation(type, factor);
	}
	
	/**
	 * Indents Java code using tabulated indentation.
	 */
	public JavaCodeIndenter() {
		this(IndentationType.TAB, 1);
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	private String createIndentation(IndentationType type, int factor) {
		StringBuilder indentation = new StringBuilder();
		
		for (int i = 0; i < factor; i++)
			indentation.append(type.getType());
		
		return indentation.toString();
	}
	
	/**
	 * Indent lines of Java code.
	 * 
	 * @param		lines Java code
	 * 
	 * @return		Indented code
	 * 
	 * @throws		IllegalArgumentException If lines is null
	 */
	@Override
	public List<String> indent(List<String> lines) {
		if (lines == null)
			throw new IllegalArgumentException("Lines cannot be null");
		
		List<String> processedLines = new ArrayList<>();
		
		for (int i = 0; i < lines.size(); i++) {
			String currentLine = lines.get(i);
			String lastLine = (i == 0) ? "" : lines.get(i-1);
			
			beforeIndentation(lastLine, currentLine);
			processedLines.add(indentLine(currentLine));
			afterIndentation(currentLine);
		}
		
		return processedLines;
	}
	
	private void beforeIndentation(String lastLine, String currentLine) {
		checkComments(currentLine);	
		
		if (inComment(currentLine))
			return;
		
		if (isCaseBody(lastLine, currentLine))
			inCaseBody = true;
		
		if (isIfElseWithoutCurlyBracket(lastLine, currentLine))
			inIfElseWithoutBody = true;
	}
	
	private void checkComments(String line) {
		checkInlineComment(line);
		checkMultiLineComment(line);
	}
	
	private void checkInlineComment(String str) {
		final String regexCommentFullLine = "^[\\t\\s]*(\\/\\/).*";
		
		inInlineComment = str.matches(regexCommentFullLine);
	}
	
	private void checkMultiLineComment(String line) {
		if (line.contains("/*") && !line.contains("*/")) {
			inMultiLineComment = true;
		}
		else if (inMultiLineComment && line.contains("*/")) {
			inMultiLineComment = false;
		}
	}
	
	private boolean inComment(String line) {
		return	inMultiLineComment
				|| inInlineComment
				|| line.contains("*/");
	}
	
	private boolean isCaseBody(String lastLine, String currentLine) {
		return lastLine.contains("case ") && !currentLine.contains("case ");
	}
	
	private boolean isLastLineOfCaseBody(String line) {
		return	line.contains("case ") 
				|| line.contains("break;") 
				|| line.contains("return;");
	}
	
	private boolean isIfElseWithoutCurlyBracket(String lastLine, String currentLine) {
		return	lastLine.matches("[\\s\\t]*(else )?if[\\s\\t]+\\([^{}]+\\)([^{}]+|$)") 
				&& !currentLine.contains("{");
	}
	
	private String indentLine(String line) {		
		return generateIndentation(line) + line;
	}
	
	private String generateIndentation(String line) {
		StringBuilder indentation = new StringBuilder();
		int deepth = calculateDeepth(line);
		
		for (int i = 0; i < deepth; i++) {
			indentation.append(indentationWithFactor);
		}
		
		return indentation.toString();
	}
	
	private int calculateDeepth(String line) {
		int deepth = curlyBracketBalance.getBalance();
		
		if (!inComment(line) && hasClosingCurlyBracket(line))
			deepth--;
		else if (inCaseBody || inIfElseWithoutBody)
			deepth++;
		
		return deepth;
	}
	
	private boolean hasClosingCurlyBracket(String line) {
		if (!line.contains("}"))
			return false;
		
		if (!line.contains("{"))
			return true;
		
		return line.indexOf("}") < line.indexOf("{");
	}
	
	private void afterIndentation(String currentLine) {
		if (inComment(currentLine))
			return;
		
		curlyBracketBalance.parse(currentLine);
		
		if (inCaseBody && isLastLineOfCaseBody(currentLine))
			inCaseBody = false;
		
		if (inIfElseWithoutBody)
			inIfElseWithoutBody = false;
	}
}
