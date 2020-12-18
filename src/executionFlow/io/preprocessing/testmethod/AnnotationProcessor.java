package executionFlow.io.preprocessing.testmethod;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import executionFlow.runtime.collector.CollectorExecutionFlow;
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
	private static final String REGEX_METHOD_DECLARATION = 
			"^.*[\\s\\t]+((?!new)[A-z0-9\\_\\<\\>\\,\\[\\]\\.\\$])+[\\s\\t]+([A-z0-9\\_\\$]+)[\\s\\t]*\\(.*\\).*$";
	private static final String REGEX_REPEATED_TEST = 
			".*@(.*\\.)?RepeatedTest(\\ |\\t)*\\(.+\\)(\\ |\\t)*";
	private static final String REGEX_JUNIT4_TEST = 
			".*@(.*\\.)?(org\\.junit\\.)?Test(\\ |\\t)*(\\ |\\t)*(\\(.*\\))?";
	private static final String REGEX_PARAMETERIZED_TEST = 
			".*@(.*\\.)?(org\\.junit\\.jupiter\\.params\\.)?ParameterizedTest(\\ |\\t)*(\\ |\\t)*";
	private String numRepetitions;
	private String testMethodSignature;
	private String[] testMethodParams;
	private Object[] testMethodArgs;
	private CurlyBracketBalance curlyBracketBalance_currentTestMethod;
	private CurlyBracketBalance curlyBracketBalance_ignore;
	private CurlyBracketBalance curlyBracketBalance_repeatedTest;
	private CurlyBracketBalance curlyBracketBalance_parameterizedTest;
	private boolean inTestMethodSignature;
	private boolean repeatedTest_putLoop;
	private boolean insideRepeatedTest;
	private boolean ignoreMethod;
	private boolean inTestAnnotationScope;
	private boolean isSelectedTestMethod;

	/**
	 * Stores test method declaration lines that are not part of the test 
	 * method provided in the constructor.
	 */
	private List<Integer> ignoredMethods = new ArrayList<>();
	
	
	//---------------------------------------------------------------------
	//		Constructor
	//---------------------------------------------------------------------
	/**
	 * Responsible for disabling collectors in a test method and converting
	 * JUnit 5 test annotation to JUnit 4 test annotation. 
	 * 
	 * @param		testMethodSignature Test method signature
	 * @param		testMethodArgs Test method arguments (for parameterized
	 * tests)
	 */
	public AnnotationParser(String testMethodSignature, Object[] testMethodArgs)
	{
		this(testMethodSignature);
		this.testMethodArgs = testMethodArgs;
	}
	
	/**
	 * Responsible for disabling collectors in a test method and converting
	 * JUnit 5 test annotation to JUnit 4 test annotation. Use this
	 * constructor if test method is not a parameterized test.
	 * 
	 * @param		testMethodSignature Test method signature
	 */
	public AnnotationParser(String testMethodSignature)
	{ 
		this.testMethodSignature = CollectorExecutionFlow.extractMethodName(testMethodSignature) + 
				testMethodSignature.substring(testMethodSignature.indexOf("(")).replace(" ", "");
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
	public String processLines(List<String> lines)
	{
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
			else if (line.matches(REGEX_METHOD_DECLARATION) ) {
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
		
		line = parseInsideRepeatedTest(line);
		
		// Converts test annotation from JUnit 5 to JUnit 4
		line = parseAnnotations(line);
		
		return line;
	}
	
	/**
	 * Parses test annotations. It will convert JUnit 5 annotations to 
	 * JUnit 4 test annotation ({@link org.junit.Test}).
	 * 
	 * @param		line Current source file line
	 * 
	 * @return		Processed line
	 */
	private String parseAnnotations(String line)
	{
		return	line.matches(REGEX_JUNIT4_TEST) 			 ?	parseTestAnnotation(line, false) :
				line.contains("@org.junit.jupiter.api.Test") ?	parseTestAnnotation(line, true) :
				line.matches(REGEX_REPEATED_TEST) 			 ?	parseRepeatedTest(line) :
				line.matches(REGEX_PARAMETERIZED_TEST) || 
							 testMethodArgs != null			 ?	parseParameterizedTest(line) : 
				line;
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
		else if (line.matches(REGEX_METHOD_DECLARATION) && !line.contains("private ") && 
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
	
	/**
	 * Gets method signature from a source file line;
	 * 
	 * @param		line Source file line
	 * 
	 * @return		Method signature
	 */
	private String extractMethodSignatureFromLine(String line)
	{
		StringBuilder methodParams = new StringBuilder();
		String methodName = line.substring(
				line.substring(0, line.indexOf("(")).lastIndexOf(" "), 
				line.lastIndexOf("(")
		);
		String methodParamsAndArgs = line.substring(line.indexOf("(")+1, line.lastIndexOf(")"));
		String response;
		
		
		if (methodParamsAndArgs.isBlank()) {
			response = methodName + "()";
		}
		else {
			for (String param : methodParamsAndArgs.split(",")) {
				methodParams.append(param.trim().split(" ")[0]);
				methodParams.append(",");
			}
			
			if (methodParams.length() > 1) {
				methodParams.deleteCharAt(methodParams.length()-1);
			}
			
			response = methodName + "(" + methodParams.toString() + ")";
		}
		
		return response;
	}
	
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
		if (repeatedTest_putLoop) {
			if (line.contains("{")) {
				repeatedTest_putLoop = false;
				line = repeatedTest_putWhileLoop(line);
				
				curlyBracketBalance_repeatedTest = new CurlyBracketBalance();
				curlyBracketBalance_repeatedTest.parse(line);
				curlyBracketBalance_repeatedTest.decreaseBalance();
			}
		}
		// Checks if it is the end of repeated test
		else if (insideRepeatedTest && curlyBracketBalance_repeatedTest != null && line.contains("}")) {
			curlyBracketBalance_repeatedTest.parse(line);
			
			if (curlyBracketBalance_repeatedTest.isBalanceEmpty()) {
				int idx = line.lastIndexOf("}");
				
				
				insideRepeatedTest = false;
				curlyBracketBalance_repeatedTest = null;
				
				line = line.substring(0, idx+1) + "}" + line.substring(idx+1);
			}
		}
		
		return line;
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
	private String parseTestAnnotation(String line, boolean isJunit5)
	{
		return	isJunit5 ? 
					line.replace("@org.junit.jupiter.api.Test", "@org.junit.Test") : 
					line.replace("@Test", "@org.junit.Test");
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
		Pattern p = Pattern.compile(REGEX_REPEATED_TEST);
		Matcher m = p.matcher(line);
		
		
		numRepetitions = "0";
		insideRepeatedTest = true;
		
		if (m.find()) {
			String repeatedTestAnnotation = m.group();
			
			
			line = line.replace(repeatedTestAnnotation, "@org.junit.Test");

			// Gets number of repetitions
			m = Pattern.compile("\\(.*\\)").matcher(repeatedTestAnnotation);
			
			if (m.find()) {
				String annotationParams = m.group().replace("(", "").replace(")", "");
				
				
				if (annotationParams.contains("value")) {
					m = Pattern.compile("[0-9]+").matcher(annotationParams);
					
					if (m.find())
						numRepetitions = m.group();
				}
				else {
					numRepetitions = annotationParams;						
				}
			}
		}
		
		repeatedTest_putLoop = true;
		
		return line;
	}
	
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
		if (curlyBracketBalance_parameterizedTest == null)
			curlyBracketBalance_parameterizedTest = new CurlyBracketBalance();
		
		// Converts JUnit 5 parameterized test to JUnit 4 test
		if (line.contains("@ParameterizedTest")) {
			line = line.replace("@ParameterizedTest", "@org.junit.Test");
			inTestMethodSignature = true;

		}
		// Gets enum type
		else if (line.contains("@EnumSource")) {
			Matcher m = Pattern.compile("(\\ |\\t)*value(\\ |\\t)*=").matcher(line);
			
			
			// EnumSource with value attribute 
			if (m.find()) {
				paramEnumType = line.split(m.group())[1].split("\\.")[0].trim();
			}
			// EnumSource without value attribute but with class specified
			else if (line.contains("(")) {
				paramEnumType = line.substring(line.indexOf("(")+1, line.indexOf(")")).split("\\.")[0].trim();
			}
			// Empty EnumSource 
			else {
				paramEnumType = "";
			}
		}
		// Checks if it is within parameterized test
		else if (inTestMethodSignature) {
			// Converts test method parameters to local variables 
			if (line.matches(REGEX_METHOD_DECLARATION)) {
				if (!extractMethodSignatureFromLine(line).replace(" ", "").equals(testMethodSignature)) {
					inTestMethodSignature = false;
					curlyBracketBalance_parameterizedTest.parse(line);
					ignoreMethod = true;
				}
				else {
					// Extracts parameters
					Matcher m = Pattern.compile("\\(.*\\)").matcher(line);
					String params;
					
					
					if (m.find()) {
						params = m.group().replace("(", "").replace(")", "");
						testMethodParams = params.split(","); // Removes parentheses
						line = line.replace(params, ""); // Deletes params from method
					}
				}
			}
			// Converts parameters to local variables
			else if (testMethodParams != null && !line.contains("@")) {
				StringBuilder localVars = new StringBuilder();
				
				
				// Converts each parameter with its value into a local variable
				for (int i=0; i<testMethodParams.length; i++) {
					testMethodParams[i] = testMethodParams[i].trim();
					
					// Checks if parameterized test contains 'EnumSource' annotation
					if (paramEnumType != null) {
						
						// Checks if parameterized test contains 'EnumType' annotation without arguments
						if (paramEnumType.isEmpty())
							testMethodArgs[i] = testMethodParams[i] + "." + ((Enum)testMethodArgs[i]).name();
						else
							testMethodArgs[i] = paramEnumType + "." + ((Enum)testMethodArgs[i]).name();
					}
					
					localVars.append(testMethodParams[i] + "=");
					
					if (testMethodArgs[i] == null)
						localVars.append("null");
					else if (testMethodParams[i].contains("String "))
						localVars.append("\"" 
								+ ((String)testMethodArgs[i])
								.replace("\n", "\\n")
								.replace("\t", "\\t")
								.replace("\r", "\\r") + "\""
						);
					else
						localVars.append(testMethodArgs[i]);
					
					localVars.append(";");
				}
				
				// Puts converted parameters on the source file line
				if (line.contains("{")) {
					int index = line.indexOf("{");
					
					
					line = line.substring(0, index+1) + localVars + line.substring(index+1);
				}
				else {
					line = localVars + line;
				}

				inTestMethodSignature = false;
			}
		}
		
		return line;
	}
				
	/**
	 * Converts repeated test to a while loop.
	 * 
	 * @param		line Line to be placed the while clause
	 * 
	 * @return		Line with while clause along with the function arguments
	 */
	private String repeatedTest_putWhileLoop(String line)
	{
		int idx = line.lastIndexOf("{");
		String varname = DataUtil.generateVarName();
		
		
		return line.substring(0, idx + 1) + "int "+ varname + "=0;while(" +
			varname + "++ < " + numRepetitions + "){" + line.substring(idx + 1);
	}
	
	
	//---------------------------------------------------------------------
	//		Getters
	//---------------------------------------------------------------------
	/**
	 * Gets test method declaration lines that are not part of the test 
	 * method provided in the constructor.
	 * 
	 * @return		Test method declaration lines
	 */
	public List<Integer> getIgnoredMethods()
	{
		return ignoredMethods;
	}
}
