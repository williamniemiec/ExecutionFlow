package executionFlow.info;

public class SignaturesInfo 
{
	private String methodSignature;
	private String testMethodSignature;
	
	public SignaturesInfo(String methodSignature, String testMethodSignature) {
		this.methodSignature = methodSignature;
		this.testMethodSignature = testMethodSignature;
	}
	
	public SignaturesInfo(String methodSignature) {
		this.methodSignature = methodSignature;
	}
	
	public boolean hasTestMethodSignature()
	{
		return testMethodSignature != null;
	}
	
	
	public String getMethodSignature() {
		return methodSignature;
	}
	
	public String getTestMethodSignature() {
		return testMethodSignature;
	}
}
