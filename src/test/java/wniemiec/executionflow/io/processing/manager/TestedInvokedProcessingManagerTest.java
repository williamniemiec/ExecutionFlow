package wniemiec.executionflow.io.processing.manager;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import wniemiec.executionflow.collector.parser.TestedInvokedParser;
import wniemiec.executionflow.invoked.Invoked;
import wniemiec.executionflow.invoked.TestedInvoked;
import wniemiec.util.logger.LogLevel;
import wniemiec.util.logger.Logger;

class TestedInvokedProcessingManagerTest {

	private Invoked testedMethod;
	private Invoked testMethod;
	private Path srcDirectory;
	private Path binDirectory;
	private List<TestedInvoked> invokedCollector;
	private TestedInvokedProcessingManager invokedCollectorParser;
	private TestedInvokedParser testedInvokedParser;
	
	TestedInvokedProcessingManagerTest() {
		srcDirectory = Path.of(".", "src", "test", "resources", "auxfiles");
		binDirectory = Path.of(".", "target", "test-classes", "auxfiles");

		Logger.setLevel(LogLevel.WARNING);
	}
	
	@BeforeEach
	void prepare() {
		invokedCollector = new ArrayList<>();
	}
	
	@AfterEach
	void restore() {
		invokedCollectorParser.restoreAll();
	}
	
	@Test
	void testParse() throws IOException {
		withTestedInvoked(getAuxClassInvoked(), getOthersInvoked());
		
		parseAfterProcessing();
		
		assertTestPathIs(invokedCollector.get(0), 35, 36, 37, 38, 39, 40, 38,
						 39, 40, 38, 39, 40, 38, 39, 40, 38, 42);
	}
	
	
	private void parseAfterProcessing() {
		invokedCollectorParser = new TestedInvokedProcessingManager();
		testedInvokedParser = invokedCollectorParser.parse(invokedCollector);
	}

	private void withTestedInvoked(Invoked testedInvoked, Invoked testMethod) {
		invokedCollector.add(new TestedInvoked(testedInvoked, testMethod));
	}

	private Invoked getAuxClassInvoked() {
		return new Invoked.Builder()
				.srcPath(srcDirectory.resolve("AuxClass.java"))
				.binPath(binDirectory.resolve("AuxClass.class"))
				.signature("auxfiles.AuxClass.factorial(int)")
				.invocationLine(32)
				.build();
	}
	
	private Invoked getOthersInvoked() {
		return new Invoked.Builder()
				.srcPath(srcDirectory.resolve("Others.java"))
				.binPath(binDirectory.resolve("Others.class"))
				.signature("auxfiles.Others.testFactorial()")
				.build();
	}
	
	private void assertTestPathIs(TestedInvoked testedInvoked, Integer... testPath) {
		assertEquals(
				List.of(Arrays.asList(testPath)), 
				testedInvokedParser.getTestPathsOf(testedInvoked)
		);
	}
}