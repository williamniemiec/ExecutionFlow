package executionFlow.io.processor.testmethod;

import java.util.List;

import executionFlow.io.processor.SourceCodeProcessor;

public class PrintCallProcessor extends SourceCodeProcessor{

	//---------------------------------------------------------------------
	//		Constructor
	//---------------------------------------------------------------------
	public PrintCallProcessor(List<String> sourceCode) {
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
