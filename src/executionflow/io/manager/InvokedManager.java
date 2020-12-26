package executionflow.io.manager;

import java.io.IOException;

import executionflow.io.processor.ProcessorType;
import executionflow.util.logger.Logger;

/**
 * Responsible for managing the processing and compilation of methods, 
 * constructors and test methods.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		6.0.0
 * @since		6.0.0
 */
public class InvokedManager {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static FilesManager testMethodManager;
	private static FilesManager invokedManager;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	/**
	 * Initializes invoked managers. If some error occurs, should stop the
	 * application execution; otherwise, the original files that have been 
	 * modified in the last run may be lost.
	 * 
	 * @param		restoreOriginalFiles If true and if there are backup files,
	 * restore them
	 * @throws		ClassNotFoundException If FileManager class has not been
	 * found
	 * @throws		IOException If backup files could not be restored
	 */
	public InvokedManager(boolean restoreOriginalFiles) 
			throws ClassNotFoundException, IOException {
		try {
			initializeInvokedManagers(restoreOriginalFiles);
		}
		catch (ClassNotFoundException e) {
			throw new ClassNotFoundException("Class FileManager not found");
		} 
		catch (IOException e) {
			throw new IOException(
					"Could not recover all backup files\n"
					+ "See more: https://github.com/williamniemiec/"
					+ "ExecutionFlow/wiki/Solu%C3%A7%C3%A3o-de-problemas"
					+ "#could-not-recover-all-backup-files"
			);
		}
	}

	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	private void initializeInvokedManagers(boolean restoreOriginalFiles) 
			throws ClassNotFoundException, IOException {
		if (testMethodManager == null)
			initializeTestMethodManager(restoreOriginalFiles);
		
		if (invokedManager == null)
			initializeInvokedManager(restoreOriginalFiles);
	}
	
	private void initializeTestMethodManager(boolean restoreOriginalFiles)
			throws ClassNotFoundException, IOException {
		testMethodManager = new FilesManager(
				ProcessorType.TEST_METHOD, 
				true, 
				restoreOriginalFiles
		);
	}

	private void initializeInvokedManager(boolean restoreOriginalFiles)
			throws ClassNotFoundException, IOException {
		invokedManager = new FilesManager(
				ProcessorType.INVOKED, 
				true, 
				restoreOriginalFiles
		);
		
		if (!restoreOriginalFiles)
			invokedManager.load();
	}
	
	/**
	 * Sets {@link #testMethodManager} to null.
	 */
	public void destroyTestMethodManager() {
		testMethodManager = null;
	}
	
	/**
	 * Sets {@link #invokedManager} to null.
	 */
	public void destroyInvokedManager() {
		invokedManager = null;
	}
	
	/**
	 * Restores original files, displaying an error message if an error occurs.
	 * 
	 * @param		fm File manager
	 */
	public void restoreOriginalFile(FileManager fm) {
		restoreBinaryFile(fm);
		restoreSourceFile(fm);
	}

	private void restoreBinaryFile(FileManager fm) {
		try {
			fm.revertCompilation();
		} 
		catch (IOException e) {
			Logger.error(
					"An error occurred while restoring the original binary file - " 
					+ e.getMessage()
			);
		}
	}

	private void restoreSourceFile(FileManager fm) {
		try {
			fm.revertProcessing();
		} 
		catch (IOException e) {
			Logger.error(
					"An error occurred while restoring the original source file - " 
					+ e.getMessage()
			);
		}
	}
	
	/**
	 * Processes test method source file.
	 * 
	 * @param		testMethodInfo Test method to be processed
	 * @param		testMethodFileManager Test method file manager
	 * 
	 * @throws		IOException If an error occurs during processing or 
	 * compilation
	 */
	public void processTestMethod(FileManager testMethodFileManager) 
			throws IOException {
		if (testMethodManager.wasProcessed(testMethodFileManager))
			return;
		
		try {
			testMethodManager.processFile(testMethodFileManager);
			testMethodManager.compile(testMethodFileManager);
		}
		catch (java.lang.NoClassDefFoundError e) {
			Logger.error("Process test method - " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
		
		Logger.info("Processing completed");	
	}
	
	/**
	 * Processes invoked source file.
	 * 
	 * @param		testMethodFileManager Test method to be processed
	 * @param		invokedFileManager Invoked file manager
	 * @param		invSig
	 * 
	 * @throws		IOException If an error occurs during processing or 
	 * compilation
	 */
	public void processInvoked(FileManager testMethodFileManager, 
			FileManager invokedFileManager) throws IOException {
		if (invokedManager.wasProcessed(invokedFileManager))
			return;
		
		boolean autoRestore = 
				!testMethodFileManager.getSrcFile()
				.equals(invokedFileManager.getSrcFile());
		
		invokedManager.processFile(invokedFileManager, autoRestore);
		invokedManager.compile(invokedFileManager);
	}
	
	public boolean isTestMethodManagerInitialized() {
		return (testMethodManager != null);
	}
	
	public boolean isInvokedManagerInitialized() {
		return (invokedManager != null);
	}
	
	public void deleteTestMethodFileManagerBackup() {
		if (!isTestMethodManagerInitialized())
			return;
		
		testMethodManager.deleteBackup();
	}
	
	public void deleteInvokedFileManagerBackup() {
		if (!isInvokedManagerInitialized())
			return;
		
		invokedManager.deleteBackup();
	}
	
	public void restoreTestMethodOriginalFiles() 
			throws ClassNotFoundException, IOException {
		if (!isTestMethodManagerInitialized())
			return;
		
		if (testMethodManager.load())
			testMethodManager.restoreAll();
	}
	
	public void restoreInvokedOriginalFiles() 
			throws ClassNotFoundException, IOException {
		if (!isInvokedManagerInitialized())
			return;
		
		if (invokedManager.load())
			invokedManager.restoreAll();
	}
}
