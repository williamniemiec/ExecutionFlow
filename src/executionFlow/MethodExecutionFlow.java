package executionFlow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import executionFlow.info.InvokedContainer;
import executionFlow.io.manager.InvokedManager;

/**
 * For each collected method, obtain the following information:
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
	private List<InvokedContainer> collectors;
	

	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	public MethodExecutionFlow(InvokedManager processingManager, 
			Map<Integer, List<InvokedContainer>> collectedMethods) {
		super(processingManager);
		
		this.collectors = new ArrayList<>();
		
		storeCollectedMethods(collectedMethods);
	}


	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	private void storeCollectedMethods(Map<Integer, List<InvokedContainer>> collectedMethods) {
		// Stores only the first collector of each line, since the test path of
		// methods called within a loop will be obtained at once
		for (List<InvokedContainer> collector : collectedMethods.values()) {
			collectors.add(collector.get(0));
		}
	}
	
	@Override
	protected List<InvokedContainer> getCollectors() {
		return collectors;
	}
}
