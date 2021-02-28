package wniemiec.executionflow.collector;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import wniemiec.executionflow.invoked.Invoked;

class CallCollectorTest {

	private final Path resourcesSrc;
	private final Path resourcesBin;
	private CallCollector callCollector;
	private Invoked testedInvoked;
	private Set<String> signatureOfMethodsCalled;
	
	public CallCollectorTest() {
		resourcesSrc = Path.of(".", "src", "test", "resources", "auxfiles",	
							   "mcti");
		resourcesBin = Path.of(".", "target", "test-classes", "auxfiles", 
							   "mcti");
		
		callCollector = CallCollector.getInstance();
		signatureOfMethodsCalled = new HashSet<>();
	}
	
	@BeforeEach
	void prepare() {
		callCollector.reset();
		callCollector.deleteStoredContent();
		testedInvoked = null;
	}
	
	@AfterEach
	void clean() {
		callCollector.deleteStoredContent();
		signatureOfMethodsCalled.clear();
	}
	
	@Test
	void testCollectCall() {
		withTestedInvoked(getTestedInvokedA());
		doCollectionCall("auxfiles.mcti.MethodsCalledByTestedInvokedAuxClass.B()");
		doCollectionCall("auxfiles.mcti.MethodsCalledByTestedInvokedAuxClass.C()");
		
		assertCallsWereCollected();
	}
	
	@Test
	void testCollectCallWithEmptySignatureOfMethodCalled() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			callCollector.collectCall("", getTestedInvokedA());
		});
	}
	
	@Test
	void testCollectCallWithNullSignatureOfMethodCalled() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			callCollector.collectCall(null, getTestedInvokedA());
		});
	}
	
	@Test
	void testCollectCallWithNullTestedInvoked() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			callCollector.collectCall("foo.SomeClass.bar(int)", null);
		});
	}
	
	private void assertCallsWereCollected() {
		Assertions.assertEquals(
				signatureOfMethodsCalled, 
				getMethodsCalledByTestedInvokedFrom(testedInvoked)
		);
	}
	
	private Set<String> getMethodsCalledByTestedInvokedFrom(Invoked testedInvoked) {
		return callCollector.getMethodsCalledByTestedInvoked().get(testedInvoked);
	}

	private void withTestedInvoked(Invoked testedInvoked) {
		this.testedInvoked = testedInvoked;
	}
	
	private void doCollectionCall(String signatureOfMethodCalled) {
		callCollector.collectCall(signatureOfMethodCalled, testedInvoked);
		signatureOfMethodsCalled.add(signatureOfMethodCalled);
	}

	

	private Invoked getTestedInvokedA() {
		return new Invoked.Builder()
				.srcPath(resourcesSrc.resolve("MethodsCalledByTestedInvokedAuxClass.java"))
				.binPath(resourcesBin.resolve("MethodsCalledByTestedInvokedAuxClass.class"))
				.signature("auxfiles.mcti.MethodsCalledByTestedInvokedAuxClass.A()")
				.invocationLine(15)
				.build();
	}
}
