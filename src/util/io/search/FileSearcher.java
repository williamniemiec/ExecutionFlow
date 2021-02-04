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
	public FileSearcher(Path workingDirectory) {
		this.workingDirectory = workingDirectory;
	}
	
	public FileSearcher() {
		this(Path.of(".").normalize().toAbsolutePath());
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * When executed it will determine the absolute path of a class.
	 * 
	 * @param		filename Name of the file to be searched File (including 
	 * its extension)
	 * 
	 * @return		File path or null if it cannot find the file
	 * 
	 * @throws		IOException If an error occurs while searching for the file
	 */
	public Path search(String filename) throws IOException {
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
