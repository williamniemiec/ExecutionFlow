package executionFlow.constructorExecutionFlow;

import java.util.Collection;

import executionFlow.ConstructorExecutionFlow;
import executionFlow.ExecutionFlow;
import executionFlow.ExecutionFlowTest;
import executionFlow.info.InvokedContainer;
import executionFlow.io.manager.InvokedManager;

public abstract class ConstructorExecutionFlowTest extends ExecutionFlowTest {
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	protected boolean isConstructor() {
		return true;
	}
	
	@Override
	protected ExecutionFlow getExecutionFlow(InvokedManager processingManager, 
											 Collection<InvokedContainer> constructorCollector) {
		return new ConstructorExecutionFlow(processingManager, constructorCollector);
	}
}
