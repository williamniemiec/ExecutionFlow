package auxfiles.aspectj;

public class TestClass {
	@foo
	void bar() {
		System.out.println("foobar");
	}
	
	public static void main(String[] args) {
		TestClass tc = new TestClass();
		tc.bar();
	}
}
