package wniemiec.executionflow.io.processing.file;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import wniemiec.executionflow.io.FileEncoding;

class InvokedFileProcessorTest extends FileProcessorTest {

private String filename;
	
	@BeforeEach
	void clean() {
		filename = "";
		InvokedFileProcessor.clearMapping();
	}
	
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
	void testProcessing() throws IOException {
		testProcessorOnFile("invoked");
	}
	
	@Test
	void testMapping
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
//	private void withFilename(String filename) {
//		this.filename = filename;
//	}
//	
//	private void assertMappingIs(Map<Integer, Integer> expectedMapping) throws IOException {
//		TestMethodFileProcessor processor = new TestMethodFileProcessor.Builder()
//				.targetFile(getTestFile(filename))
//				.outputDir(getTempFolder())
//				.outputFilename(filename + "-output")
//				.build();
//		
//		processor.processFile();
//		
//		Assertions.assertEquals(expectedMapping, processor.getMapping());
//	}
	
	@Override
	protected FileProcessor getFileProcessor(String filename) {
		return new InvokedFileProcessor.Builder()
				.targetFile(getTestFile(filename))
				.outputDir(getTempFolder())
				.outputFilename(filename + "-output")
				.build();
	}
}
