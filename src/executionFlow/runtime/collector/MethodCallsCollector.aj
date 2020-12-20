package executionFlow.runtime.collector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.aspectj.lang.JoinPoint;

import executionFlow.ExecutionFlow;
import executionFlow.util.logger.Logger;


/**
 * Captures all methods called within the tested invoked, where an invoked can
 * be a method or a constructor.
 * 
 * @apiNote		Test method, that is, the method that calls the tested method
 * must have {@link executionFlow.runtime._SkipInvoked} annotation
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.3
 * @since		2.0.0
 */
@SuppressWarnings("unused")
public aspect MethodCallsCollector extends RuntimeCollector {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private String invocationSignature;
	
	
	//-----------------------------------------------------------------------
	//		Pointcuts
	//-----------------------------------------------------------------------
	/**
	 * Gets tested method signatures by a JUnit test that has 
	 * {@link @executionFlow.runtime._SkipInvoked} annotation.
	 */
	pointcut invokedSignature(): 
		!skipAnnotation()
		&& !withincode(@executionFlow.runtime.SkipInvoked * *.*(..))
		&& cflow(execution(@executionFlow.runtime._SkipInvoked * *.*(..)))
		&& insideJUnitTest()
		&& !get(* *.*) 
		&& !set(* *.*)
		&& !execution(public int hashCode());
	
	pointcut insideConstructor():
		withincode(@executionFlow.runtime.CollectCalls *.new(..))  
		&& !cflowbelow(withincode(@executionFlow.runtime.CollectCalls * *(..)));
	
	pointcut insideMethod():
		withincode(@executionFlow.runtime.CollectCalls * *(..))
		&& !cflowbelow(withincode(@executionFlow.runtime.CollectCalls *.new(..)))  
		&& !cflowbelow(withincode(@executionFlow.runtime.CollectCalls * *(..)));
	
	/**
	 * Intercepts methods called within an invoked with 
	 * {@link @executionFlow.runtime.CollectCalls} annotation.
	 */
	pointcut invokedMethodByTestedInvoker():
		!skipAnnotation()
		&& !withincode(@executionFlow.runtime.SkipInvoked * *.*(..))
		&& !get(* *.*) 
		&& !set(* *.*) 
		&& insideConstructor()
		|| insideMethod();
	
	
	//-------------------------------------------------------------------------
	//		Join points
	//-------------------------------------------------------------------------
	before(): invokedSignature() {
		if (isNativeMethod(thisJoinPoint) || !isValidSignature(thisJoinPoint)) 
			return;
		
		invocationSignature = getSignature(thisJoinPoint);
		invocationSignature = removeReturnTypeFromSignature(invocationSignature);
	}
	
	before(): invokedMethodByTestedInvoker() {
		if ((invocationSignature == null) || !isMethodSignature(thisJoinPoint) 
				|| isNativeMethod(thisJoinPoint))
			return;
		
		collectMethod(extractMethodCalledSignature(thisJoinPoint));

		storeCollectedMethods();
	}
	

	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	private boolean isValidSignature(JoinPoint jp) {
		String signature = jp.getSignature().toString();
		
		return signature.contains("(");
	}

	private String getSignature(JoinPoint jp) {
		StringBuilder signature = new StringBuilder();
		
		signature.append(jp.getSignature().getDeclaringTypeName());
		signature.append(".");
		signature.append(jp.getSignature().getName());
		signature.append(signature.substring(signature.indexOf("(")));

		return signature.toString();
	}
	
	private String removeReturnTypeFromSignature(String signature) {
		return signature.substring(signature.indexOf(' ') + 1);
	}
	
	private String extractMethodCalledSignature(JoinPoint jp) {
		StringBuilder methodSignature = new StringBuilder();
		String signature = jp.getSignature().toString();
		
		methodSignature.append(jp.getSignature().getDeclaringTypeName());
		methodSignature.append(".");
		methodSignature.append(jp.getSignature().getName());
		methodSignature.append(signature.substring(signature.indexOf("(")));

		return methodSignature.toString().replaceAll("\\$", ".");
	}
	
	private void collectMethod(String signature) {
		if (methodsCalledByTestedInvoked.containsKey(invocationSignature)) {
			Set<String> invokedMethods = methodsCalledByTestedInvoked.get(invocationSignature);
			invokedMethods.add(signature);
		}
		else {
			Set<String> invokedMethods = new HashSet<>();
			invokedMethods.add(signature);
			
			methodsCalledByTestedInvoked.put(invocationSignature, invokedMethods);
		}
	}
	
	/**
	 * Saves methods called by tested invoked. It will save to a file named
	 * 'mcti.ef' (Methods Called by Tested Invoked).
	 */
	private void storeCollectedMethods() {
		if (methodsCalledByTestedInvoked.isEmpty())
			return;
		
		try {
			load();
			store();
		} 
		catch (IOException e) {
			Logger.error("MethodCallsCollector.aj - " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads methods called by tested invoked and merges with attribute 
	 * {@link #methodsCalledByTestedInvoked}.
	 * 
	 * @throws		FileNotFoundException If 'mcti.ef' does not exist, is a 
	 * directory rather than a regular file, or for some other reason cannot be
	 * opened for reading.
	 * @throws		IOException If 'mcti.ef' cannot be read
	 */
	private void load() throws FileNotFoundException, IOException {
		File file = new File(ExecutionFlow.getAppRootPath().toFile(), "mcti.ef");
		
		if (!file.exists())
			return;

		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
			@SuppressWarnings("unchecked")
			Map<String, Set<String>> storedCollection = 
					(Map<String, Set<String>>) ois.readObject();
			
			combineCollectedMethodWithStoredCollection(storedCollection);
		}
		catch(java.io.EOFException | ClassNotFoundException e) {
			file.delete();
		}
	}
	//Merges methods called by tested invoked with saved collection
	private void combineCollectedMethodWithStoredCollection(Map<String, Set<String>> storedCollection) {
		for (Map.Entry<String, Set<String>> e : storedCollection.entrySet()) {
			String storedInvocationSignature = e.getKey();
			Set<String> storedMethodsCalled = e.getValue();
			
			if (methodsCalledByTestedInvoked.containsKey(storedInvocationSignature)) {
				mergeCollectedMethodWithStoredCollection(
						storedInvocationSignature, 
						storedMethodsCalled
				);
			}
			else {
				methodsCalledByTestedInvoked.put(
						storedInvocationSignature, 
						storedMethodsCalled
				);							
			}
		}
	}
	
	private void mergeCollectedMethodWithStoredCollection(String storedInvocationSignature, 
			Set<String> storedMethodsCalled) {
		Set<String> currentMethodsCalled = 
				methodsCalledByTestedInvoked.get(storedInvocationSignature);

		for (String invokedMethod : storedMethodsCalled)
			currentMethodsCalled.add(invokedMethod);
		
		for (String methodCalled : storedMethodsCalled) {
			if (!currentMethodsCalled.contains(methodCalled)) {
				currentMethodsCalled.add(methodCalled);
			}
		}
		
		methodsCalledByTestedInvoked.put(storedInvocationSignature, currentMethodsCalled);
	}
	
	/**
	 * Stores {@link #methodsCalledByTestedInvoked} in the 'mcti.ef' file.
	 * 
	 * @throws		FileNotFoundException If 'mcti.ef' does not exist, is a 
	 * directory rather than a regular file, or for some other reason cannot be
	 * opened for reading.
	 * @throws		IOException If 'mcti.ef' cannot be written
	 */
	private void store() throws FileNotFoundException, IOException {
		File file = new File(ExecutionFlow.getAppRootPath().toFile(), "mcti.ef");
		
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
			oos.writeObject(methodsCalledByTestedInvoked);
		}
	}
}
