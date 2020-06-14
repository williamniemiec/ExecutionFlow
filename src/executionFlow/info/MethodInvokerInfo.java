package executionFlow.info;

import static java.lang.invoke.MethodType.methodType;

import java.lang.invoke.MethodType;
import java.nio.file.Path;
import java.util.Arrays;


/**
 * Stores information about a method.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		1.5
 * @since		1.0
 */
public class MethodInvokerInfo extends InvokerInfo
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
	 * @param		classPath Method class file path
	 * @param		srcPath Path where source file is
	 * @param		invocationLine Line of test method where method is called
	 * @param		methodSignature Signature of the method
	 * @param		methodName Method's name
	 * @param		returnType Return type of the method
	 * @param		parameterTypes Types of method's parameters
	 * @param		args Method's arguments
	 */
	private MethodInvokerInfo(Path classPath, Path srcPath, int invocationLine,
			String methodSignature, String methodName,
			Class<?> returnType, Class<?>[] parameterTypes, Object[] args) 
	{
		this.classPath = classPath;
		this.srcPath = srcPath;
		this.invokerSignature = methodSignature;
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
	 * Builder for {@link MethodInvokerInfo}. It is necessary to fill all 
	 * required fields. The required fields are: <br />
	 * <ul>
	 * 	<li>classPath</li>
	 * 	<li>srcPath</li>
	 * 	<li>methodSignature</li>
	 * </ul>
	 */
	public static class MethodInvokerInfoBuilder
	{
		private String methodName;
		private Path classPath;
		private Path srcPath;
		private String invokerSignature;
		private int invocationLine;
		private Class<?>[] parameterTypes;
		private Object[] args;
		private Class<?> returnType = void.class;
		
		
		/**
		 * @param		methodName Method's name
		 * @return		Builder to allow chained calls
		 * @throws		IllegalArgumentException If methodName is null
		 */
		public MethodInvokerInfoBuilder methodName(String methodName)
		{
			if (methodName == null)
				throw new IllegalArgumentException("Method's name cannot be null");
			
			this.methodName = methodName;
			
			return this;
		}
		
		/**
		 * @param		classPath Method class file path
		 * @return		Builder to allow chained calls
		 * @throws		IllegalArgumentException If classPath is null
		 */
		public MethodInvokerInfoBuilder classPath(Path classPath)
		{
			if (classPath == null)
				throw new IllegalArgumentException("Method class file path cannot be null");
			
			this.classPath = classPath.isAbsolute() ? classPath : classPath.toAbsolutePath();
			
			return this;
		}
		
		/**
		 * @param		srcPath Path where method's source file is
		 * @return		Builder to allow chained calls
		 * @throws		IllegalArgumentException If srcPath is null
		 */
		public MethodInvokerInfoBuilder srcPath(Path srcPath)
		{
			if (srcPath == null)
				throw new IllegalArgumentException("Method's source file cannot be null");
			
			this.srcPath = srcPath.isAbsolute() ? srcPath : srcPath.toAbsolutePath();
			
			return this;
		}
		
		/**
		 * @param		methodSignature Method signature
		 * @return		Builder to allow chained calls
		 * @throws		IllegalArgumentException If invokerSignature is null
		 */
		public MethodInvokerInfoBuilder methodSignature(String methodSignature)
		{
			if (methodSignature == null)
				throw new IllegalArgumentException("Method signature cannot be null");
			
			this.invokerSignature = methodSignature;
			
			return this;
		}
		
		/**
		 * @param		invocationLine Line of test method where method is 
		 * called
		 * @return		Builder to allow chained calls
		 * @throws		IllegalArgumentException If invocationLine is less than
		 * or equal to zero
		 */
		public MethodInvokerInfoBuilder invocationLine(int invocationLine)
		{
			if (invocationLine <= 0)
				throw new IllegalArgumentException("Invocation line must be a number greater than zero");
			
			this.invocationLine = invocationLine;
			
			return this;
		}
		
		/**
		 * @param		parameterTypes Types of method's parameters
		 * @return		Builder to allow chained calls
		 * @throws		IllegalArgumentException If parameterTypes is null
		 */
		public MethodInvokerInfoBuilder parameterTypes(Class<?>[] parameterTypes)
		{
			if (parameterTypes == null)
				throw new IllegalArgumentException("Types of method's parameters cannot be null");
			
			this.parameterTypes = parameterTypes;
			
			return this;
		}
		
		/**
		 * @param		args Method's arguments
		 * @return		Builder to allow chained calls
		 * @throws		IllegalArgumentException If args is null
		 */
		public MethodInvokerInfoBuilder args(Object... args)
		{
			if (args == null)
				throw new IllegalArgumentException("Method's arguments cannot be null");
			
			this.args = args;
			
			return this;
		}

		/**
		 * @param		returnType Method return type
		 * @return		Builder to allow chained calls
		 * @throws		IllegalArgumentException If returnType is null
		 */
		public MethodInvokerInfoBuilder returnType(Class<?> returnType)
		{
			if (returnType == null)
				throw new IllegalArgumentException("Method return type cannot be null");
			
			this.returnType = returnType;
			
			return this;
		}
		
		/**
		 * Creates {@link MethodInvokerInfo} with provided information. It is
		 * necessary that required fields must be filled. The required 
		 * fields are: <br />
		 * <ul>
		 * 	<li>classPath</li>
		 * 	<li>srcPath</li>
		 * 	<li>methodSignature</li>
		 * </ul>
		 * 
		 * @return		ClassMethodInfo with provided information
		 * @throws		IllegalArgumentException If any required field is null
		 */
		public MethodInvokerInfo build() throws IllegalArgumentException
		{
			StringBuilder nullFields = new StringBuilder();
			
			if (classPath == null)
				nullFields.append("classPath").append(", ");
			if (srcPath == null)
				nullFields.append("srcPath").append(", ");
			if (invokerSignature == null)
				nullFields.append("methodSignature").append(", ");
			
			if (nullFields.length() > 0)
				throw new IllegalArgumentException("Required fields cannot be null: "
						+ nullFields.substring(0, nullFields.length()-2));	// Removes last comma
			
			return new MethodInvokerInfo(
				classPath, srcPath, invocationLine, invokerSignature, 
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
		return "MethodInvoker ["
				+ "methodName=" + methodName 
				+ ", classPath=" + classPath 
				+ ", srcPath=" + srcPath
				+ ", classSignature=" + getClassSignature()
				+ ", classPackage=" + getPackage()
				+ ", methodSignature=" + invokerSignature 
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
		return getInvokerSignature();
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
	 * Gets values from the method's arguments.
	 * 
	 * @return		Values from the method's arguments
	 */
	public Object[] getArgs() 
	{
		return args;
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