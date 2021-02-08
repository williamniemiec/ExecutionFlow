package util.io.manager;

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
 * Responsible for handling CSV files.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 */
public class CSVFileManager {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private File csvFile;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	/**
	 * Handles CSV files. 
	 * 
	 * @param		directory Directory where CSV will be stored
	 * @param		filename CSV filename (without '.csv')
	 * 
	 * @throws		IllegalArgumentException If directory or filename is null
	 */
	public CSVFileManager(File directory, String filename) {
		if (directory == null)
			throw new IllegalArgumentException("Directory cannot be null");
		
		if (filename == null)
			throw new IllegalArgumentException("Filename cannot be null");
		
		csvFile = new File(directory, filename + ".csv");
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Reads exported CSV file and returns a Map with its content.
	 * 
	 * @param		separator Symbol that separates items
	 *
	 * @return		Matrix with CSV content
	 * 
	 * @throws		IOException If CSV file cannot be read
	 * @throws		IllegalArgumentException If separator is null
	 */
	public List<List<String>> read(String separator) throws IOException {
		if (separator == null)
			throw new IllegalArgumentException("Separator cannot be null");
		
		List<List<String>> content = new ArrayList<>();
		String line;
		
		try (BufferedReader csv = new BufferedReader(new FileReader(csvFile))) {
			while ((line = csv.readLine()) != null) {
				content.add(stringToList(line, separator));
			}
		}
		
		return content;
	}
	
	private List<String> stringToList(String str, String separator) {
		return Arrays.asList(str.split(separator));
	}
	
	/**
	 * Reads exported CSV file and returns a Map with its content. Using this
	 * method, separator will be a comma.
	 *
	 * @return		Matrix with CSV content
	 * 
	 * @throws		IOException If CSV file cannot be read 
	 */
	public List<List<String>> read() throws IOException {
		return read(",");
	}
	
	/**
	 * Writes a content to a CSV file. Using this method, separator will be a 
	 * comma.
	 * 
	 * @param		content Content to be written (lines)
	 * 
	 * @throws		IOException If an error occurs while writing the file 
	 * @throws		IllegalArgumentException If content is null
	 */
	public void writeLine(List<String> content) throws IOException {
		writeLine(content, ",");
	}
	
	/**
	 * Writes a content to a CSV file.
	 * 
	 * @param		content Content to be written (lines)
	 * @param		separator Symbol that separates items
	 * 
	 * @throws		IOException If an error occurs while writing the file 
	 * @throws		IllegalArgumentException If content or separator is null
	 */
	public void writeLine(List<String> content, String separator) throws IOException {
		if (content == null)
			throw new IllegalArgumentException("Content cannot be null");
		
		if (separator == null)
			throw new IllegalArgumentException("Separator cannot be null");
		
		try (BufferedWriter csv = new BufferedWriter(new FileWriter(csvFile, csvFile.exists()))) {
			csv.write(listToString(content, separator));
			csv.newLine();
		}
	}
	
	private String listToString(List<String> list, String separator) {
		StringBuilder str = new StringBuilder();

		for (String element : list) {
			str.append(element);
			str.append(separator);
		}
			
		// Removes last separator
		str = str.deleteCharAt(str.length()-1);
		
		return str.toString();
	}
	
	public void delete() {
		csvFile.delete();
	}
	
	public boolean exists() {
		return csvFile.exists();
	}
	
	
	//-------------------------------------------------------------------------
	//		Getters
	//-------------------------------------------------------------------------
	public String getAbsolutePath() {
		return csvFile.getAbsolutePath();
	}
}
