package executionflow.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains methods that perform data manipulation.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 */
public class FileUtils {
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	private FileUtils() {
	}
	
	
	public static void printFileWithLines(List<String> fileContent) {
		for (int i=0; i<fileContent.size(); i++) {
			System.out.printf("%-5d\t%s\n", i+1, fileContent.get(i));
		}
	}
	
	public static String extractFilenameWithoutExtension(Path file) {
		String filenameWithExtension = file.getName(file.getNameCount()-1).toString(); 
		
		return filenameWithExtension.split("\\.")[0];
	}
	
	/**
	 * Replaces characters reserved by the operating system, for file and 
	 * directory names, using the following mapping:
	 * <table border=1 width='100%'>
	 * 	<tr align='center'>
	 * 		<th>Original</th>
	 * 		<th>New</th>
	 * 	</tr>
	 * 	<tr align='center'>
	 * 		<td>&lt;</td>
	 * 		<td>(</td>
	 * 	</tr>
	 * 	<tr align='center'>
	 * 		<td>&gt;</td>
	 * 		<td>)</td>
	 * 	</tr>
	 * 	<tr align='center'>
	 * 		<td>\</td>
	 * 		<td>-</td>
	 * 	</tr>
	 * <tr align='center'>
	 * 		<td>/</td>
	 * 		<td>-</td>
	 * 	</tr>
	 * 	<tr align='center'>
	 * 		<td>:</td>
	 * 		<td>;</td>
	 * 	</tr>
	 * 	<tr align='center'>
	 * 		<td>"</td>
	 * 		<td>'</td>
	 * 	</tr>
	 * 	<tr align='center'>
	 * 		<td>|</td>
	 * 		<td>-</td>
	 * 	</tr>
	 * 	<tr align='center'>
	 * 		<td>?</td>
	 * 		<td>+</td>
	 * 	</tr>
	 * 	<tr align='center'>
	 * 		<td>*</td>
	 * 		<td>+</td>
	 * 	</tr>
	 * <table>
	 * 
	 * @param		str Path, filename or directory name to be processed
	 * 
	 * @return		String without reserved characters
	 */
	public static String replaceReservedCharacters(String str) {
		String processedStr = str;
		
		processedStr = processedStr.replaceAll("\\<", "(");
		processedStr = processedStr.replaceAll("\\>", ")");
		processedStr = processedStr.replaceAll("\\/", "-");
		processedStr = processedStr.replaceAll("\\\\", "-");
		processedStr = processedStr.replaceAll("\\:", ";");
		processedStr = processedStr.replaceAll("\"", "'");
		processedStr = processedStr.replaceAll("\\|", "-");
		processedStr = processedStr.replaceAll("\\?", "+");
		processedStr = processedStr.replaceAll("\\*", "+");
		
		return processedStr;
	}
	
	/**
	 * Generates argument file from a list of paths.
	 * 
	 * @param		dir Directory where the file will be stored
	 * @param		filename Argument file name
	 * @param		paths List of paths
	 * 
	 * @return		Argument file
	 * 
	 * @throws 		IOException If an error occurs while writing the file
	 * 
	 * @see			https://docs.oracle.com/javase/9/tools/java.htm
	 * #JSWOR-GUID-4856361B-8BFD-4964-AE84-121F5F6CF111
	 */
	public static Path createArgumentFile(Path dir, String filename, List<Path> paths) 
			throws IOException {
		Path argumentFile = dir.resolve(filename);
		
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(argumentFile.toFile()))) {
			Path path;
			
			bw.write("\"\\");
			bw.newLine();
			
			// Writes list of paths
			for (int i=0; i<paths.size()-1; i++) {
				path = paths.get(i);
				
				bw.write(path.toAbsolutePath().toString().replaceAll("\\\\", "\\\\\\\\"));
				bw.write(";\\");
				bw.newLine();
			}
			
			path = paths.get(paths.size()-1);
			bw.write(path.toAbsolutePath().toString().replaceAll("\\\\", "\\\\\\\\"));
			bw.write("\"");
		}
		
		return argumentFile;
	}
}
