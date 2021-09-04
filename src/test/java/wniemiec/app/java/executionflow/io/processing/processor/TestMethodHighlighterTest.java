package wniemiec.app.java.executionflow.io.processing.processor;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import wniemiec.app.java.executionflow.io.processing.processor.SourceCodeProcessor;
import wniemiec.app.java.executionflow.io.processing.processor.TestMethodHighlighter;

class TestMethodHighlighterTest extends SourceCodeProcessorTest {

	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
	private String testMethodSignature;
	
	
	//-----------------------------------------------------------------------
	//		Test hooks
	//-----------------------------------------------------------------------
	@BeforeEach
	void beforeEachTest() {
		testMethodSignature = null;
	}
	
	
	//-----------------------------------------------------------------------
	//		Tests
	//-----------------------------------------------------------------------
	@ParameterizedTest
	@ValueSource(strings = {
			"highlight-test-methods1"
	})
	void testTestMethodHighlighterAll(String filename) throws Exception {
		testMethodSignature = "";
		testProcessorOnFile(filename);
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
			"highlight-test-methods2"
	})
	void testTestMethodHighlighterFoo(String filename) throws Exception {
		testMethodSignature = "foo.bar.SomeClass.foo()";
		testProcessorOnFile(filename);
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
			"highlight-test-methods3"
	})
	void testTestMethodHighlighterFoo2(String filename) throws Exception {
		testMethodSignature = "foo.bar.SomeClass.foo2()";
		testProcessorOnFile(filename);
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
			"highlight-test-methods4"
	})
	void testTestMethodHighlighterFoo3(String filename) throws Exception {
		testMethodSignature = "foo.bar.SomeClass.foo3()";
		testProcessorOnFile(filename);
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
			"highlight-test-methods5"
	})
	void testTestMethodHighlighterFoo4(String filename) throws Exception {
		testMethodSignature = "foo.bar.SomeClass.foo4(int)";
		testProcessorOnFile(filename);
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
			"highlight-test-methods6"
	})
	void testTestMethodHighlighterFoo5(String filename) throws Exception {
		testMethodSignature = "foo.bar.SomeClass.foo5()";
		testProcessorOnFile(filename);
	}
	
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	@Override
	protected SourceCodeProcessor getProcessorFor(List<String> sourceCode) {
		return new TestMethodHighlighter(sourceCode, testMethodSignature);
	}
}
