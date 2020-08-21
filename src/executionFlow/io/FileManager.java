package executionFlow.io;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

import executionFlow.info.CollectorInfo;
import executionFlow.io.processor.FileProcessor;
import executionFlow.io.processor.factory.FileProcessorFactory;
import executionFlow.util.ConsoleOutput;


/**
 * Responsible for managing file processing and compilation for a file.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		4.0.0
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
	private transient Path binFile;
	private transient Path originalBinFile;
	private transient Path binDirectory;
	private String classSignature;
	private String filename;
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
	 * @param		classSignature Signature of the public class of the file
	 * @param		srcFilePath Path of java file
	 * @param		binDirectory Path of directory where .class of java file is
	 * @param		classPackage Package of the class of the java file
	 * @param		fileParserFactory Factory that will produce 
	 * {@link FileProcessor} that will be used for parsing file
	 */
	public FileManager(String classSignature, Path srcFilePath, Path binDirectory, 
			String classPackage, FileProcessorFactory fileParserFactory)
	{
		this(classSignature, srcFilePath, binDirectory, classPackage, fileParserFactory, "original");
	}
	
	/**
	 * Manages file analyzer and compiler.
	 * 
	 * @param		classSignature Signature of the public class of the file
	 * @param		srcFilePath Path of java file
	 * @param		binDirectory Path of directory where .class of java file is
	 * @param		classPackage Package of the class of the java file
	 * @param		fileParserFactory Factory that will produce 
	 * {@link FileProcessor} that will be used for parsing file
	 * @param		backupExtensionName Backup file extension name
	 * 
	 * @throws		IllegalArgumentException If srcFilePath does not exist
	 */
	public FileManager(String classSignature, Path srcFilePath, Path binDirectory, 
			String classPackage, FileProcessorFactory fileParserFactory, String backupExtensionName)
	{
		if (!Files.exists(srcFilePath))
			throw new IllegalArgumentException("srcFilePath does not exist: " + srcFilePath);
		
		this.srcFile = srcFilePath;
		this.binDirectory = binDirectory;
		this.classPackage = classPackage;
		this.filename = srcFilePath.getName(srcFilePath.getNameCount()-1).toString().split("\\.")[0];
		this.fp = fileParserFactory.newFileProcessor(
			srcFile, 
			binDirectory, 
			filename+"_parsed", 
			FileEncoding.UTF_8
		);
		this.binFile = Path.of(binDirectory+"/"+filename+".class");
		this.originalSrcFile = Path.of(srcFilePath.toAbsolutePath().toString()+"."+backupExtensionName); 
		this.originalBinFile = Path.of(binDirectory+"/"+filename+".class."+backupExtensionName);
		this.classSignature = classSignature;
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	public String toString() 
	{
		return "FileManager ["
				+ "classSignature=" + classSignature
				+ ", srcFile=" + srcFile 
				+ ", originalSrcFile=" + originalSrcFile 
				+ ", compiledFile="	+ binFile 
				+ ", originalClassPath=" + originalBinFile 
				+ ", filename=" + filename
				+ ", classOutput=" + binDirectory 
				+ ", classPackage=" + classPackage 
				+ ", fp=" + fp 
				+ ", charsetError="	+ charsetError 
				+ ", lastWasError=" + lastWasError 
			+ "]";
	}
	
	@Override
	public int hashCode()
	{
		return classSignature.hashCode();
	}

	@Override
	public boolean equals(Object obj) 
	{
		if (obj == null)						{ return false;	}
		if (obj == this)						{ return true;	}
		if (this.getClass() != obj.getClass())	{ return false;	}
		
		return this.classSignature.equals(((FileManager)obj).getClassSignature());
	}

	/**
	 * Parses and process file, saving modified file in the same file passed 
	 * to constructor.
	 * 
	 * @param		collectors Information about all invoked collected
	 * @param		autoRestore Checks if processed files exist against the 
	 * current file. If so, restore them before processing. Default is true.
	 * 
	 * @return		This object to allow chained calls
	 * 
	 * @throws		IOException If file encoding cannot be defined
	 * 
	 * @implNote	This function overwrite file passed to the constructor! To
	 * restore the original file, call {@link #revertParse()} function.
	 */
	public FileManager parseFile(List<CollectorInfo> collectors, boolean autoRestore) throws IOException
	{
		// Saves .java file to allow to restore it after
		if (autoRestore)
			createSrcBackupFile();
		
		// Parses file
		Path out;
		
		// Tries to parse file using UTF-8 encoding. If an error occurs, tries 
		// to parse the file using ISO-8859-1 encoding
		try {	
			out = Path.of(fp.processFile(collectors));
		} catch(IOException e) {	
			charsetError = true;
			fp.setEncoding(FileEncoding.ISO_8859_1);
			
			try {
				out = Path.of(fp.processFile(collectors));
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
	 * Parses and process file, saving modified file in the same file passed 
	 * to constructor.
	 * 
	 * @param		collectors Information about all invoked collected
	 * 
	 * @return		This object to allow chained calls
	 * 
	 * @throws		IOException If file encoding cannot be defined
	 * 
	 * @implNote	This function overwrite file passed to the constructor! To
	 * restore the original file, call {@link #revertParse()} function.
	 */
	public FileManager parseFile(List<CollectorInfo> collectors) throws IOException
	{
		return parseFile(collectors, true);
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
		return parseFile(null);
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
			binDirectory = binDirectory.getParent();
		}

		// Compiles parsed file. If an error has occurred in parsing, compiles 
		// using ISO-8859-1 encoding
		try {
			if (charsetError)	
				FileCompiler.compile(srcFile, binDirectory, FileEncoding.ISO_8859_1);
			else
				FileCompiler.compile(srcFile, binDirectory, FileEncoding.UTF_8);
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
			if (Files.exists(originalBinFile)) {
				
				try {
					Files.delete(binFile);
				} catch (IOException e) { }
				
				Files.move(originalBinFile, binFile);
			}
		} catch (IOException e) {
			throw new IOException("Revert compilation without backup");
		}
		
		return this;
	}
	
	/**
	 * Checks whether file has binary backup files (compiled files).
	 * 
	 * @return		If file has class backup file
	 */
	public boolean hasBinBackupStored()
	{
		return Files.exists(originalBinFile);
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
	 * Creates a copy of binary file passed to the constructor to allow to 
	 * restore it after.
	 * 
	 * @return		Itself to allow chained calls
	 * 
	 * @implNote		Backup name will be &lt;<b>name_of_file</b>.original.class&gt;.
	 * It will be saved in the same directory of the original file
	 */
	public FileManager createBackupBinFile()
	{
		try {
			Files.copy(
				binFile,
				originalBinFile, 
				StandardCopyOption.COPY_ATTRIBUTES
			);
		} catch (IOException e) {			// If already exists a .original, this means
			try {							// that last compiled file was not restored
				revertCompilation();	
				if (!lastWasError) {
					lastWasError = true;
					createBackupBinFile();	// So, restore this file and starts again
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
		return binFile;
	}
	
	public String getClassSignature()
	{
		return classSignature;
	}
	
	
	//-------------------------------------------------------------------------
	//		Serialization and deserialization methods
	//-------------------------------------------------------------------------
	private void writeObject(ObjectOutputStream oos)
	{
		try {
			oos.defaultWriteObject();
			oos.writeUTF(srcFile.toAbsolutePath().toString());
			oos.writeUTF(binDirectory.toAbsolutePath().toString());
			oos.writeUTF(binFile.toAbsolutePath().toString());
			oos.writeUTF(originalSrcFile.toAbsolutePath().toString());
			oos.writeUTF(originalBinFile.toAbsolutePath().toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void readObject(ObjectInputStream ois)
	{
		try {
			ois.defaultReadObject();
			this.srcFile = Path.of(ois.readUTF());
			this.binDirectory = Path.of(ois.readUTF());
			this.binFile = Path.of(ois.readUTF());;
			this.originalSrcFile = Path.of(ois.readUTF());;
			this.originalBinFile = Path.of(ois.readUTF());;
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}
}
