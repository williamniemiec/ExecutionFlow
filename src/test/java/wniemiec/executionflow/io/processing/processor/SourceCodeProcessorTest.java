package wniemiec.executionflow.io.processing.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

import wniemiec.util.console.ConsoleFilePrinter;
import wniemiec.util.io.manager.TextFileManager;

public abstract class SourceCodeProcessorTest {

	private static final Path RESOURCES_FOLDER;
	
	static {
		RESOURCES_FOLDER = Path.of(
				".", "src", "test", "resources", "wniemiec", 
				"executionflow", "io", "processing", 
				"processor"
		).normalize();
	}
	
	protected void testProcessorOnFile(String filename) throws IOException {
		List<String> ansTxt = readAnswerFile(filename);
		List<String> testTxt = readTestFile(filename);
		List<String> procTxt = processSourceCode(testTxt);
//		ConsoleFilePrinter.printFileWithLines(procTxt);
		assertHasEqualNumberOfLines(ansTxt, procTxt);
		assertProcessedTextIsAccordingToExpected(ansTxt, procTxt);
	}
	
	protected void assertProcessedTextIsAccordingToExpected(List<String> answerText, 
															List<String> processedText) {
		for (int i = 0; i < processedText.size(); i++) { 
			assertEquals(
					normalizeRandomVariableName(answerText.get(i)), 
					normalizeRandomVariableName(processedText.get(i))
			);
		}
	}
	
	private String normalizeRandomVariableName(String line) {
		return	line.trim()
				.replaceAll("Throwable _[0-9A-z]+", "_")
				.replaceAll("int _[0-9A-z]+[\\s\\t]*=", "int _=")
				.replaceAll("_[0-9A-z]+\\+\\+", "_++")
				.replaceAll("[\\s\\t]+"," ");
	}

	protected void assertHasEqualNumberOfLines(List<String> answerText, 
											   List<String> processedText) {
		if (processedText.size() == answerText.size())
			return;
		
		fail("The number of lines in the processed file is different from " + 
			 "the original file");
	}

	protected List<String> processSourceCode(List<String> sourceCode) {
		SourceCodeProcessor processor = getProcessorFor(sourceCode);
		
		return processor.processLines();
	}
	
	protected abstract SourceCodeProcessor getProcessorFor(List<String> sourceCode);
	
	
	protected List<String> readAnswerFile(String name) throws IOException {
		Path ansFile = RESOURCES_FOLDER.resolve(name + "-answer.txt");
		TextFileManager txtManager = new TextFileManager(ansFile, StandardCharsets.ISO_8859_1);
		
		return txtManager.readLines();
	}
	
	protected List<String> readTestFile(String name) throws IOException {
		Path ansFile = RESOURCES_FOLDER.resolve(name + "-test.txt");
		TextFileManager txtManager = new TextFileManager(ansFile, StandardCharsets.ISO_8859_1);
		
		return txtManager.readLines();
	}
}
