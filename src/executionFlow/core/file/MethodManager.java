package executionFlow.core.file;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import executionFlow.ConsoleOutput;


/**
 * Responsible for managing the parse and compilation of a file. Its main 
 * responsibility is not to perform unnecessary processing, that is, processing
 * files that have already been processed.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		1.5
 * @since		1.5
 */
public class MethodManager 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private final String backupFilename;
	private HashSet<FileManager> files;
	
	/**
	 * Stores hashcode of FileManager that have already been processed 
	 */
	private Set<Integer> parsedFiles;
	
	/**
	 * Stores hashcode of FileManager that have already been compiled 
	 */
	private Set<Integer> compiledFiles;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	/**
	 * Manages method file, being responsible for its processing and 
	 * compilation. It will avoid processing files that have already been 
	 * processed. Also, if the application ends before the original files are 
	 * restored, it will restore them.
	 * 
	 * @param		type Type of the method file
	 */
	public MethodManager(MethodManagerType type)
	{
		files = new HashSet<>(); 
		parsedFiles = new HashSet<>();
		compiledFiles = new HashSet<>();
		backupFilename = "_EF_"+type.getName()+"_FILES.ef";

		// If there are files modified from the last execution that were not
		// restored, restore them
		if (this.hasBackupStored()) {
			restoreFromBackup();
		}		
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Parses file from its {@link FileManager}.
	 * 
	 * @param		fm File manager of the file
	 * @return		This object to allow chained calls
	 * @throws		IOException If an error occurs during parsing
	 */
	public MethodManager parse(FileManager fm) throws IOException
	{
		int key = fm.hashCode();
		
		if (parsedFiles.contains(key))
			return this;
		
		parsedFiles.add(key);
		
		if (!files.contains(fm)) {
			files.add(fm);
			save();
		}
		
		fm.parseFile();
		
		return this;
	}
	
	/**
	 * Compiles file from its {@link FileManager}.
	 * 
	 * @param		fm File manager of the file
	 * @return		This object to allow chained calls
	 * @throws		IOException If an error occurs during compilation
	 */
	public MethodManager compile(FileManager fm) throws IOException
	{	
		int key = fm.hashCode();
		
		if (compiledFiles.contains(fm.hashCode()))
			return this;
		
		compiledFiles.add(key);
		
		if (!files.contains(fm)) {
			files.add(fm);
			save();
		}
		
		fm.createClassBackupFile().compileFile();
		
		return this;
	}
	
	/**
	 * Checks if the file has already been parsed.
	 * 
	 * @param		fm File manager of the file
	 * @return		If the file has already been parsed
	 */
	public boolean wasParsed(FileManager fm)
	{
		return parsedFiles.contains(fm.hashCode());
	}
	
	/**
	 * Checks if the file has already been compiled.
	 * 
	 * @param		fm File manager of the file
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
	 * Checks if exists a backup file.
	 * 
	 * @return		If exists a backup file
	 */
	private boolean hasBackupStored()
	{
		return Files.exists(Path.of(backupFilename));
	}
	
	/**
	 * Serializes list of FileManagers to allow modified files to be restored 
	 * in case the program is interrupted without having restored these files.
	 */
	private void save()
	{
		// Serializes list of parsed files
		try (ObjectOutputStream ois = new ObjectOutputStream(new FileOutputStream(backupFilename))) {
			ois.writeObject(files);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Restores all original files that have been modified.
	 * 
	 * @param		files List of modified files
	 * @return		If the restore was successful
	 */
	private boolean restoreAll(Set<FileManager> files)
	{
		boolean response = true;
		Iterator<FileManager> it = files.iterator();
		
		// Restores source file and compilation file for each modified file
		while (it.hasNext()) {
			FileManager fm = it.next(); 
			
			// Restores source file
			try {
				fm.revertParse();
			} catch (IOException e) {
				ConsoleOutput.showError("Restore parse - "+fm.getCompiledFile().toString());
				ConsoleOutput.showError("Restore parse - "+e.getMessage());
				response = false;
			}
			
			// Restores compilation file
			try {
				fm.revertCompilation();
			} catch (IOException e) {
				ConsoleOutput.showError("Restore compilation - "+fm.getCompiledFile().toString());
				ConsoleOutput.showError("Restore compilation - "+e.getMessage());
				response = false;
			}
			
			it.remove();
		}
		
		return response;
	}
	
	/**
	 * Restores list of files modified in the last execution.
	 */
	private void restoreFromBackup()
	{
		// Deserializes list of files
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(backupFilename))) {
			@SuppressWarnings("unchecked")
			HashSet<FileManager> restoredFiles = (HashSet<FileManager>)ois.readObject();
			
			// Restores original files
			restoreAll(restoredFiles);
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		deleteBackup();
	}
	
	/**
	 * Deletes backup file
	 */
	private void deleteBackup()
	{
		// Deletes backup file
		try {
			Files.delete(Path.of(backupFilename));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
