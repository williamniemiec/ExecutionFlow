package executionFlow.io.processor.testmethod;

import java.util.List;

import executionFlow.io.SourceCodeProcessor;

/**
 * Removes all print calls.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.3
 * @since 		5.2.3
 */
public class PrintCallRemover extends SourceCodeProcessor {

	//---------------------------------------------------------------------
	//		Constructor
	//---------------------------------------------------------------------
	public PrintCallRemover(List<String> sourceCode) {
		super(sourceCode, true);
	}
	
	//---------------------------------------------------------------------
	//		Methods
	//---------------------------------------------------------------------
	@Override
	protected String processLine(String line) {
		if (!line.contains("System.out.print"))
			return line;
		
		return line.replaceAll(".*System\\.out\\.print(ln)?\\(.+\\);", "");
	}
}
