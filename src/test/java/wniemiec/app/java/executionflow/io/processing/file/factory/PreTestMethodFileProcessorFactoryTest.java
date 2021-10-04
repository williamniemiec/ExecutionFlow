package wniemiec.app.java.executionflow.io.processing.file.factory;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import wniemiec.app.java.executionflow.io.FileEncoding;
import wniemiec.app.java.executionflow.io.processing.file.FileProcessor;
import wniemiec.app.java.executionflow.io.processing.file.PreTestMethodFileProcessor;
import wniemiec.app.java.executionflow.io.processing.file.factory.FileProcessorFactory;
import wniemiec.app.java.executionflow.io.processing.file.factory.PreTestMethodFileProcessorFactory;

class PreTestMethodFileProcessorFactoryTest {

	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	@Test
	void testCreateInstance() {
		Path targetFile = Path.of(".");
		Path tmpFile = Path.of(".");
		String outputFilename = "output-filename";
		
		FileProcessorFactory factory = new PreTestMethodFileProcessorFactory("foo.SomeClass.bar(int)", true);
		FileProcessor processor = factory.createInstance(
				targetFile, 
				tmpFile, 
				outputFilename, 
				FileEncoding.UTF_8
		);
		
		Assertions.assertTrue(processor instanceof PreTestMethodFileProcessor);
	}
	
	@Test
	void testCreateInstanceWithMethodArgs() {
		Path targetFile = Path.of(".");
		Path tmpFile = Path.of(".");
		String outputFilename = "output-filename";
		
		FileProcessorFactory factory = new PreTestMethodFileProcessorFactory(
				"foo.SomeClass.bar(int)",
				List.of("1"),
				true
		);
		FileProcessor processor = factory.createInstance(
				targetFile, 
				tmpFile, 
				outputFilename, 
				FileEncoding.UTF_8
		);
		
		Assertions.assertTrue(processor instanceof PreTestMethodFileProcessor);
	}
}