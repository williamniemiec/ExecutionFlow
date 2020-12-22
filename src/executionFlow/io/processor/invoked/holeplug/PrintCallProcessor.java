package executionFlow.io.processor.invoked.holeplug;

import java.util.List;

import executionFlow.io.SourceCodeProcessor;

/**
 * Disables print calls, replacing them with a method call that does nothing.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.3
 * @since 		5.2.3
 */
public class PrintCallProcessor extends SourceCodeProcessor {

	//---------------------------------------------------------------------
	//		Methods
	//---------------------------------------------------------------------
	protected PrintCallProcessor(List<String> sourceCode) {
		super(sourceCode, true);
	}
	

	//---------------------------------------------------------------------
	//		Methods
	//---------------------------------------------------------------------
	@Override
	protected String processLine(String line) {
		String processedLine = line;
		
		if (line.contains("System.out.print"))
			processedLine = line.replaceAll(".*System\\.out\\.print(ln)?\\(.+\\);", "");
		
		return processedLine;
	}
}
