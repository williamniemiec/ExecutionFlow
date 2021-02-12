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
	private static volatile Map<Integer, List<InvokedContainer>> methodCollector;
	
	/**
	 * Stores signature of collected methods.<hr/>
	 * <b>Format: </b><code>method_name + method_arguments + 
	 * constructor@hashCode (if it has one)</code>
	 */
	private static List<String> parsedMethods;
	
	
	static {
		methodCollector = new LinkedHashMap<>();
		parsedMethods = new ArrayList<>();
	}
	
	public static void storeCollector(String methodID, InvokedInfo methodInfo, InvokedInfo testMethodInfo) {
		if (methodCollector.containsKey(methodInfo.getInvocationLine())) {
			List<InvokedContainer> list = methodCollector.get(methodInfo.getInvocationLine());
			list.add(new InvokedContainer(methodInfo, testMethodInfo));
		} 
		else {	
			List<InvokedContainer> list = new ArrayList<>();
			list.add(new InvokedContainer(methodInfo, testMethodInfo));
			
			methodCollector.put(methodInfo.getInvocationLine(), list);
		}
		
		markMethodAsParsed(methodID);
	}
	
	private static void markMethodAsParsed(String methodID) {
		parsedMethods.add(methodID);
	}
	
	public static Map<Integer, List<InvokedContainer>> getCollector() {
		return methodCollector;
	}
	
	public static void clear() {
		methodCollector.clear();
		parsedMethods.clear();
	}
	
	public static boolean wasCollected(String methodID) {
		return parsedMethods.contains(methodID);
	}
}