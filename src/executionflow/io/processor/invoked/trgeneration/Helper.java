package executionflow.io.processor.invoked.trgeneration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.io.console.ConsoleFilePrinter;


/**
 * @author		Murilo Wolfart
 * @see			https://bitbucket.org/mwolfart/trgeneration/
 */
public class Helper {
	private static List<String> regexReservedChars = new ArrayList<String>(Arrays.asList(
			"{", "}", "\\", "\"", "(", ")"
			));

	public static boolean lineContainsReservedChar(String line, String ch) {
		if (regexReservedChars.contains(ch)) {
			ch = "\\" + ch;
		}
		return line.matches(".*" + ch + Regex.insideQuoteRestriction);
	}
	
	public static boolean lineContainsReservedWord(String line, String word) {
		return line.matches(".*\\b" + word + "\\b" + Regex.insideQuoteRestriction);
	}
	
	private static int matchPatternOnText(Pattern p, String text) {
		Matcher m = p.matcher(text);
		int idx = (m.find() ? m.start() : -1);
		return idx;
	}
	
	public static int getIndexOfReservedString(String text, String lookup) {
		Pattern p = Pattern.compile("\\b" + lookup + "\\b" + Regex.insideQuoteRestriction);
		return matchPatternOnText(p, text);
	}
	
	public static int getIndexOfReservedSymbol(String text, String lookup) {
		Pattern p = Pattern.compile(lookup + Regex.insideQuoteRestriction);
		return matchPatternOnText(p, text);
	}
	
	public static int getIndexOfReservedChar(String text, String lookup) {
		if (regexReservedChars.contains(lookup)) {
			lookup = "\\" + lookup;
		}
		
		Pattern p = Pattern.compile(lookup + Regex.insideQuoteRestriction);
		Matcher m = p.matcher(text);
		int match = (m.find() ? m.start() : -1);
		return match;
	}
	
	public static void createDir(String dirPath) {
		dirPath = dirPath.replace("<", "{").replace(">", "}");
		File dir = new File(dirPath);
		if (dir.exists()) return;
		if (!dir.mkdir()) {
			System.err.println("Could not create directory " + dirPath);
//			System.exit(2);
		}
	}
	
	public static void writeFile(String filePath, String content) {
		filePath = filePath.replace("<", "{").replace(">", "}").replace("?", "").replace("\"", "'");
		try {
			File file = new File(filePath);
			FileWriter fr = new FileWriter(file, true);
			fr.write(content);
			fr.close();
		} catch (IOException e) {
			System.err.println("Error in writing file " + filePath);
		}
	}
	
	public static <T> ArrayList<T> initArray(T firstElement) {
		return new ArrayList<T>(Arrays.asList(firstElement));
	}
	
	public static <T> ArrayList<T> initArray(T[] elements) {
		return new ArrayList<T>(Arrays.asList(elements));
	}
	
	/* TODO: maybe refactor the three next functions */
	public static int findStartOfBlock(List<String> sourceCode, int startingLine) {
		return findStartOfBlock(sourceCode, startingLine, false);
	}
	
	public static int getIndexAfterPosition(String text, String lookup, int startIdx) {
		return startIdx + text.substring(startIdx).indexOf(lookup);
	}
	
	public static int findMatchingParenthesis(String text, int openParenthesisIdx) {
		int depth = 0;
		boolean insideStringOrChar = false;
		boolean escapeChar = false;
		
		for (int i=openParenthesisIdx+1; i<text.length(); i++) {
			if (escapeChar) {
				escapeChar = false;
				continue;
			}
			
			char ch = text.charAt(i);
			
			if (ch == '\\') {
				escapeChar = true;
			}
			else if (ch == '"' || ch == '\'')
				insideStringOrChar = !insideStringOrChar;
			else if (!insideStringOrChar) {
				if (ch == ')' && depth == 0) return i;
				else if (ch == '(') depth++;
				else if (ch == ')') depth--;
			}
		}
		return -1;
	}
	
	public static List<String> splitByReserved(String text, char lookup) {
		List<String> fragments = new ArrayList<String>();
		String fragment = "";
		boolean insideQuote = false;
		for (int i=0; i<text.length(); i++) {

			if (text.charAt(i) == lookup && !insideQuote) {
				fragments.add(fragment);
				fragment = "";
			} else if (text.charAt(i) == '"' || text.charAt(i) == '\'') {
				insideQuote = !insideQuote;
				fragment += text.charAt(i);
//			} else if ((text.charAt(i) == '"' || text.charAt(i) == '\'') && i>0 && text.charAt(i-1) != '\\') {
//				insideQuote = !insideQuote;
//			} else if (text.charAt(i) == '"' || text.charAt(i) == '\'') {
//				insideQuote = !insideQuote;
//				fragment += text.charAt(i);
			} 
			else {
				fragment += text.charAt(i);
			}
		}
		if (fragment.length() > 0) {
			fragments.add(fragment);
		}
		return fragments;
	}

	/* TODO: Maybe reenginer this */
	public static int findStartOfBlock(List<String> sourceCode, int startingLine, boolean useBlockLines) {
		int curLineId = startingLine;
		int openingLine = -1;
		int depth = 0;
		
		while (curLineId >= 0 && openingLine == -1) {
			String curLine = sourceCode.get(curLineId);
			if (Helper.lineContainsReservedChar(curLine, "}")) {
				depth++;
			} else if (Helper.lineContainsReservedChar(curLine, "{") && depth > 0) {
				depth--;
			} else if (Helper.lineContainsReservedChar(curLine, "{")) {
				openingLine = curLineId;
			}
			curLineId--;
		}

		if (openingLine == -1) {
			System.err.println("Braces are not balanced");
			System.err.println("When trying to find start of block ending at line " + (startingLine+1));
//			System.exit(2);
		}
		
		return openingLine;
	}
	
	public static int findEndOfBlock(List<String> sourceCode, int startingLine) {
		int curLineId = startingLine;
		int closingLine = -1;
		int depth = 0;
		
		while (curLineId < sourceCode.size() && closingLine == -1) {
			if (!sourceCode.get(curLineId).contains("} catch(Throwable _")) {
				String curLine = sourceCode.get(curLineId);
				if (Helper.lineContainsReservedChar(curLine, "{")) {
					depth++;
				} else if (Helper.lineContainsReservedChar(curLine, "}") && depth > 0) {
					depth--;
				} else if (Helper.lineContainsReservedChar(curLine, "}")) {
					closingLine = curLineId;
				}
			}
			
			curLineId++;
		}

		if (closingLine == -1) {
			System.out.println("----");
			ConsoleFilePrinter.printFileWithLines(sourceCode);
			
			
			System.err.println("Braces are not balanced");
			System.err.println("When trying to find end of block starting at line " + (startingLine+1));
			System.err.println("Line content: " + sourceCode.get(startingLine));
//			System.exit(2);
		}
		
		return closingLine;
	}
	
	public static int findConditionalLine(List<String> sourceCode, int startingLine) {
		int curLineId = startingLine;
		int conditionalLine = -1;
		int depth = 0;
		
		while (curLineId >= 0 && conditionalLine == -1) {
			String curLine = sourceCode.get(curLineId);
			if (Helper.lineContainsReservedChar(curLine, "}")) {
				depth++;
			} else if (Helper.lineContainsReservedChar(curLine, "{") && depth > 0) {
				depth--;
			} else if (Helper.lineContainsReservedChar(curLine, "{") && curLine.matches("^\\b(do|while)\\b.*")) {
				conditionalLine = curLineId;
			}
			curLineId--;
		}

		if (conditionalLine == -1) {
			System.err.println("Continue without while or do");
			System.err.println("When trying to find conditional of continue at line " + (startingLine+1));			
//			System.exit(2);
		}
		
		return conditionalLine;
	}
}

