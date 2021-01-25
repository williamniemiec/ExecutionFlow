package api.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Responsible for generating argument files.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * 
 * @see			https://docs.oracle.com/javase/9/tools/java.htm
 * #JSWOR-GUID-4856361B-8BFD-4964-AE84-121F5F6CF111
 */
public class ArgumentFile {
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	private ArgumentFile() {
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
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
	 * @throws		IllegalArgumentException If working directory or filename
	 * is null or if paths is null or empty
	 * 
	 * @see			https://docs.oracle.com/javase/9/tools/java.htm
	 * #JSWOR-GUID-4856361B-8BFD-4964-AE84-121F5F6CF111
	 */
	public static Path createArgumentFile(Path dir, String filename, List<Path> paths) 
			throws IOException {
		if (dir == null)
			throw new IllegalArgumentException("Working directory cannot be null");
		
		if ((filename == null) || filename.isBlank())
			throw new IllegalArgumentException("File name cannot be empty");
		
		if ((paths == null) || paths.isEmpty())
			throw new IllegalArgumentException("Paths cannot be empty");
		
		Path argumentFile = dir.resolve(filename);
		
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(argumentFile.toFile()))) {
			writeHeader(bw);
			writeBody(paths, bw);
			writeFooter(paths, bw);
		}
		
		return argumentFile;
	}

	private static void writeBody(List<Path> paths, BufferedWriter bw) throws IOException {
		for (int i=0; i<paths.size()-1; i++) {
			Path path = paths.get(i);
			
			bw.write(path.toAbsolutePath().toString().replaceAll("\\\\", "\\\\\\\\"));
			bw.write(";\\");
			bw.newLine();
		}
	}

	private static void writeFooter(List<Path> paths, BufferedWriter bw) throws IOException {
		Path path = paths.get(paths.size()-1);
		
		bw.write(path.toAbsolutePath().toString().replaceAll("\\\\", "\\\\\\\\"));
		bw.write("\"");
	}

	private static void writeHeader(BufferedWriter bw) throws IOException {
		bw.write("\"\\");
		bw.newLine();
	}
}
