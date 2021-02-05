package util.io.search;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Searches for files.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 */
public class FileSearcher {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private Path searchFile;
	private Path workingDirectory;

	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	/**
	 * Searches for files starting from the specified working directory.
	 * 
	 * @param		workingDirectory 
	 * 
	 * @throws		IllegalArgumentException If working directory is null
	 */
	public FileSearcher(Path workingDirectory) {
		if (workingDirectory == null)
			throw new IllegalArgumentException("Working directory cannot be null");
		
		
		this.workingDirectory = workingDirectory;
	}
	
	/**
	 * Searches for files starting from the current directory.
	 */
	public FileSearcher() {
		this(Path.of(".").normalize().toAbsolutePath());
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Searches for a file starting from the specified working directory.
	 * 
	 * @param		filename Name of the file to be searched File (including 
	 * its extension)
	 * 
	 * @return		File with the specified filename or null if it cannot find
	 * the file
	 * 
	 * @throws		IOException If an error occurs while searching for the file
	 * @throws		IllegalArgumentException If filename is blank or null
	 */
	public Path search(String filename) throws IOException {
		if ((filename == null) || filename.isBlank())
			throw new IllegalArgumentException("Filename cannot be empty");
		
		searchFile = null;
		
		Files.walkFileTree(workingDirectory, new SimpleFileVisitor<Path>() {
			@Override
		    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
		        if (file.toString().endsWith(filename)) {
		        	file = fixOrg(file);
		        	searchFile = file;
		        }
		        
		        return FileVisitResult.CONTINUE;
			}
		});
		
		return searchFile;
	}
	
	private static Path fixOrg(Path file) {
		return Path.of(file.toAbsolutePath().toString().replaceAll(
				"(\\/|\\\\)org(\\/|\\\\)org(\\/|\\\\)", 
				"/org/"
		));
	}
}
