package executionFlow.io.processor.invoked.holeplug;

import java.util.List;

import executionFlow.io.SourceCodeProcessor;

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
		
		if (line.contains("System.out.print")) {
			processedLine = line.replaceAll(".*System\\.out\\.print(ln)?\\(.+\\);", "");
		}
		
		return processedLine;
	}
}
