package wniemiec.executionflow.io.preprocessor.testmethod;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import wniemiec.executionflow.io.SourceCodeProcessor;
import wniemiec.util.io.parser.balance.CurlyBracketBalance;

/**
 * Responsible for disabling collectors in a test method and converting
 * JUnit 5 test annotation to JUnit 4 test annotation.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		6.0.5
 * @since 		6.0.0
 */
public class JUnit5ToJUnit4Processor extends SourceCodeProcessor {
	
	//---------------------------------------------------------------------
	//		Attributes
	//---------------------------------------------------------------------
	private TestProcessor testProcessor;
	private RepeatedTestProcessor repeatedTestProcessor;
	private ParameterizedTestProcessor parameterizedTestProcessor;
	private Object[] testMethodArgs;
	private int totalTests;
	private String processedLine;
	
	
	//---------------------------------------------------------------------
	//		Constructor
	//---------------------------------------------------------------------
	public JUnit5ToJUnit4Processor(List<String> sourceCode, Object[] testMethodArgs) {
		super(sourceCode, true);
		
		this.testMethodArgs = testMethodArgs;
		testProcessor = new TestProcessor();
		repeatedTestProcessor = new RepeatedTestProcessor();
		parameterizedTestProcessor = new ParameterizedTestProcessor(testMethodArgs);
	}
	
	
	//---------------------------------------------------------------------
	//		Methods
	//---------------------------------------------------------------------	
	@Override
	protected String processLine(String line) {
		processedLine = line;
		
		countTest(line);
		
		repeatedTestProcessor.parseInsideRepeatedTest();
		
		if (isJUnit4Annotation(processedLine))
			testProcessor.parseJUnit4TestAnnotation();
		else if (isJUnit5Annotation(processedLine))
			testProcessor.parseJUnit5TestAnnotation();
		else if (isJUnit5RepeatedTestAnnotation(processedLine))
			repeatedTestProcessor.replaceRepeatedTestAnnotation();
		else if (isJUnit5ParameterizedTest(processedLine) || testMethodArgs != null)
			parameterizedTestProcessor.parseParameterizedTest();
		
		return processedLine;
	}
	
	private void countTest(String line) {		
		if (isTestAnnotation(line))
			totalTests++;
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
				+ "(org\\.junit\\.)?Test(\\ |\\t)*(\\ |\\t)*(\\(.*\\))?";
		
		return line.matches(regexJUnit4Test);
	}

	private boolean isJUnit5RepeatedTestAnnotation(String line) {
		final String regexRepeatedTest = ".*@(.*\\.)?"
				+ "RepeatedTest(\\ |\\t)*\\(.+\\)(\\ |\\t)*";
		
		return line.matches(regexRepeatedTest);
	}
	
	private boolean isJUnit5ParameterizedTest(String line) {
		final String regexParameterizedTest = ".*@(.*\\.)?"
				+ "(org\\.junit\\.jupiter\\.params\\.)?"
				+ "ParameterizedTest(\\ |\\t)*(\\ |\\t)*";
		
		return line.matches(regexParameterizedTest);
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
	/**
	 * Converts {@link org.junit.jupiter.api.Test} to {@link org.junit.Test}. 
	 * Also, converts '@Test' to '@org.junit.Test'.
	 */
	private class TestProcessor {
				
		//---------------------------------------------------------------------
		//		Methods
		//---------------------------------------------------------------------
		public void parseJUnit4TestAnnotation() {
			processedLine = processedLine.replace("@Test", "@org.junit.Test");
		}	
		
		public void parseJUnit5TestAnnotation() {
			processedLine = processedLine.replace(
					"@org.junit.jupiter.api.Test", 
					"@org.junit.Test"
			);
		}
	}
	
	/**
	 * Converts {@link org.junit.jupiter.api.RepeatedTest} to 
	 * {@link org.junit.Test} using while control flow instead.
	 */
	private class RepeatedTestProcessor {

		//---------------------------------------------------------------------
		//		Attributes
		//---------------------------------------------------------------------
		private static final String REGEX_REPEATED_TEST = 
				".*@(.*\\.)?RepeatedTest(\\ |\\t)*\\(.+\\)(\\ |\\t)*";
		private String totalRepetitions;
		private CurlyBracketBalance curlyBracketBalance;
		private boolean replacedRepeatedTestAnnotation;
		private boolean insideRepeatedTest;
			
		
		//---------------------------------------------------------------------
		//		Methods
		//---------------------------------------------------------------------
		public void parseInsideRepeatedTest()	{
			if (replacedRepeatedTestAnnotation && processedLine.contains("{")) {
				processedLine = openWhileStatement(processedLine);
				
				replacedRepeatedTestAnnotation = false;
				
				createCurlyBracketBalance(processedLine);
			}
			else if (insideRepeatedTest) {
				updateCurlyBracketBalance(processedLine);
				
				if (isLastLineOfRepeatedMethod(processedLine))
					processedLine = closeWhileStatement(processedLine);
			}
		}
		
		private String openWhileStatement(String line) {
			StringBuilder statement = new StringBuilder();
			String stepVariable = generateVarName();
			int idxLastOpenCurlyBracket = line.lastIndexOf("{");
			
			statement.append(line.substring(0, idxLastOpenCurlyBracket + 1));
			statement.append("int ");
			statement.append(stepVariable);
			statement.append("=0;");
			statement.append("while(");
			statement.append(stepVariable);
			statement.append("++ < ");
			statement.append(totalRepetitions);
			statement.append("){");
			statement.append(line.substring(idxLastOpenCurlyBracket + 1));
			
			return statement.toString();
		}

		private void createCurlyBracketBalance(String line) {
			curlyBracketBalance = new CurlyBracketBalance();
			curlyBracketBalance.parse(line);
			curlyBracketBalance.decreaseBalance();
		}
		
		private void updateCurlyBracketBalance(String line) {
			if (curlyBracketBalance == null)
				return;
			
			if (line.contains("}")) {
				curlyBracketBalance.parse(line);
			}
		}
		
		private boolean isLastLineOfRepeatedMethod(String line) {
			if (curlyBracketBalance == null)
				return false;
			
			return	curlyBracketBalance.isBalanceEmpty()
					&& line.contains("}");
		}

		private String closeWhileStatement(String line) {
			int idxLastClosedCurlyBracket = line.lastIndexOf("}");
			
			insideRepeatedTest = false;
			curlyBracketBalance = null;
			
			return	line.substring(0, idxLastClosedCurlyBracket+1) 
					+ "}" 
					+ line.substring(idxLastClosedCurlyBracket+1);
		}
		
		/**
		 * Converts {@link org.junit.jupiter.api.RepeatedTest} to 
		 * {@link org.junit.Test}.
		 * 
		 * @param		processedLine Current source file line
		 * 
		 * @return		Line with {@link org.junit.Test}
		 */
		public void replaceRepeatedTestAnnotation() {			
			replacedRepeatedTestAnnotation = true;
			insideRepeatedTest = true;
			totalRepetitions = extractNumberOfRepetitions(processedLine);
			
			processedLine = processedLine.replace(
					extractRepeatedTestAnnotation(processedLine), 
					"@org.junit.Test"
			);
		}
		
		private String extractRepeatedTestAnnotation(String line) {
			Pattern patternRepeatedTest = Pattern.compile(REGEX_REPEATED_TEST);
			Matcher matcherRepeatedTest = patternRepeatedTest.matcher(line);
			
			if (!matcherRepeatedTest.find())
				return "";
			
			return matcherRepeatedTest.group();
		}
		
		private String extractNumberOfRepetitions(String line) {
			String numRepetitions = "0";
			
			Pattern patternRepeatedTest = Pattern.compile(REGEX_REPEATED_TEST);
			Matcher matcherRepeatedTest = patternRepeatedTest.matcher(line);
			
			if (matcherRepeatedTest.find()) {
				String annotationParams = extractContentBetweenParenthesis(
						matcherRepeatedTest.group()
				);
				
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
	}
	
	/**
	 * Converts {@link org.junit.jupiter.params.ParameterizedTest} to 
	 * {@link org.junit.Test}.
	 */
	private class ParameterizedTestProcessor {
		
		//---------------------------------------------------------------------
		//		Attributes
		//---------------------------------------------------------------------
		private Object[] testMethodArgs;
		private String[] testMethodParams;
		private boolean inParameterizedTestMethod;
		private String paramEnumType;
		
		
		//---------------------------------------------------------------------
		//		Constructor
		//---------------------------------------------------------------------
		public ParameterizedTestProcessor(Object[] testMethodArgs) {
			this.testMethodArgs = testMethodArgs;
		}
		
		
		//---------------------------------------------------------------------
		//		Methods
		//---------------------------------------------------------------------
		/**
		 * Converts {@link org.junit.jupiter.params.ParameterizedTest} to 
		 * {@link org.junit.Test}.
		 * 
		 * @param		processedLine Current source file line
		 * 
		 * @return		Line with {@link org.junit.Test}
		 */
		public void parseParameterizedTest() {
			if (processedLine.contains("@ParameterizedTest")) {
				processedLine = convertParameterizedTestAnnotationToTestAnnotation(processedLine);
				inParameterizedTestMethod = true;
			}
			else if (processedLine.contains("@EnumSource")) {				
				paramEnumType = extractEnumParameter(processedLine);
			}
			else if (inParameterizedTestMethod) {
				if (isMethodDeclaration(processedLine)) {
					processedLine = extractParameters(processedLine);
				}
				else if ((testMethodParams != null) && !isAnnotation(processedLine)) {
					processedLine = putParametersAsLocalVariables(processedLine);

					inParameterizedTestMethod = false;
				}
			}
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
				parameter = extractContentBetweenParenthesis(line)
						.split("\\.")[0]
						.trim();
			}
			
			return parameter;
		}
		
		private boolean isMethodDeclaration(String line) {
			final String regexMethodDeclaration = 
					"^.*[\\s\\t]+((?!new)[A-z0-9\\_\\<\\>\\,\\[\\]\\.\\$])+"
					+ "[\\s\\t]+([A-z0-9\\_\\$]+)[\\s\\t]*\\(.*\\).*$";
			
			return line.matches(regexMethodDeclaration);
		}
		
		private String extractParameters(String line) {
			String params = extractContentBetweenParenthesis(line);

			if (!params.isBlank()) {
				testMethodParams = params.split(",");
				line = line.replace(params, "");
			}
			return line;
		}
		
		private boolean isAnnotation(String line) {
			return line.contains("@");
		}

		private String putParametersAsLocalVariables(String line) {
			String processedLine = line;
			String localVars = convertParametersToLocalVariables();

			if (line.contains("{")) {
				int idxOpenCurlyBracket = line.indexOf("{");
				
				processedLine = line.substring(0, idxOpenCurlyBracket+1) 
						+ localVars 
						+ line.substring(idxOpenCurlyBracket+1);
			}
			else {
				processedLine = localVars + line;
			}
			
			return processedLine;
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
		
		private String getArgument(int i) {
			if (testMethodArgs[i] == null)
				return "null";
			
			String argument;

			// Checks if parameterized test contains 'EnumSource' annotation
			if (paramEnumType != null)
				testMethodArgs[i] = extractEnumTypeArgs(i);
			
			if (testMethodParams[i].contains("String "))
				argument = "\"" + ((String)testMethodArgs[i])
						.replace("\n", "\\n")
						.replace("\t", "\\t")
						.replace("\r", "\\r") + "\"";
			else
				argument = "" + testMethodArgs[i];
			
			return argument;
		}
		
		@SuppressWarnings("rawtypes")
		private String extractEnumTypeArgs(int i) {
			StringBuilder args = new StringBuilder();
			
			if (paramEnumType.isEmpty())
				args.append(testMethodParams[i]);
			else
				args.append(paramEnumType);
			
			args.append(".");
			args.append(((Enum)testMethodArgs[i]).name());
			
			return args.toString();
		}
	}
}
