package wniemiec.app.java.executionflow.io.processing.processor;

import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import wniemiec.app.java.executionflow.io.processing.processor.MultilineToInlineCallsConverter;
import wniemiec.app.java.executionflow.io.processing.processor.SourceCodeProcessor;

class MultilineToInlineCallsConverterTest extends SourceCodeProcessorTest {

	//-----------------------------------------------------------------------
	//		Tests
	//-----------------------------------------------------------------------
	@ParameterizedTest
	@ValueSource(strings = {
			"multiline-to-inline"
	})
	void testMultilineToInlineCallsConverter(String filename) throws Exception {
		testProcessorOnFile(filename);
	}
	
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	@Override
	protected SourceCodeProcessor getProcessorFor(List<String> sourceCode) {
		return new MultilineToInlineCallsConverter(sourceCode);
	}
}
