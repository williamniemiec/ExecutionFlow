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
 * @version		3.0.0
 * @since		2.0.0
 */
public class JUnit4Runner 
{
	private static int totalTests;
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
			Process p = init(workingDirectory, classPath, classSignature);
			BufferedReader output = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader outputError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			String line;
			Pattern pattern_totalTests = Pattern.compile("[0-9]+");
			
			
			totalTests = 0;
			
			// Checks if there was an error creating the process
			while (outputError.ready() && (line = outputError.readLine()) != null) {
				System.err.println(line);
			}
			
			while ((line = output.readLine()) != null) {
				// Displays error messages (if any)
				while (outputError.ready() && (line = outputError.readLine()) != null) {
					System.err.println(line);
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
				else {
					System.out.println(line);
				}
			}
			
			// Closes process
			output.close();
			outputError.close();
			p.waitFor();
		} catch (IOException | InterruptedException e1) {
			e1.printStackTrace();
		}
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
		ProcessBuilder pb;
		
		
		pb = new ProcessBuilder(
			"cmd.exe", "/c",
			"java", "-cp", DataUtil.implode(classPath, ";"), 
			"org.junit.runner.JUnitCore", classSignature
		);
		
		pb.directory(workingDirectory.toFile());
		
		return pb.start();
	}
}
