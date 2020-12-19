package executionFlow.io.preprocessor.testmethod;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import executionFlow.util.DataUtil;
import executionFlow.util.balance.CurlyBracketBalance;

/**
 * Responsible for disabling collectors in a test method and converting
 * JUnit 5 test annotation to JUnit 4 test annotation.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.3
 * @since 		5.2.3
 */
public class AnnotationProcessor {
	
	//---------------------------------------------------------------------
	//		Attributes
	//---------------------------------------------------------------------
	private boolean inComment;
	private TestAnnotationProcessor testAnnotationProcessor;
	private RepeatedTestAnnotationProcessor repeatedAnnotationProcessor;
	private ParameterizedTestAnnotationProcessor parameterizedAnnotationProcessor;
	private static final String REGEX_REPEATED_TEST = 
			".*@(.*\\.)?RepeatedTest(\\ |\\t)*\\(.+\\)(\\ |\\t)*";
	private static final String REGEX_JUNIT4_TEST = 
			".*@(.*\\.)?(org\\.junit\\.)?Test(\\ |\\t)*(\\ |\\t)*(\\(.*\\))?";
	private static final String REGEX_PARAMETERIZED_TEST = 
			".*@(.*\\.)?(org\\.junit\\.jupiter\\.params\\.)?ParameterizedTest(\\ |\\t)*(\\ |\\t)*";
	private Object[] testMethodArgs;
	private int totalTests;
	
	
	//---------------------------------------------------------------------
	//		Constructor
	//---------------------------------------------------------------------
	public AnnotationProcessor(Object[] testMethodArgs) {
		this.testMethodArgs = testMethodArgs;
		testAnnotationProcessor = new TestAnnotationProcessor();
		repeatedAnnotationProcessor = new RepeatedTestAnnotationProcessor();
		parameterizedAnnotationProcessor = new ParameterizedTestAnnotationProcessor(testMethodArgs);
	}
	
	
	//---------------------------------------------------------------------
	//		Methods
	//---------------------------------------------------------------------
	/**
	 * Processes a line disabling collectors in a test method and converting
	 * JUnit 5 test annotation to JUnit 4 test annotation.
	 * 
	 * @param		line Line to be parsed
	 * 
	 * @return		Processed line
	 */
	public List<String> processLines(List<String> lines) {
		List<String> processedLines = lines;
		
		for (int i = 0; i < lines.size(); i++) {
			checkComments(lines.get(i));
			countTest(lines.get(i));
			
			if (!inComment) {
				String processedLine = processLine(lines.get(i));
			
				processedLines.set(i, processedLine);
			}
		}
		
		return processedLines;
	}
	
	private void countTest(String line) {
		if (line.contains("@org.junit.jupiter.api.Test") ||
				line.matches(REGEX_JUNIT4_TEST) ||
				line.matches(REGEX_REPEATED_TEST) ||
				line.matches(REGEX_PARAMETERIZED_TEST))
			totalTests++;
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
		
		processedLine = repeatedAnnotationProcessor.parseInsideRepeatedTest(line);
		
		// Converts test annotation from JUnit 5 to JUnit 4
		if (line.matches(REGEX_JUNIT4_TEST))
			processedLine = testAnnotationProcessor.parseJUnit4TestAnnotation(line);
		else if (line.contains("@org.junit.jupiter.api.Test"))
			processedLine = testAnnotationProcessor.parseJUnit5TestAnnotation(line);
		else if (line.matches(REGEX_REPEATED_TEST))
			processedLine = repeatedAnnotationProcessor.parseRepeatedTest(line);
		else if (line.matches(REGEX_PARAMETERIZED_TEST) || testMethodArgs != null)
			processedLine = parameterizedAnnotationProcessor.parseParameterizedTest(line);
		
		return processedLine;
	}
	
	//-------------------------------------------------------------------------
	//		Getters
	//-------------------------------------------------------------------------
	public int getTotalTests() {
		return totalTests;
	}
	
	
	//-------------------------------------------------------------------------
	//		Inner classes
	//-------------------------------------------------------------------------
	private class TestAnnotationProcessor {
		private static final String REGEX_JUNIT4_TEST = 
				".*@(.*\\.)?(org\\.junit\\.)?Test(\\ |\\t)*(\\ |\\t)*(\\(.*\\))?";
		
		public boolean isTestAnnotation(String line) {
			return line.matches(REGEX_JUNIT4_TEST);
		}
		
		/**
		 * Converts JUnit 5 test to JUnit 4 test (converts 
		 * {@link org.junit.jupiter.api.Test} to {@link org.junit.Test}. Also,
		 * converts '@Test' to '@org.junit.Test'.
		 * 
		 * @param		line Current source file line
		 * @param		isJunit5 Indicates if current source file line belongs
		 * to a JUnit 5 test.
		 * 
		 * @return		Processed line		
		 */
		private String parseJUnit4TestAnnotation(String line)
		{
			return line.replace("@Test", "@org.junit.Test");
		}	
		
		private String parseJUnit5TestAnnotation(String line) {
			return line.replace("@org.junit.jupiter.api.Test", "@org.junit.Test");
		}
	}
	
	private class RepeatedTestAnnotationProcessor {
		private static final String REGEX_REPEATED_TEST = 
				".*@(.*\\.)?RepeatedTest(\\ |\\t)*\\(.+\\)(\\ |\\t)*";
		private String numRepetitions;
		private CurlyBracketBalance curlyBracketBalance_repeatedTest;
		private boolean repeatedTest_putLoop;
		private boolean insideRepeatedTest;
		
		
		/**
		 * Checks whether it is inside a method with 
		 * {@link org.junit.jupiter.api.RepeatedTest}. If it is, converts it to
		 * {@link org.junit.Test} using while control flow instead.
		 * 
		 * @param		line Current source file line
		 * 
		 * @return		If is is inside a repeated test, while control flow 
		 * resulting from the conversion of 
		 * {@link org.junit.jupiter.api.RepeatedTest} to {@link org.junit.Test};
		 * otherwise, returns the same line
		 */
		private String parseInsideRepeatedTest(String line)
		{
			// Converts repeated test to while test
			if (repeatedTest_putLoop && line.contains("{")) {
				line = putWhileStatement(line);
				
				repeatedTest_putLoop = false;
				
				curlyBracketBalance_repeatedTest = new CurlyBracketBalance();
				curlyBracketBalance_repeatedTest.parse(line);
				curlyBracketBalance_repeatedTest.decreaseBalance();
			}
			// Checks if it is the end of repeated test
			else if (insideRepeatedTest && curlyBracketBalance_repeatedTest != null && line.contains("}")) {
				curlyBracketBalance_repeatedTest.parse(line);
				
				if (curlyBracketBalance_repeatedTest.isBalanceEmpty()) {
					line = closeWhileStatement(line);
				}
			}
			
			return line;
		}

		private String closeWhileStatement(String line) {
			int idx = line.lastIndexOf("}");
			
			
			insideRepeatedTest = false;
			curlyBracketBalance_repeatedTest = null;
			
			line = line.substring(0, idx+1) + "}" + line.substring(idx+1);
			return line;
		}
		
		/**
		 * Converts {@link org.junit.jupiter.api.RepeatedTest} to 
		 * {@link org.junit.Test}.
		 * 
		 * @param		line Current source file line
		 * 
		 * @return		Processed line
		 */
		private String parseRepeatedTest(String line)
		{
			insideRepeatedTest = true;
			
			Pattern patternRepeatedTest = Pattern.compile(REGEX_REPEATED_TEST);
			Matcher matcherRepeatedTest = patternRepeatedTest.matcher(line);
			if (matcherRepeatedTest.find()) {
				String repeatedTestAnnotation = matcherRepeatedTest.group();
				
				
				line = line.replace(repeatedTestAnnotation, "@org.junit.Test");
			}
			
			numRepetitions = extractNumberOfRepetitions(line);
			
			repeatedTest_putLoop = true;
			
			return line;
		}

		private String extractNumberOfRepetitions(String line) {
			String numRepetitions = "0";
			
			Pattern patternRepeatedTest = Pattern.compile(REGEX_REPEATED_TEST);
			Matcher matcherRepeatedTest = patternRepeatedTest.matcher(line);
			
			if (matcherRepeatedTest.find()) {
				String annotationParams = DataUtil.extractContentBetweenParenthesis(matcherRepeatedTest.group());
				
				if (annotationParams.contains("value")) {
					Matcher matcherNumbers = Pattern.compile("[0-9]+").matcher(annotationParams);
					
					if (matcherNumbers.find())
						numRepetitions = matcherNumbers.group();
				}
				else {
					numRepetitions = annotationParams.isBlank() ? "0" : annotationParams;						
				}
				
			}
			
			return numRepetitions;
		}

		/**
		 * Converts repeated test to a while loop.
		 * 
		 * @param		line Line to be placed the while clause
		 * 
		 * @return		Line with while clause along with the function arguments
		 */
		private String putWhileStatement(String line)
		{
			StringBuilder statement = new StringBuilder();
			String stepVariable = DataUtil.generateVarName();
			int idxLastOpenCurlyBracket = line.lastIndexOf("{");
			
			statement.append(line.substring(0, idxLastOpenCurlyBracket + 1));
			statement.append("int ");
			statement.append(stepVariable);
			statement.append("=0;");
			statement.append("while(");
			statement.append(stepVariable);
			statement.append("++ < ");
			statement.append(numRepetitions);
			statement.append("){");
			statement.append(line.substring(idxLastOpenCurlyBracket + 1));
			
			return statement.toString();
		}
	}
	
	private class ParameterizedTestAnnotationProcessor {
		private Object[] testMethodArgs;
		private String[] testMethodParams;
//		private CurlyBracketBalance curlyBracketBalance_parameterizedTest;
		private boolean inParameterizedTestMethod;
		private String paramEnumType;
		
		public ParameterizedTestAnnotationProcessor(Object[] testMethodArgs) {
			this.testMethodArgs = testMethodArgs;
		}
		
		private static final String REGEX_PARAMETERIZED_TEST = 
				".*@(.*\\.)?(org\\.junit\\.jupiter\\.params\\.)?ParameterizedTest(\\ |\\t)*(\\ |\\t)*";
		
		/**
		 * Converts {@link org.junit.jupiter.params.ParameterizedTest} to 
		 * {@link org.junit.Test}.
		 * 
		 * @param		line Current source file line
		 * 
		 * @return		Processed line
		 */
		@SuppressWarnings("rawtypes")
		private String parseParameterizedTest(String line)
		{
			if (line.contains("@ParameterizedTest")) {
				line = convertParameterizedTestAnnotationToTestAnnotation(line);
				inParameterizedTestMethod = true;
			}
			else if (line.contains("@EnumSource")) {				
				paramEnumType = extractEnumParameter(line);
			}
			else if (inParameterizedTestMethod) {
				if (isMethodDeclaration(line)) {
					line = extractParameters(line);
				}
				// Converts parameters to local variables
				else if (testMethodParams != null && !line.contains("@")) {
					line = putParametersAsLocalVariables(line);

					inParameterizedTestMethod = false;
				}
			}
			
			return line;
		}


		private String extractParameters(String line) {
			// Extracts parameters
			String params = DataUtil.extractContentBetweenParenthesis(line);
			
			if (!params.isBlank()) {
				testMethodParams = params.split(",");
				line = line.replace(params, "");
			}
			return line;
		}


		private String putParametersAsLocalVariables(String line) {
			String localVars = convertParametersToLocalVariables();
			
			// Puts converted parameters on the source file line
			if (line.contains("{")) {
				int index = line.indexOf("{");
				
				line = line.substring(0, index+1) + localVars + line.substring(index+1);
			}
			else {
				line = localVars + line;
			}
			return line;
		}


		private boolean isMethodDeclaration(String line) {
			final String regexMethodDeclaration = 
					"^.*[\\s\\t]+((?!new)[A-z0-9\\_\\<\\>\\,\\[\\]\\.\\$])+"
					+ "[\\s\\t]+([A-z0-9\\_\\$]+)[\\s\\t]*\\(.*\\).*$";
			
			return line.matches(regexMethodDeclaration);
		}


		private String convertParameterizedTestAnnotationToTestAnnotation(String line) {
			return line.replace("@ParameterizedTest", "@org.junit.Test");
		}


		private String extractEnumParameter(String line) {
			String parameter = "";
			Matcher m = Pattern.compile("(\\ |\\t)*value(\\ |\\t)*=").matcher(line);
			
			// EnumSource with value attribute 
			if (m.find()) {
				parameter = line.split(m.group())[1].split("\\.")[0].trim();
			}
			// EnumSource without value attribute but with class specified
			else if (line.contains("(")) {
				parameter = DataUtil.extractContentBetweenParenthesis(line).split("\\.")[0].trim();
			}
			
			return parameter;
		}
		
		private String convertParametersToLocalVariables() {
			StringBuilder localVars = new StringBuilder();

			for (int i=0; i<testMethodParams.length; i++) {
				testMethodParams[i] = testMethodParams[i].trim();
				
				localVars.append(testMethodParams[i]);
				localVars.append("=");
				localVars.append(getArgument(i));
				localVars.append(";");
			}
			
			return localVars.toString();
		}

		@SuppressWarnings("rawtypes")
		private String getArgument(int i) {
			if (testMethodArgs[i] == null)
				return "null";
			
			String argument;
			
			// Checks if parameterized test contains 'EnumSource' annotation
			if (paramEnumType != null) {
				// Checks if parameterized test contains 'EnumType' annotation without arguments
				if (paramEnumType.isEmpty())
					testMethodArgs[i] = testMethodParams[i] + "." + ((Enum)testMethodArgs[i]).name();
				else
					testMethodArgs[i] = paramEnumType + "." + ((Enum)testMethodArgs[i]).name();
			}
			
			if (testMethodParams[i].contains("String "))
				argument = "\"" + ((String)testMethodArgs[i])
						.replace("\n", "\\n")
						.replace("\t", "\\t")
						.replace("\r", "\\r") + "\"";
			else
				argument = ((String)testMethodArgs[i]);
			
			return argument;
		}
	}
}
