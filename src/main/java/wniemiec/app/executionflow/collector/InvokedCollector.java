package wniemiec.app.executionflow.collector;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import wniemiec.app.executionflow.invoked.Invoked;
import wniemiec.app.executionflow.invoked.TestedInvoked;

/**
 * Responsible for collect methods or constructors.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @since		7.0.0
 */
public abstract class InvokedCollector {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static Map<Invoked, Integer> modifiedCollectorInvocationLine;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	protected InvokedCollector() {
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Collect an invoked.
	 * 
	 * @param		testedInvoked Tested invoked to be collected
	 */
	public abstract void collect(TestedInvoked testedInvoked);
	
	/**
	 * Removes all collected invoked.
	 */
	public abstract void reset();	
	
	protected static void updateInvokedInvocationLines(Map<Integer, Integer> mapping, 
													 Path testMethodSrcFile, 
													 Collection<TestedInvoked> collector) {
		if (modifiedCollectorInvocationLine == null)
			modifiedCollectorInvocationLine = new HashMap<>();
	
		for (TestedInvoked cc : collector) {
			int invocationLine = cc.getTestedInvoked().getInvocationLine();
			
			if (!cc.getTestMethod().getSrcPath().equals(testMethodSrcFile)  
					|| !mapping.containsKey(invocationLine))
				continue;
			
			cc.getTestedInvoked().setInvocationLine(mapping.get(invocationLine));
			
			if (!modifiedCollectorInvocationLine.containsKey(cc.getTestedInvoked()))
				modifiedCollectorInvocationLine.put(cc.getTestedInvoked(), invocationLine);
		}
	}
	
	/**
	 * Restores original invocation line of all collected invoked.
	 */
	public static void restoreCollectorInvocationLine() {
		if (modifiedCollectorInvocationLine == null)
			return;
		
		for (Map.Entry<Invoked, Integer> e : modifiedCollectorInvocationLine.entrySet()) {
			e.getKey().setInvocationLine(e.getValue());
		}
		
		modifiedCollectorInvocationLine = null;
	}
	
	/**
	 * Updates the invocation line of all collected invoked based on a 
	 * mapping.
	 * 
	 * @param		mapping Mapping that will be used as base for the update
	 * @param		testMethodSrcFile Test method source file
	 */
	public abstract void updateInvocationLines(Map<Integer, Integer> mapping, 
									  		   Path testMethodSrcFile);
	
	public abstract Set<TestedInvoked> getAllCollectedInvoked();
}
