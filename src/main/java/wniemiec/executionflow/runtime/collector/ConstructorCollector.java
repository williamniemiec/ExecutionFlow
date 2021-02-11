package wniemiec.executionflow.runtime.collector;

import java.util.LinkedHashMap;
import java.util.Map;

import wniemiec.executionflow.invoked.InvokedContainer;
import wniemiec.executionflow.invoked.InvokedInfo;

public class ConstructorCollector {

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
	protected static Map<String, InvokedContainer> constructorCollector;
	
	static {
		constructorCollector = new LinkedHashMap<>();
	}
	
	public static void storeCollector(String id, InvokedInfo constructorInvokedInfo, 
									  InvokedInfo testMethodInfo) {
		if (constructorCollector.containsKey(id))
			return;
		
		constructorCollector.put(
				id,
				new InvokedContainer(constructorInvokedInfo, testMethodInfo)
		);
	}
	
	public static Map<String, InvokedContainer> getConstructorCollector() {
		return constructorCollector;
	}
	
	public static void reset() {
		constructorCollector.clear();
	}
}
