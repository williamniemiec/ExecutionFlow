package executionFlow.methodExecutionFlow;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import executionFlow.Control;
import executionFlow.ExecutionFlow;
import executionFlow.info.MethodInvokedInfo;
import executionFlow.io.FileManager;
import executionFlow.io.FilesManager;
import executionFlow.io.ProcessorType;
import executionFlow.io.processor.factory.PreTestMethodFileProcessorFactory;
import executionFlow.util.ConsoleOutput;


/**
 * Helper class for tests related to {@link executionFlow.MethodExecutionFlow}.
 */
public class MethodExecutionFlowTest 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	protected static FileManager testMethodFileManager;
	protected static FilesManager testMethodManager;
		
	
	//-------------------------------------------------------------------------
	//		Test preparers
	//-------------------------------------------------------------------------
	/**
	 * @param		classSignature Test class signature
	 * @param		testMethodSignature Test method signature
	 * @param		srcTestMethod Test method compiled file path
	 * @param		binTestMethod Test method source file path
	 * @param		packageTestMethod Test method package
	 * @param		testMethodArgs Test method arguments (when it is a 
	 * parameterized test)
	 * 
	 * @throws		IOException If an error occurs during file parsing
	 * @throws		ClassNotFoundException If class {@link FileManager} was not
	 * found
	 */
	protected void init(String classSignature, String testMethodSignature, 
			Path srcTestMethod, Path binTestMethod, String packageTestMethod, 
			Object... testMethodArgs) throws IOException, ClassNotFoundException
	{
		// Initializes ExecutionFlow and loads invoked manager
		ExecutionFlow.init(false);
		ExecutionFlow.destroyTestMethodManager();
		ExecutionFlow.init(true);
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() 
			{
				try {
					restoreTestMethod();
					restoreInvoked();
				} 
				catch (ClassNotFoundException | IOException e) 
				{}
			}
		});
				
		// Creates backup from original files
		testMethodManager = new FilesManager(ProcessorType.PRE_TEST_METHOD, false);
		
		if (testMethodArgs == null) {
			testMethodFileManager = new FileManager(
				classSignature,
				srcTestMethod,
				MethodInvokedInfo.getCompiledFileDirectory(binTestMethod),
				packageTestMethod,
				new PreTestMethodFileProcessorFactory(testMethodSignature),
				"original_pre_processing"
			);
		}
		else {
			testMethodFileManager = new FileManager(
				classSignature,
				srcTestMethod,
				MethodInvokedInfo.getCompiledFileDirectory(binTestMethod),
				packageTestMethod,
				new PreTestMethodFileProcessorFactory(testMethodSignature, testMethodArgs),
				"original_pre_processing"
			);
		}
		
		// Parses test method
		try {
			ConsoleOutput.showInfo("Pre-processing test method...");
			testMethodManager.parse(testMethodFileManager).compile(testMethodFileManager);
			ConsoleOutput.showInfo("Pre-processing completed");
		} catch (IOException e) {
			testMethodManager.restoreAll();
			testMethodManager.deleteBackup();
			throw e;
		}
	}
	
	/**
	 * @param		classSignature Test class signature
	 * @param		testMethodSignature Test method signature
	 * @param		srcTestMethod Test method compiled file path
	 * @param		binTestMethod Test method source file path
	 * @param		packageTestMethod Test method package
	 * @param		testMethodArgs Test method arguments (when it is a parameterized test)
	 * 
	 * @throws		IOException If an error occurs during file parsing
	 * @throws		ClassNotFoundException If class {@link FileManager} was not
	 * found
	 */
	protected void init(String classSignature, String testMethodSignature, 
			Path srcTestMethod, Path binTestMethod, String packageTestMethod) 
			throws IOException, ClassNotFoundException
	{
		init(classSignature, testMethodSignature, srcTestMethod, binTestMethod, 
				packageTestMethod, (Object[])null);
	}
	
	@BeforeClass
	public static void control()
	{
		Control.open();
	}
	
	@After
	public void restoreTestMethod() throws ClassNotFoundException, IOException
	{
		if (ExecutionFlow.getTestMethodManager() == null)
			return;
		
		if (ExecutionFlow.getTestMethodManager().load())
			ExecutionFlow.getTestMethodManager().restoreAll();	
		
		testMethodManager.restoreAll();
		
		ExecutionFlow.getTestMethodManager().deleteBackup();
		testMethodManager.deleteBackup();
		testMethodManager = null;
		ExecutionFlow.destroyTestMethodManager();		
	}
	
	/**
	 * Restores original files
	 */
	@AfterClass
	public static void restoreInvoked() throws ClassNotFoundException, IOException
	{
		if (ExecutionFlow.getInvokedManager() == null)
			return;
		
		if (ExecutionFlow.getInvokedManager().load())
			ExecutionFlow.getInvokedManager().restoreAll();	
		
		ExecutionFlow.getInvokedManager().deleteBackup();
		
		ExecutionFlow.destroyInvokedManager();
		
		Control.close();
	}
}
