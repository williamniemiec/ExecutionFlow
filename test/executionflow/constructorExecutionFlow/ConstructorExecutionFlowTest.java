package executionflow.constructorExecutionFlow;

import java.util.Collection;

import executionflow.ConstructorExecutionFlow;
import executionflow.ExecutionFlow;
import executionflow.ExecutionFlowTest;
import executionflow.info.InvokedContainer;
import executionflow.io.manager.InvokedManager;

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
