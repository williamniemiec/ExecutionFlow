package executionFlow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import executionFlow.exporter.signature.MethodsCalledByTestedInvokedExporter;
import executionFlow.info.CollectorInfo;
import executionFlow.io.ProcessingManager;
import executionFlow.runtime.collector.MethodCollector;


/**
 * Generates data for collected methods. Among these data:
 * <ul>
 * 	<li>Test path</li>
 * 	<li>Methods called by this method</li>
 * 	<li>Test methods that call this method</li>
 * </ul>
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.3
 * @since		2.0.0
 */
public class MethodExecutionFlow extends ExecutionFlow {
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private List<CollectorInfo> collectors;
	

	//-------------------------------------------------------------------------
	//		Constructors
	//-------------------------------------------------------------------------
	/**
	 * Computes test path for collected methods.
	 * 
	 * @param		collectedMethods Collected methods from {@link MethodCollector}
	 */
	public MethodExecutionFlow(ProcessingManager processingManager, 
			Map<Integer, List<CollectorInfo>> collectedMethods) {
		super(processingManager);
		
		this.collectors = new ArrayList<>();
		computedTestPaths = new HashMap<>();
		
		storeCollectedMethods(collectedMethods);
		initializeMethodsCalledByTestedInvokedExporter();
	}
	
	
	private void storeCollectedMethods(Map<Integer, List<CollectorInfo>> collectedMethods) {
		// Stores only the first collector of each line, since the test path of
		// methods called within a loop will be obtained at once
		for (List<CollectorInfo> collector : collectedMethods.values()) {
			collectors.add(collector.get(0));
		}
	}

	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	private void initializeMethodsCalledByTestedInvokedExporter() {
		if (isDevelopment()) {
			invokedMethodsExporter = new MethodsCalledByTestedInvokedExporter(
					"MethodsCalledByTestedMethod", 
					"examples\\results",
					false
			);
		}
		else {
			invokedMethodsExporter = new MethodsCalledByTestedInvokedExporter(
					"MethodsCalledByTestedMethod", 
					"results",
					false
			);
		}
	}
	
	protected List<CollectorInfo> getCollectors() {
		return collectors;
	}
}
