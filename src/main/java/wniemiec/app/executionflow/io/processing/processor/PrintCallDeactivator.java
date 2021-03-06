package wniemiec.app.executionflow.io.processing.processor;

import java.util.List;

/**
 * Deactivates all print calls, replacing them for an instruction that does
 * nothing.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @since 		6.0.0
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
