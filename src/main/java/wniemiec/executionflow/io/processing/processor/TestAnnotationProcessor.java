package wniemiec.executionflow.io.processing.processor;

import java.util.List;

/**
 * Adds {@link wniemiec.executionflow.runtime.CollectMethodsCalled} annotation next to 
 * test annotations.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		6.0.0
 * @since 		6.0.0
 */
public class TestAnnotationProcessor extends SourceCodeProcessor {

	//---------------------------------------------------------------------
	//		Constructor
	//---------------------------------------------------------------------
	public TestAnnotationProcessor(List<String> sourceCode) {
		super(sourceCode, true);
	}
	

	//---------------------------------------------------------------------
	//		Methods
	//---------------------------------------------------------------------
	@Override
	protected String processLine(String line) {
		if (!isTestAnnotation(line) || hasSkipInvokedAnnotation(line))
			return line;
		
		return "@wniemiec.executionflow.runtime.CollectMethodsCalled" + " " + line + " ";
	}


	private boolean isTestAnnotation(String line) {
		return	line.contains("@Test") 
				|| line.contains("@org.junit.Test");
	}
	
	private boolean hasSkipInvokedAnnotation(String line) {
		return line.contains("@wniemiec.executionflow.runtime.CollectMethodsCalled");
	}
}
