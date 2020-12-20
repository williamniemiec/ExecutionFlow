package executionFlow.runtime.collector;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import org.aspectj.lang.JoinPoint;

import executionFlow.info.InvokedContainer;
import executionFlow.info.InvokedInfo;
import executionFlow.util.logger.Logger;


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
 * @version		5.2.3
 * @since		1.0
 */
@SuppressWarnings("unused")
public aspect ConstructorCollector extends RuntimeCollector {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static Set<String> collectedConstructors = new HashSet<>();
	private static int invocationLine = 0;
	private Path classPath;
	private Path srcPath;
	private String signature;
	
	
	//-------------------------------------------------------------------------
	//		Pointcuts
	//-------------------------------------------------------------------------
	/**
	 * Intercepts object instantiation and gets its invocation line.
	 */
	pointcut constructorInvocationLineCollector(): 
		!skipAnnotation()
		&& (insideJUnit4Test() || insideJUnit5Test())
		&& call(*.new(..));
	
	/**
	 * Intercepts object instantiation within test methods.
	 */
	pointcut classInstantiation(): 
		!skipAnnotation()
		&& !(JUnit4InternalCall() || JUnit5InternalCall())
		&& call(*.new(..))
		&& !cflowbelow(withincode(* *(..)))
		&& !within(executionFlow..*)
		&& !within(ConstructorCollector);
	
	
	//-------------------------------------------------------------------------
	//		Join points
	//-------------------------------------------------------------------------
	before(): constructorInvocationLineCollector() {
		invocationLine = thisJoinPoint.getSourceLocation().getLine();
	}
	
	before(): classInstantiation() {
		if (!hasValidState())
			return;

		signature = getSignature(thisJoinPoint);
		
		if (!isValidConstructorSignature() || isAnonymousClassSignature() 
				|| alreadyCollected())
			return;
		
		collectedConstructors.add(signature);
	
		collectSourceAndBinaryPaths();
		
		if (srcPath == null || classPath == null)
			return;
		
		collectConstructor(thisJoinPoint);
		
		invocationLine = 0;
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	private boolean hasValidState() {
		return !(invocationLine <= 0 || (testMethodInfo == null));
	}
	
	private String getSignature(JoinPoint jp) {
		return jp.getSignature().getDeclaringTypeName() 
				+ removeParametersFromSignature(jp.getSignature().toString());
		
	}
	
	private boolean isValidConstructorSignature() {
		return	!signature.contains("java.")
				&& isConstructorSignature();
	}
	
	private boolean isConstructorSignature() {
		final String regexConstructor = 
				"[^\\s\\t]([A-z0-9-_$]*\\.)*[A-z0-9-_$]+\\([A-z0-9-_$,\\s]*\\)";
		
		return signature.matches(regexConstructor);
	}
	
	private boolean isAnonymousClassSignature() {
		final String regexAnonymousConstructor = ".+\\$[0-9]+.+";
		
		return signature.matches(regexAnonymousConstructor);
	}
	
	private boolean alreadyCollected() {
		return collectedConstructors.contains(signature);
	}
	
	private void collectSourceAndBinaryPaths() {		
		try {
			findSrcAndBinPath();
		} 
		catch (IOException e1) {
			Logger.error(e1.getMessage());
		}
	}
	
	private void findSrcAndBinPath() throws IOException {
		String classSignature = extractClassSignatureFromSignature(signature);
		
		srcPath = ClassPathSearcher.findSrcPath(classSignature);
		classPath = ClassPathSearcher.findBinPath(classSignature);
		
		if (srcPath == null || classPath == null) {
			Logger.warning("The constructor with the following signature" 
					+ " will be skiped because its source file and / or " 
					+ " binary file cannot be found: " + signature);
		}
	}
	
	private String extractClassSignatureFromSignature(String signature) {
		return signature.split("\\(")[0];
	}
	
	private void collectConstructor(JoinPoint jp) {
		String key = invocationLine + signature;
		
		if (constructorCollector.containsKey(key))
			return;
		
		InvokedInfo constructorInvokedInfo = new InvokedInfo.Builder()
				.binPath(classPath)
				.srcPath(srcPath)
				.invokedSignature(signature)
				.parameterTypes(getParameterTypes(jp))
				.args(getParameterValues(jp))
				.invocationLine(invocationLine)
				.build();
		
		constructorCollector.put(
				key, 
				new InvokedContainer(constructorInvokedInfo, testMethodInfo)
		);
	}
	
	private Class<?>[] getParameterTypes(JoinPoint jp) {
		if (jp.getArgs() == null || jp.getArgs().length == 0)
			return new Class<?>[0];
		
		return extractParamTypes(jp.getArgs());	
		
	}
	
	private Class<?>[] extractParamTypes(Object[] args) {
		if (args == null || args.length == 0)
			return new Class<?>[0];
		
		int i = 0;
		Class<?>[] paramTypes = new Class<?>[args.length];
		for (Object o : args) { 
			if (o != null)
				paramTypes[i++] = normalizeClass(o.getClass());
		}
		
		return paramTypes;
	}
	
	private static Class<?> normalizeClass(Class<?> c) {
		if 		(c == Boolean.class) 	{ return	boolean.class;	}
		else if	(c == Byte.class) 		{ return 	byte.class; 	}
		else if	(c == Character.class) 	{ return 	char.class; 	}
		else if	(c == Short.class) 		{ return 	short.class; 	}
		else if	(c == Integer.class)	{ return	int.class; 		}
		else if	(c == Float.class) 		{ return	float.class; 	}
		else if	(c == Long.class) 		{ return	long.class; 	}
		else if	(c == Double.class) 	{ return	double.class; 	}
		else return c;
	}
	
	private Object[] getParameterValues(JoinPoint jp) {
		if ((jp.getArgs() == null) || (jp.getArgs().length == 0))
			return new Object[0];
		
		return jp.getArgs();
	}
}
