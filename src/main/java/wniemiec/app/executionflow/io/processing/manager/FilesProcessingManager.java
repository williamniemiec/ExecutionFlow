package wniemiec.app.executionflow.io.processing.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import wniemiec.app.executionflow.App;
import wniemiec.app.executionflow.io.processing.file.ProcessorType;

/**
 * Responsible for managing the processing and compilation from files. Its main 
 * responsibility is not to perform unnecessary processing, that is, processing
 * files that have already been processed.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @since		2.0.0
 */
public class FilesProcessingManager {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private boolean autoDelete;
	private File backupFile;
	private HashSet<FileProcessingManager> files;
	
	/**
	 * Stores hashcode of FileManager that have already been processed.
	 */
	private Set<Integer> processedFiles;
	
	/**
	 * Stores hashcode of FileManager that have already been compiled.
	 */
	private Set<Integer> compiledFiles;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	/**
	 * Manages invoked file (an invoked can be a method or a constructor),
	 * being responsible for its processing and compilation. It will avoid 
	 * processing files that have already been processed. It will create a 
	 * backup file with the following name: <br /> <br />
	 * 
	 * <code>_EF_ + type.getName() + _FILES.ef</code>
	 * 
	 * @param		type Type of parser that will be used in 
	 * {@link #process(FileProcessingManager)}
	 * @param		autoRestore If true and if there are backup files, restore
	 * them
	 * 
	 * @throws		IOException If an error occurs during class deserialization
	 * (while restoring backup files)
	 * @throws		ClassNotFoundException If class {@link FileProcessingManager}
	 * is not found  
	 */
	public FilesProcessingManager(ProcessorType type, boolean autoRestore) 
			throws ClassNotFoundException, IOException {
		this.files = new HashSet<>(); 
		this.processedFiles = new HashSet<>();
		this.compiledFiles = new HashSet<>();
		
		// if there are backup files, it will delete them after restore them
		this.autoDelete = (type != ProcessorType.PRE_TEST_METHOD);
		
		this.backupFile = new File(
				App.getCurrentProjectRoot().toFile(), 
				"_EF_" + type.getName() + "_FILES.ef"
		);

		if (autoRestore && hasBackupStored()) {
			restoreFromBackup();
		}		
	}
	
	/**
	 * Manages invoked file (an invoked can be a method or a constructor),
	 * being responsible for its processing and compilation. It will avoid 
	 * processing files that have already been processed. Using this 
	 * constructor,If there are backup files, it will restore them. Finally,
	 * it will create a backup file with the following name: <br /> <br />
	 * 
	 * <code>_EF_ + type.getName() + _FILES.ef</code>
	 * 
	 * @param		type Type of parser that will be used in 
	 * {@link #process(FileProcessingManager)}
	 * @param		autoRestore If true and if there are backup files, restore
	 * them
	 * 
	 * @throws		IOException If an error occurs during class deserialization
	 * (while restoring backup files)
	 * @throws		ClassNotFoundException If class {@link FileProcessingManager}
	 * is not found  
	 */
	public FilesProcessingManager(ProcessorType type) 
			throws ClassNotFoundException, IOException {
		this(type, true);
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Restores list of files modified in the last execution.
	 * 
	 * @throws		IOException If an error occurs during class deserialization 
	 * @throws		ClassNotFoundException If class {@link FileProcessingManager}
	 * is not found 
	 */
	private void restoreFromBackup() throws IOException, ClassNotFoundException {
		if (!backupFile.exists())
			return;
		
		restoreAll(readFileManagersFromBackupFile());
		
		if (autoDelete)
			deleteBackup();
	}
	
	@SuppressWarnings("unchecked")
	private HashSet<FileProcessingManager> readFileManagersFromBackupFile() 
			throws IOException, ClassNotFoundException {
		HashSet<FileProcessingManager> fileManagers = new HashSet<>();
		
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(backupFile))) {
			fileManagers = (HashSet<FileProcessingManager>) ois.readObject();
		}
		
		return fileManagers;
	}
	
	/**
	 * Restores all original files that have been modified.
	 * 
	 * @param		files List of modified files
	 */
	private void restoreAll(Set<FileProcessingManager> files) {
		if (files == null)
			return;
		
		Iterator<FileProcessingManager> it = files.iterator();
		
		while (it.hasNext()) {
			FileProcessingManager fm = it.next(); 
			
			revertProcessingUsingFileManager(fm);
			revertCompilationUsingFileManager(fm);
			removeFileManager(it);
		}
	}
	
	private boolean revertProcessingUsingFileManager(FileProcessingManager fm) {
		boolean success = true;
		
		try {
			fm.revertProcessing();
		} 
		catch (IOException e) {
			success = false;
		}
		
		return success;
	}
	
	private boolean revertCompilationUsingFileManager(FileProcessingManager fm) {
		boolean success = true;
		
		try {
			fm.revertCompilation();
		} 
		catch (IOException e) {
			success = false;
		}
		
		return success;
	}
	
	private boolean removeFileManager(Iterator<FileProcessingManager> it) {
		boolean success = true;
		
		try {
			it.remove();
		}
		catch (ConcurrentModificationException e) {
			success = false;
		}
		
		return success;
	}
	
	/**
	 * Checks if exists a backup file.
	 * 
	 * @return		If exists a backup file
	 */
	public boolean hasBackupStored() {
		return backupFile.exists();
	}
	
	/**
	 * Parses file from its {@link FileProcessingManager}.
	 * 
	 * @param		fm File manager of the file
	 * @param		autoRestore Checks if processed files exist against the 
	 * current file. If so, restore them before processing. Default is true.
	 * 
	 * @return		This object to allow chained calls
	 * 
	 * @throws		IOException If an error occurs during parsing or during
	 * class serialization
	 */
	public FilesProcessingManager processFile(FileProcessingManager fm, 
											  boolean autoRestore) 
			throws IOException {
		if (wasProcessed(fm))
			return this;
		
		fm.processFile(autoRestore);
		markFileAsProcessed(fm);
		
		return this;
	}

	/**
	 * Checks if the file has already been parsed.
	 * 
	 * @param		fm File manager of the file
	 * 
	 * @return		If the file has already been parsed
	 */
	public boolean wasProcessed(FileProcessingManager fm) {
		return processedFiles.contains(fm.hashCode());
	}
	
	private void markFileAsProcessed(FileProcessingManager fm) 
			throws IOException {
		processedFiles.add(fm.hashCode());

		if (!files.contains(fm)) {
			files.add(fm);
			createBackup();
		}
	}
	
	/**
	 * Serializes list of FileManagers to allow modified files to be restored 
	 * in case the program is interrupted without having restored these files.
	 * 
	 * @throws		IOException If an error occurs during class serialization 
	 */
	public void createBackup() throws IOException {
		try (ObjectOutputStream ois = new ObjectOutputStream(new FileOutputStream(backupFile))) {
			ois.writeObject(files);
			ois.writeObject(processedFiles);
			ois.writeObject(compiledFiles);
		}
	}
	
	/**
	 * Parses file from its {@link FileProcessingManager}.
	 * 
	 * @param		fm File manager of the file
	 * @param		isTestMethod If file contains test methods
	 * 
	 * @return		This object to allow chained calls
	 * 
	 * @throws		IOException If an error occurs during parsing or during
	 * class serialization
	 */
	public FilesProcessingManager process(FileProcessingManager fm) 
			throws IOException {
		return processFile(fm, true);
	}
	
	/**
	 * Compiles file from its {@link FileProcessingManager}.
	 * 
	 * @param		fm File manager of the file
	 * 
	 * @return		This object to allow chained calls
	 * 
	 * @throws		IOException If an error occurs during compilation or during
	 * class serialization
	 */
	public FilesProcessingManager compile(FileProcessingManager fm) 
			throws IOException {	
		if (wasCompiled(fm))
			return this;
		
		fm.createBinBackupFile(false);
		fm.compileFile();

		markFileAsCompiled(fm);
		
		return this;
	}
	
	/**
	 * Checks if the file has already been compiled.
	 * 
	 * @param		fm File manager of the file
	 * 
	 * @return		If the file has already been compiled
	 */
	public boolean wasCompiled(FileProcessingManager fm) {
		return compiledFiles.contains(fm.hashCode());
	}

	private void markFileAsCompiled(FileProcessingManager fm) 
			throws IOException {
		compiledFiles.add(fm.hashCode());
		
		if (!files.contains(fm)) {
			files.add(fm);
			createBackup();
		}
	}
	
	/**
	 * Restores all original files that have been modified.
	 * 
	 * @return		If the restore was successful
	 */
	public void restoreAll() {
		restoreAll(files);
	}
	
	/**
	 * Deletes backup file
	 */
	public void deleteBackup() {
		backupFile.delete();
	}
	
	/**
	 * Retrieves the list of file managers stores in a backup file. This list
	 * will be saved in {@link #files}.
	 * 
	 * @return		True if backup file has been successful loaded; false
	 * otherwise
	 * 
	 * @throws 		IOException If an error occurs while deserializing the list
	 * of file managers 
	 * @throws 		ClassNotFoundException If class {@link FileProcessingManager}
	 * is not found 
	 * 
	 * @implNote	If backup file does not exist, {@link #files} will be null
	 */
	@SuppressWarnings("unchecked")
	public boolean loadBackup() throws IOException, ClassNotFoundException {
		if (!hasBackupStored())
			return false;
		
		boolean success = true;
		
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(backupFile))) {
			this.files = (HashSet<FileProcessingManager>) ois.readObject();
			this.processedFiles = (Set<Integer>) ois.readObject();
			this.compiledFiles = (Set<Integer>) ois.readObject();
		} 
		catch (FileNotFoundException e) {
			this.files = null;
			success = false;
		}
		
		return success;
	}
	
	/**
	 * Removes a file manager from the list of processed files.
	 * <br /><br />
	 * <b>Note:</b> By doing this, a file that has already been processed can
	 * be processed again, generating extra processing that may be unnecessary.
	 * 
	 * @param		fm File manager to be deleted
	 * 
	 * @return		If file manager was successfully removed
	 */
	public boolean remove(FileProcessingManager fm) {
		processedFiles.remove(fm.hashCode());
		compiledFiles.remove(fm.hashCode());
		
		return files.remove(fm);
	}
	
	@Override
	public String toString() {
		return "FilesProcessingManager ["
					+ "autoDelete=" + autoDelete 
					+ ", backupFile=" + backupFile 
					+ ", files=" + files
					+ ", processedFiles=" + processedFiles 
					+ ", compiledFiles=" + compiledFiles 
				+ "]";
	}
}
