package executionFlow.runtime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;

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
	public boolean assertParser(String testSrcPath) throws IOException
	{
		testMethodFileManager = new FileManager(
			testSrcPath,
			ClassMethodInfo.getTestClassDirectory(testClassPath).toString(),
			testClassPackage,
			new AssertFileParserFactory()
		);
		
		if (testMethodFileManager.hasClassBackupStored()) 
			return false;
		
		
		try {
			testMethodFileManager.createClassBackupFile()
				.parseFile()
				.compileFile();
		} catch(IOException e) {
			testMethodFileManager.revertCompilation();
			testMethodFileManager.revertParse();
			e.printStackTrace();
		} finally {
			testMethodFileManager.revertParse();
		}
		
		return true;
	}
	
	/**
	 * Runs the test method in a new process. It is necessary for the aspects 
	 * to collect methods based on the pre-processing performed by method
	 * {@link #assertParser(String)}.
	 * 
	 * @param		testClassName Class name containing the test method
	 */
	public void run(String testClassName)
	{	
		try {
			ProcessBuilder pb = new ProcessBuilder(
				"cmd.exe","/c","java "+"-classpath ..\\lib\\junit-4.13.jar;..\\lib\\hamcrest-all-1.3.jar;.;..\\lib\\aspectjrt-1.9.2.jar",
				"org.junit.runner.JUnitCore",testClassName
			);
			
			pb.directory(ClassMethodInfo.extractRootClassDirectory(testClassPath, testClassPackage).toFile());
			final String regex_junitInfo1 = "^JUnit version [0-9]+(\\.[0-9]+)?";
			final String regex_junitInfo2 = "^Time: [0-9]+(\\,[0-9]+)?";
			final String regex_junitInfo3 = "^OK \\([0-9]+\\ test\\)$";
			final String regex_endMethod = "^"+DELIMITER_END_TEST_METHOD+"$";
			boolean endOfMethod = false;
			
			try {
				Process p = pb.start();
				BufferedReader output = new BufferedReader(new InputStreamReader(p.getInputStream()));
				BufferedReader outputError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				
				String line;

				while (!endOfMethod && (line = output.readLine()) != null) {
					if (line.matches(regex_junitInfo1)) {
						line = output.readLine();
						line = line.substring(1);
					}
					
					// If it is a internal message, skip it and the next
					if ( line.matches(regex_junitInfo2) || 
						 line.matches(regex_junitInfo3) )
						line = output.readLine();
					else if (line.matches(regex_endMethod))
						endOfMethod = true;
					else
						System.out.println(line);
				}
				
				while ((line = outputError.readLine()) != null) {
					System.err.println(line);
				}
				
				output.close();
				outputError.close();
				p.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				testMethodFileManager.revertCompilation();				
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Puts a delimiter indicating that it has reached the end of the test
	 * method, being useful to indicate when the process should stop.
	 */
	public static void putEndDelimiter()
	{
		System.out.println(DELIMITER_END_TEST_METHOD);
	}
}
