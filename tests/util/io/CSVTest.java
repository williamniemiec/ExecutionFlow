package util.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Test;

import util.io.manager.CSVFileManager;

public class CSVTest {

	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final CSVFileManager csv;
	
	
	//-------------------------------------------------------------------------
	//		Initialization block
	//-------------------------------------------------------------------------
	static {
		csv = new CSVFileManager(
				new File(System.getProperty("java.io.tmpdir")), 
				"csv-test"
		);
	}
	
	
	//-------------------------------------------------------------------------
	//		Test hooks
	//-------------------------------------------------------------------------
	@After
	public void clean() {
		csv.delete();
	}
	
	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	@Test
	public void testWriteAndRead() throws IOException {
		List<String> firstLine = List.of("hello", "world");
		List<String> secondLine = List.of("world", "hello");
		List<List<String>> content = List.of(firstLine, secondLine);
		
		csv.writeLine(firstLine);
		csv.writeLine(secondLine);
		
		assertEquals(content, csv.read());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testConstructorWithDirectoryNull() {
		new CSVFileManager(null, "filename");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testConstructorWithFilenameNull() {
		new CSVFileManager(new File("."), null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testConstructorWithDirectoryAndFilenameNull() {
		new CSVFileManager(null, null);
	}
	
	@Test
	public void testWriteAndReadWithSpecifiedDelimiter() throws IOException {
		List<String> firstLine = List.of("hello", "world");
		List<String> secondLine = List.of("world", "hello");
		List<List<String>> content = List.of(firstLine, secondLine);
		String delimiter = ";";
		
		csv.writeLine(firstLine, delimiter);
		csv.writeLine(secondLine, delimiter);
		
		assertEquals(content, csv.read(delimiter));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testWriteWithNullSpecifiedDelimiter() throws IOException {
		List<String> firstLine = List.of("hello", "world");
		List<String> secondLine = List.of("world", "hello");
		String delimiter = null;
		
		csv.writeLine(firstLine, delimiter);
		csv.writeLine(secondLine, delimiter);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testReadWithNullSpecifiedDelimiter() throws IOException {
		List<String> firstLine = List.of("hello", "world");
		List<String> secondLine = List.of("world", "hello");
		String delimiter = ";";
		
		csv.writeLine(firstLine, delimiter);
		csv.writeLine(secondLine, delimiter);
		
		csv.read(null);
	}
	
	@Test
	public void testExists() throws IOException {
		List<String> firstLine = List.of("hello", "world");
		List<String> secondLine = List.of("world", "hello");
		
		csv.writeLine(firstLine);
		csv.writeLine(secondLine);
		
		assertTrue(csv.exists());
	}
	
	@Test
	public void testNotExists() throws IOException {
		csv.delete();
		
		assertFalse(csv.exists());
	}
}
