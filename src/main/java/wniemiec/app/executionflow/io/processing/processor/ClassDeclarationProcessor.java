package wniemiec.app.executionflow.io.processing.processor;

import java.util.List;

/**
 * Adds {@link wniemiec.app.executionflow.runtime.SkipCollection} annotation next to 
 * class declarations.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
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
		
		return "@wniemiec.app.executionflow.runtime.SkipCollection" + " " + line;
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
