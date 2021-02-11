package wniemiec.executionflow.runtime.collector;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import wniemiec.executionflow.invoked.InvokedContainer;
import wniemiec.executionflow.invoked.InvokedInfo;

public class MethodCollector {

	/**
	 * Stores information about collected methods.<hr/>
	 * <ul>
	 * 	<li><b>Key:</b> Method invocation line</li>
	 * 	<li><b>Value:</b> List of methods invoked from this line</li>
	 * </ul>
	 */
	private static Map<Integer, List<InvokedContainer>> methodCollector;
	
	
	
	static {
		methodCollector = new LinkedHashMap<>();
	}
	
	public static void storeCollector(InvokedInfo methodInfo, InvokedInfo testMethodInfo) {
		if (methodCollector.containsKey(methodInfo.getInvocationLine())) {
			List<InvokedContainer> list = methodCollector.get(methodInfo.getInvocationLine());
			list.add(new InvokedContainer(methodInfo, testMethodInfo));
		} 
		else {	
			List<InvokedContainer> list = new ArrayList<>();
			list.add(new InvokedContainer(methodInfo, testMethodInfo));
			
			methodCollector.put(methodInfo.getInvocationLine(), list);
		}
	}
	
	public static Map<Integer, List<InvokedContainer>> getCollector() {
		return methodCollector;
	}
	
	public static void clear() {
		methodCollector.clear();
	}
}
