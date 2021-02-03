package util.io.search;

import java.io.File;

/**
 * Searches for directories.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 */
public class DirectorySearcher {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private File workingDirectory;
	
	
	//-------------------------------------------------------------------------
	//		Constructors
	//-------------------------------------------------------------------------
	public DirectorySearcher(File workingDirectory) {
		this.workingDirectory = workingDirectory;
	}
	
	public DirectorySearcher() {
		this.workingDirectory = new File(".");
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Searches for a directory with the specified name.
	 * 
	 * @param		directoryName Name of the directory to be searched 
	 * 
	 * @return		Directory with the specified name of the parent directory 
	 * if it can't find the directory.
	 */
	public File search(String directoryName) {
		File currentDirectory = workingDirectory;
		boolean hasDirectoryWithProvidedName = false;
		
		while (!hasDirectoryWithProvidedName) {
			hasDirectoryWithProvidedName = hasFileWithName(directoryName, currentDirectory);

			if (!hasDirectoryWithProvidedName)
				currentDirectory = new File(currentDirectory.getParent());
		}
		
		return currentDirectory;
	}

	private boolean hasFileWithName(String name, File workingDirectory) {
		String[] files = workingDirectory.list();
		
		for (int i=0; i<files.length; i++) {
			if (files[i].equals(name))
				return true;
		}
		
		return false;
	}
}
