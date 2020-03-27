package executionFlow;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


public class ClassExecutionFlow 
{
	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
	private String classSignature;
	private String classPath;
	private Map<String, Method> classMethods = new HashMap<>();
	
	
	//-----------------------------------------------------------------------
	//		Constructor
	//-----------------------------------------------------------------------
	public ClassExecutionFlow(String classPath)
	{
		// Formato: "Calculator.class"
		this.classPath = classPath;
		
		try {
			
			CheapCoverage.parseClass(classPath);
			
			Class<?> parsedClass = CheapCoverage.getParsedClass();
			classSignature = parsedClass.getName();
			
			parseClassMethods(parsedClass);
		} catch (IOException e) {
			System.err.println("Error: "+classPath+" not found");
		}
	}
	
	
	//-----------------------------------------------------------------------
	//		Getters & Setters
	//-----------------------------------------------------------------------
	public String getClassPath() { return classPath; }
	
	/**
	 * Get java.lang.reflect.Method of a method if it is in the class
	 * passed to this object (by class signature)
	 * 
	 * @param methodSignature Signature of the method (without class)
	 * <br/>Example
	 * <li>method: String.concat(String)</li>
	 * <li>methodSignature: concat(String)</li>
	 * @return Method or null if it does not exist
	 */
	public Method getMethod(String methodSignature) 
	{
		return classMethods.get(methodSignature);
	}
	
	public String getClassSignature() { return this.classSignature; }
	
	/**
	 * Convert class signature in java.lang.Class
	 * 
	 * @param classSignature Signature of the class
	 * @return Class of the {@link #classSignature}
	 * @throws ClassNotFoundException if the class does not exist
	 */
	private Class<?> getSignatureClass() throws ClassNotFoundException
	{
		return Class.forName(classSignature);
	}
	
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	/**
	 * Filter all methods belong to the {@link #classSignature}
	 */
	/* PROBLEMA - PODE HAVER METODOS SOBRECARREGADOS (terão msm chave) */
	private void parseClassMethods(Class<?> parsedClass) 
	{
		Method[] allMethods;
		
		try {
			// Return explicit and implicit methods
			//allMethods = getSignatureClass().getMethods();
			allMethods = parsedClass.getMethods();
			
			for (int i = 0; i < allMethods.length; i++) {
				// Verifica se achou método válido (declarado na classe)
				if (allMethods[i].toString().contains(this.classSignature)) {
					// Save method name and method
					
					StringBuilder types = new StringBuilder();
					
					for (var paramType : allMethods[i].getParameterTypes()) {
						types.append(paramType.getTypeName()+",");
					}
					
					if (types.length() > 0)
						types.deleteCharAt(types.length()-1);	// Remove last comma
					
					
					String methodSignature = allMethods[i].getName()+"("+types+")";
					
					
					//classMethods.put(allMethods[i].getName(), allMethods[i]);
					classMethods.put(methodSignature, allMethods[i]);
				}
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}
}
