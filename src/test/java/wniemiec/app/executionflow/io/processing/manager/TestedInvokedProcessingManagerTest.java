package wniemiec.app.executionflow.io.processing.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import wniemiec.app.executionflow.App;
import wniemiec.app.executionflow.collector.parser.TestedInvokedParser;
import wniemiec.app.executionflow.invoked.Invoked;
import wniemiec.app.executionflow.invoked.TestedInvoked;
import wniemiec.io.consolex.Consolex;
import wniemiec.io.consolex.LogLevel;

class TestedInvokedProcessingManagerTest {

	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private Path srcDirectory;
	private Path binDirectory;
	private List<TestedInvoked> invokedCollector;
	private TestedInvokedProcessingManager invokedCollectorParser;
	private TestedInvokedParser testedInvokedParser;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	TestedInvokedProcessingManagerTest() {
		srcDirectory = App.getCurrentProjectRoot().resolve(
				Path.of(".", "src", "test", "java", "auxfiles")
		);
		binDirectory = App.getAppTargetPath().resolve(
				Path.of("test-classes", "auxfiles")
		);

		Consolex.setLoggerLevel(LogLevel.WARNING);
	}
	
	
	//-------------------------------------------------------------------------
	//		Test hooks
	//-------------------------------------------------------------------------
	@BeforeEach
	void prepare() {
		invokedCollector = new ArrayList<>();
	}
	
	@AfterEach
	void restore() {
		invokedCollectorParser.restoreAll();
	}
	
	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	@Test
	void testParse() throws IOException {
		withTestedInvoked(getAuxClassInvoked(), getOthersInvoked());
		
		parseAfterProcessing();
		
		assertTestPathIs(invokedCollector.get(0), 94, 96, 98, 99, 98, 99, 98,
						 99, 98, 99, 98, 102);
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	private void withTestedInvoked(Invoked testedInvoked, Invoked testMethod) {
		invokedCollector.add(new TestedInvoked(testedInvoked, testMethod));
	}
	
	private void parseAfterProcessing() {
		invokedCollectorParser = new TestedInvokedProcessingManager();
		testedInvokedParser = invokedCollectorParser.processAndParse(invokedCollector);
	}
	
	private void assertTestPathIs(TestedInvoked testedInvoked, Integer... testPath) {
		assertEquals(
				List.of(Arrays.asList(testPath)), 
				testedInvokedParser.getTestPathsOf(testedInvoked)
		);
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
}
