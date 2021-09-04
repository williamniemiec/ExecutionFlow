package wniemiec.app.java.executionflow.io.processing.file;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import wniemiec.app.java.executionflow.io.FileEncoding;
import wniemiec.app.java.executionflow.io.processing.file.FileProcessor;
import wniemiec.app.java.executionflow.io.processing.file.TestMethodFileProcessor;

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
	void testProcessingJUnit4() throws Exception {
		testProcessorOnFile("testmethod-junit4");
	}
	
	@Test
	void testProcessingJUnit5() throws Exception {
		testProcessorOnFile("testmethod-junit5");
	}
	
	@Test
	void testMappingOfJUnit4Processing() throws Exception {
		withFilename("testmethod-junit4");
		
		assertMappingIs(Map.ofEntries(
				Map.entry(14, List.of(15, 16, 17, 18, 19, 20, 21))
		));
	}

	@Test
	void testMappingOfJUnit5Processing() throws Exception {
		withFilename("testmethod-junit5");

		assertMappingIs(Map.ofEntries(
				Map.entry(45, List.of(46, 47, 48, 49, 50, 51))
		));
	}
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	private void withFilename(String filename) {
		this.filename = filename;
	}
	
	private void assertMappingIs(Map<Integer, List<Integer>> expectedMapping) throws Exception {
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
