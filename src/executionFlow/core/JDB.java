package executionFlow.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import executionFlow.ExecutionFlow;
import executionFlow.info.ClassMethodInfo;


/**
 * Computes test path from code debugging.
 */
public class JDB 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	/**
	 * Stores signature of the class of the method.
	 */
	private String methodClassSignature;
	
	/**
	 * Stores signature of the class of the test method.
	 */
	private String classInvocationSignature;
	
	/**
	 * Stores signature of the test method.
	 */
	private String testMethodSignature;
	
	/**
	 * Last line of test method (line of last curly bracket).
	 */
	private int lastLineTestMethod;
	
	/**
	 * Line of test method that the method is called.
	 */
	private int methodInvocationLine;
	
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
	private boolean overloadedMethod;
	private boolean skipped;
	private final boolean DEBUG; 
	
	
	//-------------------------------------------------------------------------
	//		Initialization block
	//-------------------------------------------------------------------------
	/**
	 * Enables or disables debug. If activated, displays shell output during JDB 
	 * execution.
	 */
	{
		DEBUG = false;
	}

	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	/**
	 * Computes test path from code debugging. 
	 * 
	 * @param lastLineTestMethod Test method end line
	 * @param skip Number of method invocations to be ignored before computing 
	 * test path
	 */
	public JDB(int lastLineMethod, int skip)
	{
		this.lastLineTestMethod = lastLineMethod;
		this.skip = skip;
		
		testPath = new ArrayList<>();
		testPaths = new ArrayList<>();
	}
	
	/**
	 * Computes test path from code debugging. Use this constructor if there is 
	 * no methods to be ignored before computing test path.
	 * 
	 * @param lastLineTestMethod Test method end line
	 */
	public JDB(int lastLineMethod)
	{
		this(lastLineMethod, 0);
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Computes test paths from a method.
	 * 
	 * @param methodInfo Informations about this method
	 * @return Test paths of this method
	 * @throws Throwable If an error occurs
	 */
	public synchronized List<List<Integer>> getTestPaths(ClassMethodInfo methodInfo) throws Throwable
	{ 
		String methodClassRootPath;	// Root path where the compiled file of method class is
		String srcRootPath;			// Root path where the source file of the method is
		String testClassRootPath;	// Root path where the compiled file of test method class is. It 
									// will be used as JDB root directory
		
		// Gets information about the method to be analyzed
		methodClassSignature = methodInfo.getClassSignature();
		
		// Gets information about the test method of the method to be analyzed
		testMethodSignature = methodInfo.getTestMethodSignature();
		classInvocationSignature = methodInfo.getTestClassSignature();
		methodInvocationLine = methodInfo.getInvocationLine();
		
		// Gets paths
		srcRootPath = extractRootPathDirectory(methodInfo.getSrcPath(), methodInfo.getPackage());
		methodClassRootPath = extractRootPathDirectory(methodInfo.getClassPath(), methodInfo.getPackage());
		testClassRootPath = extractRootPathDirectory(methodInfo.getTestClassPath(), methodInfo.getTestClassPackage());
				
		Process process = jdb_start(testClassRootPath, srcRootPath, methodClassRootPath);
		
		String methodSignature = methodInfo.getClassSignature()+"."+methodInfo.getMethodName()+"()";
		jdb_methodVisitor(process, methodSignature, methodInfo.getMethodName());
		
		return testPaths;
	}
	
	/**
	 * Initializes JDB and prepare it for executing methods within test methods.
	 * 
	 * @param testClassRootPath Root path where the compiled file of test method class is
	 * @param srcRootPath Root path where the source file of the method is
	 * @param methodClassRootPath Root path where the compiled file of method class is
	 * @return Process running JDB
	 * @throws IOException If the process cannot be created
	 */
	private synchronized Process jdb_start(String testClassRootPath, String srcRootPath, String methodClassRootPath) throws IOException
	{
		if (srcRootPath.isEmpty())
			throw new IllegalStateException("Source file path cannot be empty");
		
		// Gets paths
		findLibs(getAppRootPath());
		
		// Configures JDB, indicating path of libraries, classes and source files
		String methodClassPath = Paths.get(testClassRootPath).relativize(Path.of(methodClassRootPath)).toString();
		String libPath_relative = Paths.get(testClassRootPath).relativize(libPath).toString()+"\\";
		String lib_aspectj = libPath_relative+"aspectjrt-1.9.2.jar";
		String lib_junit = libPath_relative+"junit-4.13.jar";
		String lib_hamcrest = libPath_relative+"hamcrest-all-1.3.jar";
		String libs = lib_aspectj+";"+lib_junit+";"+lib_hamcrest;
		String jdb_classPath = "-classpath .;"+libs;
		String jdb_srcPath = "-sourcepath "+Paths.get(testClassRootPath).relativize(Paths.get(srcRootPath));
		
		if (!methodClassPath.isEmpty()) {
			jdb_classPath += ";"+methodClassPath;
		} 
		else {
			jdb_classPath += ";..\\classes\\";
		}
		
		String jdb_paths = jdb_srcPath+" "+jdb_classPath;
		
		// -----{ DEBUG }-----
		if (DEBUG) {
			System.out.println("testClassRootPath: "+testClassRootPath);
			System.out.println("jdb_paths: "+jdb_paths);
		}
		// -----{ END DEBUG }-----
		
		// Runs JDB from CMD
		ProcessBuilder pb = new ProcessBuilder(
			"cmd.exe","/c","jdb "+jdb_paths,
			"org.junit.runner.JUnitCore",classInvocationSignature
		);
		pb.directory(new File(testClassRootPath));
		
		return pb.start();
	}
	
	/**
	 * Starts JDB and computes test paths for a method via debugging.
	 * 
	 * @param methodSignature Signature of the method
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private synchronized void jdb_methodVisitor(Process process, String methodSignature, String methodName) throws IOException, InterruptedException 
	{
		boolean wasNewIteration = false;
		int currentSkip = skip;
		JDBOutput out = new JDBOutput(process, methodSignature, methodName);
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
			else if (exitMethod) {
				currentSkip--; 

				// Checks if has to skip collected test path
				if (currentSkip == -1) {
					// Saves test path
					testPaths.add(testPath);

					// Prepare for next test path
					testPath = new ArrayList<>();
					
					// Resets skip
					currentSkip = skip;
					
					// Checks if method is within a loop
					in.send("cont");
				} else {
					testPath.clear();	// Discards computed test path
					skipped = true;
					in.send("step into");
				}
				
				// Resets exit method
				exitMethod = false;
			} 
			else if (newIteration) {
				wasNewIteration = true;
				
				// Enters the method, ignoring aspectJ
				in.send("step into");
				while (!out.read()) { continue; }
				
				while (isInternalCommand) {
					in.send("next");
					while (!out.read()) { continue; }
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
		process.waitFor();
		process.destroy();
	}
	
	/**
	 * Extracts line number from a debug output.
	 * 
	 * @param line Debug output
	 * @return Line obtained from this output
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
	 * Class path: <code>C:/myProgram/src/name1/name2/name3</code><br />
	 * Class package: <code>name1.name2.name3</code><br />
	 * <u>Return:</u> <code>C:/myProgram/src/</code><br /><br />
	 * 
	 * @param classPath Class path
	 * @param classPackage Class package
	 * @return Root path directory
	 * @throws IllegalStateException If source file path is null
	 */
	private String extractRootPathDirectory(String classPath, String classPackage) throws IllegalStateException
	{
		if (classPath == null) 
			throw new IllegalStateException("Source file path cannot be null");
		
		int packageFolders = 0;
		
		if (!classPackage.isEmpty()) {
			packageFolders = classPackage.split("\\.").length;
		}
		
		Path file = new File(classPath).toPath();
		
		file = file.getParent();
		
		for (int i=0; i<packageFolders; i++) {
			file = file.getParent();
		}
		
		return file.toString();
	}
	
	/**
	 * Finds directory of application libraries and stores it in {@link #libPath}.
	 * 
	 * @param appRoot Application root path
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
	
	/**
	 * Computes and stores application root path, based on class 
	 * {@link ExecutionFlow} location.
	 * 
	 * @return Application root path
	 */
	private String getAppRootPath()
	{
		String response = null;
		
		try {
			response = new File(ExecutionFlow.class.getProtectionDomain().getCodeSource().getLocation()
				    .toURI()).getPath();
			response = new File(response+"../").getParent();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		return response;
	}
	
	
	//-------------------------------------------------------------------------
	//		Inner classes
	//-------------------------------------------------------------------------
	/**
	 * Responsible for JDB inputs.
	 */
	class JDBInput
	{
		//---------------------------------------------------------------------
		//		Attributes
		//---------------------------------------------------------------------
		PrintWriter input;
		
		
		//---------------------------------------------------------------------
		//		Constructor
		//---------------------------------------------------------------------
		/**
		 * JDB input manager. It must be used with {@link JDBOutput}.
		 * 
		 * @param p JDB process 
		 */
		JDBInput(Process p)
		{
			this.input = new PrintWriter(new BufferedWriter(new OutputStreamWriter(p.getOutputStream())));
		}
		
		
		//---------------------------------------------------------------------
		//		Methods
		//---------------------------------------------------------------------
		/**
		 * Initializes JDB.
		 */
		public void init() throws InterruptedException
		{
			List<String> args = new LinkedList<String>();
			args.add("clear");
			args.add("stop at "+classInvocationSignature+":"+methodInvocationLine);
			args.add("stop at "+classInvocationSignature+":"+lastLineTestMethod);
	        args.add("run");
	        
			for (String arg : args) {
			    input.println(arg);
			}
			
			input.flush();
		}
		
		/**
		 * Exits from JDB.
		 * @throws IOException 
		 */
		public void exit(JDBOutput out) throws IOException
		{
			input.flush();
			send("clear "+classInvocationSignature+":"+methodInvocationLine);
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
		 * @param command Command that will be sent to JDB
		 * @apiNote If DEBUG is activated, it will display the command executed
		 * on the console
		 */
		private void send(String command)
		{
			// -----{ DEBUG }-----
			if (DEBUG) { System.out.println("COMMAND: "+command); }
			// -----{ END DEBUG }-----
			
			input.println(command);
			input.flush();
		}
	}
	
	/**
	 * Responsible for JDB outputs.
	 */
	class JDBOutput
	{
		//---------------------------------------------------------------------
		//		Attributes
		//---------------------------------------------------------------------
		BufferedReader output;
    	String methodSignature;
    	String methodName;
    	boolean inMethod = false;
    	boolean checkInput = false;
    	int lastLineAdded = -1;
    	boolean started = false;
        String line;
        String srcLine = null;
		
        
        //---------------------------------------------------------------------
    	//		Constructor
    	//---------------------------------------------------------------------
        /**
         * JDB output manager. It must be used with {@link JDBInput} to send
         * commands.
         * 
         * @param p JDB process
         * @param methodSignature Signature of the method to be debugged
         * @param methodName Name of the method to be debugged
         */
		JDBOutput(Process p, String methodSignature, String methodName)
		{
			this.methodSignature = methodSignature;
			this.methodName = methodName;
			output = new BufferedReader(new InputStreamReader(p.getInputStream()));
		}
		
		
		//---------------------------------------------------------------------
    	//		Methods
    	//---------------------------------------------------------------------
		/**
		 * Reads JDB output and returns true if JDB is ready to receive commands.
		 * 
		 * @return If JDB is ready to receive commands.
		 * @throws IOException If it cannot read JDB output
		 * @apiNote If DEBUG is activated, it will display JDB output on the 
		 * console
		 */
		public boolean read() throws IOException
		{
			boolean response = false;
			
			if (output.ready()) {
            	line = output.readLine();
            	
            	if (isEmptyLine() || line.matches("^(>(\\ |\\t)*)*main\\[[0-9]\\](\\ |\\t)*$")) { 
            		return false; 
        		}
            	
            	// -----{ DEBUG }-----
            	if (DEBUG) { System.out.println("LINE: "+line); }
        		// -----{ END DEBUG }-----
        		
            	endOfMethod = endOfMethod == true ? endOfMethod : isEndOfTestMethod();
            	isInternalCommand = isInternalMethod();
            	
            	// Checks if JDB has started and is ready to receive debug commands
        		if ( !endOfMethod && line.contains("thread=") &&
    				 (line.contains("Breakpoint hit") || line.contains("Step completed")) ) {
        			response = true;
            		srcLine = output.readLine();
        			
            		// Checks if last method was skipped
            		if (skipped) {
            			newIteration = false;
            			inMethod = true;
            			skipped = false;
            		}
            		else {
            			newIteration = isNewIteration();
            		}
            		
        			// Checks if it is a call to an overloaded method
        			if (isCallToOverloadedMethod()) {
        				if (overloadedMethod) {
        					exitMethod = true;
        				} 
        				else {
        					testPath.clear();
        					overloadedMethod = true;
        					newIteration = true;
        				}
                	} 
        			else if (inMethod) { 	
        				int lineNumber = jdb_getLine(line);
        				
        				// Checks if returned from the method
        				if (line.contains(testMethodSignature)) {
        					exitMethod = true;
        					newIteration = false;
        					inMethod = false;
        					lastLineAdded = -1;
        				} 
        				// Checks if it is still in the method
        				else if (isWithinMethod(lineNumber)) {	
        					if (!isEmptyMethod()) {
        						testPath.add(lineNumber);
        						lastLineAdded = lineNumber;
        					}
        				}
            		}
        			else if (willEnterInMethod()) {
            			inMethod = true;
            		}
        		}
        		
	    		if (endOfMethod) {
	    			response = true;
	    		}
	    		
	    		// -----{ DEBUG }-----
	    		if (DEBUG && srcLine != null) { System.out.println("SRC: "+srcLine); }
	    		// -----{ END DEBUG }-----
			} 
			
			return response;
		}
		
		/**
		 * Reads output until there is nothing else.
		 * @throws IOException If it cannot read JDB output
		 */
		public void readAll() throws IOException
		{
			while (output.ready()) {
				read();
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
		 * Checks if current line of JDB has reached the end of the test method.
		 * 
		 * @return if current line of JDB has reached the end of the test method
		 */
		private boolean isEndOfTestMethod()
		{
			return 
					(line.contains("Breakpoint hit") && line.contains("line="+lastLineTestMethod)) || 
					line.contains("The application exited");
		}
		
		/**
		 * Checks if current line of JDB is an internal method.
		 * 
		 * @return If current line of JDB is an internal method
		 */
		private boolean isInternalMethod()
		{
			return 	!line.contains(methodClassSignature) && 
					!line.contains(classInvocationSignature);
		}
		
		/**
		 * Checks if current line of JDB is an empty line.
		 * 
		 * @return If current line of JDB is an empty line
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
		 * @return If current line of JDB is an empty line
		 */
		private boolean isNewIteration()
		{
			return (
				!inMethod && 
				(
					line.contains("Breakpoint hit") || 
					( line.contains("line="+methodInvocationLine) && 
					  line.contains(classInvocationSignature) )
				)
			);
		}
		
		/**
		 * Checks if current line of JDB is a call to an overloaded method.
		 * 
		 * @return If current line of JDB is a call to an overloaded method
		 */
		private boolean isCallToOverloadedMethod()
		{
			String regex_overloadedMethod = "^.*(\\ |\\t|=|\\.)"+methodName+"\\(.*\\)(\\ |\\t)*;$";
			
			return 	srcLine != null && 
					srcLine.matches(regex_overloadedMethod) && 
					!line.contains(classInvocationSignature);
		}
		
		/**
		 * Checks if next line of JDB will be within a method.
		 * 
		 * @return If next line of JDB will be within a method
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
		 * @return If current line of JDB is within a method
		 */
		private boolean isWithinMethod(int lineNumber)
		{
			return	!exitMethod && 
					line.contains(methodSignature) &&
					lineNumber != lastLineAdded;
		}
		
		/**
		 * Checks if current line of JDB is an empty method.
		 * 
		 * @return If current line of JDB is an empty method
		 */
		private boolean isEmptyMethod()
		{
			final String regex_onlyCurlyBracket = "([0-9]+)(\\ |\\t)+\\}((\\ |\\t)+)?($)";
			final String regex_emptyMethod = "^([0-9]*)(\\t|\\ )*((([a-z]+\\ ){2,}"
					+ ".+\\(.*\\)(\\ |\\t)*\\{(\\ |\\t)*\\})|(\\{(\\t|\\ )*\\})|(\\}))$";
			
			return	srcLine.matches(regex_onlyCurlyBracket) ||
					srcLine.matches(regex_emptyMethod);
		}
	}
}
