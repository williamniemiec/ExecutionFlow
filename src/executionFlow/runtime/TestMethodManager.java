package executionFlow.runtime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;

import executionFlow.ConsoleOutput;
import executionFlow.core.file.FileManager;
import executionFlow.core.file.parser.factory.AssertFileParserFactory;
import executionFlow.info.ClassMethodInfo;


/**
 * Responsible for pre-processing the test method so that the collection of the
 * methods is done. This pre-processing will make possible to collect all 
 * methods of the test method, even if {@link org.junit.ComparisonFailure}
 * occurs.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		1.5
 * @since		1.5
 *
 */
public class TestMethodManager
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private FileManager testMethodFileManager;
	private Path testClassPath; 
	private String testClassPackage;
	private static final String DELIMITER_END_TEST_METHOD = "_END_OF_TEST_METHOD";
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	/**
	 * Performs the pre-processing of the test method so that the collection of
	 * the methods within the test method is done even if 
	 * {@link org.junit.ComparisonFailure} occurs.
	 * 
	 * @param		testClassPath Path of the test method
	 * @param		testClassPackage Package of the test method
	 */
	public TestMethodManager(Path testClassPath, String testClassPackage)
	{
		this.testClassPath = testClassPath;
		this.testClassPackage = testClassPackage;
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Parses test method and handles all asserts so that method collection 
	 * is done even if {@link org.junit.ComparisonFailure} occurs.
	 * 
	 * @param		testSrcPath Test method source file path
	 * @return		If parser has been completed or false if the parser has 
	 * already been done
	 * @throws		IOException If it is not possible to reverse the processing
	 * done
	 */
	public boolean assertParser(Path testSrcPath) throws IOException
	{
		testMethodFileManager = new FileManager(
			testSrcPath,
			ClassMethodInfo.getCompiledFileDirectory(testClassPath),
			testClassPackage,
			new AssertFileParserFactory(),
			"original2"
		);
		
		if (testMethodFileManager.hasClassBackupStored()) 
			return false;
		
		ConsoleOutput.showInfo("Pre-processing test method...");
		
		try {
			testMethodFileManager.createClassBackupFile()
				.parseFile()
				.compileFile();
		} catch(IOException e) {
			testMethodFileManager.revertCompilation();
			testMethodFileManager.revertParse();
			e.printStackTrace();
		}
		
		ConsoleOutput.showInfo("Pre-processing has been completed!");
		
		return true;
	}
	
	/**
	 * Restores test method original files (source file and compiled file).
	 */
	public void restoreOriginalFiles()
	{
		try {
			testMethodFileManager.revertParse();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			testMethodFileManager.revertCompilation();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Runs the test method in a new process. It is necessary for the aspects 
	 * to collect methods based on the pre-processing performed by method
	 * {@link #assertParser(String)}.
	 * 
	 * @param		testClassName Class name containing the test method
	 * 
	 * @implSpec	At the end, it will restore compiled file before executing
	 * {@link #assertParser(String)}.
	 */
	public void run(String testClassName)
	{	
		try {
			ProcessBuilder pb = new ProcessBuilder(
				"cmd.exe","/c",
				"java "+"-classpath ..\\lib\\junit-4.13.jar;"
						+ "..\\lib\\hamcrest-all-1.3.jar;"
						+ ".;"
						+ "..\\lib\\aspectjrt-1.9.2.jar;"
						+ "..\\lib\\aspectjtools.jar;",
				"org.junit.runner.JUnitCore",testClassName
			);
			
			pb.directory(ClassMethodInfo.extractClassRootDirectory(testClassPath, testClassPackage).toFile());
			final String regex_junitInfo1 = "^JUnit version [0-9]+(\\.[0-9]+)?";
			final String regex_junitInfo2 = "^Time: [0-9]+(\\,[0-9]+)?";
			final String regex_junitInfo3 = "^OK \\([0-9]+\\ test(s)?\\)$";
			final String regex_endMethod = "^"+DELIMITER_END_TEST_METHOD+"$";
			boolean endTestMethod = false;
			
			try {
				Process p = pb.start();
				BufferedReader output = new BufferedReader(new InputStreamReader(p.getInputStream()));
				BufferedReader outputError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				
				String line;

				while ((line = output.readLine()) != null) {
					// Displays error messages (if any)
					while (outputError.ready() && (line = outputError.readLine()) != null) {
						System.err.println(line);
					}
					
					// Checks if it is initial message
					if (line.matches(regex_junitInfo1)) {
						line = output.readLine();
						line = line.substring(1);
					}
					// Checks if it has reached at the end of the test method
					else if (endTestMethod) {
						endTestMethod = false;
						
						// If line is not empty, removes dot added by the execution
						if (line.length() > 0)
							line = line.substring(1);
						// If line is empty, current line should be ignored
						else if (line.length() == 0)
							continue;
					}
					
					// If it is a internal message, skip it and the next
					if ( line.matches(regex_junitInfo2) || 
						 line.matches(regex_junitInfo3) )
						line = output.readLine();
					// Checks if it has reached at the end of the test method
					else if (line.matches(regex_endMethod))
						endTestMethod = true;
					else
						System.out.println(line);
				}
				
				
				output.close();
				outputError.close();
				p.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Puts a delimiter indicating that it has reached the end of the test
	 * method.
	 */
	public static void putEndDelimiter()
	{
		System.out.println(DELIMITER_END_TEST_METHOD);
	}
}
