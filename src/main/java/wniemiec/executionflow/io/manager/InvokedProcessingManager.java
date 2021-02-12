package wniemiec.executionflow.io.manager;

import java.io.IOException;

import wniemiec.util.logger.Logger;

public class InvokedProcessingManager {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private FilesManager invokedFilesManager;
//	private FileManager invokedFileManager;
	
	
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
	public InvokedProcessingManager(FilesManager invokedFilesManager, boolean restoreOriginalFiles/*, FileManager invokedFileManager*/) 
			throws ClassNotFoundException, IOException {
		try {
//			invokedFilesManager = new FilesManager(
//					type, 
//					true, 
//					restoreOriginalFiles
//			);
			this.invokedFilesManager = invokedFilesManager;
//			this.invokedFileManager = invokedFileManager;
//			
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

	
	@Override
	public String toString() {
		return "InvokedProcessingManager [invokedFilesManager=" + invokedFilesManager + "]";
	}


	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------	
	public void destroyInvokedFilesManager() {
		invokedFilesManager = null;
	}
	
	public void restoreInvokedOriginalFile(FileManager invokedFileManager) {
		restoreOriginalFile(invokedFileManager);
		invokedFilesManager.remove(invokedFileManager);
	}
	
	/**
	 * Restores original files, displaying an error message if an error occurs.
	 */
	private void restoreOriginalFile(FileManager invokedFileManager) {
		restoreBinaryFile(invokedFileManager);
		restoreSourceFile(invokedFileManager);
	}

	private void restoreBinaryFile(FileManager invokedFileManager) {
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

	private void restoreSourceFile(FileManager invokedFileManager) {
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
	
	public void processAndCompile(FileManager invokedFileManager, boolean autoRestore) 
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
}
