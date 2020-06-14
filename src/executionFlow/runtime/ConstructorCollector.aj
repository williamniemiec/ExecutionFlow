package executionFlow.runtime;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

import executionFlow.info.ConstructorInvokerInfo;
import executionFlow.info.InvokerInfo;


/**
 * Captures class instantiation.
 * 
 * @apiNote		Excludes calls to native java methods, ExecutionFlow's classes,
 * methods with {@link SkipMethod]} signature and all methods from classes
 * with {@link SkipCollection} annotation
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		1.5
 * @since		1.0
 */
public aspect ConstructorCollector extends RuntimeCollector
{
	//-------------------------------------------------------------------------
	//		Pointcut
	//-------------------------------------------------------------------------
	pointcut constructorCollector(): 
		!skipAnnotation() &&
		(junit4() || junit5()) &&
		!within(ConstructorCollector) &&
		call(*.new(..));
		
	/**
	 * Executed after instantiating an object within a test method.
	 */
	before(): constructorCollector()
	{
		String signature = thisJoinPoint.getSignature().toString();
		String constructorRegex = "[^\\s\\t]([A-z0-9-_$]*\\.)*[A-z0-9-_$]+\\([A-z0-9-_$,\\s]*\\)";
		
		// Collect constructor data
		Class<?>[] paramTypes;		// Constructor parameter types
		Object[] paramValues;		// Constructor parameter values
		
		// Checks if it is a constructor signature
		if (!signature.matches(constructorRegex)) { return; }
		
		// Extracts constructor data
		if (thisJoinPoint.getArgs() == null) {
			paramTypes = new Class<?>[0];
			paramValues = new Object[0];
		} 
		else {
			paramTypes = CollectorExecutionFlow.extractParamTypes(thisJoinPoint.getArgs());
			paramValues = thisJoinPoint.getArgs();			
		}
		
		String key = signature + Arrays.toString(paramValues);
		String classSignature = signature.split("\\(")[0];
		
		// Gets class path and source path
		try {
			// Class path and source path from method
			String className = CollectorExecutionFlow.extractMethodName(signature);
			
			// errados
			Path classPath = CollectorExecutionFlow.findClassPath(className, classSignature);
			Path srcPath = CollectorExecutionFlow.findSrcPath(className, classSignature);
		
			System.out.println("-----");
			System.out.println(signature);
			System.out.println(classSignature);
			System.out.println(className);
			System.out.println(classPath);
			System.out.println(srcPath);
			System.out.println("-----");
			
			ConstructorInvokerInfo cii = new ConstructorInvokerInfo.ConstructorInvokerInfoBuilder()
				.classPath(classPath)
				.srcPath(srcPath)
				.constructorSignature(signature)
				.parameterTypes(paramTypes)
				.args(paramValues)
				.invocationLine(thisJoinPoint.getSourceLocation().getLine())
				.build();
			
			// Saves extracted data
			constructorCollector.put(key, cii);
		
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
