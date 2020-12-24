package executionFlow.io.processor;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.Test;

import executionFlow.io.processor.fileprocessor.FileProcessor;
import executionFlow.io.processor.fileprocessor.PreTestMethodFileProcessor;
import executionFlow.runtime.SkipCollection;

/**
 * Tests for class {@link PreTestMethodFileProcessor}.
 */
@SkipCollection
public class PreTestMethodFileProcessorTest extends FileProcessorTest {
	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	@Test
	public void testClassTest_firstMethod() throws IOException {
		withDirectory(Path.of("test", "executionFlow", "io", "processor", 
			  			  	  "files", "PreTestMethodFileProcessorTest"));
		withFilename("TestClass");
		withTestMethodSignature("executionFlow.core.file.parser.tests" + 
								".TestClass.testLastCurlyBracketInSameLine()");
		initializeTest();
		
		processFile();
	}
	
	@Test
	public void testMethodWithInnerClass() throws IOException {
		withDirectory(Path.of("test", "executionFlow", "io", "processor", 
				  			  "files", "PreTestMethodFileProcessorTest"));
		withFilename("TestMethodWithInnerClass");
		withTestMethodSignature("org.jfree.chart.AreaChartTest" + 
								".testDrawWithNullInfo()");
		initializeTest();
		
		processFile();
	}
	
	@Test
	public void parameterizedTestMethodTest_firstMethod() throws IOException {
		withDirectory(Path.of("test", "executionFlow", "io", "processor", 
			  	  			  "files", "PreTestMethodFileProcessorTest"));
		withFilename("ParameterizedTestMethod");
		withTestMethodSignature("examples.junit5.ParameterizedTestAnnotation" + 
								".test1(int)");
		withTestMethodParameterValues(-1);
		initializeTest();
		
		processFile();
	}
	
	@Test
	public void testMethodInvokedSameLine() throws IOException {
		withDirectory(Path.of("test", "executionFlow", "io", "processor", 
							  "files"));
		withFilename("test_method_tested_invoked_same_file");
		withTestMethodSignature("examples.againstRequirements" + 
								".methodInSameFileOfTestMethod()");
		initializeTest();
		
		processFile();
	}
	
	@Test
	public void testAnonymousClass() throws IOException {
		withDirectory(Path.of("test", "executionFlow", "io", "processor", 
							  "files"));
		withFilename("test_anonymous_class");
		withTestMethodSignature("examples.override.OverrideTest" + 
								".testOverloadedMethod3()");
		initializeTest();
		
		processFile();
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	protected FileProcessor createFileProcessor() {
		return new PreTestMethodFileProcessor.Builder()
				.file(getFile())
				.outputDir(getDirectory())
				.outputFilename(getOutputFilename())
				.encoding(getEncoding())
				.fileExtension("txt")
				.testMethodSignature(getTestMethodSignature())
				.testMethodArgs(getTestMethodArgs())
				.build();
	}
}
