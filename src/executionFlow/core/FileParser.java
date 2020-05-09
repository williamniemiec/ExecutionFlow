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


public class FileParser 
{
	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
	private File file;
	private static final String VAR_NAME;
	private boolean alreadyDeclared;
	private File outputDir;
	private String outputName;
	private Stack<Character> curlyBrackets;
	boolean elseNoCurlyBrackets;
	boolean inNestedStructWithoutCurlyBrackets;
	boolean skipNextLine;
	int numberOfElses;
	
	/**
	 * key: else nesting level
	 * value: curly brackets balance
	 */
	Map<Integer, Integer> elseBlock = new HashMap<>();
	
	Map<Integer, Boolean> elseBlock_moreTwo = new HashMap<>();
	
	
	private static final String regex_onlyOpenCurlyBracket = "^(\\s|\\t)+\\{(\\s|\\t|\\/)*$";
	private static final String regex_varDeclarationWithoutInitialization = "( |\\t)*[A-z0-9\\-_$]+(\\s|\\t)[A-z0-9\\-_$]+(((,)[A-z0-9\\-_$]+)?)+;";
	private static final String regex_for = "(\\ |\\t|\\})+for(\\ |\\t)*\\(.*\\)(\\ |\\t)*";
	private static final String regex_while = "(\\ |\\t|\\})+while(\\ |\\t)*\\(.*\\)(\\ |\\t)*";
	private static final String regex_catch = "(\\ |\\t|\\})+catch(\\ |\\t)*\\(.*\\)(\\ |\\t)*";
	private static final String regex_try = "(\\ |\\t|\\})+try(\\ |\\t)*";
	private static final Pattern pattern_tryFinally = Pattern.compile("(\\t|\\ |\\})+(try|finally)[\\s\\{]");
	private static final Pattern pattern_else = Pattern.compile("(\\ |\\t|\\})+else(\\ |\\t|\\}|$)+.*");
	private static final Pattern pattern_do = Pattern.compile("(\\t|\\ |\\})+do[\\s\\{]");
	private static final Pattern pattern_switch = Pattern.compile("(\\t|\\ |\\})+case");
	private static final Pattern pattern_methodDeclaration = Pattern.compile("(\\ |\\t)*([A-z0-9\\-_$]+(\\s|\\t))+[A-z0-9\\-_$]+\\(([A-z0-9\\-_$,\\s])*\\)(\\{|(\\s\\{)||\\/)*");
	private static final Pattern pattern_openCurlyBrackets = Pattern.compile("\\{");
	private static final Pattern pattern_closedCurlyBrackets = Pattern.compile("\\}");
	
	/**
	 * If true, displays processed lines.
	 */
	private static final boolean DEBUG;
	
	
	//-----------------------------------------------------------------------
	//		Initialization blocks
	//-----------------------------------------------------------------------
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
	
	
	//-----------------------------------------------------------------------
	//		Constructor
	//-----------------------------------------------------------------------
	public FileParser(String filename, String outputDir, String outputName)
	{
		file = new File(filename);
		if (outputDir != null)
			this.outputDir = new File(outputDir);
		this.outputName = outputName;
		curlyBrackets = new Stack<>();
	}
	
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	// Open .java, 
	// parse file 
	// saves parsed file with its original name + _tmp.java
	public String parseFile()
	{
		if (file == null) { return ""; }

		File outputFile;
		//boolean skipNextLine = false;
		
		if (outputDir != null)
			outputFile = new File(outputDir, outputName+".java");
		else
			outputFile = new File(outputName+".java");
		
		try (BufferedReader br = new BufferedReader(new FileReader(file));
			 BufferedReader br2 = new BufferedReader(new FileReader(file));
			 BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
			String line, nextLine;

			String parsedLine = null;
			br2.readLine();
			
			boolean inLoop = false;
			boolean inComment = false;
			boolean inMethod = false;
			
			// Parses file line by line
			while ((line = br.readLine()) != null) {
				nextLine = br2.readLine();
				
				if (line.contains("//")) {
					bw.write(line);
					bw.newLine();
					continue;
				}
				
				if (inComment) {
					if (line.contains("*/"))
						inComment = false;
					
					bw.write(line);
					bw.newLine();
					continue;
				}
								
				if (line.contains("/*") && !line.contains("*/")) {
					inComment = true;
					bw.write(line);
					bw.newLine();
					continue;
				}
				
				if (inMethod) {
					if (line.contains("{")) {
						inMethod = false;
					}
					
					continue;
				}
				
				if (nextLine == null)
					nextLine = "";
//				System.out.println("SIZE: "+elseBlock.size());
//				System.out.println("elseNoCurlyBrackets: "+elseNoCurlyBrackets);
//				System.out.println(elseBlock);
//				System.out.println(line);
				
				if (skipNextLine) {
					skipNextLine = false;
					bw.newLine();	// It is necessary to keep line numbers equals to original file 
					
					if (DEBUG)
						System.out.println();
					
					continue;
				}
				
				// Checks if it is a method declaration
				if (pattern_methodDeclaration.matcher(line).find()) {
					alreadyDeclared = false;
					bw.write(line);
					bw.newLine();
					
					if (DEBUG)
						System.out.println(line);
					
					inMethod = true;
				
					continue;
				}
				
				if (elseNoCurlyBrackets) {
					
					Matcher openCBMatcher = pattern_openCurlyBrackets.matcher(line);
					//System.out.println(openCBMatcher.find());
					//System.out.println(openCBMatcher.);
					int size;
					for (size = 0; openCBMatcher.find(); size++);
					
					if (size > 0) {
						//for (int i=0; i<openCBMatcher.groupCount(); i++) {
						for (int i=0; i<size; i++) {
							//curlyBrackets.push('{');
							increaseElseBlockBalance(elseBlock);
						}
					}
					
					Matcher closedCBMatcher = pattern_closedCurlyBrackets.matcher(line);
					
					for (size = 0; closedCBMatcher.find(); size++);
					
					if (size > 0) {
						for (int i=0; i<size; i++) {
							//curlyBrackets.pop();
							decreaseElseBlockBalance(elseBlock);
						}
					}
					if (getElseBlockBalance(elseBlock) == 0) {
//						System.out.println(line);
						if (line.matches(regex_catch)) {
							
							if (line.contains("{") && !line.contains("}")) {
								increaseElseBlockBalance(elseBlock);
							} else if (line.contains("{") && line.contains("}")) {
								line += "}";
								decreaseElseBlockBalance(elseBlock);
								
								if (getElseBlockBalance(elseBlock) == 0)
									elseBlock.remove(numberOfElses--);
								
								if (numberOfElses == 0)
									elseNoCurlyBrackets = false;
								
								if (inNestedStructWithoutCurlyBrackets) {	// In block code without curly brackets
									inNestedStructWithoutCurlyBrackets = false;
								}
							}
						} else if (!nextLine.matches(regex_catch)){
//							System.out.println("noCatch");
//							System.out.println("INLOOP? "+inLoop);
//							System.out.println("inNestedStructWithoutCurlyBrackets: "+inNestedStructWithoutCurlyBrackets);
							//if (!inNestedStructWithoutCurlyBrackets) {	// In block code with curly brackets
							if (true) {
								line += "}";
								//decreaseElseBlockBalance(elseBlock);
								
								//if (getElseBlockBalance(elseBlock) == 0)
								elseBlock.remove(numberOfElses--);
								//elseNoCurlyBrackets = false;
								if (numberOfElses == 0)
									elseNoCurlyBrackets = false;
//								System.out.println("OUTELSE");
							} //else {	// In block code without curly brackets
								if (line.matches(regex_for) || line.matches(regex_while)) {	
									inLoop = true;

								} 
								
								if (inLoop && !nextLine.matches(regex_for) && !nextLine.matches(regex_while) && !nextLine.matches(regex_try)) {
									inNestedStructWithoutCurlyBrackets = false;
									inLoop = false;
								}
						}
					}
				}
				while (numberOfElses > 0 && elseBlock.get(numberOfElses) == 1 && elseBlock_moreTwo.get(numberOfElses)) {
//					System.out.println("remove");
					line += "}";
					elseBlock.remove(numberOfElses);
					elseBlock_moreTwo.remove(numberOfElses);
					
					numberOfElses--;
				}
				
				if (numberOfElses == 0) {
					elseNoCurlyBrackets = false;
				}
				
				if (pattern_tryFinally.matcher(line).find() && pattern_tryFinally.matcher(line).find()) {	// Try or finally
					line = checkCurlyBracketNewLine(line, nextLine);
					parsedLine = parse_try_finally(line);
				} else if (!line.contains("if") && pattern_else.matcher(line).find()) {		// Else
					line = checkCurlyBracketNewLine(line, nextLine);
					
					parsedLine = parse_else(line);
					if (elseNoCurlyBrackets) {
						numberOfElses++;
						elseBlock.put(numberOfElses, 1);
						elseBlock_moreTwo.put(numberOfElses, false);
						//System.out.println("NUM_ELSES: "+numberOfElses);
						
						
						// Checks if next line is a block code
						// If it is, put } at the end
						// Else put } at the end of line
						
						
						
						if (!nextLine.contains("{")) {	// If there are not curly brackets in else nor next line
							// Checks if it is an one line command
							if (nextLine.matches(".+;$")) { // One line command
								bw.write(parsedLine);
								bw.newLine();
								
								if (DEBUG)
									System.out.println(parsedLine);
								
								nextLine = br2.readLine();
								line = br.readLine();
								parsedLine = line +"}";
								//elseNoCurlyBrackets = false;
								//curlyBrackets.pop();
								//elseBlock.remove(numberOfElses--);
								decreaseElseBlockBalance(elseBlock);
								
								if (getElseBlockBalance(elseBlock) == 0) {
									elseBlock.remove(numberOfElses);
									elseBlock_moreTwo.remove(numberOfElses--);
								}
								
								if (numberOfElses == 0) {
									elseNoCurlyBrackets = false;
								}
								
							} else { // Checks if it is a block code
								inNestedStructWithoutCurlyBrackets = true;
							}
						}
					}
					
				} else if (pattern_do.matcher(line).find()) {								// Do while
					line = checkCurlyBracketNewLine(line, nextLine);
					parsedLine = parse_do(line);
					
//					bw.write(parsedLine);
//					bw.newLine();
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
//		System.out.println(line);
		StringBuilder sb = new StringBuilder();
		//System.out.println(line);
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
			
			//curlyBrackets.push('{');
		} else {
			//throw new IllegalStateException("Code block must be enclosed in curly brackets");
			int indexAfterElse = line.indexOf("else")+4; 
			sb.append(line.substring(0, indexAfterElse));
			
			if (alreadyDeclared)
				sb.append(" {"+VAR_NAME+"=0;");
			else {
				sb.append(" {"+"int "+VAR_NAME+"=0;");
				alreadyDeclared = true;
			}
			//sb.append(line.substring(indexAfterElse));
			//curlyBrackets.push('{');
			//increaseElseBlockBalance(elseBlock);
			
			String afterElse = line.substring(indexAfterElse);
			
			if (!afterElse.isEmpty() && !afterElse.matches("^(\\s|\\t)+$")) {	// Command in same line
				sb.append(afterElse);
				sb.append("}");
				//curlyBrackets.pop();
				//elseBlock.remove(numberOfElses);
			} else {
				elseNoCurlyBrackets = true;
			}
		}
		
		return sb.toString();
	}
	
	private String parse_try_finally(String line)
	{
		StringBuilder sb = new StringBuilder();
//		System.out.println(line);
		//Pattern pTryBlock = Pattern.compile("(\\t| )+try(\\s|\\t)?\\{");
		//Matcher m = pTryBlock.matcher(line);
		Matcher m = pattern_tryFinally.matcher(line);
		m.find();
		//sb.append(line.substring(0, m.end()));
		// try{int VAR_NAME=7;
		//---
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
		//---
		return sb.toString();
	}
	
	private String parse_varDeclaration(String line)
	{
		//System.out.println("var");
		
		
		if (alreadyDeclared)
			return line+VAR_NAME+"=0;";
		else {
			alreadyDeclared = true;
			return line+"int "+VAR_NAME+"=0;";
		}
		
		//line = VAR_NAME+"=0;"+line;
		/*
		Matcher m = rVarDeclarationWithoutInitialization.matcher(line);
		if (m.find()) {
			StringBuilder sb = new StringBuilder();
			sb.append(line.substring(0, m.end()));
			// int x,y;{int VAR_NAME=7;
			if (alreadyDeclared)
				sb.append(VAR_NAME+"=0;");
			else {
				sb.append("int "+VAR_NAME+"=0;");
				alreadyDeclared = true;
			}
			
			sb.append(line.substring(m.end()));
			parsedLine = sb.toString();
		}*/
	}
	
	private String parse_switch(String line)
	{
		StringBuilder sb = new StringBuilder();
//		System.out.println(line);
		Pattern p = Pattern.compile(":");
		Matcher m = p.matcher(line);
		m.find();
		//sb.append(line.substring(0, m.end()));
		// try{int VAR_NAME=7;
		//---
		//System.out.println(line);
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
	
	private String checkCurlyBracketNewLine(String line, String nextLine)
	{
		if (nextLine.matches(regex_onlyOpenCurlyBracket)) {
			line = line + " {";
			skipNextLine = true;
		}
		
		return line;
	}
	
	private void increaseElseBlockBalance(Map<Integer, Integer> elseBlock)
	{
		Integer balance = elseBlock.get(numberOfElses);
		balance++;
		elseBlock.put(numberOfElses, balance);
		
		if (balance >= 2) {
			elseBlock_moreTwo.put(numberOfElses, true);
		}
	}
	
	private void decreaseElseBlockBalance(Map<Integer, Integer> elseBlock)
	{
		Integer balance = elseBlock.get(numberOfElses);
		balance--;
		elseBlock.put(numberOfElses, balance);
	}
	
	private int getElseBlockBalance(Map<Integer, Integer> elseBlock)
	{
		return elseBlock.get(numberOfElses).intValue();
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
}
