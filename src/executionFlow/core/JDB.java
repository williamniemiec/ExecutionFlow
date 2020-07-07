package executionFlow.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import executionFlow.ConsoleOutput;
import executionFlow.ExecutionFlow;
import executionFlow.info.InvokerInfo;


/**
 * Computes test path from code debugging.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		1.5
 * @since		1.2
 */
public class JDB 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	/**
	 * If true, displays shell output.
	 */
	private static final boolean DEBUG; 
	
	/**
	 * Stores signature of the class of the method.
	 */
	private String classSignature;
	
	/**
	 * Stores signature of the class of the test method.
	 */
	private String classInvocationSignature;
	
	private String invokerSignature;
	private String invokerName;
	
	/**
	 * Stores signature of the test method.
	 */
	private String testMethodSignature;
	
	/**
	 * Line of test method that the method is called.
	 */
	private int invocationLine;
	
	/**
	 * Number of method invocations to be ignored before computing test path.
	 */
	private int skip;
	
	/**
	 * Stores current test path.
	 */
	private List<Integer> testPath;
	
	/**
	 * Stores all computed test paths.
	 */
	private List<List<Integer>> testPaths;
	
	/**
	 * Path of application libraries.
	 */
	private Path libPath;
	
	private boolean endOfMethod;
	private boolean newIteration;
	private boolean exitMethod;
	private boolean isInternalCommand;
	private boolean skipped;
	
	
	//-------------------------------------------------------------------------
	//		Initialization block
	//-------------------------------------------------------------------------
	/**
	 * Enables or disables debug. If activated, displays shell output during 
	 * JDB execution (performance can get worse).
	 */
	static {
		DEBUG = true;
	}

	
	//-------------------------------------------------------------------------
	//		Constructors
	//-------------------------------------------------------------------------
	/**
	 * Computes test path from code debugging. 
	 * 
	 * @param		skip Number of method invocations to be ignored before
	 * computing test path
	 */
	public JDB(int skip)
	{
		this.skip = skip;
		
		testPath = new ArrayList<>();
		testPaths = new ArrayList<>();
	}
	
	/**
	 * Computes test path from code debugging. Use this constructor if there is
	 * no methods to be ignored before computing test path.
	 */
	public JDB()
	{
		this(0);
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Gets invoked methods by tested invoker. It will return all invoked
	 * methods from tested methods
	 * 
	 * @return		Null if tested invoker does not call methods; otherwise, 
	 * returns list of invoked method signatures by tested invoker
	 * 
	 * @implSpec	After call this method, the file containing invoked methods
	 * by tested invoker will be deleted. Therefore, this method can only be 
	 * called once for each {@link #run JDB execution}
	 */
	@SuppressWarnings("unchecked")
	public List<String> getInvokedMethodsByTestedInvoker()
	{
		File f = new File(ExecutionFlow.getAppRootPath(), "imti.ef");
		Map<String, List<String>> invokedMethods = new HashMap<>();
		
		
		if (f.exists()) {
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
				invokedMethods = (Map<String, List<String>>) ois.readObject();
			} catch (IOException | ClassNotFoundException e) {
				invokedMethods = null;
				ConsoleOutput.showError("Invoked methods by tested invoker - " + e.getMessage());
				e.printStackTrace();
			}
		
			f.delete();
		}

		return invokedMethods.containsKey(invokerSignature) ? invokedMethods.get(invokerSignature) : null;
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
	 * Computes test paths from an invoker along with the invoked methods by it.
	 * 
	 * @param		methodInfo Informations about this method
	 * 
	 * @return		Test paths of this method
	 * 
	 * @throws		IOException If JDB cannot be initialized
	 * @throws		Throwable If an error occurs
	 */
	public JDB run(InvokerInfo invokerInfo, InvokerInfo testMethodInfo) throws IOException
	{
		Process process;			// JDB process
		Path classRootPath;			// Root path where the compiled file of invoker class is
		Path srcRootPath;			// Root path where the source file of the invoker is
		Path testClassRootPath;		// Root path where the compiled file of test method class is. It 
									// will be used as JDB root directory
		
		// Gets information about the invoker to be analyzed
		classSignature = invokerInfo.getClassSignature();
		
		// Gets information about the test method of the invoker to be analyzed
		testMethodSignature = testMethodInfo.getInvokerSignature();
		testMethodSignature = testMethodSignature.substring(0, testMethodSignature.indexOf("(")+1);
		classInvocationSignature = testMethodInfo.getClassSignature();
		invocationLine = invokerInfo.getInvocationLine();
		invokerSignature = invokerInfo.getInvokerSignature();
		
		
		
		invokerName = invokerSignature.substring(invokerSignature.lastIndexOf("."), invokerSignature.indexOf("(")+1);
		
		
		
		// Gets paths
		srcRootPath = extractRootPathDirectory(invokerInfo.getSrcPath(), invokerInfo.getPackage());
		classRootPath = extractRootPathDirectory(invokerInfo.getClassPath(), invokerInfo.getPackage());
		testClassRootPath = extractRootPathDirectory(testMethodInfo.getClassPath(), testMethodInfo.getPackage());
		
		// Executes JDB
		process = jdb_start(testClassRootPath, srcRootPath, classRootPath);
		jdb_methodVisitor(process);
		
		return this;
	}
	
	/**
	 * Initializes JDB and prepare it for executing methods within test methods.
	 * 
	 * @param		testClassRootPath Root path where the compiled file of test 
	 * method class is
	 * @param		srcRootPath Root path where the source file of the method is
	 * @param		methodClassRootPath Root path where the compiled file of 
	 * method class is
	 * 
	 * @return		Process running JDB
	 * 
	 * @throws		IOException If the process cannot be created
	 * @throws		IllegalStateException If srcRootPath is null
	 * 
	 * @implNote	It will run JDB from a CMD process
	 */
	private Process jdb_start(Path testClassRootPath, Path srcRootPath, Path methodClassRootPath) throws IOException
	{
		if (srcRootPath == null)
			throw new IllegalStateException("Source file path cannot be empty");
		
		// Gets paths
		findLibs(ExecutionFlow.getAppRootPath());
		
		// Configures JDB, indicating path of libraries, classes and source files
		String methodClassPath = testClassRootPath.relativize(methodClassRootPath).toString();
		String libPath_relative = testClassRootPath.relativize(libPath).toString()+"\\";
		String cp_junitPlatformConsole = libPath_relative+"junit-platform-console-standalone-1.6.2.jar";
		
		String libs = libPath_relative + "aspectjrt-1.9.2.jar" + ";"
			+ libPath_relative + "aspectjtools.jar" + ";"
			
			+ libPath_relative + "junit-4.13.jar" + ";"
			+ libPath_relative + "hamcrest-all-1.3.jar" + ";"
			
			+ cp_junitPlatformConsole;
		
		
		String jdb_classPath = ".;"+libs;
		String jdb_srcPath = testClassRootPath.relativize(srcRootPath).toString();
		
		if (!methodClassPath.isEmpty())
			jdb_classPath += ";"+methodClassPath;
		else
			jdb_classPath += ";..\\classes\\";
		
		String jdb_paths = "-sourcepath "+jdb_srcPath+" "+"-classpath "+jdb_classPath;
		
		// -----{ DEBUG }-----
		if (DEBUG) {
			ConsoleOutput.showDebug("testClassRootPath: "+testClassRootPath);
			ConsoleOutput.showDebug("jdb_paths: "+jdb_paths);
		}
		// -----{ END DEBUG }-----
		
		ProcessBuilder pb = new ProcessBuilder(
			"cmd.exe","/c","jdb "+jdb_paths,
			"org.junit.runner.JUnitCore",classInvocationSignature
		);
		
		pb.directory(testClassRootPath.toFile());
		
		return pb.start();
	}
	
	/**
	 * Starts JDB and computes test paths for a method from debugging.
	 * 
	 * @param		process Profess running JDB
	 * 
	 * @throws		IOException If an error occurs while reading the output 
	 */
	private void jdb_methodVisitor(Process process) throws IOException 
	{
		boolean wasNewIteration = false;
		//int currentSkip = skip;
		JDBOutput out = new JDBOutput(process);
		JDBInput in = new JDBInput(process);
		
		
		// Initializes JDB
		in.init();
		
		// Executes while inside the test method
		while (!endOfMethod) {
			// Checks if output has finished processing
			while (!wasNewIteration && !out.read()) { continue; }
			
			wasNewIteration = false;
			if (endOfMethod) {
				// Checks if there is unsaved test path
				if (testPath.size() > 0) {	// If there is one, save it
					testPaths.add(testPath);
				} 
			}
			// Checks if has exit the method
			else if (exitMethod && !isInternalCommand) {
//				System.out.println("skip: "+currentSkip); currentSkip--; System.out.println("skip: "+currentSkip);
				
				// Checks if has to skip collected test path
//				if (currentSkip == -1) {
					// Saves test path
					testPaths.add(testPath);System.out.println("ADDED: "+testPath);
					
					// Prepare for next test path
					testPath = new ArrayList<>();
					
					// Resets skip
//					currentSkip = skip;
					
					// Checks if method is within a loop
					in.send("cont");
//				} 
//				else {
//					testPath.clear();	// Discards computed test path
//					skipped = true;
//					in.send("step into");
//				}
				
				// Resets exit method
				exitMethod = false;
			} 
			else if (newIteration) {
				wasNewIteration = true;
				
				// Enters the method, ignoring native methods
				in.send("step into");
				out.readAll();
				
				while (isInternalCommand) {
					in.send("next");
					out.readAll();
				}
			} 
			else if (!endOfMethod) {
				in.send("next");
			}
		}

		// Exits JDB
		in.exit(out);
		in.close();
		out.close();
		
		try {
			process.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		process.destroy();
	}
	
	/**
	 * Extracts line number from a debug output.
	 * 
	 * @param		line Debug output
	 * 
	 * @return		Line obtained from this output
	 */
	private int jdb_getLine(String debugOutput)
	{
		int response = -1;
		
		Pattern p = Pattern.compile("line=[0-9]+");
		Matcher m = p.matcher(debugOutput);
		
		if (m.find()) {
			String tmp = m.group();
			p = Pattern.compile("[0-9]+");
			m = p.matcher(tmp);
			if (m.find()) {
				response = Integer.parseInt(m.group());
			}
		}
		
		return response;
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
	 * Finds directory of application libraries and stores it in {@link #libPath}.
	 * 
	 * @param		appRoot Application root path
	 */
	private void findLibs(String appRoot)
	{
		try {
			Files.walkFileTree(Path.of(appRoot), new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
				{
					if (file.endsWith("aspectjrt-1.9.2.jar")) {
						libPath = file.getParent();
						
						return FileVisitResult.TERMINATE;
					}
					
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	//-------------------------------------------------------------------------
	//		Inner classes
	//-------------------------------------------------------------------------
	/**
	 * Responsible for JDB inputs.
	 */
	private class JDBInput
	{
		//---------------------------------------------------------------------
		//		Attributes
		//---------------------------------------------------------------------
		private PrintWriter input;
		
		
		//---------------------------------------------------------------------
		//		Constructor
		//---------------------------------------------------------------------
		/**
		 * JDB input manager. It must be used with {@link JDBOutput}.
		 * 
		 * @param		p JDB process 
		 */
		public JDBInput(Process p)
		{
			this.input = new PrintWriter(new BufferedWriter(new OutputStreamWriter(p.getOutputStream())));
		}
		
		
		//---------------------------------------------------------------------
		//		Methods
		//---------------------------------------------------------------------
		/**
		 * Initializes JDB.
		 */
		public void init()
		{
			List<String> args = new LinkedList<String>();
			args.add("clear");
			args.add("stop at "+classInvocationSignature+":"+invocationLine);
			args.add("run");
	        
			for (String arg : args) {
			    input.println(arg);
			}
			
			input.flush();
		}
		
		/**
		 * Exits from JDB.
		 * 
		 * @throws		IOException 
		 */
		public void exit(JDBOutput out) throws IOException
		{
			input.flush();
			send("clear "+classInvocationSignature+":"+invocationLine);
			out.read();
			send("exit");
			out.read();
			send("exit");
			out.read();
		}
		
		/**
		 * Closes JDB input.
		 */
		public void close()
		{
			input.close();
		}
		
		/**
		 * Sends a command to JDB. After calling this method, you must to call
		 * {@link JDBOutput#read()} for JDB to process the command.
		 * 
		 * @param		command Command that will be sent to JDB
		 * 
		 * @apiNote		If {@link #DEBUG} is activated, it will display the 
		 * command executed on the console
		 */
		private void send(String command)
		{
			// -----{ DEBUG }-----
			if (DEBUG) { ConsoleOutput.showDebug("COMMAND: "+command); }
			// -----{ END DEBUG }-----
			
			input.println(command);
			input.flush();
		}
	}
	
	/**
	 * Responsible for JDB outputs.
	 */
	private class JDBOutput
	{
		//---------------------------------------------------------------------
		//		Attributes
		//---------------------------------------------------------------------
		private BufferedReader output;
		private boolean inMethod;
		private boolean withinConstructor;
		private boolean withinOverloadCall;
		private boolean lastAddWasReturn;
		private int lastLineAdded = -1;
		private String line;
		private String srcLine = "";
		private String lastSrcLine = "";
		//private String invokerSignatureWithoutParameters;
		private int methodDeclarationLine;
		private int lastIfLine;
		private boolean lastWasIf;
		private boolean withinIf;
		
        
        //---------------------------------------------------------------------
    	//		Constructor
    	//---------------------------------------------------------------------
        /**
         * JDB output manager. It must be used with {@link JDBInput} to send
         * commands.
         * 
         * @param		p JDB process
         */
		public JDBOutput(Process p)
		{
			output = new BufferedReader(new InputStreamReader(p.getInputStream()));
			//invokerSignatureWithoutParameters = invokerSignature.substring(0, invokerSignature.indexOf("("));
		}
		
		
		//---------------------------------------------------------------------
    	//		Methods
    	//---------------------------------------------------------------------
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
		public boolean read() throws IOException
		{
			boolean readyToReadInput = false;
			boolean ignore = false;
			final String regex_emptyOutput = "^(>(\\ |\\t)*)*main\\[[0-9]\\](\\ |\\t|>)*$";
			int currentLine;
			
			
			if (output.ready()) {        	
				line = output.readLine();
            	isInternalCommand = false;
            	
            	if (isEmptyLine() || line.matches(regex_emptyOutput)) { 
            		return false;
        		}
            	
            	// -----{ DEBUG }-----
            	if (DEBUG) { ConsoleOutput.showDebug("LINE: "+line); }
        		// -----{ END DEBUG }-----
            	
            	endOfMethod = endOfMethod == true ? endOfMethod : isEndOfTestMethod();
            	isInternalCommand = isInternalMethod();
            	
            	// Checks if JDB has started and is ready to receive debug commands
        		if ( !endOfMethod && line.contains("thread=") &&
    				 (line.contains("Breakpoint hit") || line.contains("Step completed")) ) {
        			readyToReadInput = true;
        			srcLine = output.readLine();
        			currentLine = getSrcLine(srcLine);
        			
        			System.out.println("cl: "+currentLine);
        			System.out.println("methodDeclarationLine: "+methodDeclarationLine);

            		// Checks if it is within a constructor
            		withinConstructor = line.contains(".<init>");

            		// Ignores native calls
            		if (isNativeCall()) {
            			isInternalCommand = true;
            			inMethod = false;
            			ignore = true;
            		}
            		// Ignores overload calls
            		else if (withinOverloadCall) {
            			if (isEmptyMethod()) {
            				withinOverloadCall = false;
            				ignore = false;
            			}
            			else {
            				ignore = true;
            			}
            		}
            		
            		if (lastWasIf && !srcLine.contains("else if")) {
            			withinIf = true;
            			lastWasIf = false;
            		}
            		
            		// Checks if it is in the constructor signature
            		if (srcLine.contains("@executionFlow.runtime.CollectInvokedMethods")) {
            			ignore = true;
            			methodDeclarationLine = currentLine;
            		}
            		else if ( 	(currentLine != -1 && currentLine < methodDeclarationLine) || 
            					(withinIf && srcLine.contains("else if"))	) {		// Avoids catching incorrect test path
            			ignore = true;
            			withinIf = false;
            		}
            		
            		
            		
            		//System.out.println("------ is: "+invokerSignature);
            		System.out.println("----in: "+invokerName);
            		
            		
            		
            		if (methodDeclarationLine == 0 && currentLine > 0 && /*line.contains(invokerSignature)*/line.contains(invokerName))
            			methodDeclarationLine = currentLine;
            		
            		System.out.println("=======");
            		System.out.println("methodDeclarationLine: "+methodDeclarationLine);
            		System.out.println(isInternalCommand);
            		System.out.println(exitMethod);
            		System.out.println(ignore);
            		System.out.println(inMethod);
            		System.out.println(jdb_getLine(line));
            		System.out.println(isWithinMethod(jdb_getLine(line)));
            		System.out.println(isEmptyMethod());
            		System.out.println(lastAddWasReturn);
            		System.out.println("=======");
            		
            		// Checks if it is still within a constructor
            		if (inMethod && withinConstructor && (isEmptyMethod() || line.contains(testMethodSignature))) {
            			withinConstructor = false;
            			exitMethod = true;
            			readyToReadInput = true;
            		}
            		// Checks if last method was skipped
            		else if (skipped) {
            			newIteration = false;
            			skipped = false;
            		}

        			newIteration = isNewIteration();
        			
            		if (!isInternalCommand && !exitMethod && !ignore) {
	        			if (inMethod) {
	        				int lineNumber = getSrcLine(line);
	        				
	        				// Checks if returned from the method
	        				if (line.contains(testMethodSignature)) {
	        					exitMethod = true;
	        					
	        					newIteration = false;
	        					inMethod = false;
	        					lastLineAdded = -1;
	        				} 
	        				// Checks if it is still in the method
	        				else if (withinConstructor || isWithinMethod(lineNumber)) {	
	        					if (!isEmptyMethod() && !lastAddWasReturn) {
	        						testPath.add(lineNumber);System.out.println("add: "+lineNumber);
	        						lastLineAdded = lineNumber;
	        						
	        						lastAddWasReturn = srcLine.contains("return ");
	        						lastWasIf = (srcLine.contains("if ") || srcLine.contains("if(")) && !srcLine.contains(" else"); 
	        					}
	        				}
	            		}
	        			else if (willEnterInMethod()) {
	            			inMethod = true;
	            		}
            		}
            		
            		// Checks if it is an internal call
            		if (!withinOverloadCall)
            			withinOverloadCall = withinConstructor && srcLine.contains("this(");
        		}
        		
	    		if (endOfMethod) {
	    			readyToReadInput = true;
	    		}
	    		
	    		if (srcLine != null && !srcLine.isEmpty()) {
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
	    			
	    			if (srcLine.contains("return ") && /*line.contains(invokerSignature)*/line.contains(invokerName))
	    				exitMethod = true;
	    			
	    			// -----{ DEBUG }-----
	    			if (DEBUG) { ConsoleOutput.showDebug("SRC: "+srcLine); }
	    			// -----{ END DEBUG }-----	    		
	    		}
			} 
			
			return readyToReadInput;
		}
		
		/**
		 * Reads output until there is nothing else.
		 * 
		 * @throws		IOException If it cannot read JDB output
		 */
		public void readAll() throws IOException
		{
			while (!read()) {
				continue;
			}
		}
		
		/**
		 * Closes JDB input.
		 */
		public void close()
		{
			try {
				output.close();
			} catch (IOException e) 
			{}
		}
		
		/**
		 * Gets executed line from source line.
		 *  
		 * @param		src Source line
		 * 
		 * @return		Executed line or -1 if src is empty or null
		 */
		private int getSrcLine(String src) 
		{
			if (src == null || src.isEmpty())
				return -1;
			
			return Integer.valueOf(src.replace(".", "").substring(0, src.indexOf(" ")).trim());
		}
		
		/**
		 * Checks if current line of JDB has reached the end of the test method.
		 * 
		 * @return		If current line of JDB has reached the end of the test method
		 */
		private boolean isEndOfTestMethod()
		{
			return line.contains("The application exited");
		}
		
		/**
		 * Checks if current line of JDB is an internal method.
		 * 
		 * @return		If current line of JDB is an internal method
		 */
		private boolean isInternalMethod()
		{
			return 	/*!line.contains(classSignature)*/!line.contains(invokerName) && 
					!line.contains(classInvocationSignature);
		}
		
		/**
		 * Checks if current line of JDB is an empty line.
		 * 
		 * @return		If current line of JDB is an empty line
		 */
		private boolean isEmptyLine()
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
		 * @return		If current line of JDB is an empty line
		 */
		private boolean isNewIteration()
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
		 * @return		If next line of JDB will be within a method
		 */
		private boolean willEnterInMethod()
		{
			return 	newIteration || 
					( !inMethod && 
					  line.contains("Step completed") && 
					  line.contains(classInvocationSignature) ); 
		}
		
		/**
		 * Checks if current line of JDB is within a method.
		 * 
		 * @return		If current line of JDB is within a method
		 */
		private boolean isWithinMethod(int lineNumber)
		{
			return	!exitMethod && lineNumber != lastLineAdded;
		}
		
		/**
		 * Checks if current line of JDB is an empty method.
		 * 
		 * @return		If current line of JDB is an empty method
		 */
		private boolean isEmptyMethod()
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
		 * @return		If current line of JDB is a native call.
		 */
		private boolean isNativeCall()
		{
			return	line.contains("line=1 ") || 
					line.contains("jdk.") || 
					line.contains("aspectj.") || 
					line.contains("executionFlow.runtime") || 
					srcLine.contains("package ") || 
					(
						/*!line.contains(invokerSignatureWithoutParameters)*/!line.contains(invokerName) && 
						!line.contains(testMethodSignature)
					);
		}
	}
}
