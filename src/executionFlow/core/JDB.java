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
	private PrintWriter input;
	private boolean outputFinished;
	private boolean endOfMethod;
	private boolean newIteration;
	private boolean exitMethod;
	private boolean inputFinished;
	private boolean isInternalCommand;
	private boolean overloadedMethod;
	private final boolean DEBUG; 
	
	
	//-------------------------------------------------------------------------
	//		Initialization block
	//-------------------------------------------------------------------------
	/**
	 * Enables or disables debug. If activated, shows shell output during JDB execution.
	 */
	{
		DEBUG = true;
	}
	
	/**
	 * Computes and stores application root path, based on class {@link ExecutionFlow} location.
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
	 * Computes test path from code debugging. Use this constructor if this class
	 * will be used within the context of aspects.
	 * 
	 * @param lastLineTestMethod Test method end line
	 */
	public JDB(int lastLineMethod, int skip)
	{
		this.lastLineTestMethod = lastLineMethod;
		this.skip = skip;
		
		testPath = new ArrayList<>();
		testPaths = new ArrayList<>();
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
		classPathRoot = extractPathDirectory(methodInfo.getTestClassPath(), methodInfo.getTestClassPackage());
		srcPath = extractPathDirectory(methodInfo.getSrcPath(), methodInfo.getPackage());
		methodClassDir = extractPathDirectory(methodInfo.getClassPath(), methodInfo.getPackage());
		
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
		
		ProcessBuilder pb = new ProcessBuilder("cmd.exe","/c","jdb "+jdb_paths,"org.junit.runner.JUnitCore",classInvocationSignature);
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
		Process process = jdb_start();
		JDBOutput out = new JDBOutput(process, methodSignature, methodName);
		JDBInput in = new JDBInput(process);
		in.init();
		
		// Executes while inside the method
		while (!endOfMethod) {
			while (!out.read()) { continue; }  
			
			if (newIteration) {
					// Enters the method, ignoring aspectJ
					in.send("step into");
					out.read();
					while (isInternalCommand) {
						in.send("next");
						out.read();
					}
			} else if (exitMethod) {
				skip--;
				
				// Checks if has to skip collected test path
				if (skip < 0) {
					// Saves test path
					testPaths.add(testPath);
					
					// Prepare for next test path
					testPath = new ArrayList<>();
					
					// Checks if method is in a loop
					in.send("cont");
					out.read();
				} else {
					testPath.clear();	// Discards computed test path
					newIteration = true;
					in.send("step into");
					out.read();
				}
				
				// Check output
				exitMethod = false;
			} else if (!endOfMethod) {
				in.send("next");
				out.read();
			}
		}
		
		in.exit();
		in.close();
		out.close();
		process.waitFor();
		process.destroyForcibly();
		
		/*
        // Output
		Thread t = jdb_output(process, methodSignature, methodName);

        // Input
		OutputStream os = process.getOutputStream();
		jdb_input(process, os);
		
		// Exits JDB
		jdb_end();
		input.close();
		os.close();
		process.waitFor();
		process.destroyForcibly();
		t.join();
		*/
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
	
	private String extractPathDirectory(String classPath, String classPackage) throws Exception
	{
		if (classPath == null) 
			throw new Exception("Source file path cannot be null");
		
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
		Process p;
		OutputStream os;
		PrintWriter input;
		
		
		//---------------------------------------------------------------------
		//		Constructor
		//---------------------------------------------------------------------
		JDBInput(Process p)
		{
			this.p = p;
			this.input = new PrintWriter(new BufferedWriter(new OutputStreamWriter(os)));
			this.os = p.getOutputStream();
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
		 */
		public void exit()
		{
			input.flush();
			input.println("clear "+classInvocationSignature+":"+methodInvocationLine);
			input.flush();
			input.println("exit");
			input.flush();
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
		 * Sends a command to JDB.
		 * 
		 * @param command Command that will be sent to JDB
		 */
		private void send(String command)
		{
			// -----{ DEBUG }-----
			if (DEBUG) { System.out.println("COMMAND: "+command); }
			// -----{ END DEBUG }-----
			
			input.flush();
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
		Process p;
		InputStream is;
		final String regex_emptyMethod;
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
		JDBOutput(Process p, String methodSignature, String methodName)
		{
			this.p = p;
			this.methodSignature = methodSignature;
			this.methodName = methodName;
			is = p.getInputStream();
			regex_overloadedMethod = "^.*(\\ |\\t|=|\\.)"+methodName+"\\(.*\\)(\\ |\\t)*;$";
			regex_emptyMethod = "^([0-9]*)(\\t|\\ )*((([a-z]+\\ ){2,}.+\\(.*\\)(\\ |\\t)*\\{(\\ |\\t)*\\})|(\\{(\\t|\\ )*\\})|(\\}))$";
			br = new BufferedReader(new InputStreamReader(is));
		}
		
		
		//---------------------------------------------------------------------
    	//		Methods
    	//---------------------------------------------------------------------
		/**
		 * Reads JDB output and returns true if JDB is ready to receive commands.
		 * 
		 * @return If JDB is ready to receive commands.
		 * @throws IOException If it cannot read JDB output
		 */
		public boolean read() throws IOException
		{
			boolean response = false;
			
			if (!endOfMethod && br.ready()) {
				outputFinished = false;
            	line = br.readLine();
            	
            	if (line.equals("\n") || line.equals("") || line.equals(" ")) return false;
            	
            	// -----{ DEBUG }-----
            	if (DEBUG) { System.out.println(line); }
        		// -----{ END DEBUG }-----
        		
            	endOfMethod = line.contains("Breakpoint hit") && line.contains("line="+lastLineTestMethod);
            	isInternalCommand = !line.contains(methodClassSignature) && !line.contains(classInvocationSignature);
            	
            	// Checks if JDB has started and is ready to receive debug commands
        		if (!endOfMethod && (line.contains("Breakpoint hit") || line.contains("Step completed"))) {
        			response = true;
            		srcLine = br.readLine();
        			
        			newIteration = 
    					(!inMethod && 
        					(
    							line.contains("Breakpoint hit") || 
    							(line.contains("line="+methodInvocationLine) && line.contains(classInvocationSignature))
							)
    					);

        			if (isInternalCommand) {
        				inMethod = false;
        			}

        			// Checks if it is a call to an overloaded method
        			else if (srcLine != null && srcLine.matches(regex_overloadedMethod) && !line.contains(classInvocationSignature)) {
        				if (overloadedMethod) {
        					exitMethod = true;
        				} else {
        					//System.out.println("OVERLOADED METHOD");
        					testPath.clear();
        					overloadedMethod = true;
        					newIteration = true;
        				}
                	} else if (newIteration || (!inMethod && line.contains("Step completed") && line.contains(classInvocationSignature))) {
            			inMethod = true;
            		} else if (inMethod) { 	
        				int lineNumber = jdb_getLine(line);
        				
        				// Checks if returned from the method
        				if (line.contains(classInvocationSignature) && line.contains("line="+methodInvocationLine)) {
        					exitMethod = true;
        					newIteration = false;
        					inMethod = false;
        					endOfMethod = !p.isAlive();
        					lastLineAdded = -1;
        				} else if (!exitMethod && line.contains(methodSignature) && lineNumber != lastLineAdded) {	// Checks if it is still in the method
        					if (!srcLine.matches("([0-9]+)(\\ |\\t)+\\}((\\ |\\t)+)?($)") &&
    							!srcLine.matches(regex_emptyMethod)) {
        						testPath.add(lineNumber);
        						lastLineAdded = lineNumber;
        					}
        				}
            		}
        		}
    		
	    		endOfMethod = line.contains("The application exited");
	
	    		if (endOfMethod) {
	    			response = true;
	    		}
	    		
	    		// -----{ DEBUG }-----
	    		if (DEBUG && srcLine != null) { System.out.println(srcLine); }
	    		// -----{ END DEBUG }-----
			} 
			
			return response;
		}
		
		public void close()
		{
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
