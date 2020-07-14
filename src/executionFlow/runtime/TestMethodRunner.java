package executionFlow.runtime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;

import executionFlow.ExecutionFlow;
import executionFlow.dependency.DependencyManager;
import executionFlow.dependency.MavenDependencyExtractor;
import executionFlow.info.MethodInvokerInfo;
import executionFlow.util.ConsoleOutput;


/**
 * Responsible for executing the processed test method file so that the 
 * collection of the methods can be done. This processing makes possible to 
 * collect all methods of the test method, even if an assert fails.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		2.0.0
 * @since		2.0.0
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
		try {
			Process p = init(testClassName, testClassPath, testClassPackage);
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
	
	/**
	 * Initializes CMD.
	 * 
	 * @return		CMD process
	 * 
	 * @throws		IOException If process cannot be created 
	 */
	private static Process init(String testClassName, Path testClassPath, String testClassPackage) throws IOException
	{
		Path testClassRootPath = MethodInvokerInfo.extractClassRootDirectory(testClassPath, testClassPackage);
		Path libPath = ExecutionFlow.getLibPath();
		String libPath_relative = testClassRootPath.relativize(libPath).toString() + "\\";
		String libs = libPath_relative + "aspectjrt-1.9.2.jar" + ";"
				+ libPath_relative + "aspectjtools.jar" + ";"
				+ libPath_relative + "junit-4.13.jar" + ";"
				+ libPath_relative + "hamcrest-all-1.3.jar";
		String classPath;
		String classSignature;
		
		
		// Gets dependencies (if any)
		if (!DependencyManager.hasDependencies()) {
			try {		
				ConsoleOutput.showInfo("Fetching dependencies...");
				DependencyManager.register(new MavenDependencyExtractor());
				DependencyManager.fetch();
				ConsoleOutput.showInfo("Fetch completed");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		classPath = ".;" 
				+ libs + ";"
				+ testClassRootPath.relativize(DependencyManager.getPath()).toString() + "\\*" + ";"
				+ "..\\classes";
		classSignature = testClassPackage.isEmpty() ? 
				testClassName : testClassPackage + "." + testClassName;


		ProcessBuilder pb = new ProcessBuilder(
			"cmd.exe", "/c",
			"java", "-cp", classPath, 
			"org.junit.runner.JUnitCore", classSignature
		);
		
		System.out.println("cp: "+classPath);
		
		pb.directory(testClassRootPath.toFile());
		
		return pb.start();
	}
}
