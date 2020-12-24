package executionFlow.io.processor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import executionFlow.io.FileEncoding;
import executionFlow.io.processor.fileprocessor.FileProcessor;

public abstract class FileProcessorTest {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final String OUTPUT_SUFFIX = "_parsed";
	private String testMethodSignature;
	private String filename;
	private Path directory;
	private Path file;
	private FileEncoding encoding = FileEncoding.UTF_8;
	private FileProcessor fileProcessor;
	private Object[] testMethodArgs;
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	protected void withTestMethodSignature(String signature) {
		this.testMethodSignature = signature;
	}
	
	protected void withFilename(String filename) {
		this.filename = filename;
	}
	
	protected void withDirectory(Path directory) {
		this.directory = directory;
	}
	
	protected void usingEncoding(FileEncoding encoding) {
		if (encoding == null)
			return;
		
		this.encoding = encoding;
	}
	
	protected void withTestMethodParameterValues(Object... args) {
		this.testMethodArgs = args;
	}
	
	protected void initializeTest() {
		checkRequiredFields();
		
		initializeFile();
		initializeFileProcessor();
	}
	
	private void checkRequiredFields() {
		checkDirectory();
		checkFilename();
	}

	private void checkDirectory() {
		if ((directory == null) || Files.notExists(directory))
			throw new IllegalStateException("Directory not found");
	}

	private void checkFilename() {
		if ((filename == null) || filename.isBlank())
			throw new IllegalStateException("Invalid filename");
	}

	private void initializeFile() {
		file = directory.resolve(filename + ".java.txt");
	}
	
	private void initializeFileProcessor() {
		fileProcessor = createFileProcessor();
	}
	
	protected abstract FileProcessor createFileProcessor();
	
	protected void processFile() throws IOException {
		fileProcessor.processFile();
	}
	
	
	//-------------------------------------------------------------------------
	//		Getters
	//-------------------------------------------------------------------------
	protected Path getDirectory() {
		return directory;
	}
	
	protected String getOutputFilename() {
		return filename + OUTPUT_SUFFIX;
	}
	
	protected Path getFile() {
		return file;
	}
	
	protected FileEncoding getEncoding() {
		return encoding;
	}
	
	protected String getTestMethodSignature() {
		return testMethodSignature;
	}
	
	protected Object[] getTestMethodArgs() {
		 return testMethodArgs;
	}
}
