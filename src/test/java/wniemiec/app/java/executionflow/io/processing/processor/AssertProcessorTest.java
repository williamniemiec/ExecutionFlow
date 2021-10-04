package wniemiec.app.java.executionflow.io.processing.processor;

import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import wniemiec.app.java.executionflow.io.processing.processor.AssertProcessor;
import wniemiec.app.java.executionflow.io.processing.processor.SourceCodeProcessor;

class AssertProcessorTest extends SourceCodeProcessorTest {

	//-----------------------------------------------------------------------
	//		Tests
	//-----------------------------------------------------------------------
	@ParameterizedTest
	@ValueSource(strings = {
			"inline-assert", 
			"multiline-assert", 
			"last-curly-bracket-same-line-assert", 
			"assert-in-try"
	})
	void testAssertProcessor(String filename) throws Exception {
		testProcessorOnFile(filename);
	}
	
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	@Override
	protected SourceCodeProcessor getProcessorFor(List<String> sourceCode) {
		return new AssertProcessor(sourceCode);
	}
}