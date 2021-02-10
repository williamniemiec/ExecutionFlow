package wniemiec.executionflow.info;

import static java.lang.invoke.MethodType.methodType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.invoke.MethodType;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Stores information about an invoked, where an invoked can be a 
 * method or a constructor. 
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		6.0.0
 * @since		2.0.0
 */
public class InvokedInfo implements Serializable {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final long serialVersionUID = 600L;
	private transient Path binPath;
	private transient Path srcPath;
	private String invokedSignature;
	private String classSignature;
	private String classPackage;
	private int invocationLine;
	private Class<?>[] parameterTypes;
	private transient Object[] args;
	private String concreteMethodSignature;
	private String invokedName;
	private Class<?> returnType;
	private boolean isConstructor;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	private InvokedInfo(Path binPath, Path srcPath, int invocationLine,	
						String invokedSignature, String invokedName, 
						Class<?> returnType, Class<?>[] parameterTypes, 
						Object[] args, boolean isConstructor) {
		checkInvokedSignature(invokedSignature);
		
		this.binPath = binPath;
		this.srcPath = srcPath;
		this.invocationLine = invocationLine;
		this.invokedSignature = invokedSignature;
		this.invokedName = invokedName;
		this.returnType = returnType;
		this.parameterTypes = parameterTypes;
		this.args = args;
		
		if (isConstructor)
			this.classSignature = invokedSignature;
		
		this.isConstructor = isConstructor;
	}
	
	
	//-------------------------------------------------------------------------
	//		Builder
	//-------------------------------------------------------------------------
	/**
	 * Builder for {@link InvokedInfo}. It is necessary to provide all required
	 * fields. The required fields are: <br />
	 * <ul>
	 * 	<li>invokedSignature</li>
	 * </ul>
	 */
	public static class Builder	{
		
		private String invokedName;
		private Path binPath;
		private Path srcPath;
		private String invokedSignature;
		private int invocationLine;
		private Class<?>[] parameterTypes;
		private Object[] args;
		private Class<?> returnType = void.class;
		private boolean isConstructor = false;
		
		
		/**
		 * @param		invokedName Invoked's name
		 * 
		 * @return		Builder to allow chained calls
		 * 
		 * @throws		IllegalArgumentException If methodName is null
		 */
		public Builder invokedName(String name)	{
			if (name == null)
				throw new IllegalArgumentException("Invoked name cannot be null");
			
			this.invokedName = name.trim();
			
			return this;
		}
		
		/**
		 * @param		binPath Path where invoked's compiled file is
		 * 
		 * @return		Builder to allow chained calls
		 * 
		 * @throws		IllegalArgumentException If binPath is null
		 */
		public Builder binPath(Path binPath) {
			checkBinPath(binPath);
			
			this.binPath = binPath.isAbsolute() ? binPath : binPath.toAbsolutePath();
			
			return this;
		}
		
		/**
		 * @param		srcPath Path where invoked's source file is
		 * 
		 * @return		Builder to allow chained calls
		 * 
		 * @throws		IllegalArgumentException If srcPath is null
		 */
		public Builder srcPath(Path srcPath) {
			checkSrcPath(srcPath);
			
			this.srcPath = srcPath.isAbsolute() ? srcPath : srcPath.toAbsolutePath();
			
			return this;
		}
		
		/**
		 * @param		invokedSignature Invoked signature (if it is an
		 * inner class method, there must be '$' between the class name and
		 * the inner class name).
		 * <h5>Example</h5>
		 * <ul>
		 * 	<li><b>Method name along with its parameters:</b> firstName(String)</li>
		 * 	<li><b>Class name:</b> Person</li>
		 * 	<li><b>Inner class name:</b> PersonBuilder</li>
		 * 	<li><b>Class package:</b> examples.builderPattern</li>
		 * 	<li><b>Inner class method signature:</b> examples.builderPattern
		 * 	.Person$PersonBuilder.firstName(String)</li>
		 * </ul>
		 * 
		 * @return		Builder to allow chained calls
		 * 
		 * @throws		IllegalArgumentException If methodSignature is null
		 */
		public Builder invokedSignature(String signature) {
			checkInvokedSignature(signature);
			
			this.invokedSignature = signature.trim();
			
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
		public Builder invocationLine(int invocationLine) {
			if (invocationLine <= 0) {
				throw new IllegalArgumentException("Invocation line must be a "
						+ "number greater than zero");
			}
			
			this.invocationLine = invocationLine;
			
			return this;
		}
		
		public Builder isConstructor(boolean isConstructor) {
			this.isConstructor = isConstructor;
			
			return this;
		}
		
		/**
		 * @param		parameterTypes Types of invoked's parameters
		 * 
		 * @return		Builder to allow chained calls
		 * 
		 * @throws		IllegalArgumentException If parameterTypes is null
		 */
		public Builder parameterTypes(Class<?>[] parameterTypes) {
			if (parameterTypes == null) {
				throw new IllegalArgumentException("Types of invoked's "
						+ "parameters cannot be null");
			}
			
			this.parameterTypes = parameterTypes;
			
			return this;
		}
		
		/**
		 * @param		args Invoked's arguments
		 * 
		 * @return		Builder to allow chained calls
		 * 
		 * @throws		IllegalArgumentException If args is null
		 */
		public Builder args(Object... args) {
			if (args == null) {
				throw new IllegalArgumentException("Invoked's arguments cannot "
						+ "be null");
			}
			
			this.args = args;
			
			return this;
		}

		/**
		 * @param		returnType Invoked return type
		 * 
		 * @return		Builder to allow chained calls
		 * 
		 * @throws		IllegalArgumentException If returnType is null
		 */
		public Builder returnType(Class<?> returnType)
		{
			if (returnType == null) {
				throw new IllegalArgumentException("Invoked return type cannot "
						+ "be null");
			}
			
			this.returnType = returnType;
			
			return this;
		}
		
		/**
		 * Creates {@link InvokedInfo} with provided information. It is 
		 * necessary to provide all required fields.. The required fields 
		 * are: <br />
		 * <ul>
		 * 	<li>invokedSignature</li>
		 * </ul>
		 * 
		 * @return		InvokedInfo with provided information
		 * 
		 * @throws		IllegalArgumentException If any required field is null
		 */
		public InvokedInfo build() {
			return new InvokedInfo(
				binPath, srcPath, invocationLine, invokedSignature, 
				invokedName, returnType, parameterTypes, args, isConstructor
			);
		}
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	private static void checkBinPath(Path binPath) {
		if (binPath == null) {
			throw new IllegalArgumentException("Invoked's compiled file path "
					+ "cannot be null");
		}
	}
	
	private static void checkSrcPath(Path srcPath) {
		if (srcPath == null) {
			throw new IllegalArgumentException("Invoked's source file path"
					+ "cannot be null");
		}
	}
	
	private static void checkInvokedSignature(String signature) {
		if (signature == null) {
			throw new IllegalArgumentException("Invoked signature cannot be null");
		}
	}
	
	/**
	 * Extracts class name from a signature.
	 * 
	 * @param	signature Signature of a method or class
	 * 
	 * @return	Name of this class or method
	 */
	public static String extractMethodName(String signature) {
		String methodName = "";
		
		Pattern p = Pattern.compile("\\.[A-z0-9-_$]+\\(");
		Matcher m = p.matcher(signature);
		
		if (m.find()) {
			methodName = m.group();					// ".<methodName>("
			p = Pattern.compile("[A-z0-9\\-\\_\\$]+");
			m = p.matcher(methodName);
			
			if (m.find())
				methodName = m.group();				// "<methodName>"
		}
		
		return methodName;
	}
	
	/**
	 * Extracts package from a class signature.
	 * 
	 * @param		classSignature Signature of the class
	 * 
	 * @return		Class package
	 */
	public static String extractPackage(String classSignature) {
		if (classSignature == null || classSignature.isEmpty())
			return "";
		
		String[] tmp = classSignature.split("\\.");
		StringBuilder pkg = new StringBuilder();
		
		for (int i=0; i<tmp.length-1; i++) {
			pkg.append(tmp[i]);
			pkg.append(".");
		}
		
		// Removes last dot
		if (pkg.length() > 0)
			pkg.deleteCharAt(pkg.length()-1);
		
		return pkg.toString();
	}
	
	/**
	 * Checks whether the invoked belongs to an anonymous class.
	 * 
	 * @return		If the invoked belongs to an anonymous class
	 */
	public boolean belongsToAnonymousClass() {
		String[] terms = getClassSignature().split("\\$");

		if (terms.length <= 1)
			return false;
		
		return terms[terms.length-1].matches("[0-9]+");
	}	
	
	@Override
	public String toString() {
		return "InvokedInfo ["
				+ "invokedName=" + invokedName 
				+ ", binPath=" + binPath 
				+ ", srcPath=" + srcPath
				+ ", classSignature=" + getClassSignature()
				+ ", classPackage=" + getPackage()
				+ ", invokedSignature=" + invokedSignature 
				+ ", invocationLine=" + invocationLine 
				+ ", parameterTypes=" + Arrays.toString(parameterTypes) 
				+ ", args="	+ Arrays.toString(args) 
				+ ", returnType=" + returnType 
			+ "]";
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		
		result = prime * result + ((binPath == null) ? 0 : binPath.hashCode());
		result = prime * result + ((invocationLine <= 0) ? 0 : invocationLine);
		result = prime * result + ((invokedSignature == null) ? 0 : invokedSignature.hashCode());
		result = prime * result + ((srcPath == null) ? 0 : srcPath.hashCode());
		
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		InvokedInfo other = (InvokedInfo) obj;
		
		if (binPath == null) {
			if (other.binPath != null)
				return false;
		} 
		else if (!binPath.equals(other.binPath))
			return false;
		
		if ((invocationLine > 0) && invocationLine != other.invocationLine)
			return false;
		
		if (srcPath == null) {
			if (other.srcPath != null)
				return false;
		} 
		else if (!srcPath.equals(other.srcPath))
			return false;
		
		return invokedSignature.equals(other.invokedSignature);
	}

	//-------------------------------------------------------------------------
	//		Getters & Setters
	//-------------------------------------------------------------------------
	/**
	 * Gets directory where a compiled file is.
	 * 
	 * @param		compiledFilePath Compiled file path
	 * 
	 * @return		Directory where the compiled test method file is
	 */
	public static Path getCompiledFileDirectory(Path compiledFilePath) {
		return compiledFilePath.getParent();
	}

	public Path getBinPath() {
		return this.binPath;
	}
	
	public Path getSrcPath() {
		return this.srcPath;
	}

	public String getInvokedSignature() {
		return this.invokedSignature;
	}
	
	/**
	 * Gets class signature. 
	 * 
	 * @return		Class signature
	 * 
	 * @implNote	Lazy initialization
	 */
	public String getClassSignature() {
		if (classSignature == null)
			classSignature = extractClassSignatureFromInvokedSignature();
		
		return classSignature; 
	}
	
	private String extractClassSignatureFromInvokedSignature() {
		if ((invokedSignature == null) || invokedSignature.isBlank())
			return "";
		
		StringBuilder signature = new StringBuilder();
		String[] terms = invokedSignature.split("\\.");

		for (int i=0; i<terms.length-1; i++) {
			signature.append(terms[i]);
			signature.append(".");
		}
		
		// Removes last dot
		if (signature.length() > 0)
			signature.deleteCharAt(signature.length()-1);
		
		return signature.toString();
	}
	
	/**
	 * Gets line where the invoked is called in the test method.
	 * 
	 * @return		Line where the invoked is called in the test method
	 */
	public int getInvocationLine() {
		return invocationLine;
	}
	
	/**
	 * Sets invocation line.
	 * 
	 * @param		line Invocation line (must be greater than zero)
	 */
	public void setInvocationLine(int line) {
		if (line > 0)
			this.invocationLine = line;
	}
	
	/**
	 * Gets package of the invoked.
	 * 
	 * @return		Package to which the invoked's class belongs
	 * 
	 * @implNote	Lazy initialization
	 */
	public String getPackage() {
		if (classPackage == null)
			classPackage = extractPackage(getClassSignature());

		return classPackage;
	}
	
	/**
	 * Gets directory where the invoked's compiled file is.
	 * 
	 * @return		Directory where the invoked's source file is.
	 */
	public Path getClassDirectory() {
		return binPath.getParent();
	}
	
	/**
	 * Gets directory where the invoked's source file is.
	 * 
	 * @return		Directory where the invoked's source file is.
	 */
	public Path getSrcDirectory() {
		return srcPath.getParent();
	}
	
	public Object[] getArgs() {
		return args;
	}
	
	public String getSignatureWithoutParameters() {
		return invokedSignature.substring(0, invokedSignature.indexOf("(")+1);
	}

	/**
	 * Gets invoked name. The name is the last term before the parameters.
	 * 
	 * @return		Invoked name
	 * 
	 * @implNote	Lazy initialization
	 */
	public String getInvokedName() {
		if (invokedName == null)
			extractInvokedName();
		
		return invokedName;
	}

	private void extractInvokedName() {
		int idxParamStart = invokedSignature.indexOf("(");
		
		invokedName = invokedSignature.substring(0, idxParamStart);
		invokedName = invokedName.substring(invokedName.lastIndexOf("."), idxParamStart);
	}

	public String getMethodSignature() {
		return getInvokedSignature();
	}

	public String getMethodName() {
		return invokedName;
	}

	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}
	
	public Class<?> getReturnType() {
		return this.returnType;
	}
	
	/**
	 * Gets parameter types and return type of the method.
	 * 
	 * @return		Return type and parameter types of the method
	 */
	public MethodType getMethodTypes() {
		if (args == null || args.length == 0)
			return methodType(returnType);
		
		return methodType(returnType, parameterTypes);
	}
	
	public String getConcreteInvokedSignature() {
		if ((concreteMethodSignature == null) || concreteMethodSignature.isBlank())
			concreteMethodSignature = invokedSignature.replaceAll("\\$", ".");
		
		return concreteMethodSignature;
	}
	
	public void setConcreteMethodSignature(String concreteMethodSignature) {
		this.concreteMethodSignature = concreteMethodSignature;
	}

	public void setInvokedSignature(String signature) {
		this.invokedSignature = signature;
	}
	
	public boolean isConstructor() {
		return this.isConstructor;
	}
	
	
	//-------------------------------------------------------------------------
	//		Serialization and deserialization methods
	//-------------------------------------------------------------------------
	private void writeObject(ObjectOutputStream oos) {
		try {
			oos.defaultWriteObject();
			oos.writeUTF((srcPath == null) ? "NULL" : srcPath.toAbsolutePath().toString());
			oos.writeUTF((binPath == null) ? "NULL" : binPath.toAbsolutePath().toString());
			oos.writeObject((binPath == null) ? null : Arrays.asList(args));
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void readObject(ObjectInputStream ois) {
		try {
			ois.defaultReadObject();
			this.srcPath = readPath(ois);
			this.binPath = readPath(ois);
			this.args = readArgs(ois);
		} 
		catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}
	
	private Path readPath(ObjectInputStream ois) throws IOException {
		String path = ois.readUTF();
		
		return path.equals("NULL") ? null : Path.of(path);
	}
	
	@SuppressWarnings("unchecked")
	private Object[] readArgs(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		Object rawList = ois.readObject();
		
		return (rawList == null) ? null : ((List<Object>) ois.readObject()).toArray();
	}
}
