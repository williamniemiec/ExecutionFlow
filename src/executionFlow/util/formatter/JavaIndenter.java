package executionFlow.util.formatter;

import java.util.*;

/**
 * Java indenter of // comments. Indent a Java program read from the standard
 * input. Indented program written to the standard output.
 * 
 * @author Liam McLeod
 * @see https://github.com/LiamMcLeod/JavaIndenter
 */
public class JavaIndenter {
	/*
	 * This approach only works so far, and there will be cases when it will be
	 * difficult to correctly indent some Java code constructs etc. For example,
	 * this comment, parts of a switch statement ...
	 */
	// The stored Java program
	private ArrayList<JavaLine> program = new ArrayList<>();

	// Flag for braces and a single pass
	private static boolean blnFlag = false;
	private boolean blnCheck = false;
	// Comment flag for block comments and final pass
	private static boolean commentFlag = false;
	private static boolean blockComment = false;

	/**
	 * @param args Arguments passed to program (ignored)
	 */

	// Should be public static when initiated from itself
	public List<String> format(List<String> lines) {
		process(lines);
		return getProcessedLines();
	}

	private List<String> getProcessedLines() {
		int max = findMaxJavaLineLength();
		List<String> formatedLines = new ArrayList<>();

		for (JavaLine strLine : program) {
			formatedLines.add(strLine.returnLineWithCommentAt(max + 1));
		}

		return formatedLines;
	}

	/*
	 * Do the actual work of indenting
	 */
	private void process(List<String> lines) {
		readProgram(lines);
//    int max = findMaxJavaLineLength();
//    printIndentedProgram( max+1 );
	}

	/*
	 * Read each source strLine of the program and store in the data structure
	 * program
	 */
	private void readProgram(List<String> lines) {

		// BIO.getString == userInput.nextLine();
		// String strLine = BIO.getString();
		// !BIO.eof
		// OR .hasNext();
		// while (userInput.hasNextLine())

		int bracketCount = 0;

		for (String strLine : lines) {
//	while (userInput.hasNextLine()){
//		String strLine = userInput.nextLine();
			// Boolean flag to signify the fact that it hasn't yet
			// reached a single bracket. Maybe
			if (strLine.equals("") && bracketCount == 0) {
				break;
			}
			if (!strLine.equals("")) {

				bracketCount = checkFlags(strLine, bracketCount);

				blockComment = checkBlock(strLine);

				bracketCount = checkChars(strLine, bracketCount);
				if (blnFlag == false) {
					bracketCount = checkBrackets(strLine, bracketCount);
				}
				// declaring new instance of JavaLine object parsing input strLine at given time
				program.add(new JavaLine(strLine, bracketCount));
			}

		}
	}

	private int checkFlags(String strLine, int bracketCount) {
		if (blnFlag == true && strLine.indexOf("{") > -1) { // || strLine.contains("case"))
			// Bracket
			bracketCount--;
			blnFlag = false;
			blnCheck = false;
		} else if (blnFlag == true) {
			blnCheck = true;
			blnFlag = false;
		} else {
			if (blnCheck == true) {
				bracketCount--;
				blnCheck = false;
			} else if (blnCheck == true && !strLine.contains("break;")) {

				blnCheck = true;
			}

		}
		if (blockComment == true && commentFlag == true) {
			blockComment = false;
			commentFlag = false;
		}
		if (blockComment == true) {
			int v = strLine.indexOf('*');
			if (strLine.indexOf('*') > -1) {
				if (v + 1 < strLine.length()) {
					if (strLine.charAt(v + 1) == '/') {

						blockComment = true;
						commentFlag = true;
					}
				}
			}
		}

		// No Bracket
//			blnFlag=false;

		return bracketCount;
	}

	private static int checkBrackets(String strLine, int bracketCount) {
		// boolean maybe
		boolean speechDetected = false;
		if (strLine.indexOf("\"") > -1) {
			speechDetected = true;
			// index of { OR } > lastIndex of '"'
		}

		if (speechDetected == false) {

			if (strLine.indexOf("{") > -1) {
				bracketCount++;
			} else {

			}

			if (strLine.indexOf("}") > -1) {
				bracketCount--;
			}

		} else {
			if (strLine.lastIndexOf("\"") < strLine.lastIndexOf("{")) {

				bracketCount++;

			}
			if (strLine.lastIndexOf("\"") < strLine.lastIndexOf("}")) {

				bracketCount--;

			}
		}
		return bracketCount;
	}

	private int checkChars(String strLine, int bracketCount) {
		String checkChar;

		strLine = strLine.trim();
		if (strLine.length() > 3) {
			checkChar = strLine.charAt(0) + "" + strLine.charAt(1) + "" + strLine.charAt(2);

			if (strLine.indexOf("{") == -1) {
				// adjust later
				// Regex maybe?

				if (checkChar.equals("do ") || checkChar.equals("do(") || checkChar.equals("for")
						|| checkChar.equals("whi") || checkChar.equals("if ") || checkChar.equals("els")
						|| checkChar.equals("if(") || checkChar.equals("try") || checkChar.equals("cat")
						|| checkChar.equals("swi")) {
					bracketCount++;
					blnFlag = true;
				}
			}
		}
		return bracketCount;
	}

	private static boolean checkBlock(String strLine) {
		int i = strLine.indexOf('/');
		boolean temp = blockComment;
		if (i > -1) {
			if (i + 1 < strLine.length()) {
				if (strLine.charAt(i + 1) == '*') {
					// Block Comment Detected
					temp = true;
				}

			}
		}
		return temp;
	}

	/*
	 * Find the longest source strLine of Java code in the data structure program
	 */
	private int findMaxJavaLineLength() {
		int max = 0;
		for (JavaLine strLine : program) {
			int lineLength = strLine.getJavaLineLength();
			if (lineLength > max)
				max = lineLength;
		}
		return max;
	}

	/*
	 * Print the Java program held in program with // comments indented vertical at
	 * col position pos
	 * 
	 * @param toPos Start // comment at column toPos
	 */
	private void printIndentedProgram(int pos) {
		for (JavaLine strLine : program) {
			System.out.println(strLine.returnLineWithCommentAt(pos));
		}
	}
	
	public static boolean getBlockComment()
	{
		return blockComment;
	}
	
	public static boolean getBinFlag()
	{
		return blnFlag;
	}
}
