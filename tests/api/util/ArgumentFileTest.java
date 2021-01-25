package api.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ArgumentFileTest {

	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static Path workingDirectory;
	private static String filename;
	private static List<Path> paths;
	
	
	//-------------------------------------------------------------------------
	//		Initialization block
	//-------------------------------------------------------------------------
	static {
		workingDirectory = Path.of(System.getProperty("java.io.tmpdir"));
		filename = "argfile-test.txt";
		paths = List.of(
				Path.of("C:\\foo\\bar\\somefile.class"),
				Path.of("C:\\bar\\foo\\file with blank spaces.class")
		);
	}
	
	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	@Test
	public void testArgumentFileWithTwoPaths() throws IOException {
		Path argFile = ArgumentFile.createArgumentFile(workingDirectory, filename, paths);
		assertArgumentFile(argFile);
	}

	private void assertArgumentFile(Path argFile) throws FileNotFoundException, IOException {
		List<String> expectedLines = List.of(
				"\"\\",
				"C:\\\\foo\\\\bar\\\\somefile.class;\\",
				"C:\\\\bar\\\\foo\\\\file with blank spaces.class\""
		);
		
		List<String> argfileLines = readFile(argFile);
		
		assertEquals(expectedLines, argfileLines);
	}

	private List<String> readFile(Path argFile) throws IOException {
		List<String> lines = new ArrayList<>();
		String currentLine;
		
		try (BufferedReader br = Files.newBufferedReader(argFile, StandardCharsets.UTF_8)) {
			while ((currentLine = br.readLine()) != null) {
				lines.add(currentLine);
			}
		}
		
		return lines;
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testArgumentFileWithNullWorkingDirectory() throws IOException {
		ArgumentFile.createArgumentFile(null, filename, paths);
		fail("IllegalArgumentException should be thrown");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testArgumentFileWithNullFilename() throws IOException {
		ArgumentFile.createArgumentFile(workingDirectory, null, paths);
		fail("IllegalArgumentException should be thrown");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testArgumentFileWithEmptyFilename() throws IOException {
		ArgumentFile.createArgumentFile(workingDirectory, "", paths);
		fail("IllegalArgumentException should be thrown");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testArgumentFileWithNullPaths() throws IOException {
		ArgumentFile.createArgumentFile(workingDirectory, filename, null);
		fail("IllegalArgumentException should be thrown");
	}
}
