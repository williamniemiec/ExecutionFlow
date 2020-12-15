package executionFlow.constructorExecutionFlow;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import executionFlow.RemoteControl;
import executionFlow.ExecutionFlow;
import executionFlow.info.InvokedInfo;
import executionFlow.io.FileManager;
import executionFlow.io.FilesManager;
import executionFlow.io.ProcessingManager;
import executionFlow.io.ProcessorType;
import executionFlow.io.processor.factory.PreTestMethodFileProcessorFactory;
import executionFlow.util.Logger;


/**
 * Helper class for tests related to {@link executionFlow.MethodExecutionFlow}.
 */
public class ConstructorExecutionFlowTest 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	protected static FileManager testMethodFileManager;
	protected static FilesManager testMethodManager;
	protected static ProcessingManager processingManager;
		
	
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
		processingManager = new ProcessingManager(false);
		processingManager.destroyTestMethodManager();
		processingManager = new ProcessingManager(true);
//		ExecutionFlow.init(false);
//		ExecutionFlow.destroyTestMethodManager();
//		ExecutionFlow.init(true);
				
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
				InvokedInfo.getCompiledFileDirectory(binTestMethod),
				packageTestMethod,
				new PreTestMethodFileProcessorFactory(testMethodSignature),
				"original_pre_processing"
			);
		}
		else {
			testMethodFileManager = new FileManager(
				classSignature,
				srcTestMethod,
				InvokedInfo.getCompiledFileDirectory(binTestMethod),
				packageTestMethod,
				new PreTestMethodFileProcessorFactory(testMethodSignature, testMethodArgs),
				"original_pre_processing"
			);
		}
		
		// Parses test method
		try {
			Logger.info("Pre-processing test method...");
			testMethodManager.parse(testMethodFileManager).compile(testMethodFileManager);
			Logger.info("Pre-processing completed");
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
	
	@After
	public void restoreTestMethod() throws ClassNotFoundException, IOException
	{
		if (!processingManager.isTestMethodManagerInitialized())
			return;
		
		processingManager.restoreTestMethodOriginalFiles();
//		if (ExecutionFlow.getTestMethodManager().load())
//			ExecutionFlow.getTestMethodManager().restoreAll();	
		
		testMethodManager.restoreAll();
		
		//ExecutionFlow.getTestMethodManager().deleteBackup();
		processingManager.deleteTestMethodFileManagerBackup();
		testMethodManager.deleteBackup();
		testMethodManager = null;
		processingManager.destroyTestMethodManager();
//		ExecutionFlow.destroyTestMethodManager();		
	}
	
	@BeforeClass
	public static void control()
	{
		RemoteControl.open();
	}
	
	@AfterClass
	public static void restoreInvoked() throws ClassNotFoundException, IOException
	{
		if (!processingManager.isInvokedManagerInitialized())
			return;
		
		processingManager.restoreInvokedOriginalFiles();
		processingManager.deleteInvokedFileManagerBackup();
		processingManager.destroyInvokedManager();
//		ExecutionFlow.getInvokedManager().deleteBackup();
		
//		ExecutionFlow.destroyInvokedManager();
		
		RemoteControl.close();
	}
}
