package executionFlow.io.processor;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.Test;

import executionFlow.runtime.SkipCollection;

/**
 * Tests for class {@link TestMethodFileProcessor}.
 */
@SkipCollection
public class TestMethodFileProcessorTest extends FileProcessorTest {
	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	@Test
	public void testClassTest() throws IOException {
		withDirectory(Path.of("test", "executionFlow", "io", "processor", 
	  			  			  "files", "TestMethodFileProcessorTest"));
		withFilename("TestClass");
		initializeTest();
		
		processFile();
	}
	
	@Test
	public void testMethodInvokedSameLine() throws IOException {
		withDirectory(Path.of("test", "executionFlow", "io", "processor", 
			  			  	  "files"));
		withFilename("test_method_tested_invoked_same_file");
		initializeTest();
		
		processFile();
	}
	
	@Test
	public void testAnonymousClass() throws IOException {
		withDirectory(Path.of("test", "executionFlow", "io", "processor", 
					  	  	  "files"));
		withFilename("test_anonymous_class");
		initializeTest();
		
		processFile();
	}
	
	@Test
	public void testMultiargsTest() throws IOException {
		withDirectory(Path.of("test", "executionFlow", "io", "processor", 
				  	  	  	  "files", "TestMethodFileProcessorTest"));
		withFilename("Multiargs");
		initializeTest();
		
		processFile();
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	protected FileProcessor createFileProcessor() {
		return new TestMethodFileProcessor.Builder()
				.file(getFile())
				.outputDir(getDirectory())
				.outputFilename(getOutputFilename())
				.encoding(getEncoding())
				.fileExtension("txt")
				.build();
	}
}
