package wniemiec.app.java.executionflow.io;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import wniemiec.app.java.ExecutionFlow;
import wniemiec.app.java.executionflow.io.ClassPathSearcher;

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
		thisSrcFile = normalizePath(ExecutionFlow.getCurrentProjectRoot().resolve(
				Path.of("src", "test", "java", "wniemiec", "app", "java", "executionflow", 
						"io", "ClassPathSearcherTest.java"))
		);
		
		thisBinFile = normalizePath(ExecutionFlow.getAppTargetPath().resolve(
				Path.of("test-classes", "wniemiec", "app", "java", "executionflow",
						"io", "ClassPathSearcherTest.class"))
		);
	}
	
	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	@Test
	void testSearchSrcPath() throws IOException {
		Path file = ClassPathSearcher.findSrcPath("wniemiec.app.java.executionflow.io" + 
												  ".ClassPathSearcherTest");
		
		Assertions.assertEquals(thisSrcFile, file);
	}
	
	@Test
	void testSearchBinPath() throws IOException {
		Path file = ClassPathSearcher.findBinPath("wniemiec.app.java.executionflow.io" + 
												  ".ClassPathSearcherTest");
		
		Assertions.assertEquals(thisBinFile, file);
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	private Path normalizePath(Path path) {
		return path.normalize().toAbsolutePath();
	}
}
