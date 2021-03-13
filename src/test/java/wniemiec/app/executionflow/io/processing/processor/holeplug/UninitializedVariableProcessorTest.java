package wniemiec.app.executionflow.io.processing.processor.holeplug;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import wniemiec.app.executionflow.io.processing.processor.SourceCodeProcessor;
import wniemiec.app.executionflow.io.processing.processor.SourceCodeProcessorTest;
import wniemiec.app.executionflow.io.processing.processor.holeplug.UninitializedVariableProcessor;

class UninitializedVariableProcessorTest extends SourceCodeProcessorTest {

	//-----------------------------------------------------------------------
	//		Constructor
	//-----------------------------------------------------------------------
	UninitializedVariableProcessorTest() {
		super(Path.of("holeplug"));
	}
	
	
	//-----------------------------------------------------------------------
	//		Tests
	//-----------------------------------------------------------------------
	@ParameterizedTest
	@ValueSource(strings = {
			"uninitialized-variables"
	})
	void testUninitializedVariableProcessor(String filename) throws IOException {
		testProcessorOnFile(filename);
	}
	
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	@Override
	protected SourceCodeProcessor getProcessorFor(List<String> sourceCode) {
		return new UninitializedVariableProcessor(sourceCode);
	}
}
