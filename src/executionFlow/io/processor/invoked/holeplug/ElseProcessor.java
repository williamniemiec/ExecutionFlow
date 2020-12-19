package executionFlow.io.processor.invoked.holeplug;

import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

import executionFlow.io.processor.SourceCodeProcessor;
import executionFlow.util.DataUtil;
import executionFlow.util.balance.CurlyBracketBalance;

/**
 * Adds instructions next to else keywords.
 */
public class ElseProcessor extends SourceCodeProcessor {

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
	//		Constructor
	//---------------------------------------------------------------------
	protected ElseProcessor(List<String> sourceCode) {
		super(sourceCode, true);
	}
	
	
	//---------------------------------------------------------------------
	//		Methods
	//---------------------------------------------------------------------
	@Override
	protected String processLine(String line) {		
		if (!isDoKeyword(line))
			return line;
		
		return putVariableNextToOpenCurlyBracket(line);
	}
	
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
	
	
	//-------------------------------------------------------------------------
	//		Inner classes
	//-------------------------------------------------------------------------			
	
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
}
