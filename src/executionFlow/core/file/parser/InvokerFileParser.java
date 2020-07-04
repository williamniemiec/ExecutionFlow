package executionFlow.core.file.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import executionFlow.ConsoleOutput;
import executionFlow.core.file.FileEncoding;
import executionFlow.util.DataUtils;


/**
 * Parses java file adding instructions in parts of the code that does not 
 * exist when converting it to bytecode.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		1.5
 * @since 		1.4
 */
public class InvokerFileParser extends FileParser
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final long serialVersionUID = 105L;
	
	/**
	 * If true, displays processed lines.
	 */
	private static final boolean DEBUG;
	
	private String fileExtension = "java";
	private boolean elseNoCurlyBrackets;
	private boolean skipNextLine;
	private boolean inComment;
	
	
	//-------------------------------------------------------------------------
	//		Initialization blocks
	//-------------------------------------------------------------------------
	/**
	 * Configures environment. If {@link #DEBUG} is true, displays processed 
	 * lines.
	 */
	static {
		DEBUG = false;
	}
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	/**
	 * Adds instructions in parts of the code that does not exist when 
	 * converting it to bytecode. Using this constructor, the directory where 
	 * parsed file will be saved will be in current directory. Also, file 
	 * encoding will be UTF-8.
	 * 
	 * @param		filename Path of the file to be parsed
	 * @param		outputFilename Name of the parsed file
	 */ 
	public InvokerFileParser(Path filepath, String outputFilename)
	{
		this(filepath, null, outputFilename);
	}
	
	/**
	 * Adds instructions in parts of the code that does not exist when 
	 * converting it to bytecode. Using this constructor, file encoding will be 
	 * UTF-8.
	 * 
	 * @param		filename Path of the file to be parsed
	 * @param		outputDir Directory where parsed file will be saved
	 * @param		outputFilename Name of the parsed file
	 */ 
	public InvokerFileParser(Path filepath, Path outputDir, String outputFilename)
	{
		this.file = filepath;
		this.outputDir = outputDir;
		this.outputFilename = outputFilename;
	}
	
	/**
	 * Adds instructions in parts of the code that does not exist when 
	 * converting it to bytecode. Using this constructor, file encoding will be 
	 * UTF-8.
	 * 
	 * @param		filename Path of the file to be parsed
	 * @param		outputDir Directory where parsed file will be saved
	 * @param		outputFilename Name of the parsed file
	 * @param		fileExtension Output file extension (without dot)
	 * (default is java)
	 */ 
	public InvokerFileParser(Path filepath, Path outputDir, String outputFilename, 
			String fileExtension)
	{
		this(filepath, outputDir, outputFilename);
		this.fileExtension = fileExtension;
	}
	
	/**
	 * Adds instructions in parts of the code that does not exist when 
	 * converting it to bytecode.
	 * 
	 * @param		filename Path of the file to be parsed
	 * @param		outputDir Directory where parsed file will be saved
	 * @param		outputFilename Name of the parsed file
	 * @param		encode File encoding
	 */ 
	public InvokerFileParser(Path filepath, Path outputDir, String outputFilename,
			FileEncoding encode)
	{
		this(filepath, outputDir, outputFilename);
		this.encode = encode;
	}
	
	/**
	 * Adds instructions in parts of the code that does not exist when 
	 * converting it to bytecode.
	 * 
	 * @param		filename Path of the file to be parsed
	 * @param		outputDir Directory where parsed file will be saved
	 * @param		outputFilename Name of the parsed file
	 * @param		encode File encoding
	 * @param		fileExtension Output file extension (without dot)
	 * (default is java)
	 */ 
	public InvokerFileParser(Path filepath, Path outputDir, String outputFilename,
			FileEncoding encode, String fileExtension)
	{
		this(filepath, outputDir, outputFilename, encode);
		this.fileExtension = fileExtension;
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Parses file adding instructions in parts of the code that does not exist 
	 * when converting it to bytecode.
	 * 
	 * @throws		IOException If file encoding is incorrect or if file cannot
	 * be read / written
	 */
	@Override
	public String parseFile() throws IOException
	{
		if (file == null) { return ""; }

		final String regex_varDeclarationWithoutInitialization = "( |\\t)*(final(\\s|\\t)+)?[A-z0-9\\-_$]+(\\s|\\t)[A-z0-9\\-_$]+(((,)[A-z0-9\\-_$]+)?)+;";
		final String regex_catch = "(\\ |\\t|\\})+catch(\\ |\\t)*\\(.*\\)(\\ |\\t)*";
		final String regex_new = "(\\ |\\t)+new(\\ |\\t)*";
		final String regex_continueBreak = "^(\\ |\\t)*(continue|break)(\\ |\\t)*;";
		final Pattern pattern_tryFinally = Pattern.compile("(\\t|\\ |\\})+(try|finally)[\\s\\{]");
		final Pattern pattern_else = Pattern.compile("(\\ |\\t|\\})+else(\\ |\\t|\\}|$)+.*");
		final Pattern pattern_do = Pattern.compile("(\\t|\\ |\\})+do[\\s\\{]");
		final Pattern pattern_switch = Pattern.compile("(\\t|\\ |\\})+(case|default)(\\t|\\ )+");
		//final Pattern pattern_invokerDeclaration = Pattern.compile("(\\ |\\t)*([A-z0-9\\-_$<>\\[\\]\\ \\t]+(\\s|\\t))+[A-z0-9\\-_$]+\\(([A-z0-9\\-_$,<>\\[\\]\\ \\t])*\\)(\\{|(\\s\\{)||\\/)*");
		final Pattern pattern_invokerDeclaration = Pattern.compile("(\\ |\\t)*([A-z0-9\\-_$<>\\[\\]\\ \\t]+(\\s|\\t))+[A-z0-9\\-_$]+(\\ |\\t)*\\(.*");
		String parsedLine = null;
		String line, nextLine;
		File outputFile;
		boolean withinInvoker = false;
		ElseBlockManager elseBlockManager = new ElseBlockManager();
		
		
		// If an output directory is specified, processed file will be saved to it
		if (outputDir != null)
			outputFile = new File(outputDir.toFile(), outputFilename + "." + fileExtension);
		// Otherwise processed file will be saved in current directory
		else	
			outputFile = new File(outputFilename + "." + fileExtension);
		
		// Opens file streams (file to be parsed and output file / processed file)
		try (BufferedReader br = Files.newBufferedReader(file, encode.getStandardCharset());
			 BufferedReader br_forward = Files.newBufferedReader(file, encode.getStandardCharset()); 
			 BufferedWriter bw = Files.newBufferedWriter(outputFile.toPath(), encode.getStandardCharset())) { 
			br_forward.readLine();
			
			// Parses file line by line
			while ((line = br.readLine()) != null) {
				nextLine = br_forward.readLine();
				
				// Checks if it is a comment line
				if (isComment(line)) {
					
					// -----{ DEBUG }-----
					if (DEBUG) { ConsoleOutput.showDebug(line); }
					// -----{ END DEBUG }-----
					
					bw.write(line);
					bw.newLine();
					continue;
				}
				
				// Checks if there are print's
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
				}
				
				// Checks if it is an invoker declaration line
				if (withinInvoker) {
					if (line.contains("{")) {
						withinInvoker = false;
					}
					
					// -----{ DEBUG }-----
					if (DEBUG) { ConsoleOutput.showDebug(line); }
					// -----{ END DEBUG }-----
					
					bw.write(line);
					bw.newLine();
					continue;
				}
				
				if (nextLine == null)
					nextLine = "";
				
				// Checks if current line has to be skipped
				if (skipNextLine) {
					skipNextLine = false;
					bw.newLine();	// It is necessary to keep line numbers equals to original file 
					
					// -----{ DEBUG }-----
					if (DEBUG) { ConsoleOutput.showDebug(""); }
					// -----{ END DEBUG }-----
					
					continue;
				}
				
				// Checks if it is an invoker declaration
				if (!line.matches(regex_new) && line.matches(pattern_invokerDeclaration.toString())) {
					if (isMethodDeclaration(line)) {
						line = "@executionFlow.runtime.CollectInvokedMethods " + line;
						
						bw.write(line);
						bw.newLine();
						
						// -----{ DEBUG }-----
						if (DEBUG) { ConsoleOutput.showDebug(line); }
						// -----{ END DEBUG }-----
						
						withinInvoker = !line.contains("{");
						continue;
					}
				}
				
				// Checks if parser is within a else block without curly brackets
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
						// Checks if it is a line with 'catch' keyword
						if (line.matches(regex_catch)) {
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
						else if (!nextLine.matches(regex_catch)) {	// Checks if next line does not have 'catch' keyword
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
				
				// Parses code
				// Try or finally
				if (pattern_tryFinally.matcher(line).find() && pattern_tryFinally.matcher(line).find()) {	
					line = checkCurlyBracketNewLine(line, nextLine);
					parsedLine = parse_try_finally(line);
				} 
				// Continue or Break
				else if (line.matches(regex_continueBreak)) {	
					line = checkCurlyBracketNewLine(line, nextLine);
					parsedLine = parse_continue_break(line);
				}
				// Else
				else if (!line.contains("if") && pattern_else.matcher(line).find()) {
					line = checkCurlyBracketNewLine(line, nextLine);
					parsedLine = parse_else(line);
					
					// Checks if parsed else is an else without curly brackets
					if (elseNoCurlyBrackets) {
						elseBlockManager.createNewElseBlock();
						
						if (!nextLine.contains("{")) {	// Checks if there are not curly brackets in else nor next line
							// Checks if it is an one line command
							if (nextLine.matches(".+;$")) { // One line command
								bw.write(parsedLine);
								bw.newLine();
								
								// -----{ DEBUG }-----
								if (DEBUG) { ConsoleOutput.showDebug(parsedLine); }
								// -----{ END DEBUG }-----
								
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
				}
				// Do while
				else if (pattern_do.matcher(line).find()) {								
					line = checkCurlyBracketNewLine(line, nextLine);
					parsedLine = parse_do(line);
				}  
				// Switch
				else if (pattern_switch.matcher(line).find()) {							
					line = checkCurlyBracketNewLine(line, nextLine);
					parsedLine = parse_switch(line);
				}
				// Variable declaration without initialization
				else if (!line.contains("return ") && !line.contains("return(") && 		
						!line.contains("package ") && !line.contains("class ") && 
						line.matches(regex_varDeclarationWithoutInitialization)) {
				parsedLine = parse_varDeclaration(line);
				}
				else {
					parsedLine = line;
				}
				
				// -----{ DEBUG }-----
				if (DEBUG) { ConsoleOutput.showDebug(parsedLine); }
				// -----{ END DEBUG }-----	
				
				bw.write(parsedLine);
				bw.newLine();
			}
		}

		return outputFile.getAbsolutePath();
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
			response.append("int "+DataUtils.generateVarName()+"=0;");
			
			// Appends in response everything after '{' 
			response.append(line.substring(curlyBracketsIndex+1));
		} 
		else {
			throw new IllegalStateException("Code block must be enclosed in curly brackets");
		}
		
		return response.toString();
	}
	
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
	private String parse_continue_break(String line)
	{
		return "if (Boolean.parseBoolean(\"True\")) {"+line+"}";
	}
	
	/**
	 * Parses line with 'else' keyword.
	 * 
	 * @param		Line with else keyword
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
			sb.append("int "+DataUtils.generateVarName()+"=0;");
			
			// Appends in response everything after '{' 
			sb.append(line.substring(curlyBracketsIndex+1));
		} 
		else {	// Else code block without curly brackets
			int indexAfterElse = line.indexOf("else")+4; 
			
			// Appends in response everything before 'else' keyword (including it) 
			sb.append(line.substring(0, indexAfterElse));
			
			// Appends in response variable assignment command
			sb.append(" {"+"int "+DataUtils.generateVarName()+"=0;");
			
			String afterElse = line.substring(indexAfterElse);
			
			// Checks if there is a command after 'else' keyword
			if (!afterElse.isEmpty() && !afterElse.matches("^(\\s|\\t)+$")) {
				sb.append(afterElse);	// If there is one, it its an in line else code block
				sb.append("}");			// Appends in response this command and a closed curly bracket
			} 
			else {
				elseNoCurlyBrackets = true;	// Else it is a else code block with more than one line
			}
		}
		
		return sb.toString();
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
			response.append("int "+DataUtils.generateVarName()+"=0;");

			// Appends in response everything after '{'
			response.append(line.substring(curlyBracketsIndex+1));
		} 
		else {
			throw new IllegalStateException("Code block must be enclosed in curly brackets");
		}

		return response.toString();
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
		return line+"int "+DataUtils.generateVarName()+"=0;";
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
		response.append("int "+DataUtils.generateVarName()+"=0;");
		
		// Appends in response everything after ':'
		response.append(line.substring(m.start()+1));

		return response.toString();
	}
	
	/**
	 * Checks if open curly bracket is in next line. If it is, moves it to
	 * the end of current line.
	 * 
	 * @param		line Current line
	 * @param		nextLine Line following the current line
	 * 
	 * @return		Current line with open curly bracket at the end
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
	
	/**
	 * Checks if a line is a comment line.
	 * 
	 * @param		line Line to be analyzed
	 * 
	 * @return		If line is a comment line
	 */
	private boolean isComment(String line)
	{
		boolean response = false;
		
		// Checks if parser is in a comment block
		if (inComment) {
			if (line.contains("*/"))
				inComment = false;
			
			response = true;
		} 
		else if (line.contains("/*") && !line.contains("*/")) {
			inComment = true;	// Parser is in a comment block
			
			response = true;
		} 
		else if (line.contains("//") || (line.contains("/*") && line.contains("*/"))) {
			response = true;
		}
		
		return response;
	}
	
	/**
	 * Checks whether a line contains an invoker declaration.
	 * 
	 * @param		line Line to be analyzed
	 * @return		If the line contains an invoker declaration
	 */
	private boolean isMethodDeclaration(String line)
	{
				// Checks if it is an invoker whose parameters are all on the same line
		return	!line.contains("return ") && !line.contains(" new ") && 
				line.matches("(\\ |\\t)*([A-z0-9\\-_$<>\\[\\]\\ \\t]+(\\s|\\t))"
					+ "+[A-z0-9\\-_$]+\\(([A-z0-9\\-_$,<>\\[\\]\\ \\t])*\\)(\\{|(\\s\\{)||\\/)*") || 
				// Checks if it is an invoker whose parameters are broken on other lines
				line.matches("(\\ |\\t)*([A-z0-9\\-_$<>\\[\\]\\ \\t?]+(\\s|\\t))+"
						+ "[A-z0-9\\-_$]+(\\ |\\t)*\\(.*,(\\ |\\t)*");
	}
	
	/**
	 * Counts how many open curly brackets are in a text.
	 * 
	 * @param		text Text to be analyzed
	 * 
	 * @return		Amount of open curly brackets in the text
	 */
	private int countOpenCurlyBrackets(String text)
	{
		final Pattern pattern_openCurlyBrackets = Pattern.compile("\\{");
		Matcher openCBMatcher = pattern_openCurlyBrackets.matcher(text);
		
		int size;
		for (size = 0; openCBMatcher.find(); size++);
		
		return size;
	}
	
	/**
	 * Counts how many closed curly brackets are in a text.
	 * 
	 * @param		text Text to be analyzed
	 * 
	 * @return		Amount of closed curly brackets in the text
	 */
	private int countClosedCurlyBrackets(String text)
	{
		final Pattern pattern_closedCurlyBrackets = Pattern.compile("\\}");
		Matcher openCBMatcher = pattern_closedCurlyBrackets.matcher(text);
		
		int size;
		for (size = 0; openCBMatcher.find(); size++);
		
		return size;
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
		 * @apiNote		Must be called only when an open curly bracket is found
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
		 * @apiNote		Must be called only when a closed curly bracket is found
		 */
		public void decreaseBalance()
		{
			curlyBracketsBalance -= 1;
		}
		
		/**
		 * Returns balance.
		 * 
		 * @return		Current balance
		 */
		public int getBalance()
		{
			return curlyBracketsBalance;
		}
		
		/**
		 * Checks if balance is empty
		 * 
		 * @return		If balance is zero
		 */
		public boolean isBalanceEmpty()
		{
			return curlyBracketsBalance == 0;
		}
		
		/**
		 * Checks if at any time the balance was equal to 2.
		 * 
		 * @return		If at any time the balance was equal to 2
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
		 * @apiNote		Must be called only when an open curly bracket is found
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
		 * @apiNote		Must be called only when a closed curly bracket is found
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
		 * @return		Current balance or -1 if nesting level is zero
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
		 * @return		If balance is zero
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
		 * @return		If at any time the balance was equal to 2
		 */
		private boolean hasBalanceAlreadyPassedTwo()
		{
			if (currentNestingLevel == 0) { return false; }
			
			return elseBlocks.peek().hasBalanceAlreadyPassedTwo();
		}
	}
}
