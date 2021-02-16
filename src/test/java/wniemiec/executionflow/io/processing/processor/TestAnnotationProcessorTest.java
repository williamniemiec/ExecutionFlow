package wniemiec.executionflow.io.processing.processor;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TestAnnotationProcessorTest extends SourceCodeProcessorTest {

	@ParameterizedTest
	@ValueSource(strings = {
			"collect-calls"
	})
	void testTestAnnotationProcessor(String filename) throws IOException {
		testProcessorOnFile(filename);
	}
	
	@Override
	protected SourceCodeProcessor getProcessorFor(List<String> sourceCode) {
		return new TestAnnotationProcessor(sourceCode);
	}
}
