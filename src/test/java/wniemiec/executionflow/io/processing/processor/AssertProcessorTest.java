package wniemiec.executionflow.io.processing.processor;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class AssertProcessorTest extends SourceCodeProcessorTest {

	@ParameterizedTest
	@ValueSource(strings = {
			"inline-assert", 
			"multiline-assert", 
			"last-curly-bracket-same-line-assert", 
			"assert-in-try"
	})
	void testAssertProcessor(String filename) throws IOException {
		List<String> ansTxt = readAnswerFile(filename);
		List<String> testTxt = readTestFile(filename);
		List<String> procTxt = processSourceCode(testTxt);
		
		assertHasEqualNumberOfLines(ansTxt, procTxt);
		assertProcessedTextIsAccordingToExpected(ansTxt, procTxt);
	}
	
	@Override
	protected SourceCodeProcessor getProcessorFor(List<String> sourceCode) {
		return new AssertProcessor(sourceCode);
	}
}
