package wniemiec.executionflow.io.processing.file.factory;

import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import wniemiec.executionflow.io.FileEncoding;
import wniemiec.executionflow.io.processing.file.FileProcessor;
import wniemiec.executionflow.io.processing.file.PreTestMethodFileProcessor;

class PreTestMethodFileProcessorFactoryTest {

	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	@Test
	void testCreateInstance() {
		Path targetFile = Path.of(".");
		Path tmpFile = Path.of(".");
		String outputFilename = "output-filename";
		
		FileProcessorFactory factory = new PreTestMethodFileProcessorFactory("foo.SomeClass.bar(int)");
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
				new Object[] {1}
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
