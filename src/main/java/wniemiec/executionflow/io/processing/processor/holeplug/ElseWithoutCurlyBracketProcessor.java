package wniemiec.executionflow.io.processing.processor.holeplug;

import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

import wniemiec.executionflow.io.processing.processor.SourceCodeProcessor;
import wniemiec.util.io.parser.balance.CurlyBracketBalance;

/**
 * Adds curly brackets in else blocks that does not have curly brackets.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		6.0.0
 * @since 		6.0.0
 */
public class ElseWithoutCurlyBracketProcessor extends SourceCodeProcessor {

	//---------------------------------------------------------------------
	//		Attributes
	//---------------------------------------------------------------------
	private ElseBlockManager elseBlockManager = new ElseBlockManager();
	
	private boolean inElseWithoutCurlyBrackets;
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
	
	private String processLine(String line, String nextLine) {
		String processedLine = line;
		
		if (removeClosedCurlyBracketFromNextLine) {
			removeClosedCurlyBracketFromNextLine = false;
			processedLine = line.substring(line.indexOf("}") + 1);
		}

		if (inlineCommand)
			return processInlineCommand(processedLine);	

		if (hasClosingCurlyBracketNextToIf(nextLine))
			processedLine = moveClosedCurlyBracketToCurrentLine(processedLine);

		if (inElseWithoutCurlyBrackets)
			processedLine = processElseWithoutCurlyBracket(processedLine, nextLine);

		processedLine = checkElseBlocks(processedLine);

		if (isElseKeyword(processedLine))
			parseElse(processedLine, nextLine);
		
		return processedLine;
	}
	
	private String processInlineCommand(String line) {
		inlineCommand = false;
		
		elseBlockManager.decreaseBalance();
		
		if (elseBlockManager.isCurrentBalanceEmpty())
			elseBlockManager.removeCurrentElseBlock();
		
		if (elseBlockManager.getCurrentNestingLevel() == 0)
			inElseWithoutCurlyBrackets = false;
		
		return line + "}";
	}
	
	private boolean hasClosingCurlyBracketNextToIf(String nextLine) {
		final String regexClosedCurlyBracketNextToIfElse = "(\\t|\\ )*"
				+ "\\}(\\t|\\ )*(else if|else)(\\t|\\ )*(\\(|\\{).*";
		
		return nextLine.matches(regexClosedCurlyBracketNextToIfElse);
	}

	private String moveClosedCurlyBracketToCurrentLine(String line) {
		String processedLine = line;
		
		if (line.contains("//")) {
			int idxStartComment = line.indexOf("//");
			
			processedLine = line.substring(0, idxStartComment) 
					+ "}" 
					+ line.substring(idxStartComment);
		}
		else {
			processedLine += "}";
		}
		
		removeClosedCurlyBracketFromNextLine = true;
		
		return processedLine;
	}
	
	private String processElseWithoutCurlyBracket(String line, String nextLine) {
		elseBlockManager.parse(line);
		
		if (!isInsideElseWithoutCurlyBrackets())
			return line;
		
		String processedLine = line;
		
		if (hasCatchKeyword(line)) {
			if (line.contains("{") && !line.contains("}")) {
				elseBlockManager.incrementBalance();
			} 
			else if (line.contains("{") && line.contains("}")) {
				processedLine += "}";
				elseBlockManager.removeCurrentElseBlock();
				
				if (elseBlockManager.getCurrentNestingLevel() == 0)
					inElseWithoutCurlyBrackets = false;
			}
		} 
		else if (!hasCatchKeyword(nextLine)) {	
			processedLine += "}";
			elseBlockManager.removeCurrentElseBlock();
			
			if (elseBlockManager.getCurrentNestingLevel() == 0)
				inElseWithoutCurlyBrackets = false;
		}
			
		return processedLine;
	}
	
	private boolean isInsideElseWithoutCurlyBrackets() {
		return !elseBlockManager.isCurrentBalanceEmpty();
	}
	
	private boolean hasCatchKeyword(String line) {
		final String regexCatchKeyword = "[\\s\\t\\}]*"
				+ "catch(\\ |\\t)*\\(.*\\)(\\ |\\t)*";
		
		return line.matches(regexCatchKeyword);
	}
	
	private void parseElse(String line, String nextLine) {
		if (isInlineElseWithMoreThanOneLine(line))
			inElseWithoutCurlyBrackets = true;
		
		if (inElseWithoutCurlyBrackets) {
			elseBlockManager.createNewElseBlock();
			elseBlockManager.parse(line);
			
			if (!nextLine.contains("{") && isInlineCommand(nextLine))
				inlineCommand = true;
		}
	}
	
	private boolean isInlineCommand(String line) {
		return line.matches(".+;$");
	}
	
	private boolean isInlineElseWithMoreThanOneLine(String line) {
		String afterElse = line.substring(line.indexOf("else") + 4);
		
		return	afterElse.isEmpty() 
				|| afterElse.matches("^(\\s|\\t)+$");
	}

	private String checkElseBlocks(String line) {
		StringBuilder processedLine = new StringBuilder(line);
		
		while (areThereAnyElseBlockThatReachedAtTheEnd()) {
			processedLine.append("}");
			elseBlockManager.removeCurrentElseBlock();
		}

		if (elseBlockManager.getCurrentNestingLevel() == 0)
			inElseWithoutCurlyBrackets = false;
		
		return processedLine.toString();
	}

	private boolean areThereAnyElseBlockThatReachedAtTheEnd() {
		return	elseBlockManager.hasBalanceAlreadyPassedTwo()
				&& (elseBlockManager.getCurrentBalance() == 1) 
				&& (elseBlockManager.getCurrentNestingLevel() > 0);
	}
	
	private boolean isElseKeyword(String line) {
		final Pattern patternElse = Pattern.compile("[\\s\\t\\}]*"
				+ "else(\\ |\\t|\\}|$)+.*");
		
		return	!line.contains("if") 
				&& patternElse.matcher(line).find();
	}
	
	
	//-------------------------------------------------------------------------
	//		Inner classes
	//-------------------------------------------------------------------------			
	/**
	 * Responsible for managing else blocks according to its nesting levels. It
	 * is used only for else blocks without curly brackets, because its 
	 * usefulness is add curly brackets in these blocks.
	 */
	private class ElseBlockManager {
		
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
		private Stack<CurlyBracketBalance> elseBlocksBalance;
		
		/**
		 * Stores if at any time the curly bracket balance of each else block 
		 * nesting level was equal to 2.
		 */
		private Stack<Boolean> elseBlocksPassedTwo;
		
		
		//---------------------------------------------------------------------
		//		Constructor
		//---------------------------------------------------------------------
		public ElseBlockManager() {
			elseBlocksBalance = new Stack<>();
			elseBlocksPassedTwo = new Stack<>();
		}
		
		
		//---------------------------------------------------------------------
		//		Methods
		//---------------------------------------------------------------------
		/**
		 * Updates curly bracket balance of current nested level parsing a line.
		 * 
		 * @param		line Line with curly brackets
		 */
		public void parse(String line) {
			elseBlocksBalance.peek().parse(line);
		}
		
		/**
		 * Creates a new else block, storing it at the 
		 * {@link #elseBlocksBalance else blocks stack}. It will also 
		 * increment {@link #currentNestingLevel current nesting level}.
		 */
		public void createNewElseBlock() {
			currentNestingLevel++;
			elseBlocksBalance.push(new CurlyBracketBalance());
			elseBlocksPassedTwo.push(false);
		}
		
		/**
		 * Removes else block of {@link #currentNestingLevel current nesting level} 
		 * from the {@link #elseBlocksBalance else blocks stack}. If stack is 
		 * empty, do not do anything.
		 */
		public void removeCurrentElseBlock() {
			if (elseBlocksBalance.size() <= 0)
				return;
			
			elseBlocksBalance.pop();
			elseBlocksPassedTwo.pop();
			currentNestingLevel--;
		}
		
		/**
		 * Gets {@link #currentNestingLevel current nesting level}.
		 * 
		 * @return		Current nesting level
		 */
		public int getCurrentNestingLevel() {
			return currentNestingLevel;
		}
		
		/**
		 * Increments balance of else block associated with 
		 * {@link #currentNestingLevel current nesting level}. If nesting level
		 * is zero, do not do anything.
		 * 
		 * @apiNote		Should be called only when an open curly bracket is found
		 */
		public void incrementBalance() {
			if (currentNestingLevel == 0)
				return;
			
			elseBlocksBalance.peek().increaseBalance();
			
			if (elseBlocksBalance.peek().getBalance() >= 2) {
				elseBlocksPassedTwo.pop();
				elseBlocksPassedTwo.push(true);
			}
		}
		
		/**
		 * Decrements balance of else block associated with 
		 * {@link #currentNestingLevel current nesting level}. If nesting level
		 * is zero, do not do anything.
		 * 
		 * @apiNote		Should be called only when a closed curly bracket is found
		 */
		public void decreaseBalance() {
			if (currentNestingLevel == 0)
				return;
			
			elseBlocksBalance.peek().decreaseBalance();
		}
		
		/**
		 * Returns balance of else block associated with 
		 * {@link #currentNestingLevel current nesting level}. If nesting level
		 * is zero, do not do anything.
		 * 
		 * @return		Current balance or -1 if nesting level is zero
		 */
		public int getCurrentBalance() {
			if (currentNestingLevel == 0)
				return -1;
			
			return elseBlocksBalance.peek().getBalance();
		}
		
		/**
		 * Checks if balance of else block associated with 
		 * {@link #currentNestingLevel current nesting level} is empty.
		 * 
		 * @return		If balance is zero
		 */
		public boolean isCurrentBalanceEmpty() {
			if (currentNestingLevel == 0)
				return true;
			
			return elseBlocksBalance.peek().isBalanceEmpty();
		}
		
		/**
		 * Checks if at any time the balance of else block associated with 
		 * {@link #currentNestingLevel current nesting level} was equal to 2.
		 * 
		 * @return		If at any time the balance was equal to 2
		 */
		private boolean hasBalanceAlreadyPassedTwo() {
			if (currentNestingLevel == 0)
				return false;
			
			return elseBlocksPassedTwo.peek();
		}
	}
}
