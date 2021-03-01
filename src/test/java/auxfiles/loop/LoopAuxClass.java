package auxfiles.loop;

public class LoopAuxClass {

	private boolean skip;
	private int num;

	public LoopAuxClass(boolean skip, int num) {
		this.skip = skip;
		this.num = num;
	}

	public LoopAuxClass(boolean skip) {
		this(skip, 0);
		factorial(1);
	}

	public LoopAuxClass(int num) {
		this(false, num);
	}

	public LoopAuxClass() {
		this(false, 0);
	}

	/**
	 * Calculates the factorial of a number.
	 * 
	 * @param x Number you want to know the factorial
	 * @return Factorial of the number or -1 if skip is true
	 */
	public long factorial(long x) {
		if (skip)
			return -1;

		long response = 1;

		for (int i = 1; i <= x; i++) {
			response *= i;
		}

		return response;
	}

	/**
	 * Calculates the factorial of a number.
	 * 
	 * @param x Number you want to know the factorial
	 * @return Factorial of the number
	 */
	public static long staticFactorial(int x) {
		long response = 1;

		for (int i = 1; i <= x; i++) {
			response *= i;
		}

		return response;
	}

	/**
	 * Calculates the factorial of a number based on {@link #num the number passed
	 * to the constructor}.
	 * 
	 * @return Factorial of the number
	 */
	public long factorial_constructor() {
		long response = 1;

		for (int i = 1; i <= num; i++) {
			response *= i;
		}

		return response;
	}

	/**
	 * Calculates x-th fibonacci term.
	 * 
	 * @param x Fibonacci index term
	 * @return x-th fibonacci term or -1 if skip is true
	 */
	public long fibonacci(int x) {
		if (skip)
			return -1;

		long response = 1;
		long last = 1;
		long aux = 0;

		for (int i = 2; i < x; i++) {
			aux = response;
			response = response + last;
			last = aux;
		}

		return response;
	}
}
