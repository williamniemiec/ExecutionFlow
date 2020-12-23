package executionFlow.io.processor.testmethod;

import java.util.List;

import executionFlow.io.SourceCodeProcessor;

/**
 * Adds {@link executionFlow.runtime.SkipCollection} annotation next to 
 * class declarations.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.3
 * @since 		5.2.3
 */
public class ClassDeclarationProcessor extends SourceCodeProcessor {
	
	//---------------------------------------------------------------------
	//		Constructor
	//---------------------------------------------------------------------
	public ClassDeclarationProcessor(List<String> sourceCode) {
		super(sourceCode, true);
	}
	

	//---------------------------------------------------------------------
	//		Methods
	//---------------------------------------------------------------------
	@Override
	protected String processLine(String line) {
		if (!isClassDeclaration(line) || hasSkipCollection(line))
			return line;
		
		return "@executionFlow.runtime.SkipCollection" + " " + line;
	}
	
	private boolean isClassDeclaration(String line) {
		return 	line.contains("class ") 
				&& !line.contains("new ");
	}
	
	private boolean hasSkipCollection(String line) {
		final String regexSkipCollection = ".*(@.+\\.SkipCollection).*";
		
		return line.matches(regexSkipCollection);
	}
}
