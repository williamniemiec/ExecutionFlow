package executionFlow.runtime;


/**
 * Class created for aspect tests
 */
public class Main {
	@Test
	public static void main(String[] args) {
		int k = 0;
		
		System.out.println("print - main");
		
		while (k < 10){
			k++;
		}
		
		TestClass t = new TestClass(55);
		t.test(2,3, "teste");
		t.test2();
		TestClass.test3(2,3,"teste");
		TestClass.test4();
	}
}
