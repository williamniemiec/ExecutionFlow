package wniemiec.executionflow.io.processing.manager;

import java.io.IOException;

import wniemiec.executionflow.invoked.Invoked;
import wniemiec.executionflow.invoked.TestedInvoked;
import wniemiec.executionflow.io.processing.file.ProcessorType;
import wniemiec.executionflow.io.processing.file.factory.InvokedFileProcessorFactory;
import wniemiec.executionflow.io.processing.file.factory.PreTestMethodFileProcessorFactory;
import wniemiec.executionflow.io.processing.file.factory.TestMethodFileProcessorFactory;
import wniemiec.util.logger.Logger;

public class ProcessingManager {

	private static ProcessingManager instance;
	private InvokedProcessingManager preTestMethodProcessingManager;
	private InvokedProcessingManager testMethodProcessingManager;
	private InvokedProcessingManager invokedProcessingManager;
	private FileProcessingManager preTestMethodFileManager;
	private final boolean AUTO_RESTORE;
	private boolean successfullPreprocessing;
	private FileProcessingManager currentInvokedFileManager;
	private FileProcessingManager currentTestMethodFileManager;
	
	private Invoked currentTestedInvoked;
	private Invoked currentTestMethod;
	private CollectorProcessingManager collectorProcessingManager;
	
	
	private ProcessingManager() {
		onShutdown();
		AUTO_RESTORE = true;
		successfullPreprocessing = false;
		collectorProcessingManager = CollectorProcessingManager.getInstance();
	}
	
	
	public static ProcessingManager getInstance() {
		if (instance == null)
			instance = new ProcessingManager();
		
		return instance;
	}
	
	private void onShutdown() {
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
	
	public void initializeManagers(boolean restoreOriginalFiles) {
		try {
			initializeProcessingManagers(restoreOriginalFiles);
		}
		catch(IOException | ClassNotFoundException | NoClassDefFoundError e) {
			Logger.error(e.toString());
			
			System.exit(-1);
		}
	}

	private void initializeProcessingManagers(boolean restoreOriginalFiles) 
			throws ClassNotFoundException, IOException {
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
	}
	
	private FilesProcessingManager initializePreTestMethodManager() 
			throws ClassNotFoundException, IOException {
		return new FilesProcessingManager(
				ProcessorType.PRE_TEST_METHOD,
				true
		);
	}
	
	private FilesProcessingManager initializeTestMethodManager(boolean restoreOriginalFiles)
			throws ClassNotFoundException, IOException {
		return new FilesProcessingManager(
				ProcessorType.TEST_METHOD,
				restoreOriginalFiles
		);
	}

	private FilesProcessingManager initializeInvokedManager(boolean restoreOriginalFiles)
			throws ClassNotFoundException, IOException {
		FilesProcessingManager invokedManager = new FilesProcessingManager(
				ProcessorType.INVOKED,
				restoreOriginalFiles
		);
		
		if (!restoreOriginalFiles)
			invokedManager.loadBackup();
		
		return invokedManager;
	}
	
	private void initializePreTestMethodFileManager(Invoked testMethod) {
		preTestMethodFileManager = new FileProcessingManager.Builder()
				.srcPath(testMethod.getSrcPath())
				.binPath(testMethod.getBinPath())
				.filePackage(Invoked.extractPackageFromClassSignature(testMethod.getClassSignature()))
				.backupExtensionName("pretestmethod.bkp")
				.fileProcessorFactory(new PreTestMethodFileProcessorFactory(
						testMethod.getInvokedSignature(), 
						testMethod.getArgs()
				))
				.build();
	}
	
	public void restoreAllTestMethodFiles() throws IOException {
		if (preTestMethodProcessingManager == null)
			return;

		preTestMethodProcessingManager.restoreInvokedOriginalFiles();
		
		deleteTestMethodBackupFiles();
	}
	
	
	
	public void doPreprocessingInTestMethod(Invoked testMethod) throws IOException {
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
	
	private void restoreOriginalFiles() {
		restoreOriginalFilesFromTestMethod();
		restoreOriginalFilesFromInvoked();
		restoreOriginalFilesFrom(preTestMethodProcessingManager);
	}
	
	public boolean restoreOriginalFilesFromTestMethod() {
		return restoreOriginalFilesFrom(testMethodProcessingManager);
	}
	
	public boolean restoreOriginalFilesFromInvoked() {
		return restoreOriginalFilesFrom(invokedProcessingManager);
	}
	
	private boolean restoreOriginalFilesFrom(InvokedProcessingManager processingManager) {
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
	
	public void deleteTestMethodBackupFiles() {
		if ((testMethodProcessingManager != null) && 
				testMethodProcessingManager.isInvokedFilesManagerInitialized())
			testMethodProcessingManager.deleteBackupFiles();
		
		if (preTestMethodProcessingManager != null) {
			preTestMethodProcessingManager.deleteBackupFiles();
			preTestMethodProcessingManager = null;
		}
		
		testMethodProcessingManager.destroyInvokedFilesManager();
	}
	
	
	public void deleteInvokedBackupFiles() {
		if (invokedProcessingManager == null)
			return;
		
		invokedProcessingManager.deleteBackupFiles();
	}
	
	private void doProcessingInInvoked(FileProcessingManager invokedFileManager, 
									   FileProcessingManager testMethodFileManager) 
			throws IOException {
		if (invokedProcessingManager == null)
			return;
		
		invokedProcessingManager.processAndCompile(
				invokedFileManager, 
				isTestMethodFileAndInvokedFileTheSameFile()
		);
	}
	
	private boolean isTestMethodFileAndInvokedFileTheSameFile() {
		return	currentTestMethod.getSrcPath()
				.equals(currentTestedInvoked.getSrcPath());
	}
	
	private void doProcessingInTestMethod(FileProcessingManager testMethodFileManager) 
			throws IOException {
		if (testMethodProcessingManager == null)
			return;
		
		testMethodProcessingManager.processAndCompile(testMethodFileManager, AUTO_RESTORE);
	}
	
	private void restoreTestMethodToBeforeProcessing(FileProcessingManager testMethodFileManager) {
		if (testMethodProcessingManager == null)
			return;
		
		testMethodProcessingManager.restoreInvokedOriginalFile(testMethodFileManager);
	}
	
	private void restoreInvokedToBeforeProcessing(FileProcessingManager invokedFileManager) {
		if (invokedProcessingManager == null)
			return;
		
		invokedProcessingManager.restoreInvokedOriginalFile(invokedFileManager);
	}
	
	public boolean wasPreprocessingDoneSuccessfully() {
		return successfullPreprocessing;
	}
	
	public void doProcessingInTestedInvoked(TestedInvoked collector) throws IOException {
		currentTestMethod = collector.getTestMethod();
		currentTestedInvoked = collector.getTestedInvoked();
		currentTestMethodFileManager = createTestMethodFileManager(collector.getTestMethod());
		currentInvokedFileManager = createInvokedFileManager(collector.getTestedInvoked());
		
		processTestMethod(collector);
		processInvokedMethod(collector);
	}
	
	private FileProcessingManager createTestMethodFileManager(Invoked testMethod) {
		return new FileProcessingManager.Builder()
				.srcPath(testMethod.getSrcPath())
				.binPath(testMethod.getBinPath())
				.filePackage(testMethod.getPackage())
				.backupExtensionName("testMethod.bkp")
				.fileProcessorFactory(new TestMethodFileProcessorFactory())
				.build();
	}
	
	private FileProcessingManager createInvokedFileManager(Invoked invoked) {
		return new FileProcessingManager.Builder()
				.srcPath(invoked.getSrcPath())
				.binPath(invoked.getBinPath())
				.filePackage(invoked.getPackage())
				.backupExtensionName("invoked.bkp")
				.fileProcessorFactory(new InvokedFileProcessorFactory())
				.build();
	}

	private void processInvokedMethod(TestedInvoked collector) throws IOException {
		Logger.info("Processing source file of invoked - " 
				+ collector.getTestedInvoked().getConcreteSignature() 
				+ "..."
		);
		
		doProcessingInInvoked(currentInvokedFileManager, currentTestMethodFileManager);
		
		collectorProcessingManager.refreshInvocationLineAfterInvokedProcessing(collector);
		
		Logger.info("Processing completed");
	}
	
	public void resetLastProcessing() {
		if (currentTestMethodFileManager != null)
			restoreTestMethodToBeforeProcessing(currentTestMethodFileManager);
		
		if (currentInvokedFileManager != null)
			restoreInvokedToBeforeProcessing(currentInvokedFileManager);
		
		collectorProcessingManager.reset();
		currentInvokedFileManager = null;
		currentTestMethodFileManager = null;
	}
	
	private void processTestMethod(TestedInvoked collector) throws IOException {
		Logger.info(
				"Processing source file of test method "
				+ collector.getTestMethod().getConcreteSignature() 
				+ "..."
		);
		
		doProcessingInTestMethod(currentTestMethodFileManager);
		
		collectorProcessingManager.refreshInvocationLineAfterTestMethodProcessing(collector);
		
		Logger.info("Processing completed");
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
