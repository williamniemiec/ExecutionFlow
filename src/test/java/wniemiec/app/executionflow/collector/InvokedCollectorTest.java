package wniemiec.app.executionflow.collector;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import wniemiec.app.executionflow.App;
import wniemiec.app.executionflow.invoked.Invoked;
import wniemiec.app.executionflow.invoked.TestedInvoked;

abstract class InvokedCollectorTest {

	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private final Path resourcesSrc;
	private final Path resourcesBin;
	private Set<TestedInvoked> collectedTestedInvoked;
	protected Invoked testedMethod;
	protected Invoked testMethod;
	protected InvokedCollector collector;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	protected InvokedCollectorTest() {
		resourcesSrc = App.getCurrentProjectRoot().resolve(
				Path.of(".", "src", "test", "java", "auxfiles", "wniemiec", 
						"app", "executionflow", "io", "processing", "manager")
		);
		resourcesBin = App.getAppTargetPath().resolve(
				Path.of("test-classes", "auxfiles", "wniemiec", "executionflow",
						"app", "io", "processing", "manager")
		);
		
		collectedTestedInvoked = new HashSet<>();
	}
	
	
	//-------------------------------------------------------------------------
	//		Test hooks
	//-------------------------------------------------------------------------
	@BeforeEach
	void restore() {
		testedMethod = null;
		testMethod = null;
	}
	
	@AfterEach
	void clean() {
		collector.reset();
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	protected void withTestedMethod(Invoked testedMethod) {
		this.testedMethod = testedMethod;
	}
			
	protected void withTestMethod(Invoked testMethod) {
		this.testMethod = testMethod;
	}
	
	protected void doCollection() {
		TestedInvoked testedInvoked = new TestedInvoked(testedMethod, testMethod);
		
		collector.collect(testedInvoked);
		collectedTestedInvoked.add(testedInvoked);
		
	}
	
	protected void assertTestedInvokedWasCollected() {
		Assertions.assertEquals(
				collectedTestedInvoked, 
				collector.getAllCollectedInvoked()
		);
	}
	
	protected void updateInvocationLineFromMapping(Map<Integer, List<Integer>> mapping) {
		collector.updateInvocationLines(mapping, testMethod.getSrcPath());
	}
	
	protected void assertTestedInvokedHasInvocationLine(int line) {
		Assertions.assertEquals(line, getFirstCollectedTestedMethod().getInvocationLine());
	}

	private Invoked getFirstCollectedTestedMethod() {
		return collector
					.getAllCollectedInvoked()
					.iterator()
					.next()
					.getTestedInvoked();
	}
	
	//-------------------------------------------------------------------------
	//		Getters
	//-------------------------------------------------------------------------
	protected Path getResourcesSrc() {
		return resourcesSrc;
	}
	
	protected Path getResourcesBin() {
		return resourcesBin;
	}
}
