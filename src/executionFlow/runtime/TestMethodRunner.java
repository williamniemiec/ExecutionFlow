package executionFlow.runtime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;

import executionFlow.ExecutionFlow;
import executionFlow.info.MethodInvokerInfo;


/**
 * Responsible for executing the processed test method file so that the 
 * collection of the methods can be done. This processing makes possible to 
 * collect all methods of the test method, even if an assert fails.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		1.5
 * @since		1.5
 */
public class TestMethodRunner 
{
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Runs the test method in a new process. It is necessary for the aspects 
	 * to collect methods based on the pre-processing performed by method
	 * {@link executionFlow.core.file.MethodManager#assertParser(String)}.
	 * 
	 * @param		testClassName Class name containing the test method
	 * @param		testClassPath Test class path containing the test method
	 * @param		testClassPackage Package of the class containing the test
	 * method 
	 */
	public static void run(String testClassName, Path testClassPath, String testClassPackage)
	{	
		String appRoot = ExecutionFlow.getAppRootPath();
		Path testClassRootPath = MethodInvokerInfo.extractClassRootDirectory(testClassPath, testClassPackage);
		Path libPath = Path.of(appRoot + "\\lib");
		String libPath_relative = testClassRootPath.relativize(libPath).toString() + "\\";
		String cp_junitPlatformConsole = libPath_relative + "junit-platform-console-standalone-1.6.2.jar";
		String libs = libPath_relative + "aspectjrt-1.9.2.jar" + ";"
				+ libPath_relative + "aspectjtools.jar" + ";"
				
				+ libPath_relative + "junit-4.13.jar" + ";"
				+ libPath_relative + "hamcrest-all-1.3.jar" + ";"
				
				+ cp_junitPlatformConsole;
		String classPath = ".;" + libs + ";..\\classes";
		String classSignature = testClassPackage.isEmpty() ? 
				testClassName : testClassPackage + "." + testClassName;

		
		try {
			ProcessBuilder pb = new ProcessBuilder(
					"cmd.exe", "/c",
					"java", "-cp", classPath, "org.junit.runner.JUnitCore",
					 classSignature
				);
			
			pb.directory(testClassRootPath.toFile());
			
			Process p = pb.start();
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
				
				System.out.println(line);
			}
			
			output.close();
			outputError.close();
			p.waitFor();
		} catch (IOException | InterruptedException e1) {
			e1.printStackTrace();
		}
	}
}
