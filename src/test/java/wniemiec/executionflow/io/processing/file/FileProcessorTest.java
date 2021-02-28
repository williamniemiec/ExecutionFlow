package wniemiec.executionflow.io.processing.file;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import wniemiec.executionflow.io.FileEncoding;
import wniemiec.executionflow.io.processing.Processing;
import wniemiec.util.io.manager.TextFileManager;

public abstract class FileProcessorTest extends Processing {

	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
	private final Path tmpFolder;
	
	
	//-----------------------------------------------------------------------
	//		Constructor
	//-----------------------------------------------------------------------
	FileProcessorTest() {
		super(Path.of("file"));
		tmpFolder = Path.of(System.getProperty("java.io.tmpdir"));
	}
	
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	@Override
	protected List<String> processSourceCodeFrom(String filename) throws IOException {
		Path outputFile = doProcessing(filename);
		
		return readLinesFrom(outputFile);
	}
	
	protected Path doProcessing(String filename) throws IOException {
		FileProcessor processor = getFileProcessor(filename);
		
		return Path.of(processor.processFile());
	}

	protected List<String> readLinesFrom(Path file) throws IOException {
		TextFileManager txtManager = new TextFileManager(
				file, 
				FileEncoding.UTF_8.getStandardCharset()
		);
		
		return txtManager.readLines();
	}

	
	//-----------------------------------------------------------------------
	//		Getters
	//-----------------------------------------------------------------------
	protected abstract FileProcessor getFileProcessor(String filename);
	
	protected Path getTestFile(String filename) {
		return workingDirectory.resolve(filename + "-test.txt");
	}
	
	protected Path getTempFolder() {
		return tmpFolder;
	}
}
