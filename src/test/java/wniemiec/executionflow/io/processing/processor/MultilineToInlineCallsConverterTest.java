package wniemiec.executionflow.io.processing.processor;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class MultilineToInlineCallsConverterTest extends SourceCodeProcessorTest {

	@ParameterizedTest
	@ValueSource(strings = {
			"multiline-to-inline"
	})
	void testMultilineToInlineCallsConverter(String filename) throws IOException {
		testProcessorOnFile(filename);
	}
	
	@Override
	protected SourceCodeProcessor getProcessorFor(List<String> sourceCode) {
		return new MultilineToInlineCallsConverter(sourceCode);
	}
}
