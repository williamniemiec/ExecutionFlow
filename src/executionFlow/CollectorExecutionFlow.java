package executionFlow;

import info.ClassMethodInfo;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Helper class to make a bridge between data collected with AOP and {@link ExecutionFlow}
 */
public class CollectorExecutionFlow 
{
	//private String classPath;
	//private Collection<ClassMethodInfo> methods;
	
	
	/*public CollectorExecutionFlow(String classPath, Map<String, ClassMethodInfo> collector) {
		this.classPath = classPath;
		this.methods = collector.values();
	}
	*/
	
	public static ExecutionFlow get(String classPath, Map<String, ClassMethodInfo> collector)
	{
		return new ExecutionFlow(classPath, collector.values());
	}
	
	public static Class<?>[] extractParamTypes(Object[] args) {
		if (args.length == 0) { return null; }
		
		int i = 0;
		Class<?>[] paramTypes = new Class<?>[args.length];
		
		for (Object o : args) {
			paramTypes[i++] = o.getClass();
		}
		
		return paramTypes;
	}
	
	public static String extractClassName(String signature) {
		String methodName = "";
		
		Pattern p = Pattern.compile("\\.[A-z0-9-_$]+\\(");
		Matcher m = p.matcher(signature);
		
		if (m.find()) {
			methodName = m.group();					// ".<methodName>("
			p = Pattern.compile("[A-z0-9-_$]+");
			m = p.matcher(methodName);
			if (m.find())
				methodName = m.group();				// "<methodName>"
		}
		
		return methodName;
	}
}
