package executionFlow.info;

import java.nio.file.Path;
import java.util.Arrays;


/**
 * Stores information about a constructor.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		4.0.0
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
	 * @param		binPath Method compiled file path
	 * @param		srcPath Path where source file is
	 * @param		invocationLine Line of test method where constructor is called
	 * @param		constructorSignature Signature of the constructor
	 * @param		parameterTypes Types of constructor's parameters
	 * @param		args Constructor's arguments
	 */
	private ConstructorInvokedInfo(Path binPath, Path srcPath, int invocationLine,
			String constructorSignature, Class<?>[] parameterTypes, Object[] args) 
	{
		this.binPath = binPath;
		this.srcPath = srcPath;
		this.invocationLine = invocationLine;
		this.invokedSignature = constructorSignature;
		this.parameterTypes = parameterTypes;
		this.args = args;
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
	public static class Builder
	{
		private Path binPath;
		private Path srcPath;
		private String invokedSignature; 
		private int invocationLine;
		private Class<?>[] parameterTypes;
		private Object[] args;
		
		
		/**
		 * @param		binPath Constructor compiled file path
		 * 
		 * @return		Builder to allow chained calls
		 * 
		 * @throws		IllegalArgumentException If binPath is null
		 */
		public Builder binPath(Path binPath)
		{
			if (binPath == null)
				throw new IllegalArgumentException("Constructor compiled file path cannot be null");
			
			this.binPath = binPath.isAbsolute() ? binPath : binPath.toAbsolutePath();
			
			return this;
		}
		
		/**
		 * @param		srcPath Path where constructor's source file is
		 * 
		 * @return		Builder to allow chained calls
		 * 
		 * @throws		IllegalArgumentException If srcPath is null
		 */
		public Builder srcPath(Path srcPath)
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
		public Builder constructorSignature(String constructorSignature)
		{
			if (constructorSignature == null)
				throw new IllegalArgumentException("Constructor signature cannot be null");
			
			this.invokedSignature = constructorSignature;
			
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
		public Builder invocationLine(int invocationLine)
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
		public Builder parameterTypes(Class<?>[] parameterTypes)
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
		public Builder args(Object... args)
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
			
			
			if (binPath == null)
				nullFields.append("classPath").append(", ");
			if (srcPath == null)
				nullFields.append("srcPath").append(", ");
			if (invokedSignature == null)
				nullFields.append("constructorSignature").append(", ");
			
			if (nullFields.length() > 0)
				throw new IllegalArgumentException("Required fields cannot be null: "
						+ nullFields.substring(0, nullFields.length()-2));	// Removes last comma
			
			return new ConstructorInvokedInfo(
				binPath, srcPath, invocationLine, invokedSignature, 
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
				+ "binPath=" + binPath 
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
	//		Getters & Setters
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
	
	/**
	 * Sets class signature.
	 * 
	 * @param		classSignature Class signature to be set
	 * 
	 * @return		Itself to allow chained calls
	 * 
	 * @implSpec	This function will update {@link #invokedSignature}
	 */
	public ConstructorInvokedInfo setClassSignature(String classSignature)
	{
		this.invokedSignature = this.invokedSignature.replace(getClassSignature(), classSignature);
		this.classSignature = classSignature;
		
		return this;
	}
	
	/**
	 * Sets invoked signature.
	 * 
	 * @param		signature New signature
	 * 
	 * @return		Itself to allow chained calls
	 */
	public ConstructorInvokedInfo setInvokedSignature(String signature)
	{
		this.invokedSignature = signature;
		
		return this;
	}
	
//	/**
//	 * Sets source file path.
//	 * 
//	 * @param		srcPath Source file path
//	 * 
//	 * @return		Itself to allow chained calls
//	 */
//	public ConstructorInvokedInfo setSrcPath(Path srcPath)
//	{
//		this.srcPath = srcPath;
//		
//		return this;
//	}
	
//	/**
//	 * Sets compiled file path.
//	 * 
//	 * @param		binPath Compiled file path
//	 * 
//	 * @return		Itself to allow chained calls
//	 */
//	public ConstructorInvokedInfo setBinPath(Path binPath)
//	{
//		this.binPath = binPath;
//		
//		return this;
//	}
}
