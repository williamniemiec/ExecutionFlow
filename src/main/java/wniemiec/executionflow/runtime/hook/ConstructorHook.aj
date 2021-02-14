package wniemiec.executionflow.runtime.hook;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aspectj.lang.JoinPoint;

import wniemiec.executionflow.collector.ClassPathSearcher;
import wniemiec.executionflow.collector.ConstructorCollector;
import wniemiec.executionflow.invoked.Invoked;
import wniemiec.executionflow.invoked.TestedInvoked;
import wniemiec.util.logger.Logger;

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
 * {@link executionflow.runtime.SkipInvoked]} annotation and all 
 * methods from classes with {@link executionflow.runtime.SkipCollection} 
 * annotation
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		6.0.4
 * @since		1.0
 */
@SuppressWarnings("unused")
public aspect ConstructorHook extends RuntimeHook {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static Set<String> collectedConstructors = new HashSet<>();
	private static int invocationLine = 0;
	private Path classPath;
	private Path srcPath;
	private String signature;
	private String classSignature;
	private Invoked constructorInvokedInfo;
	private String constructorID;

	
	//-------------------------------------------------------------------------
	//		Pointcuts
	//-------------------------------------------------------------------------
	private pointcut onClassInstantiation(): 
		!skipAnnotation()
		&& (insideJUnit4Test() || insideJUnit5Test())
		&& !isInternalPackage()
		&& call(*.new(..));

	private pointcut insideTestedConstructor(): 
		!skipAnnotation()
		&& !(JUnit4InternalCall() || JUnit5InternalCall())
		&& call(*.new(..))
		&& !cflowbelow(withincode(* *(..)))
		&& !within(executionflow..*)
		&& !isInternalPackage()
		&& !within(ConstructorHook);
	
	
	//-------------------------------------------------------------------------
	//		Join points
	//-------------------------------------------------------------------------
	before(): onClassInstantiation() {
		invocationLine = thisJoinPoint.getSourceLocation().getLine();
	}
	
	before(): insideTestedConstructor() {
		if (!isValidConstructor(thisJoinPoint))
			return;

		parseConstructorInfo(thisJoinPoint);
		
		if (!wasConstructorAlreadyParsed() && hasSourceAndBinearyPath()) {
			parseConstructor(thisJoinPoint);
			collectConstructor();
			markConstructorAsParsed();
		}
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	private boolean isValidConstructor(JoinPoint jp) {
		String signature = getSignature(jp);
		
		return	(invocationLine > 0) 
				&& (testMethodInfo != null)
				&& !signature.contains("java.")
				&& !signature.matches(".+\\$[0-9]+.+")
				&& isConstructorSignature(signature);
	}
	
	private boolean isConstructorSignature(String signature) {
		final String regexConstructor = 
				"[^\\s\\t]([A-z0-9-_$]*\\.)*[A-z0-9-_$]+\\([A-z0-9-_$,\\s]*\\)";
		
		return signature.matches(regexConstructor);
	}
	
	private void parseConstructorInfo(JoinPoint jp) {
		initializeSignature(jp);
		constructorID = generateConstructorID();
	}
	
	private void initializeSignature(JoinPoint jp) {
		signature = getSignature(jp);
		classSignature = extractClassSignatureFromSignature(signature);
	}
	
	private String getSignature(JoinPoint jp) {
		return jp.getSignature().getDeclaringTypeName() 
				+ removeParametersFromSignature(jp.getSignature().toString());	
	}
	
	private String extractClassSignatureFromSignature(String signature) {
		return signature.split("\\(")[0];
	}
	
	private String generateConstructorID() {
		return invocationLine + signature;
	}
	
	private boolean wasConstructorAlreadyParsed() {
		return collectedConstructors.contains(signature);
	}
	
	private boolean hasSourceAndBinearyPath() {
		findSourceAndBinaryPaths(classSignature);
		
		if (srcPath == null || classPath == null) {
			Logger.warning("The constructor with the following signature" 
					+ " will be skiped because its source file and / or " 
					+ " binary file cannot be found: " + signature);
			
			return false;
		}
		
		return true;
	}
	
	private void findSourceAndBinaryPaths(String classSignature) {
		try {
			classPath = ClassPathSearcher.findBinPath(classSignature);
			srcPath = ClassPathSearcher.findSrcPath(classSignature);
		} 
		catch (IOException e) {
			Logger.error("[ERROR] ConstructorCollector - " + e.getMessage() + "\n");
		}
	}
	
	private void parseConstructor(JoinPoint jp) {
		constructorInvokedInfo = new Invoked.Builder()
				.binPath(classPath)
				.srcPath(srcPath)
				.signature(signature)
				.parameterTypes(getParameterTypes(jp))
				.isConstructor(true)
				.args(getParameterValues(jp))
				.invocationLine(invocationLine)
				.build();
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
	
	private void collectConstructor() {
		ConstructorCollector.storeCollector(constructorID, constructorInvokedInfo, 
											testMethodInfo);
	}
	
	private void markConstructorAsParsed() {
		collectedConstructors.add(signature);
		invocationLine = 0;
	}
}
