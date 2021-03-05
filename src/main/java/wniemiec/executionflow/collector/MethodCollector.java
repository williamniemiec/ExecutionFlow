package wniemiec.executionflow.collector;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import wniemiec.executionflow.invoked.Invoked;
import wniemiec.executionflow.invoked.TestedInvoked;
import wniemiec.executionflow.user.User;
import wniemiec.util.logger.Logger;

/**
 * Responsible for collect methods.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		7.0.0
 * @since		7.0.0
 */
public class MethodCollector extends InvokedCollector {

	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static MethodCollector instance;
	
	/**
	 * Stores information about collected methods.<hr/>
	 * <ul>
	 * 	<li><b>Key:</b> Method invocation line</li>
	 * 	<li><b>Value:</b> List of methods invoked from this line</li>
	 * </ul>
	 */
	private volatile Map<Integer, List<TestedInvoked>> methodCollector;

	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	private MethodCollector() {
		methodCollector = new HashMap<>();
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
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
		
		try {
			User.storeMethodCollector(methodCollector);
		} 
		catch (IOException e) {
			Logger.error(e.toString());
			Logger.error("Method collector cannot be stored");
		}
	}
	
	@Override
	public Set<TestedInvoked> getAllCollectedInvoked() {
		Set<TestedInvoked> collectors = new HashSet<>();
		for (List<TestedInvoked> collector : getMethodCollection()) {
			collectors.add(collector.get(0));
		}
		
		return collectors;
	}
	
	private Collection<List<TestedInvoked>> getMethodCollection() {
		try {
			return User.getMethodCollector().values();
		} 
		catch (IOException e) {
			return List.of(new ArrayList<>());
		}
	}
	
	@Override
	public void reset() {
		methodCollector.clear();
	}
	
	@Override
	public void updateInvocationLines(Map<Integer, Integer> mapping, 
			Path testMethodSrcFile) {
		for (List<TestedInvoked> methodCollectorList : getMethodCollection()) {
			updateInvokedInvocationLines(
					mapping, 
					testMethodSrcFile,
					methodCollectorList
			);
		}
	}

	@Override
	public String toString() {
		return "MethodCollector [methodCollector=" + methodCollector + "]";
	}
}
