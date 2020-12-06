package executionFlow.analyzer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import executionFlow.ExecutionFlow;
import executionFlow.util.Logger;
import executionFlow.util.balance.RoundBracketBalance;

public class StandardAnalyzer {
	
	
	
	
	/**
	 * Gets invoked signature of the invoked analyzed by the analyzer.
	 * 
	 * @return		Invoked signature
	 */
	public String getAnalyzedInvokedSignature()
	{
		return analyzedInvokedSignature.replace('$', '.');
	}
	
	/**
	 * Gets methods called by tested invoked. It will return signature of all 
	 * called methods from tested methods.
	 * 
	 * @return		Null if tested invoked does not call methods; otherwise, 
	 * returns list of signature of methods called by tested invoked
	 * 
	 * @implSpec	After call this method, the file containing methods called
	 * by tested invoked will be deleted. Therefore, this method can only be 
	 * called once for each {@link #run JDB execution}
	 */
	@SuppressWarnings("unchecked")
	public Set<String> getMethodsCalledByTestedInvoked()
	{
		File f = new File(ExecutionFlow.getAppRootPath().toFile(), "mcti.ef");
		Map<String, Set<String>> invokedMethods = new HashMap<>();
		String invokedSignatureWithoutDollarSign = invokedSignature.replaceAll("\\$", ".");
		
		
		if (f.exists()) {
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
				invokedMethods = (Map<String, Set<String>>) ois.readObject();
			} 
			catch (IOException | ClassNotFoundException e) {
				invokedMethods = null;
				Logger.error("Called methods by tested invoked - " + e.getMessage());
				e.printStackTrace();
			}
		
			f.delete();
		}

		return invokedMethods.containsKey(invokedSignatureWithoutDollarSign) ? 
				invokedMethods.get(invokedSignatureWithoutDollarSign) : null;
	}
}
