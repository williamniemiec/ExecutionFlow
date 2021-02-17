package wniemiec.executionflow.io.processing.processor.holeplug;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import wniemiec.executionflow.io.processing.processor.ProcessorTest;

class HolePlugTest extends ProcessorTest {

	//-----------------------------------------------------------------------
	//		Constructor
	//-----------------------------------------------------------------------
	HolePlugTest() {
		super(Path.of("holeplug"));
	}
	
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	@Test
	void testHoleplug() throws IOException {
		testProcessorOnFile("holeplug");
	}

	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	@Override
	protected List<String> processSourceCode(List<String> sourceCode) {
		HolePlug holeplug = new HolePlug(sourceCode);
		
		return holeplug.processLines();
	}
}
