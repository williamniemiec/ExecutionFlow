package auxfiles.aspectj;

public aspect AspectJTest {
	
	pointcut fooBarSignature(): execution(void auxfiles.aspectj.TestClass.bar());
	
	before(): fooBarSignature() {
		System.out.println("Inside foo.bar signature");
	}
}
