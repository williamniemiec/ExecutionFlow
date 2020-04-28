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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import executionFlow.info.ClassMethodInfo;


/**
 * Computes test path from code debugging.
 */
public class JDB 
{
	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
	private String classPathRoot;
	private boolean readyToDebug = false;
	private boolean endOfMethod = false;
	private List<Integer> testPath = new ArrayList<>();
	private List<List<Integer>> testPaths = new ArrayList<>();
	private PrintWriter pw;
	private boolean newIteration;
	private boolean exitMethod;
	private boolean inputReady = false;
	private int lastLineTestMethod;
	private String classInvocationSignature;
	private int methodInvocationLine;
	private final boolean DEBUG = false; 
	
	
	//-----------------------------------------------------------------------
	//		Constructor
	//-----------------------------------------------------------------------
	/**
	 * Computes test path from code debugging.
	 * 
	 * @param appPath Path of this application
	 * @param classPath Path of test method class
	 * @param lastLineTestMethod Test method end line
	 */
	public JDB(String appPath, String testMethodclassPath, int lastLineMethod)
	{
		this.lastLineTestMethod = lastLineMethod;
		this.classPathRoot = extractClassPathDirectory(appPath, testMethodclassPath);
	}
	
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	/**
	 * Computes test paths from a method.
	 * 
	 * @param methodInfo Informations about this method
	 * @return Test paths of this method
	 * @throws Throwable If an error occurs
	 */
	public List<List<Integer>> getTestPaths(ClassMethodInfo methodInfo) throws Throwable
	{
		String methodSignature = methodInfo.getMethodSignature()+"."+methodInfo.getMethodName()+"()";
		classInvocationSignature = extractClassSignature(methodInfo.getTestMethodSignature());
		methodInvocationLine = methodInfo.getInvocationLine();
		
		jdb_methodVisitor(methodSignature);
		
		return testPaths;
	}
	
	/**
	 * Initializes JDB and prepare it for executing methods within test methods.
	 * 
	 * @return Process running JDB
	 * @throws IOException If the process cannot be created
	 */
	private Process jdb_start() throws IOException
	{
		String lib_aspectj = new File("../lib/aspectjrt-1.9.2.jar").getPath();
		String lib_junit = new File("../lib/junit-4.13.jar").getPath();
		String lib_hamcrest = new File("../lib/hamcrest-all-1.3.jar").getPath();
		String lib_asm1 = new File("../lib/org.objectweb.asm_7.2.0.v20191010-1910.jar").getPath();
		String lib_asm2 = new File("../lib/org.objectweb.asm.tree_7.2.0.v20191010-1910.jar").getPath();
		String libs = lib_aspectj+";"+lib_junit+";"+lib_hamcrest+";"+lib_asm1+";"+lib_asm2;
		String jdb_classPath = "jdb -classpath .;"+libs;
		
		ProcessBuilder pb = new ProcessBuilder("cmd.exe","/c",jdb_classPath,"org.junit.runner.JUnitCore",classInvocationSignature);
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
	private void jdb_methodVisitor(String methodSignature) throws IOException, InterruptedException 
	{
		Process process = jdb_start();
        
        // Output
		Thread t = jdb_init_output(process, methodSignature);

        // Input
		OutputStream os = process.getOutputStream();
		jdb_init_input(process, os);
		
		// Exits JDB
		jdb_end();
		pw.close();
		os.close();
		process.waitFor();
		process.destroyForcibly();
		t.join();
	}
	
	private void jdb_init_input(Process process, OutputStream os) throws InterruptedException
	{
		pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(os)));

		// Executes initial commands
		jdb_initialCommands();
				
		// Executes while inside the method
		while (!endOfMethod) {
			inputReady = false;
			if (newIteration) {
				// Enters the method, ignoring aspectJ
				jdb_sendCommand("step into");
				jdb_sendCommand("next");
				jdb_sendCommand("next");
				jdb_sendCommand("step into");
				newIteration = false;
				inputReady = true;
				Thread.sleep(1);
			}
			else if (exitMethod) {
				// Saves test path
				testPaths.add(testPath);
				
				// Prepare for next test path
				testPath = new ArrayList<>();
				
				// Check output
				jdb_sendCommand("cont");
				exitMethod = false;
				inputReady = true;
				Thread.sleep(1);
				
			} else if (!endOfMethod) {
				jdb_sendCommand("next");
				inputReady = true;
				Thread.sleep(1);
			}
		}
	}
	
	private Thread jdb_init_output(Process process, String methodSignature)
	{
		InputStream is = process.getInputStream();
        
        Thread t = new Thread(() -> {
        	boolean inMethod = false;
        	int lastLineAdded = -1;
        	
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line;
                // Shell output
                System.out.println("Generating test path...");
                
                while ((line = br.readLine()) != null) {
                	while (!inputReady) {Thread.sleep(1);}
                	
                	// -----{ DEBUG }-----
                	if (DEBUG)
                		System.out.println(line);
            		// -----{ END DEBUG }-----
            		
                	endOfMethod = line.contains("Breakpoint hit") && line.contains("line="+lastLineTestMethod);
                	
                	if (endOfMethod) {
                		readyToDebug = true;
                		break;
                	}
                	
                	// Checks if JDB has started and is ready to receive debug commands
            		if (!endOfMethod && (line.contains("Breakpoint hit") || line.contains("Step completed"))) {
            			readyToDebug = true;
            			newIteration = line.contains("Breakpoint hit");
            			
            			// Checks if entered the method
            			if (!inMethod && line.contains("Step completed") && line.contains(classInvocationSignature)) {
                			inMethod = true;
                		} 
            			else if (inMethod) { 	
            				int lineNumber = jdb_getLine(line);
            				
            				if (line.contains(methodSignature) && lineNumber != lastLineAdded) {	// Checks if it is still in the method
            					testPath.add(lineNumber);
            					lastLineAdded = lineNumber;
            				}
            				// Checks if returned from the method
            				else if (line.contains(classInvocationSignature)) {
            					exitMethod = true;
            					inMethod = false;
            					endOfMethod = !process.isAlive();
            				}
            				
                		}
            		}
            		// Checks if there are input commands
                    try { Thread.sleep(1); } 
                    catch (InterruptedException e) {}
                }
                br.close();
            } catch (java.io.IOException | InterruptedException e) { }
        });
        t.start();
        
        return t;
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
	 * Exits from JDB.
	 */
	private void jdb_end()
	{
		pw.flush();
		pw.println("clear "+classInvocationSignature+":"+methodInvocationLine);
		pw.flush();
		pw.println("exit");
		pw.flush();
	}
	
	/**
	 * Initializes JDB.
	 */
	private void jdb_initialCommands() throws InterruptedException
	{
		List<String> args = new LinkedList<String>();
		args.add("clear");
		args.add("stop at "+classInvocationSignature+":"+methodInvocationLine);
		args.add("stop at "+classInvocationSignature+":"+lastLineTestMethod);
		args.add("threads");
        args.add("run");
        
		for (String arg : args) {
		    pw.println(arg);
		}
		
		pw.flush();
		waitForShell();
	}
	
	/**
	 * Sends a command to JDB.
	 * 
	 * @param command Command that will be sent to JDB
	 */
	private void jdb_sendCommand(String command)
	{
		// -----{ DEBUG }-----
		if (DEBUG)
			System.out.println("COMMAND: "+command);
		// -----{ END DEBUG }-----
		
		try {
			pw.println(command);
			pw.flush();
			waitForShell();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Waits for shell to finish executing commands.
	 * 
	 * @throws InterruptedException If thread is interrupted before shell 
	 * finishes executing the commands.
	 */
	private void waitForShell() throws InterruptedException
	{
		inputReady = true;
		
		while (!readyToDebug) {
			Thread.sleep(1);
		}
		
		readyToDebug = false;
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
	 * Extracts directory where a class is.
	 * 
	 * @param appPath Location of this application
	 * @param classPath Path of a class
	 * @return Directory where this class is
	 */
	private String extractClassPathDirectory(String appPath, String classPath)
	{
		String response = "";
		
		classPath = classPath.replace("\\", "/");
		
		// Extracts classes path directory
		String regex = appPath.replace("\\", "\\/")+"\\/[^\\/]+\\/";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(classPath);
		
		if (m.find()) {
			response = m.group();
		}
		
		return response;
	}
}
