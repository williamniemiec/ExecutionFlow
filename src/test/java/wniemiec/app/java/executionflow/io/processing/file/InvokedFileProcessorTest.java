package wniemiec.app.java.executionflow.io.processing.file;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import wniemiec.app.java.executionflow.io.FileEncoding;
import wniemiec.app.java.executionflow.io.processing.file.FileProcessor;

class InvokedFileProcessorTest extends FileProcessorTest {
	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	@Test
	void testFullBuilder() {
		new InvokedFileProcessor.Builder()
				.targetFile(getTestFile("invoked"))
				.outputDir(getTempFolder())
				.outputFilename("invoked-output")
				.outputFileExtension("txt")
				.encoding(FileEncoding.UTF_8)
				.build();
	}
	
	@Test
	void testMinimumBuilder() {
		new InvokedFileProcessor.Builder()
				.targetFile(getTestFile("invoked"))
				.outputDir(getTempFolder())
				.outputFilename("invoked-output")
				.build();
	}
	
	@Test
	void testEmptyBuilder() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new InvokedFileProcessor.Builder()
				.build();
		});
	}
	
	@Test
	void testBuilderWithNullTargetFile() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new InvokedFileProcessor.Builder()
				.targetFile(null)
				.outputDir(getTempFolder())
				.outputFilename("invoked-output")
				.build();
		});
	}
	
	
	@Test
	void testBuilderWithNullOutputDirectory() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new InvokedFileProcessor.Builder()
				.targetFile(getTestFile("invoked"))
				.outputDir(null)
				.outputFilename("invoked-output")
				.build();
		});
	}
	
	@Test
	void testBuilderWithNullOutputFilename() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new InvokedFileProcessor.Builder()
				.targetFile(getTestFile("invoked"))
				.outputDir(getTempFolder())
				.outputFilename(null)
				.build();
		});
	}
	
	@Test
	void testProcessing() throws Exception {
		testProcessorOnFile("invoked");
	}
	
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	@Override
	protected FileProcessor getFileProcessor(String filename) {
		return new InvokedFileProcessor.Builder()
				.targetFile(getTestFile(filename))
				.outputDir(getTempFolder())
				.outputFilename(filename + "-output")
				.build();
	}
}
