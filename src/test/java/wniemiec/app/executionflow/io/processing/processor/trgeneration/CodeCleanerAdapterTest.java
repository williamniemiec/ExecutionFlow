package wniemiec.app.executionflow.io.processing.processor.trgeneration;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import wniemiec.app.executionflow.io.processing.Processing;
import wniemiec.app.executionflow.io.processing.processor.trgeneration.CodeCleanerAdapter;

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
	void testCodeCleaner() throws IOException {
		testProcessorOnFile("codecleaner");
	}

	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	@Override
	protected List<String> processSourceCodeFrom(String filename) throws IOException {
		CodeCleanerAdapter processor = new CodeCleanerAdapter(readTestFile(filename));
		
		return processor.processLines();
	}
}
