package executionFlow.analyzer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import executionFlow.ExecutionFlow;
import executionFlow.LibraryManager;
import executionFlow.info.InvokedInfo;
import executionFlow.util.Clock;
import executionFlow.util.JDB;
import executionFlow.util.Logger;
import executionFlow.util.balance.RoundBracketBalance;


/**
 * Computes test path from code debugging. Also, it registers methods called by
 * it.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.2
 * @since		2.0.0
 */
public class Analyzer 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final String REGEX_MULTILINE_ARGS = 
			"^[0-9]*(\\t|\\ )+[A-z0-9$\\-_\\.\\,\\ \\:]+(\\);)?$";

	private static final String REGEX_DOLLAR_SIGN_PLUS_NUMBERS = "^.+\\$[0-9]+.*$";

	private String lastSrcLine = "";
	private String analyzedInvokedSignature = "";
	
	private int invokedDeclarationLine;
	private int lineOverloadedCall;
	private int lastLineAdded = -1;
	
	/**
	 * Stores current test path.
	 */
	private List<Integer> testPath;
	
	/**
	 * Stores all computed test paths.
	 */
	private volatile List<List<Integer>> testPaths;

	private JDB jdb;
	private boolean stopJDB;
	private boolean newIteration;
	private boolean returnedFromTestedInvoked;
	private boolean isInternalCommand;
	private boolean inMethod;
	private boolean insideConstructor;
	private boolean insideOverloadCall;
	private boolean lastAddWasReturn;
	private boolean isMethodMultiLineArgs;
	private boolean anonymousConstructor;
	private boolean invokedNameContainsDollarSign;
	private volatile static boolean timeout;
	private volatile boolean lock;
	private RoundBracketBalance rbb;

	private InvokedInfo invoked;
	private InvokedInfo testMethod;

	
	//-------------------------------------------------------------------------
	//		Constructors
	//-------------------------------------------------------------------------	
	/**
	 * Computes test path from code debugging along with methods called by it.
	 * 
	 * @throws		IOException If occurs an error while fetching dependencies
	 */
	public Analyzer(InvokedInfo invokedInfo, InvokedInfo testMethodInfo) throws IOException
	{
		this.invoked = invokedInfo;
		this.testMethod = testMethodInfo;
		
		invokedNameContainsDollarSign = invoked.getInvokedName().substring(1).matches(REGEX_DOLLAR_SIGN_PLUS_NUMBERS);
		anonymousConstructor = checkAnonymousConstructor(invokedInfo.getClassSignature());

		initializeJDB(testMethodInfo, invokedInfo);
		testPath = new ArrayList<>();
		testPaths = new ArrayList<>();
	}

	private void initializeJDB(InvokedInfo testMethodInfo, InvokedInfo invokedInfo) {
		Path testClassRootPath = extractRootPathDirectory(testMethodInfo.getBinPath(), testMethodInfo.getPackage());
		
		List<Path> srcPath = getSourcePath(invokedInfo, testMethodInfo, testClassRootPath);
		List<Path> classPath = getClassPath(testClassRootPath);

		jdb = new JDB(testClassRootPath, classPath, srcPath);
		
		Logger.debug("Analyzer", "Classpath: " + classPath);
		Logger.debug("Analyzer", "Srcpath: " + srcPath);
		Logger.debug("Analyzer", "Working directory: " + testClassRootPath);
		
	}

	private List<Path> getClassPath(Path testClassRootPath) {
		List<Path> classPath = new ArrayList<>();
		for (String cp : System.getProperty("java.class.path").split(";")) {
			classPath.add(testClassRootPath.relativize(Path.of(cp)));
		}
		classPath.add(testClassRootPath.relativize(LibraryManager.getLibrary("JUNIT_4")));
		classPath.add(testClassRootPath.relativize(LibraryManager.getLibrary("HAMCREST")));
		return classPath;
	}


	private List<Path> getSourcePath(InvokedInfo invokedInfo, InvokedInfo testMethodInfo, Path testClassRootPath) {
		Path testMethodSrcPath;		// Root path where the source file of test method class is. It will be used as JDB root directory
		testMethodSrcPath = extractRootPathDirectory(testMethodInfo.getSrcPath(), testMethodInfo.getPackage());
		
		// Sets source path
		List<Path> srcPath = new ArrayList<>();
		// Root path where the source file of the invoked is
		Path srcRootPath = extractRootPathDirectory(invokedInfo.getSrcPath(), invokedInfo.getPackage());
		srcPath.add(testClassRootPath.relativize(srcRootPath));
		srcPath.add(testClassRootPath.relativize(testMethodSrcPath));
		
		// Fix source file of anonymous and inner classes
		srcPath.add(testClassRootPath.relativize(
				new File(ExecutionFlow.getCurrentProjectRoot().toFile(), "/src/main/java").toPath())
		);
		
		if (testMethodInfo.getSrcPath().equals(invokedInfo.getSrcPath())) {
			srcPath.add(
					testClassRootPath.relativize(
							new File(ExecutionFlow.getCurrentProjectRoot().toFile(), "/src/test/java").toPath())
			);
		}
		return srcPath;
	}
	
	
	private boolean checkAnonymousConstructor(String classSignature) {
		boolean hasDollarSign = classSignature.contains("$");

		return hasDollarSign ? invoked.getInvokedSignature().matches(REGEX_DOLLAR_SIGN_PLUS_NUMBERS)
				: false;
	}


	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Runs test method through JDB, registering its test path along with the
	 * methods called by all methods or constructors called from this test
	 * method.
	 * 
	 * @return		Itself to allow chained calls
	 * 
	 * @throws		IOException If an error occurs while reading the output 
	 * @throws		IllegalStateException If JDB has not been initialized or 
	 * if invocation line is incorrect
	 */
	public Analyzer run() throws IOException 
	{
		if (jdb == null)
			throw new IllegalStateException("JDB has not been initialized");
		
		final int timeoutID = 1;
		final int timeoutTime = 10 * 60 * 1000;

		try {
			startJDB();
			enableTimeout(timeoutID, timeoutTime);
			analyzeResults();
		}
		catch (IOException e) {
			disableTimeout(timeoutID);
			closeJDB();
			
			throw e;
		}
		
		closeJDB();
		waitForTimeout();
		disableTimeout(timeoutID);
		
		return this;
	}

	private void waitForTimeout() {
		// Waits for timeout thread to finish running (if running)
		while (lock);
	}

	private void startJDB() throws IOException {
		Logger.debug("Analyzer", "COMMAND: " + "clear ");
		Logger.debug("Analyzer", "COMMAND: " + "stop at " + testMethod.getClassSignature() + ":" + invoked.getInvocationLine());
		Logger.debug("Analyzer", "COMMAND: " + "run org.junit.runner.JUnitCore " + testMethod.getClassSignature());
		
		jdb.start().send("clear", "stop at " + testMethod.getClassSignature() + ":" + invoked.getInvocationLine(),
				"run org.junit.runner.JUnitCore "+testMethod.getClassSignature());
	}

	private void closeJDB() {
		Logger.debug("Analyzer", "COMMAND: " + "clear " + testMethod.getClassSignature() + ":" + invoked.getInvocationLine());
		Logger.debug("Analyzer", "COMMAND: " + "exit");
		
		jdb.send("clear " + testMethod.getClassSignature() + ":" + invoked.getInvocationLine(), "exit", "exit");
		jdb.quit();
	}

	private void analyzeResults() throws IOException {
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
				parseAll();
				skipInternalCalls();
			} 
			else if (!stopJDB) {
				sendJDB("next");
			}
		}
	}

	private void skipInternalCalls() throws IOException {
		while (isInternalCommand) {					
			sendJDB("next");
			parseAll();
		}
	}

	private void fixAnonymousConstructorTestPaths() {
		if (anonymousConstructor && testPath.size() > 1) {
			testPath.remove(testPath.size() - 1);
			testPath.remove(0);
		}
	}

	private void sendJDB(String command) {
		Logger.debug("Analyzer", "COMMAND: " + command);
		jdb.send(command);
	}

	private void getsReadyForTheNextTest() {
		testPath = new ArrayList<>();
		lastLineAdded = -1;
		
		// Resets exit method
		returnedFromTestedInvoked = false;
		inMethod = false;
		lastAddWasReturn = false;
	}

	private void disableTimeout(final int TIMEOUT_ID) {
		Clock.clearTimeout(TIMEOUT_ID);
	}

	private void enableTimeout(final int TIMEOUT_ID, final int TIMEOUT_TIME) {
		timeout = false;
		
		Clock.setTimeout(() -> {
			File mcti = new File(ExecutionFlow.getAppRootPath().toFile(), "mcti.ef");
	
			lock = true;
			
			mcti.delete();
			jdb.quit();
			testPaths.clear();
			Analyzer.setTimeout(true);
			
			lock = false;
		}, TIMEOUT_ID, TIMEOUT_TIME);
	}
	
	/**
	 * Deletes file containing methods called by tested invoked.
	 * 
	 * @return		If file has been successfully removed
	 */
	public boolean deleteMethodsCalledByTestedInvoked()
	{
		return new File(ExecutionFlow.getAppRootPath().toFile(), "mcti.ef").delete();
	}
	
	/**
	 * Gets root path directory from a class package. 
	 * <h2>Example</h2>
	 * <li>Class path: <code>C:/myProgram/src/name1/name2/name3</code></li>
	 * <li>Class package: <code>name1.name2.name3</code></li>
	 * <li><b>Return:</b> <code>C:/myProgram/src/</code></li>
	 * 
	 * @param		classPath Class path
	 * @param		classPackage Class package
	 * 
	 * @return		Root path directory
	 * 
	 * @throws		IllegalStateException If source file path is null
	 */
	private Path extractRootPathDirectory(Path classPath, String classPackage)
	{
		if (classPath == null) 
			throw new IllegalStateException("Source file path cannot be null");

		int packageFolders = 0;
		
		if (!classPackage.isEmpty()) {
			packageFolders = classPackage.split("\\.").length;
		}
		
		classPath = classPath.getParent();
		
		for (int i=0; i<packageFolders; i++) {
			classPath = classPath.getParent();
		}
		
		return classPath;
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
	private boolean parseOutput() throws IOException {
		boolean readyToReadInput = false;
		boolean ignore = false;
		boolean isEmptyMethod = false;
		int currentLine = -1;
		String line;
		String srcLine = null;
		
		if (!jdb.isReady()) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) 
    		{}
			
			return false;
		}
		       	
    	isInternalCommand = false;
    	line = jdb.read();
    	Logger.debug("Analyzer", "LINE: "+line);
    	
    	checkIncorrectInvocationLine(line);
    	checkInternalError(line);
    	
    	
    	if (line.contains("The application exited") || line.contains("Caused by: ")) {
    		stopJDB = true;
    		returnedFromTestedInvoked = false;
    		return true;
    	}
    	
    	final String REGEX_EMPTY_OUTPUT = "^(>(\\ |\\t)*)*main\\[[0-9]\\](\\ |\\t|>)*$";
    	if (isEmptyLine(line) || line.matches(REGEX_EMPTY_OUTPUT)) {
    		return false;
    	}

    	isInternalCommand = isInternalMethod(line);
    	
    	// Checks if JDB has started and is ready to receive debug commands
		if (!stopJDB && hasStarted(line)) {
			readyToReadInput = true;
			srcLine = jdb.read();
			isEmptyMethod = isEmptyMethod(srcLine);
			inMethod = isInsideMethod(line);
			newIteration = isNewIteration(line);
    		insideConstructor = line.contains(".<init>");
    		
    		

    		currentLine = (srcLine == null || srcLine.isEmpty()) ? getLine(line) : getSrcLine(srcLine);
    		
    		if (srcLine.contains("(") && !srcLine.contains(")") && rbb == null) {
				rbb = new RoundBracketBalance();
    		}
    		
    		if (rbb != null) {
    			rbb.parse(srcLine);
    		}

    		// Ignores native calls
    		if (isNativeCall(line, srcLine)) {
    			isInternalCommand = true;
    			inMethod = false;
    			ignore = true;
    		}
    		// Ignores overload calls
    		else if (insideOverloadCall) {
    			ignore = !isEmptyMethod;
    		}
    		
    		// Checks if it is in the constructor signature
    		if (srcLine.contains("@executionFlow.runtime.CollectCalls")) {
    			ignore = true;
    		}
    		
    		ignore = insideOverloadCall || ignore || shouldIgnore(srcLine, currentLine) || isMethodMultiLineArgs;
    		isMethodMultiLineArgs = srcLine.matches(REGEX_MULTILINE_ARGS);
    		
    		// Checks if invoked is a inner class or anonymous class
    		if (isInnerClassOrAnonymousClass(line, srcLine)) {
    			// If it is, it guarantees that its test path will be computed
    			inMethod = true;
    			ignore = false;
    			isInternalCommand = false;
    		}
    		
    		// Gets the line on which the invoked is declared
    		boolean wasInvokedDeclarationLineNumberInitialized = invokedDeclarationLine > 0;
    		if (!wasInvokedDeclarationLineNumberInitialized && currentLine > 1) {
    			if (isInvokedDeclarationLine(line) || inMethod) {
    				invokedDeclarationLine = currentLine;        		
    			}
    		}
    		
    		// Stores analyzed signature
        	storeAnalyzedInvokedSignature(line);
        	
        	// Checks if it exits the method or constructor
        	
        	System.out.println("contains? "+line.contains(testMethod.getName())+"\n");
    		if (returnedFromTestedInvoked(line, isEmptyMethod, line.contains(testMethod.getName()))) {
    			readyToReadInput = true;
    			insideConstructor = false;
    			returnedFromTestedInvoked = true;
    		}
    		
    		if (!isInternalCommand && !ignore) {
    			if (inMethod || insideConstructor)
    				analyzeLinesExecutedByInvoked(isEmptyMethod, currentLine, line, srcLine);
    			else if (willEnterInMethod(line)) {
    				inMethod = true;
    			}
    		}
    		
    		// Checks whether it is a constructor overloaded call
    		checkOverloadedConstructor(currentLine, srcLine);
    		
    		if (rbb != null && rbb.isBalanceEmpty())
				rbb = null;
		}
		
		if (stopJDB) {
			readyToReadInput = true;
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
    			
    			checkRepetition(srcLine);
    			
    			
    			lastSrcLine = srcLine;
    			
    			if (shouldExit(line, srcLine, ignore))
    				returnedFromTestedInvoked = true;
			}
			else if (isEmptyMethod) {
				insideOverloadCall = false;
			}
			
			if (returnedFromTestedInvoked) {
				currentLine = -1;
			}
			
			if (srcLine.contains("FAILURES!!!")) {
				stopJDB = true;
				returnedFromTestedInvoked = true;
			}
			
			Logger.debug("Analyzer", "SRC: "+srcLine);
		}
		
		
		return readyToReadInput;
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
		
		if (returnedToTestMethod) {
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

	private void checkRepetition(String srcLine) {
		if (!newIteration && srcLine.matches("[0-9]+(\\ |\\t)*\\}(\\ |\\t)*") && srcLine.equals(lastSrcLine)	) {
			returnedFromTestedInvoked = true;
			stopJDB = true;
		}
	}

	private boolean returnedFromTestedInvoked(String line, boolean isEmptyMethod, boolean containsTestMethodSignature) {
		if (!line.contains(testMethod.getName()))
			return false;
		
		boolean wasInsideMethodWithEmptyBody = (inMethod && insideConstructor && !insideOverloadCall && (isEmptyMethod || containsTestMethodSignature));
		
		return (containsTestMethodSignature  && !testPath.isEmpty()) || wasInsideMethodWithEmptyBody;
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
	
	private boolean isInvokedDeclarationLine(String line)
	{
		final String regexConstructorWithDollarSignPlusNumbers = "^.+\\$[0-9]+\\.\\<init\\>.*$";
		boolean hasInvokedName = line.contains(invoked.getInvokedName()+".") || 
								 line.contains(invoked.getInvokedName()+"(");
		boolean isInsideAnonymousConstructor = (anonymousConstructor && insideConstructor);
		
		return	//srcLine.contains("@executionFlow.runtime.CollectCalls") &&
				!line.contains(testMethod.getName()) &&
				(isInsideAnonymousConstructor || hasInvokedName) && 
				!line.split(",")[1].matches(regexConstructorWithDollarSignPlusNumbers);
	}

	private boolean isInnerClassOrAnonymousClass(String line, String srcLine) {
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
	
	private void checkInternalError(String line) throws IOException 
	{
		boolean error = line.contains("[INFO]") || 
						line.contains("Exception occurred") || 
						line.contains("Input stream closed.");
		
		if (error)
			throw new IOException("Error while running JDB");
	}

	/**
	 * Reads all available output.
	 * 
	 * @throws		IOException If it cannot read JDB output
	 */
	private void parseAll() throws IOException
	{
		while (!parseOutput())
			;
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
	
	/**
	 * Checks if current line of JDB is an empty line.
	 * 
	 * @param		line JDB output
	 * 
	 * @return		If current line of JDB is an empty line
	 */
	private boolean isEmptyLine(String line)
	{
		return 	line.equals("\n") || 
				line.equals("") || 
				line.equals(" ") ||
				line.equals("> ") ||
				line.equals(">") ||
				line.equals(".");
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
				( line.contains("line="+invoked.getInvocationLine()) && 
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
	private boolean isEmptyMethod(String srcLine)
	{
		final String REGEX_ONLY_CLOSED_CURLY_BRACKET = "([0-9]+)(\\ |\\t)+\\}((\\ |\\t)+)?($)";
		final String REGEX_EMPTY_METHOD = "^([0-9]*)(\\t|\\ )*((([a-z]+\\ ){2,}"
				+ ".+\\(.*\\)(\\ |\\t)*\\{(\\ |\\t)*\\})|(\\{(\\t|\\ )*\\})|(\\}))$";
		
		
		return	srcLine.matches(REGEX_ONLY_CLOSED_CURLY_BRACKET) ||
				srcLine.matches(REGEX_EMPTY_METHOD);
	}
	
	/**
	 * Checks if current line of JDB is a native call.
	 * 
	 * @param		line JDB output
	 * @param		srcLine JDB output - source line
	 * 
	 * @return		If current line of JDB is a native call.
	 */
	private boolean isNativeCall(String line, String srcLine)
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
	
	/**
	 * Checks whether current line should be ignored.
	 * 
	 * @param		srcLine JDB output - source line
	 * @param		currentLine Line number in the source file
	 * 
	 * @return		If line should be ignored
	 */
	private boolean shouldIgnore(String srcLine, int currentLine)
	{
		final String REGEX_ONLY_OPEN_CURLY_BRACKET = "([0-9]+)(\\ |\\t)+\\{((\\ |\\t)+)?($)";
		final String REGEX_METHOD_DECLARATION = "[\\ \\t0-9]*((public|proteted|private)(\\ |\\t)+)?[A-z]+\\(.*\\)(\\ |\\t)*\\{";
		
		
		return	srcLine.matches(REGEX_METHOD_DECLARATION) ||
				(currentLine != -1 && currentLine < invokedDeclarationLine) ||
				currentLine == 1 ||
				srcLine.contains(" class ") ||
				srcLine.matches(REGEX_MULTILINE_ARGS) ||
				srcLine.matches(REGEX_ONLY_OPEN_CURLY_BRACKET);
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
	private boolean shouldExit(String line, String srcLine, boolean ignoreFlag)
	{
		return	invokedDeclarationLine > 0 &&
				!line.contains("<init>") &&
				srcLine.contains("return ") && !srcLine.contains("if ") &&
				(line.contains(invoked.getInvokedName()+".") || line.contains(invoked.getInvokedName()+"(")) ||
				(ignoreFlag == true && line.contains(testMethod.getName()) && getSrcLine(srcLine) > invoked.getInvocationLine());
	}
	
	/**
	 * Checks if current line of JDB is within a method.
	 * 
	 * @param		line JDB output
	 * 
	 * @return		If current line of JDB is within a method
	 */
	private boolean isInsideMethod(String line)
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
	
	
	//-------------------------------------------------------------------------
	//		Getters
	//-------------------------------------------------------------------------
	/**
	 * Gets invoked signature of the invoked analyzed by the analyzer.
	 * 
	 * @return		Invoked signature
	 */
	public String getAnalyzedInvokedSignature()
	{
		return analyzedInvokedSignature.replace('$', '.');
	}
	
	/**
	 * Gets methods called by tested invoked. It will return signature of all 
	 * called methods from tested methods.
	 * 
	 * @return		Null if tested invoked does not call methods; otherwise, 
	 * returns list of signature of methods called by tested invoked
	 * 
	 * @implSpec	After call this method, the file containing methods called
	 * by tested invoked will be deleted. Therefore, this method can only be 
	 * called once for each {@link #run JDB execution}
	 */
	@SuppressWarnings("unchecked")
	public Set<String> getMethodsCalledByTestedInvoked()
	{
		File f = new File(ExecutionFlow.getAppRootPath().toFile(), "mcti.ef");
		Map<String, Set<String>> invokedMethods = new HashMap<>();
		String invokedSignatureWithoutDollarSign = invoked.getInvokedSignature().replaceAll("\\$", ".");
		
		
		if (f.exists()) {
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
				invokedMethods = (Map<String, Set<String>>) ois.readObject();
			} 
			catch (IOException | ClassNotFoundException e) {
				invokedMethods = null;
				Logger.error("Called methods by tested invoked - " + e.getMessage());
				e.printStackTrace();
			}
		
			f.delete();
		}

		return invokedMethods.containsKey(invokedSignatureWithoutDollarSign) ? 
				invokedMethods.get(invokedSignatureWithoutDollarSign) : null;
	}
	
	/**
	 * Gets computed test path.
	 * 
	 * @return		Computed test path
	 */
	public List<List<Integer>> getTestPaths()
	{ 	
		return testPaths;
	}
	
	/**
	 * Defines whether the execution time has been exceeded.
	 * 
	 * @param		status True if the execution time has been exceeded; false 
	 * otherwise
	 */
	public static void setTimeout(boolean status)
	{
		timeout = status;
	}
	
	/**
	 * Checks if the execution time has been exceeded.
	 * 
	 * @return		True if the execution time has been exceeded; false otherwise
	 */
	public static boolean getTimeout()
	{
		return timeout;
	}
}
