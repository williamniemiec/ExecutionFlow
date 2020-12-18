package executionFlow.io.manager;

import java.io.IOException;

import executionFlow.io.processor.ProcessorType;
import executionFlow.util.Logger;


public class ProcessingManager {
	private static FilesManager testMethodManager;
	private static FilesManager invokedManager;
	
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
	public ProcessingManager(boolean restoreOriginalFiles) throws ClassNotFoundException, IOException {
		initializeTestMethodManager(restoreOriginalFiles);
		initializeInvokedManager(restoreOriginalFiles);
	}

	private void initializeInvokedManager(boolean restoreOriginalFiles)
			throws ClassNotFoundException, IOException {
		if (invokedManager == null) {
			try {
				invokedManager = new FilesManager(ProcessorType.INVOKED, true, restoreOriginalFiles);
				
				// Loads files that have already been processed
				if (!restoreOriginalFiles)
					invokedManager.load();
			} 
			catch (ClassNotFoundException e) {
				throw new ClassNotFoundException("Class FileManager not found");
			} 
			catch (IOException e) {
				throw new IOException(
						"Could not recover all backup files for methods\n"
						+ "See more: https://github.com/williamniemiec/"
						+ "ExecutionFlow/wiki/Solu%C3%A7%C3%A3o-de-problemas"
						+ "#could-not-recover-all-backup-files"
				);
			}
		}
	}

	private void initializeTestMethodManager(boolean restoreOriginalFiles)
			throws ClassNotFoundException, IOException {
		if (testMethodManager == null) {
			try {
				testMethodManager = new FilesManager(ProcessorType.TEST_METHOD, true, restoreOriginalFiles);
			} 
			catch (ClassNotFoundException e) {
				throw new ClassNotFoundException("Class FileManager not found");
			} 
			catch (IOException e) {
				throw new IOException(
						"Could not recover the backup file of the test method\n"
						+ "See more: https://github.com/williamniemiec/"
						+ "ExecutionFlow/wiki/Solu%C3%A7%C3%A3o-de-problemas"
						+ "#could-not-recover-all-backup-files"
				);
			}
		}
	}
	
	/**
	 * Sets {@link #testMethodManager} to null.
	 */
	public void destroyTestMethodManager()
	{
		testMethodManager = null;
	}
	
	/**
	 * Sets {@link #invokedManager} to null.
	 */
	public void destroyInvokedManager()
	{
		invokedManager = null;
	}
	
	/**
	 * Restores original files, displaying an error message if an error occurs.
	 * 
	 * @param		fm File manager
	 */
	public void restoreOriginalFile(FileManager fm) 
	{
		try {
			fm.revertCompilation();
			fm.revertParse();
		} 
		catch (IOException e) {
			Logger.error(
					"An error occurred while restoring the original files - " 
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
	public void processTestMethod(FileManager testMethodFileManager) throws IOException 
	{
		if (!testMethodManager.wasProcessed(testMethodFileManager)) {
			try {
				testMethodManager.parse(testMethodFileManager).compile(testMethodFileManager);
			}
			catch (java.lang.NoClassDefFoundError e) {
				Logger.error("Process test method - " + e.getMessage());
				e.printStackTrace();
				System.exit(-1);
			}
			
			Logger.info("Processing completed");	
		}
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
	public void processInvoked(FileManager testMethodFileManager, FileManager invokedFileManager) 
			throws IOException 
	{
		if (!invokedManager.wasProcessed(invokedFileManager)) {
			boolean autoRestore = 
					!testMethodFileManager.getSrcFile().equals(invokedFileManager.getSrcFile());
			
			invokedManager.parse(invokedFileManager, autoRestore).compile(invokedFileManager);
		}
	}
	
	public boolean isTestMethodManagerInitialized()
	{
		return (testMethodManager != null);
	}
	
	public boolean isInvokedManagerInitialized()
	{
		return (invokedManager != null);
	}
	
	public void deleteTestMethodFileManagerBackup()
	{
		if (!isTestMethodManagerInitialized())
			return;
		
		testMethodManager.deleteBackup();
	}
	
	public void deleteInvokedFileManagerBackup()
	{
		if (!isInvokedManagerInitialized())
			return;
		
		invokedManager.deleteBackup();
	}
	
	public void restoreTestMethodOriginalFiles() throws ClassNotFoundException, IOException
	{
		if (!isTestMethodManagerInitialized())
			return;
		
		if (testMethodManager.load())
			testMethodManager.restoreAll();
	}
	
	public void restoreInvokedOriginalFiles() throws ClassNotFoundException, IOException
	{
		if (!isInvokedManagerInitialized())
			return;
		
		if (invokedManager.load())
			invokedManager.restoreAll();
	}
}
