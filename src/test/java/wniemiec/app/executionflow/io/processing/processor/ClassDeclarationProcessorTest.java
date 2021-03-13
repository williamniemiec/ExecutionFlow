package wniemiec.app.executionflow.io.processing.processor;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import wniemiec.app.executionflow.io.processing.processor.ClassDeclarationProcessor;
import wniemiec.app.executionflow.io.processing.processor.SourceCodeProcessor;

class ClassDeclarationProcessorTest extends SourceCodeProcessorTest {

	//-----------------------------------------------------------------------
	//		Tests
	//-----------------------------------------------------------------------
	@ParameterizedTest
	@ValueSource(strings = {
			"class-declaration"
	})
	void testClassDeclarationProcessor(String filename) throws IOException {
		testProcessorOnFile(filename);
	}
	
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	@Override
	protected SourceCodeProcessor getProcessorFor(List<String> sourceCode) {
		return new ClassDeclarationProcessor(sourceCode);
	}
}