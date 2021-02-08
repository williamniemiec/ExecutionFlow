package executionflow.io.processor.invoked.holeplug;

import java.util.List;

import executionflow.io.SourceCodeProcessor;

/**
 * Add an instruction in variable declarations without 
 * initialization.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		6.0.5
 * @since 		6.0.0
 */
public class UninitializedVariableProcessor extends SourceCodeProcessor {
	
	//---------------------------------------------------------------------
	//		Constructor
	//---------------------------------------------------------------------
	protected UninitializedVariableProcessor(List<String> sourceCode) {
		super(sourceCode, true);
	}
	
	
	//---------------------------------------------------------------------
	//		Methods
	//---------------------------------------------------------------------
	@Override
	protected String processLine(String line) {		
		if (!isUninitializedVarDeclaration(line))
			return line;
		
		return putVariableNextTo(line);
	}

	private boolean isUninitializedVarDeclaration(String line) {
		final String regexUninitializedVarDeclaration = 
				"( |\\t)*(final(\\s|\\t)+)?[A-z0-9\\-_$<?>,\\.]+"
				+ "(\\s|\\t)[A-z0-9\\-_$]+(((,)[A-z0-9\\-_$]+)?)+;";
		
		return	line.matches(regexUninitializedVarDeclaration)
				&& !line.contains("return ") 
				&& !line.contains("return(")
				&& !line.contains("throw ")
				&& !line.contains("package ") 
				&& !line.contains("class ");
	}
	
	private String putVariableNextTo(String line) {
		return line + "int " + generateVarName() + "=0;";
	}
}
