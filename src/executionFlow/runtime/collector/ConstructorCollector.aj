package executionFlow.runtime.collector;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

import executionFlow.info.CollectorInfo;
import executionFlow.info.ConstructorInvokedInfo;


/**
 * Captures class instantiation within test methods.
 * 
 * Collects various information about constructors called by a JUnit test.
 * 
 * <h1>Collected information</h1>
 * <ul>
 * 	<li>Compiled file path</li>
 *	<li>Source file path</li>
 *	<li>Constructor signature</li>
 *	<li>Parameter types</li>
 *	<li>Constructor arguments</li>
 *	<li>Test method line that calls the constructor</li>
 * </ul>
 * 
 * 
 * @apiNote		Excludes calls to native java methods, methods with 
 * {@link executionFlow.runtime.SkipInvoked]} annotation and all 
 * methods from classes with {@link executionFlow.runtime.SkipCollection} 
 * annotation
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		2.0.0
 * @since		1.0
 */
public aspect ConstructorCollector extends RuntimeCollector
{
	//-------------------------------------------------------------------------
	//		Pointcut
	//-------------------------------------------------------------------------
	/**
	 * Intercepts object instantiation within test methods.
	 */
	pointcut constructorCollector(): 
		!skipAnnotation() &&
		(junit4() || junit5()) &&
		!within(ConstructorCollector) &&
		call(*.new(..));
		
	before(): constructorCollector()
	{
		String signature = thisJoinPoint.getSignature().toString();
		final String constructorRegex = "[^\\s\\t]([A-z0-9-_$]*\\.)*[A-z0-9-_$]+\\([A-z0-9-_$,\\s]*\\)";
		
		if (signature.contains("java."))
			return;
		
		// Collect constructor data
		Class<?>[] paramTypes;		// Constructor parameter types
		Object[] paramValues;		// Constructor parameter values
		
		// Checks if it is a constructor signature
		if (!signature.matches(constructorRegex)) { return; }
		
		// Gets correct signature of inner classes
		signature = thisJoinPoint.getSignature().getDeclaringTypeName() 
				+ signature.substring(signature.indexOf("("));
		
		// Extracts constructor data
		if (thisJoinPoint.getArgs() == null || thisJoinPoint.getArgs().length == 0) {
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
			Path classPath = CollectorExecutionFlow.findClassPath(className, classSignature);
			Path srcPath = CollectorExecutionFlow.findSrcPath(className, classSignature);
			
			ConstructorInvokedInfo cii = new ConstructorInvokedInfo.ConstructorInvokerInfoBuilder()
				.classPath(classPath)
				.srcPath(srcPath)
				.constructorSignature(signature)
				.parameterTypes(paramTypes)
				.args(paramValues)
				.invocationLine(thisJoinPoint.getSourceLocation().getLine())
				.build();
			
			// Saves extracted data
			CollectorInfo ci = new CollectorInfo.CollectorInfoBuilder()
				.constructorInfo(cii)
				.testMethodInfo(testMethodInfo)
				.build();
			
			constructorCollector.put(key, ci);
		
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
