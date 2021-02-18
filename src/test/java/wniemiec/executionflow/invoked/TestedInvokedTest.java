package wniemiec.executionflow.invoked;

import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TestedInvokedTest {

	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
	private final Invoked defaultTestedMethod;
	private final Invoked defaultTestMethod;
	
	
	//-----------------------------------------------------------------------
	//		Constructor
	//-----------------------------------------------------------------------
	TestedInvokedTest() {
		defaultTestedMethod = new Invoked.Builder()
				.binPath(Path.of("."))
				.srcPath(Path.of("."))
				.signature("foo.SomeClass.method(int)")
				.invocationLine(10)
				.build();
		
		defaultTestMethod = new Invoked.Builder()
				.binPath(Path.of("."))
				.srcPath(Path.of("."))
				.signature("foo.ClassName.testMethod()")
				.build();
	}
	
	
	
	@Test
	void testConstructorWithNullTestMethod() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new TestedInvoked(defaultTestedMethod, null);
		});
	}

	@Test
	void testConstructorWithNullTestedMethod() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new TestedInvoked(null, defaultTestMethod);
		});
	}
	
	@Test
	void testGetTestedInvoked() {
		TestedInvoked testedInvoked = new TestedInvoked(defaultTestedMethod, defaultTestMethod);
		
		Assertions.assertEquals(defaultTestedMethod, testedInvoked.getTestedInvoked());
	}
	
	@Test
	void testGetTestMethod() {
		TestedInvoked testedInvoked = new TestedInvoked(defaultTestedMethod, defaultTestMethod);
		
		Assertions.assertEquals(defaultTestMethod, testedInvoked.getTestMethod());
	}
	
	@Test
	void testEquals() {
		TestedInvoked testedInvoked = new TestedInvoked(defaultTestedMethod, defaultTestMethod);
		
		Assertions.assertEquals(new TestedInvoked(defaultTestedMethod, defaultTestMethod), testedInvoked);
	}
	
	@Test
	void testNotEquals() {
		TestedInvoked testedInvoked = new TestedInvoked(defaultTestedMethod, defaultTestMethod);
		
		Assertions.assertNotEquals(new TestedInvoked(defaultTestMethod, defaultTestedMethod), testedInvoked);
	}
}
