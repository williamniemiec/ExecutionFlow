package executionflow.io.processor.testmethod;

import java.util.List;

import executionflow.io.SourceCodeProcessor;

/**
 * Adds {@link executionflow.runtime.CollectMethodsCalled} annotation next to 
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
		
		return "@executionflow.runtime.CollectMethodsCalled" + " " + line + " ";
	}


	private boolean isTestAnnotation(String line) {
		return	line.contains("@Test") 
				|| line.contains("@org.junit.Test");
	}
	
	private boolean hasSkipInvokedAnnotation(String line) {
		return line.contains("@executionflow.runtime.CollectMethodsCalled");
	}
}