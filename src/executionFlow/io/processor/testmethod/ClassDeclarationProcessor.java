package executionFlow.io.processor.testmethod;

import java.util.List;

import executionFlow.io.SourceCodeProcessor;

/**
 * Adds {@link executionFlow.runtime.SkipCollection} annotation next to 
 * class declarations.
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
		String processedLine = line;
		
		final String REGEX_SKIP_COLLECTION = ".*(@.+\\.SkipCollection).*";
		String skipCollectionAnnotation = "@executionFlow.runtime.SkipCollection";
		boolean isClassDeclaration = line.contains("class ") && !line.contains("new ");
		
		
		if (isClassDeclaration && !line.matches(REGEX_SKIP_COLLECTION)) {
			processedLine =  skipCollectionAnnotation + " " + line;
		}
		
		return processedLine;
	}
}
