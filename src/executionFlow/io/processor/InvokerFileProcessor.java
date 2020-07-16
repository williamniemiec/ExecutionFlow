package executionFlow.io.processor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import executionFlow.io.FileEncoding;
import executionFlow.util.ConsoleOutput;
import executionFlow.util.CurlyBracketBalance;
import executionFlow.util.DataUtils;


/**
 * Processes java file adding instructions in parts of the code that does not 
 * exist when converting it to bytecode.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		2.0.0
 * @since 		2.0.0
 */
public class InvokerFileProcessor extends FileProcessor
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
	private boolean skipNextLine;
	private boolean inComment;
	private boolean wasParsed;
	
	
	//-------------------------------------------------------------------------
	//		Initialization blocks
	//-------------------------------------------------------------------------
	/**
	 * Configures environment. If {@link #DEBUG} is true, displays processed 
	 * lines.
	 */
	static {
		DEBUG = true;
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
	public InvokerFileProcessor(Path filepath, String outputFilename)
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
	public InvokerFileProcessor(Path filepath, Path outputDir, String outputFilename)
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
	public InvokerFileProcessor(Path filepath, Path outputDir, String outputFilename, 
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
	public InvokerFileProcessor(Path filepath, Path outputDir, String outputFilename,
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
	public InvokerFileProcessor(Path filepath, Path outputDir, String outputFilename,
			FileEncoding encode, String fileExtension)
	{
		this(filepath, outputDir, outputFilename, encode);
		this.fileExtension = fileExtension;
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Processes the file adding instructions in parts of the code that does not
	 * exist when converting it to bytecode. Besides, modifies the code so that
	 * {@link executionFlow.core.JDB} computes the test path correctly.
	 * 
	 * @throws		IOException If file encoding is incorrect or if file cannot
	 * be read / written
	 */
	@Override
	public String processFile() throws IOException
	{
		if (file == null) { return ""; }

		String line, nextLine;
		File outputFile;
		PrintParser printParser = new PrintParser();
		InvokerParser invokerParser = new InvokerParser();
		ElseParser elseParser = new ElseParser();
		TryCatchFinallyParser tryFinallyParser = new TryCatchFinallyParser();
		ContinueBreakParser continueBreakParser = new ContinueBreakParser();
		DoWhileParser doWhileParser = new DoWhileParser();
		SwitchParser switchParser = new SwitchParser();
		VariableParser variableParser = new VariableParser();
		
		
		// If an output directory is specified, processed file will be saved to it
		if (outputDir != null)
			outputFile = new File(outputDir.toFile(), outputFilename + "." + fileExtension);
		// Otherwise processed file will be saved in current directory
		else	
			outputFile = new File(outputFilename + "." + fileExtension);
		
		// Opens file streams (file to be parsed and output file / processed file)
		try (	BufferedReader br = Files.newBufferedReader(file, encode.getStandardCharset());
				BufferedReader br_forward = Files.newBufferedReader(file, encode.getStandardCharset()); 
				BufferedWriter bw = Files.newBufferedWriter(outputFile.toPath(), encode.getStandardCharset())	) { 
			br_forward.readLine();
			
			// Parses file line by line
			while ((line = br.readLine()) != null) {
				nextLine = br_forward.readLine();
				wasParsed = false;
				
				if (nextLine == null)
					nextLine = "";
				
				// Checks if it is a comment line
				if (!skipNextLine && !isComment(line)) {
					line = printParser.parse(line);
					line = invokerParser.parse(line);
					line = elseParser.parse(line, nextLine);
					line = tryFinallyParser.parse(line, nextLine);
					line = continueBreakParser.parse(line, nextLine);
					line = doWhileParser.parse(line, nextLine);
					line = switchParser.parse(line, nextLine);
					line = variableParser.parse(line);
				}
				
				// -----{ DEBUG }-----
				if (DEBUG) { ConsoleOutput.showDebug(line); }
				// -----{ END DEBUG }-----	
				
				if (skipNextLine) {
					skipNextLine = false;
					bw.newLine();	// It is necessary to keep line numbers equals to original file 
				}
				else {
					bw.write(line);
					bw.newLine();
				}
			}
		}

		return outputFile.getAbsolutePath();
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

	
	//-------------------------------------------------------------------------
	//		Inner classes
	//-------------------------------------------------------------------------
	/**
	 * Modifies the code in a way that eliminates print statements and put in
	 * place another method, since print statements interfere with 
	 * {@link executionFlow.core.JDB} execution.
	 * 
	 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
	 * @version		2.0.0
	 * @since 		2.0.0
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
	 * Responsible for handling invoker declarations.
	 * 
	 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
	 * @version		2.0.0
	 * @since 		2.0.0
	 */
	private class InvokerParser
	{
		//---------------------------------------------------------------------
		//		Attributes
		//---------------------------------------------------------------------
		private boolean withinInvoker;
		private final String regex_new = "(\\ |\\t)+new(\\ |\\t)*";
		private final String pattern_invokerDeclaration = 
				"(\\ |\\t)*([A-z0-9\\-_$<>\\[\\]\\ \\t]+(\\s|\\t))+[A-z0-9\\-_$]+(\\ |\\t)*\\(.*";
		
		
		//---------------------------------------------------------------------
		//		Methods
		//---------------------------------------------------------------------
		/**
		 * Parses invoker declaration, adding
		 *  {@link executionFlow.runtime.CollectInvokedMethods} annotation.
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
			
			
			if (withinInvoker) {
				if (line.contains("{")) {
					withinInvoker = false;
				}
				
				wasParsed = true;
			}

			else if (	!line.matches(regex_new) && 
						line.matches(pattern_invokerDeclaration) &&
						isInvokerDeclaration(line)	) {
				line = "@executionFlow.runtime.CollectInvokedMethods " + line;
				withinInvoker = !line.contains("{");
				wasParsed = true;
			}
			
			return line;
		}
		
		/**
		 * Checks whether a line contains an invoker declaration.
		 * 
		 * @param		line Line to be analyzed
		 * 
		 * @return		If the line contains an invoker declaration
		 */
		private boolean isInvokerDeclaration(String line)
		{
					// Checks if it is an invoker whose parameters are all on the same line
			return	!line.contains("return ") && !line.contains(" new ") && (
						line.matches("(\\ |\\t)*([A-z0-9\\-\\._$<>\\[\\]\\ \\t]+(\\s|\\t))+[A-z0-9\\-_$]+"
								+ "\\(([A-z0-9\\-_$\\.,<>\\[\\]\\ \\t])*\\)(\\{|(\\s\\{)||\\/)*((\\s|\\ )+"
								+ "(throws|implements|extends)(\\s|\\ )+.+)?") ||
						// Checks if it is an invoker whose parameters are broken on other lines
						line.matches("(\\ |\\t)*([A-z0-9\\-_\\.$<>\\[\\]\\ \\t?]+(\\s|\\t))+"
								+ "[A-z0-9\\-_$]+(\\ |\\t)*\\(.*,(\\ |\\t)*")
					);
		}
	}
	
	/**
	 * Responsible for handling if-else and else statements, being useful to
	 * ensure that {@link executionFlow.core.JDB} compute test paths correctly.
	 * 
	 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
	 * @version		2.0.0
	 * @since 		2.0.0
	 */
	private class ElseParser
	{
		//---------------------------------------------------------------------
		//		Attributes
		//---------------------------------------------------------------------
		private ElseBlockManager elseBlockManager = new ElseBlockManager();
		private final Pattern pattern_else = Pattern.compile("(\\ |\\t|\\})+else(\\ |\\t|\\}|$)+.*");
		private final String regex_catch = "(\\ |\\t|\\})+catch(\\ |\\t)*\\(.*\\)(\\ |\\t)*";
		private final String regex_if_else_closed_curlybracket = "(\\t|\\ )+\\}(\\t|\\ )*(else if|else)(\\t|\\ )*(\\(|\\{).*";
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
		 * 	<li>Adjusts lines containing closed curly bracket + instruction</li>
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
				// Checks if closed curly bracket is on the same line as an
				// else-if or if statement
				if (nextLine.matches(regex_if_else_closed_curlybracket)) {
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
	
				// Else
				if (!line.contains("if") && pattern_else.matcher(line).find()) {
					line = checkCurlyBracketNewLine(line, nextLine);
					line = parse_else(line);
					
					// Checks if parsed else is an else without curly brackets
					if (elseNoCurlyBrackets) {
						elseBlockManager.createNewElseBlock();
						
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
	}
	
	/**
	 * Responsible for handling try-catch-finally statements, being useful to
	 * ensure that {@link executionFlow.core.JDB} compute test paths correctly.
	 * 
	 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
	 * @version		2.0.0
	 * @since 		2.0.0
	 */
	private class TryCatchFinallyParser
	{
		//---------------------------------------------------------------------
		//		Attributes
		//---------------------------------------------------------------------
		private final Pattern pattern_tryFinally = 
				Pattern.compile("(\\t|\\ |\\})+(try|finally)[\\s\\{]");
		private final String regex_catch_closed_curlybracket = 
				"(\\t|\\ )+\\}(\\t|\\ )*catch(\\t|\\ )*(\\(|\\{).*";
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
			
			if (pattern_tryFinally.matcher(line).find() && pattern_tryFinally.matcher(line).find()) {	
				line = checkCurlyBracketNewLine(line, nextLine);
				line = parse_try_finally(line);
				wasParsed = true;
			}
			
			// Checks if closed curly bracket is on the same line as an
			// else-if or if statement
			if (nextLine.matches(regex_catch_closed_curlybracket)) {
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
				response.append("int "+DataUtils.generateVarName()+"=0;");

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
	 * ensure that {@link executionFlow.core.JDB} compute test paths correctly.
	 * 
	 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
	 * @version		2.0.0
	 * @since 		2.0.0
	 */
	private class ContinueBreakParser
	{
		//---------------------------------------------------------------------
		//		Attributes
		//---------------------------------------------------------------------
		private final String regex_continueBreak = "^(\\ |\\t)*(continue|break)(\\ |\\t)*;";
		
		
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
			
			if (line.matches(regex_continueBreak)) {	
				line = checkCurlyBracketNewLine(line, nextLine);
				line = "if (Boolean.parseBoolean(\"True\")) {"+line+"}";
				wasParsed = true;
			}
			
			return line;
		}
	}
	
	/**
	 * Responsible for handling do-while statements, being useful to ensure 
	 * that {@link executionFlow.core.JDB} compute test paths correctly.
	 * 
	 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
	 * @version		2.0.0
	 * @since 		2.0.0
	 */
	private class DoWhileParser
	{
		//---------------------------------------------------------------------
		//		Attributes
		//---------------------------------------------------------------------
		private final Pattern pattern_do = Pattern.compile("(\\t|\\ |\\})+do[\\s\\{]");
		
		
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
			
			if (pattern_do.matcher(line).find()) {								
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
				response.append("int "+DataUtils.generateVarName()+"=0;");
				
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
	 * Responsible for handling switch statements, being useful to ensure 
	 * that {@link executionFlow.core.JDB} compute test paths correctly.
	 * 
	 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
	 * @version		2.0.0
	 * @since 		2.0.0
	 */
	private class SwitchParser
	{
		//---------------------------------------------------------------------
		//		Attributes
		//---------------------------------------------------------------------
		private final Pattern pattern_switch = Pattern.compile("(\\t|\\ |\\})+(case|default)(\\t|\\ )+");
		
		
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
			
			if (pattern_switch.matcher(line).find()) {							
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
			response.append("int "+DataUtils.generateVarName()+"=0;");
			
			// Appends in response everything after ':'
			response.append(line.substring(m.start()+1));

			return response.toString();
		}
	}
	
	/**
	 * Responsible for handling variable declarations, being useful to ensure 
	 * that {@link executionFlow.core.JDB} compute test paths correctly.
	 * 
	 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
	 * @version		2.0.0
	 * @since 		2.0.0
	 */
	private class VariableParser
	{
		//---------------------------------------------------------------------
		//		Attributes
		//---------------------------------------------------------------------
		private final String regex_varDeclarationWithoutInitialization = 
				"( |\\t)*(final(\\s|\\t)+)?[A-z0-9\\-_$]+(\\s|\\t)[A-z0-9\\-_$]+(((,)[A-z0-9\\-_$]+)?)+;";
		
		
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
			
			if (	!line.contains("return ") && !line.contains("return(") && 		
					!line.contains("package ") && !line.contains("class ") && 
					line.matches(regex_varDeclarationWithoutInitialization)	) {
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
			return line+"int "+DataUtils.generateVarName()+"=0;";
		}
	}
	
	/**
	 * Responsible for managing else blocks according to its nesting levels. It
	 * is used only for else blocks without curly brackets, because its 
	 * usefulness is add curly brackets in these blocks.
	 * 
	 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
	 * @version		2.0.0
	 * @since 		2.0.0
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
