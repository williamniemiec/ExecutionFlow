package executionFlow.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import executionFlow.ExecutionFlow;
import executionFlow.info.CollectorInfo;


/**
 * Responsible for managing the processing and compilation from files. Its main 
 * responsibility is not to perform unnecessary processing, that is, processing
 * files that have already been processed.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.1.0
 * @since		2.0.0
 */
public class FilesManager 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private File backupFile;
	private HashSet<FileManager> files;
	
	/**
	 * Stores hashcode of FileManager that have already been processed 
	 */
	private Set<Integer> parsedFiles;
	
	/**
	 * Stores hashcode of FileManager that have already been compiled 
	 */
	private Set<Integer> compiledFiles;
	
	private boolean autoDelete;
	
	
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
	 * {@link #parse(FileManager)}
	 * @param		autoDelete If true, if there are backup files, it will 
	 * delete them after restore them
	 * @param		restore If true and if there are backup files, restore them
	 * 
	 * @throws		IOException If an error occurs during class deserialization
	 * (while restoring backup files)
	 * @throws		ClassNotFoundException If class {@link FileManager} is not
	 * found  
	 */
	public FilesManager(ProcessorType type, boolean autoDelete, boolean restore) 
			throws ClassNotFoundException, IOException
	{
		files = new HashSet<>(); 
		parsedFiles = new HashSet<>();
		compiledFiles = new HashSet<>();
		backupFile = new File(ExecutionFlow.getCurrentProjectRoot().toFile(), 
				"_EF_"+type.getName()+"_FILES.ef");
		this.autoDelete = autoDelete;

		// If there are files modified from the last execution that were not
		// restored, restore them
		if (restore && this.hasBackupStored()) {
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
	 * {@link #parse(FileManager)}
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
			throws ClassNotFoundException, IOException
	{
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
	public FilesManager(boolean autoDelete) throws ClassNotFoundException, IOException
	{
		this(ProcessorType.INVOKED, autoDelete, true);
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	public String toString() 
	{
		return "FilesManager ["
				+ "backupFile=" + backupFile 
				+ ", files=" + files 
				+ ", parsedFiles=" + parsedFiles
				+ ", compiledFiles=" + compiledFiles 
				+ ", autoDelete=" + autoDelete 
			+ "]";
	}

	/**
	 * Parses file from its {@link FileManager}.
	 * 
	 * @param		fm File manager of the file
	 * @param		collectors Information about all invoked collected
	 * @param		autoRestore Checks if processed files exist against the 
	 * current file. If so, restore them before processing. Default is true.
	 * 
	 * @return		This object to allow chained calls
	 * 
	 * @throws		IOException If an error occurs during parsing or during
	 * class serialization
	 */
	public FilesManager parse(FileManager fm, List<CollectorInfo> collectors, 
			boolean autoRestore) throws IOException
	{
		int key = fm.hashCode();

		if (parsedFiles.contains(key))
			return this;
		
		parsedFiles.add(key);

		if (!files.contains(fm)) {
			files.add(fm);
			save();
		}
		
		fm.parseFile(collectors, autoRestore);
		
		return this;
	}
	
	/**
	 * Parses file from its {@link FileManager}.
	 * 
	 * @param		fm File manager of the file
	 * @param		collectors Information about all invoked collected
	 * 
	 * @return		This object to allow chained calls
	 * 
	 * @throws		IOException If an error occurs during parsing or during
	 * class serialization
	 */
	public FilesManager parse(FileManager fm, List<CollectorInfo> collectors) throws IOException
	{
		return parse(fm, collectors, true);
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
	public FilesManager parse(FileManager fm) throws IOException
	{
		return parse(fm, null);
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
	public FilesManager compile(FileManager fm) throws IOException
	{	
		int key = fm.hashCode();
		
		if (compiledFiles.contains(fm.hashCode()))
			return this;
		
		compiledFiles.add(key);
		
		if (!files.contains(fm)) {
			files.add(fm);
			save();
		}

		fm.createBackupBinFile().compileFile();
		
		return this;
	}
	
	/**
	 * Checks if the file has already been parsed.
	 * 
	 * @param		fm File manager of the file
	 * 
	 * @return		If the file has already been parsed
	 */
	public boolean wasProcessed(FileManager fm)
	{
		return parsedFiles.contains(fm.hashCode());
	}
	
	/**
	 * Checks if the file has already been compiled.
	 * 
	 * @param		fm File manager of the file
	 * 
	 * @return		If the file has already been compiled
	 */
	public boolean wasCompiled(FileManager fm)
	{
		return compiledFiles.contains(fm.hashCode());
	}
	
	/**
	 * Restores all original files that have been modified.
	 * 
	 * @return		If the restore was successful
	 */
	public boolean restoreAll()
	{
		return restoreAll(files);
	}
	
	/**
	 * Deletes backup file
	 */
	public void deleteBackup()
	{
		backupFile.delete();
	}
	
	/**
	 * Checks if exists a backup file.
	 * 
	 * @return		If exists a backup file
	 */
	public boolean hasBackupStored()
	{
		return backupFile.exists();
	}
	
	/**
	 * Serializes list of FileManagers to allow modified files to be restored 
	 * in case the program is interrupted without having restored these files.
	 * 
	 * @throws		IOException If an error occurs during class serialization 
	 */
	public void save() throws IOException
	{
		ObjectOutputStream ois = new ObjectOutputStream(new FileOutputStream(backupFile));
		ois.writeObject(files);
		ois.writeObject(parsedFiles);
		ois.writeObject(compiledFiles);
		ois.close();
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
	public boolean load() throws IOException, ClassNotFoundException
	{
		if (!hasBackupStored()) { return false; }
		
		boolean error = false;
		
		
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(backupFile));
			this.files = (HashSet<FileManager>)ois.readObject();
			this.parsedFiles = (Set<Integer>)ois.readObject();
			this.compiledFiles = (Set<Integer>)ois.readObject();
			ois.close();
		} 
		catch (FileNotFoundException e) {
			this.files = null;
			error = true;
		}
		
		return !error;
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
	public boolean remove(FileManager fm)
	{
		parsedFiles.remove(fm.hashCode());
		compiledFiles.remove(fm.hashCode());
		
		return files.remove(fm);
	}
	
	/**
	 * Restores all original files that have been modified.
	 * 
	 * @param		files List of modified files
	 * 
	 * @return		If the restore was successful
	 */
	private boolean restoreAll(Set<FileManager> files)
	{
		boolean error = false;
		Iterator<FileManager> it = files.iterator();
		
		// Restores source file and compilation file for each modified file
		while (it.hasNext()) {
			FileManager fm = it.next(); 
			
			// Restores source and compilation file
			try {
				fm.revertParse();
			} 
			catch (IOException e) {
				error = true;
			}
			
			try {
				fm.revertCompilation();
			} 
			catch (IOException e) {
				error = true;
			}
			
			if (!error)
				it.remove();
		}
		
		return !error;
	}
	
	/**
	 * Restores list of files modified in the last execution.
	 * 
	 * @throws		IOException If an error occurs during class deserialization 
	 * @throws		ClassNotFoundException If class {@link FileManager} is not
	 * found 
	 */
	private void restoreFromBackup() throws IOException, ClassNotFoundException
	{
		// Deserialization
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(backupFile));
		
		@SuppressWarnings("unchecked")
		HashSet<FileManager> restoredFiles = (HashSet<FileManager>)ois.readObject();
		
		ois.close();
		
		// Restores original files
		restoreAll(restoredFiles);
		
		if (autoDelete)
			deleteBackup();
	}
}
