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
 * @version		2.0.0
 * @since		2.0.0
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
	 *
	 * @return		Matrix with CSV content:
	 * 
	 * @throws		IOException If CSV file cannot be read 
	 */
	public static List<List<String>> read(File filepath) throws IOException
	{
		List<List<String>> content = new ArrayList<>();
		String line;
		
		
		try (BufferedReader csv = new BufferedReader(new FileReader(filepath))) {
			while ((line = csv.readLine()) != null) {
				content.add(Arrays.asList(line.split(",")));
			}
		}
		
		return content;
	}
	
	/**
	 * Writes a content to a CSV file.
	 * 
	 * @param		content Content to be written
	 * @param		Path where CSV file will be saved
	 * 
	 * @throws		IOException If an error occurs while writing the file 
	 */
	public static void write(List<String> content, File output) throws IOException
	{
		try (BufferedWriter csv = new BufferedWriter(new FileWriter(output, true))) {
			StringBuilder sb = new StringBuilder();


			for (String element : content) {
				sb.append(element);
				sb.append(",");
			}
				
			// Removes last comma
			sb = sb.deleteCharAt(sb.length()-1);	
			
			// Writes the content to CSV file
			csv.write(sb.toString());
			csv.newLine();
		}
	}
}
