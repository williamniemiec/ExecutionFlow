package wniemiec.app.executionflow.io.processing.processor.holeplug;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import wniemiec.app.executionflow.io.processing.Processing;

class HolePlugTest extends Processing {

	//-----------------------------------------------------------------------
	//		Constructor
	//-----------------------------------------------------------------------
	HolePlugTest() {
		super(Path.of("processor", "holeplug"));
	}
	
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	@Test
	void testHoleplug() throws Exception {
		testProcessorOnFile("holeplug");
	}

	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	@Override
	protected List<String> processSourceCodeFrom(String filename) throws IOException {
		HolePlug holeplug = new HolePlug(readTestFile(filename));
		
		return holeplug.processLines();
	}
}
