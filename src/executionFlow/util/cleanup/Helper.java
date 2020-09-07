package executionFlow.util.cleanup;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author		Murilo Wolfart
 * @see			https://bitbucket.org/mwolfart/trgeneration/src/master/
 */
public class Helper {
	private static List<String> regexReservedChars = new ArrayList<String>(Arrays.asList(
			"{", "}", "\\", "\"", "(", ")"
			));

	public static boolean lineContainsReservedChar(String line, String ch) {
		if (regexReservedChars.contains(ch)) {
			ch = "\\" + ch;
		}
		return line.matches(".*"+ch+"(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$).*");
	}
	
	public static boolean lineContainsReservedWord(String line, String word) {
		return line.matches(".*\\b"+word+"\\b(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$).*");
	}
	
	public static int getIndexOfReservedString(String text, String lookup) {
		Pattern p = Pattern.compile("\\b"+lookup+"\\b(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$).*");
		Matcher m = p.matcher(text);
		int idx = (m.find() ? m.start() : -1);
		return idx;
	}
	
	public static int getIndexOfReservedChar(String text, String lookup) {
		if (regexReservedChars.contains(lookup)) {
			lookup = "\\" + lookup;
		}
		
		Pattern p = Pattern.compile(lookup+"(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$).*");
		Matcher m = p.matcher(text);
		int match = (m.find() ? m.start() : -1);
		return match;
	}
	
	public static int parseInt(String strInt) {
		int result = -1;
		try {
			Integer.parseInt(strInt);
		} catch(NumberFormatException e) {
		}
		return result;
	}
	
	public static List<Integer> incOneToAll(List<Integer> items) {
		List<Integer> newlist = new ArrayList<>();
		for(Integer item : items) {
			newlist.add(item + 1);
		}
		return newlist;
	}
	
	public static void createDir(String dirPath) {
		File dir = new File(dirPath);
		if (dir.exists()) return;
		if (!dir.mkdir()) {
			System.err.println("Could not create directory " + dirPath);
			System.exit(2);
		}
	}
}
