package executionFlow.runtime.collector;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import executionFlow.info.CollectorInfo;
import executionFlow.info.ConstructorInvokedInfo;
import executionFlow.util.ConsoleOutput;


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
 * @version		4.0.1
 * @since		1.0
 */
public aspect ConstructorCollector extends RuntimeCollector
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static Set<String> collectedConstructors = new HashSet<>();
	private static int invocationLine = 0;
	
	
	//-------------------------------------------------------------------------
	//		Pointcuts
	//-------------------------------------------------------------------------
	/**
	 * Intercepts object instantiation and gets its invocation line.
	 */
	pointcut constructorInvocationLineCollector(): 
		!skipAnnotation() &&
		(junit4() || junit5()) &&
		call(*.new(..));
	
	before(): constructorInvocationLineCollector()
	{
		invocationLine = thisJoinPoint.getSourceLocation().getLine();
	}
	
	/**
	 * Intercepts object instantiation within test methods.
	 */
	pointcut constructorCollector(): 
		!skipAnnotation() &&
		cflow(
			(junit4() || junit5()) && 
			call(*.new(..))
		) &&
		!within(executionFlow.*) &&
		!within(executionFlow.*.*) &&
		!within(executionFlow.*.*.*) &&
		!within(executionFlow.*.*.*.*) &&
		!within(executionFlow.*.*.*.*.*) &&
		!cflowbelow(withincode(*.new(..))) &&
		!within(ConstructorCollector);
		
	before(): constructorCollector()
	{
		if (invocationLine <= 0)
			return;
		
		final String REGEX_CONSTRUCTOR = "[^\\s\\t]([A-z0-9-_$]*\\.)*[A-z0-9-_$]+\\([A-z0-9-_$,\\s]*\\)";
		final String REGEX_ANONYMOUS_CLASS = ".+\\$[0-9]+.+";
		String key, signature, classSignature, className;
		ConstructorInvokedInfo constructorInvokedInfo;
		CollectorInfo collectorInfo;
		Path classPath, srcPath;
		Class<?>[] paramTypes;		// Constructor parameter types
		Object[] paramValues;		// Constructor parameter values
		

		signature = thisJoinPoint.getSignature().toString();
		
		if (signature.contains("java.") || !signature.matches(REGEX_CONSTRUCTOR))
			return;

		// Gets correct signature of inner classes
		signature = thisJoinPoint.getSignature().getDeclaringTypeName() 
				+ signature.substring(signature.indexOf("("));

		if (	signature.matches(REGEX_ANONYMOUS_CLASS) || 
				collectedConstructors.contains(signature) || 
				testMethodInfo == null	) {
			return;
		}
		
		collectedConstructors.add(signature);
		
		// Extracts constructor data
		if (thisJoinPoint.getArgs() == null || thisJoinPoint.getArgs().length == 0) {
			paramTypes = new Class<?>[0];
			paramValues = new Object[0];
		} 
		else {
			paramTypes = CollectorExecutionFlow.extractParamTypes(thisJoinPoint.getArgs());
			paramValues = thisJoinPoint.getArgs();			
		}
		
		key = invocationLine + signature;
		classSignature = signature.split("\\(")[0];
		
		// Gets class path and source path
		try {
			// Class path and source path from method
			className = CollectorExecutionFlow.extractMethodName(signature);
			srcPath = CollectorExecutionFlow.findSrcPath(className, classSignature);
			classPath = CollectorExecutionFlow.findBinPath(className, classSignature);
			
			if (srcPath == null || classPath == null) {
				ConsoleOutput.showWarning("The constructor with the following signature" 
						+ " will be skiped because its source file and / or " 
						+ " binary file cannot be found: " + signature);
				return;
			}
			
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
		
			invocationLine = 0;
		} 
		catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
