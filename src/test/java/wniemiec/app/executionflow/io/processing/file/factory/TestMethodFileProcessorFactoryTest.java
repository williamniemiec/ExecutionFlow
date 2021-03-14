package wniemiec.app.executionflow.io.processing.file.factory;

import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import wniemiec.app.executionflow.io.FileEncoding;
import wniemiec.app.executionflow.io.processing.file.FileProcessor;
import wniemiec.app.executionflow.io.processing.file.TestMethodFileProcessor;
import wniemiec.app.executionflow.io.processing.file.factory.FileProcessorFactory;
import wniemiec.app.executionflow.io.processing.file.factory.TestMethodFileProcessorFactory;

class TestMethodFileProcessorFactoryTest {

	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	@Test
	void testCreateInstance() {
		Path targetFile = Path.of(".");
		Path tmpFile = Path.of(".");
		String outputFilename = "output-filename";
		
		FileProcessorFactory factory = new TestMethodFileProcessorFactory();
		FileProcessor processor = factory.createInstance(
				targetFile, 
				tmpFile, 
				outputFilename, 
				FileEncoding.UTF_8
		);
		
		Assertions.assertTrue(processor instanceof TestMethodFileProcessor);
	}
}
