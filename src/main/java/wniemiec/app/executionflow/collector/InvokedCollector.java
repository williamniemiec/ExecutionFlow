package wniemiec.app.executionflow.collector;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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
	
	protected static void updateInvokedInvocationLines(Map<Integer, List<Integer>> mapping, 
													   Path testMethodSrcFile, 
													   Collection<TestedInvoked> collector) {
		if (modifiedCollectorInvocationLine == null)
			modifiedCollectorInvocationLine = new HashMap<>();
	
		for (TestedInvoked invoked : collector) {
			if (!invoked.getTestMethod().getSrcPath().equals(testMethodSrcFile))
				continue;
			
			for (Map.Entry<Integer, List<Integer>> m : mapping.entrySet()) {
				updateInvocationLine(
						invoked.getTestedInvoked(),
						m.getKey(), 
						m.getValue()
				);
			}
			
			if (!modifiedCollectorInvocationLine.containsKey(invoked.getTestedInvoked())) {
				modifiedCollectorInvocationLine.put(
						invoked.getTestedInvoked(), 
						invoked.getTestedInvoked().getInvocationLine()
				);
			}
		}
	}

	private static void updateInvocationLine(Invoked invoked, int newLine, 
											 List<Integer> originalLines) {
		for (Integer originalSrcLine : originalLines) {
			if (originalSrcLine == invoked.getInvocationLine())
				invoked.setInvocationLine(newLine);
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
	 * @param		map Mapping that will be used as base for the update
	 * @param		testMethodSrcFile Test method source file
	 */
	public abstract void updateInvocationLines(Map<Integer, List<Integer>> map, 
									  		   Path testMethodSrcFile);
	
	public abstract Set<TestedInvoked> getAllCollectedInvoked();
}
