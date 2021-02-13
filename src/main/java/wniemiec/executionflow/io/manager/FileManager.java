package wniemiec.executionflow.io.manager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import wniemiec.executionflow.App;
import wniemiec.executionflow.invoked.InvokedInfo;
import wniemiec.executionflow.io.FileEncoding;
import wniemiec.executionflow.io.compiler.Compiler;
import wniemiec.executionflow.io.compiler.CompilerFactory;
import wniemiec.executionflow.io.processor.factory.FileProcessorFactory;
import wniemiec.executionflow.io.processor.fileprocessor.FileProcessor;
import wniemiec.executionflow.lib.LibraryManager;
import wniemiec.util.logger.Logger;

/**
 * Responsible for managing file processing and compilation for a file.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		6.0.0
 * @since		1.3
 */
public class FileManager implements Serializable {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final long serialVersionUID = 600L;
	private transient Path srcFile;
	private transient Path srcFileBackup; 
	private transient Path binFile;
	private transient Path binFileBackup;
	private transient Path binDirectory;
	private FileProcessor fileProcessor;
	private boolean encodingError;
	private boolean lastWasError;
	private boolean autoRestore;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------	
	/**
	 * Manages file analyzer and compiler.
	 * 
	 * @param		srcFilePath Path of java file
	 * @param		binDirectory Path of directory where .class of java file is
	 * @param		classPackage Package of the class of the java file
	 * @param		backupExtensionName Backup file extension name
	 * @param		fileParserFactory Factory that will produce 
	 * {@link FileProcessor} that will be used for parsing file
	 * 
	 * @throws		IllegalArgumentException If srcFilePath does not exist
	 */
	private FileManager(Path srcFilePath, Path binDirectory,
						String classPackage, String backupExtensionName, 
						FileProcessorFactory fileParserFactory) {
		checkSrcPath(srcFilePath);
		
		String filename = extractFilenameWithoutExtension(srcFilePath);
		
		this.binDirectory = extractRootBinDirectory(binDirectory, classPackage);
		
		initializeBinFile(binDirectory, backupExtensionName, filename);
		initializeSrcFile(srcFilePath, backupExtensionName);
		initializeFileProcessor(binDirectory, fileParserFactory, filename);
	}
		
	
	//-------------------------------------------------------------------------
	//		Builder
	//-------------------------------------------------------------------------
	/**
	 * Builder for {@link InvokedInfo}. It is necessary to provide all required
	 * fields. The required fields are: <br />
	 * <ul>
	 * 	<li>srcPath</li>
	 * 	<li>binDirectory</li>
	 * 	<li>classPackage</li>
	 * 	<li>backupExtensionName</li>
	 * 	<li>fileParserFactory</li>
	 * </ul>
	 */
	public static class Builder {
		
		private Path srcPath;
		private Path binDirectory;
		private String classPackage;
		private String backupExtensionName;
		private FileProcessorFactory fileParserFactory;
		

		/**
		 * @param		srcPath Path where invoked's source file is
		 * 
		 * @return		Builder to allow chained calls
		 * 
		 * @throws		IllegalArgumentException If srcPath is null
		 */
		public Builder srcPath(Path srcPath) {
			checkSrcPath(srcPath);
			
			this.srcPath = srcPath.isAbsolute() ? srcPath : srcPath.toAbsolutePath();
			
			return this;
		}
		
		/**
		 * @param		binDirectory Path of directory where .class of java
		 * file is
		 * 
		 * @return		Builder to allow chained calls
		 * 
		 * @throws		IllegalArgumentException If srcPath is null
		 */
		public Builder binDirectory(Path binDirectory) {
			this.binDirectory = binDirectory;
			
			return this;
		}
		
		/**
		 * @param		classPackage Package of the class of the java file
		 * 
		 * @return		Builder to allow chained calls
		 * 
		 * @throws		IllegalArgumentException If srcPath is null
		 */
		public Builder classPackage(String classPackage) {
			this.classPackage = classPackage;
			
			return this;
		}
		
		/**
		 * @param		backupExtensionName Backup file extension name
		 * 
		 * @return		Builder to allow chained calls
		 * 
		 * @throws		IllegalArgumentException If srcPath is null
		 */
		public Builder backupExtensionName(String backupExtensionName) {
			this.backupExtensionName = backupExtensionName;
			
			return this;
		}
		
		/**
		 * @param		fileParserFactory Factory that will produce 
		 * {@link FileProcessor} that will be used for parsing file
		 * 
		 * @return		Builder to allow chained calls
		 * 
		 * @throws		IllegalArgumentException If srcPath is null
		 */
		public Builder fileParserFactory(FileProcessorFactory fileParserFactory) {
			this.fileParserFactory = fileParserFactory;
			
			return this;
		}
		
		/**
		 * Creates {@link FileManager} with provided information. It is 
		 * necessary to provide all required fields.. The required fields 
		 * are: <br />
		 * <ul>
		 * 	<li>srcPath</li>
		 * 	<li>binDirectory</li>
		 * 	<li>classPackage</li>
		 * 	<li>backupExtensionName</li>
		 * 	<li>fileParserFactory</li>
		 * </ul>
		 * 
		 * @return		File manager with provided information
		 * 
		 * @throws		IllegalArgumentException If any required field is null
		 */
		public FileManager build() {
			return new FileManager(
					srcPath,
					binDirectory,
					classPackage,
					backupExtensionName,
					fileParserFactory
			);
		}
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	private static void checkSrcPath(Path srcPath) {
		if (srcPath == null) {
			throw new IllegalArgumentException("Invoked's source file path"
					+ "cannot be null");
		}
	}
	
	public String extractFilenameWithoutExtension(Path file) {
		String filenameWithExtension = file.getName(file.getNameCount()-1).toString(); 
		
		return filenameWithExtension.split("\\.")[0];
	}
	
	private Path extractRootBinDirectory(Path binDirectory, String classPackage) {
		Path rootBinDirectory = binDirectory;
		
		int packageFolders = 0;
		
		if ((classPackage != null) && !classPackage.isEmpty())
			packageFolders = classPackage.split("\\.").length;
		
		for (int i=0; i<packageFolders; i++) {
			rootBinDirectory = rootBinDirectory.getParent();
		}
		
		rootBinDirectory = fixOrgPackage(rootBinDirectory);
		
		return rootBinDirectory;
	}

	private Path fixOrgPackage(Path rootBinDirectory) {
		final String regexOrgPkg = ".+(\\/|\\\\)org$";
		
		if (!rootBinDirectory.toAbsolutePath().toString().matches(regexOrgPkg))
			return rootBinDirectory;
		
		return rootBinDirectory.getParent();
	}
	
	private void initializeBinFile(Path binDirectory, String backupExtensionName, 
								   String filename) {
		this.binFile = binDirectory.resolve(Path.of(filename + ".class"));
		this.binFileBackup = binDirectory.resolve(
				Path.of(filename + ".class." + backupExtensionName)
		);
	}

	private void initializeSrcFile(Path srcFilePath, String backupExtensionName) {
		this.srcFile = srcFilePath;
		this.srcFileBackup = Path.of(
				srcFilePath.toAbsolutePath().toString() 
				+ "." 
				+ backupExtensionName
		);
	}

	private void initializeFileProcessor(Path binDirectory, 
										 FileProcessorFactory fileParserFactory, 
										 String filename) {
		this.fileProcessor = fileParserFactory.newFileProcessor(
				srcFile, 
				binDirectory, 
				filename + "_parsed", 
				FileEncoding.UTF_8
		);
	}
	
	/**
	 * Parses and process file, saving modified file in the same file passed 
	 * to constructor.
	 * 
	 * @param		autoRestore Checks if processed files exist against the 
	 * current file. If so, restore them before processing. Default is true.
	 * 
	 * @return		Itself to allow chained calls
	 * 
	 * @throws		IOException If file encoding cannot be defined
	 * 
	 * @implNote	This function overwrite file passed to the constructor! To
	 * restore the original file, call {@link #revertProcessing()} function.
	 */
	public FileManager processFile(boolean autoRestore) throws IOException {
//		if (autoRestore)
		this.autoRestore = autoRestore;
			createSrcBackupFile();
		
		Path processedFile = processFile();
		
		storeProcessedFile(processedFile);
		
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
	public FileManager createSrcBackupFile() {
		createBackupFile(srcFile, srcFileBackup);
		
		return this;
	}
	
	private void createBackupFile(Path file, Path bkpFile) {
		try {
			Files.copy(file, bkpFile, StandardCopyOption.COPY_ATTRIBUTES);
		} 
		catch (IOException e) {				// If already exists a backup file, this means
			try {							// that last parsed file was not restored
				if (autoRestore)			// So, restore this file and starts again
					revertProcessing();
				else
					Files.deleteIfExists(bkpFile);
				
				if (!lastWasError) {		
					lastWasError = true;
					createBackupFile(file, bkpFile);	
					lastWasError = false;
				}
			} 
			catch (IOException e1) {
				Logger.error(e1.getMessage());
			}				
		}
	}
	
	/**
	 * Deletes modified file and restores original file. This function does not
	 * delete .class file of modified file, only .java file.
	 * 
	 * @return		Itself to allow chained calls
	 * 
	 * @throws		IOException If file has not a backup file
	 */
	public FileManager revertProcessing() throws IOException {
		if (!hasSrcBackupStored())
			return this;

		try {
			Files.move(srcFileBackup, srcFile, StandardCopyOption.REPLACE_EXISTING);
		} 
		catch (IOException e) {
			throw new IOException("Revert processing without backup");
		}
		
		return this;
	}
	
	/**
	 * Checks whether file has source backup file.
	 * 
	 * @return		True if file has source backup file; false otherwise
	 */
	public boolean hasSrcBackupStored() {
		return Files.exists(srcFileBackup);
	}

	private Path processFile() throws IOException {
		Path processedFile;
		
		try {	
			processedFile = processFileUsingEncode(FileEncoding.UTF_8);
		} 
		catch(IOException e) {	
			encodingError = true;
			
			try {
				processedFile = processFileUsingEncode(FileEncoding.ISO_8859_1);
			} 
			catch (IOException e1) {
				throw new IOException("Processing failed - " + e1.getMessage());
			}
		}
		
		return processedFile;
	}

	private Path processFileUsingEncode(FileEncoding encoding) throws IOException {
		fileProcessor.setEncoding(encoding);

		return Path.of(fileProcessor.processFile());
	}
	
	private void storeProcessedFile(Path processedFile) throws IOException {
		if (!Files.exists(processedFile))
			return;
		
		Files.move(processedFile, srcFile, StandardCopyOption.REPLACE_EXISTING);
	}
	
	/**
	 * Compiles processed file.
	 *  
	 * @return		Itself to allow chained calls
	 * 
	 * @throws		IOException If an error occurs during compilation
	 * @throws		NoClassDefFoundError If aspectjtools.jar is not found
	 */
	public FileManager compileFile() throws IOException {
		Compiler compiler = CompilerFactory.createStandardAspectJCompiler()
				.inpath(generateAspectsRootDirectory())
				.classpath(generateClasspath())
				.build();

		try {
			if (encodingError)	
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
		if (App.isDevelopment()) {
			return App.getAppRootPath().resolve(
					Path.of("target", "classes", "wniemiec", "executionflow", "runtime")
			);
		}
		
		return App.getAppRootPath().resolve(
				Path.of("classes", "wniemiec", "executionflow", "runtime")
		);
	}

	private List<Path> generateClasspath() {
		List<Path> classPaths = new ArrayList<>();
		
		classPaths.addAll(LibraryManager.getJavaClassPath());
		classPaths.add(LibraryManager.getLibrary("JUNIT_4"));
		classPaths.add(LibraryManager.getLibrary("HAMCREST"));
		classPaths.add(LibraryManager.getLibrary("ASPECTJRT"));
		classPaths.add(LibraryManager.getLibrary("JUNIT_5_API"));
		classPaths.add(LibraryManager.getLibrary("JUNIT_5_PARAMS"));
		
		return classPaths;
	}
	
	/**
	 * Deletes modified .class file and restores original .class file.
	 * 
	 * @return		Itself to allow chained calls
	 * 
	 * @throws		IOException If file has not a backup file
	 */
	public FileManager revertCompilation() throws IOException {
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
	 * @return		True if file has class backup file; false otherwise
	 */
	public boolean hasBinBackupStored() {
		return Files.exists(binFileBackup);
	}
	
	/**
	 * Creates a copy of binary file passed to the constructor to allow to 
	 * restore it after.
	 * 
	 * @return		Itself to allow chained calls
	 * 
	 * @implNote		Backup name will be &lt; name_of_file.original.class &gt;.
	 * It will be saved in the same directory of the original file
	 */
	public FileManager createBackupBinFile() {
		createBackupFile(binFile, binFileBackup);
		
		return this;
	}
	
	@Override
	public String toString() {
		return "FileManager ["
				+ "srcFile=" + srcFile
				+ ", originalSrcFile=" + srcFileBackup 
				+ ", compiledFile="	+ binFile 
				+ ", binFileBackup=" + binFileBackup
				+ ", binDirectory=" + binDirectory
				+ ", fileProcessor=" + fileProcessor 
			+ "]";
	}
	
	@Override
	public int hashCode() {
		return srcFile.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)						{ return false;	}
		if (obj == this)						{ return true;	}
		if (this.getClass() != obj.getClass())	{ return false;	}
		
		return this.srcFile.equals(((FileManager)obj).getSrcFile());
	}	
	
	
	//-------------------------------------------------------------------------
	//		Getters
	//-------------------------------------------------------------------------
	public Path getSrcFile() {
		return srcFile;
	}
	
	public Path getCompiledFile() {
		return binFile;
	}
	
	
	//-------------------------------------------------------------------------
	//		Serialization and deserialization methods
	//-------------------------------------------------------------------------
	private void writeObject(ObjectOutputStream oos) {
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
	
	private void readObject(ObjectInputStream ois) {
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