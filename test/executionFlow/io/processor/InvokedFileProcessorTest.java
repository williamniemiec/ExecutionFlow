package executionFlow.io.processor;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.Test;

import executionFlow.io.FileEncoding;
import executionFlow.io.processor.fileprocessor.FileProcessor;
import executionFlow.io.processor.fileprocessor.InvokedFileProcessor;
import executionFlow.runtime.SkipCollection;

@SkipCollection
public class InvokedFileProcessorTest extends FileProcessorTest {
	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	@Test
	public void testAuxClass() throws IOException {
		withDirectory(Path.of("test", "executionFlow", "io", "processor", 
							  "files", "InvokedFileProcessorTest"));
		withFilename("AuxClass");
		initializeTest();
		
		processFile();
	}

	@Test
	public void forEachToFor() throws IOException {
		withDirectory(Path.of("test", "executionFlow", "io", "processor", 
				  			  "files", "InvokedFileProcessorTest"));
		withFilename("forEachToFor");
		initializeTest();
		
		processFile();
	}

	@Test
	public void testEmptyClass() throws IOException {
		withDirectory(Path.of("test", "executionFlow", "io", "processor", 
				  			  "files", "InvokedFileProcessorTest"));
		withFilename("test_empty");
		initializeTest();
		
		processFile();
	}

	@Test
	public void testParseElse() throws IOException {
		withDirectory(Path.of("test", "executionFlow", "io", "processor", 
				  			  "files", "InvokedFileProcessorTest"));
		withFilename("test_else");
		initializeTest();
		
		processFile();
	}

	@Test
	public void testParseTry() throws IOException {
		withDirectory(Path.of("test", "executionFlow", "io", "processor", 
				  			  "files", "InvokedFileProcessorTest"));
		withFilename("test_try");
		initializeTest();
		
		processFile();
	}

	@Test
	public void testParseCatch() throws IOException {
		withDirectory(Path.of("test", "executionFlow", "io", "processor", 
				  			  "files", "InvokedFileProcessorTest"));
		withFilename("test_catch");
		initializeTest();
		
		processFile();
	}

	@Test
	public void testParseSwitch() throws IOException {
		withDirectory(Path.of("test", "executionFlow", "io", "processor", 
				  			  "files", "InvokedFileProcessorTest"));
		withFilename("test_switch");
		usingEncoding(FileEncoding.ISO_8859_1);
		initializeTest();
		
		processFile();
	}

	@Test
	public void testDoWhile() throws IOException {
		withDirectory(Path.of("test", "executionFlow", "io", "processor", 
				  			  "files", "InvokedFileProcessorTest"));
		withFilename("test_doWhile");
		initializeTest();
		
		processFile();
	}

	@Test
	public void testParseElse2() throws IOException {
		withDirectory(Path.of("test", "executionFlow", "io", "processor", 
				  			  "files", "InvokedFileProcessorTest", "complex"));
		withFilename("test_else");
		initializeTest();
		
		processFile();
	}

	@Test
	public void testParseTry2() throws IOException {
		withDirectory(Path.of("test", "executionFlow", "io", "processor", 
				  			 "files", "InvokedFileProcessorTest", "complex"));
		withFilename("test_try");
		initializeTest();
		
		processFile();
	}

	@Test
	public void testParseCatch2() throws IOException {
		withDirectory(Path.of("test", "executionFlow", "io", "processor", 
				  	  		  "files", "InvokedFileProcessorTest", "complex"));
		withFilename("test_catch");
		initializeTest();
		
		processFile();
	}

	@Test
	public void testParseSwitch2() throws IOException {
		withDirectory(Path.of("test", "executionFlow", "io", "processor", 
				  	  		  "files", "InvokedFileProcessorTest", "complex"));
		withFilename("test_switch");
		usingEncoding(FileEncoding.ISO_8859_1);
		initializeTest();
		
		processFile();
	}

	@Test
	public void testDoWhile2() throws IOException {
		withDirectory(Path.of("test", "executionFlow", "io", "processor", 
							  "files", "InvokedFileProcessorTest", "complex"));
		withFilename("test_doWhile");
		initializeTest();
		
		processFile();
	}

	@Test
	public void testElseNoCurlyBrackets() throws IOException {
		withDirectory(Path.of("test", "executionFlow", "io", "processor", 
				  			  "files", "InvokedFileProcessorTest"));
		withFilename("test_else_noCurlyBraces");
		initializeTest();
		
		processFile();
	}

	@Test
	public void testForeighCode1() throws IOException {
		withDirectory(Path.of("test", "executionFlow", "io", "processor", 
				  			  "files", "InvokedFileProcessorTest", "foreign"));
		withFilename("HelpFormatter");
		initializeTest();
		
		processFile();
	}

	@Test
	public void testForeighCode2() throws IOException {
		withDirectory(Path.of("test", "executionFlow", "io", "processor", 
				  			  "files", "InvokedFileProcessorTest", "foreign"));
		withFilename("MathArrays");
		initializeTest();
		
		processFile();
	}

	@Test
	public void testConstructor() throws IOException {
		withDirectory(Path.of("test", "executionFlow", "io", "processor", 
				  			  "files", "InvokedFileProcessorTest"));
		withFilename("test_constructor");
		initializeTest();
		
		processFile();
	}

	@Test
	public void testClauseAndBodySameLine() throws IOException {
		withDirectory(Path.of("test", "executionFlow", "io", "processor", 
				  			  "files", "InvokedFileProcessorTest"));
		withFilename("test_clauseAndBodySameLine");
		initializeTest();
		
		processFile();
	}

	@Test
	public void testControlFlow() throws IOException {
		withDirectory(Path.of("test", "executionFlow", "io", "processor", 
				  			  "files", "InvokedFileProcessorTest"));
		withFilename("test_controlFlow");
		usingEncoding(FileEncoding.ISO_8859_1);
		initializeTest();
		
		processFile();
	}

	@Test
	public void testMethodInvokedSameLine() throws IOException {
		withDirectory(Path.of("test", "executionFlow", "io", "processor", 
				  			  "files"));
		withFilename("test_method_tested_invoked_same_file");
		usingEncoding(FileEncoding.ISO_8859_1);
		initializeTest();
		
		processFile();
	}

	@Test
	public void testAnonymousClass() throws IOException {
		withDirectory(Path.of("test", "executionFlow", "io", "processor", 
							  "files"));
		withFilename("test_anonymous_class");
		usingEncoding(FileEncoding.ISO_8859_1);
		initializeTest();
		
		processFile();
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	protected FileProcessor createFileProcessor() {
		return new InvokedFileProcessor.Builder()
				.file(getFile())
				.outputDir(getDirectory())
				.outputFilename(getOutputFilename())
				.encoding(getEncoding())
				.fileExtension("txt")
				.build();
	}
}
