package executionFlow.info;

import java.nio.file.Path;
import java.util.Arrays;


/**
 * Stores information about a class' constructor.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		2.0.0
 * @since		2.0.0
 */
public class ConstructorInvokedInfo extends InvokedInfo
{
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	/**
	 * Stores information about a method.
	 * 
	 * @param		classPath Method class file path
	 * @param		srcPath Path where source file is
	 * @param		invocationLine Line of test method where constructor is called
	 * @param		constructorSignature Signature of the constructor
	 * @param		parameterTypes Types of constructor's parameters
	 * @param		args Constructor's arguments
	 */
	private ConstructorInvokedInfo(Path classPath, Path srcPath, int invocationLine,
			String constructorSignature, Class<?>[] parameterTypes, Object[] args) 
	{
		this.binPath = classPath;
		this.srcPath = srcPath;
		this.invokedSignature = constructorSignature;
		this.parameterTypes = parameterTypes;
		this.args = args;
		this.invocationLine = invocationLine;
	}
	
	
	//-------------------------------------------------------------------------
	//		Builder
	//-------------------------------------------------------------------------
	/**
	 * Builder for {@link ConstructorInvokedInfo}. It is necessary to fill all 
	 * required fields. The required fields are: <br />
	 * <ul>
	 * 	<li>classPath</li>
	 * 	<li>srcPath</li>
	 * 	<li>constructorSignature</li>
	 * </ul>
	 */
	public static class ConstructorInvokerInfoBuilder
	{
		private Path classPath;
		private Path srcPath;
		private String invokerSignature; 
		private int invocationLine;
		private Class<?>[] parameterTypes;
		private Object[] args;
		
		
		/**
		 * @param		classPath Constructor class file path
		 * 
		 * @return		Builder to allow chained calls
		 * 
		 * @throws		IllegalArgumentException If classPath is null
		 */
		public ConstructorInvokerInfoBuilder classPath(Path classPath)
		{
			if (classPath == null)
				throw new IllegalArgumentException("Constructor class file path cannot be null");
			
			this.classPath = classPath.isAbsolute() ? classPath : classPath.toAbsolutePath();
			
			return this;
		}
		
		/**
		 * @param		srcPath Path where constructor's source file is
		 * 
		 * @return		Builder to allow chained calls
		 * 
		 * @throws		IllegalArgumentException If srcPath is null
		 */
		public ConstructorInvokerInfoBuilder srcPath(Path srcPath)
		{
			if (srcPath == null)
				throw new IllegalArgumentException("Constructor's source file cannot be null");
			
			this.srcPath = srcPath.isAbsolute() ? srcPath : srcPath.toAbsolutePath();
			
			return this;
		}
		
		/**
		 * @param		constructorSignature Constructor signature (if it is an
		 * inner class constructor, there must be '$' between the class name and
		 * the inner class name).
		 * <h5>Example</h5>
		 * <ul>
		 * 	<li><b>Class name:</b> Person</li>
		 * 	<li><b>Inner class name:</b> PersonBuilder</li>
		 * 	<li><b>Class package:</b> examples.builderPattern</li>
		 * 	<li><b>Inner class constructor signature:</b> examples.builderPattern.Person$PersonBuilder()</li>
		 * </ul>
		 * 
		 * @return		Builder to allow chained calls
		 * 
		 * @throws		IllegalArgumentException If constructorSignature is null
		 */
		public ConstructorInvokerInfoBuilder constructorSignature(String constructorSignature)
		{
			if (constructorSignature == null)
				throw new IllegalArgumentException("Constructor signature cannot be null");
			
			this.invokerSignature = constructorSignature;
			
			return this;
		}
		
		/**
		 * @param		invocationLine Line of test method where constructor is 
		 * called
		 * 
		 * @return		Builder to allow chained calls
		 * 
		 * @throws		IllegalArgumentException If invocationLine is less than
		 * or equal to zero
		 */
		public ConstructorInvokerInfoBuilder invocationLine(int invocationLine)
		{
			if (invocationLine <= 0)
				throw new IllegalArgumentException("Invocation line must be a number greater than zero");
			
			this.invocationLine = invocationLine;
			
			return this;
		}
		
		/**
		 * @param		parameterTypes Types of constructor's parameters
		 * 
		 * @return		Builder to allow chained calls
		 * 
		 * @throws		IllegalArgumentException If parameterTypes is null
		 */
		public ConstructorInvokerInfoBuilder parameterTypes(Class<?>[] parameterTypes)
		{
			if (parameterTypes == null)
				throw new IllegalArgumentException("Types of constructor's parameters cannot be null");
			
			this.parameterTypes = parameterTypes;
			
			return this;
		}
		
		/**
		 * @param		args Constructor's arguments
		 * 
		 * @return		Builder to allow chained calls
		 * 
		 * @throws		IllegalArgumentException If args is null
		 */
		public ConstructorInvokerInfoBuilder args(Object... args)
		{
			if (args == null)
				throw new IllegalArgumentException("Constructor's arguments cannot be null");
			
			this.args = args;
			
			return this;
		}
		
		/**
		 * Creates {@link ConstructorInvokedInfo} with provided information. It is
		 * necessary that required fields must be filled. The required 
		 * fields are: <br />
		 * <ul>
		 * 	<li>classPath</li>
		 * 	<li>srcPath</li>
		 * 	<li>constructorSignature</li>
		 * </ul>
		 * 
		 * @return		ClassConstructorInfo with provided information
		 * 
		 * @throws		IllegalArgumentException If any required field is null
		 */
		public ConstructorInvokedInfo build() throws IllegalArgumentException
		{
			StringBuilder nullFields = new StringBuilder();
			
			if (classPath == null)
				nullFields.append("classPath").append(", ");
			if (srcPath == null)
				nullFields.append("srcPath").append(", ");
			if (invokerSignature == null)
				nullFields.append("constructorSignature").append(", ");
			
			if (nullFields.length() > 0)
				throw new IllegalArgumentException("Required fields cannot be null: "
						+ nullFields.substring(0, nullFields.length()-2));	// Removes last comma
			
			return new ConstructorInvokedInfo(
				classPath, srcPath, invocationLine, invokerSignature, 
				parameterTypes, args
			);
		}
	}
	

	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	public String toString() 
	{
		return "ConstructorInvokedInfo ["
				+ "classPath=" + binPath 
				+ ", srcPath=" + srcPath
				+ ", classSignature=" + getClassSignature()
				+ ", classPackage=" + getPackage()
				+ ", constructorSignature=" + invokedSignature 
				+ ", invocationLine=" + invocationLine 
				+ ", parameterTypes=" + Arrays.toString(parameterTypes) 
				+ ", args="	+ Arrays.toString(args) 
			+ "]";
	}
	
	
	//-------------------------------------------------------------------------
	//		Getters
	//-------------------------------------------------------------------------
	@Override
	public String getClassSignature()
	{
		if (classSignature == null)
			classSignature = invokedSignature.split("\\(")[0];
		
		return classSignature; 
	}
	
	public String getConstructorSignature()
	{
		return getInvokedSignature();
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
}