package executionFlow.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Parses java file adding instructions in places in the code that do not exist 
 * when converting it to bytecode.
 */
public class FileParser 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private File file;
	private static final String VAR_NAME;
	private boolean alreadyDeclared;
	private File outputDir;
	private String outputFilename;
	boolean elseNoCurlyBrackets;
	boolean skipNextLine;
	boolean inComment;

	private static final String regex_varDeclarationWithoutInitialization = "( |\\t)*(final(\\s|\\t)+)?[A-z0-9\\-_$]+(\\s|\\t)[A-z0-9\\-_$]+(((,)[A-z0-9\\-_$]+)?)+;";
	private static final String regex_for = "(\\ |\\t|\\})+for(\\ |\\t)*\\(.*\\)(\\ |\\t)*";
	private static final String regex_while = "(\\ |\\t|\\})+while(\\ |\\t)*\\(.*\\)(\\ |\\t)*";
	private static final String regex_catch = "(\\ |\\t|\\})+catch(\\ |\\t)*\\(.*\\)(\\ |\\t)*";
	private static final String regex_try = "(\\ |\\t|\\})+try(\\ |\\t)*";
	private static final String regex_new = "(\\ |\\t)+new(\\ |\\t)*";
	private static final Pattern pattern_tryFinally = Pattern.compile("(\\t|\\ |\\})+(try|finally)[\\s\\{]");
	private static final Pattern pattern_else = Pattern.compile("(\\ |\\t|\\})+else(\\ |\\t|\\}|$)+.*");
	private static final Pattern pattern_do = Pattern.compile("(\\t|\\ |\\})+do[\\s\\{]");
	private static final Pattern pattern_switch = Pattern.compile("(\\t|\\ |\\})+case");
	private static final Pattern pattern_methodDeclaration = Pattern.compile("(\\ |\\t)*([A-z0-9\\-_$<>\\[\\]\\ \\t]+(\\s|\\t))+[A-z0-9\\-_$]+\\(([A-z0-9\\-_$,<>\\[\\]\\ \\t])*\\)(\\{|(\\s\\{)||\\/)*");
	
	/**
	 * If true, displays processed lines.
	 */
	private static final boolean DEBUG;
	
	
	//-------------------------------------------------------------------------
	//		Initialization blocks
	//-------------------------------------------------------------------------
	/**
	 * Generates variable name. It will be current time encrypted in MD5 to
	 * avoid conflict with variables already declared.
	 */
	static {
		Date now = new Date();
		VAR_NAME = "_"+md5(String.valueOf(now.getTime()));
	}
	
	/**
	 * Configures environment. If {@link DEBUG} is true, displays processed lines.
	 */
	static {
		DEBUG = false;
	}
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	/**
	 * Adds instructions in places in the code that do not exist when converting
	 * it to bytecode.
	 * 
	 * @param filename Path of the file to be parsed
	 * @param outputDir Directory where parsed file will be saved
	 * @param outputFilename Name of the parsed file
	 */ 
	public FileParser(String filepath, String outputDir, String outputFilename)
	{
		this.file = new File(filepath);
		
		if (outputDir != null)
			this.outputDir = new File(outputDir);
		
		this.outputFilename = outputFilename;
	}
	
	/**
	 * Adds instructions in places in the code that do not exist when converting
	 * it to bytecode.
	 * 
	 * @param filename Path of the file to be parsed
	 * @param outputFilename Name of the parsed file
	 */ 
	public FileParser(String filepath, String outputFilename)
	{
		this(filepath, null, outputFilename);
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Parses file adding instructions in places in the code that do not exist 
	 * when converting it to bytecode.
	 */
	public String parseFile()
	{
		if (file == null) { return ""; }

		String parsedLine = null;
		String line, nextLine;
		File outputFile;
		boolean inLoop = false;
		boolean inMethod = false;
		ElseBlockManager elseBlockManager = new ElseBlockManager();
		
		
		// If an output directory is specified, processed file will be saved to it
		if (outputDir != null)
			outputFile = new File(outputDir, outputFilename+".java");
		else	// Else processed file will be saved in current directory
			outputFile = new File(outputFilename+".java");
		
		// Opens file streams (file to be parsed and output file / processed file)
		try (BufferedReader br = new BufferedReader(new FileReader(file));
			 BufferedReader br_forward = new BufferedReader(new FileReader(file));
			 BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
			br_forward.readLine();
			
			// Parses file line by line
			while ((line = br.readLine()) != null) {
				nextLine = br_forward.readLine();
				
				if (isComment(line)) {
					bw.write(line);
					bw.newLine();
					continue;
				}
				
				if (inMethod) {
					if (line.contains("{")) {
						inMethod = false;
					}
					
					bw.write(line);
					bw.newLine();
					continue;
				}
				
				if (nextLine == null)
					nextLine = "";
				
				if (skipNextLine) {
					skipNextLine = false;
					bw.newLine();	// It is necessary to keep line numbers equals to original file 
					
					if (DEBUG)
						System.out.println();
					
					continue;
				}
				
				// Checks if it is a method declaration
				if (!line.matches(regex_new) && pattern_methodDeclaration.matcher(line).find()) {
					alreadyDeclared = false;
					bw.write(line);
					bw.newLine();
					
					if (DEBUG)
						System.out.println(line);
					
					inMethod = true;
					continue;
				}
				
				// Checks if parser is in else block without curly brackets
				if (elseNoCurlyBrackets) {
					int amountOpenCurlyBrackets = countOpenCurlyBrackets(line);
					int amountClosedCurlyBrackets = countClosedCurlyBrackets(line);
					
					// Updates curly brackets balance
					if (amountOpenCurlyBrackets > 0) {
						for (int i=0; i<amountOpenCurlyBrackets; i++) {
							elseBlockManager.incrementBalance();
						}
					}

					if (amountClosedCurlyBrackets > 0) {
						for (int i=0; i<amountClosedCurlyBrackets; i++) {
							elseBlockManager.decreaseBalance();
						}
					}
					
					// Checks if else block balance is empty
					if (elseBlockManager.isCurrentBalanceEmpty()) {
						if (line.matches(regex_catch)) {
							if (line.contains("{") && !line.contains("}")) {
								elseBlockManager.incrementBalance();
							} else if (line.contains("{") && line.contains("}")) {
								line += "}";
								elseBlockManager.removeCurrentElseBlock();
								
								if (elseBlockManager.getCurrentNestingLevel() == 0)
									elseNoCurlyBrackets = false;
							}
						} else if (!nextLine.matches(regex_catch)){
							line += "}";
							elseBlockManager.removeCurrentElseBlock();
							
							if (elseBlockManager.getCurrentNestingLevel() == 0)
								elseNoCurlyBrackets = false;
						 
							if (line.matches(regex_for) || line.matches(regex_while)) {	
								inLoop = true;
							} 
							
							if	( inLoop && !nextLine.matches(regex_for) && 
								  !nextLine.matches(regex_while) && 
								  !nextLine.matches(regex_try) ) {
								inLoop = false;
							}
						}
					}
				}
				
				while (	elseBlockManager.getCurrentNestingLevel() > 0 && 
						elseBlockManager.getCurrentBalance() == 1 && 
						elseBlockManager.hasBalanceAlreadyPassedTwo()) {
					line += "}";
					elseBlockManager.removeCurrentElseBlock();
				}
				
				if (elseBlockManager.getCurrentNestingLevel() == 0) {
					elseNoCurlyBrackets = false;
				}
				
				// Analyzes code
				if (pattern_tryFinally.matcher(line).find() && pattern_tryFinally.matcher(line).find()) {	// Try or finally
					line = checkCurlyBracketNewLine(line, nextLine);
					parsedLine = parse_try_finally(line);
				} else if (!line.contains("if") && pattern_else.matcher(line).find()) {		// Else
					line = checkCurlyBracketNewLine(line, nextLine);
					parsedLine = parse_else(line);
					
					// Checks if parsed else is an else without curly brackets
					if (elseNoCurlyBrackets) {
						elseBlockManager.createNewElseBlock();
						
						if (!nextLine.contains("{")) {	// If there are not curly brackets in else nor next line
							// Checks if it is an one line command
							if (nextLine.matches(".+;$")) { // One line command
								bw.write(parsedLine);
								bw.newLine();
								
								if (DEBUG)
									System.out.println(parsedLine);
								
								nextLine = br_forward.readLine();
								line = br.readLine();
								parsedLine = line +"}";
								elseBlockManager.decreaseBalance();
								
								if (elseBlockManager.isCurrentBalanceEmpty()) {
									elseBlockManager.removeCurrentElseBlock();
								}
								
								if (elseBlockManager.getCurrentNestingLevel() == 0) {
									elseNoCurlyBrackets = false;
								}
							}
						}
					}
					
				} else if (pattern_do.matcher(line).find()) {								// Do while
					line = checkCurlyBracketNewLine(line, nextLine);
					parsedLine = parse_do(line);
				}  else if (pattern_switch.matcher(line).find()) {							// Switch
					line = checkCurlyBracketNewLine(line, nextLine);
					parsedLine = parse_switch(line);
				} else if (	!line.contains("return ") && !line.contains("return(") && 		// Var declaration
						!line.contains("package ") && !line.contains("class ") && 
						line.matches(regex_varDeclarationWithoutInitialization)) {
				parsedLine = parse_varDeclaration(line);
				} else {
					parsedLine = line;
				}
				
				if (DEBUG)
					System.out.println(parsedLine);
					
				bw.write(parsedLine);
				bw.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return outputFile.getAbsolutePath();
	}
	
	private String parse_do(String line)
	{
		StringBuilder sb = new StringBuilder();

		// Checks if block has curly brackets
		if (line.contains("{")) {
			int curlyBracketsIndex = line.indexOf('{');
			sb.append(line.substring(0, curlyBracketsIndex+1));
			
			if (alreadyDeclared)
				sb.append(VAR_NAME+"=0;");
			else {
				sb.append("int "+VAR_NAME+"=0;");
				alreadyDeclared = true;
			}
			
			sb.append(line.substring(curlyBracketsIndex+1));
		} else {
			throw new IllegalStateException("Code block must be enclosed in curly brackets");
		}
		
		return sb.toString();
	}
	
	private String parse_else(String line)
	{
		StringBuilder sb = new StringBuilder();
		
		// Checks if block has curly brackets
		if (line.contains("{")) {
			int curlyBracketsIndex = line.indexOf('{');
			sb.append(line.substring(0, curlyBracketsIndex+1));
			
			if (alreadyDeclared)
				sb.append(VAR_NAME+"=0;");
			else {
				sb.append("int "+VAR_NAME+"=0;");
				alreadyDeclared = true;
			}
			
			sb.append(line.substring(curlyBracketsIndex+1));
		} else {
			int indexAfterElse = line.indexOf("else")+4; 
			sb.append(line.substring(0, indexAfterElse));
			
			if (alreadyDeclared)
				sb.append(" {"+VAR_NAME+"=0;");
			else {
				sb.append(" {"+"int "+VAR_NAME+"=0;");
				alreadyDeclared = true;
			}
			
			String afterElse = line.substring(indexAfterElse);
			
			if (!afterElse.isEmpty() && !afterElse.matches("^(\\s|\\t)+$")) {	// Command in same line
				sb.append(afterElse);
				sb.append("}");
			} else {
				elseNoCurlyBrackets = true;
			}
		}
		
		return sb.toString();
	}
	
	private String parse_try_finally(String line)
	{
		StringBuilder sb = new StringBuilder();

		Matcher m = pattern_tryFinally.matcher(line);
		m.find();

		if (line.contains("{")) {
			int curlyBracketsIndex = line.indexOf('{');
			sb.append(line.substring(0, curlyBracketsIndex+1));
			
			if (alreadyDeclared)
				sb.append(VAR_NAME+"=0;");
			else {
				sb.append("int "+VAR_NAME+"=0;");
				alreadyDeclared = true;
			}
			
			sb.append(line.substring(curlyBracketsIndex+1));
		} else {
			throw new IllegalStateException("Code block must be enclosed in curly brackets");
		}

		return sb.toString();
	}
	
	private String parse_varDeclaration(String line)
	{
		if (alreadyDeclared)
			return line+VAR_NAME+"=0;";
		else {
			alreadyDeclared = true;
			return line+"int "+VAR_NAME+"=0;";
		}
	}
	
	private String parse_switch(String line)
	{
		StringBuilder sb = new StringBuilder();
		Pattern p = Pattern.compile(":");
		Matcher m = p.matcher(line);
		m.find();
		sb.append(line.substring(0, m.start()+1));
		
		if (alreadyDeclared)
			sb.append(VAR_NAME+"=0;");
		else {
			sb.append("int "+VAR_NAME+"=0;");
			alreadyDeclared = true;
		}
		
		sb.append(line.substring(m.start()+1));

		return sb.toString();
	}
	
	/**
	 * Checks if open curly bracket is in next line. If it is, moves it to
	 * the end of current line.
	 * 
	 * @param line Current line
	 * @param nextLine Line following the current line
	 * @return Current line with open curly bracket at the end
	 */
	private String checkCurlyBracketNewLine(String line, String nextLine)
	{
		final String regex_onlyOpenCurlyBracket = "^(\\s|\\t)+\\{(\\s|\\t|\\/)*$";
		
		if (nextLine.matches(regex_onlyOpenCurlyBracket)) {
			line = line + " {";
			skipNextLine = true;
		}
		
		return line;
	}
	
	private boolean isComment(String line)
	{
		boolean response = false;
		
		if (inComment) {
			if (line.contains("*/"))
				inComment = false;
			
			response = true;
		} else if (line.contains("/*") && !line.contains("*/")) {
			inComment = true;
			
			response = true;
		} else if (line.contains("//") || (line.contains("/*") && line.contains("*/"))) {
			response = true;
		}
		
		return response;
	}
	
	private int countOpenCurlyBrackets(String line)
	{
		final Pattern pattern_openCurlyBrackets = Pattern.compile("\\{");
		Matcher openCBMatcher = pattern_openCurlyBrackets.matcher(line);
		int size;
		for (size = 0; openCBMatcher.find(); size++);
		
		return size;
	}
	
	private int countClosedCurlyBrackets(String line)
	{
		final Pattern pattern_closedCurlyBrackets = Pattern.compile("\\}");
		Matcher openCBMatcher = pattern_closedCurlyBrackets.matcher(line);
		
		int size;
		for (size = 0; openCBMatcher.find(); size++);
		
		return size;
	}
	
	private static String md5(String text)
	{
		String response;
		
		try {
			MessageDigest m = MessageDigest.getInstance("MD5");
			m.update(text.getBytes(),0,text.length());
			response = new BigInteger(1,m.digest()).toString(16);
		} catch (NoSuchAlgorithmException e) {
			response = text;
		}
		
		return response;
	}
	
	
	//-------------------------------------------------------------------------
	//		Inner classes
	//-------------------------------------------------------------------------
	/**
	 * Responsible for managing an else block. It is used only for else blocks
	 * without curly brackets, because its usefulness is add curly brackets in
	 * these blocks.
	 */
	class ElseBlock
	{
		//---------------------------------------------------------------------
		//		Attributes
		//---------------------------------------------------------------------
		/**
		 * Balance of curly brackets. It is always positive or zero.
		 */
		private int curlyBracketsBalance;
		
		/**
		 * Flag used to control if balance at any time was equal to 2.
		 */
		private boolean balancePassedTwo;
		
		
		//---------------------------------------------------------------------
		//		Methods
		//---------------------------------------------------------------------
		/**
		 * Increments balance.
		 * 
		 * @apiNote Must be called only when an open curly bracket is found
		 */
		public void increaseBalance()
		{
			curlyBracketsBalance += 1;
			
			if (curlyBracketsBalance >= 2) {
				balancePassedTwo = true;
			}
		}
		
		/**
		 * Decrements balance.
		 * 
		 * @apiNote Must be called only when a closed curly bracket is found
		 */
		public void decreaseBalance()
		{
			curlyBracketsBalance -= 1;
		}
		
		/**
		 * Returns balance.
		 * 
		 * @return Current balance
		 */
		public int getBalance()
		{
			return curlyBracketsBalance;
		}
		
		/**
		 * Checks if balance is empty
		 * 
		 * @return If balance is zero
		 */
		public boolean isBalanceEmpty()
		{
			return curlyBracketsBalance == 0;
		}
		
		/**
		 * Checks if at any time the balance was equal to 2.
		 * 
		 * @return If at any time the balance was equal to 2
		 */
		public boolean hasBalanceAlreadyPassedTwo()
		{
			return balancePassedTwo;
		}
	}
	
	/**
	 * Manages else blocks according to its nesting levels.
	 */
	class ElseBlockManager
	{
		//---------------------------------------------------------------------
		//		Attributes
		//---------------------------------------------------------------------
		/**
		 * Stores current nesting level of else block.
		 */
		private int currentNestingLevel;
		
		/**
		 * Stores else block for each nesting level.
		 */
		private Stack<ElseBlock> elseBlocks;
		
		
		//---------------------------------------------------------------------
		//		Initialization block
		//---------------------------------------------------------------------
		{
			elseBlocks = new Stack<>();
		}
		
		
		//---------------------------------------------------------------------
		//		Methods
		//---------------------------------------------------------------------
		/**
		 * Creates a new else block, storing it at the 
		 * {@link #elseBlocks else blocks stack}. It will also increment 
		 * {@link #currentNestingLevel current nesting level}.
		 */
		public void createNewElseBlock()
		{
			currentNestingLevel++;
			elseBlocks.push(new ElseBlock());
		}
		
		/**
		 * Removes else block of {@link #currentNestingLevel current nesting level} 
		 * from the {@link #elseBlocks else blocks stack}. If stack is empty, do 
		 * not do anything.
		 */
		public void removeCurrentElseBlock()
		{
			if (elseBlocks.size() > 0) {
				elseBlocks.pop();
				currentNestingLevel--;
			}
		}
		
		/**
		 * Gets {@link #currentNestingLevel current nesting level}.
		 * 
		 * @return Current nesting level
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
		 * @apiNote Must be called only when an open curly bracket is found
		 */
		public void incrementBalance()
		{
			if (currentNestingLevel != 0)
				elseBlocks.peek().increaseBalance();
		}
		
		/**
		 * Decrements balance of else block associated with 
		 * {@link #currentNestingLevel current nesting level}. If nesting level
		 * is zero, do not do anything.
		 * 
		 * @apiNote Must be called only when a closed curly bracket is found
		 */
		public void decreaseBalance()
		{
			if (currentNestingLevel != 0)
				elseBlocks.peek().decreaseBalance();
		}
		
		/**
		 * Returns balance of else block associated with 
		 * {@link #currentNestingLevel current nesting level}. If nesting level
		 * is zero, do not do anything.
		 * 
		 * @return Current balance or -1 if nesting level is zero
		 */
		public int getCurrentBalance()
		{
			if (currentNestingLevel == 0) { return -1; }
			
			return elseBlocks.peek().getBalance();
		}
		
		/**
		 * Checks if balance of else block associated with 
		 * {@link #currentNestingLevel current nesting level} is empty.
		 * 
		 * @return If balance is zero
		 */
		public boolean isCurrentBalanceEmpty()
		{
			if (currentNestingLevel == 0) { return true; }
			
			return elseBlocks.peek().isBalanceEmpty();
		}
		
		/**
		 * Checks if at any time the balance of else block associated with 
		 * {@link #currentNestingLevel current nesting level} was equal to 2.
		 * 
		 * @return If at any time the balance was equal to 2
		 */
		private boolean hasBalanceAlreadyPassedTwo()
		{
			if (currentNestingLevel == 0) { return false; }
			
			return elseBlocks.peek().hasBalanceAlreadyPassedTwo();
		}
	}
}
