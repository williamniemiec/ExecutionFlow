package executionFlow.io.processor.invoked.holeplug;

import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import executionFlow.util.DataUtil;
import executionFlow.util.balance.CurlyBracketBalance;


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
 * @version		5.0.0
 * @since 		5.0.0
 */
public class HolePlug 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private List<String> sourceCode;
	private boolean skipNextLine;
	private boolean wasParsed;
	private boolean insideTestMethod;
	private boolean insideAnonymousClass;
	private CurlyBracketBalance testMethodCBB;
	private Stack<CurlyBracketBalance> anonymousClassesCBB = new Stack<>();

	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	public HolePlug(List<String> sourceCode)
	{
		this.sourceCode = sourceCode;
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	line = printParser.parse(line);
	line = invokerParser.parse(line);
	line = elseParser.parse(line, nextLine);
	line = tryFinallyParser.parse(line, nextLine);
	line = continueBreakParser.parse(line, nextLine);
	line = doWhileParser.parse(line, nextLine);
	line = whileParser.parse(line);
	line = switchParser.parse(line, nextLine);
	line = variableParser.parse(line);
	
	
	public List<String> processLines() {
		List<String> processedLines = sourceCode;
		
		OpenCurlyBracketProcessor openCurlyBracketProcessor = new OpenCurlyBracketProcessor(processedLines);
		processedLines = openCurlyBracketProcessor.processLines();
		
		PrintCallProcessor printCallProcessor = new PrintCallProcessor();
		processedLines = printCallProcessor.processLines(processedLines);
		
		InvokedProcessor invokedProcessor = new InvokedProcessor();
		processedLines = invokedProcessor.processLines(processedLines);
		
		ElseProcessor elseProcessor = new ElseProcessor();
		processedLines = elseProcessor.processLines(processedLines);
		
		TryCatchFinallyProcessor tryCatchFinallyProcessor = new TryCatchFinallyProcessor();
		processedLines = tryCatchFinallyProcessor.processLines(processedLines);
		
		ContinueBreakProcessor continueBreakProcessor = new ContinueBreakProcessor();
		processedLines = continueBreakProcessor.processLines(processedLines);
		
		DoWhileProcessor doWhileProcessor = new DoWhileProcessor();
		processedLines = doWhileProcessor.processLines(processedLines);
		
		WhileProcessor whileProcessor = new WhileProcessor();
		processedLines = whileProcessor.processLines(processedLines);
		
		SwitchProcessor switchProcessor = new SwitchProcessor();
		processedLines = switchProcessor.processLines(processedLines);
		
		UninitializedVariableProcessor uninitializedVariableProcessor = new UninitializedVariableProcessor();
		processedLines = uninitializedVariableProcessor.processLines(processedLines);
		
		return processedLines;
	}
	
	
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
	 * @return		Processed code
	 */
	public List<String> parse()
	{
		final String REGEX_COMMENT_FULL_LINE = 
				"^(\\t|\\ )*(\\/\\/|\\/\\*|\\*\\/|\\*).*";
		final String REGEX_ANONYMOUS_CLASS = 
				".+new(\\s|\\t)+[A-z0-9\\-_$]+\\(([A-z0-9\\-_$\\.,<>\\[\\]\\ \\t])*\\)"
				+ "(\\{|(\\s\\{)||\\/)*(\\s|\\t)*$";
		String line, nextLine;
		PrintParser printParser = new PrintParser();
		InvokedParser invokerParser = new InvokedParser();
		ElseParser elseParser = new ElseParser();
		TryCatchFinallyParser tryFinallyParser = new TryCatchFinallyParser();
		ContinueBreakParser continueBreakParser = new ContinueBreakParser();
		DoWhileParser doWhileParser = new DoWhileParser();
		WhileParser whileParser = new WhileParser();
		SwitchParser switchParser = new SwitchParser();
		VariableParser variableParser = new VariableParser();
		boolean multilineComment = false;
		
		
		for (int i=0; i < sourceCode.size() - 1; i++) {
			if (skipNextLine) {
				skipNextLine = false;
				sourceCode.set(i, ""); // It is necessary to keep line numbers equals to original file 
				continue;
			}
			
			line = sourceCode.get(i);
			nextLine = sourceCode.get(i+1);
	
			wasParsed = false;
			
			// Ignores multiline comments
			if (multilineComment) {
				multilineComment = !line.contains("*/");
				continue;
			}
			
			if (line.contains("/*") && !line.contains("*/")) {
				multilineComment = true;
				continue;
			}
			
			// Checks if it is inside a test method
			if (!line.matches(REGEX_COMMENT_FULL_LINE)) {
				
				
				// Checks if it is a test method
				if (line.contains("@org.junit.Test")) {
					insideTestMethod = true;
					testMethodCBB = new CurlyBracketBalance();
				}
				
				// Checks if it is inside a test method
				if (insideTestMethod) {
					testMethodCBB.parse(line);
					
					if (testMethodCBB.isBalanceEmpty() && testMethodCBB.alreadyIncreased()) {
						insideTestMethod = false;
						testMethodCBB = null;
					}
					
					// Checks if it is an anonymous class
					if (line.matches(REGEX_ANONYMOUS_CLASS)) {
						CurlyBracketBalance cbb = new CurlyBracketBalance();
						
						
						cbb.parse(line);
						anonymousClassesCBB.push(cbb);
					}
				}
				
				// Checks if it is inside an anonymous class
				if (anonymousClassesCBB.size() > 0) {
					CurlyBracketBalance cbb = new CurlyBracketBalance();
					
					
					cbb = anonymousClassesCBB.peek();
					cbb.parse(line);
					
					if (cbb.isBalanceEmpty()) {
						insideAnonymousClass = false;
						anonymousClassesCBB.pop();
					}
					else {
						insideAnonymousClass = true;
					}
				}
				
				// Process the line
				if ((!insideTestMethod && !skipNextLine) || (insideTestMethod && insideAnonymousClass)) {
					line = printParser.parse(line);
					line = invokerParser.parse(line);
					line = elseParser.parse(line, nextLine);
					line = tryFinallyParser.parse(line, nextLine);
					line = continueBreakParser.parse(line, nextLine);
					line = doWhileParser.parse(line, nextLine);
					line = whileParser.parse(line);
					line = switchParser.parse(line, nextLine);
					line = variableParser.parse(line);
				}
			}
			
			sourceCode.set(i, line);
		}
		
		return sourceCode;
	}
}
