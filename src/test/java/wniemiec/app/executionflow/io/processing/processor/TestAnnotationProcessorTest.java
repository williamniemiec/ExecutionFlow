package wniemiec.app.executionflow.io.processing.processor;

import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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
