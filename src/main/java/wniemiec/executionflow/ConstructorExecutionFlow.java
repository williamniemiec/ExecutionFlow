package wniemiec.executionflow;

import java.util.Collection;
import java.util.HashSet;

import wniemiec.executionflow.invoked.InvokedContainer;
import wniemiec.executionflow.runtime.hook.ConstructorHook;

/**
 * For each collected constructor, obtain the following information:
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
public class ConstructorExecutionFlow extends ExecutionFlow {
	
	//-------------------------------------------------------------------------
	//		Constructors
	//-------------------------------------------------------------------------
	/**
	 * Computes test path for collected constructors. Using this constructor,
	 * methods called by tested constructor will be exported to a CSV file.
	 * 
	 * @param		constructorCollector Collected constructors from 
	 * {@link ConstructorHook.runtime.ConstructorCollector}
	 */
	public ConstructorExecutionFlow(Collection<InvokedContainer> constructorCollector) {
		super(new HashSet<>(constructorCollector));
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------	
	@Override
	protected boolean isConstructor() {
		return true;
	}
}
