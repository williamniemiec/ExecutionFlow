package wniemiec.app.executionflow.io.processing.processor.holeplug;

import java.util.List;

/**
 * Adds instructions to pieces of code that are omitted during compilation.
 * Are they:
 * <ul>
 * 	<li>if-else clauses</li>
 * 	<li>try-catch-finally clauses</li>
 * 	<li>continue and break keywords</li>
 * 	<li>do-while clauses</li>
 * 	<li>switch clauses</li>
 * 	<li>variable declarations that are not initialized</li>
 * </ul>
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @since 		5.0.0
 */
public class HolePlug {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private List<String> sourceCode;

	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	public HolePlug(List<String> sourceCode) {
		this.sourceCode = sourceCode;
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	public List<String> processLines() {
		moveAloneOpenCurlyBracketsToThePreviousLine();
		
		putCollectAnnotationNextToInvokedDeclarations();
		
		addCurlyBracketsInElseStatementsWithoutCurlyBrackets();
		
		addInstructionNextToElseKeywords();
		addInstructionNextToTryFinallyKeywords();
		addInstructionNextToContinueBreakKeywords();
		addInstructionNextToDoWhileKeywords();
		addInstructionNextToWhileKeywords();
		addInstructionInSwitchStructures();
		addInstructionNextToUninitializedVariables();
		
		return sourceCode;
	}

	private void addInstructionNextToUninitializedVariables() {
		UninitializedVariableProcessor uninitializedVariableProcessor = 
				new UninitializedVariableProcessor(sourceCode);
		
		sourceCode = uninitializedVariableProcessor.processLines();
	}

	private void addInstructionInSwitchStructures() {
		SwitchProcessor switchProcessor = new SwitchProcessor(sourceCode);
		
		sourceCode = switchProcessor.processLines();
	}

	private void addInstructionNextToWhileKeywords() {
		WhileProcessor whileProcessor = new WhileProcessor(sourceCode);
		
		sourceCode = whileProcessor.processLines();
	}

	private void addInstructionNextToDoWhileKeywords() {
		DoWhileProcessor doWhileProcessor = new DoWhileProcessor(sourceCode);
		
		sourceCode = doWhileProcessor.processLines();
	}

	private void addInstructionNextToContinueBreakKeywords() {
		ContinueBreakProcessor continueBreakProcessor =
				new ContinueBreakProcessor(sourceCode);
		
		sourceCode = continueBreakProcessor.processLines();
	}

	private void addInstructionNextToTryFinallyKeywords() {
		TryCatchFinallyProcessor tryCatchFinallyProcessor = 
				new TryCatchFinallyProcessor(sourceCode);
		
		sourceCode = tryCatchFinallyProcessor.processLines();
	}

	private void addInstructionNextToElseKeywords() {
		ElseProcessor elseProcessor = new ElseProcessor(sourceCode);
		
		sourceCode = elseProcessor.processLines();
	}

	private void addCurlyBracketsInElseStatementsWithoutCurlyBrackets() {
		ElseWithoutCurlyBracketProcessor processor = 
				new ElseWithoutCurlyBracketProcessor(sourceCode);
		
		sourceCode = processor.processLines();
	}

	private void putCollectAnnotationNextToInvokedDeclarations() {
		InvokedProcessor invokedProcessor = new InvokedProcessor(sourceCode);
		
		sourceCode = invokedProcessor.processLines();
	}

	private void moveAloneOpenCurlyBracketsToThePreviousLine() {
		OpenCurlyBracketProcessor openCurlyBracketProcessor = 
				new OpenCurlyBracketProcessor(sourceCode);
		
		sourceCode = openCurlyBracketProcessor.processLines();
	}
}
