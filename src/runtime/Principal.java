package runtime;


class Te {
	public void teste(int x, int y, String k) {
		x = 3;
	}
	
	public void teste2() {
	}
	
	public static void teste3(int x, int y, String k) {
		x = 3;
	}
	
	public static void teste4() {
	}
}

public class Principal {
	//@Pointcut("execution(* *.println(*))")
	@Test
	public static void main(String[] args) {
		/*
		//System.out.println(Arrays.toString(Thread.currentThread().getStackTrace()));
		System.out.println("hello world");
		System.out.println("proc1");
		System.out.println("proc2");
		System.out.println("proc3");
		*/
		int k = 0;
		while (k < 10){
			k++;
		}
		
		Te t = new Te();
		t.teste(2,3, "teste");
		t.teste2();
		Te.teste3(2,3,"teste");
		Te.teste4();
	}
}
