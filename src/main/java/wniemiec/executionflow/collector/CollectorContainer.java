package wniemiec.executionflow.collector;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wniemiec.executionflow.invoked.InvokedContainer;
import wniemiec.executionflow.invoked.InvokedInfo;

public class CollectorContainer {
	
	private static Map<InvokedInfo, Integer> modifiedCollectorInvocationLine;
	
	// collector
	
		/**
		 * Updates the invocation line of constructor and method collector based on
		 * a mapping.
		 * 
		 * @param		mapping Mapping that will be used as base for the update
		 * @param		testMethodSrcFile Test method source file
		 */
		public static void updateCollectorInvocationLines(Map<Integer, Integer> mapping, 
														  Path testMethodSrcFile)	{
			if (modifiedCollectorInvocationLine == null)
				modifiedCollectorInvocationLine = new HashMap<>();
			
			updateConstructorInvocationLines(mapping, testMethodSrcFile);
			updateMethodInvocationLines(mapping, testMethodSrcFile);
		}

		private static void updateConstructorInvocationLines(Map<Integer, Integer> mapping, 
															 Path testMethodSrcFile) {
			updateInvokedInvocationLines(
					mapping, 
					testMethodSrcFile, 
					ConstructorCollector.getConstructorCollector().values()
			);
		}
		
		private static void updateMethodInvocationLines(Map<Integer, Integer> mapping, 
														Path testMethodSrcFile) {
			for (List<InvokedContainer> methodCollectorList : MethodCollector.getCollector().values()) {
				updateInvokedInvocationLines(
						mapping, 
						testMethodSrcFile,
						methodCollectorList
				);
			}
		}
		
		private static void updateInvokedInvocationLines(Map<Integer, Integer> mapping, 
														 Path testMethodSrcFile, 
														 Collection<InvokedContainer> collector) {
			for (InvokedContainer cc : collector) {
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
