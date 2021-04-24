package wniemiec.app.executionflow.io.processing.processor.trgeneration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import wniemiec.io.consolex.Consolex;

/**
 * @author		Murilo Wolfart
 * @see			https://bitbucket.org/mwolfart/trgeneration/
 */
class Helper {

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
	
	public static boolean hasOddNumberOfQuotes(String text) {
		int countSimple = 0;
		int countDouble = 0;
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == '"' && i > 0 && text.charAt(i-1) != '\\')
				countDouble++;
			else if (text.charAt(i) == '\'' && i > 0 && text.charAt(i-1) != '\\')
				countSimple++;
		}
		return countSimple % 2 == 1 || countDouble % 2 == 1;
	}
	
	public static void createDir(String dirPath) throws Exception {
		dirPath = dirPath.replace("<", "{").replace(">", "}").replace("?", "QM");
		File dir = new File(dirPath);
		if (dir.exists()) return;
		if (!dir.mkdir()) {
			throw new Exception("Could not create directory " + dirPath);
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
			System.out.println("Error in writing file " + filePath);
		}
	}
	
	public static <T> ArrayList<T> initArray(T firstElement) {
		return new ArrayList<T>(Arrays.asList(firstElement));
	}
	
	public static <T> ArrayList<T> initArray(T[] elements) {
		return new ArrayList<T>(Arrays.asList(elements));
	}
	
	/* TODO: maybe refactor the three next functions */
	public static int findStartOfBlock(List<String> sourceCode, int startingLine) throws Exception {
		return findStartOfBlock(sourceCode, startingLine, false);
	}
	
	public static int getIndexAfterPosition(String text, String lookup, int startIdx) {
		return startIdx + text.substring(startIdx).indexOf(lookup);
	}
	
	public static int findMatchingParenthesis(String text, int openParenthesisIdx) {
		int depth = 0;
		for (int i=openParenthesisIdx+1; i<text.length(); i++) {
			char ch = text.charAt(i);
			if (ch == ')' && depth == 0) return i;
			else if (ch == '(') depth++;
			else if (ch == ')') depth--;
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
	public static int findStartOfBlock(List<String> sourceCode, int startingLine, boolean useBlockLines) throws Exception {
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
			throw new Exception("Braces are not balanced when trying to find start of block ending at line " + (startingLine+1));
		}
		
		return openingLine;
	}
	
	public static int findEndOfBlock(List<String> sourceCode, int startingLine) throws Exception {
		int curLineId = startingLine;
		int closingLine = -1;
		int depth = 0;

		while (curLineId < sourceCode.size() && closingLine == -1) {
			String curLine = sourceCode.get(curLineId);
			if (Helper.lineContainsReservedChar(curLine, "{")) {
				depth++;
			} else if (Helper.lineContainsReservedChar(curLine, "}") && depth > 0) {
				depth--;
			} else if (Helper.lineContainsReservedChar(curLine, "}")) {
				closingLine = curLineId;
			}
			curLineId++;
		}
		
		if (closingLine == -1) {
			throw new Exception("Braces are not balanced when trying to find end of block starting at line " 
						+ (startingLine+1) + "\nLine content: " + sourceCode.get(startingLine));
		}
		
		return closingLine;
	}
	
	public static int findConditionalLine(List<String> sourceCode, int startingLine, boolean isBreak) throws Exception {
		int curLineId = startingLine;
		int conditionalLine = -1;
		int depth = 0;
		String words = isBreak ? "do|while|switch" : "do|while";
		
		while (curLineId >= 0 && conditionalLine == -1) {
			String curLine = sourceCode.get(curLineId);
			if (Helper.lineContainsReservedChar(curLine, "}")) {
				depth++;
			} else if (Helper.lineContainsReservedChar(curLine, "{") && depth > 0) {
				depth--;
			} else if (Helper.lineContainsReservedChar(curLine, "{") 
					&& (curLine.matches("^\\b("+words+")\\b.*")
							|| curLine.matches("^[a-zA-Z_]+[a-zA-Z0-9_]*:\\s*("+words+").*"))) {
				conditionalLine = curLineId;
			}
			curLineId--;
		}

		if (conditionalLine == -1) {		
			throw new Exception("Continue or break without while or do when trying to find conditional of continue at line " + (startingLine+1));
		}
		
		return conditionalLine;
	}
	
	public static int findConditionalLine(List<String> sourceCode, int startingLine) throws Exception {
		return findConditionalLine(sourceCode, startingLine, false);
	}
}

