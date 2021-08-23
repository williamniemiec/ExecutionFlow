package wniemiec.app.java.executionflow.io.processing.processor;

import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import wniemiec.app.java.executionflow.io.processing.processor.InlineCommentRemover;
import wniemiec.app.java.executionflow.io.processing.processor.SourceCodeProcessor;

class InlineCommentRemoverTest extends SourceCodeProcessorTest {

	//-----------------------------------------------------------------------
	//		Tests
	//-----------------------------------------------------------------------
	@ParameterizedTest
	@ValueSource(strings = {
			"inline-comment"
	})
	void testInlineCommentRemover(String filename) throws Exception {
		testProcessorOnFile(filename);
	}
	
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	@Override
	protected SourceCodeProcessor getProcessorFor(List<String> sourceCode) {
		return new InlineCommentRemover(sourceCode);
	}
}
