package executionFlow.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Responsible for executing JUnit 4 tests.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 */
public class JUnit4Runner 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static int totalTests;
	private static Process process;
	private static BufferedReader output;
	private static BufferedReader outputError;
	private static boolean stopped;
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Runs a JUnit 4 test file.
	 * 
	 * @param		workingDirectory Directory that will serve as a reference
	 * for the execution of the process
	 * @param		classPathArgumentFile Argument file containing class path 
	 * list that will be used in JUnit 4 runner
	 * @param		classSignature Class signature to begin execution
	 * 
	 * @throws		IOException If an error occurs while reading process output
	 * @throws		InterruptedException If the process containing JUnit 4 is 
	 * interrupted by another thread while it is waiting
	 * @throws		IllegalStateException If process is null
	 * 
	 * @see			{@link #run(Path, List, String, boolean)}
	 */
	public static void run(Path workingDirectory, Path classPathArgumentFile, 
			String classSignature) throws IOException, InterruptedException
	{
		process = init(workingDirectory, classPathArgumentFile, classSignature);
		Thread.sleep(3000);
		run(workingDirectory, classSignature, false);
	}
	
	/**
	 * Runs a JUnit 4 test file.
	 * 
	 * @param		workingDirectory Directory that will serve as a reference
	 * for the execution of the process
	 * @param		classPath Class path list that will be used in JUnit 4 
	 * runner
	 * @param		classSignature Class signature to begin execution
	 * 
	 * @throws		IOException If an error occurs while reading process output
	 * @throws		InterruptedException If the process containing JUnit 4 is 
	 * interrupted by another thread while it is waiting
	 * @throws		IllegalStateException If process is null
	 * 
	 * @see			{@link #run(Path, List, String, boolean)}
	 */
	public static void run(Path workingDirectory, List<String> classPath, 
			String classSignature) throws IOException, InterruptedException
	{
		process = init(workingDirectory, classPath, classSignature);
		
		run(workingDirectory, classSignature, false);
	}
	
	/**
	 * Runs a JUnit 4 test file. 
	 * 
	 * @param		workingDirectory Directory that will serve as a reference
	 * for the execution of the process
	 * @param		classPath Class path list that will be used in JUnit 4 runner
	 * @param		classSignature Class signature to begin execution
	 * @param		displayVersion If true displays JUnit version at the end
	 * 
	 * @throws		IOException If an error occurs while reading process output
	 * @throws		InterruptedException If the process containing JUnit 4 is 
	 * interrupted by another thread while it is waiting
	 * @throws		IllegalStateException If process is null
	 */
	private static void run(Path workingDirectory, 	String classSignature, 
			boolean displayVersion) throws IOException, InterruptedException
	{	
		if (process == null)
			throw new IllegalStateException("Process has not been initialized");
		
		String line;
		Pattern pattern_totalTests = Pattern.compile("[0-9]+");
		boolean error = false;
		boolean fatalError = false;
		
		
		stopped = false;			
		totalTests = 0;
		output = new BufferedReader(new InputStreamReader(process.getInputStream()));
		outputError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		
		// Checks if there was an error creating the process
		while (outputError.ready() && (line = outputError.readLine()) != null) {
			System.err.println(line);
		}
		
		while (!fatalError && !stopped && (line = output.readLine()) != null) {
			error = false;
			
			// Displays error messages (if any)
			if (outputError.ready() && (line = outputError.readLine()) != null) {
				System.err.println(line);
				error = true;
			}
			
			if (line.contains("OK (")) {
				Matcher m = pattern_totalTests.matcher(line);
				
				
				m.find();
				totalTests = Integer.valueOf(m.group());
			}
			else if (line.contains("JUnit version")) {
				if (displayVersion)
					System.out.println(line);
			}
			else if (!error){
				System.out.println(line);
			}
			
			System.out.flush();
			
			fatalError = line.equals("FAILURES!!!");
		}
		
		// Closes process
		if (!stopped) {
			output.close();
			outputError.close();
			process.waitFor();
		}
	}
	
	/**
	 * Ends the process containing JUnit 4.
	 * 
	 * @throws		IOException If an I/O error occurs
	 */
	public static void quit() throws IOException
	{
		stopped = true;
		process.destroyForcibly();
		output.close();
		outputError.close();
	}
	
	/**
	 * Checks whether the process is running.
	 * 
	 * @return		True if the process is running; false otherwise
	 */
	public static boolean isRunning()
	{
		return process != null && process.isAlive();
	}
	
	/**
	 * Gets the total number of tests performed.
	 * 
	 * @return		Total of tests performed
	 */
	public static int getTotalTests()
	{
		return totalTests;
	}
	
	/**
	 * Initializes CMD with JUnit 4 running a test file.
	 * 
	 * @param		workingDirectory Directory that will serve as a reference
	 * for the execution of the process
	 * @param		classPath Class path list that will be used in JUnit 4 runner
	 * @param		classSignature Class signature to begin execution 
	 * 
	 * @return		CMD process running JUnit 4 test
	 * 
	 * @throws		IOException If process cannot be created 
	 */
	private static Process init(Path workingDirectory, List<String> classPath, 
			String classSignature) throws IOException
	{
		ProcessBuilder pb = new ProcessBuilder(
			"java", "-cp", DataUtil.implode(classPath, ";"), 
			"org.junit.runner.JUnitCore", classSignature
		);
		
		pb.directory(workingDirectory.toFile());
		
		return pb.start();
	}
	
	/**
	 * Initializes CMD with JUnit 4 running a test file.
	 * 
	 * @param		workingDirectory Directory that will serve as a reference
	 * for the execution of the process
	 * @param		classPathArgumentFile Argument file containing class path 
	 * list that will be used in JUnit 4 runner
	 * @param		classSignature Class signature to begin execution 
	 * 
	 * @return		CMD process running JUnit 4 test
	 * 
	 * @throws		IOException If process cannot be created 
	 */
	private static Process init(Path workingDirectory, Path classPathArgumentFile, 
			String classSignature) throws IOException
	{
		classPathArgumentFile = workingDirectory.relativize(classPathArgumentFile);

		ProcessBuilder pb = new ProcessBuilder(
			"java", "-cp", "@" + classPathArgumentFile, 
			"org.junit.runner.JUnitCore", classSignature
		);
		
		pb.directory(workingDirectory.toFile());
		
		return pb.start();
	}
}
