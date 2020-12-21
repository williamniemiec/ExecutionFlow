package executionFlow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import executionFlow.info.InvokedContainer;
import executionFlow.io.manager.ProcessingManager;

/**
 * For each collected constructor, obtain the following information:
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
public class ConstructorExecutionFlow extends ExecutionFlow {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private List<InvokedContainer> collectors;	
	
	
	//-------------------------------------------------------------------------
	//		Constructors
	//-------------------------------------------------------------------------
	/**
	 * Computes test path for collected constructors. Using this constructor,
	 * methods called by tested constructor will be exported to a CSV file.
	 * 
	 * @param		constructorCollector Collected constructors from 
	 * {@link executionFlow.runtime.ConstructorCollector}
	 */
	public ConstructorExecutionFlow(ProcessingManager processingManager, 
			Collection<InvokedContainer> constructorCollector) {
		super(processingManager);
		
		this.collectors = new ArrayList<>(constructorCollector);
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------	
	@Override
	protected boolean isConstructor() {
		return true;
	}
	
	@Override
	protected List<InvokedContainer> getCollectors() {
		return collectors;
	}
}
