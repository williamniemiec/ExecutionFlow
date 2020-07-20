package executionFlow.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.List;


/**
 * Responsible for executing JUnit 4 tests.
 * 
 * @apiNote		Compatible with aspects
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		2.0.0
 * @since		2.0.0
 */
public class JUnit4Runner 
{
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Runs a JUnit 4 test file. 
	 * 
	 * @param		workingDirectory Directory that will serve as a reference
	 * for the execution of the process
	 * @param		classPath Class path list that will be used in JUnit 4 runner
	 * @param		classSignature Class signature to begin execution 
	 */
	public static void run(Path workingDirectory, List<String> classPath, String classSignature)
	{	
		try {
			Process p = init(workingDirectory, classPath, classSignature);
			BufferedReader output = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader outputError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			String line;
			
			
			// Checks if there was an error creating the process
			while (outputError.ready() && (line = outputError.readLine()) != null) {
				System.err.println(line);
			}
			
			while ((line = output.readLine()) != null) {
				// Displays error messages (if any)
				while (outputError.ready() && (line = outputError.readLine()) != null) {
					System.err.println(line);
				}
				
				if (!line.contains("JUnit version"))
					System.out.println(line);
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
	private static Process init(Path workingDirectory, List<String> classPath, String classSignature) throws IOException
	{
		ProcessBuilder pb;
		
		
		pb = new ProcessBuilder(
			"cmd.exe", "/c",
			"java", "-cp", DataUtils.implode(classPath, ";"), 
			"org.junit.runner.JUnitCore", classSignature
		);
		
		pb.directory(workingDirectory.toFile());
		
		return pb.start();
	}
}
