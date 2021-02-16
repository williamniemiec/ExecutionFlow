package wniemiec.executionflow.io.processing.processor;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PrintCallDeactivatorTest extends SourceCodeProcessorTest {

	@ParameterizedTest
	@ValueSource(strings = {
			"print-call"
	})
	void testPrintCallDeactivator(String filename) throws IOException {
		testProcessorOnFile(filename);
	}
	
	@Override
	protected SourceCodeProcessor getProcessorFor(List<String> sourceCode) {
		return new PrintCallDeactivator(sourceCode);
	}
}
