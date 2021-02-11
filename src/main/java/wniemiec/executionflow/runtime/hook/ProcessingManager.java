package wniemiec.executionflow.runtime.hook;

import java.io.IOException;

import wniemiec.executionflow.invoked.InvokedInfo;
import wniemiec.executionflow.io.manager.FileManager;
import wniemiec.executionflow.io.manager.FilesManager;
import wniemiec.executionflow.io.manager.InvokedManager;
import wniemiec.executionflow.io.manager.InvokedProcessingManager;
import wniemiec.executionflow.io.processor.ProcessorType;
import wniemiec.executionflow.io.processor.factory.PreTestMethodFileProcessorFactory;
import wniemiec.util.logger.Logger;

public class ProcessingManager {

//	private static InvokedManager processingManager;
	private static InvokedProcessingManager preTestMethodProcessingManager;
	private static InvokedProcessingManager testMethodProcessingManager;
	private static InvokedProcessingManager invokedProcessingManager;
	private static FileManager preTestMethodFileManager;
//	private static FilesManager testMethodManager;
//	private static FileManager testMethodFileManager;
	private static final boolean AUTO_RESTORE;
	
	static {
		onShutdown();
		AUTO_RESTORE = true;
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
	
	public static FilesManager initializePreTestMethodManager() 
			throws ClassNotFoundException, IOException {
		return new FilesManager(
				ProcessorType.PRE_TEST_METHOD, 
				false, 
				true
		);
	}
	
	private static FilesManager initializeTestMethodManager(boolean restoreOriginalFiles)
			throws ClassNotFoundException, IOException {
		return new FilesManager(
				ProcessorType.TEST_METHOD, 
				true, 
				restoreOriginalFiles
		);
	}

	private static FilesManager initializeInvokedManager(boolean restoreOriginalFiles)
			throws ClassNotFoundException, IOException {
		FilesManager invokedManager = new FilesManager(
				ProcessorType.INVOKED, 
				true, 
				restoreOriginalFiles
		);
		
		if (!restoreOriginalFiles)
			invokedManager.load();
		
		return invokedManager;
	}
	
	private static void initializePreTestMethodFileManager(InvokedInfo testMethod) {	
		preTestMethodFileManager = new FileManager.Builder()
				.srcPath(testMethod.getSrcPath())
				.binDirectory(InvokedInfo.getCompiledFileDirectory(testMethod.getBinPath()))
				.classPackage(InvokedInfo.extractPackage(testMethod.getClassSignature()))
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
	
	
	
	public static void doPreprocessingInTestMethod(InvokedInfo testMethod) throws IOException {
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
	}
	
	private static void restoreOriginalFiles() {
		restoreOriginalFilesFrom(testMethodProcessingManager);
		restoreOriginalFilesFrom(invokedProcessingManager);
		restoreOriginalFilesFrom(preTestMethodProcessingManager);
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
	
	public static void doProcessingInInvoked(FileManager invokedFileManager) 
			throws IOException {
		if (invokedProcessingManager == null)
			return;
		
		invokedProcessingManager.processAndCompile(
				invokedFileManager, 
				!isTestMethodFileAndInvokedFileTheSameFile(invokedFileManager)
		);
	}
	
	private static boolean isTestMethodFileAndInvokedFileTheSameFile(FileManager invokedFileManager) {
		return	!preTestMethodFileManager.getSrcFile()
				.equals(invokedFileManager.getSrcFile());
	}
	
	public static void doProcessingInTestMethod(FileManager testMethodFileManager) 
			throws IOException {
		if (testMethodProcessingManager == null)
			return;
		
		testMethodProcessingManager.processAndCompile(testMethodFileManager, AUTO_RESTORE);
	}
	
	public static void restoreTestMethodToBeforeProcessing(FileManager testMethodFileManager) {
		if (testMethodProcessingManager == null)
			return;
		
		testMethodProcessingManager.restoreInvokedOriginalFile(testMethodFileManager);
	}
	
	public static void restoreInvokedToBeforeProcessing(FileManager invokedFileManager) {
		if (invokedProcessingManager == null)
			return;
		
		invokedProcessingManager.restoreInvokedOriginalFile(invokedFileManager);
	}
}
