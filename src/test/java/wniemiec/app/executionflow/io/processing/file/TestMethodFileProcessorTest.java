package wniemiec.app.executionflow.io.processing.file;

import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import wniemiec.app.executionflow.io.FileEncoding;
import wniemiec.app.executionflow.io.processing.file.FileProcessor;
import wniemiec.app.executionflow.io.processing.file.TestMethodFileProcessor;

class TestMethodFileProcessorTest extends FileProcessorTest {

	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private String filename;
	
	
	//-------------------------------------------------------------------------
	//		Test hooks
	//-------------------------------------------------------------------------
	@BeforeEach
	void clean() {
		filename = "";
		TestMethodFileProcessor.clearMapping();
	}
	
	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	@Test
	void testFullBuilder() {
		new TestMethodFileProcessor.Builder()
				.targetFile(getTestFile("testmethod-junit4"))
				.outputDir(getTempFolder())
				.outputFilename("testmethod-junit4-output")
				.outputFileExtension("txt")
				.encoding(FileEncoding.UTF_8)
				.build();
	}
	
	@Test
	void testMinimumBuilder() {
		new TestMethodFileProcessor.Builder()
				.targetFile(getTestFile("testmethod-junit4"))
				.outputDir(getTempFolder())
				.outputFilename("testmethod-junit4-output")
				.build();
	}
	
	@Test
	void testEmptyBuilder() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new TestMethodFileProcessor.Builder()
				.build();
		});
	}
	
	@Test
	void testBuilderWithNullTargetFile() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new TestMethodFileProcessor.Builder()
				.targetFile(null)
				.outputDir(getTempFolder())
				.outputFilename("testmethod-junit4-output")
				.build();
		});
	}
	
	
	@Test
	void testBuilderWithNullOutputDirectory() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new TestMethodFileProcessor.Builder()
				.targetFile(getTestFile("testmethod-junit4"))
				.outputDir(null)
				.outputFilename("testmethod-junit4-output")
				.build();
		});
	}
	
	@Test
	void testBuilderWithNullOutputFilename() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new TestMethodFileProcessor.Builder()
				.targetFile(getTestFile("testmethod-junit4"))
				.outputDir(getTempFolder())
				.outputFilename(null)
				.build();
		});
	}
	
	@Test
	void testProcessingJUnit4() throws IOException {
		testProcessorOnFile("testmethod-junit4");
	}
	
	@Test
	void testProcessingJUnit5() throws IOException {
		testProcessorOnFile("testmethod-junit5");
	}
	
	@Test
	void testMappingOfJUnit4Processing() throws IOException {
		withFilename("testmethod-junit4");
		
		assertMappingIs(Map.ofEntries(
				Map.entry(15, 14),
				Map.entry(16, 14),
				Map.entry(17, 14),
				Map.entry(18, 14),
				Map.entry(19, 14),
				Map.entry(20, 14),
				Map.entry(21, 14)
		));
	}

	@Test
	void testMappingOfJUnit5Processing() throws IOException {
		withFilename("testmethod-junit5");

		assertMappingIs(Map.ofEntries(
				Map.entry(46, 45),
				Map.entry(47, 45),
				Map.entry(48, 45),
				Map.entry(49, 45),
				Map.entry(50, 45),
				Map.entry(51, 45)
		));
	}
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	private void withFilename(String filename) {
		this.filename = filename;
	}
	
	private void assertMappingIs(Map<Integer, Integer> expectedMapping) throws IOException {
		TestMethodFileProcessor processor = new TestMethodFileProcessor.Builder()
				.targetFile(getTestFile(filename))
				.outputDir(getTempFolder())
				.outputFilename(filename + "-output")
				.build();
		
		processor.processFile();
		
		Assertions.assertEquals(expectedMapping, TestMethodFileProcessor.getMapping());
	}
	
	@Override
	protected FileProcessor getFileProcessor(String filename) {
		return new TestMethodFileProcessor.Builder()
				.targetFile(getTestFile(filename))
				.outputDir(getTempFolder())
				.outputFilename(filename + "-output")
				.build();
	}
}
