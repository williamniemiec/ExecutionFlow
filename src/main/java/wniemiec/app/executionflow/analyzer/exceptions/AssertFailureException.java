package wniemiec.app.executionflow.analyzer.exceptions;

public class AssertFailureException extends RuntimeException {

	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------	
	private static final long serialVersionUID = 1L;

	
	//-------------------------------------------------------------------------
	//		Constructors
	//-------------------------------------------------------------------------	
	public AssertFailureException(String message) {
		super(message);
	}

	public AssertFailureException() {
		super();
	}
}
