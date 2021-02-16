package wniemiec.executionflow.io.processing.processor.holeplug;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import wniemiec.executionflow.io.processing.processor.SourceCodeProcessor;
import wniemiec.executionflow.io.processing.processor.SourceCodeProcessorTest;

class DoWhileProcessorTest extends SourceCodeProcessorTest {

	DoWhileProcessorTest() {
		super(Path.of("holeplug"));
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
			"dowhile"
	})
	void testDoWhileProcessor(String filename) throws IOException {
		testProcessorOnFile(filename);
	}
	
	@Override
	protected SourceCodeProcessor getProcessorFor(List<String> sourceCode) {
		return new DoWhileProcessor(sourceCode);
	}
}
