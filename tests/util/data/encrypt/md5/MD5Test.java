package util.data.encrypt.md5;

import static org.junit.Assert.*;

import org.junit.Test;

public class MD5Test {

	@Test
	public void testHelloWorld() {
		assertEquals("5eb63bbbe01eeed093cb22bb8f5acdc3", MD5.encrypt("hello world"));
	}

	@Test
	public void testEmpty() {
		assertEquals("", MD5.encrypt(""));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testNull() {
		MD5.encrypt(null);

		fail();
	}
}
