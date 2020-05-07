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
	private static String appPath;
	private int lastLineTestMethod;
	private int methodInvocationLine;
	private List<Integer> testPath;
	private List<List<Integer>> testPaths;
	private static Path libPath;
	private PrintWriter pw;
	private boolean readyToDebug;
	private boolean endOfMethod;
	private boolean newIteration;
	private boolean exitMethod;
	private boolean inputReady;
	private boolean isInternalCommand;
	private final boolean USING_ASPECTJ;
	private final boolean DEBUG; 
	String srcPath;
	
	//-----------------------------------------------------------------------
	//		Initialization block
	//-----------------------------------------------------------------------
	/**
	 * Enables or disables debug. If activated, shows shell output during JDB execution.
	 * Also, 
	 * 
	 * <b>Note:</b> If debug is enabled and there are multiple method tests, the
	 * computation of test path is not guaranteed.
	 */
	{
		DEBUG = false;
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
	
	
	//-----------------------------------------------------------------------
	//		Constructor
	//-----------------------------------------------------------------------
	/**
	 * Computes test path from code debugging. Use this constructor if this class
	 * will be used within the context of aspects.
	 * 
	 * @param lastLineTestMethod Test method end line
	 */
	public JDB(int lastLineMethod)
	{
		this.lastLineTestMethod = lastLineMethod;
		this.USING_ASPECTJ = true;
		
		testPath = new ArrayList<>();
		testPaths = new ArrayList<>();
	}
	
	/**
	 * Computes test path from code debugging. If this class will be used
	 * outside the context of aspects, you must pass 'true' for 'usingAspectJ'
	 * parameter.
	 * 
	 * @param lastLineTestMethod Test method end line
	 * @param usingAspectJ If this class will be used within the context of
	 * aspects 
	 */
	public JDB(int lastLineMethod, boolean usingAspectJ)
	{
		this.USING_ASPECTJ = usingAspectJ;
		this.lastLineTestMethod = lastLineMethod;
		
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
		System.out.println("####"+methodInfo);
		
		methodClassSignature = methodInfo.getMethodSignature();
		String methodSignature = methodInfo.getMethodSignature()+"."+methodInfo.getMethodName()+"()";
		classInvocationSignature = extractClassSignature(methodInfo.getTestMethodSignature());
		methodInvocationLine = methodInfo.getInvocationLine();
		
		extractClassPathDirectory(methodInfo);
		srcPath = extractSrcPathDirectory(methodInfo);
		
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
		String libPath_relative = Paths.get(classPathRoot).relativize(libPath).toString()+"\\";
		String lib_aspectj = libPath_relative+"aspectjrt-1.9.2.jar";
		String lib_junit = libPath_relative+"junit-4.13.jar";
		String lib_hamcrest = libPath_relative+"hamcrest-all-1.3.jar";
		String lib_asm1 = libPath_relative+"org.objectweb.asm_7.2.0.v20191010-1910.jar";
		String lib_asm2 = libPath_relative+"org.objectweb.asm.tree_7.2.0.v20191010-1910.jar";
		String libs = lib_aspectj+";"+lib_junit+";"+lib_hamcrest+";"+lib_asm1+";"+lib_asm2;
		String jdb_classPath = "-classpath .;"+libs;
		String jdb_srcPath = "-sourcepath "+Paths.get(classPathRoot).relativize(Paths.get(srcPath));
		String jdb_paths = jdb_srcPath+" "+jdb_classPath;
		
		System.out.println(jdb_paths);
		//System.out.println(new File(".").getCanonicalPath());
//		System.out.println("jdb classpath: "+jdb_classPath);
//		System.out.println("CPR: "+classPathRoot);
//		System.out.println("cis: "+classInvocationSignature);
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

				if (USING_ASPECTJ) {
					jdb_sendCommand("step into");
					newIteration = false;
					jdb_checkOutput();
				}
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
                String commandLine = null;
                while (!endOfMethod && (line = br.readLine()) != null) {
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
            			newIteration = !inMethod && (line.contains("Breakpoint hit") || (line.contains("line="+methodInvocationLine) && line.contains(classInvocationSignature)));
            			commandLine = br.readLine();
            			
            			if (isInternalCommand) {
            				inMethod = false;
            				Thread.sleep(1);
            				continue;
            			}

            			// Checks if entered the method
            			if (newIteration || (!inMethod && line.contains("Step completed") && line.contains(classInvocationSignature))) {
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
            					if (!commandLine.matches("([0-9]+)(\\ |\\t)+\\}((\\ |\\t)+)?($)")) {
            						testPath.add(lineNumber);
            						lastLineAdded = lineNumber;
            					}
            				}
                		}
            		}
            		
            		endOfMethod = line.contains("The application exited");

            		if (endOfMethod) {
            			readyToDebug = true;
            			break;
            		}
            		
            		// Checks if there are input commands
            		while (!inputReady) { Thread.sleep(1); }
            		
            		// -----{ DEBUG }-----
                	if (DEBUG && commandLine != null)
                		System.out.println(commandLine);
            		// -----{ END DEBUG }-----
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
	 * Extracts directory where classes are. This directory is the first before 
	 * package directories.
	 * 
	 * @param methodInfo Information about a method
	 * @return Directory where classes are
	 */
	private void extractClassPathDirectory(ClassMethodInfo methodInfo)
	{
		String classPath = methodInfo.getClassPath();
		String[] tmp = classPath.split("\\\\");
		String classFileName = tmp[tmp.length-1];

		try {
			Files.walkFileTree(Path.of(appPath), new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
				{
					if (file.endsWith(classFileName)) {
						file = file.getParent();

						// -1 not to consider method name
						//int packageFolders = methodInfo.getSignature().split("\\(")[0].split("\\.").length - 1;
						int packageFolders = classInvocationSignature.split("\\.").length-1;
						
						for (int i=0; i<packageFolders; i++) {
							file = file.getParent();
						}
						
//						System.out.println("packageFolders: "+packageFolders);
//						System.out.println("methodSig: "+methodInfo.getSignature());
//						System.out.println("FILE: "+file);
						classPathRoot = file.toString();
						
						return FileVisitResult.TERMINATE;
					}
					
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String extractSrcPathDirectory(ClassMethodInfo methodInfo)
	{
		int packageFolders = methodInfo.getMethodSignature().split("\\.").length-1;
		Path file = new File(methodInfo.getSrcPath()).toPath();
		
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
}
