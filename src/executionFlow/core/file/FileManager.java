package executionFlow.core.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import executionFlow.core.file.parser.FileParser;
import executionFlow.core.file.parser.factory.FileParserFactory;


/**
 * Responsible for managing file parser and compiler.
 */
public class FileManager 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private File inputFile;
	private File originalFile; 
	private String filename;
	private String classOutput;
	private String classPackage;
	private boolean charsetError;
	private FileParser fp;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	/**
	 * Manages file analyzer and compiler.
	 * 
	 * @param srcFilePath Absolute path of java file
	 * @param classOutput Absolute path of directory where .class of java file is
	 * @param classPackage Package of the class of the java file
	 * @param fileParserFactory Factory that will produce {@link FileParser} 
	 * that will be used for parsing file
	 */
	public FileManager(String srcFilePath, String classOutput, String classPackage, FileParserFactory fileParserFactory)
	{
		this.classOutput = classOutput;
		this.classPackage = classPackage;
		this.inputFile = new File(srcFilePath);
		this.originalFile = new File(srcFilePath+".original"); 
		this.filename = inputFile.getName().split("\\.")[0];
		this.fp = fileParserFactory.newFileParser(
			inputFile.getAbsolutePath(), classOutput, 
			filename+"_parsed", FileEncoding.UTF_8
		);
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Deletes modified file and restores original file. This function does not
	 * delete .class file of modified file, only .java file.
	 * 
	 * @return This object to allow chained calls
	 */
	public FileManager revert()
	{
		inputFile.delete();
		originalFile.renameTo(inputFile);
		
		return this;
	}
	
	public void revertCompilation()
	{}
	
	/**
	 * Parses and process file, saving modified file in the same file passed 
	 * to constructor.
	 * 
	 * @return This object to allow chained calls
	 * @throws IOException If file encoding cannot be defined
	 * 
	 * @implNote This function overwrite file passed to the constructor! To
	 * restore the original file, call {@link #revert()} function.
	 */
	public FileManager parseFile() throws IOException
	{
		// Saves .java file to allow to restore it after
		createBackupFile();
		
		// Parses file
		File out;
		
		// Tries to parse file using UTF-8 encoding. If an error occurs, tries 
		// to parse the file using ISO-8859-1 encoding
		try {	
			out = new File(fp.parseFile());
		} catch(IOException e) {	
			charsetError = true;
			fp.setCharset(FileEncoding.ISO_8859_1);
			
			try {
				out = new File(fp.parseFile());
			} catch (IOException e1) {
				throw new IOException("Parsing failed");
			}
		}
		
		// Changes parsed file name to the same as received filename
		inputFile.delete();
		out.renameTo(inputFile);
		
		return this;
	}
	
	/**
	 * Compiles processed file.
	 *  
	 * @return Path of compiled file
	 * @throws Exception If an error occurs
	 */
	public String compileFile() throws Exception
	{
		int packageFolders = classPackage.split("\\.").length;
		Path file = Paths.get(classOutput);
		
		// Sets path to the compiler
		for (int i=0; i<packageFolders; i++) {
			file = file.getParent();
		}
		
		// Compiles parsed file. If an error has occurred in parsing, compiles 
		// using ISO-8859-1 encoding
		if (charsetError)	
			return FileCompiler.compile(inputFile, file.toString(), FileEncoding.ISO_8859_1);
		else
			return FileCompiler.compile(inputFile, file.toString(), FileEncoding.UTF_8);
	}
	
	/**
	 * Creates a copy of file passed to the constructor to allow to restore 
	 * it after.
	 * 
	 * @implNote Backup name will be &lt;<b>name_of_file</b>.original.java&gt;.
	 * It will be saved in the same directory of the original file
	 */
	private void createBackupFile()
	{
		try {
			Files.copy(
				inputFile.toPath(), 
				originalFile.toPath(), 
				StandardCopyOption.COPY_ATTRIBUTES
			);
		} catch (IOException e) {	// If already exists a .original, this means
			revert();				// that last parsed file was not restored
			createBackupFile();		// So, restore this file and starts again
		}
	}
}
