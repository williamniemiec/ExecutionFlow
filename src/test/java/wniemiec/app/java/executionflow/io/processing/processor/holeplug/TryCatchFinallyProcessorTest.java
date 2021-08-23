package wniemiec.app.java.executionflow.io.processing.processor.holeplug;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import wniemiec.app.java.executionflow.io.processing.processor.SourceCodeProcessor;
import wniemiec.app.java.executionflow.io.processing.processor.SourceCodeProcessorTest;
import wniemiec.app.java.executionflow.io.processing.processor.holeplug.TryCatchFinallyProcessor;

class TryCatchFinallyProcessorTest extends SourceCodeProcessorTest {

	//-----------------------------------------------------------------------
	//		Constructor
	//-----------------------------------------------------------------------
	TryCatchFinallyProcessorTest() {
		super(Path.of("holeplug"));
	}
	
	
	//-----------------------------------------------------------------------
	//		Tests
	//-----------------------------------------------------------------------
	@ParameterizedTest
	@ValueSource(strings = {
			"try-catch-finally"
	})
	void testTryCatchFinallyProcessor(String filename) throws Exception {
		testProcessorOnFile(filename);
	}
	
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	@Override
	protected SourceCodeProcessor getProcessorFor(List<String> sourceCode) {
		return new TryCatchFinallyProcessor(sourceCode);
	}
}
