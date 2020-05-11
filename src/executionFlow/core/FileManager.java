package executionFlow.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


/**
 * Responsible for managing file parser and compiler.
 */
public class FileManager 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private String filename;
	private File inputFile;
	private File originalFile; 
	private String classOutput;
	private String classPackage;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	/**
	 * Manages file analyzer and compiler.
	 * 
	 * @param srcFilePath Path of java file
	 * @param classOutput Directory where .class of java file is
	 * @param classPackage Package of the class of the java file 
	 */
	public FileManager(String srcFilePath, String classOutput, String classPackage)
	{
		this.classOutput = classOutput;
		this.classPackage = classPackage;
		this.inputFile = new File(srcFilePath);
		this.originalFile = new File(srcFilePath+".original"); 
		this.filename = inputFile.getName().split("\\.")[0];
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
	
	/**
	 * Parses and process file, saving modified file in the same file passed 
	 * to constructor.
	 * 
	 * @return This object to allow chained calls
	 * 
	 * @implNote This function overwrite file passed to the constructor! To
	 * restore the original file, call {@link #revert()} function.
	 */
	public FileManager parseFile()
	{
		// Saves .java file to allow to restore it after
		createBackupFile();
		
		// Parses file
		FileParser fp = new FileParser(inputFile.getAbsolutePath(), inputFile.getParent(), filename+"_parsed");
		File out = new File(fp.parseFile());
		
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
		
		// Compiles parsed file
		return FileCompiler.compile(inputFile, file.toString());
	}
	
	/**
	 * Creates a copy of file passed to the constructor to allow to restore 
	 * it after.
	 * 
	 * @implNote Backup name will be &lt;<b>name_of_file</b>.original.java&gt;.
	 * It will be saved in the same directory of original file
	 */
	private void createBackupFile()
	{
		try {
			Files.copy(
				inputFile.toPath(), 
				originalFile.toPath(), 
				StandardCopyOption.REPLACE_EXISTING,
				StandardCopyOption.COPY_ATTRIBUTES
			);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
