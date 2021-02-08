package util.io.manager;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class TextFileManagerTest {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static Path txtFile;
	private static List<String> content;
	
	
	//-------------------------------------------------------------------------
	//		Initialization blocks
	//-------------------------------------------------------------------------
	static {
		Path tmpDir = Path.of(System.getProperty("java.io.tmpdir"));
		
		txtFile = tmpDir.resolve("txt-tmp-file.txt");
		
		content = List.of("hello", "world!");
	}
	
	
	//-------------------------------------------------------------------------
	//		Test hooks
	//-------------------------------------------------------------------------
	@Before
	public void beforeEachTest() throws IOException {
		Files.deleteIfExists(txtFile);
	}
	
	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------|
	@Test
	public void testWriteAndRead() throws IOException {
		TextFileManager txtManager = new TextFileManager(txtFile, StandardCharsets.ISO_8859_1);
		txtManager.writeLines(content);
		
		List<String> fileContent = txtManager.readLines();
		
		assertEquals(content, fileContent);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testConstructorWithNullFile() {
		new TextFileManager(null, StandardCharsets.ISO_8859_1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testConstructorWithNullEncoding() {
		new TextFileManager(txtFile, null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testWriteNullLines() throws IOException {
		TextFileManager txtManager = new TextFileManager(txtFile, StandardCharsets.ISO_8859_1);
		txtManager.writeLines(null);
	}
}
