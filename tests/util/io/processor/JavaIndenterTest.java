package util.io.processor;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import executionflow.util.FileUtils;
import util.io.manager.TextFileManager;

class JavaIndenterTest {
	
	private static final Path noFormatedFile;
	private static final Path formatedFile;
	
	static {
		Path thisFolder = Path.of(".", "tests", "util", "io", "processor");
		
		noFormatedFile = thisFolder.resolve("no-formated-file.txt");
		formatedFile = thisFolder.resolve("formated-file.txt");
	}

	@Test
	void testIndentFile() throws IOException {
		JavaIndenter indenter = new JavaIndenter();
		
		TextFileManager txtManager = new TextFileManager(noFormatedFile, StandardCharsets.ISO_8859_1);
		List<String> noFormatedFileContent = txtManager.readLines();
		
		txtManager = new TextFileManager(formatedFile, StandardCharsets.ISO_8859_1);
		List<String> formatedFileContent = txtManager.readLines();
		
		List<String> output = indenter.format(noFormatedFileContent);
		
		//FileUtils.printFileWithLines(noFormatedFileContent);
		System.out.println("@");FileUtils.printFileWithLines(output);System.out.println("@");
		
		
		
		assertEquals(formatedFileContent, output);
	}
}
