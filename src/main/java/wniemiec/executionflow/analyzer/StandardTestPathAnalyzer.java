package wniemiec.executionflow.analyzer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import wniemiec.executionflow.info.InvokedInfo;
import wniemiec.util.io.parser.balance.RoundBracketBalance;
import wniemiec.util.logger.Logger;

/**
 * Standard strategy for computing test path of a method and records the 
 * methods called by it.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		6.0.5
 * @since		6.0.0
 */
public class StandardTestPathAnalyzer extends Analyzer {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final String REGEX_DOLLAR_SIGN_PLUS_NUMBERS = "^.+\\$[0-9]+.*$";
	private List<Integer> testPath;
	private boolean newIteration;
	private boolean finishedTestedInvoked;
	private boolean isInternalCommand;
	private boolean inMethod;
	private boolean insideConstructor;
	private boolean insideOverloadCall;
	private boolean lastTpAddedWasReturn;
	private boolean anonymousConstructor;
	private int invokedDeclarationLine;
	private int lineOverloadedCall;
	private int lastLineAdded = -1;
	private int currentLine;
	private String lastSrcLine = "";
	private String line;
	private String srcLine;
	private RoundBracketBalance rbb;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	public StandardTestPathAnalyzer(InvokedInfo invokedInfo, InvokedInfo testMethodInfo) 
			throws IOException {
		super(invokedInfo, testMethodInfo);
		
		anonymousConstructor = checkAnonymousConstructor(invokedInfo.getClassSignature());
		testPath = new ArrayList<>();
		testPaths = new ArrayList<>();
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	private boolean checkAnonymousConstructor(String classSignature) {
		if (!classSignature.contains("$"))
			return false;
		
		return invoked.getInvokedSignature().matches(REGEX_DOLLAR_SIGN_PLUS_NUMBERS);
	}
	
	protected void run() throws IOException {
		try {
			runJDB();
		}
		catch (IllegalStateException e) {
			// Debugger was closed before execution ended
		}
	}
	
	private void runJDB() throws IOException {
		boolean wasNewIteration = false;
		
		while (!stopJDB && !timeout) {
			while (!wasNewIteration && !timeout && !stopJDB && !parseOutput())
				;
			
			wasNewIteration = false;
			
			if (stopJDB) {
				if (!testPath.isEmpty()) {
					fixAnonymousConstructorTestPaths();
					testPaths.add(testPath);
				} 
			}
			else if (finishedTestedInvoked && !isInternalCommand) {
				testPaths.add(testPath);
				
				getsReadyForTheNextTest();
				sendJDB("cont");
			} 
			else if (newIteration) {
				wasNewIteration = true;
				
				sendJDB("step into");
				readAllOutput();
				skipInternalCalls();
			} 
			else if (!stopJDB) {
				sendJDB("next");
			}
		}
	}
	
	private void fixAnonymousConstructorTestPaths() {
		if (!anonymousConstructor || testPath.size() <= 1)
			return;
		
		testPath.remove(testPath.size() - 1);
		testPath.remove(0);
	}
	
	private void getsReadyForTheNextTest() {
		testPath = new ArrayList<>();
		lastLineAdded = -1;
		finishedTestedInvoked = false;
		inMethod = false;
		lastTpAddedWasReturn = false;
	}
	
	private void sendJDB(String command) {
		jdb.send(command);
		
		Logger.debug(this.getClass(), "COMMAND: " + command);
	}
	
	private void readAllOutput() throws IOException	{
		while (!parseOutput() && !timeout && !stopJDB)
			;
	}
	
	private void skipInternalCalls() throws IOException {
		while (isInternalCommand && !timeout && !stopJDB) {					
			sendJDB("next");
			readAllOutput();
		}
	}
	
	private boolean parseOutput() throws IOException {
		if (!isJDBReady())
			return false;

		initializeLine();
    	
    	if (isEmptyLine())
    		return false;
    	
    	if (appExited(line) || isAssertionError(line)) {
    		stopJDB = true;
    		return true;
    	}
    	
    	checkIncorrectInvocationLine();
    	checkInternalError();
    	
    	isInternalCommand = isInternalMethod();
    	
		if (!stopJDB && initializationFinished())
			parseSrcLine();
		
		return stopJDB || finishedTestedInvoked || initializationFinished();
	}
	
	private boolean isJDBReady() {
		if (jdb.isReady())
			return true;
		
		try {
			Thread.sleep(200);
		} 
		catch (InterruptedException e) {
		}
		
		return false;
	}

	private void initializeLine() {
		line = jdb.read();
    	
    	Logger.debug(this.getClass(), "LINE: " + line);
	}

	private boolean isEmptyLine() {
		final String regexEmptyOutput = "^(>(\\ |\\t)*)*main\\[[0-9]\\](\\ |\\t|>)*$";
		
		return 	line.matches(regexEmptyOutput)
				|| line.equals("\n") 
				|| line.equals("") 
				|| line.equals(" ")
				|| line.equals("> ")
				|| line.equals(">")
				|| line.equals(".");
	}
	
	private boolean appExited(String line) {
		return line.contains("The application exited"); 
	}
	
	private boolean isAssertionError(String line) {
		return line.contains("java.lang.AssertionError: Expected exception:");
	}
	
	private void checkIncorrectInvocationLine() {
		if (!line.contains("Unable to set deferred breakpoint "))
			return;
		
		try {
			//jdb.quit();
			jdb.forceQuit();
		} 
		catch (IOException e) {
		}
		
		throw new IllegalStateException("Incorrect invocation line {invocationLine: " 
				+ invoked.getInvocationLine() + ", test method signature: "
				+ testMethod.getSignatureWithoutParameters() + ")" + ", invokedSignature: " 
				+ invoked.getInvokedSignature() + "}"
		);
	}
	
	private void checkInternalError() throws IOException {
		if (isErrorMessage(line)) 
			throw new IOException("Error while running JDB");}

	private boolean isErrorMessage(String line) {
		return	line.contains("[INFO]")
				|| line.contains("Exception occurred") 
				|| line.contains("Input stream closed.")
				|| line.contains("FAILURES!!!") 
				|| line.contains("Caused by: ")
				|| isStackTrace(line);
	}
	
	private boolean isStackTrace(String line) {
		return line.matches("[\\s\\t]*at[\\s\\t]+[^\\/]+\\/[^\\(]+\\([^\\)]+\\).*");
	}


	private boolean isInternalMethod() {
		return 	!hasLineInvokedName()
				&& !inConstructor()
				&& !line.contains(testMethod.getClassSignature());
	}
	
	private boolean hasLineInvokedName() {
		return	line.contains(invoked.getInvokedName() + ".") 
				|| line.contains(invoked.getInvokedName() + "(");
	}
	
	private boolean inConstructor() {
		return line.contains(".<init>");
	}
	
	private boolean initializationFinished() {
		return	(line.contains("Breakpoint hit") || line.contains("Step completed"))
				&& line.contains("thread=");
	}
	
	private void parseSrcLine() {
		initializeSrcLine();
		
		newIteration = isNewIteration();
		insideConstructor = inConstructor() && !returnedFromTestedInvoked();
		currentLine = getLineNumber();
		isInternalCommand = isNativeCall();
		inMethod = !isInternalCommand && isInsideMethod();		
		
		checkBalanceOfParentheses();
		initializeInvokedDeclarationLineNumber();

		if (!isInternalCommand && !returnedFromTestedInvoked() && !shouldIgnore()) {
			if (inMethod || insideConstructor) {
				analyzeLinesExecutedByInvoked();
				storeAnalyzedInvokedSignature();
			}
			else if (willEnterInMethod(line)) {
				inMethod = true;
			}
		}
		
		checkOverloadedConstructor();
		resetBalanceOfParenthesisIfEmpty();
		
		if (srcLine != null && !srcLine.isEmpty()) {
			checkOverloadedCall();

			stopJDB = checkFailure() || checkRepetition();
			
			if (!insideOverloadCall) {
				lastSrcLine = srcLine;
				
				if (finishedTestedInvoked) {
					inMethod = false;
					lastTpAddedWasReturn = false;
					invokedDeclarationLine = 0;
				}
			}

			finishedTestedInvoked = hasFinishedTestedInvoked();
		}
	}
	
	private void initializeSrcLine() {
		srcLine = jdb.read();
		
		Logger.debug(this.getClass(), "SRC: " + srcLine);
	}

	private boolean isNewIteration() {
		return (
			!inMethod 
			&& !finishedTestedInvoked 
			&& (line.contains("Breakpoint hit")	|| isTestedInvokedCall()));
	}
	
	private boolean isTestedInvokedCall() {
		if (line == null)
			return false;
		
		return	(getLine(line) == invoked.getInvocationLine())
				&& line.contains(testMethod.getClassSignature());
	}
	
	private int getLine(String line) {
		if (line == null || line.isEmpty() || !line.contains(","))
			return -1;
		
		String lineNumber = line.split(",")[2].split("=")[1].split(" ")[0].trim();
		lineNumber = lineNumber.replaceAll("\\.", "");
		
		return Integer.valueOf(lineNumber);
	}
	
	private boolean returnedFromTestedInvoked() {
		if (!line.contains(testMethod.getSignatureWithoutParameters()))
			return false;
		
		return	(line.contains(testMethod.getSignatureWithoutParameters()) && !testPath.isEmpty()) 
				|| wasInsideMethodWithEmptyBody();
	}

	private boolean wasInsideMethodWithEmptyBody() {
		return 	inMethod 
				&& insideConstructor 
				&& !insideOverloadCall 
				&& (isMethodWithEmptyBody() || line.contains(testMethod.getSignatureWithoutParameters()));
	}
	
	private int getLineNumber() {
		if ((line == null) && (srcLine == null))
			return -1;
		
		if ((srcLine == null) || srcLine.isEmpty())
			return getSrcLine(srcLine);

		return getLine(line);
	}
	
	private int getSrcLine(String srcLine) {
		if (srcLine == null || srcLine.isEmpty())
			return -1;
		
		return	Integer.valueOf(extractSrcLineNumber(srcLine));
	}

	private String extractSrcLineNumber(String srcLine) {
		return srcLine.replace(".", "")
				.substring(0, srcLine.indexOf(" ")).trim();
	}
	
	private boolean isNativeCall() {
		return	line.contains("line=1 ") 
				|| line.contains("jdk.") 
				|| line.contains("aspectj.")
				|| line.contains("executionflow.runtime")
				|| srcLine.contains("package ") 
				|| isNativeAnonymousClass()
				&& !inConstructor()
				&& !hasLineInvokedName()
				&& !line.contains(testMethod.getSignatureWithoutParameters());
	}
	
	private boolean isNativeAnonymousClass() {
		final String regexNativeAnonymousClass = ".+\\$[0-9]+.+";
		
		return	line.matches(regexNativeAnonymousClass) 
				&& !srcLine.matches(".+\\{(\\ |\\t)*$");
	}
	
	private boolean isInsideMethod() {
		if (finishedTestedInvoked || line.contains(testMethod.getSignatureWithoutParameters()))
			return false;
			
		if (anonymousConstructor)
			return !line.contains("$") && inConstructor();

		return 	line.contains(invoked.getInvokedName() + "(") 
				|| line.contains(invoked.getInvokedName() + ".<init>");

	}
	
	private void checkBalanceOfParentheses() {
		if (srcLine.contains("(") && !srcLine.contains(")") && rbb == null) {
			rbb = new RoundBracketBalance();
		}
		
		if (rbb != null)
			rbb.parse(srcLine);
	}
	
	private void initializeInvokedDeclarationLineNumber() {
		if (!isInvokedDeclarationLine(line, srcLine) && !inMethod)
			return;

		if (!wasInvokedDeclarationLineNumberInitialized() && (currentLine > 1))
			invokedDeclarationLine = currentLine;        		
	}
	
	private boolean isInvokedDeclarationLine(String line, String srcLine) {
		final String regexConstructorWithDollarSignPlusNumbers = "^.+\\$[0-9]+\\.\\<init\\>.*$";
		boolean isInsideAnonymousConstructor = (anonymousConstructor && insideConstructor);
		
		return	srcLine.contains("@wniemiec.executionflow.runtime.CollectCalls")
				&& !line.contains(testMethod.getSignatureWithoutParameters())
				&& (isInsideAnonymousConstructor || hasLineInvokedName())
				&& !line.split(",")[1].matches(regexConstructorWithDollarSignPlusNumbers);
	}
	
	private boolean wasInvokedDeclarationLineNumberInitialized() {
		return invokedDeclarationLine > 0;
	}
	
	private boolean shouldIgnore() {
		if (isInnerClassOrAnonymousClass())
			return true;
		
		if (insideOverloadCall)
			return !isMethodWithEmptyBody();
		
		return	srcLine.contains("@wniemiec.executionflow.runtime.CollectCalls") 
				|| insideOverloadCall 
				|| isInternalCommand
				|| (currentLine > 1 && currentLine < invokedDeclarationLine)
				|| srcLine.contains(" class ")
				|| isMethodDeclaration(srcLine) 
				|| isMultiLineArgs()
				|| isMethodWithEmptyBody();
	}
	
	private boolean isInnerClassOrAnonymousClass() {
		return 	line.contains("$")
				&& hasInvokedNameDollarSign()
				&& !line.contains("line=1 ")
				&& !line.contains("jdk.")
				&& !line.contains("aspectj.")
				&& !line.contains("executionflow.runtime")
				&& !srcLine.contains("package ")
				&& !line.contains(invoked.getInvokedName());
	}
	
	private boolean isMethodDeclaration(String line) {
		final String regexMethodDeclaration = "[\\ \\t0-9]*((public|proteted|private)"
				+ "(\\ |\\t)+)?[A-z]+\\(.*\\)(\\ |\\t)*\\{";
		
		return line.matches(regexMethodDeclaration);
	}

	private boolean isMultiLineArgs() {
		final String regexMultiLineArgs = 
				"^[0-9]*(\\t|\\ )+[A-z0-9$\\-_\\.\\,\\ \\:]+(\\);)?$";
		
		return srcLine.matches(regexMultiLineArgs);
	}
	
	private boolean isMethodWithEmptyBody() {
		final String regexOnlyOpenCurlyBracket = "([0-9]+)(\\ |\\t)+\\{((\\ |\\t)+)?$";
		final String regexOnlyclosedCurlyBracket = "([0-9]+)(\\ |\\t)+\\}((\\ |\\t)+)?$";
		final String regexEmptyMethod = "^([0-9]*)(\\t|\\ )*((([a-z]+\\ ){2,}"
				+ ".+\\(.*\\)(\\ |\\t)*\\{(\\ |\\t)*\\})|(\\{(\\t|\\ )*\\})|(\\}))$";
		
		return	srcLine.matches(regexOnlyclosedCurlyBracket)
				|| srcLine.matches(regexEmptyMethod)
				|| srcLine.matches(regexOnlyOpenCurlyBracket);
	}
	
	private void analyzeLinesExecutedByInvoked() {
		if (finishedTestedInvoked)
			return;
		
		if (line.contains(testMethod.getSignatureWithoutParameters())) {
			finishedTestedInvoked = true;
			inMethod = false;
		} 
		else if (insideTestedInvoked()) {
			testPath.add(currentLine);
			lastLineAdded = currentLine;
			lastTpAddedWasReturn = (srcLine.contains("return ") && !srcLine.contains("if "));
		}
	}
	
	private boolean insideTestedInvoked() {
		return 	!isMethodWithEmptyBody() 
				&& !lastTpAddedWasReturn 
				&& wasInvokedDeclarationLineNumberInitialized()
				&& !insideAnonymousConstructor()
				&& !srcLine.contains("} catch(Throwable _")
				&& areParenthesesUnbalanced()
				&& currentLine != lastLineAdded;
	}

	private boolean insideAnonymousConstructor() {
		return	insideConstructor 
				&& line.matches(REGEX_DOLLAR_SIGN_PLUS_NUMBERS);
	}
	
	private boolean areParenthesesUnbalanced() {
		return	(rbb == null)
				|| !rbb.isBalanceEmpty();
	}

	private void storeAnalyzedInvokedSignature() {
		if (!analyzedInvokedSignature.isBlank() || wasInvokedDeclarationLineNumberInitialized())
			return;
			
		if (anonymousConstructor || hasInvokedNameDollarSign())	
			analyzedInvokedSignature = fixAnonymousSignature();
		else
			analyzedInvokedSignature = invoked.getInvokedSignature();
		
		analyzedInvokedSignature = analyzedInvokedSignature.trim();
	}
	
	private boolean hasInvokedNameDollarSign() {
		return invoked.getInvokedName()
				.substring(1)
				.matches(REGEX_DOLLAR_SIGN_PLUS_NUMBERS);
	}
	
	private String fixAnonymousSignature() {
		String params = extractSignatureParameters();

		return line.split(",")[1].split("\\.\\<init\\>")[0] + "(" + params + ")";
	}

	private String extractSignatureParameters() {
		StringBuilder params = new StringBuilder();
		String[] signatureParams = 
				extractContentBetweenParentheses(invoked.getInvokedSignature()).split(",");
		
		for (int i=1; i<signatureParams.length; i++) {
			params.append(signatureParams[i].trim().replace(",", ""));
			
			if (i+1 != signatureParams.length)
				params.append(", ");
		}
		
		return params.toString();
	}
	
	private String extractContentBetweenParentheses(String str) {
		return str.substring(str.indexOf("(")+1, str.indexOf(")"));
	}
	
	private boolean willEnterInMethod(String line) {
		if (newIteration)
			return newIteration;
		
		return 	!inMethod 
				&& line.contains("Step completed")
				&& line.contains(testMethod.getClassSignature()) 
				&& line.contains("line=" + invoked.getInvocationLine()); 
	}
	
	private void checkOverloadedConstructor() {
		if (insideOverloadCall)
			return;
		
		insideOverloadCall = insideConstructor && srcLine.contains("this(");
		lineOverloadedCall = currentLine;
		
	}
	
	private void resetBalanceOfParenthesisIfEmpty() {
		if (rbb != null && rbb.isBalanceEmpty())
			rbb = null;
	}
	
	private void checkOverloadedCall() {
		if (!insideOverloadCall)
			return;
		
		if ((currentLine == lineOverloadedCall + 1) || isMethodWithEmptyBody())
			insideOverloadCall = false;
	}
	
	private boolean hasFinishedTestedInvoked() {
		return	finishedTestedInvoked 
				|| returnedFromTestedInvoked()
				|| checkFailure() 
				|| (
					!insideOverloadCall
					&& invokedDeclarationLine > 0
					&& !inConstructor()
					&& srcLine.contains("return ") 
					&& !srcLine.contains("if ")
					&& hasInvokedName(line) 
					|| (
							shouldIgnore()
							&& line.contains(testMethod.getSignatureWithoutParameters()) 
							&& getSrcLine(srcLine) > invoked.getInvocationLine()
		));
	}
	
	private boolean checkRepetition() {
		return	!newIteration 
				&& hasOnlyClosedCurlyBracket(srcLine) 
				&& srcLine.equals(lastSrcLine);
	}
	
	private boolean hasOnlyClosedCurlyBracket(String str) {
		return str.matches("[0-9]+(\\ |\\t)*\\}(\\ |\\t)*");
	}
	
	private boolean checkFailure() {
		return srcLine.contains("FAILURES!!!");
	}


	private boolean hasInvokedName(String line) {
		return	line.contains(invoked.getInvokedName()+".") 
				|| line.contains(invoked.getInvokedName()+"(");
	}
}
