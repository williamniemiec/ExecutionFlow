package executionFlow.info;

import java.util.Arrays;


/**
 * Stores information about a class' constructor.
 * 
 * @author William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @since 1.0
 * @version 1.0
 */
public class ClassConstructorInfo 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private Class<?>[] constructorTypes;
	private Object[] constructorArgs;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	/**
	 * @param constructorTypes Parameter's types of the constructor
	 * @param constructorArgs Parameter's values of the constructor
	 */
	public ClassConstructorInfo(Class<?>[] constructorTypes, Object... constructorArgs) 
	{
		this.constructorTypes = constructorTypes;
		this.constructorArgs = constructorArgs;
	}


	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	public String toString() 
	{
		return "ClassConstructorInfo [initTypes=" 
				+ Arrays.toString(constructorTypes) + ", initArgs="
				+ Arrays.toString(constructorArgs) + "]";
	}
	
	
	//-------------------------------------------------------------------------
	//		Getters
	//-------------------------------------------------------------------------
	public Class<?>[] getConstructorTypes() 
	{
		return constructorTypes;
	}
	
	public Object[] getConstructorArgs() 
	{
		return constructorArgs;
	}
}
