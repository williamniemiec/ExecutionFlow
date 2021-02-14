package wniemiec.executionflow.collector;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import wniemiec.executionflow.invoked.InvokedInfo;

public class InvokedCollector {
	
	private static Map<InvokedInfo, Integer> modifiedCollectorInvocationLine;
	
	protected InvokedCollector() {
	}
		
	protected static void updateInvokedInvocationLines(Map<Integer, Integer> mapping, 
													 Path testMethodSrcFile, 
													 Collection<InvokedCollection> collector) {
		if (modifiedCollectorInvocationLine == null)
			modifiedCollectorInvocationLine = new HashMap<>();
		
		for (InvokedCollection cc : collector) {
			int invocationLine = cc.getInvokedInfo().getInvocationLine();
			
			if (!cc.getTestMethodInfo().getSrcPath().equals(testMethodSrcFile)  
					|| !mapping.containsKey(invocationLine))
				continue;
			
			cc.getInvokedInfo().setInvocationLine(mapping.get(invocationLine));
			
			if (!modifiedCollectorInvocationLine.containsKey(cc.getInvokedInfo()))
				modifiedCollectorInvocationLine.put(cc.getInvokedInfo(), invocationLine);
		}
	}
	
	public static void restoreCollectorInvocationLine() {
		if (modifiedCollectorInvocationLine == null)
			return;
		
		for (Map.Entry<InvokedInfo, Integer> e : modifiedCollectorInvocationLine.entrySet()) {
			e.getKey().setInvocationLine(e.getValue());
		}
		
		modifiedCollectorInvocationLine = null;
	}
}
