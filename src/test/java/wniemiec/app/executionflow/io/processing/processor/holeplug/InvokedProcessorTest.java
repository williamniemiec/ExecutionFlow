package wniemiec.app.executionflow.io.processing.processor.holeplug;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import wniemiec.app.executionflow.io.processing.processor.SourceCodeProcessor;
import wniemiec.app.executionflow.io.processing.processor.SourceCodeProcessorTest;

class InvokedProcessorTest extends SourceCodeProcessorTest {

	//-----------------------------------------------------------------------
	//		Constructor
	//-----------------------------------------------------------------------
	InvokedProcessorTest() {
		super(Path.of("holeplug"));
	}
	
	
	//-----------------------------------------------------------------------
	//		Tests
	//-----------------------------------------------------------------------
	@ParameterizedTest
	@ValueSource(strings = {
			"invoked-processor"
	})
	void testInvokedProcessor(String filename) throws Exception {
		testProcessorOnFile(filename);
	}
	
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	@Override
	protected SourceCodeProcessor getProcessorFor(List<String> sourceCode) {
		return new InvokedProcessor(sourceCode);
	}
}
