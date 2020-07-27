package executionFlow.io;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import executionFlow.io.processor.FileProcessor;
import executionFlow.io.processor.factory.FileProcessorFactory;
import executionFlow.util.ConsoleOutput;


/**
 * Responsible for managing file processing and compilation for a file.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		2.0.2
 * @since		1.3
 */
public class FileManager implements Serializable
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final long serialVersionUID = 200L;
	
	private transient Path srcFile;
	private transient Path originalSrcFile; 
	private transient Path compiledFile;
	private transient Path originalCompiledFile;
	private String filename;
	private transient Path classOutput;
	private String classPackage;
	private FileProcessor fp;
	private boolean charsetError;
	private boolean lastWasError;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	/**
	 * Manages file analyzer and compiler. Using this constructor, backup files
	 * will end with '.original'.
	 * 
	 * @param		srcFilePath Path of java file
	 * @param		classOutput Path of directory where .class of java file is
	 * @param		classPackage Package of the class of the java file
	 * @param		fileParserFactory Factory that will produce 
	 * {@link FileProcessor} that will be used for parsing file
	 */
	public FileManager(Path srcFilePath, Path classOutput, String classPackage, 
			FileProcessorFactory fileParserFactory)
	{
		this(srcFilePath, classOutput, classPackage, fileParserFactory, "original");
	}
	
	/**
	 * Manages file analyzer and compiler.
	 * 
	 * @param		srcFilePath Path of java file
	 * @param		classOutput Path of directory where .class of java file is
	 * @param		classPackage Package of the class of the java file
	 * @param		fileParserFactory Factory that will produce 
	 * {@link FileProcessor} that will be used for parsing file
	 * @param		backupExtensionName Backup file extension name
	 * 
	 * @throws		IllegalArgumentException If srcFilePath does not exist
	 */
	public FileManager(Path srcFilePath, Path classOutput, String classPackage, 
			FileProcessorFactory fileParserFactory, String backupExtensionName)
	{
		if (!Files.exists(srcFilePath))
			throw new IllegalArgumentException("srcFilePath does not exist: " + srcFilePath);
		
		this.srcFile = srcFilePath;
		this.classOutput = classOutput;
		this.classPackage = classPackage;
		this.filename = srcFilePath.getName(srcFilePath.getNameCount()-1).toString().split("\\.")[0];
		this.fp = fileParserFactory.newFileProcessor(
			srcFile, 
			classOutput, 
			filename+"_parsed", 
			FileEncoding.UTF_8
		);
		this.compiledFile = Path.of(classOutput+"/"+filename+".class");
		this.originalSrcFile = Path.of(srcFilePath.toAbsolutePath().toString()+"."+backupExtensionName); 
		this.originalCompiledFile = Path.of(classOutput+"/"+filename+".class."+backupExtensionName);
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	public String toString() 
	{
		return "FileManager ["
				+ "srcFile=" + srcFile 
				+ ", originalSrcFile=" + originalSrcFile 
				+ ", compiledFile="	+ compiledFile 
				+ ", originalClassPath=" + originalCompiledFile 
				+ ", filename=" + filename
				+ ", classOutput=" + classOutput 
				+ ", classPackage=" + classPackage 
				+ ", fp=" + fp 
				+ ", charsetError="	+ charsetError 
				+ ", lastWasError=" + lastWasError 
			+ "]";
	}
	
	@Override
	public int hashCode()
	{
		return srcFile.hashCode();
	}

	@Override
	public boolean equals(Object obj) 
	{
		if (obj == null)						{ return false;	}
		if (obj == this)						{ return true;	}
		if (this.getClass() != obj.getClass())	{ return false;	}
		
		return this.srcFile.equals(((FileManager)obj).getSrcFile());
	}

	/**
	 * Parses and process file, saving modified file in the same file passed 
	 * to constructor.
	 * 
	 * @return		This object to allow chained calls
	 * 
	 * @throws		IOException If file encoding cannot be defined
	 * 
	 * @implNote	This function overwrite file passed to the constructor! To
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
			out = Path.of(fp.processFile());
		} catch(IOException e) {	
			charsetError = true;
			fp.setEncoding(FileEncoding.ISO_8859_1);
			
			try {
				out = Path.of(fp.processFile());
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
	 * 
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
		try {
			if (charsetError)	
				FileCompiler.compile(srcFile, classOutput, FileEncoding.ISO_8859_1);
			else
				FileCompiler.compile(srcFile, classOutput, FileEncoding.UTF_8);
		} 
		catch (java.lang.NoClassDefFoundError e) {
			ConsoleOutput.showError("aspectjtools.jar not found");
			throw e;
		}
		
		return this;
	}
	
	/**
	 * Deletes modified file and restores original file. This function does not
	 * delete .class file of modified file, only .java file.
	 * 
	 * @return		This object to allow chained calls
	 * 
	 * @throws		IOException If file has not a backup file
	 */
	public FileManager revertParse() throws IOException
	{
		try {
			if (Files.exists(originalSrcFile)) {
				try {
					Files.delete(srcFile);
				} catch (IOException e) { }

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
	 * 
	 * @throws		IOException If file has not a backup file
	 */
	public FileManager revertCompilation() throws IOException
	{
		try {
			if (Files.exists(originalCompiledFile)) {
				
				try {
					Files.delete(compiledFile);
				} catch (IOException e) { }
				
				Files.move(originalCompiledFile, compiledFile);
			}
		} catch (IOException e) {
			throw new IOException("Revert compilation without backup");
		}
		
		return this;
	}
	
	/**
	 * Checks whether file has class backup file.
	 * 
	 * @return		If file has class backup file
	 */
	public boolean hasClassBackupStored()
	{
		return Files.exists(originalCompiledFile);
	}
	
	/**
	 * Checks whether file has source backup file.
	 * 
	 * @return		If file has source backup file
	 */
	public boolean hasSrcBackupStored()
	{
		return Files.exists(originalSrcFile);
	}
	
	/**
	 * Creates a copy of class file passed to the constructor to allow to 
	 * restore it after.
	 * 
	 * @return		Itself to allow chained calls
	 * 
	 * @implNote		Backup name will be &lt;<b>name_of_file</b>.original.class&gt;.
	 * It will be saved in the same directory of the original file
	 */
	public FileManager createBackupCompiledFile()
	{
		try {
			Files.copy(
				compiledFile,
				originalCompiledFile, 
				StandardCopyOption.COPY_ATTRIBUTES
			);
		} catch (IOException e) {			// If already exists a .original, this means
			try {							// that last compiled file was not restored
				revertCompilation();	
				if (!lastWasError) {
					lastWasError = true;
					createBackupCompiledFile();	// So, restore this file and starts again
					lastWasError = false;
				}
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
	 * @return		Itself to allow chained calls
	 * 
	 * @implNote		Backup name will be &lt;<b>name_of_file</b>.original.java&gt;.
	 * It will be saved in the same directory of the original file
	 */
	public FileManager createSrcBackupFile()
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
				if (!lastWasError) {
					lastWasError = true;
					createSrcBackupFile();	// So, restore this file and starts again
					lastWasError = false;
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}				
		}
		
		return this;
	}
	
	
	//-------------------------------------------------------------------------
	//		Getters
	//-------------------------------------------------------------------------
	public Path getSrcFile()
	{
		return srcFile;
	}
	
	public Path getCompiledFile()
	{
		return compiledFile;
	}
	
	
	//-------------------------------------------------------------------------
	//		Serialization and deserialization methods
	//-------------------------------------------------------------------------
	private void writeObject(ObjectOutputStream oos)
	{
		try {
			oos.defaultWriteObject();
			oos.writeUTF(srcFile.toAbsolutePath().toString());
			oos.writeUTF(classOutput.toAbsolutePath().toString());
			oos.writeUTF(compiledFile.toAbsolutePath().toString());
			oos.writeUTF(originalSrcFile.toAbsolutePath().toString());
			oos.writeUTF(originalCompiledFile.toAbsolutePath().toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void readObject(ObjectInputStream ois)
	{
		try {
			ois.defaultReadObject();
			this.srcFile = Path.of(ois.readUTF());
			this.classOutput = Path.of(ois.readUTF());
			this.compiledFile = Path.of(ois.readUTF());;
			this.originalSrcFile = Path.of(ois.readUTF());;
			this.originalCompiledFile = Path.of(ois.readUTF());;
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}
}
