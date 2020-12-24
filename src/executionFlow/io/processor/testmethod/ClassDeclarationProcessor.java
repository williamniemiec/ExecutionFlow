package executionflow.io.processor.testmethod;

import java.util.List;

import executionflow.io.SourceCodeProcessor;

/**
 * Adds {@link executionflow.runtime.SkipCollection} annotation next to 
 * class declarations.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		6.0.0
 * @since 		6.0.0
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
		
		return "@executionflow.runtime.SkipCollection" + " " + line;
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
