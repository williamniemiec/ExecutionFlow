package executionFlow.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Responsible for reading and writing CSV files.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 */
public class CSV {
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Reads exported CSV file and returns a Map with its content.
	 * 
	 * @param		filepath CSV file location
	 * @param		separator Symbol that separates items
	 *
	 * @return		Matrix with CSV content
	 * 
	 * @throws		IOException If CSV file cannot be read 
	 */
	public static List<List<String>> read(File filepath, String separator) 
			throws IOException {
		List<List<String>> content = new ArrayList<>();
		String line;
		
		try (BufferedReader csv = new BufferedReader(new FileReader(filepath))) {
			while ((line = csv.readLine()) != null) {
				content.add(stringToList(line, separator));
			}
		}
		
		return content;
	}
	
	private static List<String> stringToList(String str, String separator) {
		return Arrays.asList(str.split(separator));
	}
	
	/**
	 * Reads exported CSV file and returns a Map with its content. Using this
	 * method, separator will be a comma.
	 * 
	 * @param		filepath CSV file location
	 *
	 * @return		Matrix with CSV content
	 * 
	 * @throws		IOException If CSV file cannot be read 
	 */
	public static List<List<String>> read(File filepath) throws IOException {
		return read(filepath, ",");
	}
	
	/**
	 * Writes a content to a CSV file. Using this method, separator will be a 
	 * comma.
	 * 
	 * @param		content Content to be written (lines)
	 * @param		Path where CSV file will be saved
	 * 
	 * @throws		IOException If an error occurs while writing the file 
	 */
	public static void write(List<String> content, File output) 
			throws IOException {
		write(content, output, ",");
	}
	
	/**
	 * Writes a content to a CSV file.
	 * 
	 * @param		content Content to be written (lines)
	 * @param		Path where CSV file will be saved
	 * @param		separator Symbol that separates items
	 * 
	 * @throws		IOException If an error occurs while writing the file 
	 */
	public static void write(List<String> content, File output, String separator) 
			throws IOException {
		try (BufferedWriter csv = new BufferedWriter(new FileWriter(output, true))) {
			csv.write(listToString(content, separator));
			csv.newLine();
		}
	}
	
	private static String listToString(List<String> list, String separator) {
		StringBuilder str = new StringBuilder();

		for (String element : list) {
			str.append(element);
			str.append(separator);
		}
			
		// Removes last separator
		str = str.deleteCharAt(str.length()-1);
		
		return str.toString();
	}
}
