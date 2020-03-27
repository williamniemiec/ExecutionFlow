package executionFlow;


/**
 * Ex:
 *  int x = 2;
 *  call(x);
 *  
 *  type = int.class; 
 *  parameterValue = 2
 */
public class MethodParam 
{
	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
	private Class<?> type;
	private Object parameterValue;
	
	
	//-----------------------------------------------------------------------
	//		Constructor
	//-----------------------------------------------------------------------
	MethodParam(Class<?> type, Object parameterValue) {
		this.type = type;
		this.parameterValue = parameterValue;
	}
	
	
	//-----------------------------------------------------------------------
	//		Getters & Setters
	//-----------------------------------------------------------------------
	public Class<?> getType() { return type; }
	
	public Object getParameterValue() { return parameterValue; }
}
