package executionFlow.io.processor;

import java.util.List;

import executionFlow.io.SourceCodeProcessor;

/**
 * Deactivates all print calls, replacing them for an instruction that does
 * nothing.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.3
 * @since 		5.2.3
 */
public class PrintCallDeactivator extends SourceCodeProcessor {

	//---------------------------------------------------------------------
	//		Constructor
	//---------------------------------------------------------------------
	public PrintCallDeactivator(List<String> sourceCode) {
		super(sourceCode, true);
	}
	
	//---------------------------------------------------------------------
	//		Methods
	//---------------------------------------------------------------------
	@Override
	protected String processLine(String line) {
		if (!line.contains("System.out.print"))
			return line;
		
		return disablePrintCalls(line);
	}

	private String disablePrintCalls(String line) {
		StringBuilder processedLine = new StringBuilder();

		for (String term : line.split(";")) {
			if (term.contains("System.out.print"))
				processedLine.append("Boolean.parseBoolean(\"True\")");
			else
				processedLine.append(term);	
			
			processedLine.append(";");
		}
		
		return processedLine.toString();
	}
}
