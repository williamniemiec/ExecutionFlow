package executionFlow.core.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import executionFlow.core.file.parser.FileParser;
import executionFlow.core.file.parser.factory.FileParserFactory;


/**
 * Responsible for managing file parser and compiler.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		1.5
 * @since		1.3
 */
public class FileManager 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private Path srcFile;
	private Path originalSrcFile; 
	private Path classPath;
	private Path originalClassPath;
	private String filename;
	private Path classOutput;
	private String classPackage;
	private FileParser fp;
	private boolean charsetError;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	/**
	 * Manages file analyzer and compiler.
	 * 
	 * @param		srcFilePath Path of java file
	 * @param		classOutput Path of directory where .class of java file is
	 * @param		classPackage Package of the class of the java file
	 * @param		fileParserFactory Factory that will produce 
	 * {@link FileParser} that will be used for parsing file
	 */
	public FileManager(Path srcFilePath, Path classOutput, String classPackage, FileParserFactory fileParserFactory)
	{
		this.classOutput = classOutput;
		this.classPackage = classPackage;;
		this.filename = srcFilePath.getName(srcFilePath.getNameCount()-1).toString().split("\\.")[0];
		this.originalSrcFile = Path.of(srcFilePath.toAbsolutePath().toString()+".original"); 
		this.fp = fileParserFactory.newFileParser(
			srcFile, 
			classOutput, 
			filename+"_parsed", 
			FileEncoding.UTF_8
		);
		this.classPath = Path.of(classOutput+"/"+filename+".class");
		this.originalClassPath = Path.of(classOutput+"/"+filename+".class.original");
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Parses and process file, saving modified file in the same file passed 
	 * to constructor.
	 * 
	 * @return		This object to allow chained calls
	 * @throws		IOException If file encoding cannot be defined
	 * 
	 * @implNote This function overwrite file passed to the constructor! To
	 * restore the original file, call {@link #revertParse()} function.
	 */
	public FileManager parseFile() throws IOException
	{
		// Saves .java file to allow to restore it after
		createSrcBackupFile();
		
		// Parses file
		Path out;
		
		// Tries to parse file using UTF-8 encoding. If an error occurs, tries 
		// to parse the file using ISO-8859-1 encoding
		try {	
			out = Path.of(fp.parseFile());
		} catch(IOException e) {	
			charsetError = true;
			fp.setEncoding(FileEncoding.ISO_8859_1);
			
			try {
				out = Path.of(fp.parseFile());
			} catch (IOException e1) {
				throw new IOException("Parsing failed");
			}
		}
		
		// Changes parsed file name to the same as received filename
		if (Files.exists(out)) {
			Files.delete(srcFile);
			Files.move(out, srcFile);
		}
		
		return this;
	}
	
	/**
	 * Compiles processed file.
	 *  
	 * @return		This object to allow chained calls
	 * @throws		IOException If an error occurs during compilation
	 */
	public FileManager compileFile() throws IOException 
	{
		int packageFolders = classPackage.isEmpty() || classPackage == null ? 
								0 : classPackage.split("\\.").length;
		
		// Sets path to the compiler
		for (int i=0; i<packageFolders; i++) {
			classOutput = classOutput.getParent();
		}

		// Compiles parsed file. If an error has occurred in parsing, compiles 
		// using ISO-8859-1 encoding
		if (charsetError)	
			FileCompiler.compile(srcFile, classOutput, FileEncoding.ISO_8859_1);
		else
			FileCompiler.compile(srcFile, classOutput, FileEncoding.UTF_8);
		
		return this;
	}
	
	/**
	 * Deletes modified file and restores original file. This function does not
	 * delete .class file of modified file, only .java file.
	 * 
	 * @return		This object to allow chained calls
	 * @throws		IOException If method is called without creating a backup 
	 * file
	 */
	public FileManager revertParse() throws IOException
	{
		try {
			if (Files.exists(originalSrcFile)) {
				Files.delete(srcFile);
				Files.move(originalSrcFile, srcFile);
			}
		} catch (IOException e) {
			throw new IOException("Revert parse without backup");
		}
		
		return this;
	}
	
	/**
	 * Deletes modified .class file and restores original .class file.
	 * 
	 * @return		This object to allow chained calls
	 * @throws		IOException If method is called without creating a backup 
	 * file
	 */
	public FileManager revertCompilation() throws IOException
	{
		try {
			if (Files.exists(originalClassPath)) {
				Files.delete(classPath);
				Files.move(originalClassPath, classPath);
			}
		} catch (IOException e) {
			throw new IOException("Revert compilation without backup");
		}
		
		return this;
	}
	
	public boolean hasClassBackupStored()
	{
		return Files.exists(originalClassPath);
	}
	
	public boolean hasSrcBackupStored()
	{
		return Files.exists(originalSrcFile);
	}
	
	/**
	 * Creates a copy of class file passed to the constructor to allow to 
	 * restore it after.
	 * 
	 * @implNote		Backup name will be &lt;<b>name_of_file</b>.original.class&gt;.
	 * It will be saved in the same directory of the original file
	 */
	public FileManager createClassBackupFile()
	{
		try {
			Files.copy(
				classPath,
				originalClassPath, 
				StandardCopyOption.COPY_ATTRIBUTES
			);
		} catch (IOException e) {			// If already exists a .original, this means
			try {							// that last compiled file was not restored
				revertCompilation();	
				createClassBackupFile();	// So, restore this file and starts again
			} catch (IOException e1) {
				e1.printStackTrace();
			}			
		}
		
		return this;
	}
	
	/**
	 * Creates a copy of source file passed to the constructor to allow to 
	 * restore it after.
	 * 
	 * @implNote		Backup name will be &lt;<b>name_of_file</b>.original.java&gt;.
	 * It will be saved in the same directory of the original file
	 */
	private void createSrcBackupFile()
	{
		try {
			Files.copy(
				srcFile, 
				originalSrcFile, 
				StandardCopyOption.COPY_ATTRIBUTES
			);
		} catch (IOException e) {		// If already exists a .original, this means
			try {						// that last parsed file was not restored
				revertParse();			
				createSrcBackupFile();	// So, restore this file and starts again
			} catch (IOException e1) {
				e1.printStackTrace();
			}				
		}
	}
}
