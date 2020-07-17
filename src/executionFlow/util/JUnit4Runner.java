package executionFlow.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;

import executionFlow.ExecutionFlow;
import executionFlow.dependency.DependencyManager;
import executionFlow.dependency.MavenDependencyExtractor;
import executionFlow.info.MethodInvokedInfo;


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
	 * @param		testClassName Class name containing the test
	 * @param		testClassPath File path containing JUnit 4 test
	 * @param		testClassPackage Class package containing the test 
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
	 * @param		testClassName Class name containing the test
	 * @param		testClassPath File path containing JUnit 4 test
	 * @param		testClassPackage Class package containing the test
	 * 
	 * @return		CMD process
	 * 
	 * @throws		IOException If process cannot be created 
	 */
	private static Process init(String testClassName, Path testClassPath, String testClassPackage) throws IOException
	{
		Path testClassRootPath = MethodInvokedInfo.extractClassRootDirectory(testClassPath, testClassPackage);
		String classPath, classSignature, libPath;
		ProcessBuilder pb;
		
		
		libPath = testClassRootPath.relativize(ExecutionFlow.getLibPath()).toString() + "\\";
		
		// Gets dependencies (if any)
		if (!DependencyManager.hasDependencies()) {		
			ConsoleOutput.showInfo("Fetching dependencies...");
			DependencyManager.register(new MavenDependencyExtractor());
			DependencyManager.fetch();
			ConsoleOutput.showInfo("Fetch completed");
		}
		
		classPath = ".;" 
				+ libPath + "aspectjrt-1.9.2.jar;"
				+ libPath + "aspectjtools.jar;"
				+ libPath + "junit-4.13.jar;"
				+ libPath + "hamcrest-all-1.3.jar;"
				+ testClassRootPath.relativize(DependencyManager.getPath()).toString() + "\\*;"
				+ "..\\classes";
		
		classSignature = testClassPackage.isEmpty() ? 
				testClassName : testClassPackage + "." + testClassName;

		pb = new ProcessBuilder(
			"cmd.exe", "/c",
			"java", "-cp", classPath, 
			"org.junit.runner.JUnitCore", classSignature
		);
		
		pb.directory(testClassRootPath.toFile());
		
		return pb.start();
	}
}
