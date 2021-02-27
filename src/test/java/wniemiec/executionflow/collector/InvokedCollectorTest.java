package wniemiec.executionflow.collector;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import wniemiec.executionflow.invoked.Invoked;
import wniemiec.executionflow.invoked.TestedInvoked;

abstract class InvokedCollectorTest {

	private final Path resourcesSrc;
	private final Path resourcesBin;
	private Set<TestedInvoked> collectedTestedInvoked;
	protected Invoked testedMethod;
	protected Invoked testMethod;
	protected InvokedCollector collector;
	
	
	protected InvokedCollectorTest() {
		resourcesSrc = Path.of(".", "src", "test", "resources", 
				"wniemiec",	"executionflow", "io", "processing", "manager");
		resourcesBin = Path.of(".", "target", "test-classes", 
				"wniemiec", "executionflow", "io", "processing", "manager");
		
		collectedTestedInvoked = new HashSet<>();
	}
	
	@BeforeEach
	void restore() {
		testedMethod = null;
		testMethod = null;
	}
	
	protected Path getResourcesSrc() {
		return resourcesSrc;
	}
	
	protected Path getResourcesBin() {
		return resourcesBin;
	}
	
	protected void doCollection() {
		TestedInvoked testedInvoked = new TestedInvoked(testedMethod, testMethod);
		
		collector.collect(testedInvoked);
		collectedTestedInvoked.add(testedInvoked);
		
	}

	protected void withTestedMethod(Invoked testedMethod) {
		this.testedMethod = testedMethod;
	}
			
	protected void withTestMethod(Invoked testMethod) {
		this.testMethod = testMethod;
	}
	
	protected void assertTestedInvokedWasCollected() {
		Assertions.assertEquals(
				collectedTestedInvoked, 
				collector.getAllCollectedInvoked()
		);
	}
	
	protected void updateInvocationLineFromMapping(Map<Integer, Integer> mapping) {
		collector.updateInvocationLines(mapping, testMethod.getSrcPath());
	}
	
	protected void assertTestedInvokedHasInvocationLine(int line) {
		Assertions.assertEquals(line, testedMethod.getInvocationLine());
	}
}
