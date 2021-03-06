package wniemiec.app.executionflow.io.processing.processor.holeplug;

import java.util.List;

import wniemiec.app.executionflow.io.processing.processor.SourceCodeProcessor;

/**
 * Process 'switch' code block (most specifically, line with 'case' or 
 * 'default' keyword).
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @since 		6.0.0
 */
public class SwitchProcessor extends SourceCodeProcessor {

	//---------------------------------------------------------------------
	//		Constructor
	//---------------------------------------------------------------------
	protected SwitchProcessor(List<String> sourceCode) {
		super(sourceCode, true);
	}
	
	
	//---------------------------------------------------------------------
	//		Methods
	//---------------------------------------------------------------------
	@Override
	protected String processLine(String line) {		
		if (!isSwitchStatement(line))
			return line;
		
		return putVariableNextToColon(line);
	}
	
	private boolean isSwitchStatement(String line) {
		final String regexSwitch = "[\\t\\s\\}]*(case|default)[\\t\\s]+.*:.*";
		
		return line.matches(regexSwitch);
	}
	
	private String putVariableNextToColon(String line) {		
		StringBuilder statement = new StringBuilder();
		int idxColon = line.indexOf(":");
		 
		statement.append(line.substring(0, idxColon+1));
		statement.append("int " + generateVarName() + "=0;"); 
		statement.append(line.substring(idxColon+1));
		
		return statement.toString();
	}
}
