package wniemiec.executionflow.io.runner;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import wniemiec.executionflow.invoked.Invoked;

class JUnitRunnerTest {

	private final Path resourcesSrc;
	private final Path resourcesBin;
	private Invoked testMethod;
	
	public JUnitRunnerTest() {
		resourcesSrc = Path.of(".", "src", "test", "resources", "auxfiles");
		resourcesBin = Path.of(".", "target", "test-classes", "auxfiles");
	}
	
	@Test
	void testRunTestmethod() throws InterruptedException, IOException {
		withTestMethod(getTestMethodTestFactorial());
		runJUnitRunner();

		Assertions.assertTrue(JUnitRunner.isRunningFromJUnitAPI());
		JUnitRunner.stopRunner();
		Assertions.assertFalse(JUnitRunner.isRunningFromJUnitAPI());
	}
	
	private void runJUnitRunner() throws InterruptedException {
		Thread t = new Thread(() -> {
			JUnitRunner.runTestMethod(testMethod);			
		});
		
		t.start();
		
		Thread.sleep(400);
	}
	
	private void withTestMethod(Invoked testMethod) {
		this.testMethod = testMethod;
	}
	
	private Invoked getTestMethodTestFactorial() {
		return new Invoked.Builder()
				.srcPath(resourcesSrc.resolve("Others.java"))
				.binPath(resourcesBin.resolve("Others.class"))
				.signature("auxfiles.Others.testFactorial()")
				.build();
	}
}
