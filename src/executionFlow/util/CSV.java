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
public class CSV
{
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
	public static List<List<String>> read(File filepath, String separator) throws IOException
	{
		List<List<String>> content = new ArrayList<>();
		String line;
		
		
		try (BufferedReader csv = new BufferedReader(new FileReader(filepath))) {
			while ((line = csv.readLine()) != null) {
				content.add(Arrays.asList(line.split(separator)));
			}
		}
		
		return content;
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
	public static List<List<String>> read(File filepath) throws IOException
	{
		return read(filepath, ",");
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
	public static void write(List<String> content, File output, String separator) throws IOException
	{
		try (BufferedWriter csv = new BufferedWriter(new FileWriter(output, true))) {
			StringBuilder sb = new StringBuilder();


			for (String element : content) {
				sb.append(element);
				sb.append(separator);
			}
				
			// Removes last comma
			sb = sb.deleteCharAt(sb.length()-1);	
			
			// Writes the content to CSV file
			csv.write(sb.toString());
			csv.newLine();
		}
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
	public static void write(List<String> content, File output) throws IOException
	{
		write(content, output, ",");
	}
}
