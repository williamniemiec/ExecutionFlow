package executionFlow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import executionFlow.exporter.signature.MethodsCalledByTestedInvokedExporter;
import executionFlow.info.CollectorInfo;
import executionFlow.io.ProcessingManager;


/**
 * Generates data for collected constructors. Among these data:
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
	private List<CollectorInfo> collectors;	
	
	
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
			Collection<CollectorInfo> constructorCollector) {
		super(processingManager);
		this.collectors = new ArrayList<>(constructorCollector);
		
		computedTestPaths = new HashMap<>();
		
		setMethodsCalledByTestedInvokedExporter();
	}


	private void setMethodsCalledByTestedInvokedExporter() {
		if (isDevelopment()) {
			invokedMethodsExporter = new MethodsCalledByTestedInvokedExporter(
					"MethodsCalledByTestedConstructor", "examples\\results"
			);
		}
		else {
			invokedMethodsExporter = new MethodsCalledByTestedInvokedExporter(
					"MethodsCalledByTestedConstructor", "results"
			);
		}
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	protected boolean isConstructor() {
		return true;
	}
	
	protected List<CollectorInfo> getCollectors() {
		return collectors;
	}
}
