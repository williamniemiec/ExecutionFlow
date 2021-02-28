package wniemiec.executionflow.collector;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import wniemiec.executionflow.invoked.Invoked;

class ConstructorCollectorTest extends InvokedCollectorTest {

	@BeforeEach
	void restore() {
		collector = ConstructorCollector.getInstance();
	}
	
	@Test
	void testStoreAndGetCollector() {
		withTestedMethod(getTestedConstructor());
		withTestMethod(getTestMethodMethod4());
		doCollection();
		
		assertTestedInvokedWasCollected();
	}

	@Test
	void testUpdateInvocationLine() {
		withTestedMethod(getTestedConstructor());
		withTestMethod(getTestMethodMethod4());
		doCollection();
		
		updateInvocationLineFromMapping(Map.ofEntries(
				Map.entry(24, 99)
		));
		
		assertTestedInvokedHasInvocationLine(99);
	}
	
	private Invoked getTestMethodMethod4() {
		return new Invoked.Builder()
				.srcPath(getResourcesSrc().resolve("testmethod.java"))
				.binPath(getResourcesBin().resolve("testmethod.class"))
				.signature("wniemiec.executionflow.io.processing.manager.testmethod.method4()")
				.build();
	}

	private Invoked getTestedConstructor() {
		return new Invoked.Builder()
				.srcPath(getResourcesSrc().resolve("testedinvoked.java"))
				.binPath(getResourcesBin().resolve("testedinvoked.class"))
				.signature("wniemiec.executionflow.io.processing.manager.testedinvoked(int)")
				.invocationLine(24)
				.build();
	}
}