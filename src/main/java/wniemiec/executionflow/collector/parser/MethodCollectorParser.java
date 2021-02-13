package wniemiec.executionflow.collector.parser;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import wniemiec.executionflow.invoked.InvokedContainer;

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
public class MethodCollectorParser extends CollectorParser {
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	public MethodCollectorParser(Map<Integer, List<InvokedContainer>> collectedMethods) {
		super(storeCollectedMethods(collectedMethods));
		storeCollectedMethods(collectedMethods);
	}
	
	public MethodCollectorParser(Collection<InvokedContainer> collectedMethods) {
		super(new HashSet<>(collectedMethods));
	}


	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	private static Set<InvokedContainer> storeCollectedMethods(Map<Integer, List<InvokedContainer>> collectedMethods) {
		// Stores only the first collector of each line, since the test path of
		// methods called within a loop will be obtained at once
		Set<InvokedContainer> collectors = new HashSet<>();
		for (List<InvokedContainer> collector : collectedMethods.values()) {
			collectors.add(collector.get(0));
		}
		
		return collectors;
	}
	
//	@Override
//	protected Set<InvokedContainer> getCollectors() {
//		return collectors;
//	}
}
