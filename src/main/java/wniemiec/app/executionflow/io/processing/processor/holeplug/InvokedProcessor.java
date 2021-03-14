package wniemiec.app.executionflow.io.processing.processor.holeplug;

import java.util.List;

import wniemiec.app.executionflow.io.processing.processor.SourceCodeProcessor;

/**
 * Adds {@link wniemiec.app.executionflow.runtime.CollectCalls} annotation in method and
 * constructor declarations.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		6.0.0
 * @since 		6.0.0
 */
public class InvokedProcessor extends SourceCodeProcessor {
	
	//---------------------------------------------------------------------
	//		Attributes
	//---------------------------------------------------------------------
	private boolean insideInvoked;
	
	
	//---------------------------------------------------------------------
	//		Constructor
	//---------------------------------------------------------------------
	protected InvokedProcessor(List<String> sourceCode) {
		super(sourceCode, true);
	}
	
	
	//---------------------------------------------------------------------
	//		Methods
	//---------------------------------------------------------------------
	@Override
	protected String processLine(String line) {	
		String processedLine = line;

		if (insideInvoked) {
			if (line.contains("{")) {
				insideInvoked = false;
			}
		}
		else if (isInvokedDeclaration(line)) {
			processedLine = "@wniemiec.app.executionflow.runtime.CollectCalls " + line;
			insideInvoked = !line.contains("{");
		}
		
		return processedLine;
	}
	
	private boolean isInvokedDeclaration(String line) {
		return	isMethodDeclaration(line)
				&& !isConstructor(line)
				&& !line.contains("return ");
	}
	
	private boolean isMethodDeclaration(String line) {
		final String regexMethodDeclaration = 
				"[\\s\\t]*(public|protected|private)[\\s\\t]+.+"
				+ "\\(.*\\)[\\s\\t]*\\{[\\s\\t]*$";
		
		return line.matches(regexMethodDeclaration);
	}
	
	private boolean isConstructor(String line) {
		final String regexConstructorInstantiation = 
				"(\\ |\\t)*new[\\s\\t\\{\\n]+";
		
		return	line.matches(regexConstructorInstantiation) || 
				line.contains(" new ");
	}
}
