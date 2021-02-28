package wniemiec.executionflow.analyzer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import wniemiec.executionflow.App;
import wniemiec.executionflow.invoked.Invoked;
import wniemiec.executionflow.invoked.TestedInvoked;

class StandardDebuggerAnalyzerTest {

	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private final Path resourcesSrc;
	private final Path resourcesBin;
	private DebuggerAnalyzer debugger;
	private Invoked testMethod;
	private Invoked testedInvoked;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	public StandardDebuggerAnalyzerTest() {
		resourcesSrc = App.getCurrentProjectRoot().resolve(
				Path.of(".", "src", "test", "resources", "auxfiles")
		);
		resourcesBin = App.getCurrentProjectRoot().resolve(
				Path.of(".", "target", "test-classes", "auxfiles")
		);
	}
	
	
	//-------------------------------------------------------------------------
	//		Test hooks
	//-------------------------------------------------------------------------
	@BeforeEach
	void prepare() {
		testedInvoked = null;
		testMethod = null;
		debugger = null;
	}
	
	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	@Test
	void testGetTestPath() throws IOException {
		withTestedInvoked(getTestedInvokedFactorial());
		withTestMethod(getTestMethodTestFactorial());
		runDebuggerAnalyzer();
		
		assertTestPathIs(94, 96, 98, 99, 98, 99, 98, 99, 98, 99, 98, 102);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void testGetTestPathWithinLoop() throws IOException {
		withTestedInvoked(getTestedInvokedLoopAuxClass());
		withTestMethod(getTestMethodLoop());
		runDebuggerAnalyzer();
		
		assertTestPathIs(
				List.of(68, 70, 71, 70, 74),
				List.of(68, 70, 71, 70, 71, 70, 74),
				List.of(68, 70, 71, 70, 71, 70, 71, 70, 74),
				List.of(68, 70, 71, 70, 71, 70, 71, 70, 71, 70, 74)
		);
	}
	
	@Test
	void testTimeoutTrue() throws IOException {
		withTestedInvoked(getTestedInvokedFactorial());
		withTestMethod(getTestMethodTestFactorial());
		
		runDebuggerAnalyzerWithTimeout(100);
		
		assertTimeoutOcurred();
	}
	
	@Test
	void testNegativeTimeout() throws IOException {
		withTestedInvoked(getTestedInvokedFactorial());
		withTestMethod(getTestMethodTestFactorial());
		
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			runDebuggerAnalyzerWithTimeout(-100);			
		});
	}
	
	@Test
	void testTimeoutFalse() throws IOException {
		withTestedInvoked(getTestedInvokedFactorial());
		withTestMethod(getTestMethodTestFactorial());
		
		runDebuggerAnalyzerWithTimeout(1000*60*10);
		
		assertNoTimeoutOcurred();
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	private void withTestedInvoked(Invoked testedInvoked) {
		this.testedInvoked = testedInvoked;
	}
	
	private void withTestMethod(Invoked testMethod) {
		this.testMethod = testMethod;
	}
		
	private void runDebuggerAnalyzer() throws IOException {
		debugger = DebuggerAnalyzerFactory.createStandardTestPathAnalyzer(
				new TestedInvoked(testedInvoked, testMethod)
		);
		
		debugger.disableTimeout();
		debugger.analyze();
	}
	
	private void runDebuggerAnalyzerWithTimeout(int timeout) throws IOException {
		debugger = DebuggerAnalyzerFactory.createStandardTestPathAnalyzer(
				new TestedInvoked(testedInvoked, testMethod)
		);
		
		debugger.enableTimeout();
		debugger.setTimeout(timeout);
		debugger.analyze();
	}
	
	private void assertTestPathIs(Integer... testPath) {
		assertEquals(
				List.of(Arrays.asList(testPath)), 
				debugger.getTestPaths()
		);
	}
	
	@SuppressWarnings("unchecked")
	private void assertTestPathIs(List<Integer>... testPath) {
		Assertions.assertEquals(
				List.of(testPath), 
				debugger.getTestPaths()
		);
		
		Assertions.assertTrue(debugger.wasTestPathObtainedInALoop());
	}
	
	private void assertNoTimeoutOcurred() {
		Assertions.assertFalse(debugger.checkTimeout());
	}

	private void assertTimeoutOcurred() {
		Assertions.assertTrue(debugger.checkTimeout());
	}
	
	private Invoked getTestMethodTestFactorial() {
		return new Invoked.Builder()
				.srcPath(resourcesSrc.resolve("Others.java"))
				.binPath(resourcesBin.resolve("Others.class"))
				.signature("auxfiles.Others.testFactorial()")
				.build();
	}

	private Invoked getTestedInvokedFactorial() {
		return new Invoked.Builder()
				.srcPath(resourcesSrc.resolve("AuxClass.java"))
				.binPath(resourcesBin.resolve("AuxClass.class"))
				.signature("auxfiles.AuxClass.factorial(int)")
				.invocationLine(32)
				.build();
	}
	
	private Invoked getTestMethodLoop() {
		return new Invoked.Builder()
				.srcPath(resourcesSrc.resolve(Path.of("loop", "Loop.java")))
				.binPath(resourcesBin.resolve(Path.of("loop", "Loop.class")))
				.signature("auxfiles.loop.Loop.testForConstructorAndMethod()")
				.build();
	}

	private Invoked getTestedInvokedLoopAuxClass() {
		return new Invoked.Builder()
				.srcPath(resourcesSrc.resolve(Path.of("loop", "AuxClass.java")))
				.binPath(resourcesBin.resolve(Path.of("loop", "AuxClass.class")))
				.signature("auxfiles.loop.LoopAuxClass.factorial_constructor()")
				.invocationLine(13)
				.build();
	}
}
