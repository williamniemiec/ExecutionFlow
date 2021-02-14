package wniemiec.executionflow.io.processing.manager;

import java.io.IOException;

import wniemiec.util.logger.Logger;

/**
 * Responsible for managing the processing and compilation of methods, 
 * constructors and test methods.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		7.0.0
 * @since		7.0.0
 */
public class InvokedProcessingManager {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private FilesProcessingManager invokedFilesManager;
	
	
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
	public InvokedProcessingManager(FilesProcessingManager invokedFilesManager, boolean restoreOriginalFiles/*, FileManager invokedFileManager*/) 
			throws ClassNotFoundException, IOException {
		try {
			this.invokedFilesManager = invokedFilesManager;
			
			if (!restoreOriginalFiles)
				invokedFilesManager.load();
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
	public void destroyInvokedFilesManager() {
		invokedFilesManager = null;
	}
	
	public void restoreInvokedOriginalFile(FileProcessingManager invokedFileManager) {
		restoreOriginalFile(invokedFileManager);
		invokedFilesManager.remove(invokedFileManager);
	}
	
	/**
	 * Restores original files, displaying an error message if an error occurs.
	 */
	private void restoreOriginalFile(FileProcessingManager invokedFileManager) {
		restoreBinaryFile(invokedFileManager);
		restoreSourceFile(invokedFileManager);
	}

	private void restoreBinaryFile(FileProcessingManager invokedFileManager) {
		try {
			invokedFileManager.revertCompilation();
		} 
		catch (IOException e) {
			Logger.error(
					"An error occurred while restoring the original binary file - " 
					+ e.getMessage()
			);
		}
	}

	private void restoreSourceFile(FileProcessingManager invokedFileManager) {
		try {
			invokedFileManager.revertProcessing();
		} 
		catch (IOException e) {
			Logger.error(
					"An error occurred while restoring the original source file - " 
					+ e.getMessage()
			);
		}
	}
	
	public void processAndCompile(FileProcessingManager invokedFileManager, boolean autoRestore) 
			throws IOException {
		if (invokedFilesManager.wasProcessed(invokedFileManager))
			return;
		
		invokedFilesManager.processFile(invokedFileManager, autoRestore);
		invokedFilesManager.compile(invokedFileManager);
		
		Logger.info("Processing completed");	
	}
	
	
	public boolean isInvokedFilesManagerInitialized() {
		return (invokedFilesManager != null);
	}
	
	public void deleteBackupFiles() {
		if (!isInvokedFilesManagerInitialized())
			return;
		
		invokedFilesManager.deleteBackup();
	}
	
	public void restoreInvokedOriginalFiles() 
			throws IOException {
		if (!isInvokedFilesManagerInitialized())
			return;
		
		try {
			if (invokedFilesManager.load())
				invokedFilesManager.restoreAll();
		} 
		catch (ClassNotFoundException e) {
			Logger.error("Class missing: FileManager");
			System.exit(-1);
		}
	}
	
	@Override
	public String toString() {
		return "InvokedProcessingManager ["
				+ "invokedFilesManager=" + invokedFilesManager 
			+ "]";
	}
}
