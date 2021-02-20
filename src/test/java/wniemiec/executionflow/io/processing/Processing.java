package wniemiec.executionflow.io.processing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

import wniemiec.util.io.manager.TextFileManager;

public abstract class Processing {

	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
	protected final Path workingDirectory;
	
	
	//-----------------------------------------------------------------------
	//		Constructors
	//-----------------------------------------------------------------------
	protected Processing(Path relativePath) {
		Path thisFolder = Path.of(
				".", "src", "test", "resources", "wniemiec", 
				"executionflow", "io", "processing"
		);
		
		workingDirectory = thisFolder.resolve(relativePath).normalize();
	}
	
	protected Processing() {
		this(Path.of("."));
	}
	
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------|
	protected void testProcessorOnFile(String filename) throws IOException {
		List<String> ansTxt = readAnswerFile(filename);
		List<String> procTxt = processSourceCodeFrom(filename);
		for (String line : procTxt) {
			System.out.println(line);
		}
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
	
	private List<String> readAnswerFile(String name) throws IOException {
		Path ansFile = workingDirectory.resolve(name + "-answer.txt");
		TextFileManager txtManager = new TextFileManager(ansFile, StandardCharsets.ISO_8859_1);
		
		return txtManager.readLines();
	}
	
	abstract protected List<String> processSourceCodeFrom(String filename) throws IOException;
	
	protected List<String> readTestFile(String name) throws IOException {
		Path ansFile = workingDirectory.resolve(name + "-test.txt");
		TextFileManager txtManager = new TextFileManager(ansFile, StandardCharsets.ISO_8859_1);
		
		return txtManager.readLines();
	}
}
