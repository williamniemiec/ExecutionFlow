package wniemiec.app.java.executionflow.analyzer;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import wniemiec.app.java.ExecutionFlow;
import wniemiec.app.java.executionflow.invoked.Invoked;
import wniemiec.app.java.executionflow.invoked.TestedInvoked;

class DebuggerAnalyzerFactoryTest {

	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private final Path resourcesSrc;
	private final Path resourcesBin;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	public DebuggerAnalyzerFactoryTest() {
		resourcesSrc = ExecutionFlow.getCurrentProjectRoot().resolve(
				Path.of(".", "src", "test", "java", "auxfiles")
		);
		resourcesBin = ExecutionFlow.getAppTargetPath().resolve(
				Path.of("test-classes", "auxfiles")
		);
	}
	
	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	@Test
	void testCreateStandardTestPathAnalyzer() throws IOException {
		DebuggerAnalyzer debugger = DebuggerAnalyzerFactory.createStandardTestPathAnalyzer(
				new TestedInvoked(
						getTestedInvokedFactorial(), 
						getTestMethodTestFactorial()
				)
		);
		
		Assertions.assertTrue(debugger instanceof StandardDebuggerAnalyzer);
	}
	
	@Test
	void testCreateStandardTestPathAnalyzerWithNullTestedInvoked() throws IOException {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			DebuggerAnalyzerFactory.createStandardTestPathAnalyzer(null);		
		});
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
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