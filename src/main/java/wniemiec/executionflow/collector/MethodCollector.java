package wniemiec.executionflow.collector;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import wniemiec.executionflow.invoked.InvokedInfo;

public class MethodCollector extends InvokedCollector {

	/**
	 * Stores information about collected methods.<hr/>
	 * <ul>
	 * 	<li><b>Key:</b> Method invocation line</li>
	 * 	<li><b>Value:</b> List of methods invoked from this line</li>
	 * </ul>
	 */
	private static volatile Map<Integer, List<InvokedCollection>> methodCollector;
	
	static {
		methodCollector = new LinkedHashMap<>();
	}
	
	public static void storeCollector(InvokedInfo methodInfo, InvokedInfo testMethodInfo) {
		if (methodCollector.containsKey(methodInfo.getInvocationLine())) {
			List<InvokedCollection> list = methodCollector.get(methodInfo.getInvocationLine());
			list.add(new InvokedCollection(methodInfo, testMethodInfo));
		} 
		else {	
			List<InvokedCollection> list = new ArrayList<>();
			list.add(new InvokedCollection(methodInfo, testMethodInfo));
			
			methodCollector.put(methodInfo.getInvocationLine(), list);
		}
	}
	
	public static Map<Integer, List<InvokedCollection>> getCollector() {
		return methodCollector;
	}
	
	public static Set<InvokedCollection> getCollectorSet() {
		Set<InvokedCollection> collectors = new HashSet<>();
		for (List<InvokedCollection> collector : methodCollector.values()) {
			collectors.add(collector.get(0));
		}
		
		return collectors;
	}
	
	public static void clear() {
		methodCollector.clear();
	}
	
	/**
	 * Updates the invocation line of all collected methods based on a mapping.
	 * 
	 * @param		mapping Mapping that will be used as base for the update
	 * @param		testMethodSrcFile Test method source file
	 */
	public static void updateInvocationLines(Map<Integer, Integer> mapping, 
			Path testMethodSrcFile) {
		for (List<InvokedCollection> methodCollectorList : MethodCollector.getCollector().values()) {
			updateInvokedInvocationLines(
					mapping, 
					testMethodSrcFile,
					methodCollectorList
			);
		}
	}
}
