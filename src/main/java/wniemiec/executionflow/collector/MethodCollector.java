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

	private static MethodCollector instance;
	
	/**
	 * Stores information about collected methods.<hr/>
	 * <ul>
	 * 	<li><b>Key:</b> Method invocation line</li>
	 * 	<li><b>Value:</b> List of methods invoked from this line</li>
	 * </ul>
	 */
	private volatile Map<Integer, List<TestedInvoked>> methodCollector;
	
	private MethodCollector() {
		methodCollector = new LinkedHashMap<>();
	}
	
	public static MethodCollector getInstance() {
		if (instance == null)
			instance = new MethodCollector();
		
		return instance;
	}
	
	@Override
	public void collect(TestedInvoked testedInvoked) {
		if (wasMethodCollected(testedInvoked.getTestedInvoked())) {
			List<TestedInvoked> list = getAllCollectedInvokedFromMethod(
					testedInvoked.getTestedInvoked()
			);
			list.add(testedInvoked);
		} 
		else {	
			List<TestedInvoked> list = new ArrayList<>();
			list.add(testedInvoked);
			
			putInMethodCollection(testedInvoked.getTestedInvoked(), list);
		}
	}
	
	private boolean wasMethodCollected(Invoked method) {
		return methodCollector.containsKey(method.getInvocationLine());
	}
	
	private List<TestedInvoked> getAllCollectedInvokedFromMethod(Invoked method) {
		return methodCollector.get(method.getInvocationLine());
	}
	
	private void putInMethodCollection(Invoked method, List<TestedInvoked> list) {
		methodCollector.put(method.getInvocationLine(), list);
	}
	
	@Override
	public Set<TestedInvoked> getAllCollectedInvoked() {
		Set<TestedInvoked> collectors = new HashSet<>();
		for (List<TestedInvoked> collector : methodCollector.values()) {
			collectors.add(collector.get(0));
		}
		
		return collectors;
	}
	
	@Override
	public void reset() {
		methodCollector.clear();
	}
	
	@Override
	public void updateInvocationLines(Map<Integer, Integer> mapping, 
			Path testMethodSrcFile) {
		for (List<TestedInvoked> methodCollectorList : methodCollector.values()) {
			updateInvokedInvocationLines(
					mapping, 
					testMethodSrcFile,
					methodCollectorList
			);
		}
	}
}
