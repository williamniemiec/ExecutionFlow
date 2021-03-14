package auxfiles.loop;

import static org.junit.Assert.assertEquals;

public class Loop {
	
	@org.junit.Test
	public void testForConstructorAndMethod() {
		long before = 1;
		
		for (int i=1; i<=4; i++) {
			LoopAuxClass tc = new LoopAuxClass(i);
			assertEquals(i*before, tc.factorial_constructor());
			
			before = tc.factorial_constructor();
		}
	}
	
	@org.junit.Test 
	public void moreOneConstructor() {
		LoopAuxClass tcct = new LoopAuxClass();
		LoopAuxClass tcct2 = new LoopAuxClass(true);
		
		assertEquals(24, tcct.factorial(4));
		assertEquals(-1, tcct2.factorial(4));
	}
	
	@org.junit.Test
	public void moreOneConstructorAndStaticMethod() {
		LoopAuxClass tcct = new LoopAuxClass();
		LoopAuxClass tcct2 = new LoopAuxClass(true);
		
		assertEquals(-1, tcct2.factorial(4));
		assertEquals(24, tcct.factorial(4));
		
		assertEquals(24, LoopAuxClass.staticFactorial(4));
	}
}
