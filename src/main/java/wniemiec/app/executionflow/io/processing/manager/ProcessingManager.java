package wniemiec.app.executionflow.io.processing.manager;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import wniemiec.app.executionflow.collector.ConstructorCollector;
import wniemiec.app.executionflow.collector.InvokedCollector;
import wniemiec.app.executionflow.collector.MethodCollector;
import wniemiec.app.executionflow.invoked.Invoked;
import wniemiec.app.executionflow.invoked.TestedInvoked;
import wniemiec.app.executionflow.io.processing.file.InvokedFileProcessor;
import wniemiec.app.executionflow.io.processing.file.ProcessorType;
import wniemiec.app.executionflow.io.processing.file.TestMethodFileProcessor;
import wniemiec.app.executionflow.io.processing.file.factory.InvokedFileProcessorFactory;
import wniemiec.app.executionflow.io.processing.file.factory.PreTestMethodFileProcessorFactory;
import wniemiec.app.executionflow.io.processing.file.factory.TestMethodFileProcessorFactory;
import wniemiec.util.logger.Logger;

/**
 * Responsible for processing test methods, methods and constructors. The
 * following processing is performed:
 * <ul>
 * 	<li>Preprocessing of test methods</li>
 * 	<li>Processing of test methods</li>
 * 	<li>Processing of tested methods and constructors</li>
 * </ul>
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		7.0.0
 * @since		7.0.0
 */
public class ProcessingManager {

	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static ProcessingManager instance;
	private final boolean autoRestore;
	private boolean successfullPreprocessing;
	private InvokedProcessingManager preTestMethodProcessingManager;
	private InvokedProcessingManager testMethodProcessingManager;
	private InvokedProcessingManager invokedProcessingManager;
	private FileProcessingManager preTestMethodFileManager;
	private FileProcessingManager currentInvokedFileManager;
	private FileProcessingManager currentTestMethodFileManager;
	private Invoked currentTestedInvoked;
	private Invoked currentTestMethod;
	private CollectorProcessingManager collectorProcessingManager;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	private ProcessingManager() {
		autoRestore = true;
		successfullPreprocessing = false;
		collectorProcessingManager = initializeCollectorProcessingManager();
		
		onShutdown();
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	private CollectorProcessingManager initializeCollectorProcessingManager() {
		Set<InvokedCollector> collectors = new HashSet<>(Set.of(
				MethodCollector.getInstance(),
				ConstructorCollector.getInstance()
		));
		
		return CollectorProcessingManager.getInstance(collectors);
	}
	
	private void onShutdown() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() {
		    	try {
		    		restoreOriginalFiles();
		    		deleteBackupFilesOfPreprocessingOfTestMethod();
			    	deleteBackupFilesOfProcessingOfInvoked();
			    	deleteBackupFilesOfProcessingOfTestMethod();
		    	}
		    	catch (Throwable t) {
		    	}
		    }
		});
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
	
	public void deleteBackupFilesOfProcessingOfTestMethod() {
		if (testMethodProcessingManager == null)
			return;
		
		if (testMethodProcessingManager.isInvokedFilesManagerInitialized())
			testMethodProcessingManager.deleteBackupFiles();
		
		testMethodProcessingManager.destroyInvokedFilesManager();
	}
	
	public void deleteBackupFilesOfPreprocessingOfTestMethod() {
		if (preTestMethodProcessingManager == null)
			return;
		
		preTestMethodProcessingManager.deleteBackupFiles();
		preTestMethodProcessingManager = null;
		successfullPreprocessing = false;
	}
	
	public void deleteBackupFilesOfProcessingOfInvoked() {
		if (invokedProcessingManager == null)
			return;
		
		invokedProcessingManager.deleteBackupFiles();
	}
	
	public static ProcessingManager getInstance() {
		if (instance == null)
			instance = new ProcessingManager();
		
		return instance;
	}
	
	public void initializeManagers() {
		initializeManagers(false);
	}
	
	public void initializeManagers(boolean restoreOriginalFiles) {
		try {
			initializeProcessingManagers(restoreOriginalFiles);
		}
		catch(IOException | NoClassDefFoundError e) {
			Logger.error(e.toString());
			
			System.exit(-1);
		}
	}

	private void initializeProcessingManagers(boolean restoreOriginalFiles) 
			throws IOException {
		initializePreTestMethodProcessingManager(restoreOriginalFiles);
		initializeTestMethodProcessingManager(restoreOriginalFiles);
		initializeInvokedProcessingManager(restoreOriginalFiles);
	}
	
	private void initializePreTestMethodProcessingManager(boolean restoreOriginalFiles) 
			throws IOException {
		preTestMethodProcessingManager = new InvokedProcessingManager(
				initializePreTestMethodManager(restoreOriginalFiles)
		);
	}
	
	private FilesProcessingManager initializePreTestMethodManager(boolean restoreOriginalFiles) 
			throws IOException {
		FilesProcessingManager manager = null;
		
		try {
			manager = new FilesProcessingManager(
					ProcessorType.PRE_TEST_METHOD,
					restoreOriginalFiles
			);
		} 
		catch (ClassNotFoundException e) {
			Logger.error(e.toString());
			System.exit(-1);
		}
		
		return manager;
	}
	
	private void initializeTestMethodProcessingManager(boolean restoreOriginalFiles) 
			throws IOException {
		testMethodProcessingManager = new InvokedProcessingManager(
				initializeTestMethodManager(restoreOriginalFiles)
		);
	}
	
	private FilesProcessingManager initializeTestMethodManager(boolean restoreOriginalFiles)
			throws IOException {
		FilesProcessingManager manager = null;
		
		try {
			manager = new FilesProcessingManager(
					ProcessorType.TEST_METHOD,
					restoreOriginalFiles
			);
		} 
		catch (ClassNotFoundException e) {
			Logger.error(e.toString());
			System.exit(-1);
		}
		
		return manager;
	}
	
	private void initializeInvokedProcessingManager(boolean restoreOriginalFiles) 
			throws IOException {
		invokedProcessingManager = new InvokedProcessingManager(
				initializeInvokedManager(restoreOriginalFiles)
		);
	}

	private FilesProcessingManager initializeInvokedManager(boolean restoreOriginalFiles)
			throws IOException {
		FilesProcessingManager manager = null;
		
		try {
			manager = new FilesProcessingManager(
					ProcessorType.INVOKED,
					restoreOriginalFiles
			);
			
			if (!restoreOriginalFiles)
				manager.loadBackup();
		} 
		catch (ClassNotFoundException e) {
			Logger.error(e.toString());
			System.exit(-1);
		}
		
		return manager;
	}
	
	public void undoPreprocessing() throws IOException {
		if (preTestMethodProcessingManager == null)
			return;

		preTestMethodProcessingManager.restoreInvokedOriginalFiles();
	}
	
	/**
	 * Performs the following processing:
	 * <ul>
	 * 	<li>Preprocessing of test methods</li>
	 * </ul>
	 * 
	 * @param		testMethod Test method to be processed
	 * 
	 * @throws		IOException If an error occurs while processing files
	 */
	public void doPreprocessingInTestMethod(Invoked testMethod) throws IOException {
		initializePreTestMethodFileManager(testMethod);
		
		try {
			Logger.info("Preprocessing test method...");
			
			preTestMethodProcessingManager.processAndCompile(
					preTestMethodFileManager, 
					autoRestore
			);
			
			Logger.info("Preprocessing completed");
		}
		catch (IOException e) {
			Logger.error(e.getMessage());
			
			if (preTestMethodProcessingManager != null) {
				preTestMethodProcessingManager.restoreInvokedOriginalFiles();
				deleteBackupFilesOfProcessingOfTestMethod();
			}
			
			throw e;
		}
		
		successfullPreprocessing = true;
	}
	
	private void initializePreTestMethodFileManager(Invoked testMethod) {
		preTestMethodFileManager = new FileProcessingManager.Builder()
				.srcPath(testMethod.getSrcPath())
				.binPath(testMethod.getBinPath())
				.filePackage(testMethod.getPackage())
				.backupExtensionName("pretestmethod.bkp")
				.fileProcessorFactory(new PreTestMethodFileProcessorFactory(
						testMethod.getInvokedSignature(), 
						testMethod.getArgs()
				))
				.build();
	}
	
	public boolean wasPreprocessingDoneSuccessfully() {
		return successfullPreprocessing;
	}
	
	/**
	 * Performs the following processing:
	 * <ul>
	 * 	<li>Processing of test methods</li>
	 * 	<li>Processing of tested methods and constructors</li>
	 * </ul>
	 * 
	 * @param		testedInvoked Tested invoked
	 * 
	 * @throws		IOException If an error occurs while processing files
	 */
	public void doProcessingInTestedInvoked(TestedInvoked testedInvoked) throws IOException {
		withTestedInvoked(testedInvoked);
		
		processTestMethod();
		processInvokedMethod();
	}
	
	private void withTestedInvoked(TestedInvoked testedInvoked) {
		currentTestMethod = testedInvoked.getTestMethod();
		currentTestedInvoked = testedInvoked.getTestedInvoked();
		currentTestMethodFileManager = createTestMethodFileManager(
				testedInvoked.getTestMethod()
		);
		currentInvokedFileManager = createInvokedFileManager(
				testedInvoked.getTestedInvoked()
		);
		
		if (testedInvoked.getTestedInvoked().isConstructor())
			ConstructorCollector.getInstance().collect(testedInvoked);
		else
			MethodCollector.getInstance().collect(testedInvoked);
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
	
	private void processTestMethod() throws IOException {
		Logger.info(
				"Processing source file of test method "
				+ currentTestMethod.getConcreteSignature() 
				+ "..."
		);
		
		doProcessingInTestMethod(currentTestMethodFileManager);

		collectorProcessingManager.updateCollectorsFromMapping(
				TestMethodFileProcessor.getMapping(),
				currentTestMethod.getSrcPath(),
				currentTestedInvoked.getSrcPath()
		);
		
		Logger.info("Processing completed");
	}

	private void doProcessingInTestMethod(FileProcessingManager testMethodFileManager) 
			throws IOException {
		if (testMethodProcessingManager == null)
			initializeTestMethodProcessingManager(true);
		
		testMethodProcessingManager.processAndCompile(testMethodFileManager, autoRestore);
	}
	
	private void processInvokedMethod() throws IOException {
		Logger.info("Processing source file of invoked - " 
				+ currentTestedInvoked.getConcreteSignature() 
				+ "..."
		);
		
		doProcessingInInvoked(currentInvokedFileManager);
		
		collectorProcessingManager.updateCollectorsFromMapping(
				InvokedFileProcessor.getMapping(),
				currentTestMethod.getSrcPath(),
				currentTestedInvoked.getSrcPath()
		);
		
		Logger.info("Processing completed");
	}
	
	private void doProcessingInInvoked(FileProcessingManager invokedFileManager) 
			throws IOException {
		if (invokedProcessingManager == null)
			initializeInvokedProcessingManager(true);
		
		invokedProcessingManager.processAndCompile(
				invokedFileManager, 
				isTestMethodFileAndInvokedFileTheSameFile()
		);
	}
	
	private boolean isTestMethodFileAndInvokedFileTheSameFile() {
		return	currentTestMethod.getSrcPath()
				.equals(currentTestedInvoked.getSrcPath());
	}
	
	public void undoLastProcessing() {
		if (currentTestMethodFileManager != null)
			undoTestMethodProcessing(currentTestMethodFileManager);
		
		if (currentInvokedFileManager != null)
			undoInvokedProcessing(currentInvokedFileManager);
		
		collectorProcessingManager.reset();
		currentInvokedFileManager = null;
		currentTestMethodFileManager = null;
	}
	
	private void undoTestMethodProcessing(FileProcessingManager testMethodFileManager) {
		if (testMethodProcessingManager == null)
			return;
		
		testMethodProcessingManager.restoreInvokedOriginalFile(testMethodFileManager);
	}
	
	private void undoInvokedProcessing(FileProcessingManager invokedFileManager) {
		if (invokedProcessingManager == null)
			return;
		
		invokedProcessingManager.restoreInvokedOriginalFile(invokedFileManager);
	}
}
