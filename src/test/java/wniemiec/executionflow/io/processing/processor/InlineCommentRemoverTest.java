package wniemiec.executionflow.io.processing.processor;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class InlineCommentRemoverTest extends SourceCodeProcessorTest {

	@ParameterizedTest
	@ValueSource(strings = {
			"inline-comment"
	})
	void testInlineCommentRemover(String filename) throws IOException {
		testProcessorOnFile(filename);
	}
	
	@Override
	protected SourceCodeProcessor getProcessorFor(List<String> sourceCode) {
		return new InlineCommentRemover(sourceCode);
	}
}
