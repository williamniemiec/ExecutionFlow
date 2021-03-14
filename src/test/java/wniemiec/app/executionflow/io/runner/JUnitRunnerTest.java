package wniemiec.app.executionflow.io.runner;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import wniemiec.app.executionflow.App;
import wniemiec.app.executionflow.invoked.Invoked;
import wniemiec.app.executionflow.io.runner.JUnitRunner;

class JUnitRunnerTest {

	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private final Path resourcesSrc;
	private final Path resourcesBin;
	private Invoked testMethod;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	public JUnitRunnerTest() {
		resourcesSrc = App.getCurrentProjectRoot().resolve(
				Path.of(".", "src", "test", "java", "auxfiles")
		);
		resourcesBin = App.getAppTargetPath().resolve(
				Path.of("test-classes", "auxfiles")
		);
	}
	
	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	@Test
	void testRunTestmethod() throws InterruptedException, IOException {
		withTestMethod(getTestMethodTestFactorial());
		runJUnitRunner();

		Assertions.assertTrue(JUnitRunner.isRunningFromJUnitAPI());
		JUnitRunner.stopRunner();
		Assertions.assertFalse(JUnitRunner.isRunningFromJUnitAPI());
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	private void withTestMethod(Invoked testMethod) {
		this.testMethod = testMethod;
	}
	
	private void runJUnitRunner() throws InterruptedException {
		Thread t = new Thread(() -> {
			JUnitRunner.runTestMethod(testMethod);			
		});
		
		t.start();
		
		Thread.sleep(500);
	}
	
	private Invoked getTestMethodTestFactorial() {
		return new Invoked.Builder()
				.srcPath(resourcesSrc.resolve(Path.of("loop", "InfiniteLoop.java")))
				.binPath(resourcesBin.resolve(Path.of("loop", "InfiniteLoop.class")))
				.signature("auxfiles.loop.InfiniteLoop.infinite()")
				.build();
	}
}
