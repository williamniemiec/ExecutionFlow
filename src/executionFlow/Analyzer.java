package executionFlow;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import executionFlow.dependency.DependencyManager;
import executionFlow.info.InvokedInfo;
import executionFlow.util.ConsoleOutput;
import executionFlow.util.JDB;


/**
 * Computes test path from code debugging. Also, it registers methods called by
 * it.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		3.0.0
 * @since		2.0.0
 */
public class Analyzer 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	/**
	 * If true, displays shell output.
	 */
	private static final boolean DEBUG; 
	
	/**
	 * Stores signature of the class of the test method.
	 */
	private String classInvocationSignature;
	
	private String invokedSignature;
	private String invokedName;
	private String lastSrcLine = "";
	
	/**
	 * Stores signature of the test method.
	 */
	private String testMethodSignature;
	
	/**
	 * Line of test method that the method is called.
	 */
	private int invocationLine;
	
	private int methodDeclarationLine;
	private int lineOverloadedCall;
	
	/**
	 * Stores current test path.
	 */
	private List<Integer> testPath;
	
	/**
	 * Stores all computed test paths.
	 */
	private List<List<Integer>> testPaths;

	private JDB jdb;
	private boolean endOfMethod;
	private boolean newIteration;
	private boolean exitMethod;
	private boolean isInternalCommand;
	private boolean inMethod;
	private boolean withinConstructor;
	private boolean withinOverloadCall;
	private boolean lastAddWasReturn;

	
	//-------------------------------------------------------------------------
	//		Initialization block
	//-------------------------------------------------------------------------
	/**
	 * Enables or disables debug. If activated, displays shell output during 
	 * JDB execution (performance can get worse).
	 */
	static {
		DEBUG = false;
	}

	
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
		List<String> classPath = new ArrayList<>(), srcPath = new ArrayList<>();
		String methodClassPath, libPath_relative;
		Path classRootPath;			// Root path where the compiled file of invoked class is
		Path srcRootPath;			// Root path where the source file of the invoked is
		Path testMethodSrcPath;		// Root path where the source file of test method class is. It 
									// will be used as JDB root directory
		Path testClassRootPath;		// Root path where the compiled file of test method class is. It 
									// will be used as JDB root directory
		
		
		// Gets information about the test method of the invoked to be analyzed
		testMethodSignature = testMethodInfo.getInvokedSignature();
		testMethodSignature = testMethodSignature.substring(0, testMethodSignature.indexOf("(")+1);
		classInvocationSignature = testMethodInfo.getClassSignature();
		invocationLine = invokedInfo.getInvocationLine();
		invokedSignature = invokedInfo.getInvokedSignature();
		invokedName = invokedSignature.substring(invokedSignature.lastIndexOf("."), invokedSignature.indexOf("("));
		
		// Gets paths
		srcRootPath = extractRootPathDirectory(invokedInfo.getSrcPath(), invokedInfo.getPackage());
		classRootPath = extractRootPathDirectory(invokedInfo.getBinPath(), invokedInfo.getPackage());
		testClassRootPath = extractRootPathDirectory(testMethodInfo.getBinPath(), testMethodInfo.getPackage());
		testMethodSrcPath = extractRootPathDirectory(testMethodInfo.getSrcPath(), testMethodInfo.getPackage());
				
		// Configures JDB, indicating path of libraries, classes and source files
		methodClassPath = testClassRootPath.relativize(classRootPath).toString();
		libPath_relative = testClassRootPath.relativize(ExecutionFlow.getLibPath()).toString() + "\\";
		
		// Fetch dependencies
		if (!DependencyManager.hasDependencies()) {
			ConsoleOutput.showInfo("Fetching dependencies...");
			DependencyManager.fetch();
			ConsoleOutput.showInfo("Fetch completed");
		}
		
		// Sets class path
		classPath.add(".");
		classPath.add(libPath_relative + "*");
		classPath.add("\"" + testClassRootPath.relativize(DependencyManager.getPath()).toString() + "\\*");
		
		if (methodClassPath.isEmpty())
			classPath.add("..\\classes\\");
		else
			classPath.add(methodClassPath);
		
		// Sets source path
		srcPath.add(testClassRootPath.relativize(srcRootPath).toString());
		srcPath.add(testClassRootPath.relativize(testMethodSrcPath).toString());
		
		jdb = new JDB(testClassRootPath, classPath, srcPath);
		testPath = new ArrayList<>();
		testPaths = new ArrayList<>();
		
		// -----{ DEBUG }-----
		if (DEBUG) {
			ConsoleOutput.showInfo("Classpath: " + classPath);
			ConsoleOutput.showInfo("Srcpath: " + srcPath);
			ConsoleOutput.showInfo("Working directory: " + testClassRootPath);
		}
		// -----{ END DEBUG }-----
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
	 * @throws		IllegalStateException If JDB has not been initialized
	 */
	public Analyzer run() throws IOException 
	{
		if (jdb == null)
			throw new IllegalStateException("JDB has not been initialized");
		
		boolean wasNewIteration = false;
		
		
		// Runs JDB
		// -----{ DEBUG }-----
		if (DEBUG) {
			ConsoleOutput.showDebug("COMMAND: " + "clear ");
			ConsoleOutput.showDebug("COMMAND: " + "stop at " + classInvocationSignature + ":" + invocationLine);
			ConsoleOutput.showDebug("COMMAND: " + "run org.junit.runner.JUnitCore "+classInvocationSignature);
		}
		// -----{ END DEBUG }-----
		
		try {
			jdb.start().send("clear", "stop at " + classInvocationSignature + ":" + invocationLine,
					"run org.junit.runner.JUnitCore "+classInvocationSignature);
			
			// Executes while inside the test method
			while (!endOfMethod) {
				// Checks if output has finished processing
				while (!wasNewIteration && !parseOutput()) { continue; }
				
				wasNewIteration = false;
				
				if (endOfMethod) {
					// Checks if there is unsaved test path
					if (testPath.size() > 0) {	// If there is one, save it
						testPaths.add(testPath);
					} 
				}
				// Checks if has exit the method
				else if (exitMethod && !isInternalCommand) {
					// Saves test path
					testPaths.add(testPath);
					
					// Prepare for next test path
					testPath = new ArrayList<>();
					
					// Checks if method is within a loop
					// -----{ DEBUG }-----
					if (DEBUG) { ConsoleOutput.showDebug("COMMAND: cont"); }
					// -----{ END DEBUG }-----
					
					// Resets exit method
					exitMethod = false;
					inMethod = false;
					lastAddWasReturn = false;
					
					jdb.send("cont");
				} 
				else if (newIteration) {
					wasNewIteration = true;
					
					// Enters the method, ignoring native methods
					// -----{ DEBUG }-----
					if (DEBUG) { ConsoleOutput.showDebug("COMMAND: step into"); }
					// -----{ END DEBUG }-----
					
					jdb.send("step into");
					parseAll();
					
					while (isInternalCommand) {
						// -----{ DEBUG }-----
						if (DEBUG) { ConsoleOutput.showDebug("COMMAND: next"); }
						// -----{ END DEBUG }-----
						
						jdb.send("next");
						parseAll();
					}
				} 
				else if (!endOfMethod) {
					// -----{ DEBUG }-----
					if (DEBUG) { ConsoleOutput.showDebug("COMMAND: next"); }
					// -----{ END DEBUG }-----
					
					jdb.send("next");
				}
			}
		}
		catch (IOException e) {
			// Exits jdb
			jdb.send("clear " + classInvocationSignature + ":" + invocationLine, "exit", "exit");
			jdb.quit();
			throw e;
		}

		// Exits JDB
		// -----{ DEBUG }-----
		if (DEBUG) {
			ConsoleOutput.showDebug("COMMAND: " + "clear " + classInvocationSignature + ":" + invocationLine);
			ConsoleOutput.showDebug("COMMAND: " + "exit");
			ConsoleOutput.showDebug("COMMAND: " + "exit");
		}
		// -----{ END DEBUG }-----
		
		jdb.send("clear " + classInvocationSignature + ":" + invocationLine, "exit", "exit");
		jdb.quit();
		
		return this;
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
	public List<String> getMethodsCalledByTestedInvoked()
	{
		File f = new File(ExecutionFlow.getAppRootPath().toFile(), "mcti.ef");
		Map<String, List<String>> invokedMethods = new HashMap<>();
		
		
		if (f.exists()) {
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
				invokedMethods = (Map<String, List<String>>) ois.readObject();
			} catch (IOException | ClassNotFoundException e) {
				invokedMethods = null;
				ConsoleOutput.showError("Called methods by tested invoked - " + e.getMessage());
				e.printStackTrace();
			}
		
			f.delete();
		}

		return invokedMethods.containsKey(invokedSignature) ? invokedMethods.get(invokedSignature) : null;
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
	 * Gets computed test path.
	 * 
	 * @return		Computed test path
	 */
	public List<List<Integer>> getTestPaths()
	{ 
		return testPaths;
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
	private Path extractRootPathDirectory(Path classPath, String classPackage) throws IllegalStateException
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
	 * @throws		IOException If it cannot read JDB output
	 * 
	 * @apiNote		If {@link #DEBUG} is activated, it will display JDB 
	 * output on the console
	 */
	private boolean parseOutput() throws IOException
	{
		boolean readyToReadInput = false, ignore = false, isEmptyMethod = false;
		final String regex_emptyOutput = "^(>(\\ |\\t)*)*main\\[[0-9]\\](\\ |\\t|>)*$";
		int currentLine = -1;
		String line, srcLine = null;
		
		
		if (jdb.isReady()) {        	
        	isInternalCommand = false;
        	line = jdb.read();
        	
        	if ( line.contains("Stopping due to deferred breakpoint errors"))
        		throw new IOException("Incorrect invocation line");
        	
        	if (isEmptyLine(line) || line.matches(regex_emptyOutput)) 
        		return false;
        	
        	// -----{ DEBUG }-----
        	if (DEBUG) { ConsoleOutput.showDebug("LINE: "+line); }
    		// -----{ END DEBUG }-----
        	
        	endOfMethod = endOfMethod == true ? endOfMethod : isEndOfTestMethod(line);
        	isInternalCommand = isInternalMethod(line);
        	
        	// Checks if JDB has started and is ready to receive debug commands
    		if ( !endOfMethod && hasStarted(line)) {
    			readyToReadInput = true;
    			srcLine = jdb.read();
    			isEmptyMethod = isEmptyMethod(srcLine);
    			inMethod = isInsideMethod(line);
    			newIteration = isNewIteration(line);
        		withinConstructor = line.contains(".<init>");
        		currentLine = getSrcLine(srcLine);

        		// Ignores native calls
        		if (isNativeCall(line, srcLine)) {
        			isInternalCommand = true;
        			inMethod = false;
        			ignore = true;
        		}
        		// Ignores overload calls
        		else if (withinOverloadCall) {
        			ignore = !isEmptyMethod;
        		}
        		
        		// Checks if it is in the constructor signature
        		if (srcLine.contains("@executionFlow.runtime.CollectCalls")) {
        			ignore = true;
        			methodDeclarationLine = currentLine;
        		}
        		
        		ignore = withinOverloadCall || ignore || shouldIgnore(srcLine, currentLine);
        		
        		if (methodDeclarationLine == 0 && currentLine > 0 && 
        				(line.contains(invokedName+".") || line.contains(invokedName+"(")))
        			methodDeclarationLine = currentLine;        		
        		
        		// Checks if it is still within a constructor
        		if (inMethod && withinConstructor && !withinOverloadCall && 
        				(isEmptyMethod || line.contains(testMethodSignature))) {
        			withinConstructor = false;
        			exitMethod = true;
        			readyToReadInput = true;
        		}
    			
        		if (!isInternalCommand && !exitMethod && !ignore) {
        			if (inMethod || withinConstructor) {
        				// Checks if returned from the method
        				if (line.contains(testMethodSignature)) {
        					exitMethod = true;
        					newIteration = false;
        					inMethod = false;
        				} 
        				// Checks if it is still inside the method
        				else if (!isEmptyMethod && !lastAddWasReturn) {
    						testPath.add(currentLine);
    						lastAddWasReturn = srcLine.contains("return "); 
    					}
            		}
        			else if (willEnterInMethod(line)) {
            			inMethod = true;
            		}
        		}
        		
        		// Checks whether it is a constructor overloaded call
        		if (!withinOverloadCall) {
        			withinOverloadCall = withinConstructor && srcLine.contains("this(");
        			lineOverloadedCall = currentLine;
        		}
    		}
    		
    		if (endOfMethod) {
    			readyToReadInput = true;
    		}
    		
    		if (srcLine != null && !srcLine.isEmpty()) {
    			if (withinOverloadCall && currentLine == lineOverloadedCall + 1) {
					withinOverloadCall = false;
    			}
    			
    			if (!withinOverloadCall) {
	    			if (exitMethod) {
	    				inMethod = false;
	    				lastAddWasReturn = false;
	    				methodDeclarationLine = 0;
	    			}
	    			
	    			if (!newIteration && srcLine.matches("[0-9]+(\\ |\\t)*\\}(\\ |\\t)*") && srcLine.equals(lastSrcLine)) {
	    				exitMethod = true;
	    				endOfMethod = true;
	    			}
	    			
	    			lastSrcLine = srcLine;
	    			
	    			if (isInsideMethod(line, srcLine, ignore))
	    				exitMethod = true;
    			}
    			else if (isEmptyMethod) {
    				withinOverloadCall = false;
				}
    			
    			// -----{ DEBUG }-----
    			if (DEBUG) { ConsoleOutput.showDebug("SRC: "+srcLine); }
    			// -----{ END DEBUG }-----	    		
    		}
		} 
		
		return readyToReadInput;
	}
	
	/**
	 * Reads all available output.
	 * 
	 * @throws		IOException If it cannot read JDB output
	 */
	private void parseAll() throws IOException
	{
		while (!parseOutput()) {
			continue;
		}
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
	 * Checks if current line of JDB has reached the end of the test method.
	 * 
	 * @param		line JDB output
	 * 
	 * @return		If current line of JDB has reached the end of the test method
	 */
	private boolean isEndOfTestMethod(String line)
	{
		return line.contains("The application exited");
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
		return 	!line.contains(invokedName+".") && 
				!line.contains(invokedName+"(") && 
				!line.contains(classInvocationSignature);
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
			!inMethod && !exitMethod &&
			(
				line.contains("Breakpoint hit") || 
				( line.contains("line="+invocationLine) && 
				  line.contains(classInvocationSignature) )
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
				  line.contains(classInvocationSignature) && 
				  line.contains("line="+invocationLine) ); 
	}
	
	/**
	 * Checks if current line of JDB is an empty method.
	 * 
	 * @param		srcLine JDB output - source line
	 * 
	 * @return		If current line of JDB is an empty method
	 */
	private boolean isEmptyMethod(String srcLine)
	{
		final String regex_onlyCurlyBracket = "([0-9]+)(\\ |\\t)+\\}((\\ |\\t)+)?($)";
		final String regex_emptyMethod = "^([0-9]*)(\\t|\\ )*((([a-z]+\\ ){2,}"
				+ ".+\\(.*\\)(\\ |\\t)*\\{(\\ |\\t)*\\})|(\\{(\\t|\\ )*\\})|(\\}))$";
		
		
		return	srcLine.matches(regex_onlyCurlyBracket) ||
				srcLine.matches(regex_emptyMethod);
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
		return	line.contains("line=1 ") || 
				line.contains("jdk.") || 
				line.contains("aspectj.") || 
				line.contains("executionFlow.runtime") || 
				srcLine.contains("package ") || (
					!line.contains(invokedName+".") && 
					!line.contains(invokedName+"(") && 
					!line.contains(testMethodSignature)
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
		final String regex_multiLineArg = "^[0-9]*(\\t|\\ )+[A-z0-9$\\-_\\.\\,\\ \\:]+(\\);)?$";
		
		
		return	(currentLine != -1 && currentLine < methodDeclarationLine) ||
				currentLine == 1 ||
				srcLine.contains(" class ") ||
				srcLine.matches(regex_multiLineArg);
	}
	
	/**
	 * Checks whether it is inside a method.
	 * 
	 * @param		line JDB output
	 * @param		srcLine JDB output - source line
	 * @param		ignoreFlag Current value of the ignore flag
	 * 
	 * @return		If it is inside a method
	 */
	private boolean isInsideMethod(String line, String srcLine, boolean ignoreFlag)
	{
		return	(srcLine.contains("return ") || srcLine.matches("[0-9]+(\\ |\\t)*\\}(\\ |\\t)*")) && 
				(line.contains(invokedName+".") || line.contains(invokedName+"(")) ||
				(ignoreFlag == true && line.contains(testMethodSignature) && getSrcLine(srcLine) > invocationLine);
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
		return	!line.contains(testMethodSignature) &&
				line.contains(invokedName+"(") && 
				!exitMethod;
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
