package executionFlow.runtime.collector;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import org.aspectj.lang.JoinPoint;

import executionFlow.info.CollectorInfo;
import executionFlow.info.ConstructorInvokedInfo;
import executionFlow.util.Logger;


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
 * @version		5.2.0
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
		!(junit4_internal() || junit5_internal()) &&
		call(*.new(..)) && 
		!cflowbelow(withincode(* *(..))) &&
		!within(executionFlow..*) &&
		!within(ConstructorCollector);
	
	before(): constructorCollector()
	{
		if (invocationLine <= 0)
			return;
		
		final String REGEX_CONSTRUCTOR = "[^\\s\\t]([A-z0-9-_$]*\\.)*[A-z0-9-_$]+\\([A-z0-9-_$,\\s]*\\)";
		final String REGEX_ANONYMOUS_CLASS = ".+\\$[0-9]+.+";
		String signature, classSignature;
		Path classPath, srcPath;
		

		signature = thisJoinPoint.getSignature().toString();
		
		if (signature.contains("java.") || !signature.matches(REGEX_CONSTRUCTOR))
			return;

		// Gets correct signature of inner classes
		signature = thisJoinPoint.getSignature().getDeclaringTypeName() 
				+ signature.substring(signature.indexOf("("));

		if (signature.matches(REGEX_ANONYMOUS_CLASS) ||	
				collectedConstructors.contains(signature) || 
				(testMethodInfo == null)) {
			return;
		}
		
		collectedConstructors.add(signature);
		
		classSignature = signature.split("\\(")[0];
		
		// Gets class path and source path
		try {
			// Class path and source path from method
			srcPath = CollectorExecutionFlow.findSrcPath(classSignature);
			classPath = CollectorExecutionFlow.findBinPath(classSignature);
			
			if (srcPath == null || classPath == null) {
				Logger.warning("The constructor with the following signature" 
						+ " will be skiped because its source file and / or " 
						+ " binary file cannot be found: " + signature);
				return;
			}
			
			collectConstructor(thisJoinPoint, signature, srcPath, classPath);
			invocationLine = 0;
		} 
		catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Collects current constructor.
	 * 
	 * @param		jp Join point
	 * @param		signature Constructor signature
	 * @param		srcPath Constructor source file
	 * @param		classPath Constructor binary file
	 */
	private void collectConstructor(JoinPoint jp, String signature, Path srcPath, Path classPath)
	{
		CollectorInfo collectorInfo;
		ConstructorInvokedInfo constructorInvokedInfo;
		Class<?>[] paramTypes;
		Object[] paramValues;
		String key = invocationLine + signature;
		
		
		if (jp.getArgs() == null || jp.getArgs().length == 0) {
			paramTypes = new Class<?>[0];
			paramValues = new Object[0];
		} 
		else {
			paramTypes = CollectorExecutionFlow.extractParamTypes(jp.getArgs());
			paramValues = jp.getArgs();			
		}
		
		constructorInvokedInfo = new ConstructorInvokedInfo.Builder()
				.binPath(classPath)
				.srcPath(srcPath)
				.constructorSignature(signature)
				.parameterTypes(paramTypes)
				.args(paramValues)
				.invocationLine(invocationLine)
				.build();
			
		collectorInfo = new CollectorInfo.Builder()
			.constructorInfo(constructorInvokedInfo)
			.testMethodInfo(testMethodInfo)
			.build();
		
		if (!constructorCollector.containsKey(key))
			constructorCollector.put(key, collectorInfo);
	}
}
