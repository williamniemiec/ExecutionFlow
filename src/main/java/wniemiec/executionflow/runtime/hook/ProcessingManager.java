package wniemiec.executionflow.runtime.hook;

import java.io.IOException;

import wniemiec.executionflow.invoked.Invoked;
import wniemiec.executionflow.io.processing.file.ProcessorType;
import wniemiec.executionflow.io.processing.file.factory.PreTestMethodFileProcessorFactory;
import wniemiec.executionflow.io.processing.manager.FileProcessingManager;
import wniemiec.executionflow.io.processing.manager.FilesProcessingManager;
import wniemiec.executionflow.io.processing.manager.InvokedProcessingManager;
import wniemiec.util.logger.Logger;

public class ProcessingManager {

//	private static InvokedManager processingManager;
	private static InvokedProcessingManager preTestMethodProcessingManager;
	private static InvokedProcessingManager testMethodProcessingManager;
	private static InvokedProcessingManager invokedProcessingManager;
	private static FileProcessingManager preTestMethodFileManager;
//	private static FilesManager testMethodManager;
//	private static FileManager testMethodFileManager;
	private static final boolean AUTO_RESTORE;
	private static boolean successfullPreprocessing;
	
	static {
		onShutdown();
		AUTO_RESTORE = true;
		successfullPreprocessing = false;
	}
	
	private static void onShutdown() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() {
		    	try {
		    		restoreOriginalFiles();
			    	deleteInvokedBackupFiles();
			    	deleteTestMethodBackupFiles();
		    	}
		    	catch (Throwable t) {
		    	}
		    }
		});
	}
	
	public static void initializeManagers(boolean restoreOriginalFiles) {
		try {
			//processingManager = new InvokedManager(restoreOriginalFiles);
			
			preTestMethodProcessingManager = new InvokedProcessingManager(
					initializePreTestMethodManager(),
					restoreOriginalFiles
			);
			
			testMethodProcessingManager = new InvokedProcessingManager(
					initializeTestMethodManager(restoreOriginalFiles),
					restoreOriginalFiles
			);
			
			invokedProcessingManager = new InvokedProcessingManager(
					initializeInvokedManager(restoreOriginalFiles),
					restoreOriginalFiles
			);
			
//			testMethodProcessingManager = new InvokedProcessingManager(initializeTestMethodManager(), invokedFileManager)
//			initializeFileManager();
		}
		catch(IOException | ClassNotFoundException | NoClassDefFoundError e) {
			Logger.error(e.getMessage());
			
			System.exit(-1);
		}
	}
	
	public static FilesProcessingManager initializePreTestMethodManager() 
			throws ClassNotFoundException, IOException {
		return new FilesProcessingManager(
				ProcessorType.PRE_TEST_METHOD, 
				false, 
				true
		);
	}
	
	private static FilesProcessingManager initializeTestMethodManager(boolean restoreOriginalFiles)
			throws ClassNotFoundException, IOException {
		return new FilesProcessingManager(
				ProcessorType.TEST_METHOD, 
				true, 
				restoreOriginalFiles
		);
	}

	private static FilesProcessingManager initializeInvokedManager(boolean restoreOriginalFiles)
			throws ClassNotFoundException, IOException {
		FilesProcessingManager invokedManager = new FilesProcessingManager(
				ProcessorType.INVOKED, 
				true, 
				restoreOriginalFiles
		);
		
		if (!restoreOriginalFiles)
			invokedManager.load();
		
		return invokedManager;
	}
	
	private static void initializePreTestMethodFileManager(Invoked testMethod) {	
		preTestMethodFileManager = new FileProcessingManager.Builder()
				.srcPath(testMethod.getSrcPath())
				.binDirectory(Invoked.getCompiledFileDirectory(testMethod.getBinPath()))
				.classPackage(Invoked.extractPackage(testMethod.getClassSignature()))
				.backupExtensionName("pretestmethod.bkp")
				.fileParserFactory(new PreTestMethodFileProcessorFactory(
						testMethod.getInvokedSignature(), 
						testMethod.getArgs()
				))
				.build();
	}
	
	
	
	
	
	public static void restoreAllTestMethodFiles() throws IOException {
		if (preTestMethodProcessingManager == null)
			return;

		preTestMethodProcessingManager.restoreInvokedOriginalFiles();
		
		deleteTestMethodBackupFiles();
	}
	
	
	
	public static void doPreprocessingInTestMethod(Invoked testMethod) throws IOException {
		initializePreTestMethodFileManager(testMethod);
		
		try {
			Logger.info("Pre-processing test method...");
			
			preTestMethodProcessingManager.processAndCompile(preTestMethodFileManager, AUTO_RESTORE);
			
			Logger.info("Pre-processing completed");
		}
		catch (IOException e) {
			Logger.error(e.getMessage());
			
			if (preTestMethodProcessingManager != null) {
				preTestMethodProcessingManager.restoreInvokedOriginalFiles();
				deleteTestMethodBackupFiles();
			}
			
			throw e;
		}
		
		successfullPreprocessing = true;
	}
	
	private static void restoreOriginalFiles() {
		restoreOriginalFilesFromTestMethod();
		restoreOriginalFilesFromInvoked();
		restoreOriginalFilesFrom(preTestMethodProcessingManager);
	}
	
	public static boolean restoreOriginalFilesFromTestMethod() {
		return restoreOriginalFilesFrom(testMethodProcessingManager);
	}
	
	public static boolean restoreOriginalFilesFromInvoked() {
		return restoreOriginalFilesFrom(invokedProcessingManager);
	}
	
	private static boolean restoreOriginalFilesFrom(InvokedProcessingManager processingManager) {
		if (processingManager == null)
			return true;
		
		boolean success = true;
		
		try {
			processingManager.restoreInvokedOriginalFiles();
		}
		catch (IOException e) {
			success = false;
			
			Logger.error(e.getMessage());
			Logger.error("Could not recover backup files.");
			Logger.error("See more: https://github.com/williamniemiec/"
					+ "ExecutionFlow/wiki/Solu%C3%A7%C3%A3o-de-problemas"
					+ "#could-not-recover-all-backup-files");
		}
		catch (NullPointerException e) {
			success = false;
		}
		
		return success;
	}
	
	public static void deleteTestMethodBackupFiles() {
		if ((testMethodProcessingManager != null) && 
				testMethodProcessingManager.isInvokedFilesManagerInitialized())
			testMethodProcessingManager.deleteBackupFiles();
		
		if (preTestMethodProcessingManager != null) {
			preTestMethodProcessingManager.deleteBackupFiles();
			preTestMethodProcessingManager = null;
		}
		
		testMethodProcessingManager.destroyInvokedFilesManager();
	}
	
	
	public static void deleteInvokedBackupFiles() {
		if (invokedProcessingManager == null)
			return;
		
		invokedProcessingManager.deleteBackupFiles();
	}
	
	public static void doProcessingInInvoked(FileProcessingManager invokedFileManager, FileProcessingManager testMethodFileManager) 
			throws IOException {
		if (invokedProcessingManager == null)
			return;
		
		invokedProcessingManager.processAndCompile(
				invokedFileManager, 
				testMethodFileManager.getSrcFile().equals(invokedFileManager.getSrcFile())
//				!isTestMethodFileAndInvokedFileTheSameFile(invokedFileManager)
		);
	}
	
	private static boolean isTestMethodFileAndInvokedFileTheSameFile(FileProcessingManager invokedFileManager) {
		return	preTestMethodFileManager.getSrcFile()
				.equals(invokedFileManager.getSrcFile());
	}
	
	public static void doProcessingInTestMethod(FileProcessingManager testMethodFileManager) 
			throws IOException {
		if (testMethodProcessingManager == null)
			return;
		
		testMethodProcessingManager.processAndCompile(testMethodFileManager, AUTO_RESTORE);
	}
	
	public static void restoreTestMethodToBeforeProcessing(FileProcessingManager testMethodFileManager) {
		if (testMethodProcessingManager == null)
			return;
		
		testMethodProcessingManager.restoreInvokedOriginalFile(testMethodFileManager);
	}
	
	public static void restoreInvokedToBeforeProcessing(FileProcessingManager invokedFileManager) {
		if (invokedProcessingManager == null)
			return;
		
		invokedProcessingManager.restoreInvokedOriginalFile(invokedFileManager);
	}
	
	public static boolean wasPreprocessingDoneSuccessfully() {
		return successfullPreprocessing;
	}
}
