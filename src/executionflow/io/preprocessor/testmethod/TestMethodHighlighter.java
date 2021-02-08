package executionflow.io.preprocessor.testmethod;

import java.util.ArrayList;
import java.util.List;

import executionflow.info.InvokedInfo;
import executionflow.io.SourceCodeProcessor;
import util.io.parser.balance.CurlyBracketBalance;

/**
 * Comments on all test methods except the one with the signature provided.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		6.0.4
 * @since 		6.0.0
 */
public class TestMethodHighlighter extends SourceCodeProcessor {

	//---------------------------------------------------------------------
	//		Attributes
	//---------------------------------------------------------------------
	private CurlyBracketBalance curlyBracketBalanceTestMethod;
	private CurlyBracketBalance curlyBracketBalanceIgnoredMethod;
	private boolean ignoreMethod;
	private boolean inTestAnnotationScope;
	private boolean isTestMethodToBeHighlighted;
	private String testMethodSignature;
	private List<Integer> ignoredMethods;
	
	
	//---------------------------------------------------------------------
	//		Constructor
	//---------------------------------------------------------------------
	public TestMethodHighlighter(List<String> sourceCode, 
								 String testMethodSignature) {
		super(sourceCode, true);
		
		this.testMethodSignature = 
				InvokedInfo.extractMethodName(testMethodSignature)
				+ testMethodSignature.substring(testMethodSignature.indexOf("("))
									 .replace(" ", "");
		this.ignoredMethods = new ArrayList<>();
	}

	
	//---------------------------------------------------------------------
	//		Methods
	//---------------------------------------------------------------------
	@Override
	protected String processLine(String line) {
		if (isAnnotation(line) && !inTestAnnotationScope)
			inTestAnnotationScope =	isTestAnnotation(line);
		
		if (!inTestAnnotationScope)
			return line;
		
		if (isInsideTestMethod()) {
			updateCurlyBracketBalance(line);
		}
		else if (isTestMethodDeclaration(line)) {
			initializeCurlyBracketBalance(line);
			isTestMethodToBeHighlighted = isTheMethodToBeHighlighted(line);
		}
		
		if (isInsideTestMethod() && !isTestMethodToBeHighlighted)
			parseIgnore(line);
		
		if (isLastLineOfMethod())
			endOfMethod();
		
		return line;
	}
	
	private boolean isAnnotation(String line) {
		return line.contains("@");
	}
	
	private boolean isTestAnnotation(String line) {
		return	isJUnit4Annotation(line)
				|| isJUnit5Annotation(line)
				|| isJUnit5RepeatedTestAnnotation(line)
				|| isJUnit5ParameterizedTest(line);
	}
	
	private boolean isJUnit5Annotation(String line) {
		return line.contains("@org.junit.jupiter.api.Test");
	}
	
	private boolean isJUnit4Annotation(String line) {
		final String regexJUnit4Test = ".*@(.*\\.)?"
				+ "(org\\.junit\\.)?Test(\\ |\\t)*(\\ |\\t)*(\\(.*\\))?.*";
		
		return line.matches(regexJUnit4Test);
	}

	private boolean isJUnit5RepeatedTestAnnotation(String line) {
		final String regexRepeatedTest = ".*@(.*\\.)?"
				+ "RepeatedTest(\\ |\\t)*\\(.+\\)(\\ |\\t)*.*";
		
		return line.matches(regexRepeatedTest);
	}
	
	private boolean isJUnit5ParameterizedTest(String line) {
		final String regexParameterizedTest = ".*@(.*\\.)?"
				+ "(org\\.junit\\.jupiter\\.params\\.)?"
				+ "ParameterizedTest(\\ |\\t)*(\\ |\\t)*.*";
		
		return line.matches(regexParameterizedTest);
	}
	
	private boolean isInsideTestMethod() {
		return	(curlyBracketBalanceTestMethod != null);
	}
	
	private void updateCurlyBracketBalance(String line) {
		curlyBracketBalanceTestMethod.parse(line);
	}
	
	private boolean isTestMethodDeclaration(String line) {
		final String regexMethodDeclaration = 
				"^.*[\\s\\t]+((?!new)[A-z0-9\\_\\<\\>\\,\\[\\]\\.\\$])+"
				+ "[\\s\\t]+([A-z0-9\\_\\$]+)[\\s\\t]*\\(.*\\).*$";
		
		return	line.matches(regexMethodDeclaration)
				&& !line.contains("private ");
	}
	
	private void initializeCurlyBracketBalance(String line) {
		curlyBracketBalanceTestMethod = new CurlyBracketBalance();
		curlyBracketBalanceTestMethod.parse(line);
	}
	
	private boolean isTheMethodToBeHighlighted(String line) {
		String methodSignature = extractMethodSignatureFromLine(line);
		methodSignature = methodSignature.replace(" ", "");

		return methodSignature.equals(testMethodSignature);
	}
	
	private String extractMethodSignatureFromLine(String line) {
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
		
		String methodParamsAndArgs = extractContentBetweenParenthesis(line);
		methodParamsAndArgs = methodParamsAndArgs.replaceAll("final ", "");
		
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
		
		return line.substring(
				extractMethodSignatureWithoutParameters(line).lastIndexOf(" "), 
				line.lastIndexOf("(")
		);
	}
	
	private String extractMethodSignatureWithoutParameters(String signature) {
		return signature.substring(0, signature.indexOf("("));
	}
	
	private boolean isLastLineOfMethod() {
		return	(curlyBracketBalanceTestMethod != null)
				&& curlyBracketBalanceTestMethod.isBalanceEmpty();
	}
	
	private void endOfMethod() {
		resetCurlyBracketBalance();
		
		inTestAnnotationScope = false;
		isTestMethodToBeHighlighted = false;
	}
	
	private void resetCurlyBracketBalance() {
		curlyBracketBalanceTestMethod = null;
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
	private void parseIgnore(String line) {			
		if (ignoreMethod) {
			checkIfCurlyBracketBalanceOfIgnoredMethodIsInitialized();
			
			if (isLastLineOfMethodToBeIgnored())
				ignoredMethods.add(getCurrentIndex());
			
			updateCurlyBracketBalanceOfIgnoredMethod(line);
			
			ignoreMethod = !isLastLineOfMethodToBeIgnored();
		}
		else if (isTestMethodDeclaration(line) && !isTheMethodToBeHighlighted(line)) {
			checkIfCurlyBracketBalanceOfIgnoredMethodIsInitialized();
			updateCurlyBracketBalanceOfIgnoredMethod(line);
			
			ignoreMethod = true;
			ignoredMethods.add(getCurrentIndex());
		}
	}
	
	private void checkIfCurlyBracketBalanceOfIgnoredMethodIsInitialized() {
		if (curlyBracketBalanceIgnoredMethod == null)
			curlyBracketBalanceIgnoredMethod = new CurlyBracketBalance();
	}
	
	private void updateCurlyBracketBalanceOfIgnoredMethod(String line) {
		curlyBracketBalanceIgnoredMethod.parse(line);
	}
	
	private boolean isLastLineOfMethodToBeIgnored() {
		return	(curlyBracketBalanceIgnoredMethod != null)
				&& curlyBracketBalanceIgnoredMethod.isBalanceEmpty();
	}
	
	@Override
	protected void whenFinished(List<String> processedLines) {
		commentIgnoredMethods(processedLines);
	}
	
	private void commentIgnoredMethods(List<String> lines) {
		if (ignoredMethods.isEmpty())
			return;

		commentHeaderOfIgnoredMethods(lines);		
		commentBodyOfIgnoredMethods(lines);
	}

	private void commentHeaderOfIgnoredMethods(List<String> lines) {
		boolean insideMethod = false;
		
		for (int i=lines.size()-1; i>=0; i--) {
			String line = lines.get(i);
			
			if (insideMethod) {
				if (!isAnnotation(line) && !line.contains("//"))
					insideMethod = false;
				else
					line = "//" + line;		
			}
			else if (ignoredMethods.contains(i)) {
				insideMethod = true;
			}
			
			lines.set(i, line);
		}
	}
	
	private void commentBodyOfIgnoredMethods(List<String> lines) {
		for (int idx : ignoredMethods) {
			CurlyBracketBalance cbb = new CurlyBracketBalance();
			String line = lines.get(idx);			
			
			while (!endWithOpenCurlyBracket(line)) {
				lines.set(idx, "//" + line);
				idx++;
				line = lines.get(idx);
			}
			
			do {
				line = lines.get(idx);
				cbb.parse(line);
				lines.set(idx, "//" + line);
				idx++;
			}
			while (!cbb.isBalanceEmpty());
		}
	}
	
	private boolean endWithOpenCurlyBracket(String str) {
		return removeInlineComments(str).matches(".*\\{[\\s\\t]*$");
	}
	
	private String removeInlineComments(String str) {
		if (!str.contains("//"))
			return str;
		
		return str.substring(0, str.lastIndexOf("//"));
	}
}
