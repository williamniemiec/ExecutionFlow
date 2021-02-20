package wniemiec.executionflow.io.processing.processor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import wniemiec.executionflow.io.processing.Processing;

public abstract class SourceCodeProcessorTest extends Processing {
	
	//-----------------------------------------------------------------------
	//		Constructors
	//-----------------------------------------------------------------------
	protected SourceCodeProcessorTest(Path relativePath) {
		super(Path.of("processor").resolve(relativePath));
	}
	
	protected SourceCodeProcessorTest() {
		this(Path.of("."));
	}
	
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	@Override
	protected List<String> processSourceCodeFrom(String filename) throws IOException {
		SourceCodeProcessor processor = getProcessorFor(readTestFile(filename));
		
		return processor.processLines();
	}
	
	protected abstract SourceCodeProcessor getProcessorFor(List<String> sourceCode);
}
