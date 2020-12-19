package executionFlow.io.processor.invoked.holeplug;

import java.util.List;

import executionFlow.io.processor.SourceCodeProcessor;
import executionFlow.util.DataUtil;

/**
 * Process 'switch' code block (most specifically, line with 'case' or 
 * 'default' keyword).
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
		statement.append("int " + DataUtil.generateVarName() + "=0;"); 
		statement.append(line.substring(idxColon+1));
		
		return statement.toString();
	}
}
