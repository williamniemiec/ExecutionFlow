package util.io.path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import util.io.manager.TextFileManager;

public class ArgumentFileTest {

	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
	private static final String FILENAME;
	private static final Path WORKING_DIRECTORY;
	private static final Path THIS_FOLDER;
	private static final List<Path> PATHS;
	private ArgumentFile argumentFile;
	
	
	//-----------------------------------------------------------------------
	//		Initialization blocks
	//-----------------------------------------------------------------------
	static {
		THIS_FOLDER = Path.of(".", "tests", "util", "io", "path");
		
		WORKING_DIRECTORY = Path.of(System.getProperty("java.io.tmpdir"));
		
		FILENAME = "argfile-test";
		
		PATHS = List.of(
				Path.of("C:", "Foo", "Bar", "file1.jar"),
				Path.of("C:", "Foo", "Bar", "file2.jar"),
				Path.of("C:", "Foo")
		);
	}
	
	
	//-----------------------------------------------------------------------
	//		Test hooks
	//-----------------------------------------------------------------------
	@Before
	public void beforeEachTest() {
		argumentFile = null;
	}
	
	@After
	public void afterEachTest() throws IOException {
		if (argumentFile == null)
			return;
		
		argumentFile.delete();
	}
	
	
	//-----------------------------------------------------------------------
	//		Tests
	//-----------------------------------------------------------------------
	@Test
	public void testCreateArgumentFile() throws IOException {		
		argumentFile = new ArgumentFile(WORKING_DIRECTORY, FILENAME);
		argumentFile.create(PATHS);
			
		assertArgumentFile(argumentFile.getFile());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testConstructorWithNullWorkingDirectory() {
		new ArgumentFile(null, FILENAME);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testConstructorWithNullFilename() {
		new ArgumentFile(WORKING_DIRECTORY, null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testConstructorWithEmptyFilename() {
		new ArgumentFile(WORKING_DIRECTORY, "");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testConstructorWithNullWorkingDirectoryAndFilename() {
		new ArgumentFile(null, null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testCreateArgumentFileWithNullPaths() throws IOException {
		argumentFile = new ArgumentFile(WORKING_DIRECTORY, FILENAME);
		argumentFile.create(null);
	}
	
	@Test
	public void testExists() throws IOException {
		argumentFile = new ArgumentFile(WORKING_DIRECTORY, FILENAME);
		argumentFile.create(PATHS);
				
		assertTrue(argumentFile.exists());
	}
	
	@Test
	public void testDelete() throws IOException {
		argumentFile = new ArgumentFile(WORKING_DIRECTORY, FILENAME);
		argumentFile.create(PATHS);
		argumentFile.delete();
		
		assertFalse(argumentFile.exists());
	}
	
	
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	private void assertArgumentFile(Path argumentFile) throws IOException {
		Path expectedArgFile = THIS_FOLDER.resolve("expected-arg-file-content.txt");
		
		assertEquals(getFileContent(expectedArgFile), getFileContent(argumentFile));
	}
	
	private List<String> getFileContent(Path file) throws IOException {
		TextFileManager txtManager = new TextFileManager(file, StandardCharsets.ISO_8859_1);
		
		return txtManager.readLines();
	}
}
