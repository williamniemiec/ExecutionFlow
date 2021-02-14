package wniemiec.executionflow.collector;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import wniemiec.executionflow.invoked.Invoked;
import wniemiec.executionflow.invoked.TestedInvoked;

public class ConstructorCollector extends InvokedCollector {

	/**
	 * Stores information about collected constructor.<hr/>
	 * <ul>
	 * 	<li><b>Key(with arguments):</b>		
	 * 	<code>invocationLine + classSignature[arg1,arg2,...]</code></li>
	 * 	<li><b>Key(without arguments):</b>	
	 * 	<code>invocationLine + classSignature[]</code></li>
	 * 	<li><b>Value:</b> Informations about the constructor</li>
	 * </ul>
	 */
	protected static volatile Map<String, TestedInvoked> constructorCollector;
	
	static {
		constructorCollector = new LinkedHashMap<>();
	}
	
	public static void storeCollector(String id, Invoked constructorInvokedInfo, 
									  Invoked testMethodInfo) {
		if (constructorCollector.containsKey(id))
			return;
		
		constructorCollector.put(
				id,
				new TestedInvoked(constructorInvokedInfo, testMethodInfo)
		);
	}
	
	public static Map<String, TestedInvoked> getCollector() {
		return constructorCollector;
	}
	
	public static Set<TestedInvoked> getCollectorSet() {
		return new HashSet<>(constructorCollector.values());
	}
	
	public static void reset() {
		constructorCollector.clear();
	}
	

	/**
	 * Updates the invocation line of all collected constructors based on a 
	 * mapping.
	 * 
	 * @param		mapping Mapping that will be used as base for the update
	 * @param		testMethodSrcFile Test method source file
	 */
	public static void updateInvocationLines(Map<Integer, Integer> mapping, 
														 Path testMethodSrcFile) {
		updateInvokedInvocationLines(
				mapping, 
				testMethodSrcFile, 
				constructorCollector.values()
		);
	}
}
