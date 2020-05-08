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
	
	/**
	 * If true, displays processed lines.
	 */
	private final boolean DEBUG;
	
	
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
	{
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
		boolean skipNextLine = false;
		
		if (outputDir != null)
			outputFile = new File(outputDir, outputName+".java");
		else
			outputFile = new File(outputName+".java");
		
		try (BufferedReader br = new BufferedReader(new FileReader(file));
			 BufferedReader br2 = new BufferedReader(new FileReader(file));
			 BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
			String line, nextLine;
			String regex_varDeclarationWithoutInitialization = "( |\\t)*[A-z0-9\\-_$]+(\\s|\\t)[A-z0-9\\-_$]+(((,)[A-z0-9\\-_$]+)?)+;";
			Pattern pattern_else = Pattern.compile("[\\s\\}]else[\\s\\{]");
			Pattern pattern_do = Pattern.compile("(\\t|\\ |\\})+do[\\s\\{]");
			Pattern pattern_tryFinally = Pattern.compile("(\\t|\\ |\\})+(try|finally)[\\s\\{]");
			Pattern pattern_switch = Pattern.compile("(\\t|\\ |\\})+case");
			Pattern pattern_methodDeclaration = Pattern.compile("(\\ |\\t)*([A-z0-9\\-_$]+(\\s|\\t))+[A-z0-9\\-_$]+\\(([A-z0-9\\-_$,\\s])*\\)(\\{|(\\s\\{))?");
			Pattern pattern_openCurlyBrackets = Pattern.compile("\\{");
			Pattern pattern_closedCurlyBrackets = Pattern.compile("\\}");
			String regex_onlyOpenCurlyBracket = "^(\\s|\\t)+\\{(\\s|\\t)*$";
			String regex_for = "(\\ |\\t|\\})+for(\\ |\\t)*\\(.*\\)(\\ |\\t)*";
			String regex_while = "(\\ |\\t|\\})+while(\\ |\\t)*\\(.*\\)(\\ |\\t)*";
			String regex_catch = "(\\ |\\t|\\})+catch(\\ |\\t)*\\(.*\\)(\\ |\\t)*";
			String regex_try = "(\\ |\\t|\\})+try(\\ |\\t)*";
			String parsedLine = null;
			br2.readLine();
			
			boolean inLoop = false;
			
			// Parses file line by line
			while ((line = br.readLine()) != null) {
				nextLine = br2.readLine();
				
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
					
					continue;
				}
				
				if (elseNoCurlyBrackets) {
					Matcher openCBMatcher = pattern_openCurlyBrackets.matcher(line);
					if (openCBMatcher.find()) {
						for (int i=0; i<openCBMatcher.groupCount(); i++) {
							curlyBrackets.push('{');
						}
					}
					
					Matcher closedCBMatcher = pattern_closedCurlyBrackets.matcher(line);
					if (closedCBMatcher.find()) {
						for (int i=0; i<closedCBMatcher.groupCount(); i++) {
							curlyBrackets.pop();
						}
					}
					
					if (curlyBrackets.empty()) {							
						if (line.matches(regex_catch)) {
							if (line.contains("{") && !line.contains("}")) {
								curlyBrackets.push('{');
							} else if (line.contains("{") && line.contains("}")) {
								line += "}";
								elseNoCurlyBrackets = false;
								
								if (inNestedStructWithoutCurlyBrackets) {	// In block code without curly brackets
									inNestedStructWithoutCurlyBrackets = false;
								}
							}
						} else if (!nextLine.matches(regex_catch)){
							if (!inNestedStructWithoutCurlyBrackets) {	// In block code with curly brackets
								line += "}";
								elseNoCurlyBrackets = false;
							} else {	// In block code without curly brackets
								if (line.matches(regex_for) || line.matches(regex_while)) {	
									inLoop = true;
								} else if (inLoop && !nextLine.matches(regex_for) && !nextLine.matches(regex_while)) {
									inNestedStructWithoutCurlyBrackets = false;
									inLoop = false;
									
									if (!nextLine.matches(regex_try)) {
										elseNoCurlyBrackets = false;
										line += "}";
									}
								}
							}
						}
					}
				} 
				
				if (pattern_tryFinally.matcher(line).find() && pattern_tryFinally.matcher(line).find()) {	// Try or finally
					if (nextLine.matches(regex_onlyOpenCurlyBracket)) {
						line = line + " {";
						skipNextLine = true;
					}
					
					parsedLine = parse_try_finally(line);
				} else if (	!line.contains("return ") && !line.contains("return(") && 		// Var declaration
							!line.contains("package ") && !line.contains("class ") && 
							line.matches(regex_varDeclarationWithoutInitialization)) {
					parsedLine = parse_varDeclaration(line);
				} else if (!line.contains("if") && pattern_else.matcher(line).find()) {		// Else
					if (nextLine.matches(regex_onlyOpenCurlyBracket)) {
						line = line + " {";
						skipNextLine = true;
					}
					
					parsedLine = parse_else(line);
					if (elseNoCurlyBrackets) {
						// Checks if next line is a block code
						// If it is, put } at the end
						// Else put } at the end of line
						
						if (!nextLine.contains("{")) {	// If there are not curly brackets in else nor next line
							// Checks if it is an one line command
							if (nextLine.matches(";$")) { // One line command
								bw.write(parsedLine);
								bw.newLine();
								
								if (DEBUG)
									System.out.println(parsedLine);
								
								nextLine = br2.readLine();
								line = br.readLine();
								parsedLine = line +"}";
								elseNoCurlyBrackets = false;
							} else { // Checks if it is a block code
								inNestedStructWithoutCurlyBrackets = true;
							}
						}
					}
					
				} else if (pattern_do.matcher(line).find()) {								// Do while
					if (nextLine.matches(regex_onlyOpenCurlyBracket)) {
						line = line + " {";
						skipNextLine = true;
					}
					
					parsedLine = parse_do(line);
					
//					bw.write(parsedLine);
//					bw.newLine();
				}  else if (pattern_switch.matcher(line).find()) {							// Switch
					if (nextLine.matches(regex_onlyOpenCurlyBracket)) {
						line = line + " {";
						skipNextLine = true;
					}
					
					parsedLine = parse_switch(line);
					
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
				sb.append(VAR_NAME+"=7;");
			else {
				sb.append("int "+VAR_NAME+"=7;");
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
				sb.append(VAR_NAME+"=7;");
			else {
				sb.append("int "+VAR_NAME+"=7;");
				alreadyDeclared = true;
			}
			
			sb.append(line.substring(curlyBracketsIndex+1));
		} else {
			//throw new IllegalStateException("Code block must be enclosed in curly brackets");
			
			int indexAfterElse = line.indexOf("else")+4; 
			sb.append(line.substring(0, indexAfterElse));
			
			if (alreadyDeclared)
				sb.append(" {"+VAR_NAME+"=7;");
			else {
				sb.append(" {"+"int "+VAR_NAME+"=7;");
				alreadyDeclared = true;
			}
			
			String afterElse = line.substring(indexAfterElse);
			if (!afterElse.matches("^(\\s|\\t)+$")) {	// Command in same line
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
//		System.out.println(line);
		Pattern pTryBlock = Pattern.compile("(\\t| )+try(\\s|\\t)?\\{");
		Matcher m = pTryBlock.matcher(line);
		m.find();
		//sb.append(line.substring(0, m.end()));
		// try{int VAR_NAME=7;
		//---
		if (line.contains("{")) {
			int curlyBracketsIndex = line.indexOf('{');
			sb.append(line.substring(0, curlyBracketsIndex+1));
			
			if (alreadyDeclared)
				sb.append(VAR_NAME+"=7;");
			else {
				sb.append("int "+VAR_NAME+"=7;");
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
			return line+VAR_NAME+"=7;";
		else {
			alreadyDeclared = true;
			return line+"int "+VAR_NAME+"=7;";
		}
		
		//line = VAR_NAME+"=7;"+line;
		/*
		Matcher m = rVarDeclarationWithoutInitialization.matcher(line);
		if (m.find()) {
			StringBuilder sb = new StringBuilder();
			sb.append(line.substring(0, m.end()));
			// int x,y;{int VAR_NAME=7;
			if (alreadyDeclared)
				sb.append(VAR_NAME+"=7;");
			else {
				sb.append("int "+VAR_NAME+"=7;");
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
		sb.append(line.substring(0, m.start()+1));
		
		if (alreadyDeclared)
			sb.append(VAR_NAME+"=7;");
		else {
			sb.append("int "+VAR_NAME+"=7;");
			alreadyDeclared = true;
		}
		
		sb.append(line.substring(m.start()+1));

		return sb.toString();
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
