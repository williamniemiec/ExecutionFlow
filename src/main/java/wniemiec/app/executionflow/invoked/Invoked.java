package wniemiec.app.executionflow.invoked;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import wniemiec.io.consolex.Consolex;

/**
 * Stores information about an invoked, where an invoked can be a 
 * method or a constructor. 
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @since		7.0.0
 */
public class Invoked implements Serializable {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final long serialVersionUID = 700L;
	private File binPath;
	private File srcPath;
	private String invokedSignature;
	private String classSignature;
	private String classPackage;
	private int invocationLine;
	private Class<?>[] parameterTypes;
	private List<String> args;
	private String concreteInvokedSignature;
	private String invokedName;
	private Class<?> returnType;
	private boolean isConstructor;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	private Invoked(Path binPath, Path srcPath, int invocationLine,	
					String invokedSignature, String invokedName, 
					Class<?> returnType, Class<?>[] parameterTypes, 
					Object[] args, boolean isConstructor) {
		checkSignature(invokedSignature);
		checkBinPath(binPath);
		checkSrcPath(srcPath);
		
		this.binPath = new File(binPath.normalize().toAbsolutePath().toString());
		this.srcPath = new File(srcPath.normalize().toAbsolutePath().toString());
		this.invocationLine = invocationLine;
		this.invokedSignature = removeKeywordFromSignature(invokedSignature);
		this.invokedName = invokedName;
		this.returnType = returnType;
		this.parameterTypes = parameterTypes;
		this.args = argsToStringList(args);
		this.isConstructor = isConstructor;
	}
	
	@SuppressWarnings({ "rawtypes", "unused" })
	private Object[] normalizeArgs(Object[] args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i] instanceof Enum)
				args[i] = ((Enum) args[i]).name();
		}
		
		return args;
	}

	private List<String> argsToStringList(Object[] args) {
		List<String> stringArgsList = new ArrayList<>();
		
		for (String arg : convertArrayToString(args)) {
			if (arg == null)
				stringArgsList.add(null);
			else
				stringArgsList.add(arg);
		}
		
		return stringArgsList;
	}
	
	private String[] convertArrayToString(Object[] args) {
		String toStringArray = Arrays.toString(args);
		String individualArgs = toStringArray.substring(1, toStringArray.length()-1);
		individualArgs = individualArgs.replaceAll(", ", ",");
		
		return individualArgs.split(",");
	}


	//-------------------------------------------------------------------------
	//		Builder
	//-------------------------------------------------------------------------
	/**
	 * Builder for {@link Invoked}. It is necessary to provide all required
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
		private Class<?>[] parameterTypes = new Class<?>[0];
		private Object[] args = new Object[0];
		private Class<?> returnType = void.class;
		private boolean isConstructor = false;
		
		
		/**
		 * @param		invokedName Invoked's name
		 * 
		 * @return		Builder to allow chained calls
		 * 
		 * @throws		IllegalArgumentException If methodName is null
		 */
		public Builder name(String name)	{
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
			if (binPath == null) {
				throw new IllegalArgumentException("Compiled file cannot "
						+ "be null");
			}
			
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
			if (srcPath == null) {
				throw new IllegalArgumentException("Source file cannot "
						+ "be null");
			}
			
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
		public Builder signature(String signature) {
			if (signature == null) {
				throw new IllegalArgumentException("Invoked signature cannot "
						+ "be null");
			}
			
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
		 * Creates {@link Invoked} with provided information. It is 
		 * necessary to provide all required fields.. The required fields 
		 * are: <br />
		 * <ul>
		 * 	<li>invokedSignature</li>
		 * </ul>
		 * 
		 * @return		InvokedInfo with provided information
		 * 
		 * @throws		IllegalStateException If any required field is null
		 */
		public Invoked build() {
			return new Invoked(
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
			throw new IllegalStateException("Invoked's compiled file path "
					+ "cannot be null");
		}
	}
	
	private static void checkSrcPath(Path srcPath) {
		if (srcPath == null) {
			throw new IllegalStateException("Invoked's source file path"
					+ "cannot be null");
		}
	}
	
	private static void checkSignature(String signature) {
		if (signature == null) {
			throw new IllegalStateException("Invoked signature cannot be null");
		}
	}
	
	private String removeKeywordFromSignature(String signature) {
		return signature.replaceAll("(final|transient|static|synchronized) ", "");
	}
	
	/**
	 * Extracts class name from a signature.
	 * 
	 * @param		signature Signature of a method or class
	 * 
	 * @return		Name of this class or method
	 * 
	 * @throws		IllegalArgumentException If signature is null
	 */
	public static String extractMethodNameFromMethodSignature(String signature) {
		if (signature == null)
			throw new IllegalArgumentException("Signature cannot be null");
		
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
	 * @return		Package from class signature
	 * 
	 * @throws		IllegalArgumentException If class signature is null
	 */
	public static String extractPackageFromClassSignature(String classSignature) {
		if (classSignature == null)
			throw new IllegalArgumentException("Class signature cannot be null");
		
		if (classSignature.isEmpty())
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
		return "Invoked ["
				+ "name=" + invokedName 
				+ ", binPath=" + binPath 
				+ ", srcPath=" + srcPath
				+ ", classSignature=" + getClassSignature()
				+ ", package=" + getPackage()
				+ ", signature=" + invokedSignature 
				+ ", invocationLine=" + invocationLine 
				+ ", parameterTypes=" + Arrays.toString(parameterTypes) 
				+ ", args="	+ args.toString() 
				+ ", returnType=" + returnType 
			+ "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		
		result = prime * result + ((binPath == null) ? 0 : binPath.hashCode());
		result = prime * result + ((invocationLine <= 0) ? 0 : invocationLine);
		result = prime * result + ((invokedSignature == null) 
				? 0 
				: invokedSignature.hashCode());
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
		
		Invoked other = (Invoked) obj;
		
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
	public Path getBinPath() {
		return binPath.toPath();
	}
	
	public Path getSrcPath() {
		return srcPath.toPath();
	}

	public String getInvokedSignature() {
		return invokedSignature;
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
		
		if (isConstructor)
			return getSignatureWithoutParameters();
		
		StringBuilder signature = new StringBuilder();
		String[] terms = getSignatureWithoutParameters().split("\\.");

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
	 * 
	 * @throws		IllegalArgumentException If invocation line is negative
	 */
	public void setInvocationLine(int invocationLine) {
		if (invocationLine <= 0) {
			throw new IllegalArgumentException("Invocation line must be a "
					+ "number greater than zero");
		}

		this.invocationLine = invocationLine;
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
			classPackage = extractPackageFromClassSignature(getClassSignature());

		return classPackage;
	}
	
	public List<String> getArgs() {
		return args;
	}
	
	public String getSignatureWithoutParameters() {
		return invokedSignature.substring(0, invokedSignature.indexOf("("));
	}

	/**
	 * Gets invoked name. The name is the last term before the parameters.
	 * 
	 * @return		Invoked name
	 * 
	 * @implNote	Lazy initialization
	 */
	public String getName() {
		if (invokedName == null)
			extractInvokedName();
		
		return invokedName;
	}

	private void extractInvokedName() {
		int idxParamStart = invokedSignature.indexOf("(");
		
		invokedName = invokedSignature.substring(0, idxParamStart);

		if (isConstructor) {
			invokedName = invokedName.substring(
					invokedName.lastIndexOf(".")+1, 
					idxParamStart
			);
		}
		else {
			invokedName = invokedName.substring(
					invokedName.lastIndexOf("."), 
					idxParamStart
			);
		}
	}

	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}
	
	public Class<?> getReturnType() {
		return this.returnType;
	}
	
	public String getConcreteSignature() {
		if ((concreteInvokedSignature == null) || concreteInvokedSignature.isBlank()) {
			concreteInvokedSignature = invokedSignature.replaceAll("\\$[0-9]+", "");
			concreteInvokedSignature = concreteInvokedSignature.replaceAll("\\$", ".");
		}
		
		return concreteInvokedSignature;
	}
	
	public void setConcreteSignature(String concreteSignature) {
		if (concreteSignature == null)
			throw new IllegalArgumentException("Signature cannot be null");
		
		this.concreteInvokedSignature = concreteSignature;
	}

	public void setSignature(String signature) {
		if (signature == null)
			throw new IllegalArgumentException("Signature cannot be null");
		
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
		} 
		catch (IOException e) {
			Consolex.writeError(e.toString());
		}
	}

	private void readObject(ObjectInputStream ois) {
		try {
			ois.defaultReadObject();
		} 
		catch (ClassNotFoundException | IOException e) {
			Consolex.writeError(e.toString());
		}
		catch (OutOfMemoryError e2) {
			System.gc();
			
			try {
				ois.reset();
				ois.defaultReadObject();
			} 
			catch (ClassNotFoundException | IOException e3) {
				Consolex.writeError(e3.toString());
			}
		}
	}
}
