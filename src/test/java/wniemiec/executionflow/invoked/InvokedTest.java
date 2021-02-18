package wniemiec.executionflow.invoked;

import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class InvokedTest {

	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
	private final Invoked defaultMethodInvoked;
	private final Invoked defaultConstructorInvoked;
	
	
	//-----------------------------------------------------------------------
	//		Constructor
	//-----------------------------------------------------------------------
	InvokedTest() {
		defaultMethodInvoked = new Invoked.Builder()
				.name("bar")
				.binPath(Path.of("."))
				.srcPath(Path.of("."))
				.signature("foo.ClassName.bar(int)")
				.invocationLine(10)
				.isConstructor(false)
				.parameterTypes(new Class[] {int.class})
				.args(1)
				.returnType(int.class)
				.build();
		
		defaultConstructorInvoked = new Invoked.Builder()
				.binPath(Path.of("."))
				.srcPath(Path.of("."))
				.signature("foo.ClassName(String)")
				.invocationLine(10)
				.isConstructor(true)
				.parameterTypes(new Class[] {String.class})
				.args("something")
				.build();
	}
	
	
	//-----------------------------------------------------------------------
	//		Test hooks
	//-----------------------------------------------------------------------
	@AfterEach
	void restoreOriginalValues() {
		defaultMethodInvoked.setInvocationLine(10);
		defaultConstructorInvoked.setInvocationLine(10);
		
		defaultMethodInvoked.setSignature("foo.ClassName.bar(int)");
		defaultConstructorInvoked.setSignature("foo.ClassName(String)");
		
		defaultMethodInvoked.setConcreteSignature("foo.ClassName.bar(int)");
		defaultConstructorInvoked.setConcreteSignature("foo.ClassName(String)");
	}
	
	
	//-----------------------------------------------------------------------
	//		Tests
	//-----------------------------------------------------------------------
	@Test
	void testFullBuilder() {
		Invoked invoked = new Invoked.Builder()
				.name("bar")
				.binPath(Path.of("."))
				.srcPath(Path.of("."))
				.signature("foo.ClassName.bar(int)")
				.invocationLine(10)
				.isConstructor(false)
				.parameterTypes(new Class[] {int.class})
				.args(1)
				.returnType(int.class)
				.build();
		
		Assertions.assertTrue(invoked instanceof Invoked);
	}
	
	@Test
	void testMinimumBuilder() {
		Invoked invoked = new Invoked.Builder()
				.binPath(Path.of("."))
				.srcPath(Path.of("."))
				.signature("foo.bar(int)")
				.build();
		
		Assertions.assertTrue(invoked instanceof Invoked);
	}

	@Test
	void testEmptyBuilder() {
		Assertions.assertThrows(IllegalStateException.class, () -> {
			new Invoked.Builder().build();
		});
	}
	
	@Test
	void testBuilderWithoutBinPath() {
		Assertions.assertThrows(IllegalStateException.class, () -> {
			new Invoked.Builder()
				.srcPath(Path.of("."))
				.signature("foo.bar(int)")
				.build();
		});
	}
	
	@Test
	void testBuilderWithoutSrcPath() {
		Assertions.assertThrows(IllegalStateException.class, () -> {
			new Invoked.Builder()
				.binPath(Path.of("."))
				.signature("foo.bar(int)")
				.build();
		});
	}
	
	@Test
	void testBuilderWithoutSignature() {
		Assertions.assertThrows(IllegalStateException.class, () -> {
			new Invoked.Builder()
				.binPath(Path.of("."))
				.srcPath(Path.of("."))
				.build();
		});
	}
	
	@Test
	void testBuilderWithNullName() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new Invoked.Builder()
				.name(null)
				.binPath(Path.of("."))
				.srcPath(Path.of("."))
				.build();
		});
	}
	
	@Test
	void testBuilderWithNullBinPath() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new Invoked.Builder()
				.binPath(null)
				.srcPath(Path.of("."))
				.signature("foo.bar(int)")
				.build();
		});
	}
	
	@Test
	void testBuilderWithNullSrcPath() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new Invoked.Builder()
				.srcPath(null)
				.binPath(Path.of("."))
				.signature("foo.bar(int)")
				.build();
		});
	}
	
	@Test
	void testBuilderWithNullSignature() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new Invoked.Builder()
				.signature(null)
				.binPath(Path.of("."))
				.srcPath(Path.of("."))
				.build();
		});
	}
	
	@Test
	void testBuilderWithNegativeInvocationLine() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new Invoked.Builder()
				.invocationLine(-1)
				.binPath(Path.of("."))
				.srcPath(Path.of("."))
				.signature("foo.bar(int)")
				.build();
		});
	}
	
	@Test
	void testBuilderWithNullParameterTypes() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new Invoked.Builder()
				.parameterTypes(null)
				.binPath(Path.of("."))
				.srcPath(Path.of("."))
				.signature("foo.bar(int)")
				.build();
		});
	}
	
	@SuppressWarnings("all")
	@Test
	void testBuilderWithNullArgs() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new Invoked.Builder()
				.args(null)
				.binPath(Path.of("."))
				.srcPath(Path.of("."))
				.signature("foo.bar(int)")
				.build();
		});
	}
	
	@Test
	void testBuilderWithNullReturnType() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new Invoked.Builder()
				.returnType(null)
				.binPath(Path.of("."))
				.srcPath(Path.of("."))
				.signature("foo.bar(int)")
				.build();
		});
	}
	
	@Test
	void testExtractMethodName() {
		Assertions.assertEquals(
				"bar", 
				Invoked.extractMethodNameFromMethodSignature("foo.ClassName.bar(int, String)")
		);
	}
	
	@Test
	void testExtractMethodNameFromEmptySignature() {
		Assertions.assertEquals("", Invoked.extractMethodNameFromMethodSignature(""));
	}
	
	@Test
	void testExtractMethodNameFromNullSignature() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			Invoked.extractMethodNameFromMethodSignature(null);
		});
	}
	
	@Test
	void testExtractPackageFromClassSignature() {
		Assertions.assertEquals(
				"foo", 
				Invoked.extractPackageFromClassSignature("foo.ClassName")
		);
	}
	
	@Test
	void testExtractPackageFromEmptyClassSignature() {
		Assertions.assertEquals("", Invoked.extractPackageFromClassSignature(""));
	}
	
	@Test
	void testExtractPackageFromNullClassSignature() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			Invoked.extractPackageFromClassSignature(null);
		});
	}
	
	@Test
	void testBelongsToAnonymousClass() {
		Invoked invoked = buildDefaultInvokedOfTypeMethod("foo.ClassName$1.bar(int)");
		
		Assertions.assertTrue(invoked.belongsToAnonymousClass());
	}
	
	@Test
	void testDoesNotBelongToAnonymousClass() {
		Invoked invoked = buildDefaultInvokedOfTypeMethod("foo.ClassName.bar(int)");
		
		Assertions.assertFalse(invoked.belongsToAnonymousClass());
	}
	
	@Test
	void testEqualsMethod() {
		Invoked invoked1 = new Invoked.Builder()
				.binPath(Path.of("."))
				.srcPath(Path.of("."))
				.signature("foo.ClassName.bar(int)")
				.invocationLine(10)
				.build();
		
		Invoked invoked2 = new Invoked.Builder()
				.binPath(Path.of("."))
				.srcPath(Path.of("."))
				.signature("foo.ClassName.bar(int)")
				.invocationLine(10)
				.build();
		
		Assertions.assertEquals(invoked1, invoked2);
	}
	
	@Test
	void testNotEqualsMethod() {
		Invoked invoked1 = new Invoked.Builder()
				.binPath(Path.of("."))
				.srcPath(Path.of("."))
				.signature("foo.ClassName.bar(int)")
				.invocationLine(10)
				.build();
		
		Invoked invoked2 = new Invoked.Builder()
				.binPath(Path.of("."))
				.srcPath(Path.of("."))
				.signature("foo.ClassName.bar()")
				.invocationLine(10)
				.build();
		
		Assertions.assertNotEquals(invoked1, invoked2);
	}
	
	@Test
	void testEqualsConstructor() {
		Invoked invoked1 = new Invoked.Builder()
				.binPath(Path.of("."))
				.srcPath(Path.of("."))
				.signature("foo.SomeClass(int)")
				.invocationLine(10)
				.isConstructor(true)
				.build();
		
		Invoked invoked2 = new Invoked.Builder()
				.binPath(Path.of("."))
				.srcPath(Path.of("."))
				.signature("foo.SomeClass(int)")
				.invocationLine(10)
				.isConstructor(true)
				.build();
		
		Assertions.assertEquals(invoked1, invoked2);
	}
	
	@Test
	void testNotEqualsConstructor() {
		Invoked invoked1 = new Invoked.Builder()
				.binPath(Path.of("."))
				.srcPath(Path.of("."))
				.signature("foo.SomeClass(int)")
				.invocationLine(10)
				.isConstructor(true)
				.build();
		
		Invoked invoked2 = new Invoked.Builder()
				.binPath(Path.of("."))
				.srcPath(Path.of("."))
				.signature("foo.SomeClass()")
				.invocationLine(10)
				.isConstructor(true)
				.build();
		
		Assertions.assertNotEquals(invoked1, invoked2);
	}
	
	@Test
	void testEqualsMethodAndConstructor() {
		Invoked invoked1 = new Invoked.Builder()
				.binPath(Path.of("."))
				.srcPath(Path.of("."))
				.signature("foo.ClassName.bar(int)")
				.invocationLine(10)
				.build();
		
		Invoked invoked2 = new Invoked.Builder()
				.binPath(Path.of("."))
				.srcPath(Path.of("."))
				.signature("foo.SomeClass(int)")
				.invocationLine(10)
				.isConstructor(true)
				.build();
		
		Assertions.assertNotEquals(invoked1, invoked2);
	}
	
	@Test
	void testGetBinPath() {
		assertPathEquals(Path.of("."), defaultMethodInvoked.getBinPath());
	}
	
	@Test
	void testGetSrcPath() {
		assertPathEquals(Path.of("."), defaultMethodInvoked.getSrcPath());
	}
	
	@Test
	void testGetMethodInvokedSignature() {
		Assertions.assertEquals("foo.ClassName.bar(int)", defaultMethodInvoked.getInvokedSignature());
	}
	
	@Test
	void testGetConstructorInvokedSignature() {
		Assertions.assertEquals("foo.ClassName(String)", defaultConstructorInvoked.getInvokedSignature());
	}
	
	@Test
	void testGetClassSignatureFromMethodInvoked() {
		Assertions.assertEquals("foo.ClassName.bar(int)", defaultMethodInvoked.getInvokedSignature());
	}
	
	@Test
	void testGetClassSignatureFromConstructorInvoked() {
		Assertions.assertEquals("foo.ClassName(String)", defaultConstructorInvoked.getInvokedSignature());
	}
	
	@Test 
	void testGetInvocationLine() {
		Assertions.assertEquals(10, defaultMethodInvoked.getInvocationLine());
	}
	
	@Test 
	void testSetInvocationLine() {
		defaultMethodInvoked.setInvocationLine(123);
		
		Assertions.assertEquals(123, defaultMethodInvoked.getInvocationLine());
	}
	
	@Test 
	void testSetNegativeInvocationLine() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			defaultMethodInvoked.setInvocationLine(-123);
		});
		Assertions.assertEquals(10, defaultMethodInvoked.getInvocationLine());
	}
	
	@Test
	void testGetPackageFromMethod() {
		Assertions.assertEquals("foo", defaultMethodInvoked.getPackage());
	}
	
	@Test
	void testGetPackageFromConstructor() {
		Assertions.assertEquals("foo", defaultConstructorInvoked.getPackage());
	}
	
	@Test
	void testGetArgs() {
		Assertions.assertArrayEquals(new Object[] {1}, defaultMethodInvoked.getArgs());
	}
	
	@Test
	void testGetParameterTypes() {
		Assertions.assertArrayEquals(new Class[] {int.class}, defaultMethodInvoked.getParameterTypes());
	}
	
	@Test
	void testGetSignatureWithoutParametersFromMethod() {
		Assertions.assertEquals("foo.ClassName.bar", defaultMethodInvoked.getSignatureWithoutParameters());
	}
	
	@Test
	void testGetSignatureWithoutParametersFromConstructor() {
		Assertions.assertEquals("foo.ClassName", defaultConstructorInvoked.getSignatureWithoutParameters());
	}
	
	@Test
	void testGetNameFromMethod() {
		Assertions.assertEquals("bar", defaultMethodInvoked.getName());
	}
	
	@Test
	void testGetNameFromConstructor() {
		Assertions.assertEquals("ClassName", defaultConstructorInvoked.getName());
	}
	
	@Test
	void testGetReturnType() {
		Assertions.assertEquals(int.class, defaultMethodInvoked.getReturnType());
	}
	
	@Test
	void testGetConcreteSignatureFromConcreteSignature() {
		Assertions.assertEquals("foo.ClassName.bar(int)", defaultMethodInvoked.getConcreteSignature());
	}
	
	@Test
	void testGetConcreteSignatureFromAnonymousSignature() {
		Invoked invoked = buildDefaultInvokedOfTypeMethod("foo.ClassName$1.bar(int)");
		
		Assertions.assertEquals("foo.ClassName.bar(int)", invoked.getConcreteSignature());
	}
	
	@Test
	void testGetConcreteSignatureFromInnerClassSignature() {
		Invoked invoked = buildDefaultInvokedOfTypeMethod("foo.OuterClass$InnerClass.bar(int)");
		
		Assertions.assertEquals("foo.OuterClass.InnerClass.bar(int)", invoked.getConcreteSignature());
	}
	
	@Test
	void testIsConstructorFromMethod() {
		Assertions.assertFalse(defaultMethodInvoked.isConstructor());
	}
	
	@Test
	void testIsConstructorFromConstructor() {
		Assertions.assertTrue(defaultConstructorInvoked.isConstructor());
	}
	
	@Test
	void testSetSignature() {
		defaultMethodInvoked.setSignature("some.signature()");
		
		Assertions.assertEquals("some.signature()", defaultMethodInvoked.getInvokedSignature());
	}
	
	@Test
	void testSetConcreteSignature() {
		defaultMethodInvoked.setConcreteSignature("some.concrete.signature()");
		
		Assertions.assertEquals("some.concrete.signature()", defaultMethodInvoked.getConcreteSignature());
	}
	
	@Test
	void testSetNullSignature() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			defaultMethodInvoked.setSignature(null);
		});
	}
	
	@Test
	void testSetNullConcreteSignature() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			defaultMethodInvoked.setConcreteSignature(null);
		});
	}
	
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	private Invoked buildDefaultInvokedOfTypeMethod(String signature) {
		return new Invoked.Builder()
				.name("bar")
				.binPath(Path.of("."))
				.srcPath(Path.of("."))
				.signature(signature)
				.invocationLine(10)
				.isConstructor(false)
				.parameterTypes(new Class[] {int.class})
				.args(1)
				.returnType(int.class)
				.build();
	}
	
	private void assertPathEquals(Path expected, Path actual) {
		Assertions.assertEquals(
				expected.normalize().toAbsolutePath(), 
				actual.normalize().toAbsolutePath()
		);
	}
}
