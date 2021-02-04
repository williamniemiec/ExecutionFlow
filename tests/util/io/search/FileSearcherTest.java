package util.io.search;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.Test;

public class FileSearcherTest {

	private static final Path testsFolder;
	
	static {
		testsFolder = Path.of(".", "tests");
	}
	
	@Test
	public void test() throws IOException {
		FileSearcher searcher = new FileSearcher(testsFolder);
		Path thisFile = testsFolder.resolve(Path.of("util", "io", "search", "FileSearcherTest.java")).toAbsolutePath();
		assertEquals(thisFile, searcher.search("FileSearcherTest.java"));
	}

}
