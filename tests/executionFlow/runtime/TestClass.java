package executionFlow.runtime;

import java.util.Arrays;

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
	
	public static void test4() { }
	
	public long factorial(int x) {
		long response = 1;
		
		for (int i=1; i<=x; i++) {
			response *= i;
		}
		
		return response;
	}
	
	public long fibonacci(int x) {
		long response = 1;
		long last = 1;
		long aux = 0;

		for (int i=2; i<x; i++) {
			aux = response;
			response = response + last;
			last = aux;
		}
		
		return response;
	}
	
	public static char[] parseLetters_noInternalCall(char[] letters) {
		char[] response = new char[letters.length];
		
		for (int i=0; i<letters.length; i++) {
			if (letters[i] == Character.toUpperCase(letters[i]))
				response[i] =  Character.toLowerCase(letters[i]);
			else
				response[i] =  Character.toUpperCase(letters[i]);
		}
		
		return response;
	}
	
	public static char[] parseLetters_withInternalCall(char[] letters) {
		char[] response = new char[letters.length];
		
		for (int i=0; i<letters.length; i++) {
			// Internal call - should not be considered the path made within the call
			response[i] = invertLetter(letters[i]);
		}
		
		return response;
	}
	
	private static char invertLetter(char letter) {
		if (letter == Character.toUpperCase(letter)) {
			return Character.toLowerCase(letter);
		}
		
		return Character.toUpperCase(letter);
	}
}
