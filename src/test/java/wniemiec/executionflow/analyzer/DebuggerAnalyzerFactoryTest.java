package wniemiec.executionflow.analyzer;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import wniemiec.executionflow.invoked.Invoked;

class DebuggerAnalyzerFactoryTest {

	private final Path resourcesSrc;
	private final Path resourcesBin;
	
	public DebuggerAnalyzerFactoryTest() {
		resourcesSrc = Path.of(".", "src", "test", "resources", "auxfiles");
		resourcesBin = Path.of(".", "target", "test-classes", "auxfiles");
	}
	
	@Test
	void testCreateStandardTestPathAnalyzer() throws IOException {
		DebuggerAnalyzer debugger = DebuggerAnalyzerFactory.createStandardTestPathAnalyzer(
				getTestedInvokedFactorial(), 
				getTestMethodTestFactorial()
		);
		
		Assertions.assertTrue(debugger instanceof StandardDebuggerAnalyzer);
	}
	
	@Test
	void testCreateStandardTestPathAnalyzerWithNullTestedInvoked() throws IOException {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			DebuggerAnalyzerFactory.createStandardTestPathAnalyzer(
					null, 
					getTestMethodTestFactorial()
			);		
		});
	}
	
	@Test
	void testCreateStandardTestPathAnalyzerWithNullTestMethod() throws IOException {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			DebuggerAnalyzerFactory.createStandardTestPathAnalyzer(
					getTestedInvokedFactorial(), 
					null
			);		
		});
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
}
