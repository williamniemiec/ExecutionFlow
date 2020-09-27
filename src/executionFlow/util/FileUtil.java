package executionFlow.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


/**
 * Contains methods that perform data manipulation.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 */
public class FileUtil 
{
	/**
	 * Gets all lines from a file and puts them in a list.
	 * 
	 * @param		file Base file
	 * @param		encode File encoding
	 * 
	 * @return		List containing all lines of the file
	 * 
	 * @throws		IOException If an I/O error occurs opening the file
	 */
	public static List<String> getLines(Path file, Charset encode) throws IOException
	{
		List<String> lines = new ArrayList<>();
		String currentLine;
		
		
		try (BufferedReader br = Files.newBufferedReader(file, encode)) {
			while ( (currentLine = br.readLine()) != null ) {
				lines.add(currentLine);
			}
		}
		
		return lines;
	}
	
	/**
	 * Writes all items in a string list to a file.
	 * 
	 * @param		lines Content to be written
	 * @param		file Output file
	 * @param		encode File encoding 
	 * @throws		IOException If an I/O error occurs opening the file
	 */
	public static void putLines(List<String> lines, Path file, Charset encode) throws IOException
	{
		try (BufferedWriter bw = Files.newBufferedWriter(file, encode)) {
			for (String line : lines) {
				bw.write(line);
				bw.newLine();
			}
		}
	}
	
	/**
	 * Copies files to a directory.
	 * 
	 * @param		files Files to be copied
	 * @param		output Destination directory
	 * 
	 * @throws		IOException If a failure occurs during copying
	 */
	public static void putFilesInFolder(List<Path> files, Path output) throws IOException
	{
		if (!Files.exists(output)) {
			Files.createDirectories(output);
		}
		
		for (Path p : files) {
			Path target = output.resolve(p.getFileName());
			
			
			if (Files.notExists(target))
				Files.copy(p, target);
		}
	}
}
