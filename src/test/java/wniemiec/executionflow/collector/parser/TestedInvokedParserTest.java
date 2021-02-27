package wniemiec.executionflow.collector.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.channels.InterruptedByTimeoutException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import wniemiec.executionflow.analyzer.DebuggerAnalyzer;
import wniemiec.executionflow.analyzer.DebuggerAnalyzerFactory;
import wniemiec.executionflow.invoked.Invoked;
import wniemiec.executionflow.invoked.TestedInvoked;
import wniemiec.util.logger.LogLevel;
import wniemiec.util.logger.Logger;

class TestedInvokedParserTest {

	private Invoked testedMethod;
	private Invoked testMethod;
	private TestedInvokedParser testedInvokedParser;
	private Path srcDirectory;
	private Path binDirectory;
	private TestedInvoked testedInvoked;
	
	public TestedInvokedParserTest() throws IOException {
		srcDirectory = Path.of(".", "src", "test", "resources", "wniemiec", 
				"executionflow", "collector", "parser");
		binDirectory = Path.of(".", "target", "test-classes", "wniemiec", 
				"executionflow", "collector", "parser");

		Logger.setLevel(LogLevel.WARNING);
	}
	
	private Invoked getAuxClassInvoked() {
		return new Invoked.Builder()
				.srcPath(srcDirectory.resolve("AuxClass.java"))
				.binPath(binDirectory.resolve("AuxClass.class"))
				.signature("wniemiec.executionflow.collector.parser.AuxClass.factorial(int)")
				.invocationLine(32)
				.build();
	}
	
	private Invoked getOthersInvoked() {
		return new Invoked.Builder()
				.srcPath(srcDirectory.resolve("Others.java"))
				.binPath(binDirectory.resolve("Others.class"))
				.signature("wniemiec.executionflow.collector.parser.Others.testFactorial()")
				.build();
	}
	
	@BeforeEach
	void prepare() throws InterruptedByTimeoutException, IOException {
		testedInvokedParser = new TestedInvokedParser();
	}
	
	@Test
	void testTestPath() throws InterruptedByTimeoutException, IOException {
		withTestedInvoked(getAuxClassInvoked());
		withTestMethod(getOthersInvoked());

		doParsing();
		
		assertTestPathIs(94, 96, 98, 99, 98, 99, 98, 99, 98, 99, 98, 102);
	}
	
	private void withTestMethod(Invoked testMethod) {
		this.testMethod = testMethod;
	}

	private void withTestedInvoked(Invoked testedMethod) {
		this.testedMethod = testedMethod;
	}

	private void doParsing() throws InterruptedByTimeoutException, IOException {
		testedInvoked = new TestedInvoked(testedMethod, testMethod);
		
		testedInvokedParser.parse(
				testedInvoked, 
				DebuggerAnalyzerFactory.createStandardTestPathAnalyzer(testedMethod, testMethod)
		);
	}

	private void assertTestPathIs(Integer... testPath) {
		assertEquals(
				List.of(Arrays.asList(testPath)), 
				testedInvokedParser.getTestPathsOf(testedInvoked)
		);
	}
}
