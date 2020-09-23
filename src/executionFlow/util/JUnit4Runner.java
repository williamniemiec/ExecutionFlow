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
 * @apiNote		Compatible with aspects
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.1.0
 * @since		2.0.0
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
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Runs a JUnit 4 test file. To display the JUnit version at the end, use
	 * the method in @see section.
	 * 
	 * @param		workingDirectory Directory that will serve as a reference
	 * for the execution of the process
	 * @param		classPath Class path list that will be used in JUnit 4 
	 * runner
	 * @param		classSignature Class signature to begin execution
	 * 
	 * @see			{@link #run(Path, List, String, boolean)}
	 */
	public static void run(Path workingDirectory, List<String> classPath, 
			String classSignature)
	{
		run(workingDirectory, classPath, classSignature, false);
	}
	
	/**
	 * Runs a JUnit 4 test file. 
	 * 
	 * @param		workingDirectory Directory that will serve as a reference
	 * for the execution of the process
	 * @param		classPath Class path list that will be used in JUnit 4 runner
	 * @param		classSignature Class signature to begin execution
	 * @param		displayVersion If true displays JUnit version at the end
	 */
	public static void run(Path workingDirectory, List<String> classPath, 
			String classSignature, boolean displayVersion)
	{	
		try {
			process = init(workingDirectory, classPath, classSignature);
			String line;
			Pattern pattern_totalTests = Pattern.compile("[0-9]+");
			boolean error = false;
			
						
			totalTests = 0;
			output = new BufferedReader(new InputStreamReader(process.getInputStream()));
			outputError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			
			// Checks if there was an error creating the process
			while (outputError.ready() && (line = outputError.readLine()) != null) {
				System.err.println(line);
			}
			
			while ((line = output.readLine()) != null) {
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
			}
			
			// Closes process
			output.close();
			outputError.close();
			process.waitFor();
		} catch (IOException | InterruptedException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Ends the process containing JUnit 4.
	 * 
	 * @throws		IOException If an I/O error occurs
	 */
	public static void quit() throws IOException
	{
		output.close();
		outputError.close();
		process.destroyForcibly();
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
}
