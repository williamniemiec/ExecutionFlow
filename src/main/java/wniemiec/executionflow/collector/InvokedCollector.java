package wniemiec.executionflow.collector;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import wniemiec.executionflow.invoked.Invoked;
import wniemiec.executionflow.invoked.TestedInvoked;

public abstract class InvokedCollector {
	
	private static Map<Invoked, Integer> modifiedCollectorInvocationLine;
	
	protected InvokedCollector() {
	}
		
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
	
	public static void restoreCollectorInvocationLine() {
		if (modifiedCollectorInvocationLine == null)
			return;
		
		for (Map.Entry<Invoked, Integer> e : modifiedCollectorInvocationLine.entrySet()) {
			e.getKey().setInvocationLine(e.getValue());
		}
		
		modifiedCollectorInvocationLine = null;
	}
}