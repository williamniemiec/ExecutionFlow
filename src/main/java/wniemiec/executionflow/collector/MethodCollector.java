package wniemiec.executionflow.collector;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import wniemiec.executionflow.invoked.Invoked;
import wniemiec.executionflow.invoked.TestedInvoked;

public class MethodCollector extends InvokedCollector {

	/**
	 * Stores information about collected methods.<hr/>
	 * <ul>
	 * 	<li><b>Key:</b> Method invocation line</li>
	 * 	<li><b>Value:</b> List of methods invoked from this line</li>
	 * </ul>
	 */
	private static volatile Map<Integer, List<TestedInvoked>> methodCollector;
	
	static {
		methodCollector = new LinkedHashMap<>();
	}
	
	public static void storeCollector(Invoked methodInfo, Invoked testMethodInfo) {
		if (methodCollector.containsKey(methodInfo.getInvocationLine())) {
			List<TestedInvoked> list = methodCollector.get(methodInfo.getInvocationLine());
			list.add(new TestedInvoked(methodInfo, testMethodInfo));
		} 
		else {	
			List<TestedInvoked> list = new ArrayList<>();
			list.add(new TestedInvoked(methodInfo, testMethodInfo));
			
			methodCollector.put(methodInfo.getInvocationLine(), list);
		}
	}
	
	public static Map<Integer, List<TestedInvoked>> getCollector() {
		return methodCollector;
	}
	
	public static Set<TestedInvoked> getCollectorSet() {
		Set<TestedInvoked> collectors = new HashSet<>();
		for (List<TestedInvoked> collector : methodCollector.values()) {
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
		for (List<TestedInvoked> methodCollectorList : MethodCollector.getCollector().values()) {
			updateInvokedInvocationLines(
					mapping, 
					testMethodSrcFile,
					methodCollectorList
			);
		}
	}
}
