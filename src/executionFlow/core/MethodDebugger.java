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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import executionFlow.MethodExecutionFlow;
import executionFlow.info.ClassConstructorInfo;
import executionFlow.info.ClassMethodInfo;


public class MethodDebugger 
{
	private String classPathRoot;
	private boolean readyToDebug = false;
	private boolean endOfMethod = false;
	private List<Integer> testPath = new ArrayList<>();
	private List<List<Integer>> testPathes = new ArrayList<>();
	private PrintWriter pw;
	private InputStream is;
	private InputStream ies;
	private OutputStream os;
	private int lastLineAdded = -1;
	private boolean newIteration;
	private boolean exitMethod;
	private Process process;
	boolean inputProcessing = false;
	boolean inputReady = false;
	private int lastLineMethod;
	
	public MethodDebugger(String projectPath, String classPath)
	{
		classPath = classPath.replace("\\", "/");
		// Extract class path root
		String regex = projectPath.replace("\\", "\\/")+"\\/[^\\/]+\\/";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(classPath);
		
		if (m.find()) {
			classPathRoot = m.group();
		}
	}
	
	public MethodDebugger(String projectPath, String classPath, int lastLineMethod)
	{
		this.lastLineMethod = lastLineMethod;
		this.classPathRoot = extractClassPathRoot(projectPath, classPath);
	}
	
	public List<List<Integer>> getTestPaths(ClassMethodInfo methodInfo) throws Throwable
	{
		String methodSignature = methodInfo.getMethodSignature()+"."+methodInfo.getMethodName()+"()";
		String[] tmp = methodInfo.getTestMethodSignature().split("\\.");
		StringBuilder sb = new StringBuilder();
		
		for (int i=0; i<tmp.length-1;i++) {
			sb.append(tmp[i]);
			sb.append(".");
		}
		
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length()-1);	// Removes last dot
		}
		final String classInvocationSignature = sb.toString();
		jdb_methodVisitor(classInvocationSignature, methodSignature, methodInfo.getInvocationLine());
		
		return testPathes;
	}
	
	
	private void jdb_methodVisitor(String classInvocationSignature, String methodSignature, int methodInvocationLine) throws IOException, InterruptedException 
	{
		//lastLineMethod = 36;
		
		// Shell initialization
		String lib_aspectj = new File("../lib/aspectjrt-1.9.2.jar").getPath();
		String lib_junit = new File("../lib/junit-4.13.jar").getPath();
		String lib_hamcrest = new File("../lib/hamcrest-all-1.3.jar").getPath();
		String lib_asm1 = new File("../lib/org.objectweb.asm_7.2.0.v20191010-1910.jar").getPath();
		String lib_asm2 = new File("../lib/org.objectweb.asm.tree_7.2.0.v20191010-1910.jar").getPath();
		String libs = lib_aspectj+";"+lib_junit+";"+lib_hamcrest+";"+lib_asm1+";"+lib_asm2;
		String jdb_classPath = "jdb -classpath .;"+libs;
		
        //ProcessBuilder pb = new ProcessBuilder("cmd.exe","/c","jdb -classpath ../lib/aspectjrt-1.9.2.jar;.;../lib/junit-4.13.jar;../lib/hamcrest-all-1.3.jar","org.junit.runner.JUnitCore",classInvocationSignature);
		ProcessBuilder pb = new ProcessBuilder("cmd.exe","/c",jdb_classPath,"org.junit.runner.JUnitCore",classInvocationSignature);
		pb.directory(new File(classPathRoot));
        process = pb.start();
        
        /* OUTPUT */
        is = process.getInputStream();
        ies = process.getErrorStream();
        Thread t = new Thread(() -> {
        	boolean inMethod = false;
        	boolean firstTime = true;
        	
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                BufferedReader bre = new BufferedReader(new InputStreamReader(ies));
                String line;
                // Shell output
                System.out.println("Generating test path...");
                
                while ((line = br.readLine()) != null) {
                	// -----{ DEBUG }-----
//                	System.out.println("output");
//                	System.out.println("endOfMethod: "+endOfMethod);
//                	System.out.println();
                	while (!inputReady) {Thread.sleep(1);}
                	
                	if (bre.ready()) {
                		System.out.println("ERROR: "+bre.readLine());
                	}
                	
            		System.out.println(line);
            		
                	// -----{ END DEBUG }-----
                	endOfMethod = line.contains("Breakpoint hit") && line.contains("line="+lastLineMethod);
                	
                	if (endOfMethod) {
                		readyToDebug = true;
                		break;
                	}
                	
                	
                	
                	// Checks if JDB has started and is ready to receive debug commands
            		if (!endOfMethod && (line.contains("Breakpoint hit") || line.contains("Step completed"))) {
            			readyToDebug = true;
            			newIteration = line.contains("Breakpoint hit");
                    	System.out.println("NEW IT! "+newIteration);
//                    	while(!inputReady) {
//                    		while (br.ready()) {
//                    			line = br.readLine();
//                    			System.out.println(line);
//                    			if (line.contains("Breakpoint hit") || line.contains("Step completed")) 
//        							readyToDebug = true;
//                    		}
//                    		Thread.sleep(1);
//                    	}
//                    	inputReady = false;
            			
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
            				//else if (isEndOfMethod(line)) {
            				//else if (line.contains("jdk.internal.misc.VM")) {
            				// There is a new bp
            				else if (line.contains(classInvocationSignature)) {
            					exitMethod = true;
            					inMethod = false;
            					endOfMethod = !process.isAlive();
            					//this.wait();
            				}
            				
                		}
            		}
                    //System.out.println("false");
            		// Checks if there are input commands
                    try { Thread.sleep(1); } 
                    catch (InterruptedException e) {}
                }
                br.close(); System.out.println("END THREAD");
            } catch (java.io.IOException | InterruptedException e) { }
        });
        t.start();

        /* INPUT */
        os = process.getOutputStream();
		pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(os)));

		// Executes initial commands
		jdb_initialCommands(pw, classInvocationSignature, methodInvocationLine, lastLineMethod);
		
		// Enters the method, ignoring aspectJ
		
		// Executes while inside the method
		while (!endOfMethod) {
			inputReady = false;
			System.out.println("while");
			if (newIteration) {
				//inputProcessing = true;
				jdb_sendCommand(pw, "step into");
				jdb_sendCommand(pw, "next");
				jdb_sendCommand(pw, "next");
				jdb_sendCommand(pw, "step into");
				//inputProcessing = false;
				newIteration = false;
				inputReady = true;
				Thread.sleep(1);
			}
			else if (exitMethod) {
				//inputProcessing = true;
				
				
				// Saves test path
				testPathes.add(testPath);
				System.out.println("@@@@@@@@@@@@@@@@@@@");
				System.out.println("tp added: "+testPath);
				System.out.println("@@@@@@@@@@@@@@@@@@@");
				
				// Prepare for next test path
				testPath = new ArrayList<>();
				// Check output
				//inputProcessing = false;
				jdb_sendCommand(pw, "cont");
				exitMethod = false;
				inputReady = true;
				Thread.sleep(1);
				
			} else if (!endOfMethod) {
				//inputProcessing = true;
				jdb_sendCommand(pw, "next");
				//inputProcessing = false;
				inputReady = true;
				Thread.sleep(1);
			}
			
		}
		//System.out.println("end while");
		jdb_end(pw, classInvocationSignature, methodInvocationLine);
		pw.close();
		os.close();
		//System.out.println("closed");
		process.waitFor();
		process.destroyForcibly();
		//System.out.println("process closed");
		t.join();
		System.out.println("@@@@@@@@@@@@@@@@@@@");
		System.out.println("tp inside method: "+testPathes);
		System.out.println("@@@@@@@@@@@@@@@@@@@");
	}
	
	private boolean isEndOfMethod(String line)
	{
		return (
			!line.contains("aspectj") &&
			!line.contains("executionFlow")
		);
	}
	
	private int jdb_getLine(String line)
	{
		int response = -1;
		
		Pattern p = Pattern.compile("line=[0-9]+");
		Matcher m = p.matcher(line);
		
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
	
	private void jdb_end(PrintWriter pw, String classSignature, int methodLine)
	{
		System.out.println("end");
		pw.flush();
		pw.println("clear "+classSignature+":"+methodLine);
		pw.flush();
		pw.println("exit");
		pw.flush();
	}
	
	private void jdb_initialCommands(PrintWriter pw, String classSignature, int methodLine, int lastLineMethod) throws InterruptedException
	{
		List<String> args = new LinkedList<String>();
		args.add("clear");
		args.add("stop at "+classSignature+":"+methodLine);
		args.add("stop at "+classSignature+":"+lastLineMethod);
		args.add("threads");
        args.add("run");
        
		for (String arg : args) {
		    pw.println(arg);
		}
		
		pw.flush();
		waitForShell();
	}
	
	private void jdb_sendCommand(PrintWriter pw, String command)
	{
		System.out.println("COMMAND: "+command);
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
	
	private String extractClassPathRoot(String projectPath, String classPath)
	{
		String response = "";
		
		classPath = classPath.replace("\\", "/");
		
		// Extract class path root
		String regex = projectPath.replace("\\", "\\/")+"\\/[^\\/]+\\/";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(classPath);
		
		if (m.find()) {
			response = m.group();
		}
		
		return response;
	}
}
