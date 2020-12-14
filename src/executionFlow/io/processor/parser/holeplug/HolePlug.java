package executionFlow.io.processor.parser.holeplug;

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
//		FinalParser finalParser = new FinalParser();
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
//					line = finalParser.parse(line);
				}
			}
			
			sourceCode.set(i, line);
		}
		
		return sourceCode;
	}
	
	/**
	 * Checks if an opening curly bracket is in next line or not. If it is,
	 * moves it to the end of current line.
	 * 
	 * @param		line Current line
	 * @param		nextLine Line following the current line
	 * 
	 * @return		Current line with a opening curly bracket at the end
	 */
	private String checkCurlyBracketNewLine(String line, String nextLine)
	{
		final String regex_onlyOpeningCurlyBracket = "^(\\s|\\t)*\\{(\\s|\\t|\\/)*$";
		
		
		if (nextLine.matches(regex_onlyOpeningCurlyBracket)) {
			line = line + " {";
			skipNextLine = true;
		}
		
		return line;
	}
	
	
	//-------------------------------------------------------------------------
	//		Inner classes
	//-------------------------------------------------------------------------
	/**
	 * Modifies the code in a way that eliminates print statements and put in
	 * place another method, since print statements interfere with 
	 * {@link executionFlow.util.core.JDB} execution.
	 * 
	 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
	 * @version		5.0.0
	 * @since 		5.0.0
	 */
	private class PrintParser
	{
		//---------------------------------------------------------------------
		//		Methods
		//---------------------------------------------------------------------
		/**
		 * Replaces print statements with Boolean.parseBoolean("True") 
		 * method.
		 * 
		 * @param		line Line to be parsed
		 * 
		 * @return		Parsed line or the same line if {@link #wasParsed} is
		 * true
		 */
		public String parse(String line)
		{
			if (wasParsed)
				return line;
			
			if (line.contains("System.out.print")) {
				String[] tmp = line.split(";");
				StringBuilder response = new StringBuilder();
				
				
				// Deletes print's from the line (it will replace it with 
				// 'Boolean.parseBoolean("True")') to avoid problems with
				// JDB
				for (String term : tmp) {
					if (!term.contains("System.out.print")) {
						response.append("Boolean.parseBoolean(\"True\")");
						response.append(";");
					}
					response.append(term);
					response.append(";");
				}
				
				line = response.toString();
				wasParsed = true;
			}
			
			return line;
		}
	}
	
	/**
	 * Responsible for handling invoked declarations.
	 * 
	 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
	 * @version		5.2.0
	 * @since 		5.0.0
	 */
	private class InvokedParser
	{
		//---------------------------------------------------------------------
		//		Attributes
		//---------------------------------------------------------------------
		private boolean insideInvoked;
		
		
		//---------------------------------------------------------------------
		//		Methods
		//---------------------------------------------------------------------
		/**
		 * Parses invoker declaration, adding
		 *  {@link executionFlow.runtime.CollectCalls} annotation.
		 * 
		 * @param		line Line to be parsed
		 * 
		 * @return		Parsed line or the same line if {@link #wasParsed} is
		 * true
		 */
		public String parse(String line)
		{
			if (wasParsed)
				return line;
			
			if (insideInvoked) {
				if (line.contains("{")) {
					insideInvoked = false;
				}
				
				wasParsed = true;
			}
			else if (isInvokedDeclaration(line)) {
				line = "@executionFlow.runtime.CollectCalls " + line;
				insideInvoked = !line.contains("{");
				wasParsed = true;
			}
			
			return line;
		}
		
		/**
		 * Checks whether a line contains an invoked declaration.
		 * 
		 * @param		line Line to be analyzed
		 * 
		 * @return		If the line contains an invoked declaration
		 */
		private boolean isInvokedDeclaration(String line)
		{
			final String REGEX_NEW = "(\\ |\\t)*new[\\s\\t\\{\\n]+";
			final String REGEX_INVOKED_DECLARATION = 
					"[\\s\\t]*(public|protected|private)[\\s\\t]+.+\\(.*\\)[\\s\\t]*\\{[\\s\\t]*$";
			
			boolean isConstructor = line.matches(REGEX_NEW) || line.contains(" new ");
			boolean isMethodDeclaration = line.matches(REGEX_INVOKED_DECLARATION);
			
			
			return	isMethodDeclaration &&
					!line.contains("return ") && 
					!isConstructor;
		}
	}
	
	/**
	 * Responsible for handling if-else and else statements, being useful to
	 * ensure that {@link executionFlow.util.core.JDB} compute test paths correctly.
	 * 
	 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
	 * @version		5.0.0
	 * @since 		5.0.0
	 */
	private class ElseParser
	{
		//---------------------------------------------------------------------
		//		Attributes
		//---------------------------------------------------------------------
		private ElseBlockManager elseBlockManager = new ElseBlockManager();
		private final Pattern PATTERN_ELSE = Pattern.compile("[\\s\\t\\}]*else(\\ |\\t|\\}|$)+.*");
		private final String REGEX_CATCH = "[\\s\\t\\}]*catch(\\ |\\t)*\\(.*\\)(\\ |\\t)*";
		private final String REGEX_IF_ELSE_CLOSED_CURLY_BRACKET = "(\\t|\\ )*\\}(\\t|\\ )*(else if|else)(\\t|\\ )*(\\(|\\{).*";
		private boolean elseNoCurlyBrackets;
		private boolean inlineCommand;
		private boolean removedClosedCB;
		
		
		//---------------------------------------------------------------------
		//		Methods
		//---------------------------------------------------------------------
		/**
		 * Makes adjustments to lines of code to allow test paths to be
		 * computed correctly. These adjustments are:
		 * <ul>
		 * 	<li>Adds an instruction in else statements</li>
		 * 	<li>Adjusts lines containing closing curly bracket + instruction</li>
		 * 	<li>Adds curly brackets in else blocks that does not have curly 
		 * 	brackets</li>
		 * </ul>
		 * 
		 * @param		line Line to be parsed
		 * @param		nextLine Line following the line to be parsed
		 * 
		 * @return		Parsed line		
		 */
		public String parse(String line, String nextLine)
		{
			if (removedClosedCB) {
				removedClosedCB = false;
				line = line.substring(line.indexOf("}")+1);
			}
			
			if (wasParsed)
				return line;
			
			if (inlineCommand) {
				inlineCommand = false;
				
				line = line +"}";
				elseBlockManager.decreaseBalance();
				
				if (elseBlockManager.isCurrentBalanceEmpty()) {
					elseBlockManager.removeCurrentElseBlock();
				}
				
				if (elseBlockManager.getCurrentNestingLevel() == 0) {
					elseNoCurlyBrackets = false;
				}
			}
			else {
				// Checks if a closing curly bracket is on the same line as an
				// else-if or if statement
				if (nextLine.matches(REGEX_IF_ELSE_CLOSED_CURLY_BRACKET)) {
					if (line.contains("//")) {
						int idx_comment = line.indexOf("//");
						
						
						line = line.substring(0, idx_comment) + "}" + line.substring(idx_comment);
					}
					else
						line += "}";
					
					removedClosedCB = true;
				}
				
				// Checks if parser is within a else block without curly brackets
				if (elseNoCurlyBrackets) {
					elseBlockManager.parse(line);
					
					// Checks if else block balance is empty
					if (elseBlockManager.isCurrentBalanceEmpty()) {
						// Checks if it is a line with 'catch' keyword
						if (line.matches(REGEX_CATCH)) {
							// Updates else block balance
							if (line.contains("{") && !line.contains("}")) {
								elseBlockManager.incrementBalance();
							} 
							else if (line.contains("{") && line.contains("}")) {
								line += "}";
								elseBlockManager.removeCurrentElseBlock();
								
								if (elseBlockManager.getCurrentNestingLevel() == 0)
									elseNoCurlyBrackets = false;
							}
						} 
						else if (!nextLine.matches(REGEX_CATCH)) {	// Checks if next line does not have 'catch' keyword
							line += "}";
							elseBlockManager.removeCurrentElseBlock();
							
							if (elseBlockManager.getCurrentNestingLevel() == 0)
								elseNoCurlyBrackets = false;
						}
					}
				}
	
				// Checks if any else blocks have reached at the end. If yes,
				// append '}' in the line, removes this else block and check
				// other else blocks (if there is another)
				while (	elseBlockManager.getCurrentNestingLevel() > 0 && 
						elseBlockManager.getCurrentBalance() == 1 && 
						elseBlockManager.hasBalanceAlreadyPassedTwo()) {
					line += "}";
					elseBlockManager.removeCurrentElseBlock();
				}
	
				// If there are not else blocks, it means that parser left else
				// code block
				if (elseBlockManager.getCurrentNestingLevel() == 0) {
					elseNoCurlyBrackets = false;
				}
	
				// Else
				if (!line.contains("if") && PATTERN_ELSE.matcher(line).find()) {
					line = checkCurlyBracketNewLine(line, nextLine);
					line = parse_else(line);
					
					// Checks if parsed else is an else without curly brackets
					if (elseNoCurlyBrackets) {
						elseBlockManager.createNewElseBlock();
						elseBlockManager.parse(line);
						
						if (!nextLine.contains("{")) {	// Checks if there are not curly brackets in else nor next line
							// Checks if it is one line command
							if (nextLine.matches(".+;$")) { // One line command
								inlineCommand = true;
							}
						}
					}
					
					wasParsed = true;
				}
			}
			
			return line;
		}
		
		/**
		 * Parses line with 'else' keyword.
		 * 
		 * @param		Line with else keyword
		 * 
		 * @return		Processed line (line + variable assignment command)
		 */
		private String parse_else(String line)
		{
			StringBuilder sb = new StringBuilder();
			
			// Checks if block has curly brackets
			if (line.contains("{")) {
				int curlyBracketsIndex = line.indexOf('{');
				
				// Appends in response everything before '{' (including it) 
				sb.append(line.substring(0, curlyBracketsIndex+1));
				
				// Appends in response variable assignment command
				sb.append("int "+DataUtil.generateVarName()+"=0;");
				
				// Appends in response everything after '{' 
				sb.append(line.substring(curlyBracketsIndex+1));
			} 
			else {	// Else code block without curly brackets
				int indexAfterElse = line.indexOf("else")+4; 
				
				// Appends in response everything before 'else' keyword (including it) 
				sb.append(line.substring(0, indexAfterElse));
				
				// Appends in response variable assignment command
				sb.append(" {"+"int "+DataUtil.generateVarName()+"=0;");
				
				String afterElse = line.substring(indexAfterElse);
				
				// Checks if there is a command after 'else' keyword
				if (!afterElse.isEmpty() && !afterElse.matches("^(\\s|\\t)+$")) {
					sb.append(afterElse);	// If there is one, it its an in line else code block
					sb.append("}");			// Appends in response this command and a closing curly bracket
				} 
				else {
					elseNoCurlyBrackets = true;	// Else it is a else code block with more than one line
				}
			}
			
			return sb.toString();
		}
	}
	
	/**
	 * Responsible for handling try-catch-finally statements, being useful to
	 * ensure that {@link executionFlow.util.core.JDB} compute test paths correctly.
	 * 
	 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
	 * @version		5.2.0
	 * @since 		5.0.0
	 */
	private class TryCatchFinallyParser
	{
		//---------------------------------------------------------------------
		//		Attributes
		//---------------------------------------------------------------------
		private final String REGEX_TRY_FINALLY = "[\\t\\s\\}]*(try|finally)[\\s\\t\\{]+";
		private final String REGEX_CATCH_CLOSED_CURLY_BRACKET = 
				"(\\t|\\ )*\\}(\\t|\\ )*catch(\\t|\\ )*[\\(|\\{]+.*";
		private boolean removedClosedCB;
		
		
		//---------------------------------------------------------------------
		//		Methods
		//---------------------------------------------------------------------
		/**
		 * Makes adjustments to lines of code to allow test paths to be
		 * computed correctly. It will adjust lines containing closed curly
		 * bracket + catch statement. Also, it will add an instruction in try 
		 * and finally statements.
		 * 
		 * @param		line Line to be parsed
		 * @param		nextLine Line following the line to be parsed
		 * 
		 * @return		Parsed line		
		 */
		public String parse(String line, String nextLine)
		{
			if (removedClosedCB) {
				removedClosedCB = false;
				line = line.substring(line.indexOf("}")+1);
			}
			
			if (wasParsed)
				return line;
			
			if (line.matches(REGEX_TRY_FINALLY)) {	
				line = checkCurlyBracketNewLine(line, nextLine);
				line = parse_try_finally(line);
				wasParsed = true;
			}
			
			// Checks if closed curly bracket is on the same line as an
			// else-if or if statement
			if (nextLine.matches(REGEX_CATCH_CLOSED_CURLY_BRACKET)) {
				if (line.contains("//")) {
					int idx_comment = line.indexOf("//");
					
					
					line = line.substring(0, idx_comment) + "}" + line.substring(idx_comment);
				}
				else
					line += "}";
				
				removedClosedCB = true;
			}
			
			return line;
		}
		
		/**
		 * Parses line with 'try' or 'finally' keywords.
		 * 
		 * @param		Line with try or finally keyword
		 * 
		 * @return		Processed line (line + variable assignment command)
		 */
		private String parse_try_finally(String line)
		{
			StringBuilder response = new StringBuilder();

			if (line.contains("{")) {
				int curlyBracketsIndex = line.indexOf('{');
				
				// Appends in response everything before '{' (including it) 
				response.append(line.substring(0, curlyBracketsIndex+1));
				
				// Appends in response variable assignment command
				response.append("int "+DataUtil.generateVarName()+"=0;");

				// Appends in response everything after '{'
				response.append(line.substring(curlyBracketsIndex+1));
			} 
			else {
				throw new IllegalStateException("Code block must be enclosed in curly brackets");
			}

			return response.toString();
		}
	}
	
	/**
	 * Responsible for handling continue and break instructions, being useful to
	 * ensure that {@link executionFlow.util.core.JDB} compute test paths correctly.
	 * 
	 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
	 * @version		5.0.0
	 * @since 		5.0.0
	 */
	private class ContinueBreakParser
	{
		//---------------------------------------------------------------------
		//		Attributes
		//---------------------------------------------------------------------
		private final String REGEX_CONTINUE_BREAK = "^(\\ |\\t)*(continue|break)(\\ |\\t)*[^;\\s\\t\\(\\)\\.]*;";
		
		
		//---------------------------------------------------------------------
		//		Methods
		//---------------------------------------------------------------------
		/**
		 * Parses line with 'continue' or 'break' keyword. It will add the following
		 * code: <br /> 
		 * 
		 * <code>if (Boolean.parseBoolean("True")) { &lt;line&gt; }</code>. <br />
		 * 
		 * This method cannot add an if clause like "if (true) {line}" because it
		 * is ignored when class is compiled. The function 'parseBoolean' is just 
		 * a randomly chosen function and can be replaced by any other function
		 * that returns true.
		 * 
		 * @param		line Line with 'continue' or 'break' keyword
		 * 
		 * @return		Processed line ("if (Boolean.parseBoolean("True")) {"+line+"}"
		 */
		public String parse(String line, String nextLine)
		{
			if (wasParsed)
				return line;
			
			if (line.matches(REGEX_CONTINUE_BREAK)) {	
				line = checkCurlyBracketNewLine(line, nextLine);
				line = "if (Boolean.parseBoolean(\"True\")) {"+line+"}";
				
				if (line.contains("break;"))
					line += "break;";
				
				wasParsed = true;
			}
			
			return line;
		}
	}
	
	/**
	 * Responsible for handling do-while statements, being useful to ensure 
	 * that {@link executionFlow.util.core.JDB} compute test paths correctly.
	 * 
	 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
	 * @version		5.0.0
	 * @since 		5.0.0
	 */
	private class DoWhileParser
	{
		//---------------------------------------------------------------------
		//		Attributes
		//---------------------------------------------------------------------
		private final Pattern PATTERN_DO = Pattern.compile("[\\t\\s\\}]*do[\\n\\s\\t\\{]+");
		
		
		//---------------------------------------------------------------------
		//		Methods
		//---------------------------------------------------------------------
		/**
		 * Makes adjustments to lines of code to allow test paths to be
		 * computed correctly. It will add an instruction in do statements.
		 * 
		 * @param		line Line to be parsed
		 * @param		nextLine Line following the line to be parsed
		 * 
		 * @return		Parsed line		
		 */
		public String parse(String line, String nextLine)
		{
			if (wasParsed)
				return line;
			
			if (PATTERN_DO.matcher(line).find()) {								
				line = checkCurlyBracketNewLine(line, nextLine);
				line = parse_do(line);
				wasParsed = true;
			}
			
			return line;
		}
		
		/**
		 * Parses line with 'do' keyword.
		 * 
		 * @param		Line with 'do' keyword
		 * 
		 * @return		Processed line (line + variable assignment command)
		 */
		private String parse_do(String line)
		{
			StringBuilder response = new StringBuilder();

			if (line.contains("{")) {
				int curlyBracketsIndex = line.indexOf('{');
				
				// Appends in response everything before '{' (including it) 
				response.append(line.substring(0, curlyBracketsIndex+1));
				
				// Appends in response variable assignment command
				response.append("int "+DataUtil.generateVarName()+"=0;");
				
				// Appends in response everything after '{' 
				response.append(line.substring(curlyBracketsIndex+1));
			} 
			else {
				throw new IllegalStateException("Code block must be enclosed in curly brackets; line: " + line);
			}
			
			return response.toString();
		}
	}
	/**
	 * Responsible for handling while statements, being useful to ensure 
	 * that {@link executionFlow.util.core.JDB} compute test paths correctly.
	 * 
	 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
	 * @version		5.2.3
	 * @since 		5.2.3
	 */
	private class WhileParser
	{
		//---------------------------------------------------------------------
		//		Methods
		//---------------------------------------------------------------------
		/**
		 * Parses only <code>while(true)</code>.
		 * 
		 * @param		line Line to be parsed
		 * @param		nextLine Line following the line to be parsed
		 * 
		 * @return		Parsed lines
		 */
		public String parse(String line)
		{
			if (wasParsed)
				return line;
			
			if (line.matches(".*while[\\s\\t]+\\([\\s\\t]*true[\\s\\t]*\\)[\\s\\t{]*[^}]")) {								
				line = parse_while(line);
				wasParsed = true;
			}
			
			return line;
		}
		
		/**
		 * Parses line with 'while' keyword.
		 * 
		 * @param		Line with 'while' keyword
		 * 
		 * @return		Processed line (line + variable assignment command)
		 */
		private String parse_while(String line)
		{
			StringBuilder response = new StringBuilder();

			if (line.contains("{")) {
				int curlyBracketsIndex = line.indexOf('{');
				
				// Appends in response everything before '{' (including it) 
				response.append(line.substring(0, curlyBracketsIndex+1));
				
				// Appends in response variable assignment command
				response.append("int "+DataUtil.generateVarName()+"=0;");
				
				// Appends in response everything after '{' 
				response.append(line.substring(curlyBracketsIndex+1));
			} 
			else {
				throw new IllegalStateException("Code block must be enclosed in curly brackets; line: " + line);
			}
			
			return response.toString();
		}
	}
	
	/**
	 * Responsible for handling switch statements, being useful to ensure 
	 * that {@link executionFlow.util.core.JDB} compute test paths correctly.
	 * 
	 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
	 * @version		5.2.0
	 * @since 		5.0.0
	 */
	private class SwitchParser
	{
		//---------------------------------------------------------------------
		//		Attributes
		//---------------------------------------------------------------------
		private final String PATTERN_SWITCH = "[\\t\\s\\}]*(case|default)[\\t\\s]+.*:.*";
		
		
		//---------------------------------------------------------------------
		//		Methods
		//---------------------------------------------------------------------
		/**
		 * Makes adjustments to lines of code to allow test paths to be
		 * computed correctly. It will add an instruction in case statements.
		 * 
		 * @param		line Line to be parsed
		 * @param		nextLine Line following the line to be parsed
		 * 
		 * @return		Parsed line		
		 */
		public String parse(String line, String nextLine)
		{
			if (wasParsed)
				return line;
			
			if (line.matches(PATTERN_SWITCH)) {							
				line = checkCurlyBracketNewLine(line, nextLine);
				line = parse_switch(line);
				wasParsed = true;
			}
			
			return line;
		}
		
		/**
		 * Parses 'switch' code block (most specifically, line with 'case' or 
		 * 'default' keyword).
		 * 
		 * @param		Line with 'case' or 'default' keyword (inside a switch 
		 * block)
		 * 
		 * @return		Processed line (line + variable assignment command)
		 */
		private String parse_switch(String line)
		{
			StringBuilder response = new StringBuilder();
			Pattern p = Pattern.compile(":");
			Matcher m = p.matcher(line);
			
			
			// Appends in response everything before ':' (including it) 
			m.find();
			response.append(line.substring(0, m.start()+1));
			
			// Appends in response variable assignment command
			response.append("int "+DataUtil.generateVarName()+"=0;");
			
			// Appends in response everything after ':'
			response.append(line.substring(m.start()+1));

			return response.toString();
		}
	}
	
	/**
	 * Responsible for handling variable declarations, being useful to ensure 
	 * that {@link executionFlow.util.core.JDB} compute test paths correctly.
	 * 
	 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
	 * @version		5.2.3
	 * @since 		5.0.0
	 */
	private class VariableParser
	{
		//---------------------------------------------------------------------
		//		Attributes
		//---------------------------------------------------------------------
		private final String REGEX_VARIABLE_DECLARATION_WITHOUT_INITIALIZATION = 
				"( |\\t)*(final(\\s|\\t)+)?[A-z0-9\\-_$<?>,\\.]+(\\s|\\t)[A-z0-9\\-_$]+(((,)[A-z0-9\\-_$]+)?)+;";
		
		
		//---------------------------------------------------------------------
		//		Methods
		//---------------------------------------------------------------------
		/**
		 * It will add an instruction in variable declarations without 
		 * initialization.
		 * 
		 * @param		line Line to be parsed
		 * 
		 * @return		Parsed line
		 */
		public String parse(String line)
		{
			if (wasParsed)
				return line;
			
			if (isVarDeclaration(line)) {
				line = parse_varDeclaration(line);
				wasParsed = true;
			}
			
			return line;
		}
		
		/**
		 * Parses line with variable declaration without initialization.
		 * 
		 * @param		Line with variable declaration line without initialization
		 * 
		 * @return		Processed line (line + variable assignment command)
		 */
		private String parse_varDeclaration(String line)
		{
			return line+"int "+DataUtil.generateVarName()+"=0;";
		}
		
		/**
		 * Checks whether a line is a variable declaration.
		 * 
		 * @param		line Line to be analyzed
		 * 
		 * @return		True if the line is a variable declaration; false otherwise
		 */
		private boolean isVarDeclaration(String line)
		{
			return	!line.contains("return ") && 
					!line.contains("return(") &&
					!line.contains("throw ") && 
					!line.contains("package ") && 
					!line.contains("class ") && 
					line.matches(REGEX_VARIABLE_DECLARATION_WITHOUT_INITIALIZATION);
		}
	}
	
	/**
	 * Responsible for managing else blocks according to its nesting levels. It
	 * is used only for else blocks without curly brackets, because its 
	 * usefulness is add curly brackets in these blocks.
	 * 
	 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
	 * @version		5.0.0
	 * @since 		5.0.0
	 */
	private class ElseBlockManager
	{
		//---------------------------------------------------------------------
		//		Attributes
		//---------------------------------------------------------------------
		/**
		 * Stores current nesting level of else block.
		 */
		private int currentNestingLevel;
		
		/**
		 * Stores curly bracket balance for each else block nesting level.
		 */
		private Stack<CurlyBracketBalance> elseBlocks_balance;
		
		/**
		 * Stores if at any time the curly bracket balance of each else block 
		 * nesting level was equal to 2.
		 */
		private Stack<Boolean> elseBlocks_passedTwo;
		
		
		//---------------------------------------------------------------------
		//		Initialization block
		//---------------------------------------------------------------------
		{
			elseBlocks_balance = new Stack<>();
			elseBlocks_passedTwo = new Stack<>();
		}
		
		
		//---------------------------------------------------------------------
		//		Methods
		//---------------------------------------------------------------------
		/**
		 * Updates curly bracket balance of current nested level parsing a line.
		 * 
		 * @param		line Line with curly brackets
		 */
		public void parse(String line)
		{
			elseBlocks_balance.peek().parse(line);
		}
		
		/**
		 * Creates a new else block, storing it at the 
		 * {@link #elseBlocks_balance else blocks stack}. It will also 
		 * increment {@link #currentNestingLevel current nesting level}.
		 */
		public void createNewElseBlock()
		{
			currentNestingLevel++;
			elseBlocks_balance.push(new CurlyBracketBalance());
			elseBlocks_passedTwo.push(false);
		}
		
		/**
		 * Removes else block of {@link #currentNestingLevel current nesting level} 
		 * from the {@link #elseBlocks_balance else blocks stack}. If stack is 
		 * empty, do not do anything.
		 */
		public void removeCurrentElseBlock()
		{
			if (elseBlocks_balance.size() > 0) {
				elseBlocks_balance.pop();
				elseBlocks_passedTwo.pop();
				currentNestingLevel--;
			}
		}
		
		/**
		 * Gets {@link #currentNestingLevel current nesting level}.
		 * 
		 * @return		Current nesting level
		 */
		public int getCurrentNestingLevel()
		{
			return currentNestingLevel;
		}
		
		/**
		 * Increments balance of else block associated with 
		 * {@link #currentNestingLevel current nesting level}. If nesting level
		 * is zero, do not do anything.
		 * 
		 * @apiNote		Should be called only when an open curly bracket is found
		 */
		public void incrementBalance()
		{
			if (currentNestingLevel != 0) {
				elseBlocks_balance.peek().increaseBalance();
				
				if (elseBlocks_balance.peek().getBalance() >= 2) {
					elseBlocks_passedTwo.pop();
					elseBlocks_passedTwo.push(true);
				}
			}
		}
		
		/**
		 * Decrements balance of else block associated with 
		 * {@link #currentNestingLevel current nesting level}. If nesting level
		 * is zero, do not do anything.
		 * 
		 * @apiNote		Should be called only when a closed curly bracket is found
		 */
		public void decreaseBalance()
		{
			if (currentNestingLevel != 0)
				elseBlocks_balance.peek().decreaseBalance();
		}
		
		/**
		 * Returns balance of else block associated with 
		 * {@link #currentNestingLevel current nesting level}. If nesting level
		 * is zero, do not do anything.
		 * 
		 * @return		Current balance or -1 if nesting level is zero
		 */
		public int getCurrentBalance()
		{
			if (currentNestingLevel == 0) { return -1; }
			
			return elseBlocks_balance.peek().getBalance();
		}
		
		/**
		 * Checks if balance of else block associated with 
		 * {@link #currentNestingLevel current nesting level} is empty.
		 * 
		 * @return		If balance is zero
		 */
		public boolean isCurrentBalanceEmpty()
		{
			if (currentNestingLevel == 0) { return true; }
			
			return elseBlocks_balance.peek().isBalanceEmpty();
		}
		
		/**
		 * Checks if at any time the balance of else block associated with 
		 * {@link #currentNestingLevel current nesting level} was equal to 2.
		 * 
		 * @return		If at any time the balance was equal to 2
		 */
		private boolean hasBalanceAlreadyPassedTwo()
		{
			if (currentNestingLevel == 0) { return false; }
			
			return elseBlocks_passedTwo.peek();
		}
	}
	
	private class FinalParser
	// TODO should be in a separate class
	{
		//---------------------------------------------------------------------
		//		Methods
		//---------------------------------------------------------------------
		public String parse(String line)
		{
			if (!line.contains("static ") && 
					line.matches(".*([^A-z]+|[\\s\\t]*)final[\\s\\t]+.+")) {								
				Matcher m = Pattern.compile("final[\\s\\t]+").matcher(line);
				
				if (m.find())
					line = line.replaceAll(m.group(), "");
			}
			
			return line;
		}
	}
}
