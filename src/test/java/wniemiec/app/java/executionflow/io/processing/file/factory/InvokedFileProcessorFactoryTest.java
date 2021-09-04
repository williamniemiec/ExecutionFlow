package wniemiec.app.java.executionflow.io.processing.file.factory;

import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import wniemiec.app.java.executionflow.io.FileEncoding;
import wniemiec.app.java.executionflow.io.processing.file.FileProcessor;
import wniemiec.app.java.executionflow.io.processing.file.InvokedFileProcessor;
import wniemiec.app.java.executionflow.io.processing.file.factory.FileProcessorFactory;
import wniemiec.app.java.executionflow.io.processing.file.factory.InvokedFileProcessorFactory;

class InvokedFileProcessorFactoryTest {

	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	@Test
	void testCreateInstance() {
		Path targetFile = Path.of(".");
		Path tmpFile = Path.of(".");
		String outputFilename = "output-filename";
		
		FileProcessorFactory factory = new InvokedFileProcessorFactory();
		FileProcessor processor = factory.createInstance(
				targetFile, 
				tmpFile, 
				outputFilename, 
				FileEncoding.UTF_8
		);
		
		Assertions.assertTrue(processor instanceof InvokedFileProcessor);
	}
}
