package wniemiec.app.java.executionflow.io.processing.processor;

import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import wniemiec.app.java.executionflow.io.processing.processor.SourceCodeProcessor;
import wniemiec.app.java.executionflow.io.processing.processor.TestAnnotationProcessor;

class TestAnnotationProcessorTest extends SourceCodeProcessorTest {

	//-----------------------------------------------------------------------
	//		Tests
	//-----------------------------------------------------------------------
	@ParameterizedTest
	@ValueSource(strings = {
			"collect-calls"
	})
	void testTestAnnotationProcessor(String filename) throws Exception {
		testProcessorOnFile(filename);
	}
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	@Override
	protected SourceCodeProcessor getProcessorFor(List<String> sourceCode) {
		return new TestAnnotationProcessor(sourceCode);
	}
}
