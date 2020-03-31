package executionFlow.runtime;


/**
 * Class created for aspect tests
 */
public class TestClass {
	public TestClass(int x) {
		x = 2;
	}
	
	public TestClass(int x, int y) {
		x = 2;
	}
	
	public TestClass(String x) {
		x = "2";
	}
	
	public void test(int x, int y, String k) {
		x = 3;
	}
	
	public void test2() {	}
	
	public static void test3(int x, int y, String k) {
		x = 0;
		while (x < 3) {
			x++;
		}
		
		x = 3;
	}
	
	public static void test4() {
	}
	
	public long factorial(int x) {
		long response = 1;
		
		for (int i=1; i<=x; i++) {
			response *= i;
		}
		
		return response;
	}
}
