package executionFlow.runtime;

import java.util.Arrays;

import executionFlow.info.ClassConstructorInfo;


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
		Class<?>[] consParamTypes;		// Constructor parameter types
		Object[] consParamValues;		// Constructor parameter values
		
		// Checks if it is a constructor signature
		if (!signature.matches(constructorRegex)) { return; }
		
		// Extracts constructor data
		if (thisJoinPoint.getArgs() == null) {
			consParamTypes = new Class<?>[0];
			consParamValues = new Object[0];
		} 
		else {
			consParamTypes = CollectorExecutionFlow.extractParamTypes(thisJoinPoint.getArgs());
			consParamValues = thisJoinPoint.getArgs();			
		}
		
		String key = signature+Arrays.toString(consParamValues);
		
		// Saves extracted data
		//consCollector.put(key, new ClassConstructorInfo(consParamTypes, consParamValues));
	}
}
