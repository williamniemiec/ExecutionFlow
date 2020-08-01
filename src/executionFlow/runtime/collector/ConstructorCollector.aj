package executionFlow.runtime.collector;

import java.io.IOException;
import java.nio.file.Path;

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
 * @version		3.0.0
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
		final String constructorRegex = "[^\\s\\t]([A-z0-9-_$]*\\.)*[A-z0-9-_$]+\\([A-z0-9-_$,\\s]*\\)";
		String key, signature, classSignature, className;
		ConstructorInvokedInfo constructorInvokedInfo;
		CollectorInfo collectorInfo;
		Path classPath, srcPath;
		int invocationLine;
		Class<?>[] paramTypes;		// Constructor parameter types
		Object[] paramValues;		// Constructor parameter values
		
		signature = thisJoinPoint.getSignature().toString();
		
		if (signature.contains("java.") || !signature.matches(constructorRegex))
			return;

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
		
		invocationLine = thisJoinPoint.getSourceLocation().getLine();	
		key = invocationLine + signature;
		classSignature = signature.split("\\(")[0];
		
		// Gets class path and source path
		try {
			// Class path and source path from method
			className = CollectorExecutionFlow.extractMethodName(signature);
			classPath = CollectorExecutionFlow.findBinPath(className, classSignature);
			srcPath = CollectorExecutionFlow.findSrcPath(className, classSignature);
			
			constructorInvokedInfo = new ConstructorInvokedInfo.Builder()
				.binPath(classPath)
				.srcPath(srcPath)
				.constructorSignature(signature)
				.parameterTypes(paramTypes)
				.args(paramValues)
				.invocationLine(invocationLine)
				.build();
			
			// Saves extracted data
			collectorInfo = new CollectorInfo.Builder()
				.constructorInfo(constructorInvokedInfo)
				.testMethodInfo(testMethodInfo)
				.build();
			
			if (!constructorCollector.containsKey(key))
				constructorCollector.put(key, collectorInfo);
		
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
