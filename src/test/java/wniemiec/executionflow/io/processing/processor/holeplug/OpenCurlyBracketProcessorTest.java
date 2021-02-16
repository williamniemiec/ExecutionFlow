package wniemiec.executionflow.io.processing.processor.holeplug;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import wniemiec.executionflow.io.processing.processor.SourceCodeProcessor;
import wniemiec.executionflow.io.processing.processor.SourceCodeProcessorTest;

class OpenCurlyBracketProcessorTest extends SourceCodeProcessorTest {

	//-----------------------------------------------------------------------
	//		Constructor
	//-----------------------------------------------------------------------
	OpenCurlyBracketProcessorTest() {
		super(Path.of("holeplug"));
	}
	
	
	//-----------------------------------------------------------------------
	//		Tests
	//-----------------------------------------------------------------------
	@ParameterizedTest
	@ValueSource(strings = {
			"open-curlybrackets"
	})
	void testOpenCurlyBracketProcessor(String filename) throws IOException {
		testProcessorOnFile(filename);
	}
	
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	@Override
	protected SourceCodeProcessor getProcessorFor(List<String> sourceCode) {
		return new OpenCurlyBracketProcessor(sourceCode);
	}
}
