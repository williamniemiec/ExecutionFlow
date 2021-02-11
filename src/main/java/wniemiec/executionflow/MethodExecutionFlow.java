package wniemiec.executionflow;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import wniemiec.executionflow.info.InvokedContainer;
import wniemiec.executionflow.io.manager.InvokedManager;

/**
 * For each collected method, obtain the following information:
 * <ul>
 * 	<li>Test path</li>
 * 	<li>Methods called by this method</li>
 * 	<li>Test methods that call this method</li>
 * </ul>
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		7.0.0
 * @since		2.0.0
 */
public class MethodExecutionFlow extends ExecutionFlow {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private Set<InvokedContainer> collectors;
	

	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	public MethodExecutionFlow(InvokedManager processingManager, 
							   Map<Integer, List<InvokedContainer>> collectedMethods) {
		super(processingManager);
		
		this.collectors = new HashSet<>();
		
		storeCollectedMethods(collectedMethods);
	}
	
	public MethodExecutionFlow(InvokedManager processingManager, 
							   Collection<InvokedContainer> collectedMethods) {
		super(processingManager);
		
		this.collectors = new HashSet<>(collectedMethods);
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
	protected Set<InvokedContainer> getCollectors() {
		return collectors;
	}
}
