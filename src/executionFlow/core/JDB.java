package executionFlow.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
 * Computes test path from code debug.
 */
public class JDB 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private String classPathRoot;
	private String methodClassDir;
	private String methodClassSignature;
	private String classInvocationSignature;
	private static String appPath;
	private String srcPath;
	private int lastLineTestMethod;
	private int methodInvocationLine;
	private int skip;
	private List<Integer> testPath;
	private List<List<Integer>> testPaths;
	private static Path libPath;
	private boolean endOfMethod;
	private boolean newIteration;
	private boolean exitMethod;
	private boolean isInternalCommand;
	private boolean overloadedMethod;
	private final boolean DEBUG; 
	private boolean skipped;
	
	
	//-------------------------------------------------------------------------
	//		Initialization block
	//-------------------------------------------------------------------------
	/**
	 * Enables or disables debug. If activated, shows shell output during JDB 
	 * execution.
	 */
	{
		DEBUG = true;
	}
	
	/**
	 * Computes and stores application root path, based on class 
	 * {@link ExecutionFlow} location.
	 */
	static {
		try {
			appPath = new File(ExecutionFlow.class.getProtectionDomain().getCodeSource().getLocation()
				    .toURI()).getPath();
			appPath = new File(appPath+"../").getParent();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		findLibs();
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
		methodClassSignature = methodInfo.getClassSignature();
		String methodSignature = methodInfo.getClassSignature()+"."+methodInfo.getMethodName()+"()";
		classInvocationSignature = extractClassSignature(methodInfo.getTestMethodSignature());
		methodInvocationLine = methodInfo.getInvocationLine();
		
		// Gets class path root (using test method class directory)
		classPathRoot = extractRootPathDirectory(methodInfo.getTestClassPath(), methodInfo.getTestClassPackage());
		srcPath = extractRootPathDirectory(methodInfo.getSrcPath(), methodInfo.getPackage());
		methodClassDir = extractRootPathDirectory(methodInfo.getClassPath(), methodInfo.getPackage());
		
		jdb_methodVisitor(methodSignature, methodInfo.getMethodName());
		
		return testPaths;
	}
	
	/**
	 * Initializes JDB and prepare it for executing methods within test methods.
	 * 
	 * @return Process running JDB
	 * @throws IOException If the process cannot be created
	 */
	private synchronized Process jdb_start() throws IOException
	{
		if (srcPath.isEmpty())
			throw new IllegalStateException("Source file path is empty");
				
		String methodClassPath = Paths.get(classPathRoot).relativize(Path.of(methodClassDir)).toString();
		String libPath_relative = Paths.get(classPathRoot).relativize(libPath).toString()+"\\";
		String lib_aspectj = libPath_relative+"aspectjrt-1.9.2.jar";
		String lib_junit = libPath_relative+"junit-4.13.jar";
		String lib_hamcrest = libPath_relative+"hamcrest-all-1.3.jar";
		String libs = lib_aspectj+";"+lib_junit+";"+lib_hamcrest;
		String jdb_classPath = "-classpath .;"+libs;
		String jdb_srcPath = "-sourcepath "+Paths.get(classPathRoot).relativize(Paths.get(srcPath));
		
		if (!methodClassPath.isEmpty()) {
			jdb_classPath += ";"+methodClassPath;
		}
		
		String jdb_paths = jdb_srcPath+" "+jdb_classPath;
		
		System.out.println("CPR: "+classPathRoot);
		System.out.println("jdb_paths: "+jdb_paths);
		System.out.println();
		
		ProcessBuilder pb = new ProcessBuilder(
			"cmd.exe","/c","jdb "+jdb_paths,
			"org.junit.runner.JUnitCore",classInvocationSignature
		);
		pb.directory(new File(classPathRoot));
		
		return pb.start();
	}
	
	/**
	 * Starts JDB and computes test paths for a method via debugging.
	 * 
	 * @param methodSignature Signature of the method
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private synchronized void jdb_methodVisitor(String methodSignature, String methodName) throws IOException, InterruptedException 
	{
		boolean wasNewIteration = false;
		Process process = jdb_start();
		JDBOutput out = new JDBOutput(process, methodSignature, methodName);
		JDBInput in = new JDBInput(process);
		
		in.init();
		
		System.out.println("Computing test path...");
		
		// Executes while inside the method
		while (!endOfMethod) {
			// Checks if output has finished processing
			while (!wasNewIteration && !out.read()) { continue; }  
			
			if (endOfMethod) { break; }
			wasNewIteration = false;
			//System.out.println("CURRENT SKIP: "+skip);
			// Check if is entering a method
			if (exitMethod) {
				skip--; //System.out.println("CURRENT SKIP: "+skip);

				// Checks if has to skip collected test path
				if (skip == -1) {
					// Saves test path
					testPaths.add(testPath);

					// Prepare for next test path
					testPath = new ArrayList<>();
					
					// Checks if method is in a loop
					in.send("cont");
				} else {
					testPath.clear();	// Discards computed test path
					skipped = true;
					in.send("step into");
				}
				
				// Check output
				exitMethod = false;
			} else if (newIteration) {
				wasNewIteration = true;
				// Enters the method, ignoring aspectJ
				in.send("step into");
				while (!out.read()) { continue; }
				
				while (isInternalCommand) {
					in.send("next");
					while (!out.read()) { continue; }
				}
			} else if (!endOfMethod) {
				in.send("next");
			}
		}
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
	 * Extracts class signature from a method signature.
	 * 
	 * @param methodSignature Signature of the method
	 * @return Class signature of this method
	 */
	private String extractClassSignature(String methodSignature)
	{
		String[] terms = methodSignature.split("\\.");
		StringBuilder sb = new StringBuilder();
		
		for (int i=0; i<terms.length-1;i++) {
			sb.append(terms[i]);
			sb.append(".");
		}
		
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length()-1);	// Removes last dot
		}
		
		return sb.toString();
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
	 * @param binPath Location of binary files (.class)
	 */
	private static void findLibs()
	{
		try {
			Files.walkFileTree(Path.of(appPath), new SimpleFileVisitor<Path>() {
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
	class JDBInput
	{
		//---------------------------------------------------------------------
		//		Attributes
		//---------------------------------------------------------------------
		OutputStream os;
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
			this.os = p.getOutputStream();
			this.input = new PrintWriter(new BufferedWriter(new OutputStreamWriter(os)));
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
			//out.readAll();
			input.flush();
			send("clear "+classInvocationSignature+":"+methodInvocationLine);
			out.readAll();
			send("exit");
			out.readAll();
			send("exit");
			out.readAll();
		}
		
		/**
		 * Closes JDB input.
		 */
		public void close()
		{
			try {
				os.close();
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
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
			
			//input.flush();
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
		InputStream is;
    	final String regex_overloadedMethod;
    	String methodSignature;
    	String methodName;
    	boolean inMethod = false;
    	boolean processInput = false;
    	int lastLineAdded = -1;
    	boolean started = false;
    	BufferedReader br;
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
			is = p.getInputStream();
			br = new BufferedReader(new InputStreamReader(is));
			regex_overloadedMethod = "^.*(\\ |\\t|=|\\.)"+methodName+"\\(.*\\)(\\ |\\t)*;$";
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
			
			if (br.ready()) {
            	line = br.readLine();
            	
            	if (isEmptyLine() || line.matches("^(>(\\ |\\t)*)*main\\[[0-9]\\](\\ |\\t)*$")) { 
            		return false; 
        		}
            	
            	// -----{ DEBUG }-----
            	if (DEBUG) { System.out.println("LINE: "+line); }
        		// -----{ END DEBUG }-----
        		
            	endOfMethod = isEndOfTestMethod();
            	isInternalCommand = isInternalMethod();
            	
            	// Checks if JDB has started and is ready to receive debug commands
        		if ( !endOfMethod && 
    				 (line.contains("Breakpoint hit") || line.contains("Step completed")) ) {
        			response = true;
            		srcLine = br.readLine();
        			
            		// Checks if last method was skipped
            		if (skipped) {
            			newIteration = false;
            			inMethod = true;
            			skipped = false;
            		}
            		else {
            			newIteration = isNewIteration();
            		}
            		
        			
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
        			else if (willEnterInMethod()) {
            			inMethod = true;
            		} 
        			else if (inMethod) { 	
        				int lineNumber = jdb_getLine(line);
        				
        				// Checks if returned from the method
        				if ( line.contains(classInvocationSignature) && 
    						 line.contains("line="+methodInvocationLine) ) {
        					exitMethod = true;
        					newIteration = false;
        					inMethod = false;
        					lastLineAdded = -1;
        				} 
        				else if (isWithinMethod(lineNumber)) {	// Checks if it is still in the method
        					if (!isEmptyMethod()) {
        						testPath.add(lineNumber);
        						lastLineAdded = lineNumber;
        					}
        				}
            		}
        		}
	
//        		System.out.println("OUT");
//        		System.out.println(exitMethod);
//        		System.out.println(skipped);
//        		System.out.println(newIteration);
//        		System.out.println(isCallToOverloadedMethod());
//        		System.out.println(willEnterInMethod());
//        		System.out.println(inMethod);
        		
//        		if (isInternalCommand) {
//    				inMethod = false;
//    			}
        		
//        		System.out.println(inMethod);
//        		System.out.println("END OUT");
        		
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
			while (br.ready()) {
				read();
			}
		}
		
		/**
		 * Closes JDB input.
		 */
		public void close()
		{
			try {
				is.close();
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
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
