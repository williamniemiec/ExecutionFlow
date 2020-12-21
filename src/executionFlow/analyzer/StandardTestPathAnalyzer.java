package executionFlow.analyzer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import executionFlow.info.InvokedInfo;
import executionFlow.util.balance.RoundBracketBalance;
import executionFlow.util.logger.Logger;

/**
 * Standard strategy for computing test path of a method and records the 
 * methods called by it.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.3
 * @since		5.2.3
 */
public class StandardTestPathAnalyzer extends Analyzer {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final String REGEX_MULTILINE_ARGS = 
			"^[0-9]*(\\t|\\ )+[A-z0-9$\\-_\\.\\,\\ \\:]+(\\);)?$";
	private static final String REGEX_DOLLAR_SIGN_PLUS_NUMBERS = "^.+\\$[0-9]+.*$";
	private List<Integer> testPath;
//	private boolean stopJDB;
	private boolean newIteration;
	private boolean returnedFromTestedInvoked;
	private boolean isInternalCommand;
	private boolean inMethod;
	private boolean insideConstructor;
	private boolean insideOverloadCall;
	private boolean lastAddWasReturn;
//	private boolean isMethodMultiLineArgs;
	private boolean anonymousConstructor;
	private boolean invokedNameContainsDollarSign;
	private int invokedDeclarationLine;
	private int lineOverloadedCall;
	private int lastLineAdded = -1;
	private String lastSrcLine = "";
	private RoundBracketBalance rbb;
	private String line;
	private String srcLine;
	private int currentLine;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	public StandardTestPathAnalyzer(InvokedInfo invokedInfo, InvokedInfo testMethodInfo) 
			throws IOException {
		super(invokedInfo, testMethodInfo);
		
		invokedNameContainsDollarSign = invoked.getInvokedName().substring(1).matches(REGEX_DOLLAR_SIGN_PLUS_NUMBERS);
		anonymousConstructor = checkAnonymousConstructor(invokedInfo.getClassSignature());
		testPath = new ArrayList<>();
		testPaths = new ArrayList<>();
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	protected void run() throws IOException {
		boolean wasNewIteration = false;
		
		// Executes while inside the test method
		while (!stopJDB && !timeout) {
			// Checks if output has finished processing
			while (!wasNewIteration && !timeout && !parseOutput())
				;
			
			wasNewIteration = false;
			
			if (stopJDB) {
				if (!testPath.isEmpty()) {
					fixAnonymousConstructorTestPaths();
					testPaths.add(testPath);
				} 
			}
			else if (returnedFromTestedInvoked && !isInternalCommand) {
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
		returnedFromTestedInvoked = false;
		inMethod = false;
		lastAddWasReturn = false;
	}
	
	private void sendJDB(String command) {
		jdb.send(command);
		
		Logger.debug(this.getClass().getName(), "COMMAND: " + command);
	}
	
	private void readAllOutput() throws IOException	{
		while (!parseOutput())
			;
	}
	
	private void skipInternalCalls() throws IOException {
		while (isInternalCommand) {					
			sendJDB("next");
			readAllOutput();
		}
	}
	
	private boolean checkAnonymousConstructor(String classSignature) {
		boolean hasDollarSign = classSignature.contains("$");

		return hasDollarSign ? invoked.getInvokedSignature().matches(REGEX_DOLLAR_SIGN_PLUS_NUMBERS)
				: false;
	}
	
	/**
	 * Reads JDB output and returns true if JDB is ready to receive commands.
	 * 
	 * @return		If JDB is ready to receive commands.
	 * 
	 * @throws		IllegalStateException If invocation line is incorrect
	 * @throws		IOException If an internal error occurs while running JDB
	 * 
	 * @apiNote		If {@link #DEBUG} is activated, it will display JDB 
	 * output on the console
	 */
	
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
	
	private boolean parseOutput() throws IOException {
		if (!isJDBReady())
			return false;
		
		boolean readyToReadInput = false;
//		boolean ignore = false;
    	line = jdb.read();
    	Logger.debug("Analyzer", "LINE: " + line);
    	
    	if (isEmptyLine(line))
    		return false;
    	
    	if (appExited(line)) {
    		stopJDB = true;
    		return true;
    	}
    	
    	checkIncorrectInvocationLine(line);
    	checkInternalError(line);
    	
    	isInternalCommand = isInternalMethod(line);
    	
    	// Checks if JDB has started and is ready to receive debug commands
		if (!stopJDB && hasStarted(line)) {
			readyToReadInput = true;
			srcLine = jdb.read();
			newIteration = isNewIteration(line);
    		insideConstructor = line.contains(".<init>") 
    				&& !returnedFromTestedInvoked();

    		currentLine = getLineNumber();
    		
    		checkBalanceOfParentheses();
    		
    		isInternalCommand = isNativeCall();
    		inMethod = !isInternalCommand && isInsideMethod();
    		
    		initializeInvokedDeclarationLineNumber(currentLine, line, srcLine);
        	storeAnalyzedInvokedSignature(line);
    		
    		if (!isInternalCommand && !returnedFromTestedInvoked() && !shouldIgnore()) {
    			if (inMethod || insideConstructor)
    				analyzeLinesExecutedByInvoked(isEmptyMethod(srcLine), currentLine, line, srcLine);
    			else if (willEnterInMethod(line)) {
    				inMethod = true;
    			}
    		}
    		
    		checkOverloadedConstructor(currentLine, srcLine);
    		
    		if (rbb != null && rbb.isBalanceEmpty())
				rbb = null;
		}
		
		if (srcLine != null && !srcLine.isEmpty()) {
			if (insideOverloadCall && currentLine == lineOverloadedCall + 1) {
				insideOverloadCall = false;
			}
			
			if (!insideOverloadCall) {
    			if (returnedFromTestedInvoked) {
    				inMethod = false;
    				lastAddWasReturn = false;
    				invokedDeclarationLine = 0;
    			}
    			
    			checkRepetition();
    			
    			
    			lastSrcLine = srcLine;
    			
    			if (shouldExit())
    				returnedFromTestedInvoked = true;
			}
			else if (isEmptyMethod(srcLine)) {
				insideOverloadCall = false;
			}
//			
//			if (returnedFromTestedInvoked) {
//				currentLine = -1;
//			}
			
//			if (srcLine.contains("FAILURES!!!")) {
//				stopJDB = true;
//				returnedFromTestedInvoked = true;
//			}
			//

			returnedFromTestedInvoked = 
					returnedFromTestedInvoked 
					|| returnedFromTestedInvoked()
					|| checkRepetition() 
					|| srcLine.contains("FAILURES!!!") 
					|| (!insideOverloadCall && shouldExit());
			stopJDB = srcLine.contains("FAILURES!!!") || checkRepetition();
			
			Logger.debug("Analyzer", "SRC: "+srcLine);
		}
		
		return stopJDB || returnedFromTestedInvoked || readyToReadInput;
	}
	
	
	private boolean shouldIgnore() {
		
		if (isInnerClassOrAnonymousClass())
			return true;
		
		if (insideOverloadCall)
			return !isEmptyMethod(srcLine);
		
		return	srcLine.contains("@executionFlow.runtime.CollectCalls") 
				|| insideOverloadCall 
				|| isInternalCommand
				|| (currentLine > 1 && currentLine < invokedDeclarationLine)
				|| srcLine.contains(" class ")
				|| isMethodDeclaration(srcLine) 
				|| srcLine.matches(REGEX_MULTILINE_ARGS)
				|| isEmptyMethod(srcLine);
	}


	private void checkBalanceOfParentheses() {
		if (srcLine.contains("(") && !srcLine.contains(")") && rbb == null) {
			rbb = new RoundBracketBalance();
		}
		
		if (rbb != null)
			rbb.parse(srcLine);
	}
	
	private int getLineNumber() {
		if ((line == null) && (srcLine == null))
			return -1;
		
		if ((srcLine == null) || srcLine.isEmpty())
			return getSrcLine(srcLine);

		return getLine(line);
	}


	private boolean appExited(String line) {
		return line.contains("The application exited"); 
	}


	private void initializeInvokedDeclarationLineNumber(int currentLine, String line, String srcLine) {
		boolean wasInvokedDeclarationLineNumberInitialized = invokedDeclarationLine > 0;
		if (!wasInvokedDeclarationLineNumberInitialized && currentLine > 1) {
			if (isInvokedDeclarationLine(line, srcLine) || inMethod) {
				invokedDeclarationLine = currentLine;        		
			}
		}
	}

	private void checkOverloadedConstructor(int currentLine, String srcLine) {
		if (!insideOverloadCall) {
			insideOverloadCall = insideConstructor && srcLine.contains("this(");
			lineOverloadedCall = currentLine;
		}
	}

	private void analyzeLinesExecutedByInvoked(boolean isEmptyMethod, int currentLine, String line,
			String srcLine) {
		if (returnedFromTestedInvoked)
			return;
		
		boolean insideAnonymousConstructor = insideConstructor && line.matches(REGEX_DOLLAR_SIGN_PLUS_NUMBERS);
		boolean returnedToTestMethod = line.contains(testMethod.getName());
		boolean insideTestedInvoked = !isEmptyMethod && !lastAddWasReturn && invokedDeclarationLine > 0 && 
				!insideAnonymousConstructor &&
				!srcLine.contains("} catch(Throwable _") &&
				(rbb == null || !rbb.isBalanceEmpty()) &&
				currentLine != lastLineAdded;
		
		if (returnedToTestMethod) {System.out.println("RETORNOU!\n");
			returnedFromTestedInvoked = true;
			newIteration = false;
			inMethod = false;
		} 
		else if (insideTestedInvoked) {
			testPath.add(currentLine);
			lastLineAdded = currentLine;
			lastAddWasReturn = (srcLine.contains("return ") && !srcLine.contains("if "));
		}
	}

	private boolean checkRepetition() {
//		if (!newIteration && srcLine.matches("[0-9]+(\\ |\\t)*\\}(\\ |\\t)*") && srcLine.equals(lastSrcLine)	) {
//			returnedFromTestedInvoked = true;
//			stopJDB = true;
//		}
		return !newIteration && srcLine.matches("[0-9]+(\\ |\\t)*\\}(\\ |\\t)*") && srcLine.equals(lastSrcLine);
	}

	private boolean returnedFromTestedInvoked() {
		if (!line.contains(testMethod.getName()))
			return false;
		
		boolean wasInsideMethodWithEmptyBody = (inMethod && insideConstructor && !insideOverloadCall && (isEmptyMethod(srcLine) || line.contains(testMethod.getName())));
		
		return (line.contains(testMethod.getName()) && !testPath.isEmpty()) || wasInsideMethodWithEmptyBody;
	}

	private void storeAnalyzedInvokedSignature(String line) {
		if (analyzedInvokedSignature.isBlank() && invokedDeclarationLine > 0 
				&& (inMethod || insideConstructor)) {
			// Fix anonymous signature
			if (anonymousConstructor || invokedNameContainsDollarSign) {	
				String[] signatureParams = invoked.getInvokedSignature().substring(
						invoked.getInvokedSignature().indexOf("(")+1, invoked.getInvokedSignature().indexOf(")")
				).split(",");
				StringBuilder params = new StringBuilder();
				

				// Parameter types
				for (int i=1; i<signatureParams.length; i++) {
					params.append(signatureParams[i].trim().replace(",", ""));
					
					if (i+1 != signatureParams.length)
						params.append(", ");
				}
				
				// Stores the fixed anonymous signature
				analyzedInvokedSignature = line.split(",")[1].split("\\.\\<init\\>")[0] + "(" + params.toString() + ")";
			}
			else {
				analyzedInvokedSignature = invoked.getInvokedSignature();
			}
			
			analyzedInvokedSignature = analyzedInvokedSignature.trim();
		}
	}
	
	private boolean isInvokedDeclarationLine(String line, String srcLine)
	{
		final String regexConstructorWithDollarSignPlusNumbers = "^.+\\$[0-9]+\\.\\<init\\>.*$";
		boolean hasInvokedName = line.contains(invoked.getInvokedName()+".") || 
								 line.contains(invoked.getInvokedName()+"(");
		boolean isInsideAnonymousConstructor = (anonymousConstructor && insideConstructor);
		
		return	srcLine.contains("@executionFlow.runtime.CollectCalls") &&
				!line.contains(testMethod.getName()) &&
				(isInsideAnonymousConstructor || hasInvokedName) && 
				!line.split(",")[1].matches(regexConstructorWithDollarSignPlusNumbers);
	}

	private boolean isInnerClassOrAnonymousClass() {
		boolean isInnerClassOrAnonymousClass;
		isInnerClassOrAnonymousClass = 
				line.contains("$") && 
				invokedNameContainsDollarSign && 
				!line.contains("line=1 ") &&
				!line.contains("jdk.") &&
				!line.contains("aspectj.") && 
				!line.contains("executionFlow.runtime") && 
				!srcLine.contains("package ") &&
				!line.contains(invoked.getInvokedName());
		return isInnerClassOrAnonymousClass;
	}

	private void checkIncorrectInvocationLine(String line) {
		if (line.contains("Stopping due to deferred breakpoint errors")) {
    		jdb.quit();
    		
    		throw new IllegalStateException("Incorrect invocation line {invocationLine: " 
					+ invoked.getInvocationLine() + ", test method signature: "
    				+ testMethod.getName() + ")" + ", invokedSignature: " 
					+ invoked.getInvokedSignature() + "}"
			);
    	}
	}
	
	private void checkInternalError(String line) throws IOException {
		if (isErrorMessage(line)) 
			throw new IOException("Error while running JDB");}

	private boolean isErrorMessage(String line) {
		return	line.contains("[INFO]")
				|| line.contains("Exception occurred") 
				|| line.contains("Input stream closed.")
//				|| line.contains("The application exited") 
				|| line.contains("FAILURES!!!") 
				|| line.contains("Caused by: ");
	}
	
	
	/**
	 * Gets executed line from source line.
	 *  
	 * @param		srcLine JDB output - source line
	 * 
	 * @return		Executed line or -1 if srcLine is empty or null
	 */
	private int getSrcLine(String srcLine) 
	{
		if (srcLine == null || srcLine.isEmpty())
			return -1;
		
		return Integer.valueOf(srcLine.replace(".", "").substring(0, srcLine.indexOf(" ")).trim());
	}
	
	/**
	 * Gets executed line from debugger line.
	 *  
	 * @param		srcLine JDB output - line
	 * 
	 * @return		Executed line or -1 if line is empty or null
	 */
	private int getLine(String line) 
	{
		if (line == null || line.isEmpty() || !line.contains(","))
			return -1;
		
		String lineNumber = line.split(",")[2].split("=")[1].split(" ")[0].trim();
		lineNumber = lineNumber.replaceAll("\\.", "");
		
		return Integer.valueOf(lineNumber);
	}
	
	/**
	 * Checks if current line of JDB is an internal method.
	 * 
	 * @param		line JDB output
	 * 
	 * @return		If current line of JDB is an internal method
	 */
	private boolean isInternalMethod(String line)
	{
		return 	!line.contains(invoked.getInvokedName()+".") && 
				!line.contains(invoked.getInvokedName()+"(") && 
				!line.contains("<init>") &&
				!line.contains(testMethod.getClassSignature());
	}
	
	private boolean isEmptyLine(String line) {
		final String regexEmptyOutput = "^(>(\\ |\\t)*)*main\\[[0-9]\\](\\ |\\t|>)*$";
		
		return 	line.matches(regexEmptyOutput)
				|| line.equals("\n") 
				|| line.equals("") 
				|| line.equals(" ")
				|| line.equals("> ")
				|| line.equals(">")
				|| line.equals(".");
	}
	
	/**
	 * Checks if current line of JDB is an empty line.
	 * 
	 * @param		line JDB output
	 * 
	 * @return		If current line of JDB is an empty line
	 */
	private boolean isNewIteration(String line)
	{
		return (
			!inMethod && !returnedFromTestedInvoked &&
			(
				line.contains("Breakpoint hit") || 
				( getLine(line) == invoked.getInvocationLine() &&
				  line.contains(testMethod.getClassSignature()) )
			)
		);
	}
	
	/**
	 * Checks if next line of JDB will be within a method.
	 * 
	 * @param		line JDB output
	 * 
	 * @return		If next line of JDB will be within a method
	 */
	private boolean willEnterInMethod(String line)
	{
		return 	newIteration || 
				( !inMethod && 
				  line.contains("Step completed") && 
				  line.contains(testMethod.getClassSignature()) && 
				  line.contains("line="+invoked.getInvocationLine()) ); 
	}
	
	/**
	 * Checks whether the current line of JDB is a method with empty body.
	 * 
	 * @param		srcLine JDB output - source line
	 * 
	 * @return		If current line is a method with empty body
	 */
	private boolean isEmptyMethod(String srcLine) {
		final String REGEX_ONLY_OPEN_CURLY_BRACKET = "([0-9]+)(\\ |\\t)+\\{((\\ |\\t)+)?$";
		final String REGEX_ONLY_CLOSED_CURLY_BRACKET = "([0-9]+)(\\ |\\t)+\\}((\\ |\\t)+)?$";
		final String REGEX_EMPTY_METHOD = "^([0-9]*)(\\t|\\ )*((([a-z]+\\ ){2,}"
				+ ".+\\(.*\\)(\\ |\\t)*\\{(\\ |\\t)*\\})|(\\{(\\t|\\ )*\\})|(\\}))$";
		
		
		return	srcLine.matches(REGEX_ONLY_CLOSED_CURLY_BRACKET)
				|| srcLine.matches(REGEX_EMPTY_METHOD)
				|| srcLine.matches(REGEX_ONLY_OPEN_CURLY_BRACKET);
	}
	
	/**
	 * Checks if current line of JDB is a native call.
	 * 
	 * @param		line JDB output
	 * @param		srcLine JDB output - source line
	 * 
	 * @return		If current line of JDB is a native call.
	 */
	private boolean isNativeCall()
	{
		final String REGEX_ANONYMOUS_CLASS = ".+\\$[0-9]+.+";

		
		return	(line.contains("line=1 ") || 
				line.contains("jdk.") || 
				line.contains("aspectj.") || 
				line.contains("executionFlow.runtime") || 
				srcLine.contains("package ") || (
					(
							!line.matches(REGEX_ANONYMOUS_CLASS) || 
							(line.matches(REGEX_ANONYMOUS_CLASS) && !srcLine.matches(".+\\{(\\ |\\t)*$"))
					)) &&
					!line.contains("<init>") &&
					!line.contains(invoked.getInvokedName()+".") && 
					!line.contains(invoked.getInvokedName()+"(") && 
					!line.contains(testMethod.getName())
				);
	}
	
	
	
	private boolean isMethodDeclaration(String line) {
		final String regexMethodDeclaration = "[\\ \\t0-9]*((public|proteted|private)"
				+ "(\\ |\\t)+)?[A-z]+\\(.*\\)(\\ |\\t)*\\{";
		
		return line.matches(regexMethodDeclaration);
	}
	
	/**
	 * Checks whether to exit the method.
	 * 
	 * @param		line JDB output
	 * @param		srcLine JDB output - source line
	 * @param		ignoreFlag Current value of the ignore flag
	 * 
	 * @return		If it is inside a method
	 */
	private boolean shouldExit()
	{
		return	invokedDeclarationLine > 0 &&
				!line.contains("<init>") &&
				srcLine.contains("return ") && !srcLine.contains("if ") &&
				(line.contains(invoked.getInvokedName()+".") || line.contains(invoked.getInvokedName()+"(")) ||
				(shouldIgnore() && line.contains(testMethod.getName()) && getSrcLine(srcLine) > invoked.getInvocationLine());
	}
	
	/**
	 * Checks if current line of JDB is within a method.
	 * 
	 * @param		line JDB output
	 * 
	 * @return		If current line of JDB is within a method
	 */
	private boolean isInsideMethod()
	{
		if (anonymousConstructor) {
			return	!line.contains(testMethod.getName()) &&
					!line.contains("$") && 
					line.contains("<init>") && 
					!returnedFromTestedInvoked;
		}
		else {
			return	!line.contains(testMethod.getName()) &&
					(line.contains(invoked.getInvokedName() + "(") || line.contains(invoked.getInvokedName() + ".<init>")) && 
					!returnedFromTestedInvoked;
		}
	}
	
	/**
	 * Checks whether JDB has started executing the method / constructor.
	 * 
	 * @param		line JDB output - source line
	 * 
	 * @return		If JDB has started executing the method / constructor
	 */
	private boolean hasStarted(String line)
	{
		return	line.contains("thread=") &&
				(line.contains("Breakpoint hit") || line.contains("Step completed"));
	}
}
