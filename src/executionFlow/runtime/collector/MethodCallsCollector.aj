package executionflow.runtime.collector;

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

import javax.swing.JOptionPane;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;

import executionflow.ExecutionFlow;
import executionflow.info.InvokedInfo;
import executionflow.util.logger.Logger;

/**
 * Captures all methods called within the tested invoked, where an invoked can
 * be a method or a constructor.
 * 
 * @apiNote		Test method, that is, the method that calls the tested method
 * must have {@link executionflow.runtime.CollectMethodsCalled} annotation
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		6.0.0
 * @since		2.0.0
 */
@SuppressWarnings("unused")
public aspect MethodCallsCollector extends RuntimeCollector {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
//	private String invocationSignature;
	private InvokedInfo invoked;
	
	
	//-----------------------------------------------------------------------
	//		Pointcuts
	//-----------------------------------------------------------------------
	/**
	 * Gets tested method signatures by a JUnit test that has 
	 * {@link executionflow.runtime.CollectMethodsCalled} annotation.
	 */
	private pointcut invokedSignature(): 
		!within(@executionFlow.runtime.SkipCollection *)
		&& !withincode(@executionflow.runtime.SkipInvoked * *.*(..))
		&& cflow(execution(@executionflow.runtime.CollectMethodsCalled * *.*(..)))
		&& insideJUnitTest()
		&& !get(* *.*) 
		&& !set(* *.*)
		&& !execution(public int hashCode());
	
	/**
	 * Intercepts methods called within an invoked with 
	 * {@link @executionflow.runtime.CollectCalls} annotation.
	 */
	private pointcut invokedMethodByTestedInvoker():
		!withincode(@executionflow.runtime.SkipInvoked * *.*(..))
		&& !get(* *.*) 
		&& !set(* *.*) 
		&& insideConstructor()	|| insideMethod();
	
	private pointcut insideConstructor():
		withincode(@executionflow.runtime.CollectCalls *.new(..))  
		&& !cflowbelow(withincode(@executionflow.runtime.CollectCalls * *(..)));
	
	private pointcut insideMethod():
		withincode(@executionflow.runtime.CollectCalls * *(..))
		&& !cflowbelow(withincode(@executionflow.runtime.CollectCalls *.new(..)))  
		&& !cflowbelow(withincode(@executionflow.runtime.CollectCalls * *(..)));
	
	
	//-------------------------------------------------------------------------
	//		Join points
	//-------------------------------------------------------------------------
	before(): invokedSignature() {
//		JOptionPane.showMessageDialog(null, "@ " + getSignature(thisJoinPoint));
//		JOptionPane.showMessageDialog(null, "@ " + thisJoinPoint.getTarget());
//		JOptionPane.showMessageDialog(null, "@ " + thisJoinPoint.getKind());
//		JOptionPane.showMessageDialog(null, "@ " + thisJoinPoint.getSourceLocation().getWithinType());
//		JOptionPane.showMessageDialog(null, "@ " + thisJoinPoint.getSourceLocation().getFileName());
		if (isNativeMethod(thisJoinPoint) || !isValidSignature(thisJoinPoint)) 
			return;
		
		invoked = new InvokedInfo.Builder()
				.invokedSignature(removeReturnTypeFromSignature(getSignature(thisJoinPoint)))
				.isConstructor(thisJoinPoint.getKind().equals("constructor-call"))
				.build();
//		invocationSignature = getSignature(thisJoinPoint);
//		invocationSignature = removeReturnTypeFromSignature(invocationSignature);
	}
	
	before(): invokedMethodByTestedInvoker() {
//		JOptionPane.showMessageDialog(null, "#" + getSignature(thisJoinPoint));
//		JOptionPane.showMessageDialog(null, "#" + thisJoinPoint.getTarget());
//		JOptionPane.showMessageDialog(null, "# " + thisJoinPoint.getKind().equals("method-call"));
//		JOptionPane.showMessageDialog(null, "#" + thisJoinPoint.getSourceLocation().getWithinType());
//		
//		
//		
//		JOptionPane.showMessageDialog(null, isMethodSignature(thisJoinPoint));
		
		if (!thisJoinPoint.getKind().equals("method-call") || isNativeMethod(thisJoinPoint))
			return;
		 
		
//		if ((invocationSignature == null) || !isMethodSignature(thisJoinPoint) 
//				|| isNativeMethod(thisJoinPoint))
//			return;
		
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
		if (jp.getSignature().getName().contains("<init>"))
			return jp.getSignature().getDeclaringTypeName();
		
		StringBuilder signature = new StringBuilder();
		Signature jpSignature = jp.getSignature();
		
		signature.append(jp.getSignature().getDeclaringTypeName());
		signature.append(".");
		signature.append(jpSignature.getName());
		signature.append(jpSignature.toString()
						 .substring(jpSignature.toString().indexOf("(")));
		
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
		if (methodsCalledByTestedInvoked.containsKey(invoked)) {
			Set<String> invokedMethods = methodsCalledByTestedInvoked.get(invoked);
			invokedMethods.add(signature);
		}
		else {
			Set<String> invokedMethods = new HashSet<>();
			invokedMethods.add(signature);
			
			methodsCalledByTestedInvoked.put(invoked, invokedMethods);
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
			Map<InvokedInfo, Set<String>> storedCollection = 
					(Map<InvokedInfo, Set<String>>) ois.readObject();
			
			combineCollectedMethodWithStoredCollection(storedCollection);
		}
		catch(java.io.EOFException | ClassNotFoundException e) {
			file.delete();
		}
	}
	
	private void combineCollectedMethodWithStoredCollection(Map<InvokedInfo, Set<String>> 
															storedCollection) {
		for (Map.Entry<InvokedInfo, Set<String>> e : storedCollection.entrySet()) {
			InvokedInfo storedInvocation = e.getKey();
			Set<String> storedMethodsCalled = e.getValue();
			
			if (methodsCalledByTestedInvoked.containsKey(storedInvocation)) {
				mergeCollectedMethodWithStoredCollection(
						storedInvocation, 
						storedMethodsCalled
				);
			}
			else {
				methodsCalledByTestedInvoked.put(
						storedInvocation, 
						storedMethodsCalled
				);							
			}
		}
	}
	
	private void mergeCollectedMethodWithStoredCollection(InvokedInfo storedInvocation, 
														  Set<String> storedMethodsCalled) {
		Set<String> currentMethodsCalled = 
				methodsCalledByTestedInvoked.get(storedInvocation);

		for (String invokedMethod : storedMethodsCalled)
			currentMethodsCalled.add(invokedMethod);
		
		for (String methodCalled : storedMethodsCalled) {
			if (!currentMethodsCalled.contains(methodCalled)) {
				currentMethodsCalled.add(methodCalled);
			}
		}
		
		methodsCalledByTestedInvoked.put(storedInvocation, currentMethodsCalled);
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
