package wniemiec.executionflow.io.processing.manager;

import org.junit.Test;

public class testmethod {

	@Test
	public void method1() {
		testedinvoked.m3(2);
	}
	
	@org.junit.Test
	public void method2() {
		// code
	}
	
	@wniemiec.executionflow.runtime.CollectMethodsCalled @Test
	public void method3() {
		// code
	}
	
	@Test
	public void method4() {
		new testedinvoked(0);
	}
	
	public void method5() {
		// code
	}
}
