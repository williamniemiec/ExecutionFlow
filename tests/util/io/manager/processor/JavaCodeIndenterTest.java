package util.io.manager.processor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import util.io.manager.TextFileManager;
import util.io.processor.IndentationType;
import util.io.processor.Indenter;
import util.io.processor.JavaCodeIndenter;

public class JavaCodeIndenterTest {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final Path noFormatedFile;
	private static final Path formatedFile;
	private static final Path formatedFileWith4Spaces;
	
	
	//-------------------------------------------------------------------------
	//		Initialization block
	//-------------------------------------------------------------------------
	static {
		Path thisFolder = Path.of(".", "tests", "util", "io", "processor");
		
		noFormatedFile = thisFolder.resolve("no-formated-file.txt");
		formatedFile = thisFolder.resolve("formated-file.txt");
		formatedFileWith4Spaces = thisFolder.resolve("formated-file-with-4-spaces.txt");
	}

	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	@Test
	public void testIndentFile() throws IOException {
		Indenter indenter = new JavaCodeIndenter();
		List<String> originalIndentedFileContent = extractLinesFromIndentedFile(formatedFile);
		List<String> indentedLines = indentLinesOfNonIndentedFile(indenter);
		
		assertSizeIsEqual(originalIndentedFileContent, indentedLines);
		assertLinesAreEqual(originalIndentedFileContent, indentedLines);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testConstructorNullIndenterType() {
		new JavaCodeIndenter(null, 1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testConstructorNegativeFactor() {
		new JavaCodeIndenter(IndentationType.TAB, -1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testConstructorNullIndenterTypeAndNegativeFactor() {
		new JavaCodeIndenter(null, -1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIndentNullContent() {
		JavaCodeIndenter indenter = new JavaCodeIndenter(IndentationType.TAB, 1);
		
		indenter.indent(null);
	}
	
	public void testIndentBlankContent() {
		JavaCodeIndenter indenter = new JavaCodeIndenter(IndentationType.TAB, 1);
		
		assertEquals(new ArrayList<String>(), indenter.indent(new ArrayList<String>()));
	}
	
	@Test
	public void testIndentFileWithSpace() throws IOException {
		Indenter indenter = new JavaCodeIndenter(IndentationType.SPACE, 4);
		List<String> originalIndentedLines = extractLinesFromIndentedFile(formatedFileWith4Spaces);
		List<String> indentedLines = indentLinesOfNonIndentedFile(indenter);
		
		assertSizeIsEqual(originalIndentedLines, indentedLines);
		assertLinesAreEqual(originalIndentedLines, indentedLines);
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	private List<String> extractLinesFromIndentedFile(Path file) throws IOException {
		TextFileManager txtManager = new TextFileManager(file, StandardCharsets.ISO_8859_1);

		return txtManager.readLines();
	}

	private List<String> indentLinesOfNonIndentedFile(Indenter indenter) throws IOException {
		TextFileManager txtManager = new TextFileManager(noFormatedFile, 
														 StandardCharsets.ISO_8859_1);
		List<String> noFormatedFileContent = txtManager.readLines();
		
		return indenter.indent(noFormatedFileContent);
	}
	
	private void assertSizeIsEqual(List<String> originalContent, List<String> indentedContent) {
		if (originalContent.size() != indentedContent.size())
			fail("The indented file is different in size from the original file");
	}
	
	private void assertLinesAreEqual(List<String> formatedFileContent, List<String> output) {
		for (int i = 0; i < formatedFileContent.size(); i++) {
			if (formatedFileContent.get(i).isBlank())
				continue;
			
			assertEquals("Line: " + (i+1), formatedFileContent.get(i), output.get(i));			
		}
	}
}
