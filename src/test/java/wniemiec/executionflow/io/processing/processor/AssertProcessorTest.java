package wniemiec.executionflow.io.processing.processor;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

public class AssertProcessorTest extends SourceCodeProcessorTest {

	@Test
	public void testInlineAssert() throws IOException {
		List<String> ansTxt = readAnswerFile("inline-assert");
		List<String> testTxt = readTestFile("inline-assert");
		List<String> procTxt = processSourceCode(testTxt);
		
		assertHasEqualNumberOfLines(ansTxt, procTxt);
		assertProcessedTextIsAccordingToExpected(ansTxt, procTxt);
	}
	
	@Test
	public void testMultlineAssert() throws IOException {
		List<String> ansTxt = readAnswerFile("multiline-assert");
		List<String> testTxt = readTestFile("multiline-assert");
		List<String> procTxt = processSourceCode(testTxt);
		
		assertHasEqualNumberOfLines(ansTxt, procTxt);
		assertProcessedTextIsAccordingToExpected(ansTxt, procTxt);
	}
	
	@Test
	public void testLastCurlyBracketSameLineAssert() throws IOException {
		List<String> ansTxt = readAnswerFile("last-curly-bracket-same-line-assert");
		List<String> testTxt = readTestFile("last-curly-bracket-same-line-assert");
		List<String> procTxt = processSourceCode(testTxt);
		
		assertHasEqualNumberOfLines(ansTxt, procTxt);
		assertProcessedTextIsAccordingToExpected(ansTxt, procTxt);
	}
	
	@Test
	public void testAssertInTry() throws IOException {
		List<String> ansTxt = readAnswerFile("assert-in-try");
		List<String> testTxt = readTestFile("assert-in-try");
		List<String> procTxt = processSourceCode(testTxt);
		
		assertHasEqualNumberOfLines(ansTxt, procTxt);
		assertProcessedTextIsAccordingToExpected(ansTxt, procTxt);
	}
	
	
	@Override
	protected SourceCodeProcessor getProcessorFor(List<String> sourceCode) {
		return new AssertProcessor(sourceCode);
	}
}
