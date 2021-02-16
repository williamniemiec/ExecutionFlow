package wniemiec.executionflow.io.processing.processor;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ClassDeclarationProcessorTest extends SourceCodeProcessorTest {

	@ParameterizedTest
	@ValueSource(strings = {
			"class-declaration"
	})
	void testClassDeclarationProcessor(String filename) throws IOException {
		testProcessorOnFile(filename);
	}
	
	@Override
	protected SourceCodeProcessor getProcessorFor(List<String> sourceCode) {
		return new ClassDeclarationProcessor(sourceCode);
	}
}
