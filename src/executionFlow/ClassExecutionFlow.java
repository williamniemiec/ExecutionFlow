package executionFlow;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import executionFlow.cheapCoverage.CheapCoverage;


/**
 * Manage class manipulation (extract data that {@link ExecutionFlow} will need)
 */
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
	/**
	 * @param classPath Path of the .class file of the class
	 */
	public ClassExecutionFlow(String classPath)
	{
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
	public String getClassSignature() { return this.classSignature; }
	
	public String getClassPath() { return classPath; }
	
	/**
	 * Get {@link java.lang.reflect.Method} of a method if it is in the class
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
	
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	/**
	 * Get all methods belong to the {@link #classSignature} and save in
	 * {@link #classMethods} 
	 */
	private void parseClassMethods(Class<?> parsedClass) 
	{
		Method[] allMethods;
		
		try {
			// Return explicit and implicit methods
			allMethods = parsedClass.getMethods();

			for (int i = 0; i < allMethods.length; i++) {
				// Check if it is a valid method (declared in the class)
				
				if (allMethods[i].toString().contains(this.classSignature)) {
					// Save method name and method
					StringBuilder types = new StringBuilder();
					
					for (var paramType : allMethods[i].getParameterTypes()) {
						types.append(paramType.getTypeName()+",");
					}
					
					if (types.length() > 0)
						types.deleteCharAt(types.length()-1);	// Remove last comma
					
					// Format: methodName(arg1,arg2,...)
					String methodName = allMethods[i].getName()+"("+types+")";
					classMethods.put(methodName, allMethods[i]);
				}
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}
}
