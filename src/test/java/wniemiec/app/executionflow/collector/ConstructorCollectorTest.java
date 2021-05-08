package wniemiec.app.executionflow.collector;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import wniemiec.app.executionflow.invoked.Invoked;

class ConstructorCollectorTest extends InvokedCollectorTest {

	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	ConstructorCollectorTest() {
		collector = ConstructorCollector.getInstance();
	}
	
	
	//-------------------------------------------------------------------------
	//		Test hooks
	//-------------------------------------------------------------------------
	@BeforeEach
	void restore() {
		collector.reset();
	}
	
	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
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
				Map.entry(99, List.of(24))
		));
		
		assertTestedInvokedHasInvocationLine(99);
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	private Invoked getTestMethodMethod4() {
		return new Invoked.Builder()
				.srcPath(getResourcesSrc().resolve("testmethod.java"))
				.binPath(getResourcesBin().resolve("testmethod.class"))
				.signature("auxfiles.wniemiec.app.executionflow.io.processing.manager.testmethod.method4()")
				.build();
	}

	private Invoked getTestedConstructor() {
		return new Invoked.Builder()
				.srcPath(getResourcesSrc().resolve("testedinvoked.java"))
				.binPath(getResourcesBin().resolve("testedinvoked.class"))
				.signature("auxfiles.wniemiec.app.executionflow.io.processing.manager.testedinvoked(int)")
				.invocationLine(24)
				.isConstructor(true)
				.build();
	}
}
