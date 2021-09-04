package wniemiec.app.java.executionflow.io.processing.manager;

import java.io.IOException;

import wniemiec.io.java.Consolex;

/**
 * Responsible for managing the processing and compilation of methods, 
 * constructors and test methods.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
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
	 * @throws		IOException If backup files could not be restored
	 */
	public InvokedProcessingManager(FilesProcessingManager invokedFilesManager) 
			throws IOException {
		this.invokedFilesManager = invokedFilesManager;
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------	
	public void destroyInvokedFilesManager() {
		invokedFilesManager = null;
	}
	
	public void restoreInvokedOriginalFile(FileProcessingManager invokedFileManager) {
		checkInvokedFilesManagerIsInitialized();
		checkFileProcessingManager(invokedFileManager);
		
		restoreOriginalFile(invokedFileManager);
		invokedFilesManager.remove(invokedFileManager);
	}
	
	private void checkFileProcessingManager(FileProcessingManager invokedFileManager) {
		if (invokedFileManager == null) {
			throw new IllegalArgumentException("Invoked file manager cannot be null");
		}
	}

	private void restoreOriginalFile(FileProcessingManager invokedFileManager) {
		checkFileProcessingManager(invokedFileManager);
		
		restoreBinaryFile(invokedFileManager);
		restoreSourceFile(invokedFileManager);
	}

	private void restoreBinaryFile(FileProcessingManager invokedFileManager) {
		try {
			invokedFileManager.revertCompilation();
		} 
		catch (IOException e) {
			Consolex.writeError(
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
			Consolex.writeError(
					"An error occurred while restoring the original source file - " 
					+ e.getMessage()
			);
		}
	}
	
	public void processAndCompile(FileProcessingManager invokedFileManager, 
								  boolean autoRestore) 
			throws Exception {
		checkInvokedFilesManagerIsInitialized();
		checkFileProcessingManager(invokedFileManager);
		
		if (invokedFilesManager.wasProcessed(invokedFileManager))
			return;
		
		invokedFilesManager.createBackup();
		invokedFilesManager.processFile(invokedFileManager, autoRestore);
		invokedFilesManager.compile(invokedFileManager);
	}
	
	private void checkInvokedFilesManagerIsInitialized() {
		if (!isInvokedFilesManagerInitialized()) {
			throw new IllegalStateException("Invoked files manager was destroyed");
		}
	}

	public void processAndCompile(FileProcessingManager invokedFileManager) 
			throws Exception {
		processAndCompile(invokedFileManager, false);
	}
	
	
	public boolean isInvokedFilesManagerInitialized() {
		return (invokedFilesManager != null);
	}
	
	public void deleteBackupFiles() {
		checkInvokedFilesManagerIsInitialized();
		
		invokedFilesManager.deleteBackup();
	}
	
	public boolean hasBackupFiles() {
		if (!isInvokedFilesManagerInitialized())
			return false;
		
		return invokedFilesManager.hasBackupStored();
	}
	
	public void restoreInvokedOriginalFiles() 
			throws IOException {
		if (!isInvokedFilesManagerInitialized())
			return;
		
		try {
			if (invokedFilesManager.loadBackup())
				invokedFilesManager.restoreAll();
		} 
		catch (ClassNotFoundException e) {
			Consolex.writeError("Class missing: FileManager");
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
