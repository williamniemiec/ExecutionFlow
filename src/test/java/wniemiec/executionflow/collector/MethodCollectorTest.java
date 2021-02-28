package wniemiec.executionflow.collector;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import wniemiec.executionflow.invoked.Invoked;

class MethodCollectorTest extends InvokedCollectorTest {
	
	//-------------------------------------------------------------------------
	//		Test hooks
	//-------------------------------------------------------------------------
	@BeforeEach
	void restore() {
		collector = MethodCollector.getInstance();
	}
	
	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	@Test
	void testStoreAndGetCollector() {
		withTestedMethod(getTestedInvokedM3Method());
		withTestMethod(getTestMethodMethod1());
		doCollection();
		
		assertTestedInvokedWasCollected();
	}

	@Test
	void testUpdateInvocationLine() {
		withTestedMethod(getTestedInvokedM3Method());
		withTestMethod(getTestMethodMethod1());
		doCollection();
		
		updateInvocationLineFromMapping(Map.ofEntries(
				Map.entry(9, 99)
		));
		
		assertTestedInvokedHasInvocationLine(99);
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	private Invoked getTestMethodMethod1() {
		return new Invoked.Builder()
				.srcPath(getResourcesSrc().resolve("testmethod.java"))
				.binPath(getResourcesBin().resolve("testmethod.class"))
				.signature("wniemiec.executionflow.io.processing.manager.testmethod.method1()")
				.build();
	}

	private Invoked getTestedInvokedM3Method() {
		return new Invoked.Builder()
				.srcPath(getResourcesSrc().resolve("testedinvoked.java"))
				.binPath(getResourcesBin().resolve("testedinvoked.class"))
				.signature("wniemiec.executionflow.io.processing.manager.testedinvoked(int)")
				.invocationLine(9)
				.isConstructor(true)
				.build();
	}
}
