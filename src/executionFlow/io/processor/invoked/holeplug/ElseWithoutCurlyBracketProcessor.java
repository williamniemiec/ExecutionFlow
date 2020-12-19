package executionFlow.io.processor.invoked.holeplug;

import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

import executionFlow.io.processor.SourceCodeProcessor;
import executionFlow.util.balance.CurlyBracketBalance;

public class ElseWithoutCurlyBracketProcessor extends SourceCodeProcessor {

	//---------------------------------------------------------------------
	//		Attributes
	//---------------------------------------------------------------------
	private ElseBlockManager elseBlockManager = new ElseBlockManager();
	
	private final String REGEX_CATCH = "[\\s\\t\\}]*catch(\\ |\\t)*\\(.*\\)(\\ |\\t)*";
	private final String REGEX_IF_ELSE_CLOSED_CURLY_BRACKET = "(\\t|\\ )*\\}(\\t|\\ )*(else if|else)(\\t|\\ )*(\\(|\\{).*";
	private boolean elseWithoutCurlyBrackets;
	private boolean inlineCommand;
	private boolean removeClosedCurlyBracketFromNextLine;
	
	
	//---------------------------------------------------------------------
	//		Constructor
	//---------------------------------------------------------------------
	protected ElseWithoutCurlyBracketProcessor(List<String> sourceCode) {
		super(sourceCode, true);
	}
	
	
	//---------------------------------------------------------------------
	//		Methods
	//---------------------------------------------------------------------
	@Override
	protected String processLine(String line) {		
		return processLine(line, getNextLine());
	}
	
	
	private String processLine(String line, String nextLine)
	{
		// TODO should be refactored
		if (removeClosedCurlyBracketFromNextLine) {
			removeClosedCurlyBracketFromNextLine = false;
			line = line.substring(line.indexOf("}")+1);
		}

		if (inlineCommand) {
			line = processInlineCommand(line);
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
				
				removeClosedCurlyBracketFromNextLine = true;
			}
			
			// Checks if parser is within a else block without curly brackets
			if (elseWithoutCurlyBrackets) {
				line = processElseWithoutCurlyBracket(line, nextLine);
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
				elseWithoutCurlyBrackets = false;
			}

			// Else
			if (isElseKeyword(line)) {
				if (isInlineElseWithMoreThanOneLine(line)) {
					elseWithoutCurlyBrackets = true;	// Else it is a else code block with more than one line
				}
				
				if (elseWithoutCurlyBrackets) {
					elseBlockManager.createNewElseBlock();
					elseBlockManager.parse(line);
					
					if (!nextLine.contains("{") && isInlineCommand(nextLine)) {
						inlineCommand = true;
					}
				}
			}
		}
		
		return line;
	}
	
	private boolean isInlineCommand(String line) {
		return line.matches(".+;$");
	}
	
	private boolean isInlineElseWithMoreThanOneLine(String line) {
		String afterElse = line.substring(line.indexOf("else")+4);
		
		return afterElse.isEmpty() || afterElse.matches("^(\\s|\\t)+$");
	}


	private String processElseWithoutCurlyBracket(String line, String nextLine) {
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
						elseWithoutCurlyBrackets = false;
				}
			} 
			else if (!nextLine.matches(REGEX_CATCH)) {	// Checks if next line does not have 'catch' keyword
				line += "}";
				elseBlockManager.removeCurrentElseBlock();
				
				if (elseBlockManager.getCurrentNestingLevel() == 0)
					elseWithoutCurlyBrackets = false;
			}
		}
		return line;
	}


	private String processInlineCommand(String line) {
		inlineCommand = false;
		
		line = line +"}";
		elseBlockManager.decreaseBalance();
		
		if (elseBlockManager.isCurrentBalanceEmpty()) {
			elseBlockManager.removeCurrentElseBlock();
		}
		
		if (elseBlockManager.getCurrentNestingLevel() == 0) {
			elseWithoutCurlyBrackets = false;
		}
		return line;
	}
	
	private boolean isElseKeyword(String line) {
		final Pattern patternElse = 
				Pattern.compile("[\\s\\t\\}]*else(\\ |\\t|\\}|$)+.*");
		
		return !line.contains("if") && patternElse.matcher(line).find();
	}
	
	
	//-------------------------------------------------------------------------
	//		Inner classes
	//-------------------------------------------------------------------------			
	/**
	 * Responsible for managing else blocks according to its nesting levels. It
	 * is used only for else blocks without curly brackets, because its 
	 * usefulness is add curly brackets in these blocks.
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
