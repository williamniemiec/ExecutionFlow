package wniemiec.executionflow.io.processing.manager;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import wniemiec.executionflow.App;
import wniemiec.executionflow.collector.ConstructorCollector;
import wniemiec.executionflow.collector.MethodCollector;
import wniemiec.executionflow.invoked.Invoked;
import wniemiec.executionflow.invoked.TestedInvoked;
import wniemiec.executionflow.io.processing.file.InvokedFileProcessor;
import wniemiec.executionflow.io.processing.file.ProcessorType;
import wniemiec.executionflow.io.processing.file.TestMethodFileProcessor;
import wniemiec.executionflow.io.processing.file.factory.InvokedFileProcessorFactory;
import wniemiec.executionflow.io.processing.file.factory.PreTestMethodFileProcessorFactory;
import wniemiec.executionflow.io.processing.file.factory.TestMethodFileProcessorFactory;
import wniemiec.util.logger.Logger;

public class ProcessingManager {

	private static InvokedProcessingManager preTestMethodProcessingManager;
	private static InvokedProcessingManager testMethodProcessingManager;
	private static InvokedProcessingManager invokedProcessingManager;
	private static FileProcessingManager preTestMethodFileManager;
	private static final boolean AUTO_RESTORE;
	private static boolean successfullPreprocessing;
	private static FileProcessingManager currentInvokedFileManager;
	private static FileProcessingManager currentTestMethodFileManager;
	private static Set<String> alreadyChanged;
	private static Invoked currentTestedInvoked;
	private static Invoked currentTestMethod;
	
	static {
		onShutdown();
		AUTO_RESTORE = true;
		successfullPreprocessing = false;
		alreadyChanged = new HashSet<>();
	}
	
	private ProcessingManager() {
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
				.binPath(testMethod.getBinPath())
				.filePackage(Invoked.extractPackageFromClassSignature(testMethod.getClassSignature()))
				.backupExtensionName("pretestmethod.bkp")
				.fileProcessorFactory(new PreTestMethodFileProcessorFactory(
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
	
	private static void doProcessingInInvoked(FileProcessingManager invokedFileManager, FileProcessingManager testMethodFileManager) 
			throws IOException {
		if (invokedProcessingManager == null)
			return;
		
		invokedProcessingManager.processAndCompile(
				invokedFileManager, 
				currentTestMethod.getSrcPath().equals(currentTestedInvoked.getSrcPath())
//				!isTestMethodFileAndInvokedFileTheSameFile(invokedFileManager)
		);
	}
	
	private static boolean isTestMethodFileAndInvokedFileTheSameFile(FileProcessingManager invokedFileManager) {
		return	currentTestMethod.getSrcPath()
				.equals(currentTestedInvoked.getSrcPath());
	}
	
	private static void doProcessingInTestMethod(FileProcessingManager testMethodFileManager) 
			throws IOException {
		if (testMethodProcessingManager == null)
			return;
		
		testMethodProcessingManager.processAndCompile(testMethodFileManager, AUTO_RESTORE);
	}
	
	private static void restoreTestMethodToBeforeProcessing(FileProcessingManager testMethodFileManager) {
		if (testMethodProcessingManager == null)
			return;
		
		testMethodProcessingManager.restoreInvokedOriginalFile(testMethodFileManager);
	}
	
	private static void restoreInvokedToBeforeProcessing(FileProcessingManager invokedFileManager) {
		if (invokedProcessingManager == null)
			return;
		
		invokedProcessingManager.restoreInvokedOriginalFile(invokedFileManager);
	}
	
	public static boolean wasPreprocessingDoneSuccessfully() {
		return successfullPreprocessing;
	}
	
	public static void doProcessingInTestedInvoked(TestedInvoked collector) throws IOException {
		currentTestMethod = collector.getTestMethod();
		currentTestedInvoked = collector.getTestedInvoked();
		currentTestMethodFileManager = createTestMethodFileManager(collector.getTestMethod());
		currentInvokedFileManager = createInvokedFileManager(collector.getTestedInvoked());
		
		processTestMethod(collector);
		processInvokedMethod(collector);
	}
	
	private static FileProcessingManager createTestMethodFileManager(Invoked testMethod) {
		return new FileProcessingManager.Builder()
				.srcPath(testMethod.getSrcPath())
				.binPath(testMethod.getBinPath())
				.filePackage(testMethod.getPackage())
				.backupExtensionName("testMethod.bkp")
				.fileProcessorFactory(new TestMethodFileProcessorFactory())
				.build();
	}
	
	private static Path getBinDirectory(Path binPath) {
		return binPath.getParent();
	}

	private static FileProcessingManager createInvokedFileManager(Invoked invoked) {
		return new FileProcessingManager.Builder()
				.srcPath(invoked.getSrcPath())
				.binPath(invoked.getBinPath())
				.filePackage(invoked.getPackage())
				.backupExtensionName("invoked.bkp")
				.fileProcessorFactory(new InvokedFileProcessorFactory())
				.build();
	}

	private static void processInvokedMethod(TestedInvoked collector) throws IOException {
		Logger.info("Processing source file of invoked - " 
				+ collector.getTestedInvoked().getConcreteSignature() 
				+ "..."
		);
		
		doProcessingInInvoked(currentInvokedFileManager, currentTestMethodFileManager);
		
		updateInvocationLineAfterInvokedProcessing(collector);
		
		Logger.info("Processing completed");
	}
	
	private static void processTestMethod(TestedInvoked collector) throws IOException {
		Logger.info(
				"Processing source file of test method "
				+ collector.getTestMethod().getConcreteSignature() 
				+ "..."
		);
		
		ProcessingManager.doProcessingInTestMethod(currentTestMethodFileManager);
		
		updateInvocationLineAfterTestMethodProcessing(collector);
		
		Logger.info("Processing completed");
	}
	
	private static void updateInvocationLineAfterInvokedProcessing(TestedInvoked collector) {
		if (App.isTestMode()) {
			if (collector.getTestedInvoked().getSrcPath().equals(
					collector.getTestMethod().getSrcPath())) {
				updateCollector(collector, InvokedFileProcessor.getMapping());
			}
		}
		else {
			updateCollectors(
					InvokedFileProcessor.getMapping(),
					collector.getTestMethod().getSrcPath(), 
					collector.getTestedInvoked().getSrcPath()
			);
		}
	}

	private static void updateCollector(TestedInvoked collector, Map<Integer, Integer> mapping) {
		int invocationLine = collector.getTestedInvoked().getInvocationLine();
		
		if (mapping.containsKey(invocationLine))
			collector.getTestedInvoked().setInvocationLine(mapping.get(invocationLine));
	}

	private static void updateCollectors(Map<Integer, Integer> mapping, Path testMethodSrcPath,
								  Path invokedSrcPath) {
		if (alreadyChanged.contains(testMethodSrcPath.toString()) && 
				!invokedSrcPath.equals(testMethodSrcPath))
			return;

		ConstructorCollector.updateInvocationLines(
				mapping, 
				testMethodSrcPath
		);
		
		MethodCollector.updateInvocationLines(
				mapping, 
				testMethodSrcPath
		);
		
		alreadyChanged.add(testMethodSrcPath.toString());
	}

	

	private static void updateInvocationLineAfterTestMethodProcessing(TestedInvoked collector) {
		if (App.isTestMode()) {
			updateCollector(collector, TestMethodFileProcessor.getMapping());
		}
		else {
			updateCollectors(
					TestMethodFileProcessor.getMapping(),
					collector.getTestMethod().getSrcPath(), 
					collector.getTestedInvoked().getSrcPath()
			);
		}
	}
	
	public static void resetLastProcessing() {
		if (currentTestMethodFileManager != null)
			restoreTestMethodToBeforeProcessing(currentTestMethodFileManager);
		
		if (currentInvokedFileManager != null)
			restoreInvokedToBeforeProcessing(currentInvokedFileManager);
		
		alreadyChanged.clear();
		currentInvokedFileManager = null;
		currentTestMethodFileManager = null;
	}
}
