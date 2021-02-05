package util.io.search;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.Test;

import util.io.search.FileSearcher;

public class FileSearcherTest {

	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final Path testsFolder;
	
	
	//-------------------------------------------------------------------------
	//		Initialization block
	//------------------------------------------------------------------------
	static {
		testsFolder = Path.of(".", "tests");
	}
	
	
	//-------------------------------------------------------------------------
	//		Tests
	//------------------------------------------------------------------------
	@Test
	public void testSearchFile() throws IOException {
		FileSearcher searcher = new FileSearcher(testsFolder);
		Path thisFile = testsFolder.resolve(Path.of("util", "io", "search", 
													"FileSearcherTest.java"));
		
		assertEquals(thisFile.toAbsolutePath(), searcher.search("FileSearcherTest.java"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorNullWorkingDirectory() {
		new FileSearcher(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSearchNullFilename() throws IOException {
		FileSearcher searcher = new FileSearcher(testsFolder);
		
		searcher.search(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSearchEmptyFilename() throws IOException {
		FileSearcher searcher = new FileSearcher(testsFolder);
		
		searcher.search("");
	}
}
