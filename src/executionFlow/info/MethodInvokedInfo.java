package executionFlow.info;

import static java.lang.invoke.MethodType.methodType;

import java.lang.invoke.MethodType;
import java.nio.file.Path;
import java.util.Arrays;


/**
 * Stores information about a method.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		3.0.0
 * @since		2.0.0
 */
public class MethodInvokedInfo extends InvokedInfo
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private String methodName;
	private Class<?> returnType;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	/**
	 * Stores information about a method.
	 * 
	 * @param		binPath Method compiled file path
	 * @param		srcPath Path where source file is
	 * @param		invocationLine Line of test method where method is called
	 * @param		methodSignature Signature of the method
	 * @param		methodName Method's name
	 * @param		returnType Return type of the method
	 * @param		parameterTypes Types of method's parameters
	 * @param		args Method's arguments
	 */
	private MethodInvokedInfo(Path binPath, Path srcPath, int invocationLine,
			String methodSignature, String methodName,
			Class<?> returnType, Class<?>[] parameterTypes, Object[] args) 
	{
		this.binPath = binPath;
		this.srcPath = srcPath;
		this.invokedSignature = methodSignature;
		this.methodName = methodName;
		this.returnType = returnType;
		this.parameterTypes = parameterTypes;
		this.args = args;
		this.invocationLine = invocationLine;
	}

	
	//-------------------------------------------------------------------------
	//		Builder
	//-------------------------------------------------------------------------
	/**
	 * Builder for {@link MethodInvokedInfo}. It is necessary to provide all 
	 * required fields. The required fields are: <br />
	 * <ul>
	 * 	<li>binPath</li>
	 * 	<li>srcPath</li>
	 * 	<li>methodSignature</li>
	 * </ul>
	 */
	public static class Builder
	{
		private String methodName;
		private Path binPath;
		private Path srcPath;
		private String invokedSignature;
		private int invocationLine;
		private Class<?>[] parameterTypes;
		private Object[] args;
		private Class<?> returnType = void.class;
		
		
		/**
		 * @param		methodName Method's name
		 * 
		 * @return		Builder to allow chained calls
		 * 
		 * @throws		IllegalArgumentException If methodName is null
		 */
		public Builder methodName(String methodName)
		{
			if (methodName == null)
				throw new IllegalArgumentException("Method's name cannot be null");
			
			this.methodName = methodName.trim();
			
			return this;
		}
		
		/**
		 * @param		binPath Path where method's compiled file is
		 * 
		 * @return		Builder to allow chained calls
		 * 
		 * @throws		IllegalArgumentException If binPath is null
		 */
		public Builder binPath(Path binPath)
		{
			if (binPath == null)
				throw new IllegalArgumentException("Method compiled file path cannot be null");
			
			this.binPath = binPath.isAbsolute() ? binPath : binPath.toAbsolutePath();
			
			return this;
		}
		
		/**
		 * @param		srcPath Path where method's source file is
		 * 
		 * @return		Builder to allow chained calls
		 * 
		 * @throws		IllegalArgumentException If srcPath is null
		 */
		public Builder srcPath(Path srcPath)
		{
			if (srcPath == null)
				throw new IllegalArgumentException("Method's source file cannot be null");
			
			this.srcPath = srcPath.isAbsolute() ? srcPath : srcPath.toAbsolutePath();
			
			return this;
		}
		
		/**
		 * @param		methodSignature Method signature (if it is an
		 * inner class method, there must be '$' between the class name and
		 * the inner class name).
		 * <h5>Example</h5>
		 * <ul>
		 * 	<li><b>Method name along with its parameters:</b> firstName(String)</li>
		 * 	<li><b>Class name:</b> Person</li>
		 * 	<li><b>Inner class name:</b> PersonBuilder</li>
		 * 	<li><b>Class package:</b> examples.builderPattern</li>
		 * 	<li><b>Inner class method signature:</b> examples.builderPattern.Person$PersonBuilder.firstName(String)</li>
		 * </ul>
		 * 
		 * @return		Builder to allow chained calls
		 * 
		 * @throws		IllegalArgumentException If methodSignature is null
		 */
		public Builder methodSignature(String methodSignature)
		{
			if (methodSignature == null)
				throw new IllegalArgumentException("Method signature cannot be null");
			
			this.invokedSignature = methodSignature.trim();
			
			return this;
		}
		
		/**
		 * @param		invocationLine Line of test method where method is 
		 * called
		 * 
		 * @return		Builder to allow chained calls
		 * 
		 * @throws		IllegalArgumentException If invocationLine is less than
		 * or equal to zero
		 */
		public Builder invocationLine(int invocationLine)
		{
			if (invocationLine <= 0)
				throw new IllegalArgumentException("Invocation line must be a number greater than zero");
			
			this.invocationLine = invocationLine;
			
			return this;
		}
		
		/**
		 * @param		parameterTypes Types of method's parameters
		 * 
		 * @return		Builder to allow chained calls
		 * 
		 * @throws		IllegalArgumentException If parameterTypes is null
		 */
		public Builder parameterTypes(Class<?>[] parameterTypes)
		{
			if (parameterTypes == null)
				throw new IllegalArgumentException("Types of method's parameters cannot be null");
			
			this.parameterTypes = parameterTypes;
			
			return this;
		}
		
		/**
		 * @param		args Method's arguments
		 * 
		 * @return		Builder to allow chained calls
		 * 
		 * @throws		IllegalArgumentException If args is null
		 */
		public Builder args(Object... args)
		{
			if (args == null)
				throw new IllegalArgumentException("Method's arguments cannot be null");
			
			this.args = args;
			
			return this;
		}

		/**
		 * @param		returnType Method return type
		 * 
		 * @return		Builder to allow chained calls
		 * 
		 * @throws		IllegalArgumentException If returnType is null
		 */
		public Builder returnType(Class<?> returnType)
		{
			if (returnType == null)
				throw new IllegalArgumentException("Method return type cannot be null");
			
			this.returnType = returnType;
			
			return this;
		}
		
		/**
		 * Creates {@link MethodInvokedInfo} with provided information. It is 
		 * necessary to provide all required fields.. The required fields 
		 * are: <br />
		 * <ul>
		 * 	<li>classPath</li>
		 * 	<li>srcPath</li>
		 * 	<li>methodSignature</li>
		 * </ul>
		 * 
		 * @return		ClassMethodInfo with provided information
		 * 
		 * @throws		IllegalArgumentException If any required field is null
		 */
		public MethodInvokedInfo build() throws IllegalArgumentException
		{
			StringBuilder nullFields = new StringBuilder();
			
			if (binPath == null)
				nullFields.append("classPath").append(", ");
			if (srcPath == null)
				nullFields.append("srcPath").append(", ");
			if (invokedSignature == null)
				nullFields.append("methodSignature").append(", ");
			
			if (nullFields.length() > 0)
				throw new IllegalArgumentException("Required fields cannot be null: "
						+ nullFields.substring(0, nullFields.length()-2));	// Removes last comma
			
			return new MethodInvokedInfo(
				binPath, srcPath, invocationLine, invokedSignature, 
				methodName, returnType, parameterTypes, args
			);
		}
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	public String toString() 
	{
		return "MethodInvoked ["
				+ "methodName=" + methodName 
				+ ", classPath=" + binPath 
				+ ", srcPath=" + srcPath
				+ ", classSignature=" + getClassSignature()
				+ ", classPackage=" + getPackage()
				+ ", methodSignature=" + invokedSignature 
				+ ", invocationLine=" + invocationLine 
				+ ", parameterTypes=" + Arrays.toString(parameterTypes) 
				+ ", args="	+ Arrays.toString(args) 
				+ ", returnType=" + returnType 
			+ "]";
	}
	
	
	//-------------------------------------------------------------------------
	//		Getters
	//-------------------------------------------------------------------------
	public String getMethodSignature()
	{
		return getInvokedSignature();
	}
	
	/**
	 * Gets method name.
	 * 
	 * @return		Method name
	 */
	public String getMethodName() 
	{
		return methodName;
	}

	/**
	 * Gets types from the method's parameters.
	 * 
	 * @return		Method parameters
	 */
	public Class<?>[] getParameterTypes() 
	{
		return parameterTypes;
	}
	
	/**
	 * Gets return type of the method.
	 * 
	 * @return		Method return type
	 */
	public Class<?> getReturnType()
	{
		return this.returnType;
	}
	
	/**
	 * Gets parameter types and return type of the method.
	 * 
	 * @return		Return type and parameter types of the method
	 */
	public MethodType getMethodTypes() 
	{
		if (args == null || args.length == 0)
			return methodType(returnType);
		
		return methodType(returnType, parameterTypes);
	}
}