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
	private File file;
	private static final String VAR_NAME;
	private boolean alreadyDeclared;
	private File outputDir;
	private String outputName;
	private Stack<Character> curlyBraces;
	
	static {
		Date now = new Date();
		VAR_NAME = "_"+md5(String.valueOf(now.getTime()));
	}
	
	public FileParser(String filename, String outputDir, String outputName)
	{
		file = new File(filename);
		if (outputDir != null)
			this.outputDir = new File(outputDir);
		this.outputName = outputName;
		curlyBraces = new Stack<>();
	}
	
	// Open .java, 
	// parse file 
	// saves parsed file with its original name + _tmp.java
	public String parseFile()
	{
		if (file == null) { return ""; }
		
		//String[] filename = file.getName().split("\\.");
		//File outputFile = new File(filename[0]+"_tmp.java");
		File outputFile;
		
		if (outputDir != null)
			outputFile = new File(outputDir, outputName+".java");
		else
			outputFile = new File(outputName+".java");
		
		try (BufferedReader br = new BufferedReader(new FileReader(file));
			 BufferedReader br2 = new BufferedReader(new FileReader(file));
			 BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
			String line, nextLine;
			String rVarDeclarationWithoutInitialization = "( |\\t)*[A-z0-9\\-_$]+(\\s|\\t)[A-z0-9\\-_$]+(((,)[A-z0-9\\-_$]+)?)+;";
			//String rTryBlock = "try(\\s|\\t)?\\{";
			Pattern elsePattern = Pattern.compile("[\\s\\}]else[\\s\\{]");
			Pattern doPattern = Pattern.compile("(\\t|\\ )+do[\\s\\{]");
			Pattern tryPattern = Pattern.compile("(\\t|\\ )+try[\\s\\{]");
			Pattern switchPattern = Pattern.compile("(\\t|\\ )+case");
			//Pattern pVarDeclarationWithoutInitialization = Pattern.compile("[A-z0-9\\-_$]+(\\s|\\t)[A-z0-9\\-_$]+;");
			//Pattern pTryBlock = Pattern.compile("try(\\s|\\t)?\\{");
			String parsedLine = null;
			br2.readLine();
			//int c=0;
			while ((line = br.readLine()) != null) {
				nextLine = br2.readLine();
				// Parses file line by line
				//System.out.println(line);
				//System.out.println(++c);
				// Try
				// Problem: try without curly braces
				//Matcher m = rTryBlock.matcher(line);
				if (tryPattern.matcher(line).find() && tryPattern.matcher(line).find()) {
					parsedLine = parse_try(line, nextLine);
				} else if (	!line.contains("return ") && !line.contains("return(") && 
							!line.contains("package ") && !line.contains("class ") && 
							line.matches(rVarDeclarationWithoutInitialization)) {
					//System.out.println("var");
					parsedLine = parse_varDeclaration(line);
				} else if (!line.contains("if") && elsePattern.matcher(line).find()) {
					parsedLine = parse_else(line, nextLine);
				} else if (doPattern.matcher(line).find()) {
					parsedLine = parse_do(line, nextLine);
				}  else if (switchPattern.matcher(line).find()) {
					parsedLine = parse_switch(line);
				} else {
					parsedLine = line;
				}
				
				bw.write(parsedLine);
				bw.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return outputFile.getAbsolutePath();
	}
	
	private String parse_do(String line, String nextLine)
	{
		StringBuilder sb = new StringBuilder();

		// Checks if block has curly braces
		if (line.contains("{")) {
			int curlyBraceIndex = line.indexOf('{');
			sb.append(line.substring(0, curlyBraceIndex+1));
			
			if (alreadyDeclared)
				sb.append(VAR_NAME+"=7;");
			else {
				sb.append("int "+VAR_NAME+"=7;");
				alreadyDeclared = true;
			}
			
			sb.append(line.substring(curlyBraceIndex+1));
		} else if (nextLine.contains("{")) {
			int curlyBraceIndex = nextLine.indexOf('{');
			sb.append(nextLine.substring(0, curlyBraceIndex+1));
			
			if (alreadyDeclared)
				sb.append(VAR_NAME+"=7;");
			else {
				sb.append("int "+VAR_NAME+"=7;");
				alreadyDeclared = true;
			}
			
			sb.append(nextLine.substring(curlyBraceIndex+1));
		} else {
			throw new IllegalStateException("Code block must be enclosed in curly braces");
		}
		
		return sb.toString();
	}
	
	private String parse_else(String line, String nextLine)
	{
		StringBuilder sb = new StringBuilder();
		//System.out.println(line);
		// Checks if block has curly braces
		if (line.contains("{")) {
			int curlyBraceIndex = line.indexOf('{');
			sb.append(line.substring(0, curlyBraceIndex+1));
			
			if (alreadyDeclared)
				sb.append(VAR_NAME+"=7;");
			else {
				sb.append("int "+VAR_NAME+"=7;");
				alreadyDeclared = true;
			}
			
			sb.append(line.substring(curlyBraceIndex+1));
		} else if (nextLine.contains("{")) {
			int curlyBraceIndex = nextLine.indexOf('{');
			sb.append(nextLine.substring(0, curlyBraceIndex+1));
			
			if (alreadyDeclared)
				sb.append(VAR_NAME+"=7;");
			else {
				sb.append("int "+VAR_NAME+"=7;");
				alreadyDeclared = true;
			}
			
			sb.append(nextLine.substring(curlyBraceIndex+1));
		} else {
			throw new IllegalStateException("Code block must be enclosed in curly braces");
		}
		
		return sb.toString();
	}
	
	private String parse_try(String line, String nextLine)
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
			int curlyBraceIndex = line.indexOf('{');
			sb.append(line.substring(0, curlyBraceIndex+1));
			
			if (alreadyDeclared)
				sb.append(VAR_NAME+"=7;");
			else {
				sb.append("int "+VAR_NAME+"=7;");
				alreadyDeclared = true;
			}
			
			sb.append(line.substring(curlyBraceIndex+1));
		} else if (nextLine.contains("{")) {
			int curlyBraceIndex = nextLine.indexOf('{');
			sb.append(nextLine.substring(0, curlyBraceIndex+1));
			
			if (alreadyDeclared)
				sb.append(VAR_NAME+"=7;");
			else {
				sb.append("int "+VAR_NAME+"=7;");
				alreadyDeclared = true;
			}
			
			sb.append(nextLine.substring(curlyBraceIndex+1));
		} else {
			throw new IllegalStateException("Code block must be enclosed in curly braces");
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
