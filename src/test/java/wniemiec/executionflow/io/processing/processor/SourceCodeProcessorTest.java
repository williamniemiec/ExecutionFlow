package wniemiec.executionflow.io.processing.processor;

import java.nio.file.Path;
import java.util.List;

public abstract class SourceCodeProcessorTest extends ProcessorTest {
	
	//-----------------------------------------------------------------------
	//		Constructors
	//-----------------------------------------------------------------------
	protected SourceCodeProcessorTest(Path relativePath) {
		super(relativePath);
	}
	
	protected SourceCodeProcessorTest() {
		this(Path.of("."));
	}
	
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	@Override
	protected List<String> processSourceCode(List<String> sourceCode) {
		SourceCodeProcessor processor = getProcessorFor(sourceCode);
		
		return processor.processLines();
	}
	
	protected abstract SourceCodeProcessor getProcessorFor(List<String> sourceCode);
}
