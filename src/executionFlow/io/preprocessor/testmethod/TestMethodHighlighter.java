package executionFlow.io.preprocessor.testmethod;

import java.util.ArrayList;
import java.util.List;

import executionFlow.info.InvokedInfo;
import executionFlow.util.DataUtil;
import executionFlow.util.balance.CurlyBracketBalance;

public class TestMethodHighlighter {

	//---------------------------------------------------------------------
	//		Attributes
	//---------------------------------------------------------------------
	private static final String REGEX_REPEATED_TEST = 
			".*@(.*\\.)?RepeatedTest(\\ |\\t)*\\(.+\\)(\\ |\\t)*";
	private static final String REGEX_JUNIT4_TEST = 
			".*@(.*\\.)?(org\\.junit\\.)?Test(\\ |\\t)*(\\ |\\t)*(\\(.*\\))?";
	private static final String REGEX_PARAMETERIZED_TEST = 
			".*@(.*\\.)?(org\\.junit\\.jupiter\\.params\\.)?ParameterizedTest(\\ |\\t)*(\\ |\\t)*";
	private CurlyBracketBalance curlyBracketBalance_currentTestMethod;
	private CurlyBracketBalance curlyBracketBalance_ignore;
	private boolean ignoreMethod;
	private boolean inTestAnnotationScope;
	private boolean isSelectedTestMethod;
	private boolean inComment;
	private int currentLine;
	private String testMethodSignature;
	private static int totalIgnored;
	private List<Integer> ignoredMethods;
	private boolean inTestMethodSignature;
	
	
	//---------------------------------------------------------------------
	//		Constructor
	//---------------------------------------------------------------------
	public TestMethodHighlighter(String testMethodSignature) {
		this.testMethodSignature = InvokedInfo.extractMethodName(testMethodSignature) + 
		testMethodSignature.substring(testMethodSignature.indexOf("(")).replace(" ", "");
		
//		this.testMethodSignature = testMethodSignature;
		this.ignoredMethods = new ArrayList<>();
	}

	
	//---------------------------------------------------------------------
	//		Methods
	//---------------------------------------------------------------------
	public List<String> processLines(List<String> lines) {
		List<String> processedLines = lines;
		
		currentLine = 1;
		
		for (int i = 0; i < lines.size(); i++) {
			checkComments(lines.get(i));
			
			if (!inComment) {
				String processedLine = processLine(lines.get(i));
			
				processedLines.set(i, processedLine);
			}
			
			currentLine++;
		}
		
		commentIgnoredMethods(lines);
		
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
		if (line.contains("@") && !inTestAnnotationScope) {
			inTestAnnotationScope =	line.matches(REGEX_JUNIT4_TEST) || 
									line.matches(REGEX_REPEATED_TEST) || 
									line.matches(REGEX_PARAMETERIZED_TEST) || 
									line.contains("@org.junit.jupiter.api.Test");
		}

		if (inTestAnnotationScope) {
			if (curlyBracketBalance_currentTestMethod != null) {
				curlyBracketBalance_currentTestMethod.parse(line);
			}
			else if (isMethodDeclaration(line)) {
				curlyBracketBalance_currentTestMethod = new CurlyBracketBalance();
				curlyBracketBalance_currentTestMethod.parse(line);
				isSelectedTestMethod = extractMethodSignatureFromLine(line).replace(" ", "").equals(testMethodSignature);
			}
		
			if (curlyBracketBalance_currentTestMethod != null) {
				if (!isSelectedTestMethod) { 
					line = parseIgnore(line);
				}
				
				if (curlyBracketBalance_currentTestMethod.isBalanceEmpty()) {
					curlyBracketBalance_currentTestMethod = null;
					inTestAnnotationScope = false;
					isSelectedTestMethod = false;
				}
			}
		}
		
		return line;
	}
	
	/**
	 * Checks whether current source file line should be ignored. If yes,
	 * comments the entire line. It will be ignored methods other than the 
	 * provided test method ({@link #testMethodSignature}).
	 * 
	 * @param		line Current source file line
	 * 
	 * @return		Processed line (it will be commented if it should be 
	 * ignored)
	 * 
	 * @implNote	Only the body of the methods will be commented, 
	 * requiring a second scan of the file to comment on the 
	 * declaration of the methods as well as their annotations.
	 */
	private String parseIgnore(String line)
	{			
		if (ignoreMethod) {
			if (curlyBracketBalance_ignore == null)
				curlyBracketBalance_ignore = new CurlyBracketBalance();
			
			if (curlyBracketBalance_ignore.isBalanceEmpty())
				ignoredMethods.add(currentLine);
			
			curlyBracketBalance_ignore.parse(line);
			
			ignoreMethod = !curlyBracketBalance_ignore.isBalanceEmpty();
		}
		
		// If current method it is not the given test method, ignores it		
		else if (isMethodDeclaration(line) && !line.contains("private ") && 
				!extractMethodSignatureFromLine(line).replace(" ", "").equals(testMethodSignature)) {
			if (curlyBracketBalance_ignore == null)
				curlyBracketBalance_ignore = new CurlyBracketBalance();

			curlyBracketBalance_ignore.parse(line);
			inTestMethodSignature = false;
			ignoreMethod = true;
			ignoredMethods.add(currentLine);
		}
		
		return line;
	}
	
	private void commentIgnoredMethods(List<String> lines) {
		totalIgnored = ignoredMethods.size();
		
		// Comments test annotations and method declaration from all tests
		// except the test method provided
		if (!ignoredMethods.isEmpty()) {
			boolean insideMethod = false;
			
			
			currentLine--;
			// Comments annotations of ignored methods
			for (int i=lines.size()-1; i>=0; i--) {
				String line = lines.get(i);
				
				if (insideMethod) {
					if (!line.contains("@"))
						insideMethod = false;
					else
						line = "//" + line;		
				}
				else if (ignoredMethods.contains(currentLine)) {
					line = "//" + line;
					insideMethod = true;
				}
				
				lines.set(i, line);
				currentLine--;
			}
			
			// Comments body of ignored methods			
			for (int idx : ignoredMethods) {
				CurlyBracketBalance cbb = new CurlyBracketBalance();
				
				String line = lines.get(idx-1);
				
				while (!line.contains("{")) {
					lines.set(idx-1, "//" + line);
					idx++;
					line = lines.get(idx-1);
				}
				
				do {
					line = lines.get(idx-1);
					cbb.parse(line);
					lines.set(idx-1, "//" + line);
					idx++;

				}
				while (!cbb.isBalanceEmpty());
				
			}
		}
	}
	
	private boolean isMethodDeclaration(String line) {
		final String regexMethodDeclaration = 
				"^.*[\\s\\t]+((?!new)[A-z0-9\\_\\<\\>\\,\\[\\]\\.\\$])+"
				+ "[\\s\\t]+([A-z0-9\\_\\$]+)[\\s\\t]*\\(.*\\).*$";
		
		return line.matches(regexMethodDeclaration);
	}
	
	
	
	
	/**
	 * Gets method signature from a source file line;
	 * 
	 * @param		line Source file line
	 * 
	 * @return		Method signature
	 */
	private String extractMethodSignatureFromLine(String line)
	{
		StringBuilder signature = new StringBuilder();
		String methodParams = extractParametersFromMethodDeclaration(line);			
		
		signature.append(extractMethodNameFromMethodDeclaration(line));
		
		if (methodParams.isBlank()) {
			signature.append("()");
		}
		else {
			signature.append("(");
			signature.append(methodParams);
			signature.append(")");
		}
		
		return signature.toString();
	}


	private String extractParametersFromMethodDeclaration(String line) {
		StringBuilder methodParams = new StringBuilder();
		String methodParamsAndArgs = DataUtil.extractContentBetweenParenthesis(line);
		
		if (methodParamsAndArgs.isBlank())
			return "";
		
		for (String param : methodParamsAndArgs.split(",")) {
			methodParams.append(param.trim().split(" ")[0]);
			methodParams.append(",");
		}
		
		// Removes last comma
		if (methodParams.length() > 1) {
			methodParams.deleteCharAt(methodParams.length()-1);
		}
		
		return methodParams.toString();
	}
	
	private String extractMethodNameFromMethodDeclaration(String line) {
		String methodName = line.substring(
				line.substring(0, line.indexOf("(")).lastIndexOf(" "), 
				line.lastIndexOf("(")
		);
		return methodName;
	}
}
