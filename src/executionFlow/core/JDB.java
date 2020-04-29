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
	private String methodClassSignature;
	private String classInvocationSignature;
	private int lastLineTestMethod;
	private int methodInvocationLine;
	private List<Integer> testPath;
	private List<List<Integer>> testPaths;
	private Path libPath;
	private PrintWriter pw;
	private boolean readyToDebug;
	private boolean endOfMethod;
	private boolean newIteration;
	private boolean exitMethod;
	private boolean inputReady;
	private boolean isInternalCommand;
	private final boolean DEBUG; 
	
	
	//-----------------------------------------------------------------------
	//		Initialization block
	//-----------------------------------------------------------------------
	/**
	 * Enables or disables debug. If activated, shows shell output during JDB execution.
	 * 
	 * <b>Note:</b> If it is enabled and there are multiple method tests, the
	 * computation of test path is not guaranteed.
	 */
	{
		DEBUG = false;
	}
	
	
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
		
		testPath = new ArrayList<>();
		testPaths = new ArrayList<>();
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
	public synchronized List<List<Integer>> getTestPaths(ClassMethodInfo methodInfo) throws Throwable
	{
		methodClassSignature = methodInfo.getMethodSignature();
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
	private synchronized Process jdb_start() throws IOException
	{
		findLibs(Path.of(classPathRoot));
		String libPath_relative = Paths.get(classPathRoot).relativize(libPath).toString()+"\\";
		String lib_aspectj = libPath_relative+"aspectjrt-1.9.2.jar";
		String lib_junit = libPath_relative+"junit-4.13.jar";
		String lib_hamcrest = libPath_relative+"hamcrest-all-1.3.jar";
		String lib_asm1 = libPath_relative+"org.objectweb.asm_7.2.0.v20191010-1910.jar";
		String lib_asm2 = libPath_relative+"org.objectweb.asm.tree_7.2.0.v20191010-1910.jar";
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
	private synchronized void jdb_methodVisitor(String methodSignature) throws IOException, InterruptedException 
	{
		Process process = jdb_start();
        
        // Output
		Thread t = jdb_output(process, methodSignature);

        // Input
		OutputStream os = process.getOutputStream();
		jdb_input(process, os);
		
		// Exits JDB
		jdb_end();
		pw.close();
		os.close();
		process.waitFor();
		process.destroyForcibly();
		t.join();
	}
	
	private synchronized void jdb_input(Process process, OutputStream os) throws InterruptedException
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
				
				while (isInternalCommand) {
					jdb_sendCommand("next");
					jdb_checkOutput();
				}

				jdb_sendCommand("step into");
				newIteration = false;
				jdb_checkOutput();
			}
			else if (exitMethod) {
				// Saves test path
				testPaths.add(testPath);
				
				// Prepare for next test path
				testPath = new ArrayList<>();
				
				// Checks if method is in a loop
				jdb_sendCommand("cont");
				
				// Check output
				exitMethod = false;
				jdb_checkOutput();
			} else if (!endOfMethod) {
				jdb_sendCommand("next");
				jdb_checkOutput();
			}
		}
	}
	
	private Thread jdb_output(Process process, String methodSignature)
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
                	while (!inputReady) { Thread.sleep(1); }
                	
                	if (line.equals("\n") || line.equals("") || line.equals(" ")) continue;

                	// -----{ DEBUG }-----
                	if (DEBUG)
                		System.out.println(line);
            		// -----{ END DEBUG }-----
            		
                	endOfMethod = line.contains("Breakpoint hit") && line.contains("line="+lastLineTestMethod);
                	isInternalCommand = !line.contains(methodClassSignature) && !line.contains(classInvocationSignature);//line.contains("aspectj") || line.contains("aspectOf");
                	
                	if (endOfMethod) {
                		readyToDebug = true;
                		break;
                	}

                	// Checks if JDB has started and is ready to receive debug commands
            		if (!endOfMethod && (line.contains("Breakpoint hit") || line.contains("Step completed"))) {
            			readyToDebug = true;
            			newIteration = line.contains("Breakpoint hit") || (line.contains("line="+methodInvocationLine) && line.contains(classInvocationSignature));
            			
            			if (isInternalCommand) {
            				inMethod = false;
            				Thread.sleep(1);
            				continue;
            			}

            			// Checks if entered the method
            			if (!inMethod && line.contains("Step completed") && line.contains(classInvocationSignature)) {
                			inMethod = true;
                		} 
            			else if (inMethod) { 	
            				int lineNumber = jdb_getLine(line);
            				
            				// Checks if returned from the method
            				if (line.contains(classInvocationSignature) && line.contains("line="+methodInvocationLine)) {
            					exitMethod = true;
            					newIteration = false;
            					inMethod = false;
            					endOfMethod = !process.isAlive();
            				} else if (line.contains(methodSignature) && lineNumber != lastLineAdded) {	// Checks if it is still in the method
            					testPath.add(lineNumber);
            					lastLineAdded = lineNumber;
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
	 * Checks if there are outputs that have to be processed. For this, it will 
	 * use the thread defined in the method 'jdb_output'.
	 */
	private void jdb_checkOutput()
	{
		inputReady = true;
		
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
	
	/**
	 * Finds directory of application libraries and stores it in {@link #libPath}.
	 * 
	 * @param binPath Location of binary files (.class)
	 */
	private void findLibs(Path binPath)
	{
		binPath = binPath.getParent();
		
		try {
			Files.walkFileTree(binPath, new SimpleFileVisitor<Path>() {
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
}
