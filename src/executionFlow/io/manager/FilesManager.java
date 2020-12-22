package executionFlow.io.manager;

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

import executionFlow.ExecutionFlow;
import executionFlow.io.processor.ProcessorType;

/**
 * Responsible for managing the processing and compilation from files. Its main 
 * responsibility is not to perform unnecessary processing, that is, processing
 * files that have already been processed.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.3
 * @since		2.0.0
 */
public class FilesManager {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private boolean autoDelete;
	private File backupFile;
	private HashSet<FileManager> files;
	
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
	 * {@link #processFile(FileManager)}
	 * @param		autoDelete If true, if there are backup files, it will 
	 * delete them after restore them
	 * @param		autoRestore If true and if there are backup files, restore them
	 * 
	 * @throws		IOException If an error occurs during class deserialization
	 * (while restoring backup files)
	 * @throws		ClassNotFoundException If class {@link FileManager} is not
	 * found  
	 */
	public FilesManager(ProcessorType type, boolean autoDelete, boolean autoRestore) 
			throws ClassNotFoundException, IOException {
		this.files = new HashSet<>(); 
		this.processedFiles = new HashSet<>();
		this.compiledFiles = new HashSet<>();
		this.autoDelete = autoDelete;
		
		this.backupFile = new File(
				ExecutionFlow.getCurrentProjectRoot().toFile(), 
				"_EF_" + type.getName() + "_FILES.ef"
		);

		if (autoRestore && hasBackupStored()) {
			restoreFromBackup();
		}		
	}
	
	/**
	 * Manages invoked file (an invoked can be a method or a constructor),
	 * being responsible for its processing and compilation. It will avoid 
	 * processing files that have already been processed. Also, if the 
	 * application ends before the original files are restored, it will restore
	 * them. It will create a backup file with the following name: <br /> <br />
	 * 
	 * <code>_EF_ + type.getName() + _FILES.ef</code>
	 * 
	 * @param		type Type of parser that will be used in 
	 * {@link #processFile(FileManager)}
	 * @param		autoDelete If true, if there are backup files, it will 
	 * delete them after restore them
	 * @param		restore If true and if there are backup files, restore them
	 * 
	 * @throws		IOException If an error occurs during class deserialization
	 * (while restoring backup files)
	 * @throws		ClassNotFoundException If class {@link FileManager} is not
	 * found  
	 */
	public FilesManager(ProcessorType type, boolean autoDelete)
			throws ClassNotFoundException, IOException {
		this(type, autoDelete, true);
	}
	
	/**
	 * Manages invoked file (an invoked can be a method or a constructor), 
	 * being responsible for its processing and compilation. It will avoid
	 * processing files that have already been processed. Also, if the 
	 * application ends before the original files are restored, it will restore
	 * them. Using this constructor, if there are backup files, they will be 
	 * restored. Besides, {@link ProcessorType} will be 
	 * {@link ProcessorType#INVOKED}. Also, it will create a backup file with the 
	 * following name: <br /> <br />
	 * 
	 * <code>_EF_ + {@link ProcessorType#INVOKED}.getName() + _FILES.ef</code>
	 * 
	 * @param		autoDelete If true, if there are backup files, it will 
	 * delete them after restore them
	 * 
	 * @throws		IOException If an error occurs during class deserialization
	 * (while restoring backup files)
	 * 
	 * @throws		ClassNotFoundException If class {@link FileManager} is not
	 * found  
	 */
	public FilesManager(boolean autoDelete) throws ClassNotFoundException, IOException {
		this(ProcessorType.INVOKED, autoDelete, true);
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Restores list of files modified in the last execution.
	 * 
	 * @throws		IOException If an error occurs during class deserialization 
	 * @throws		ClassNotFoundException If class {@link FileManager} is not
	 * found 
	 */
	private void restoreFromBackup() throws IOException, ClassNotFoundException {
		if (!backupFile.exists())
			return;
		
		restoreAll(readFileManagersFromBackupFile());
		
		if (autoDelete)
			deleteBackup();
	}
	
	@SuppressWarnings("unchecked")
	private HashSet<FileManager> readFileManagersFromBackupFile() 
			throws IOException, ClassNotFoundException {
		HashSet<FileManager> fileManagers = new HashSet<>();
		
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(backupFile))) {
			fileManagers = (HashSet<FileManager>) ois.readObject();
		}
		
		return fileManagers;
	}
	
	/**
	 * Restores all original files that have been modified.
	 * 
	 * @param		files List of modified files
	 */
	private void restoreAll(Set<FileManager> files) {
		if (files == null)
			return;
		
		Iterator<FileManager> it = files.iterator();
		
		while (it.hasNext()) {
			FileManager fm = it.next(); 
			
			revertProcessingUsingFileManager(fm);
			revertCompilationUsingFileManager(fm);
			removeFileManager(it);
		}
	}
	
	private boolean revertProcessingUsingFileManager(FileManager fm) {
		boolean success = true;
		
		try {
			fm.revertProcessing();
		} 
		catch (IOException e) {
			success = false;
		}
		
		return success;
	}
	
	private boolean revertCompilationUsingFileManager(FileManager fm) {
		boolean success = true;
		
		try {
			fm.revertCompilation();
		} 
		catch (IOException e) {
			success = false;
		}
		
		return success;
	}
	
	private boolean removeFileManager(Iterator<FileManager> it) {
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
	 * Parses file from its {@link FileManager}.
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
	public FilesManager processFile(FileManager fm, boolean autoRestore) 
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
	public boolean wasProcessed(FileManager fm) {
		return processedFiles.contains(fm.hashCode());
	}
	
	private void markFileAsProcessed(FileManager fm) throws IOException {
		processedFiles.add(fm.hashCode());

		if (!files.contains(fm)) {
			files.add(fm);
			save();
		}
	}
	
	/**
	 * Serializes list of FileManagers to allow modified files to be restored 
	 * in case the program is interrupted without having restored these files.
	 * 
	 * @throws		IOException If an error occurs during class serialization 
	 */
	public void save() throws IOException {
		try (ObjectOutputStream ois = new ObjectOutputStream(new FileOutputStream(backupFile))) {
			ois.writeObject(files);
			ois.writeObject(processedFiles);
			ois.writeObject(compiledFiles);
		}
	}
	
	/**
	 * Parses file from its {@link FileManager}.
	 * 
	 * @param		fm File manager of the file
	 * @param		isTestMethod If file contains test methods
	 * 
	 * @return		This object to allow chained calls
	 * 
	 * @throws		IOException If an error occurs during parsing or during
	 * class serialization
	 */
	public FilesManager processFile(FileManager fm) throws IOException {
		return processFile(fm, true);
	}
	
	/**
	 * Compiles file from its {@link FileManager}.
	 * 
	 * @param		fm File manager of the file
	 * 
	 * @return		This object to allow chained calls
	 * 
	 * @throws		IOException If an error occurs during compilation or during
	 * class serialization
	 */
	public FilesManager compile(FileManager fm) throws IOException {	
		if (wasCompiled(fm))
			return this;
		
		fm.createBackupBinFile();
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
	public boolean wasCompiled(FileManager fm) {
		return compiledFiles.contains(fm.hashCode());
	}

	private void markFileAsCompiled(FileManager fm) throws IOException {
		compiledFiles.add(fm.hashCode());
		
		if (!files.contains(fm)) {
			files.add(fm);
			save();
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
	 * @return		If the lists of file managers were retrieved
	 * 
	 * @throws 		IOException If an error occurs while deserializing the list
	 * of file managers 
	 * @throws 		ClassNotFoundException If class {@link FileManager} is not
	 * found 
	 * 
	 * @implNote	If backup file does not exist, {@link #files} will be null
	 */
	@SuppressWarnings("unchecked")
	public boolean load() throws IOException, ClassNotFoundException {
		if (!hasBackupStored())
			return false;
		
		boolean success = true;
		
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(backupFile))) {
			this.files = (HashSet<FileManager>) ois.readObject();
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
	public boolean remove(FileManager fm) {
		processedFiles.remove(fm.hashCode());
		compiledFiles.remove(fm.hashCode());
		
		return files.remove(fm);
	}
	
	@Override
	public String toString() {
		return "FilesManager ["
				+ "backupFile=" + backupFile 
				+ ", files=" + files 
				+ ", processedFiles=" + processedFiles
				+ ", compiledFiles=" + compiledFiles 
				+ ", autoDelete=" + autoDelete 
			+ "]";
	}
}
