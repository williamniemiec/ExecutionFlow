package wniemiec.executionflow.io;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ClassPathSearcherTest {

	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private final Path thisSrcFile;
	private final Path thisBinFile;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	public ClassPathSearcherTest() {
		thisSrcFile = normalizePath(Path.of(".", "src", "test", "java", "wniemiec", 
						   				 "executionflow", "io", 
						   				 "ClassPathSearcherTest.java"));
		
		thisBinFile = normalizePath(Path.of(".", "target", "test-classes", 
											"wniemiec", "executionflow", "io",
											"ClassPathSearcherTest.class"));
	}
	
	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	@Test
	void testSearchSrcPath() throws IOException {
		Path file = ClassPathSearcher.findSrcPath("wniemiec.executionflow.io" + 
												  ".ClassPathSearcherTest(int)");
		
		Assertions.assertEquals(thisSrcFile, file);
	}
	
	@Test
	void testSearchBinPath() throws IOException {
		Path file = ClassPathSearcher.findBinPath("wniemiec.executionflow.io" + 
												  ".ClassPathSearcherTest(int)");
		
		Assertions.assertEquals(thisBinFile, file);
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	private Path normalizePath(Path path) {
		return path.normalize().toAbsolutePath();
	}
}
