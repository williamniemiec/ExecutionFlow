package executionFlow.io.manager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

import executionFlow.ExecutionFlow;
import executionFlow.LibraryManager;
import executionFlow.io.FileEncoding;
import executionFlow.io.compiler.Compiler;
import executionFlow.io.compiler.CompilerFactory;
import executionFlow.io.processor.FileProcessor;
import executionFlow.io.processor.factory.FileProcessorFactory;
import executionFlow.util.FileUtil;
import executionFlow.util.Logger;


/**
 * Responsible for managing file processing and compilation for a file.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.3
 * @since		1.3
 */
public class FileManager implements Serializable
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final long serialVersionUID = 200L;
	
	private transient Path srcFile;
	private transient Path srcFileBackup; 
	private transient Path binFile;
	private transient Path binFileBackup;
	private transient Path binDirectory;
	private FileProcessor fileProcessor;
	private boolean charsetError;
	private boolean lastWasError;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------	
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
		
		String filename = FileUtil.extractFilenameWithoutExtension(srcFilePath);
		
		this.binDirectory = extractRootBinDirectory(binDirectory, classPackage);
		this.binFile = binDirectory.resolve(Path.of(filename + ".class"));
		this.binFileBackup = binDirectory.resolve(
				Path.of(filename + ".class." + backupExtensionName)
		);
		
		this.srcFile = srcFilePath;
		this.srcFileBackup = Path.of(
				srcFilePath.toAbsolutePath().toString() 
				+ "." + backupExtensionName
		);
		
		this.fileProcessor = fileParserFactory.newFileProcessor(
				srcFile, 
				binDirectory, 
				filename+"_parsed", 
				FileEncoding.UTF_8
		);
	}

	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	private Path extractRootBinDirectory(Path binDirectory, String classPackage) {
		Path rootBinDirectory = binDirectory;
		
		int packageFolders = 0;
		
		if ((classPackage != null) && !classPackage.isEmpty())
			packageFolders = classPackage.split("\\.").length;
		
		for (int i=0; i<packageFolders; i++) {
			rootBinDirectory = rootBinDirectory.getParent();
		}
		
		if (rootBinDirectory.toAbsolutePath().toString().matches(".+(\\/|\\\\)org$")) {
			rootBinDirectory = rootBinDirectory.getParent();
		}
		
		return rootBinDirectory;
	}
	
	@Override
	public String toString() 
	{
		return "FileManager ["
				+ "srcFile=" + srcFile
				+ ", originalSrcFile=" + srcFileBackup 
				+ ", compiledFile="	+ binFile 
				+ ", originalClassPath=" + binFileBackup
				+ ", classOutput=" + binDirectory
				+ ", fileProcessor=" + fileProcessor 
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
	 * @param		collectors Information about all invoked collected
	 * @param		autoRestore Checks if processed files exist against the 
	 * current file. If so, restore them before processing. Default is true.
	 * 
	 * @return		This object to allow chained calls
	 * 
	 * @throws		IOException If file encoding cannot be defined
	 * 
	 * @implNote	This function overwrite file passed to the constructor! To
	 * restore the original file, call {@link #revertProcessing()} function.
	 */
	public FileManager processFile(boolean autoRestore) throws IOException
	{
		if (autoRestore)
			createSrcBackupFile();
		
		Path processedFile = processFile();
		
		storeProcessedFile(processedFile);
		
		return this;
	}

	private Path processFile() throws IOException {
		Path processedFile;
		
		try {	
			processedFile = processFileUsing(FileEncoding.UTF_8);
		} 
		catch(IOException e) {	
			charsetError = true;
			
			try {
				processedFile = processFileUsing(FileEncoding.ISO_8859_1);
			} 
			catch (IOException e1) {
				throw new IOException("Parsing failed");
			}
		}
		
		return processedFile;
	}

	private Path processFileUsing(FileEncoding encoding) throws IOException {
		fileProcessor.setEncoding(encoding);

		return Path.of(fileProcessor.processFile());
	}

	private void storeProcessedFile(Path processedFile) throws IOException {
		// Changes parsed file name to the same as received filename
		if (Files.exists(processedFile)) {
			Files.move(processedFile, srcFile, StandardCopyOption.REPLACE_EXISTING);
		}
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
		Compiler compiler = CompilerFactory.createStandardAspectJCompiler()
				.inpath(generateAspectsRootDirectory())
				.classpath(generateClasspath())
				.build();

		try {
			if (charsetError)	
				compiler.compile(srcFile, binDirectory, FileEncoding.ISO_8859_1);
			else
				compiler.compile(srcFile, binDirectory, FileEncoding.UTF_8);
		} 
		catch (java.lang.NoClassDefFoundError e) {
			Logger.error("aspectjtools.jar not found");
			throw e;
		}
		
		return this;
	}

	private Path generateAspectsRootDirectory() {
		return ExecutionFlow.isDevelopment() ? 
				ExecutionFlow.getAppRootPath().resolve(Path.of("bin", "executionFlow", "runtime")) 
				: ExecutionFlow.getAppRootPath().resolve(Path.of("executionFlow", "runtime"));
	}

	private List<Path> generateClasspath() {
		return List.of(
				Path.of(System.getProperty("java.class.path")),
				LibraryManager.getLibrary("JUNIT_4"),
				LibraryManager.getLibrary("HAMCREST"),
				LibraryManager.getLibrary("ASPECTJRT"),
				LibraryManager.getLibrary("JUNIT_5_API"),
				LibraryManager.getLibrary("JUNIT_5_PARAMS")
		);
	}
	
	/**
	 * Deletes modified file and restores original file. This function does not
	 * delete .class file of modified file, only .java file.
	 * 
	 * @return		This object to allow chained calls
	 * 
	 * @throws		IOException If file has not a backup file
	 */
	public FileManager revertProcessing() throws IOException
	{
		if (!hasSrcBackupStored())
			return this;
		
		try {
			Files.move(srcFileBackup, srcFile, StandardCopyOption.REPLACE_EXISTING);
		} 
		catch (IOException e) {
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
		if (!hasBinBackupStored())
			return this;
		
		try {
			Files.move(binFileBackup, binFile, StandardCopyOption.REPLACE_EXISTING);
		} 
		catch (IOException e) {
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
		return Files.exists(binFileBackup);
	}
	
	/**
	 * Checks whether file has source backup file.
	 * 
	 * @return		If file has source backup file
	 */
	public boolean hasSrcBackupStored()
	{
		return Files.exists(srcFileBackup);
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
				binFileBackup, 
				StandardCopyOption.COPY_ATTRIBUTES
			);
		} 
		catch (IOException e) {			// If already exists a backup file, this means
			try {						// that last compiled file was not restored
				revertCompilation();	
				
				if (!lastWasError) {
					lastWasError = true;
					createBackupBinFile();	// So, restore this file and starts again
					lastWasError = false;
				}
			} 
			catch (IOException e1) {
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
				srcFileBackup, 
				StandardCopyOption.COPY_ATTRIBUTES
			);
		} 
		catch (IOException e) {		// If already exists a backup file, this means
			try {					// that last parsed file was not restored
				revertProcessing();
				
				if (!lastWasError) {
					lastWasError = true;
					createSrcBackupFile();	// So, restore this file and starts again
					lastWasError = false;
				}
			} 
			catch (IOException e1) {
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
			oos.writeUTF(srcFileBackup.toAbsolutePath().toString());
			oos.writeUTF(binFileBackup.toAbsolutePath().toString());
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void readObject(ObjectInputStream ois)
	{
		try {
			ois.defaultReadObject();
			this.srcFile = Path.of(ois.readUTF());
			this.binDirectory = Path.of(ois.readUTF());
			this.binFile = Path.of(ois.readUTF());
			this.srcFileBackup = Path.of(ois.readUTF());
			this.binFileBackup = Path.of(ois.readUTF());
		} 
		catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}
}