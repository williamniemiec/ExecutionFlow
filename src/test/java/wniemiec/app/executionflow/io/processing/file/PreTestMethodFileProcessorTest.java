package wniemiec.app.executionflow.io.processing.file;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import wniemiec.app.executionflow.io.FileEncoding;

class PreTestMethodFileProcessorTest extends FileProcessorTest {
	
	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
	private String testMethodSignature;
	private String filename;
	
	
	//-----------------------------------------------------------------------
	//		Test hooks
	//-----------------------------------------------------------------------	
	@BeforeEach
	void clear() {
		testMethodSignature = "";
		filename = "";
	}

	
	//-----------------------------------------------------------------------
	//		Tests
	//-----------------------------------------------------------------------
	@Test
	void testFullBuilderWithoutTestMethodArgs() {
		new PreTestMethodFileProcessor.Builder()
				.targetFile(getTestFile("pretestmethod-junit4"))
				.outputDir(getTempFolder())
				.outputFilename("pretestmethod-junit4-output")
				.outputFileExtension("txt")
				.encoding(FileEncoding.UTF_8)
				.testMethodSignature("examples.others.SimpleTestPath.simpleTestPath()")
				.build();
	}
	
	@Test
	void testFullBuilderWithTestMethodArgs() {
		new PreTestMethodFileProcessor.Builder()
				.targetFile(getTestFile("pretestmethod-junit5"))
				.outputDir(getTempFolder())
				.outputFilename("pretestmethod-junit5-output")
				.outputFileExtension("txt")
				.encoding(FileEncoding.UTF_8)
				.testMethodSignature("examples.others.auxClasses.AuxClass.MixedJUnit5Annotations.parameterizedTestAnnotation(int)")
				.testMethodArgs(List.of("-1"))
				.build();
	}
	
	@Test
	void testMinimumBuilder() {
		new PreTestMethodFileProcessor.Builder()
				.targetFile(getTestFile("pretestmethod-junit4"))
				.outputDir(getTempFolder())
				.outputFilename("pretestmethod-junit4-output")
				.testMethodSignature("examples.others.SimpleTestPath.simpleTestPath()")
				.build();
	}
	
	@Test
	void testEmptyBuilder() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new PreTestMethodFileProcessor.Builder()
				.build();
		});
	}
	
	@Test
	void testBuilderWithNullTargetFile() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new PreTestMethodFileProcessor.Builder()
				.targetFile(null)
				.outputDir(getTempFolder())
				.outputFilename("pretestmethod-junit4-output")
				.testMethodSignature("examples.others.SimpleTestPath.simpleTestPath()")
				.build();
		});
	}
	
	
	@Test
	void testBuilderWithNullOutputDirectory() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new PreTestMethodFileProcessor.Builder()
				.targetFile(getTestFile("pretestmethod-junit4"))
				.outputDir(null)
				.outputFilename("pretestmethod-junit4-output")
				.testMethodSignature("examples.others.SimpleTestPath.simpleTestPath()")
				.build();
		});
	}
	
	@Test
	void testBuilderWithNullOutputFilename() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new PreTestMethodFileProcessor.Builder()
				.targetFile(getTestFile("pretestmethod-junit4"))
				.outputDir(getTempFolder())
				.outputFilename(null)
				.testMethodSignature("examples.others.SimpleTestPath.simpleTestPath()")
				.build();
		});
	}
	
	@Test
	void testBuilderWithNullTestMethodSignature() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new PreTestMethodFileProcessor.Builder()
				.targetFile(getTestFile("pretestmethod-junit4"))
				.outputDir(getTempFolder())
				.outputFilename("pretestmethod-junit4-output")
				.testMethodSignature(null)
				.build();
		});
	}
	
	@Test
	void testProcessingJUnit4() throws Exception {
		withTestMethodSignature("examples.others.SimpleTestPath.simpleTestPath()");
		testProcessorOnFile("pretestmethod-junit4");
	}
	
	@Test
	void testProcessingJUnit5() throws Exception {
		withTestMethodSignature("examples.junit5.MixedJUnit5Annotations.parameterizedTestAnnotation(int)");
		testProcessorOnFile("pretestmethod-junit5");
	}
	
	@Test
	void testProcessingJUnit4TotalTests() throws Exception {
		withTestMethodSignature("examples.others.SimpleTestPath.simpleTestPath()");
		withFilename("pretestmethod-junit4");
		assertTotalTestsIs(3);
	}

	@Test
	void testProcessingJUnit5TotalTests() throws Exception {
		withTestMethodSignature("examples.junit5.MixedJUnit5Annotations.parameterizedTestAnnotation(int)");
		withFilename("pretestmethod-junit5");
		assertTotalTestsIs(3);
	}

	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	private void withTestMethodSignature(String signature) {
		testMethodSignature = signature;
	}
	
	private void withFilename(String filename) {
		this.filename = filename;
	}
	
	private void assertTotalTestsIs(int totalTests) throws Exception {
		PreTestMethodFileProcessor processor = 
				(PreTestMethodFileProcessor) getFileProcessor(filename);
		processor.processFile();
		
		Assertions.assertEquals(totalTests, PreTestMethodFileProcessor.getTotalTests());
	}
		
	@Override
	protected FileProcessor getFileProcessor(String filename) {
		return new PreTestMethodFileProcessor.Builder()
				.targetFile(getTestFile(filename))
				.outputDir(getTempFolder())
				.outputFilename(filename + "-output")
				.testMethodSignature(testMethodSignature)
				.build();
	}
}
