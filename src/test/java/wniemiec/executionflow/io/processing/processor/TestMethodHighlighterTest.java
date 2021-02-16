package wniemiec.executionflow.io.processing.processor;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TestMethodHighlighterTest extends SourceCodeProcessorTest {

	private String testMethodSignature;
	
	@BeforeEach
	void beforeEachTest() {
		testMethodSignature = null;
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
			"highlight-test-methods1"
	})
	void testTestMethodHighlighterAll(String filename) throws IOException {
		testMethodSignature = "";
		testProcessorOnFile(filename);
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
			"highlight-test-methods2"
	})
	void testTestMethodHighlighterFoo(String filename) throws IOException {
		testMethodSignature = "foo.bar.SomeClass.foo()";
		testProcessorOnFile(filename);
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
			"highlight-test-methods3"
	})
	void testTestMethodHighlighterFoo2(String filename) throws IOException {
		testMethodSignature = "foo.bar.SomeClass.foo2()";
		testProcessorOnFile(filename);
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
			"highlight-test-methods4"
	})
	void testTestMethodHighlighterFoo3(String filename) throws IOException {
		testMethodSignature = "foo.bar.SomeClass.foo3()";
		testProcessorOnFile(filename);
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
			"highlight-test-methods5"
	})
	void testTestMethodHighlighterFoo4(String filename) throws IOException {
		testMethodSignature = "foo.bar.SomeClass.foo4(int)";
		testProcessorOnFile(filename);
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
			"highlight-test-methods6"
	})
	void testTestMethodHighlighterFoo5(String filename) throws IOException {
		testMethodSignature = "foo.bar.SomeClass.foo5()";
		testProcessorOnFile(filename);
	}
	
	@Override
	protected SourceCodeProcessor getProcessorFor(List<String> sourceCode) {
		return new TestMethodHighlighter(sourceCode, testMethodSignature);
	}

}
