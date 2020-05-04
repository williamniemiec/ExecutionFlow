package executionFlow.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileParser 
{
	private File file;
	private static final String VAR_NAME;
	private boolean alreadyDeclared;
	
	static {
		VAR_NAME="x";
	}
	
	public FileParser(String filename)
	{
		file = new File(filename);
		
	}
	
	// Open .java, 
	// parse file 
	// saves parsed file with its original name + _tmp.java
	public void parseFile()
	{
		if (file == null) { return; }
		
		String[] filename = file.getName().split("\\.");
		File outputFile = new File(filename[0]+"_tmp.java");
		
		try (BufferedReader br = new BufferedReader(new FileReader(file));
			 BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
			String line;
			Pattern rVarDeclarationWithoutInitialization = Pattern.compile("[A-z0-9\\-_$]+(\\s|\\t)[A-z0-9\\-_$]+;");
			Pattern rTryBlock = Pattern.compile("try(\\s|\\t)?\\{");
			String parsedLine = null;
			
			while ((line = br.readLine()) != null) {
				// Parses file line by line
				
				// Try
				// Problem: try without curly braces
				Matcher m = rTryBlock.matcher(line);
				if (line.contains("try") && m.find()) {
					StringBuilder sb = new StringBuilder();
					sb.append(line.substring(0, m.end()));
					// try{int VAR_NAME=7;
					if (alreadyDeclared)
						sb.append(VAR_NAME+"=7");
					
					else {
						sb.append("int "+VAR_NAME+"=7;");
						alreadyDeclared = true;
					}
					
					sb.append(line.substring(m.end()));
					parsedLine = sb.toString();
				} else if (!line.contains("return") && line.matches("[A-z0-9\\-_$]+(\\s|\\t)[A-z0-9\\-_$]+(((,)[A-z0-9\\-_$]+)?)+;")) {
					line = VAR_NAME+"=7;"+line;
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
				} else {
					parsedLine = line;
				}
				
				bw.write(parsedLine);
				bw.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
