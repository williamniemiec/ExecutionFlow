package wniemiec.app.executionflow.io.processing.processor.trgeneration;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import wniemiec.app.executionflow.io.processing.Processing;

class CodeCleanerAdapterTest extends Processing {

	//-----------------------------------------------------------------------
	//		Constructor
	//-----------------------------------------------------------------------
	CodeCleanerAdapterTest() {
		super(Path.of("processor", "codecleaner"));
	}
	
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	@Test
	void testCodeCleaner() throws Exception {
		testProcessorOnFile("codecleaner");
	}

	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	@Override
	protected List<String> processSourceCodeFrom(String filename) throws Exception {
		CodeCleanerAdapter processor = new CodeCleanerAdapter(readTestFile(filename));
		
		return processor.processLines();
	}
}
